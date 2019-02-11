package ir.appsoar.teknesian.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ir.appsoar.teknesian.R;

/**
 * Created by LapTop on 23/08/2017.
 */

public class FactorViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rel1;
    public RelativeLayout rel2;
    public RelativeLayout rel3;
    public TextView itemname;
    public TextView tedad;
    public TextView cost;

    public FactorViewHolder(View itemView) {
        super(itemView);
        itemname = itemView.findViewById(R.id.radif1factor);
        tedad = itemView.findViewById(R.id.radif2factor);
        cost = itemView.findViewById(R.id.radif3factor);
        rel1 = itemView.findViewById(R.id.factorrel1);
        rel2 = itemView.findViewById(R.id.factorrel2);
        rel3 = itemView.findViewById(R.id.factorrel3);
    }


}