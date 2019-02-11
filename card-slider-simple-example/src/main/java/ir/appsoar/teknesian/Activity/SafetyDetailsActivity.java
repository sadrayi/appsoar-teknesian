package ir.appsoar.teknesian.Activity;

import android.annotation.SuppressLint;
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
import android.net.Uri;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
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

import cn.jzvd.JZDataSource;
import cn.jzvd.Jzvd;
import ir.appsoar.teknesian.Helper.Config;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SafetyDetailsActivity extends AppCompatActivity {


    private ImageView imageView;
    private TextView txtName;
    private CoordinatorLayout coordinatorLayout;
    private ProgressBar progressBar;
    private TextView txtAlert;
    private FloatingActionButton pdf;
    private WebView txtDescription;
    private String Menu_image;
    private String Menu_name;
    private String Menu_description;
    private Jzvd videoplayer;
    private String pdfurl;
    private String MenuDetailAPI;
    private int IOConnect = 0;
    final Context context = this;
    private CollapsingToolbarLayout collapsingToolbarLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_details);

        if (Config.ENABLE_RTL_MODE) {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }

        imageView = findViewById(R.id.imgPreview);
        txtName = findViewById(R.id.txtName);
        txtDescription = findViewById(R.id.txtDescription);
        coordinatorLayout = findViewById(R.id.main_content);

        progressBar = findViewById(R.id.prgLoading);
        txtAlert = findViewById(R.id.txtAlert);


        Intent iGet = getIntent();
        long menu_ID = iGet.getLongExtra("menu_id", 0);
        Menu_name = iGet.getStringExtra("menu_name");

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        videoplayer = findViewById(R.id.video);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");
        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

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
        pdf = findViewById(R.id.floatingActionButton);

        MenuDetailAPI = Config.ADMIN_PANEL_URL + "getmatlabdetail?menu_id=" + menu_ID;

        new SafetyDetailsActivity.getDataTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;

            case R.id.cart:
                startActivity(new Intent(getApplicationContext(), ActivityCart.class));
                return true;

            case R.id.checkout:
                startActivity(new Intent(getApplicationContext(), ActivityCart.class));
                return true;

            case R.id.save:
                (new SafetyDetailsActivity.SaveTask(SafetyDetailsActivity.this)).execute(Config.Pic_Url + "product_pic/" + Menu_image);
                return true;

            case R.id.share:
                String formattedString = android.text.Html.fromHtml(Menu_description).toString();
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, Menu_name + "\n" + formattedString + "\n" + getResources().getString(R.string.share_content) + "https://play.google.com/store/apps/details?id=" + getPackageName());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);

            default:
                return super.onOptionsItemSelected(item);
        }

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

        @SuppressLint({"RestrictedApi", "CheckResult", "SetJavaScriptEnabled"})
        @Override
        protected void onPostExecute(Void result) {

            if (Config.ENABLE_RTL_MODE) {

                progressBar.setVisibility(View.GONE);
                if ((Menu_name != null) && IOConnect == 0) {
                    coordinatorLayout.setVisibility(View.VISIBLE);
                    String[] ext = Menu_image.split("\\.");
                    if (ext[ext.length - 1].equals("avi") || ext[ext.length - 1].equals("mp4")) {
                        imageView.setVisibility(View.GONE);
                        videoplayer.setVisibility(View.VISIBLE);
                        JZDataSource jzDataSource = new JZDataSource(Config.Pic_Url + "matlab_pic/" + Menu_image);
                        videoplayer.setUp(jzDataSource,
                                Jzvd.SCREEN_WINDOW_NORMAL);
                    } else {
                        Intent imageview=new Intent(SafetyDetailsActivity.this,ImageViewer.class);
                        imageview.putExtra("url",Config.Pic_Url + "matlab_pic/" + Menu_image);
                        imageView.setOnClickListener(view -> startActivity(imageview));
                        RequestOptions requestOptions = new RequestOptions();
                        requestOptions.placeholder(R.drawable.ic_loading);
                        requestOptions.error(R.drawable.ic_loading);
                        Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions).load(Config.Pic_Url + "matlab_pic/" + Menu_image)
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
                    }
                    txtName.setText(Menu_name);


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
                    if (!pdfurl.equals("")) {
                        if (pdfurl.split("\\.")[pdfurl.split("\\.").length - 1].equals("pdf")) {
                            pdf.setVisibility(View.VISIBLE);
                            pdf.setOnClickListener(view -> {
                                String url = Config.Pic_Url + "matlab_pdf/" + pdfurl;
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                startActivity(i);
                            });
                        } else {
                            pdf.setImageDrawable(getResources().getDrawable(R.mipmap.ic_image));
                            pdf.setVisibility(View.VISIBLE);
                            pdf.setOnClickListener(view -> {/*
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse(Config.Pic_Url + "matlab_pdf/" + pdfurl), "image/*");
                                startActivity(intent);*/
                                Intent imageview=new Intent(SafetyDetailsActivity.this,ImageViewer.class);
                                imageview.putExtra("url",Config.Pic_Url + "matlab_pdf/" + pdfurl);
                                startActivity(imageview);
                            });
                        }

                    }
                    txtDescription.loadData(text, mimeType, encoding);

                } else {
                    txtAlert.setVisibility(View.VISIBLE);
                }

            } else {

                progressBar.setVisibility(View.GONE);
                if ((Menu_name != null) && IOConnect == 0) {
                    coordinatorLayout.setVisibility(View.VISIBLE);

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.placeholder(R.drawable.ic_loading);
                    requestOptions.error(R.drawable.ic_loading);
                    Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions).load(Config.Pic_Url + "matlab_pic/" + Menu_image)
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
                    imageView.setOnClickListener(view -> {
                        /*Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(Config.Pic_Url + "matlab_pic/" + Menu_image), "image/*");
                        startActivity(intent);*/

                        Intent imageview=new Intent(SafetyDetailsActivity.this,ImageViewer.class);
                        imageview.putExtra("url",Config.Pic_Url + "matlab_pdf/" + pdfurl);
                        startActivity(imageview);
                    });
                    txtName.setText(Menu_name);

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

                    String text = "<html><head>"
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

            for (int i = 0; i < data.length(); i++) {
                JSONObject menu = data.getJSONObject(i);


                Menu_image = menu.getString("matlab_pic");
                Menu_name = menu.getString("title");
                if (menu.has("pdf"))
                    pdfurl = menu.getString("pdf");
                else
                    pdfurl = "";
                Menu_description = menu.getString("detail");

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
        // TODO Auto-generated method stub
        super.onBackPressed();
        if (Jzvd.backPress()) {
            return;
        }
        finish();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
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

                MediaScannerConnection.scanFile(SafetyDetailsActivity.this, as, null, (s1, uri) -> {
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

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }
}
