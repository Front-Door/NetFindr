package nz.frontdoor.netfindr;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 30/04/2016.
 */
public class ConnectionAdapter extends RecyclerView.Adapter<ConnectionAdapter.ViewHolder> {

    private List<String> datain;

    public ConnectionAdapter(List<String> datain){

        this.datain = datain;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflated = LayoutInflater.from(parent.getContext());



        return new ViewHolder(inflated.inflate(R.layout.connection_item,parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String s = datain.get(position);
        holder.data0.setText(s);
        holder.data1.setText(s);
        holder.data2.setText(s);
        holder.data3.setText(s);
        holder.data4.setText(s);
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
            data4 = (TextView) itemView.findViewById(R.id.time_data4);
        }
    }
}
