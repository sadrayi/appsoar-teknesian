package ir.appsoar.teknesian.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import ir.appsoar.teknesian.Helper.Permissons;
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
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ir.appsoar.teknesian.Activity.divar.DivarActivity;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Helper.DBHelper;
import ir.appsoar.teknesian.Helper.LocationManagerService;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED;

public class FirstActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private SharedPreferences prefs;
    public static String lastlocation;
    @SuppressLint("StaticFieldLeak")
    private static DBHelper dbhelper;
    private SweetAlertDialog pDialog;
    private SweetAlertDialog activdialuge;
    private static int activestatus = 0;
    @SuppressLint("StaticFieldLeak")
    private static ToggleButton statustextviewheader;
    @SuppressLint("StaticFieldLeak")
    private static TextView mogheiattextview;
    private static Boolean isactive = false;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        isactive = true;
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        FirstActivity main = this;
        IntentFilter intentFilter = new IntentFilter(
                "locationmanager");
        Uri uri = getIntent().getData();
        String message;
        if (uri != null) {
            message = uri.getQueryParameter("message"); // x = "1.2"
            if (message.equals("success")) {
                SweetAlertDialog pDialog1 = new SweetAlertDialog(FirstActivity.this, SweetAlertDialog.WARNING_TYPE);
                pDialog1
                        .setTitleText("تایید")
                        .setContentText("پرداخت با موفقیت انجام شد.")
                        .setConfirmClickListener(Dialog::dismiss)
                        .setConfirmText("تایید").show();
            } else {
                SweetAlertDialog pDialog1 = new SweetAlertDialog(FirstActivity.this, SweetAlertDialog.WARNING_TYPE);
                pDialog1
                        .setTitleText("خطا")
                        .setContentText("امکان پرداخت آنلاین وجود ندارد!")
                        .setConfirmClickListener(Dialog::dismiss)
                        .setConfirmText("تایید").show();
            }

        }
        BroadcastReceiver mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent
                if (intent.getExtras() != null) {
                    String status = intent.getExtras().getString("status");
                    Log.i("InchooTutorial", status);
                    //log our message value
                    if (status != null) {
                        Log.i("InchooTutorial", status);
                        if (status.equals("0")) {
                            activestatus = 0;
                            statustextviewheader.setText("غیرفعال");
                            statustextviewheader.setChecked(false);
                            mogheiattextview.setText("غیرفعال");
                        } else {
                            if (activdialuge != null) {
                                prefs.edit().putString(getString(R.string.lastlatlng), status).apply();
                                activdialuge.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                activdialuge.setContentText("وضعیت شما فعال شد.");
                                activdialuge.showCancelButton(false);
                                activdialuge.setConfirmText("تایید");
                                activdialuge.setConfirmClickListener(sweetAlertDialog -> activdialuge.dismiss());
                                activestatus = 1;
                                statustextviewheader.setText("فعال");
                                statustextviewheader.setChecked(true);
                                mogheiattextview.setText("فعال");
                            }
                        }
                    }
                }
            }
        };
        //registering our receiver
        this.registerReceiver(mReceiver, intentFilter);
        Activity activity = this;
        String onlinestatus = prefs.getString(getString(R.string.onlinestatus), "0");
        String profile_pic = "teknesian_profile/" + prefs.getString(getString(R.string.profile_pic), null);
        ImageView asansor = findViewById(R.id.elevator);
        asansor.setOnClickListener(view -> {
            Log.i("*******stattusservice", String.valueOf(isMyServiceRunning(LocationManagerService.class)));

            Log.i("***********prefs", "154");
            if (activestatus == 1)
                if (isMyServiceRunning(LocationManagerService.class)) {
                    Log.i("***********prefs", "157");
                    String latlng = prefs.getString(getString(R.string.lastlatlng), null);
                    Log.i("***********prefs", String.valueOf(latlng));
                    if (latlng != null)
                        startActivity(new Intent(FirstActivity.this, MainActivity.class));
                    else {

                        pDialog = new SweetAlertDialog(FirstActivity.this, SweetAlertDialog.ERROR_TYPE);
                        pDialog.setTitleText("خطا")
                                .setConfirmText("تایید")
                                .setContentText("ارتباط شما با سرور قطع می باشد\n لطفا مجددا وضعیت را فعال نمایید.").setCancelable(false);
                        pDialog.show();
                    }
                } else {
                    pDialog = new SweetAlertDialog(FirstActivity.this, SweetAlertDialog.ERROR_TYPE);
                    pDialog.setTitleText("خطا")
                            .setConfirmText("تایید")
                            .setContentText("ارتباط شما با سرور قطع می باشد\n لطفا مجددا وضعیت را فعال نمایید.").setCancelable(false);
                    pDialog.show();
                }
            else {
                pDialog = new SweetAlertDialog(FirstActivity.this, SweetAlertDialog.ERROR_TYPE);
                pDialog.setTitleText("خطا")
                        .setConfirmText("تایید")
                        .setContentText("ابتدا وضعیت را فعال نمایید.").setCancelable(false);
                pDialog.show();
            }
        });

        ImageView takhasosi = findViewById(R.id.tasisat);
        @SuppressLint("CutPasteId") ImageView[] images = new ImageView[]{findViewById(R.id.elevator), findViewById(R.id.logo), findViewById(R.id.profile), findViewById(R.id.back), takhasosi, findViewById(R.id.door), findViewById(R.id.pelebarghi)};
        int[] drawable = {R.drawable.listwork, R.drawable.logomain, R.mipmap.profile, R.drawable.back, R.drawable.takhasosi, R.drawable.chat, R.drawable.sabad};

        takhasosi.setOnClickListener(view -> startActivity(new Intent(FirstActivity.this, HamkaranActivity.class)));
        int i = 0;
        for (ImageView img : images) {
            Glide.with(this).load(drawable[i]).into(img);
            i++;

        }
        @SuppressLint("CutPasteId") ImageView chat = findViewById(R.id.door);
        @SuppressLint("CutPasteId") ImageView pele = findViewById(R.id.pelebarghi);
        chat.setOnClickListener(view -> startActivity(new Intent(FirstActivity.this, DivarActivity.class)));
        pele.setOnClickListener(view -> startActivity(new Intent(FirstActivity.this, Requests_Frag.class)));
        dbhelper = new DBHelper(this);

        // create database
        dbhelper.createDataBase();

        dbhelper.openDataBase();

        if (dbhelper.isPreviousDataExist()) {
            showAlertDialog();
        }
        TextView name = findViewById(R.id.name);
        name.setText(prefs.getString(getString(R.string.username), ""));
        service = new Intent(FirstActivity.this, LocationManagerService.class);
        mogheiattextview = findViewById(R.id.positiontext);
        statustextviewheader = findViewById(R.id.eshterak);
        statustextviewheader.setOnClickListener(view -> {
            if (prefs.getString(getString(R.string.sherkatno), null) != null) {
                ArrayList<String> permissions = new ArrayList<>();
                if (!Permissons.Check_FINE_LOCATION(this))
                    permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);

                if (!Permissons.Check_Internet(this))
                    permissions.add(Manifest.permission.INTERNET);
                if (permissions.size() > 0) {
                    String[] namesArr = new String[permissions.size()];
                    for (int j = 0; j < permissions.size(); j++) {
                        namesArr[j] = permissions.get(j);
                    }
                    ActivityCompat.requestPermissions(this, namesArr, 22);

                } else {
                    activateState();
                }


            } else {
                final SweetAlertDialog pDialog = new SweetAlertDialog(FirstActivity.this, SweetAlertDialog.ERROR_TYPE);
                pDialog.setTitleText("خطا")
                        .setConfirmText("تایید")
                        .setConfirmClickListener(sweetAlertDialog -> pDialog.dismiss())
                        .setContentText("شما عضو شرکت معتبری نمی باشید.").setCancelable(false);
                pDialog.show();
            }
        });
        Log.d("profile", Config.Pic_Url + profile_pic);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.mipmap.profile);
        Glide.with(this).setDefaultRequestOptions(requestOptions).load(Config.Pic_Url + profile_pic).into((ImageView) findViewById(R.id.profile));
        if (onlinestatus.equals("1")) {
            activestatus = 1;
            statustextviewheader.setChecked(true);
            statustextviewheader.setText("فعال");
            mogheiattextview.setText("فعال");
        } else {
            activestatus = 0;
            statustextviewheader.setChecked(false);
            statustextviewheader.setText("غیرفعال");
            mogheiattextview.setText("غیرفعال");
            stopService(service);
        }
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        // BEGIN_INCLUDE(permission_result)
        // Received permission result for camera permission.
        Log.i("respone", "Received response for Camera permission request.");
        boolean granted = true;
        for (int grantResult : grantResults) {
            if (grantResult != 0)
                granted = false;
        }
        // Check if the only required permission has been granted
        if (granted) {
            activateState();
        } else {
            SweetAlertDialog pDialog1 = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
            pDialog1
                    .setTitleText("خطا")
                    .setContentText("دسترسی به تمام مجوز ها الزامی است.")
                    .setCancelText("خروج");
            pDialog1
                    .setCancelClickListener(sweetAlertDialog -> {
                        pDialog1.dismiss();
                        this.finish();
                    });
            pDialog1
                    .setConfirmClickListener(sweetAlertDialog -> {
                        ArrayList<String> permissions1 = new ArrayList<>();
                        if (!Permissons.Check_FINE_LOCATION(this))
                            permissions1.add(Manifest.permission.ACCESS_FINE_LOCATION);

                        if (!Permissons.Check_STORAGE(this))
                            permissions1.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

                        if (!Permissons.Check_Internet(this))
                            permissions1.add(Manifest.permission.INTERNET);
                        if (permissions1.size() > 0) {
                            String[] namesArr = new String[permissions1.size()];
                            for (int i = 0; i < permissions1.size(); i++) {
                                namesArr[i] = permissions1.get(i);
                            }
                            ActivityCompat.requestPermissions(this, namesArr, 22);

                        }
                        pDialog1.dismiss();
                    })
                    .setConfirmText("سعی مجدد").show();
        }


    }
    private Intent service;

    private void showAlertDialog() {

        dbhelper.deleteAllData();

    }

    private boolean doubleBackToExitPressedOnce = false;


    void activateState() {
        String locationProviders = Settings.Secure.getString(getContentResolver(), LOCATION_PROVIDERS_ALLOWED);
        if (locationProviders == null || locationProviders.equals("")) {
            final SweetAlertDialog pDialog = new SweetAlertDialog(FirstActivity.this, SweetAlertDialog.ERROR_TYPE);
            pDialog.setTitleText("خطا")
                    .setConfirmText("تایید")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        pDialog.dismiss();
                    })
                    .setContentText("ابتدا موقعیت یاب خود را فعال نمایید.").setCancelable(false);
            pDialog.show();
        } else {
            if (activestatus == 0) {
                activdialuge = new SweetAlertDialog(FirstActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                activdialuge
                        .setTitleText("فعالسازی")
                        .setContentText("درحال دریافت موقعیت")
                        .setCancelText("لغو")
                        .setCancelClickListener(sweetAlertDialog -> {
                            stopService(service);
                            activdialuge.dismiss();
                        })
                        .setCancelable(false);
                activdialuge.show();
                startService(service);
            } else if (activestatus == 1) {
                stopService(service);
                new sendDatastart().execute();

            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "برای خروج دوباره بازگشت را بزنید", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isactive = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isactive = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        isactive = true;
        if (prefs.getString(getString(R.string.onlinestatus), "0").equals("0")) {
            activestatus = 0;
            statustextviewheader.setChecked(false);
            statustextviewheader.setText("غیرفعال");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Log.d("navigation", "clicked");
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        int id = item.getItemId();

        if (id == R.id.requests) {
            startActivity(new Intent(FirstActivity.this, Requests_Frag.class));
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(FirstActivity.this, RegisterActivity.class));
        } else if (id == R.id.help) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://snapplift.ir/help.html"));
            startActivity(browserIntent);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    ///////////


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
            if (result.equals("success")) {
                result = "200";
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
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "deactiveteknesian");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("phone", phone));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            requeststart.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse responsestart = client.execute(requeststart);
            resultstart = requeststart(responsestart);
        } catch (Exception ex) {
            resultstart = "Unable to connect.";
        }
        return resultstart;
    }

    private void resultAlertstart(String HasilProses) {
        if (HasilProses.trim().equalsIgnoreCase("200")) {
            activestatus = 0;
            statustextviewheader.setChecked(false);
            statustextviewheader.setText("غیرفعال");
        } else if (HasilProses.trim().equalsIgnoreCase("400")) {
            startActivity(new Intent(FirstActivity.this, LoginActivity.class));
            finish();
        }
    }

/////////////////check service running status

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        int id = v.getId();
        if (id == R.id.nav_camera) {

        } else if (id == R.id.requests) {
            startActivity(new Intent(FirstActivity.this, Requests_Frag.class));
        } else if (id == R.id.editprofile) {
            startActivity(new Intent(FirstActivity.this, RegisterActivity.class));
        } else if (id == R.id.terms) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://snapplift.ir/help.html"));
            startActivity(browserIntent);
        }
        drawer.closeDrawer(GravityCompat.START);
    }

}
