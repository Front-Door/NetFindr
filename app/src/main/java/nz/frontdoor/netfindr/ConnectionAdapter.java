package nz.frontdoor.netfindr;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import nz.frontdoor.netfindr.services.Database;
import nz.frontdoor.netfindr.services.Network;

/**
 * Created by Jack on 30/04/2016.
 */
public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.ViewHolder> {
    Database db;
    SimpleDateFormat SD = new SimpleDateFormat("KK:mm a dd/MM/yy ");
    private List<Network> datain;

    public ConnectionAdapter(Database db){
        this.db = db;
        this.datain = this.db.getSuccessfulNetworks();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflated = LayoutInflater.from(parent.getContext());



        return new ViewHolder(inflated.inflate(R.layout.connection_item,parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Network C = datain.get(position);

        holder.data0.setText(SD.format(C.getTimestamp()));
        holder.data1.setText(String.valueOf(C.getWifiName()));
        holder.data2.setText(String.valueOf(C.getSecurityType()));
        holder.data3.setText(String.valueOf(C.getPassword(db).getPhrase()));
        //holder.data3.setText(String.valueOf(C.getLatitude())+"N "+String.valueOf(C.getLongitude())+"E");
    }

    @Override
    public int getItemCount() {
        return datain.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView data0,data1,data2,data3,data4;

        public ViewHolder(View itemView) {
            super(itemView);
            data0 = (TextView) itemView.findViewById(R.id.time_data0);
            data1 = (TextView) itemView.findViewById(R.id.time_data1);
            data2 = (TextView) itemView.findViewById(R.id.time_data2);
            data3 = (TextView) itemView.findViewById(R.id.time_data3);
        }
    }
}
