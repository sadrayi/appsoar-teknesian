package ir.appsoar.teknesian.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import ir.appsoar.teknesian.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import ir.appsoar.teknesian.Adapter.AdapterSafety;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Models.ItemSafety;
import ir.appsoar.teknesian.json.JsonUtils;
import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SafetyActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private String Keyword;
    private RecyclerView recyclerView;
    private List<ItemSafety> arrayItemGallery;
    private AdapterSafety AdapterGalleryRecent;
    private ArrayList<Long> array_cat_id;
    private Long[] str_cat_id;
    private ArrayList<String> array_cid;
    private ArrayList<String> array_image;
    private ArrayList<String> array_name;
    private ArrayList<String> array_desc;
    private String[] str_cid;
    private String[] str_image;
    private String[] str_name;
    private String[] str_desc;
    int textLength = 0;
    private String MenuAPI;
    private EditText edtKeyword;
    private static String title;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);
        Intent iGet = getIntent();
        title = iGet.getStringExtra("type");
        String title1 = iGet.getStringExtra("type1");
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title1);
        }
        edtKeyword = findViewById(R.id.edtKeyword);
        ImageButton btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(arg0 -> {

            try {
                Keyword = URLEncoder.encode(edtKeyword.getText().toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {

                e.printStackTrace();
            }
            MenuAPI = Config.ADMIN_PANEL_URL + "getmatlablist?type="+title+"&keyword=" + Keyword;
            clearData();
            new MyTask().execute(MenuAPI);
        });
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.requestFocus();
        progressBar = findViewById(R.id.progressBar);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(dpToPx()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        arrayItemGallery = new ArrayList<>();
        array_cid = new ArrayList<>();
        array_cat_id = new ArrayList<>();
        array_name = new ArrayList<>();
        array_image = new ArrayList<>();
        array_desc = new ArrayList<>();
//        array_date = new ArrayList<String>();

        str_cid = new String[array_cid.size()];
        str_cat_id = new Long[array_cat_id.size()];
        str_name = new String[array_name.size()];
        str_image = new String[array_image.size()];
        str_desc = new String[array_desc.size()];
//        str_desc = new String[array_date.size()];

        if (JsonUtils.isNetworkAvailable(SafetyActivity.this)) {
            new MyTask().execute(Config.ADMIN_PANEL_URL + "getmatlablist?type="+title);
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
        }


    }

    private void clearData() {
        int size = this.arrayItemGallery.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.arrayItemGallery.remove(0);
            }

            AdapterGalleryRecent.notifyItemRangeRemoved(0, size);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressBar.setVisibility(View.GONE);

            if (null == result || result.length() == 0) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray("list");
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);

                        ItemSafety objItem = new ItemSafety();
                        if(objJson.has("title"))
                            objItem.setSTitle(objJson.getString("title"));
                        else
                            objItem.setSTitle("بدون عنوان");
                        if(objJson.has("_id"))
                            objItem.setSId(objJson.getLong("_id"));
                        else
                            objItem.setSId(Long.parseLong("0"));

                        if(objJson.has("created"))
                            objItem.setCreateddate(objJson.getString("created"));
                        else
                            objItem.setCreateddate("بدون تاریخ");
                        if(objJson.has("detail"))
                            objItem.setSContent(objJson.getString("detail"));
                        else
                            objItem.setSContent("بدون توضیح");
                        if(objJson.has("kind"))
                            objItem.setType(objJson.getString("kind"));
                        else
                            objItem.setType("نامشخص");



                        arrayItemGallery.add(objItem);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int j = 0; j < arrayItemGallery.size(); j++) {

                    ItemSafety itemGallery = arrayItemGallery.get(j);

                    array_cat_id.add(itemGallery.getSId());
                    str_cat_id = array_cat_id.toArray(str_cat_id);

                    array_cid.add(String.valueOf(itemGallery.getSId()));
                    str_cid = array_cid.toArray(str_cid);
                    str_image = array_image.toArray(str_image);

                    array_name.add(String.valueOf(itemGallery.getSTitle()));
                    str_name = array_name.toArray(str_name);

                    array_desc.add(String.valueOf(itemGallery.getSContent()));
                    str_desc = array_desc.toArray(str_desc);
//
//                    array_date.add(String.valueOf(ItemGallery.getNewsDate()));
//                    str_desc = array_date.toArray(str_desc);

                }

                setAdapterToRecyclerView();
            }

        }
    }

    private void setAdapterToRecyclerView() {
        AdapterGalleryRecent = new AdapterSafety(SafetyActivity.this, arrayItemGallery);
        recyclerView.setAdapter(AdapterGalleryRecent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

//        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
//
//        final MenuItem searchMenuItem = menu.findItem(R.id.search);
//        searchView.setQueryHint(getResources().getString(R.string.search_query_text));
//
//        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
//
//                    @Override
//                    public void onFocusChange(View v, boolean hasFocus) {
//                        if (!hasFocus) {
//                            searchMenuItem.collapseActionView();
//                            searchView.setQuery("", false);
//                        }
//                    }
//                });
//
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextChange(String newText) {
//
//                textLength = newText.length();
//                arrayItemGallery.clear();
//
//                for (int i = 0; i < str_name.length; i++) {
//                    if (textLength <= str_name[i].length()) {
//                        if (str_name[i].toLowerCase().contains(newText.toLowerCase())) {
//
//                            ItemGallery objItem = new ItemGallery();
//
//                            objItem.setCatId(str_cat_id[i]);
//                            objItem.setCId(str_cid[i]);
//                            objItem.setNewsDate(str_desc[i]);
//                            objItem.setNewsDescription(str_desc[i]);
//                            objItem.setNewsHeading(str_name[i]);
//                            objItem.setNewsImage(str_image[i]);
//                            arrayItemGallery.add(objItem);
//                        }
//                    }
//                }
//
//                setAdapterToRecyclerView();
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                return true;
//            }
//        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
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
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics()));
    }

}
