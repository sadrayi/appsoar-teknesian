package ir.appsoar.teknesian.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import ir.appsoar.teknesian.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ir.appsoar.teknesian.Helper.Config;
import ir.map.sdk_common.MaptexLatLng;
import ir.map.sdk_map.wrapper.MaptexBitmapDescriptorFactory;
import ir.map.sdk_map.wrapper.MaptexCameraUpdateFactory;
import ir.map.sdk_map.wrapper.MaptexMap;
import ir.map.sdk_map.wrapper.MaptexMarker;
import ir.map.sdk_map.wrapper.MaptexMarkerOptions;
import ir.map.sdk_map.wrapper.OnMaptexReadyCallback;
import ir.map.sdk_map.wrapper.SupportMaptexFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddressMapActivity extends FragmentActivity implements OnMaptexReadyCallback, LocationListener, MaptexMap.d {
    private MaptexMarker mMarker;
    private SharedPreferences prefs;
    Boolean firsttime = false;
    MaptexMap maptexMap = null;
    @SuppressLint("StaticFieldLeak")
    public static SweetAlertDialog pDialog;
    private Bundle extra;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private AutoCompleteTextView textSearch;
    private ImageView buttonSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_map);
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        extra = getIntent().getExtras();
        assert extra != null;
        textSearch = findViewById(R.id.search_bar_edit_text);
        buttonSearch = findViewById(R.id.search_bar_hint_icon);
        buttonSearch.setOnClickListener(view -> new getDataTask(mMarker.getPosition().latitude, mMarker.getPosition().longitude).execute());
        Button save = findViewById(R.id.button10);
        save.setOnClickListener(v -> {
            Intent Map = new Intent(AddressMapActivity.this, AddressAddActivity.class);
            Map.putExtra("lat", mMarker.getPosition().latitude);
            Map.putExtra("lng", mMarker.getPosition().longitude);
            startActivity(Map);
            finish();
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMaptexFragment mapFragment = (SupportMaptexFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMaptexAsync(this);
        textSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (maptexMap != null) {
                    if (mMarker != null) {
                        mMarker.remove();
                    }
                    String selected = values.get(textSearch.getText().toString());
                    //Place current location marker
                    MaptexLatLng latLng = new MaptexLatLng(Double.parseDouble(values.get(textSearch.getText().toString()).split(",")[0]), Double.parseDouble(values.get(textSearch.getText().toString()).split(",")[1]));
                    MaptexMarkerOptions markerOptions = new MaptexMarkerOptions();
                    markerOptions.position(latLng);
                    markerOptions.icon(MaptexBitmapDescriptorFactory.fromResource(R.mipmap.ic_marker));
                    mMarker = maptexMap.addMarker(markerOptions);

                    //move map camera
                    maptexMap.moveCamera(MaptexCameraUpdateFactory.newLatLng(latLng));
                    maptexMap.animateCamera(MaptexCameraUpdateFactory.zoomTo(18));
                }
            }
        });
    }

    private String Resultstart;

    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (maptexMap != null) {
                        maptexMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onMaptexReady(MaptexMap var1) {
        maptexMap = var1;
        if (maptexMap != null) { // Checks if we were successful in obtaining the map

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(AddressMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(AddressMapActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            //endregion get permissions


            MaptexLatLng sydney;
            // Add a marker in Sydney and move the camera
            sydney = new MaptexLatLng(36.310699, 59.599457);

            mMarker = maptexMap.addMarker(new MaptexMarkerOptions().position(sydney).draggable(true).icon(MaptexBitmapDescriptorFactory.fromResource(R.mipmap.ic_marker)));
            maptexMap.moveCamera(MaptexCameraUpdateFactory.newLatLngZoom(sydney, 15.0f));
            maptexMap.setOnCameraChangeListener(MaptexcameraPosition -> mMarker.setPosition(MaptexcameraPosition.target));
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                maptexMap.setMyLocationEnabled(true);

            }
            maptexMap.getUiSettings().setMyLocationButtonEnabled(true);
            maptexMap.setOnMyLocationChangeListener(this);

        }
    }

    Location mLastLocation;

    @Override
    public void onLocationChanged(Location location) {
        if (!firsttime) {
            firsttime = true;
            mLastLocation = location;
            if (mMarker != null) {
                mMarker.remove();
            }

            //Place current location marker
            MaptexLatLng latLng = new MaptexLatLng(location.getLatitude(), location.getLongitude());
            MaptexMarkerOptions markerOptions = new MaptexMarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(MaptexBitmapDescriptorFactory.fromResource(R.mipmap.ic_marker));
            mMarker = maptexMap.addMarker(markerOptions);

            //move map camera
            maptexMap.moveCamera(MaptexCameraUpdateFactory.newLatLng(latLng));
            maptexMap.animateCamera(MaptexCameraUpdateFactory.zoomTo(11));
        }
    }

    @Override
    public void a(Location location) {
        if (!firsttime) {
            firsttime = true;
            mLastLocation = location;
            if (mMarker != null) {
                mMarker.remove();
            }

            //Place current location marker
            MaptexLatLng latLng = new MaptexLatLng(location.getLatitude(), location.getLongitude());
            MaptexMarkerOptions markerOptions = new MaptexMarkerOptions();
            markerOptions.position(latLng);
            markerOptions.icon(MaptexBitmapDescriptorFactory.fromResource(R.mipmap.ic_marker));
            mMarker = maptexMap.addMarker(markerOptions);

            //move map camera
            maptexMap.moveCamera(MaptexCameraUpdateFactory.newLatLng(latLng));
            maptexMap.animateCamera(MaptexCameraUpdateFactory.zoomTo(11));
        }
    }

    /////////////
    public class getDataTask extends AsyncTask<Void, Void, Void> {
        Double lat, lng;

        getDataTask(Double lat, Double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            parseJSONData(lat, lng);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


        }
    }

    HashMap<String, String> values = new HashMap<>();

    public void parseJSONData(Double lat, Double lng) {

        try {
            // request data from menu detail API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet(Config.SocketUrl + "map/search?text=" + textSearch.getText().toString().replace(" ", "%20") + "&lat=" + lat + "&lng=" + lng);
            HttpResponse response = client.execute(request);
            InputStream atomInputStream = response.getEntity().getContent();


            BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

            String line;
            String str = "";
            while ((line = in.readLine()) != null) {
                str += line;
            }

            // parse json data and store into tax and currency variables
            JSONObject json = new JSONObject(str);
            JSONObject body = json.getJSONObject("body");
            if (json.getString("statusCode").equals("200")) {

                JSONArray value = body.getJSONArray("value");
                values = new HashMap<>();
                String[] values1 = new String[value.length()];
                for (int i = 0; i < value.length(); i++) {
                    values.put(value.getJSONObject(i).getString("Address"), value.getJSONObject(i).getJSONObject("Coordinate").getString("lat") + "," + value.getJSONObject(i).getJSONObject("Coordinate").getString("lon"));
                    values1[i] = value.getJSONObject(i).getString("Address");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.mapautocomplete, values1);
                showerrorconnect(adapter);
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void showerrorconnect(ArrayAdapter<String> adapter) {
        runOnUiThread(() -> {
                    textSearch.setAdapter(adapter);
                    textSearch.setThreshold(0);
                    textSearch.showDropDown();
                }
        );
    }
}

