package nz.frontdoor.netfindr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import nz.frontdoor.netfindr.services.Database;
import nz.frontdoor.netfindr.services.Network;

public class SingleConnectionActivity extends AppCompatActivity {

    SimpleDateFormat SD = new SimpleDateFormat("KK:mm a dd/MM/yy ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Database db = new Database(getApplicationContext());
        int id = getIntent().getIntExtra("id", -1);
        if(id == -1){
            Toast.makeText(getBaseContext(), "No Connection Information Found", Toast.LENGTH_LONG). show();
        }
        else {
            setContentView(R.layout.activity_single_connection);
            Network n = db.getNetworkdById(id);

            TextView password = (TextView) findViewById(R.id.password_single_data);
            password.setText(n.getPassword(db).getPhrase());

            TextView ssid = (TextView) findViewById(R.id.ssid_data);
            ssid.setText("SSID: " + n.getWifiName());

            TextView timestamp = (TextView) findViewById(R.id.time_data);
            DateFormat format = SimpleDateFormat.getDateInstance();
            timestamp.setText("Time Stamp: " + format.format(n.getTimestamp()));
        }
    }
}
