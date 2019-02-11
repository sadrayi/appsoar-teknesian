package ir.appsoar.teknesian.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import ir.appsoar.teknesian.Adapter.AdapterMenu;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Models.ItemMenu;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ActivityForushgahList extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private RecyclerView recyclerView;
    private ProgressBar prgLoading;
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private EditText edtKeyword;
    private TextView txtAlert;
    private AdapterMenu adapterMenu;
    private String MenuAPI;
    private int IOConnect = 0;
    private String Category_ID;
    private String Keyword;
    private List<ItemMenu> arrayItemMenu;
    private static ArrayList<Long> Menu_ID = new ArrayList<>();
    private static ArrayList<String> Menu_name = new ArrayList<>();
    private static ArrayList<String> Menu_price = new ArrayList<>();
    private static ArrayList<String> Menu_image = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        if (Config.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);

        prgLoading = findViewById(R.id.prgLoading);
        recyclerView = findViewById(R.id.recycler_view);
        edtKeyword = findViewById(R.id.edtKeyword);
        ImageButton btnSearch = findViewById(R.id.btnSearch);
        txtAlert = findViewById(R.id.txtAlert);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(dpToPx()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        MenuAPI = Config.ADMIN_PANEL_URL + "getkalabycat?catid=";

        Intent iGet = getIntent();
        String category_name = iGet.getStringExtra(getString(R.string.catname));
        Category_ID = iGet.getStringExtra(getString(R.string.catid));
        MenuAPI += Category_ID;

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(category_name);
        }


        new getDataTask().execute();


        btnSearch.setOnClickListener(arg0 -> {

            try {
                Keyword = URLEncoder.encode(edtKeyword.getText().toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }
            MenuAPI = Config.ADMIN_PANEL_URL + "getkalabycat?catid="+Category_ID+"&keyword=" + Keyword;
            IOConnect = 0;
            clearData();
            new getDataTask().execute();
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(() -> {
                    Intent intent = new Intent(ActivityForushgahList.this, ActivityForushgahDetail.class);
                    intent.putExtra("menu_id", arrayItemMenu.get(position).getMenuId());
                    intent.putExtra("menu_name", arrayItemMenu.get(position).getMenuName());
                    startActivity(intent);
                }, 400);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            IOConnect = 0;
            clearData();
            new getDataTask().execute();
        }, 3000));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_list, menu);

        return true;
    }
    private void initrecycler()
    {
        runOnUiThread(() -> {
            if (arrayItemMenu.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                adapterMenu = new AdapterMenu(ActivityForushgahList.this, arrayItemMenu);
                recyclerView.setAdapter(adapterMenu);
            } else {
                txtAlert.setVisibility(View.VISIBLE);
            }


        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch (item.getItemId()) {
            case R.id.cart:
                Intent i = new Intent(ActivityForushgahList.this, ActivityCart.class);
                startActivity(i);
                return true;

            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void clearData() {
        Menu_ID.clear();
        Menu_name.clear();
        Menu_price.clear();
        Menu_image.clear();
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


        }
    }

    private void parseJSONData() {

        clearData();

        try {
            
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet(MenuAPI);
            HttpResponse response = client.execute(request);
            InputStream atomInputStream = response.getEntity().getContent();

            BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));
            arrayItemMenu=new ArrayList<>();
            String line;
            StringBuilder str = new StringBuilder();
            while ((line = in.readLine()) != null) {
                str.append(line);
            }

            JSONObject json = new JSONObject(str.toString());
            JSONArray data = json.getJSONArray("list");

            for (int i = 0; i < data.length(); i++) {
                JSONObject menu = data.getJSONObject(i);
                ItemMenu item= new ItemMenu();
                item.setMenuId(Long.parseLong(menu.getString("_id")));
                if(menu.has("productname"))
                    item.setMenuName(menu.getString("productname"));
                else
                    item.setMenuName("بدون نام");
                if(menu.has("productcost"))
                    item.setMenuPrice(menu.getString("productcost"));
                else
                    item.setMenuPrice("0");
                if(menu.has("product_pic"))
                    item.setMenuImage(menu.getString("product_pic"));
                else
                    item.setMenuImage(" ");
                arrayItemMenu.add(item);
            }
            initrecycler();


            Log.e("recycler","recived");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        recyclerView.setAdapter(null);
        super.onDestroy();
    }
    
    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        GridSpacingItemDecoration(int spacing) {
            this.spanCount = 2;
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
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, r.getDisplayMetrics()));
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {

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

    interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

}
