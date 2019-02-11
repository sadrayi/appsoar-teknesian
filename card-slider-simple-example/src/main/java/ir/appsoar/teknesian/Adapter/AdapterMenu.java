package ir.appsoar.teknesian.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import ir.appsoar.teknesian.R;

import java.util.List;

import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Models.ItemMenu;

import static ir.appsoar.teknesian.Helper.MoneyFormater.Mooneyformatter;

public class AdapterMenu extends RecyclerView.Adapter<AdapterMenu.ViewHolder> {

    private Context context;
    private List<ItemMenu> arrayItemMenu;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtText, txtSubText;
        ImageView imgThumb;

        ViewHolder(View view) {
            super(view);

            txtText = view.findViewById(R.id.txtName);
            txtSubText = view.findViewById(R.id.txtPrice);
            imgThumb = view.findViewById(R.id.imgThumb);

        }

    }

    public AdapterMenu(Context context, List<ItemMenu> arrayItemMenu) {
        this.context = context;
        this.arrayItemMenu = arrayItemMenu;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_menu, parent, false);

        return new ViewHolder(itemView);

    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.txtText.setText(arrayItemMenu.get(position).getMenuName());
        holder.txtSubText.setText(Mooneyformatter(arrayItemMenu.get(position).getMenuPrice()));
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_loading);
        requestOptions.error(R.drawable.ic_loading);
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(Config.Pic_Url + "product_pic/" + arrayItemMenu.get(position).getMenuImage()).into(holder.imgThumb);

    }

    @Override
    public int getItemCount() {
        return arrayItemMenu.size();
    }

}
