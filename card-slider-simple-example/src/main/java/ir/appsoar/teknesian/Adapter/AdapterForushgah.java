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

import ir.appsoar.teknesian.Activity.divar.fragments.ForushgahFragment;
import ir.appsoar.teknesian.Helper.Config;

public class AdapterForushgah extends RecyclerView.Adapter<AdapterForushgah.ViewHolder> {

    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtText;
        ImageView imgThumb;

        ViewHolder(View view) {
            super(view);

            txtText = view.findViewById(R.id.Stitle);
            imgThumb = view.findViewById(R.id.imageforushgah);

        }

    }

    public AdapterForushgah(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_forushgah, parent, false);

        return new ViewHolder(itemView);

    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.txtText.setText(ForushgahFragment.category_name.get(position));

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_loading);
        Glide.with(context).setDefaultRequestOptions(requestOptions).load(Config.Pic_Url+"category_pic/"  + ForushgahFragment.category_image.get(position)).into(holder.imgThumb);

    }

    @Override
    public int getItemCount() {
        return ForushgahFragment.category_id.size();
    }

}
