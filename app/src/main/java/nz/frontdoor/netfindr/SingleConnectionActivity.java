package nz.frontdoor.netfindr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.SimpleDateFormat;

public class SingleConnectionActivity extends AppCompatActivity {

    SimpleDateFormat SD = new SimpleDateFormat("KK:mm a dd/MM/yy ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_connection);
    }
}
