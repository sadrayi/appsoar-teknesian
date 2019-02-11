package ir.appsoar.teknesian.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import ir.appsoar.teknesian.R;

import java.util.ArrayList;
import java.util.List;

import ir.appsoar.teknesian.Activity.FactorActivity;
import ir.appsoar.teknesian.Helper.GlobalVariable;
import ir.appsoar.teknesian.Models.CatnameModel;

/**
 * Created by LapTop on 23/08/2017.
 */

public class OrdersAdapter extends RecyclerView.Adapter<OrdersViewHolder> {

    private List<CatnameModel> item;
    private Context context;
    private View antiprogmain;
    private View mProgressView;
    ArrayList<Boolean> itemChecked;
    public OrdersAdapter(List<CatnameModel> list, Context context) {
        this.item = list;
        this.context = context;
        itemChecked = new ArrayList<>(item.size());
        for (int i = 0; i < item.size(); i++)
        {
            itemChecked.add(i,false);
        }
    }
    public static SweetAlertDialog pDialog;

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tarokhcherecyclerrow, null);
        // antiprogmain = (View) layoutView.findViewById(R.id.antiprogmain);
        //mProgressView = layoutView.findViewById(R.id.progressBar2);
        OrdersViewHolder recyclerViewHolder = new OrdersViewHolder(layoutView);

        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, final int position) {
        holder.title.setText(item.get(item.size()-position-1).getCatName());
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.fitCenter();
        Glide.with(context).load(R.drawable.tamin).into(holder.image);

        holder.detail.setOnClickListener(v -> {
            Intent detailintent = new Intent(context, FactorActivity.class);
            GlobalVariable.request_id=item.get(item.size()-position-1).getRequestId();
            detailintent.putExtra("requestid",item.get(item.size()-position-1).getRequestId());
            context.startActivity(detailintent);
        });

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

}