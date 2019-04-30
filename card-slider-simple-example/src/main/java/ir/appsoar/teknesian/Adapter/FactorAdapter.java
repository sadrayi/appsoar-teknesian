package ir.appsoar.teknesian.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import ir.appsoar.teknesian.R;

import java.util.ArrayList;
import java.util.List;

import ir.appsoar.teknesian.Models.FactorDetailModel;

import static ir.appsoar.teknesian.Helper.MoneyFormater.Mooneyformatter;

/**
 * Created by LapTop on 23/08/2017.
 */

public class FactorAdapter extends RecyclerView.Adapter<FactorViewHolder> {

    private List<FactorDetailModel> item;
    private View antiprogmain;
    private View mProgressView;

    public FactorAdapter(List<FactorDetailModel> list, Context context) {
        this.item = list;
        Context context1 = context;
        for (int i = 0; i < item.size(); i++)
        {
            ArrayList<Boolean> itemChecked = new ArrayList<>();
            itemChecked.add(i,false);
        }
    }
    public static SweetAlertDialog pDialog;

    @NonNull
    @Override
    public FactorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.factorrecyclerrow, null);

        return new FactorViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull FactorViewHolder holder, final int position) {
        holder.itemname.setText(item.get(position).getFactorItemsName());
        holder.tedad.setText(item.get(position).getFactorItemsCount());
        holder.cost.setText(Mooneyformatter(item.get(position).getFactorItemsCost()));
        if((position % 2) == 0) {
            holder.rel1.setBackground(null);
            holder.rel2.setBackground(null);
            holder.rel3.setBackground(null);
        }

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

}