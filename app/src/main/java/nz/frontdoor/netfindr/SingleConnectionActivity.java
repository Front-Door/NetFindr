package nz.frontdoor.netfindr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
            Network n = db.getNetworkById(id);

            TextView password = (TextView) findViewById(R.id.password_single_data);
            password.setText(n.getPassword(db).getPhrase());

            TextView ssid = (TextView) findViewById(R.id.ssid_data);
            ssid.setText(n.getWifiName());

            TextView rank = (TextView) findViewById(R.id.rank_data);
            rank.setText(""+n.getPassword(db).getRank());

            TextView encrypt = (TextView) findViewById(R.id.encryption_data);
            encrypt.setText(n.getSecurityType());

            TextView loc = (TextView) findViewById(R.id.lat_long_data);
            loc.setText("" + n.getLatitude() + "°S, " + n.getLongitude() + "°E");

            TextView timestamp = (TextView) findViewById(R.id.time_data);
            DateFormat format = SimpleDateFormat.getDateInstance();
            timestamp.setText(""+format.format(n.getTimestamp()));
        }
    }
}
