package ir.appsoar.teknesian.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ir.appsoar.teknesian.R;

import ir.appsoar.teknesian.Activity.ActivityCart;
import ir.appsoar.teknesian.Helper.Config;

import static ir.appsoar.teknesian.Helper.MoneyFormater.Mooneyformatter;

public class AdapterCart extends RecyclerView.Adapter<AdapterCart.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtMenuName;
        TextView txtQuantity;
        TextView txtPrice;

        ViewHolder(View view) {
            super(view);

            txtMenuName = view.findViewById(R.id.txtMenuName);
            txtQuantity = view.findViewById(R.id.txtQuantity);
            txtPrice = view.findViewById(R.id.txtPrice);

        }

    }

    public AdapterCart() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (Config.ENABLE_RTL_MODE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_order_list_rtl, parent, false);
            return new ViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_order_list, parent, false);
            return new ViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.txtMenuName.setText(ActivityCart.product_name.get(position));
        holder.txtQuantity.setText(String.valueOf(ActivityCart.Quantity.get(position)));
        holder.txtPrice.setText(Mooneyformatter(ActivityCart.Sub_total_price.get(position).toString()));

    }

    @Override
    public int getItemCount() {
        return ActivityCart.productid.size();
    }

}
