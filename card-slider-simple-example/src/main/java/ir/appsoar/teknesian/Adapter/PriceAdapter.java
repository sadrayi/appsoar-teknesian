package ir.appsoar.teknesian.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ir.appsoar.teknesian.R;

import java.util.ArrayList;
import java.util.List;

import ir.appsoar.teknesian.Dialoge.InfoDialogue;
import ir.appsoar.teknesian.Models.PriceModel;

class PriceViewHolder extends RecyclerView.ViewHolder {

    public RelativeLayout rel1;
    public TextView itemname;

    public PriceViewHolder(View itemView) {
        super(itemView);
        itemname = itemView.findViewById(R.id.radif1factor);
        rel1 = itemView.findViewById(R.id.factorrel1);
    }


}

class PriceAdapter extends RecyclerView.Adapter<PriceViewHolder> {

    private List<PriceModel> item;
    private Context context;
    private View antiprogmain;
    private View mProgressView;

    public PriceAdapter(List<PriceModel> list, Context context) {
        this.item = list;
        this.context = context;
        for (int i = 0; i < item.size(); i++)
        {
            ArrayList<Boolean> itemChecked = new ArrayList<>();
            itemChecked.add(i,false);
        }
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pricerecyclerrow, null);
        PriceViewHolder recyclerViewHolder = new PriceViewHolder(layoutView);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PriceViewHolder holder, final int position) {
        holder.itemname.setText(item.get(position).getSherkatname());
        holder.itemname.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                if(event.getRawX() <= holder.itemname.getTotalPaddingLeft()) {
                    // your action for drawable click event
                    InfoDialogue cdd=new InfoDialogue((Activity) context, item.get(position).getSherkatname(),item.get(position).getSherkataddress(),item.get(position).getSherkatphone());
                    cdd.show();                        return true;
                }
            }
            return true;
        });
        if((position % 2) == 0) {
            holder.rel1.setBackground(null);
        }

    }
    private RadioButton lastCheckedRB = null;

    @Override
    public int getItemCount() {
        return item.size();
    }

}