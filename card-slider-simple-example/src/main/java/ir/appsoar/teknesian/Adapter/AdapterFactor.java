package ir.appsoar.teknesian.Adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ir.appsoar.teknesian.R;

import ir.appsoar.teknesian.Activity.OrderManageActivity;
import ir.appsoar.teknesian.Dialoge.DeleteFactorItemDialog;
import ir.appsoar.teknesian.Fragment.AddFactorFrag;
import ir.appsoar.teknesian.Fragment.FactorFrag;

import static ir.appsoar.teknesian.Helper.MoneyFormater.Mooneyformatter;


public class AdapterFactor extends RecyclerView.Adapter<AdapterFactor.ViewHolder> {

    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemname;
        TextView itemcount;
        TextView itemcost;
        ImageView edit;
        ImageView delete;

        ViewHolder(View view) {
            super(view);

            itemcount = view.findViewById(R.id.textView60);
            itemname = view.findViewById(R.id.textView64);
            itemcost = view.findViewById(R.id.textView69);
            edit = view.findViewById(R.id.editaddress);
            delete= view.findViewById(R.id.deleteaddress);

        }

    }

    public AdapterFactor(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_factor, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.itemcost.setText(Mooneyformatter(FactorFrag.factoritems.get(position).get("percost").toString()));
        holder.itemname.setText(FactorFrag.factoritems.get(position).get("title").toString());
        holder.itemcount.setText(FactorFrag.factoritems.get(position).get("quantity").toString());
        if(OrderManageActivity.reqstatus.equals("2"))
        holder.delete.setOnClickListener(view -> {
            DeleteFactorItemDialog cdd=new DeleteFactorItemDialog((Activity) context,FactorFrag.factoritems.get(position).get("id").toString(),FactorFrag.id,FactorFrag.token);
            cdd.show();
        });
        else
            holder.delete.setVisibility(View.INVISIBLE);
        if(OrderManageActivity.reqstatus.equals("2"))
        holder.edit.setOnClickListener(view -> {
            OrderManageActivity myActivity=(OrderManageActivity)context;
            FragmentTransaction transaction = myActivity.getSupportFragmentManager().beginTransaction();
            AddFactorFrag map = new AddFactorFrag();
            final Bundle bundle = new Bundle();
            bundle.putString("type","edit");
            bundle.putString("cost",FactorFrag.factoritems.get(position).get("percost").toString());
            bundle.putString("count",FactorFrag.factoritems.get(position).get("quantity").toString());
            bundle.putString("name",FactorFrag.factoritems.get(position).get("title").toString());
            bundle.putString("comment",FactorFrag.factoritems.get(position).get("comment").toString());
            bundle.putString("itemid",FactorFrag.factoritems.get(position).get("id").toString());
            map.setArguments(bundle);
            transaction.replace(R.id.main_frame,map);
            transaction.addToBackStack(null);
            transaction.commit();

        });
        else
            holder.edit.setVisibility(View.INVISIBLE);

    }

    @Override
    public int getItemCount() {
        return FactorFrag.factoritems.size();
    }

}
