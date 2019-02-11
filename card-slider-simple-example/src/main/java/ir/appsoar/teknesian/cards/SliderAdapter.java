package ir.appsoar.teknesian.cards;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import ir.appsoar.teknesian.R;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.ViewHolder> {

    private final int count;
    private  Context context;
    private final String[] content;
    private final View.OnClickListener listener;

    public SliderAdapter(Context context,String[] content, int count, View.OnClickListener listener) {
        this.content = content;
        this.count = count;
        this.context = context;
        this.listener = listener;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        ViewHolder(View view) {
            super(view);
            imageView= view.findViewById(R.id.image);
        }

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.layout_slider_card, parent, false);

        if (listener != null) {
            view.setOnClickListener(view1 -> listener.onClick(view1));
        }

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(content[position % content.length]).into(holder.imageView);
        //holder.setContent(content[position % content.length]);
    }



    @Override
    public int getItemCount() {
        return count;
    }

}
