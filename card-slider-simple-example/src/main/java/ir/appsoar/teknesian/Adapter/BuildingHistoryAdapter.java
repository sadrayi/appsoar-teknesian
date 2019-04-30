package ir.appsoar.teknesian.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import java.util.ArrayList;
import java.util.List;

import ir.appsoar.teknesian.Activity.FactorActivity;
import ir.appsoar.teknesian.Helper.GlobalVariable;
import ir.appsoar.teknesian.Helper.HelperShamsi;
import ir.appsoar.teknesian.Models.BuildingHistoryModel;
import ir.appsoar.teknesian.Models.CatnameModel;
import ir.appsoar.teknesian.R;

/**
 * Created by LapTop on 23/08/2017.
 */

public class BuildingHistoryAdapter extends RecyclerView.Adapter<BuildingHistoryAdapter.BuildingHistoryViewHolder> {

    private List<BuildingHistoryModel> item;
    private Context context;
    private View antiprogmain;
    private View mProgressView;
    ArrayList<Boolean> itemChecked;
    public BuildingHistoryAdapter(List<BuildingHistoryModel> list, Context context) {
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
    public BuildingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.buildinghistoryrecyclerrow, null);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        return new BuildingHistoryViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull BuildingHistoryViewHolder holder, final int position) {
        if(position%2==0){
            holder.container.setBackground(null);
        }
        holder.date.setText((item.get(position).getDate()));
        holder.kind.setText(item.get(position).getKind());
        holder.teknesian.setText(item.get(position).getTeknesian());

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class BuildingHistoryViewHolder extends RecyclerView.ViewHolder{

        TextView date;
        TextView kind;
        TextView teknesian;
        LinearLayout container;

        public BuildingHistoryViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            container = itemView.findViewById(R.id.container);
            kind = itemView.findViewById(R.id.kind);
            teknesian = itemView.findViewById(R.id.teknesian);
        }

    }
}