package nz.frontdoor.netfindr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ConnectionListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectionlist);
        RecyclerView mainList = (RecyclerView) findViewById(R.id.main_list);
        ArrayList<String> names = new ArrayList<>();
        names.add("TWO O'CLOCK");
        names.add("ONE O'CLOCK");
        names.add("NINE O'CLOCK");
        names.add("FIVE O'CLOCK");

        mainList.setLayoutManager(new LinearLayoutManager(this));
        mainList.setAdapter(new ConnectionAdapter(names));
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
