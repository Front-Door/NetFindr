package nz.frontdoor.netfindr;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import nz.frontdoor.netfindr.services.Database;
import nz.frontdoor.netfindr.services.Password;
import nz.frontdoor.netfindr.services.Network;
import nz.frontdoor.netfindr.services.SingleConnectionInfo;

import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Intent wifiServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);

        WifiService.context = getApplicationContext();

        //MainActivity.this.startService(wifiServiceIntent);

        BroadcastReceiver wifiServiceReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO
                Log.v("GAIDFHWAODHJW", "ALIVE");
            }
        };
        getBaseContext().registerReceiver(wifiServiceReciver, new IntentFilter(WifiService.BROADCAST_ACTION));
        Database db = new Database(getApplicationContext());
        // db.clearNetworks();

        Password passwd = null;
        for (Password pass : db.getPasswords()) {
            if (pass == null) {
                Log.e("db", "pass null");
            }
            Log.d("db", pass.getPhrase());
            passwd = pass;
        }

         /*
        db.addNetwork(Network.SuccessfulConnection("Elf's Super Secret Network", passwd, 0.10, 23.45, "Open", new Date()));
        db.addNetwork(Network.SuccessfulConnection("Poorly secure Wifi", passwd, 234.0, 34.034534, "WPA", new Date()));
        db.addNetwork(Network.UnsuccessfulConnection("Secure WiFi", 40.2, 150.2, "WPA", new Date()));
        */

        for (Network conn : db.getSuccessfulNetworks()) {
            Log.d("db", "wifi:" + conn.getWifiName());
            Log.d("db", "loc:" + conn.getLongitude() + ":" + conn.getLatitude());
            Log.d("db", "phrase:" + conn.getPassword(db).getPhrase());
        }

        for (Network conn : db.getUnsuccessfulNetworks()) {
            Log.d("db", "unsuccessful wifi:" + conn.getWifiName());
        }

        TextView hackCount = (TextView) findViewById(R.id.total_hacks);
        hackCount.setText("" + db.getSuccessfulNetworks().size());

        TextView netCount = (TextView) findViewById(R.id.total_networks);
        netCount.setText("" + db.getNetworkCount());



        Network recent = db.getMostRecentSuccessfulNetwork();
        if (recent != null) {
            TextView password = (TextView) findViewById(R.id.password);
            password.setText(recent.getPassword(db).getPhrase());

            TextView ssid = (TextView) findViewById(R.id.ssid);
            ssid.setText("SSID: " + recent.getWifiName());

            TextView timestamp = (TextView) findViewById(R.id.timestamp1);
            DateFormat format = SimpleDateFormat.getDateInstance();
            timestamp.setText("Time Stamp: " + format.format(recent.getTimestamp()));
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.on_off_switch);
        item.setActionView(R.layout.switch_view);
        SwitchCompat toggle_scanner = (SwitchCompat) item.getActionView().findViewById(R.id.switchForActionBar);
        toggle_scanner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //TODO start hacking
                    Toast.makeText(getBaseContext(), "Start Scanning", Toast.LENGTH_SHORT).show();

                    wifiServiceIntent = new Intent(MainActivity.this, WifiService.class);
                    wifiServiceIntent.setData(Uri.parse("START"));
                    MainActivity.this.startService(wifiServiceIntent);
                }
                else {
                    // TODO stop hacking
                    Toast.makeText(getBaseContext(), "Stop Scanning", Toast.LENGTH_SHORT).show();

                    wifiServiceIntent = new Intent(MainActivity.this, WifiService.class);
                    wifiServiceIntent.setData(Uri.parse("START"));
                    MainActivity.this.stopService(wifiServiceIntent);
                }
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getTitle().equals("Settings")) {

        }
        return true;
    }

    public void startConnectionList(View v) {
        Intent newList = new Intent(this, ConnectionListActivity.class);
        startActivity(newList);
    }

    public void viewMostRecent(View v) {
        Database db = new Database(getApplicationContext());
        int id = -1;
        if(db.getMostRecentSuccessfulNetwork() != null){
            id = db.getMostRecentSuccessfulNetwork().getId();
        }
        Intent mostRecent = new Intent(this, SingleConnectionActivity.class);
        mostRecent.putExtra("id", id);
        startActivity(mostRecent);
    }
}
