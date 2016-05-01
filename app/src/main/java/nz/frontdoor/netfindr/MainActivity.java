package nz.frontdoor.netfindr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;

import nz.frontdoor.netfindr.services.Database;
import nz.frontdoor.netfindr.services.Password;
import nz.frontdoor.netfindr.services.Network;
import android.view.Menu;
import android.view.View;

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
        MainActivity.this.startService(wifiServiceIntent);

        BroadcastReceiver wifiServiceReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO
                Log.v("GAIDFHWAODHJW", "ALIVE");
            }
        };
        getBaseContext().registerReceiver(wifiServiceReciver, new IntentFilter(WifiService.BROADCAST_ACTION));
        Database db = new Database(getApplicationContext());
        db.clearNetworks();

        Password passwd = null;
        for (Password pass : db.getPasswords()) {
            if (pass == null) {
                Log.e("db", "pass null");
            }
            Log.d("db", pass.getPhrase());
            passwd = pass;
        }

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
        return super.onCreateOptionsMenu(menu);
    }

    public void startConnectionList(View v) {
        Intent newList = new Intent(this, ConnectionListActivity.class);
        startActivity(newList);
    }
}
