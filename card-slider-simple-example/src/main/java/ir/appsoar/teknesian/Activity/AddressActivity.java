package ir.appsoar.teknesian.Activity;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.github.nitrico.mapviewpager.MapViewPager;
import ir.appsoar.teknesian.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import ir.appsoar.teknesian.Adapter.Sample2Adapter;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Helper.Utils;
import ir.map.sdk_common.MaptexLatLng;
import ir.map.sdk_map.wrapper.MaptexCameraPosition;
import ir.map.sdk_map.wrapper.MaptexMap;
import ir.map.sdk_map.wrapper.OnMaptexReadyCallback;
import ir.map.sdk_map.wrapper.SupportMaptexFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddressActivity extends AppCompatActivity implements OnMaptexReadyCallback {
    public static String[] addresstitle;
    public static String[] address;
    public static String[] address_picname;
    public static String[] latlng;
    public static String[] addressid;
    public static String[] zone;
    public static String[] city;
    public static String[] ostan;



    public static List<LinkedList<MaptexCameraPosition>> LONDON;

    private static SharedPreferences prefs;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        LONDON = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle("انتخاب آدرس");
        toolbar.inflateMenu(R.menu.menu_address);
        new sendDatastart().execute();
        ActionMenuItemView add = findViewById(R.id.addaddress);
        add.setOnClickListener(view -> {
            startActivity(new Intent(AddressActivity.this, AddressMapActivity.class));
            finish();
        });
    }


    private void initaddressview() {

        SupportMaptexFragment map = (SupportMaptexFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setPageMargin(Utils.dp(this, 18));
        Utils.setMargins(viewPager, 0, 0, 0, Utils.getNavigationBarHeight(this));
        Sample2Adapter adapter = new Sample2Adapter(getSupportFragmentManager());
        if( map != null) {
            MapViewPager mvp = new MapViewPager.Builder(this) // this is Context
                    .mapFragment(map)                 // mapFragment is SupportMapFragment
                    .viewPager(viewPager)                     // viewPager is ViewPager
                    .adapter(adapter)                         // adapter is MapViewPager.Adapter or MapViewPager.MultiAdapter
                    .position(1)                // Optional initialPosition is int
                    .build();
            viewPager.setAdapter(adapter);
        }
    }


    private String Resultstart;

    @Override
    public void onMaptexReady(MaptexMap var1) {

    }

    @SuppressLint("StaticFieldLeak")
    class sendDatastart extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(AddressActivity.this, "", getString(R.string.sending_alert), true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String id = prefs.getString(getString(R.string.id), "");
            String token = prefs.getString(getString(R.string.token), "");
            Resultstart = getRequeststart(id, token);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            resultAlertstart(Resultstart);
        }
    }

    private static String requeststart(HttpResponse response) {
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
                JSONArray addresslist = json.getJSONArray("list");
                addresstitle = new String[addresslist.length()];
                addressid = new String[addresslist.length()];
                zone = new String[addresslist.length()];
                address = new String[addresslist.length()];
                address_picname = new String[addresslist.length()];
                latlng = new String[addresslist.length()];
                city = new String[addresslist.length()];
                ostan = new String[addresslist.length()];
                for (int w = 0; w < addresslist.length(); w++) {
                    JSONObject addressobject = addresslist.getJSONObject(w);
                    addresstitle[w] = addressobject.getString("title");
                    addressid[w] = addressobject.getString("_id");
                    zone[w] = addressobject.getString("zone");
                    city[w] = addressobject.getString("city");
                    ostan[w] = addressobject.getString("ostan");
                    address[w] = addressobject.getString("address");
                    address_picname[w] = addressobject.getString("address_pic");
                    latlng[w] = addressobject.getString("latlng");
                    String[] latlng = addressobject.getString("latlng").split(",");
                    LinkedList<MaptexCameraPosition> england;
                    england = new LinkedList<>();
                    if (latlng.length > 1)
                        england.add(MaptexCameraPosition.fromLatLngZoom(new MaptexLatLng(Float.parseFloat(latlng[0]), Float.parseFloat(latlng[1])), 16f));
                    else
                        england.add(MaptexCameraPosition.fromLatLngZoom(new MaptexLatLng(Float.parseFloat("36.299319681156454"), Float.parseFloat("59.52185582502784")), 16f));
                    LONDON.add(england);
                }
            } else if (result.equals("empty"))
                result = "400";

        } catch (Exception ex) {
            if (result == null)
                result = "Error";
        }
        return result;
    }

    private String getRequeststart(
            String id,
            String token
    ) {
        String resultstart;

        HttpClient client = new DefaultHttpClient();
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "getaddresses");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("id", id));
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
            initaddressview();
        } else if (HasilProses.trim().equalsIgnoreCase("400")) {
            startActivity(new Intent(AddressActivity.this,AddressMapActivity.class));
            finish();

        } else {
            Toast.makeText(AddressActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
}