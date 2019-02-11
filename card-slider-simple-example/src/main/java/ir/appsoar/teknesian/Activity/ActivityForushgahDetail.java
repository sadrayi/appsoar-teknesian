package ir.appsoar.teknesian.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import ir.appsoar.teknesian.Dialoge.InfoDialogue;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Helper.DBHelper;
import ir.appsoar.teknesian.Models.PriceModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.appsoar.teknesian.Helper.MoneyFormater.Mooneyformatter;

public class ActivityForushgahDetail extends AppCompatActivity {
    public static String sherkatid = null;
    private ImageView imageView;
    private TextView txtName;
    private TextView sherkatnametxt;
    private TextView info;
    private TextView txtAlert;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar progressBar;
    private WebView txtDescription;
    @SuppressLint("StaticFieldLeak")
    private static DBHelper dbhelper;
    private String Menu_image;
    private String Menu_name;
    private String Menu_description;
    private static String Menu_price;
    public static String sherkatname;
    private List<PriceModel> pricedata;
    private long Menu_ID;
    private String MenuDetailAPI;
    private int IOConnect = 0;
    private FloatingActionButton fab;
    private final Context context = this;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private com.github.clans.fab.FloatingActionButton fab3;
    private com.github.clans.fab.FloatingActionButton fab2;
    private com.github.clans.fab.FloatingActionButton fab4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forushgahdetail);

        if (Config.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }


        init();
        createlayout();
        new getDataTask().execute();

    }

    private void createlayout() {
        fab2.setOnClickListener(v ->
                startActivity(new Intent(this, AddressActivity.class)));
        fab.setOnClickListener(v -> inputDialog());

        fab3.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ActivityCart.class)));
        collapsingToolbarLayout.setTitle("");
        appBarLayout.setExpanded(true);
        fab4.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(Menu_description));
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "iran");
            startActivity(Intent.createChooser(shareIntent, "اشتراک گذاری با ..."));
        });
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(Menu_name);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });

        MenuDetailAPI = Config.ADMIN_PANEL_URL + "getmenudetail?menu_id=" + Menu_ID;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        imageView = findViewById(R.id.imgPreview);
        txtName = findViewById(R.id.txtName);
        sherkatnametxt = findViewById(R.id.radif1factor);
        info = findViewById(R.id.radif3factor);
        txtDescription = findViewById(R.id.txtDescription);
        coordinatorLayout = findViewById(R.id.main_content);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        pricedata = new ArrayList<>();
        appBarLayout = findViewById(R.id.appbar);

        progressBar = findViewById(R.id.prgLoading);
        txtAlert = findViewById(R.id.txtAlert);
        new LinearLayoutManager(this);

        fab = findViewById(R.id.btnAdd);
        fab3 = findViewById(R.id.checkout);
        dbhelper = new DBHelper(this);
        fab2 = findViewById(R.id.cart);
        fab4 = findViewById(R.id.save);
        Intent iGet = getIntent();
        Menu_ID = iGet.getLongExtra("menu_id", 0);
        Menu_name = iGet.getStringExtra("menu_name");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;

            case R.id.buy:
                inputDialog();
                return true;

            case R.id.cart:
                startActivity(new Intent(getApplicationContext(), ActivityCart.class));
                return true;

            case R.id.checkout:
                startActivity(new Intent(getApplicationContext(), ActivityCart.class));
                return true;

            case R.id.save:
                (new SaveTask(ActivityForushgahDetail.this)).execute(Config.Pic_Url + "product_pic/" + Menu_image);
                return true;

            case R.id.share:
                String formattedString = Html.fromHtml(Menu_description).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, Menu_name + "\n" + formattedString + "\n" + getResources().getString(R.string.share_content) + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void inputDialog() {

        dbhelper.openDataBase();


        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(context);

        @SuppressLint("InflateParams") View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog_rtl, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setView(mView);

        final EditText edtQuantity = mView.findViewById(R.id.userInputDialog);
        alert.setCancelable(false);
        int maxLength = 3;
        edtQuantity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        edtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);

        alert.setPositiveButton("افزودن", (dialog, whichButton) -> {
            String temp = edtQuantity.getText().toString();
            int quantity;

            if (!temp.equalsIgnoreCase("")) {
                quantity = Integer.parseInt(temp);
                Toast.makeText(getApplicationContext(), "با موفقیت به سبد خرید افزوده شد", Toast.LENGTH_SHORT).show();

                dbhelper.deleteAllData();
                dbhelper.addData(Menu_ID, Menu_name, quantity, Integer.parseInt(Menu_price), pricedata.get(0).getSherkatid(), pricedata.get(0).getSherkatname());
                startActivity(new Intent(ActivityForushgahDetail.this, ActivityCart.class));
            } else {
                dialog.cancel();
            }
        });

        alert.setNegativeButton("لغو",
                (dialog, id) -> dialog.cancel());

        AlertDialog alertDialog = alert.create();
        alertDialog.show();


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @SuppressLint("StaticFieldLeak")
    class getDataTask extends AsyncTask<Void, Void, Void> {

        getDataTask() {
            if (!progressBar.isShown()) {
                progressBar.setVisibility(View.VISIBLE);
                txtAlert.setVisibility(View.GONE);
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            parseJSONData();
            return null;
        }

        @SuppressLint({"CheckResult", "SetTextI18n", "SetJavaScriptEnabled"})
        @Override
        protected void onPostExecute(Void result) {
            progressBar.setVisibility(View.GONE);
            if ((Menu_name != null) && IOConnect == 0) {
                coordinatorLayout.setVisibility(View.VISIBLE);
                RequestOptions requestOptions = new RequestOptions();
                requestOptions.placeholder(R.drawable.ic_loading);
                requestOptions.error(R.drawable.ic_loading);
                Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions).load(Config.Pic_Url + "product_pic/" + Menu_image)
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
                txtName.setText(Menu_name + "  " + Mooneyformatter(Menu_price ));
                info.setOnClickListener(view -> {
                    InfoDialogue cdd = new InfoDialogue((Activity) context, pricedata.get(0).getSherkatname(), pricedata.get(0).getSherkataddress(), pricedata.get(0).getSherkatphone());
                    cdd.show();
                });
                sherkatnametxt.setText("شرکت:" + pricedata.get(0).getSherkatname());
                txtDescription.setBackgroundColor(Color.parseColor("#ffffff"));
                txtDescription.setFocusableInTouchMode(false);
                txtDescription.setFocusable(false);
                txtDescription.getSettings().setDefaultTextEncodingName("UTF-8");

                WebSettings webSettings = txtDescription.getSettings();
                Resources res = getResources();
                int fontSize = res.getInteger(R.integer.font_size);
                webSettings.setDefaultFontSize(fontSize);
                webSettings.setJavaScriptEnabled(true);

                String mimeType = "text/html; charset=UTF-8";
                String encoding = "utf-8";
                String htmlText = Menu_description;

                String text = "<html dir='rtl'><head>"
                        + "<style type=\"text/css\">body{color: #525252;}"
                        + "</style></head>"
                        + "<body>"
                        + htmlText
                        + "</body></html>";

                txtDescription.loadData(text, mimeType, encoding);

            } else {
                txtAlert.setVisibility(View.VISIBLE);
            }


        }
    }


    // method to parse json data from server
    private void parseJSONData() {

        try {
            // request data from menu detail API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet(MenuDetailAPI);
            HttpResponse response = client.execute(request);
            InputStream atomInputStream = response.getEntity().getContent();


            BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

            String line;
            StringBuilder str = new StringBuilder();
            while ((line = in.readLine()) != null) {
                str.append(line);
            }

            // parse json data and store into tax and currency variables
            JSONObject json = new JSONObject(str.toString());
            JSONArray data = json.getJSONArray("list"); // this is the "items: [ ] part
            JSONObject sherkatobj = json.getJSONObject("sherkat"); // this is the "items: [ ] part
            PriceModel newpricemolel = new PriceModel(sherkatobj.getString("sherkatid"), sherkatobj.getString("sherkatname"), sherkatobj.getString("sherkataddress"), sherkatobj.getString("sherkatphone"));
            pricedata.add(newpricemolel);
            for (int i = 0; i < data.length(); i++) {
                JSONObject menu = data.getJSONObject(i);
                if (menu.has("product_pic"))
                    Menu_image = menu.getString("product_pic");
                if (menu.has("productname"))
                    Menu_name = menu.getString("productname");
                else
                    Menu_name = "بدون عنوان";
                if (menu.has("productcost"))
                    Menu_price = menu.getString("productcost");
                else
                    Menu_price = "0";
                String menu_status;
                if (menu.has("status"))
                    menu_status = menu.getString("status");
                else
                    menu_status = "inactive";
                if (menu.has("productdetail"))
                    Menu_description = menu.getString("productdetail");
                else
                    Menu_description = " ";
                String serve_for;
                if (menu.has("productcount"))
                    serve_for = menu.getString("productcount");
                else
                    serve_for = "0";

            }


        } catch (MalformedURLException | JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            IOConnect = 1;
            e.printStackTrace();
        }
    }


    // close database before back to previous page
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dbhelper.deleteAllData();
        finish();
    }


    @Override
    protected void onDestroy() {
        //imageLoader.clearCache();
        super.onDestroy();
    }


    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        // Ignore orientation change to keep activity from restarting
        super.onConfigurationChanged(newConfig);
    }

    @SuppressLint("StaticFieldLeak")
    class SaveTask extends AsyncTask<String, String, String> {

        private Context context;
        private ProgressDialog pDialog;
        URL myFileUrl;
        Bitmap bmImg = null;
        File file;

        SaveTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(context);
            pDialog.setMessage("دریافت تصویر ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... args) {
            String as[];
            try {
                myFileUrl = new URL(args[0]);
                HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                bmImg = BitmapFactory.decodeStream(is);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                String path = myFileUrl.getPath();
                String idStr = path.substring(path.lastIndexOf('/') + 1);
                File filepath = Environment.getExternalStorageDirectory();
                File dir = new File(filepath.getAbsolutePath() + "/" + getResources().getString(R.string.app_name) + "/");
                dir.mkdirs();
                String fileName = "Image_" + "_" + idStr;
                file = new File(dir, fileName);
                FileOutputStream fos = new FileOutputStream(file);
                bmImg.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();
                as = new String[1];
                as[0] = file.toString();

                MediaScannerConnection.scanFile(ActivityForushgahDetail.this, as, null, (s1, uri) -> {
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String args) {
            Toast.makeText(getApplicationContext(), "تصویر با موفقیت ذخیره شد.", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }
    }

}
