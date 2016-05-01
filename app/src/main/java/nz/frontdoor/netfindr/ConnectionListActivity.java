package nz.frontdoor.netfindr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import nz.frontdoor.netfindr.services.Database;
import nz.frontdoor.netfindr.services.Network;

public class ConnectionListActivity extends AppCompatActivity implements ConnectionAdapter.OnNetWorkClickedListner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectionlist);
        RecyclerView mainList = (RecyclerView) findViewById(R.id.main_list);

        mainList.setLayoutManager(new LinearLayoutManager(this));
        Database db = new Database(getApplicationContext());
        mainList.setAdapter(new ConnectionAdapter(db,this));
        updateList();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void updateList(){

    }

    @Override
    public void onNetWorkClicked(Network netWork) {

    }
}
