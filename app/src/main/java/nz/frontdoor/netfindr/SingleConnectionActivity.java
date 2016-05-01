package nz.frontdoor.netfindr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.text.SimpleDateFormat;

public class SingleConnectionActivity extends AppCompatActivity {

    SimpleDateFormat SD = new SimpleDateFormat("KK:mm a dd/MM/yy ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int id = getIntent().getIntExtra("id", -1);

        if(id == -1){
            Toast.makeText(getBaseContext(), "No Connection Information Found", Toast.LENGTH_LONG). show();
        }
        else {
            setContentView(R.layout.activity_single_connection);
        }
    }
}
