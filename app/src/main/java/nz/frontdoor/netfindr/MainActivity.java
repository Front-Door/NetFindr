package nz.frontdoor.netfindr;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.Button;
import android.widget.LinearLayout;
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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Intent wifiServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);

        WifiService.context = getApplicationContext();
        wifiServiceIntent = new Intent(this, WifiService.class);
        wifiServiceIntent.setData(Uri.parse("START"));

        wifiServiceIntent = new Intent(this, WifiService.class);
        wifiServiceIntent.setData(Uri.parse("START"));

//        Button hack = (Button) findViewById(R.id.hack);
//        hack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MainActivity.this.startService(wifiServiceIntent);
//            }
//        });

        Database db = new Database(getApplicationContext());
        // Insert dummy data
        db.addPassword(new Password("password", 1));
        // Log the passwords and success full connections
        Password passwd = null;
        for (Password pass : db.getPasswords()) {
            if (pass == null) {
                Log.e("db", "pass null");
            }
            Log.d("db", pass.getPhrase());
            passwd = pass;
        }


        Log.d("db", "know wifi? " + db.isKnownNetwork("Poorly secure Wifi"));
        db.addNetwork(Network.SuccessfulConnection("Poorly secure Wifi", passwd, 234.0, 34.034534, "WPA", new Date()));
        db.addNetwork(Network.UnsuccessfulConnection("Secure WiFi", 40.2, 150.2, "WPA", new Date()));
        Log.d("db", "know wifi? " + db.isKnownNetwork("Poorly secure Wifi"));


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

            TextView timestamp = (TextView) findViewById(R.id.timestamp);
            DateFormat format = SimpleDateFormat.getDateInstance();
            timestamp.setText("Time Stamp: " + format.format(recent.getTimestamp()));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
        Intent mostRecent = new Intent(this, SingleConnectionInfo.class);
        mostRecent.putExtra("id", id);
        startActivity(mostRecent);
    }

    public void onOffDialog(View v){

    }
}
