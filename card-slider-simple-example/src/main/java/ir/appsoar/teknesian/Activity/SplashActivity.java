package ir.appsoar.teknesian.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import ir.appsoar.teknesian.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import co.ronash.pushe.Pushe;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Helper.Permissons;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends AppCompatActivity {
    private static SharedPreferences prefs;
    private String regkind = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Pushe.initialize(this, true);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        Glide.with(this).load(R.drawable.logomain).into((ImageView) findViewById(R.id.imageView6));
            if (prefs.getString(getString(R.string.token), null) != null) {
                new sendDatastart().execute();
            } else {

                new Handler().postDelayed(() -> {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }, 2000);
            }


    }


    private String Resultstart;

    @SuppressLint("StaticFieldLeak")
    class sendDatastart extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            String phone = prefs.getString(getString(R.string.phone), "");
            String token = prefs.getString(getString(R.string.token), "");
            Resultstart = getRequeststart(phone, token);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(Resultstart.equals("404"))
                showerrorconnect();
            else
                resultAlertstart(Resultstart);
        }
    }

    private String requeststart(HttpResponse response) {
        String result = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            StringBuilder str = new StringBuilder();
            while ((line = in.readLine()) != null) {
                str.append(line);
            }
            JSONObject json = new JSONObject(str.toString());
            result = json.getString("message");
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                if (json.getInt("ver") != pInfo.versionCode) {
                    runOnUiThread(() -> {
                        pDialog1 = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE);
                        pDialog1.setContentText("ورژن جدید در دسترس می باشد..");
                        pDialog1.setTitleText("بروزرسانی");
                        pDialog1.setConfirmText("دریافت").setCancelText("خروج").setCancelClickListener(sweetAlertDialog -> SplashActivity.this.finish());
                        pDialog1.setConfirmClickListener(sweetAlertDialog -> {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://snapplift.ir/help.html"));
                            startActivity(browserIntent);
                        });
                        if (! isFinishing()) {

                            pDialog1.show();

                        }
                    });
                }
            } catch (Exception ignored) {

            }
            if (result.equals("success")) {
                result = "200";
                if (json.has("_id"))
                    prefs.edit().putString(getString(R.string.id), json.getString("_id")).apply();
                if (json.has("token"))
                    prefs.edit().putString(getString(R.string.token), json.getString("token")).apply();
                if (json.has("sherkatno"))
                    prefs.edit().putString(getString(R.string.sherkatno), json.getString("sherkatno")).apply();
                if (json.has("lastname"))
                    prefs.edit().putString(getString(R.string.username), json.getString("lastname")).apply();
                if (json.has("onlinestatus"))
                    prefs.edit().putString(getString(R.string.onlinestatus), json.getString("onlinestatus")).apply();
                if (json.has("profile_pic"))
                    prefs.edit().putString(getString(R.string.profile_pic), json.getString("profile_pic")).apply();
                regkind = json.getString("regkind");

                if (json.has("status"))
                    if (json.getString("status").equals("inactive")) {
                        result = "420";
                    }
            } else if (result.equals("wrongtoken"))
                result = "400";
        } catch (Exception ex) {
            if (result == null)
                result = "Error";
        }
        return result;
    }

    private String getRequeststart(
            String phone,
            String token
    ) {
        String resultstart;

        HttpClient client = new DefaultHttpClient();
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "teknesiantokenchek");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("phone", phone));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("deviceid", Pushe.getPusheId(SplashActivity.this)));
            requeststart.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse responsestart = client.execute(requeststart);
            resultstart = requeststart(responsestart);
        } catch (Exception ex) {
            resultstart = "404";
        }
        return resultstart;
    }

    private void resultAlertstart(String HasilProses) {
        if (HasilProses.trim().equalsIgnoreCase("200")) {
            if (regkind.equals("new")) {
                Intent i = new Intent(SplashActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            } else if (regkind.equals("old")) {
                Intent i = new Intent(SplashActivity.this, FirstActivity.class);
                startActivity(i);
                finish();
            }
        } else if (HasilProses.trim().equalsIgnoreCase("400")) {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        } else if (HasilProses.trim().equalsIgnoreCase("420")) {
            Toast.makeText(SplashActivity.this, "حساب کاربری شما غیر فعال می باشد.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();
        }
    }

    void UpdateApp() {

        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "teknesian.apk";
        destination += fileName;
        final Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
        File file = new File(destination);
        if (file.exists())
            //file.delete() - test this, I think sometimes it doesnt work
            file.delete();

        //get url of app on server
        String url = Config.Pic_Url + fileName;

        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription("Updating App");
        request.setTitle(SplashActivity.this.getString(R.string.app_name));

        //set destination
        request.setDestinationUri(uri);

        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        assert manager != null;
        final long downloadId = manager.enqueue(request);

        //set BroadcastReceiver to install app when .apk is downloaded
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.setDataAndType(uri,
                        manager.getMimeTypeForDownloadedFile(downloadId));
                startActivity(install);

                unregisterReceiver(this);
                finish();
            }
        };
        //register receiver for when .apk download is compete
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    SweetAlertDialog pDialog1;
    public void showerrorconnect(){
        runOnUiThread(() -> {
            pDialog1 = new SweetAlertDialog(SplashActivity.this, SweetAlertDialog.ERROR_TYPE);
            pDialog1.setContentText("در برقراری ارتباط با سرور مشکلی پیش آمده است لطفا اتصال اینترنت خود را چک نمایید.");
            pDialog1.setTitleText("عدم ارتباط");
            pDialog1.setConfirmText("سعی مجدد").setCancelText("خروج").setCancelClickListener(sweetAlertDialog -> SplashActivity.this.finish());
            pDialog1.setConfirmClickListener(sweetAlertDialog -> {new sendDatastart().execute();pDialog1.dismiss();});
            if (! isFinishing()) {

                pDialog1.show();

            }
        });
    }
}