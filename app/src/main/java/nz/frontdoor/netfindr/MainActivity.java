package nz.frontdoor.netfindr;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.TextView;

import java.util.Date;

import nz.frontdoor.netfindr.services.Database;
import nz.frontdoor.netfindr.services.Password;
import nz.frontdoor.netfindr.services.SuccessfulConnection;
import android.view.Menu;

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
        int id = 0;
        for (Password pass : db.getPasswords()) {
            if (pass == null) {
                Log.e("db", "pass null");
            }
            Log.d("db", pass.getPhrase());
            id = pass.getId();
        }

        db.addSuccessConnection(new SuccessfulConnection("Wifi", id, 0, 0, "WPA", new Date()));

        Log.d("db", "id:" + id);
        for (SuccessfulConnection conn : db.getSuccessfulConnections()) {
            if (conn == null) {
                Log.e("db", "pass null");
            }
            Log.d("db", "wifi:" + conn.getWifiName());

            Password pass = conn.getPassword(db);
            if (pass != null) {
                Log.d("db", "phrase:" + pass.getPhrase());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
