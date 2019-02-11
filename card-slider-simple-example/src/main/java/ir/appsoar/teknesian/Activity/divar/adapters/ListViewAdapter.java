package ir.appsoar.teknesian.Activity.divar.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;

import ir.appsoar.teknesian.Activity.ImageViewer;
import ir.appsoar.teknesian.R;

import java.util.ArrayList;

import ir.appsoar.teknesian.Activity.divar.DivarActivity;
import ir.appsoar.teknesian.Activity.divar.Models.ListViewModel;
import ir.appsoar.teknesian.Helper.Config;

import static ir.appsoar.teknesian.Activity.divar.DivarActivity.detailsList;


public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ViewHolder> {

    private int lastPosition = -1;
    private ArrayList<ListViewModel> arrayList;
    private DivarActivity _ctx;
    public ListViewAdapter(DivarActivity ctx, ArrayList<ListViewModel> arrayList) {
        this.arrayList = arrayList;
        _ctx=ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.divar_listitem, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        DivarActivity.selected_agahi =position;
        holder.ripple.setOnClickListener(view -> _ctx.show_divar_detail(position));
        holder.ostan.setText(arrayList.get(position).getOstan());
        holder.price.setText((arrayList.get(position).getPrice()));
        holder.title.setText(arrayList.get(position).getTitle());
        setAnimation(holder.itemView, position);
        Glide.with(_ctx).load(Config.Pic_Url+"divar_pic/"+arrayList.get(position).getImage()).into(holder.imageView);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(_ctx,ImageViewer.class);
                intent.putExtra("url",Config.Pic_Url+"divar_pic/"+arrayList.get(position).getImage());
                _ctx.startActivity(intent);
            }
        });
    }
   /* @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String query = charSequence.toString();

               ArrayList<ListViewModel> filtered = new ArrayList<>();

                if (query.isEmpty()) {
                    filtered = arrayList;
                } else {
                    for (ListViewModel row : arrayList) {
                        if (row.getDetail().toLowerCase().contains(query.toLowerCase())||row.getTitle().toLowerCase().contains(query.toLowerCase())||row.getPrice().toLowerCase().contains(query.toLowerCase())||row.getCity().toLowerCase().contains(query.toLowerCase())||row.getOstan().toLowerCase().contains(query.toLowerCase())) {
                            filtered.add(row);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filtered.size();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                itemsFiltered = (ArrayList<ListViewModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }*/
    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        MaterialRippleLayout ripple;
        TextView ostan,title,price;
        ImageView imageView;
        ViewHolder(View view) {
            super(view);
            ripple=view.findViewById(R.id.ripple);
            ostan=view.findViewById(R.id.ostanlist);
            title=view.findViewById(R.id.titlelist);
            price=view.findViewById(R.id.pricelist);
            imageView=view.findViewById(R.id.imagelist);

        }
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(_ctx.getBaseContext(), R.anim.item_animation_fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}


