package ir.appsoar.teknesian.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ir.appsoar.teknesian.R;


/**
 * Created by LapTop on 23/08/2017.
 */

public class OrdersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public Button detail;
    public ImageView image;
    public TextView title;

    public OrdersViewHolder(View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);
        detail = itemView.findViewById(R.id.cost1);
        image = itemView.findViewById(R.id.cotsttitle1);
        title = itemView.findViewById(R.id.cost2);
    }

    @Override
    public void onClick(View view) {
        //Toast.makeText(, "", Toast.LENGTH_SHORT).show();
    }
}