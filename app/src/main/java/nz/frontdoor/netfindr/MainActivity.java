package nz.frontdoor.netfindr;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;

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

        wifiServiceIntent = new Intent(this, WifiService.class);
        wifiServiceIntent.setData(Uri.parse("START"));

        /*Button hack = (Button) findViewById(R.id.hack);
        hack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startService(wifiServiceIntent);
            }
        });*/

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


        Log.d("db", "id:" + passwd.getId() + ":" + passwd.getPhrase());
        for (Network conn : db.getSuccessfulNetworks()) {
            Log.d("db", "wifi:" + conn.getWifiName());
            Log.d("db", "loc:" + conn.getLongitude() + ":" + conn.getLatitude());
            Log.d("db", "phrase:" + conn.getPassword(db).getPhrase());
        }

        for (Network conn : db.getUnsuccessfulNetworks()) {
            Log.d("db", "unsuccessful wifi:" + conn.getWifiName());
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
