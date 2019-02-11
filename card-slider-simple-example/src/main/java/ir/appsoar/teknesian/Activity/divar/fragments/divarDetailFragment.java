package ir.appsoar.teknesian.Activity.divar.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import ir.appsoar.teknesian.Activity.ImageViewer;
import ir.appsoar.teknesian.R;

import java.util.Objects;

import ir.appsoar.teknesian.Dialoge.DivarContactDialogue;
import ir.appsoar.teknesian.Helper.Config;

import static ir.appsoar.teknesian.Activity.divar.DivarActivity.detailsList;
import static ir.appsoar.teknesian.Helper.MoneyFormater.Mooneyformatter;


public class divarDetailFragment extends Fragment {
    private ImageView imageView;
    private TextView place;
    private TextView title;
    private TextView price;
    private TextView detail;
    private CoordinatorLayout coordinatorLayout;
    private FloatingActionButton fab;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_divardetail, container, false);
        init();
        createlayout();
        return view;
    }

    @SuppressLint({"SetTextI18n", "CheckResult"})
    private void createlayout() {

        Bundle bundle = this.getArguments();
        int productid=0;
        if (bundle != null) {
           productid=bundle.getInt("type");
        }
            DivarContactDialogue cc = new DivarContactDialogue(getActivity(), detailsList.get(productid).getPhone());
        Objects.requireNonNull(cc.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        fab.setOnClickListener(v -> cc.show());
        collapsingToolbarLayout.setTitle("");
        appBarLayout.setExpanded(true);


        int finalProductid = productid;
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(detailsList.get(finalProductid).getTitle());
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
        coordinatorLayout.setVisibility(View.VISIBLE);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_loading);
        requestOptions.error(R.drawable.ic_loading);
        Glide.with(Objects.requireNonNull(getContext())).setDefaultRequestOptions(requestOptions).load(Config.Pic_Url + "divar_pic/" + detailsList.get(productid).getImage())
                .listener(new RequestListener<Drawable>() {

                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        Palette.from(bitmap).generate(palette -> {
                        });
                        return false;
                    }
                })
                .into(imageView);
        int finalProductid1 = productid;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),ImageViewer.class);
                intent.putExtra("url",Config.Pic_Url + "divar_pic/" + detailsList.get(finalProductid1).getImage());
                startActivity(intent);
            }
        });
        price.setText(Mooneyformatter(detailsList.get(productid).getPrice()));
        detail.setText(detailsList.get(productid).getDetail());
        place.setText(detailsList.get(productid).getOstan() + "-" + detailsList.get(productid).getCity());
        title.setText(detailsList.get(productid).getTitle());
    }

    private void init() {
        imageView = view.findViewById(R.id.imgPreview);
        coordinatorLayout = view.findViewById(R.id.main_content);
        collapsingToolbarLayout = view.findViewById(R.id.collapsing_toolbar);
        appBarLayout = view.findViewById(R.id.appbar);
        place = view.findViewById(R.id.place);
        price = view.findViewById(R.id.price);
        title = view.findViewById(R.id.title);
        detail = view.findViewById(R.id.detail);
        fab = view.findViewById(R.id.btnAdd);
    }


}
