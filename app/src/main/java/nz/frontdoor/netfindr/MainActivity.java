package nz.frontdoor.netfindr;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Intent wifiServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiServiceIntent = new Intent(this, WifiService.class);
        wifiServiceIntent.setData(Uri.parse("START"));

        Button hack = (Button) findViewById(R.id.hack);
        hack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startService(wifiServiceIntent);
            }
        });
    }
}
