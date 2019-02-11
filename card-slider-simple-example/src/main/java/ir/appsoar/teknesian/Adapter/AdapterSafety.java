package ir.appsoar.teknesian.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ir.appsoar.teknesian.R;

import java.util.List;

import ir.appsoar.teknesian.Activity.SafetyDetailsActivity;
import ir.appsoar.teknesian.Models.ItemSafety;

public class AdapterSafety extends RecyclerView.Adapter<AdapterSafety.ViewHolder> {

    private Context context;
    private List<ItemSafety> arrayItemGalleryList;
    String[] str_gallery, str_cid, str_cat_id, str_image, str_name, str_desc;

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        WebView content;
        LinearLayout clicker;

        ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.Stitle);
            content = view.findViewById(R.id.SContent);
            clicker = view.findViewById(R.id.clicker);


        }

    }

    public AdapterSafety(Context context, List<ItemSafety> arrayItemGalleryList) {
        this.context = context;
        this.arrayItemGalleryList = arrayItemGalleryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_safety, parent, false);

        return new ViewHolder(itemView);

    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.title.setText(arrayItemGalleryList.get(position).getSTitle());
        holder.content.setBackgroundColor(Color.parseColor("#ffffff"));
        holder.content.setFocusableInTouchMode(false);
        holder.content.setFocusable(false);
        holder.content.getSettings().setDefaultTextEncodingName("UTF-8");
        holder.clicker.setOnClickListener(view -> {
            Log.e("clicked", arrayItemGalleryList.get(position).getSId().toString());
            Intent intent = new Intent(context, SafetyDetailsActivity.class);
            intent.putExtra("menu_id", arrayItemGalleryList.get(position).getSId());
            intent.putExtra("menu_name", arrayItemGalleryList.get(position).getSTitle());
            context.startActivity(intent);
        });
        WebSettings webSettings = holder.content.getSettings();
        Resources res = context.getResources();
        int fontSize = res.getInteger(R.integer.font_size);
        webSettings.setDefaultFontSize(fontSize);
        webSettings.setJavaScriptEnabled(true);

        String mimeType = "text/html; charset=UTF-8";
        String encoding = "utf-8";
        String htmlText = arrayItemGalleryList.get(position).getSContent();

        String text = "<html dir='rtl'><head>"
                + "<style type=\"text/css\">body{color: #525252;}"
                + "</style></head>"
                + "<body>"
                + htmlText
                + "</body></html>";

        holder.content.loadData(text, mimeType, encoding);


    }

    @Override
    public int getItemCount() {
        return arrayItemGalleryList.size();
    }

}

