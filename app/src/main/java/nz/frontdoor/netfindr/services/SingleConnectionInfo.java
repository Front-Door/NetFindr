package nz.frontdoor.netfindr.services;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import nz.frontdoor.netfindr.ConnectionAdapter;
import nz.frontdoor.netfindr.R;

/**
 * Created by Divya on 1/05/2016.
 */
public class SingleConnectionInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_connection);
        if(savedInstanceState.get("id") != -1){

        }
        else {

        }

    }
}
