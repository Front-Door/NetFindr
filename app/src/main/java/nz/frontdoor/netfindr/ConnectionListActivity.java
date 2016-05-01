package nz.frontdoor.netfindr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import nz.frontdoor.netfindr.services.Database;

public class ConnectionListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectionlist);
        RecyclerView mainList = (RecyclerView) findViewById(R.id.main_list);

        mainList.setLayoutManager(new LinearLayoutManager(this));
        Database db = new Database(getApplicationContext());
        mainList.setAdapter(new ConnectionAdapter(db));
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
}
