package ir.appsoar.teknesian.Activity.divar.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import ir.appsoar.teknesian.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import ir.appsoar.teknesian.Activity.ActivityForushgahList;
import ir.appsoar.teknesian.Activity.ForushgahActivity;
import ir.appsoar.teknesian.Adapter.AdapterForushgah;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Models.ItemForushgah;


public class ForushgahFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar prgLoading;
    private List<ItemForushgah> arrayItemForushgah;
    private TextView txtAlert;
    private SwipeRefreshLayout swipeRefreshLayout = null;
    public static ArrayList<Long> category_id = new ArrayList<>();
    public static ArrayList<String> category_name = new ArrayList<>();
    public static ArrayList<String> category_image = new ArrayList<>();
    private String CategoryAPI;
    private int IOConnect = 0;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_forushgah, container, false);
        init();
        creatlayout();
        recyclerlistner();
        swipelistner();
        new getDataTask().execute();
        return view;
    }
    private void init() {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);

        prgLoading = view.findViewById(R.id.prgLoading);
        recyclerView = view.findViewById(R.id.recycler_view);
        txtAlert = view.findViewById(R.id.txtAlert);
    }

    private void creatlayout() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(dpToPx()));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());


        CategoryAPI = Config.ADMIN_PANEL_URL + "getcategory";

    }

    private void recyclerlistner() {
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ForushgahActivity.ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(getContext(), ActivityForushgahList.class);
                    intent.putExtra(getString(R.string.catid), category_id.get(position).toString());
                    intent.putExtra(getString(R.string.catname), category_name.get(position));
                    startActivity(intent);
                }, 400);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void swipelistner() {
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            IOConnect = 0;
            clearData();
            new getDataTask().execute();
        }, 3000));

    }




    private void clearData() {
        category_id.clear();
        category_name.clear();
        category_image.clear();
    }

    @SuppressLint("StaticFieldLeak")
    class getDataTask extends AsyncTask<Void, Void, Void> {

        getDataTask() {
            if (!prgLoading.isShown()) {
                prgLoading.setVisibility(View.VISIBLE);
                txtAlert.setVisibility(View.GONE);
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            parseJSONData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            prgLoading.setVisibility(View.GONE);

            if ((category_id.size() > 0) && (IOConnect == 0)) {
                AdapterForushgah adapterForushgah = new AdapterForushgah(getContext());
                adapterForushgah.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(adapterForushgah);
            } else {
                txtAlert.setVisibility(View.VISIBLE);
            }
        }
    }

    private void parseJSONData() {

        clearData();

        try {

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet(CategoryAPI);
            HttpResponse response = client.execute(request);
            InputStream atomInputStream = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

            String line;
            StringBuilder str = new StringBuilder();
            while ((line = in.readLine()) != null) {
                str.append(line);
            }
            arrayItemForushgah=new ArrayList<>();
            JSONArray data = new JSONArray(str.toString());
            ItemForushgah itemForushgah;
            for (int i = 0; i < data.length(); i++) {
                JSONObject category = data.getJSONObject(i);
                itemForushgah=new ItemForushgah();
                category_id.add(Long.parseLong(category.getString("_id")));
                itemForushgah.setFId(Long.parseLong(category.getString("_id")));
                if (category.has("catname")){
                    category_name.add(category.getString("catname"));
                    itemForushgah.setFTitle(category.getString("catname"));

                }

                else{
                    category_name.add(" ");
                    itemForushgah.setFTitle("");

                }
                if (category.has("category_pic")){
                    category_image.add(category.getString("category_pic"));
                    itemForushgah.setFimage(category.getString("category_pic"));
                }
                else{
                    category_image.add(" ");
                    itemForushgah.setFimage(" ");
                }
                Log.d("Category name", category_name.get(i));
                arrayItemForushgah.add(itemForushgah);
            }

        } catch (MalformedURLException | JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            IOConnect = 1;
            e.printStackTrace();
        }
    }

    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        GridSpacingItemDecoration(int spacing) {
            this.spanCount = 1;
            this.spacing = spacing;
            this.includeEdge = true;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }

    private int dpToPx() {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics()));
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ForushgahActivity.ClickListener clickListener;

        RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ForushgahActivity.ClickListener clickListener) {

            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }




}
