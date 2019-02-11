package ir.appsoar.teknesian.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import ir.appsoar.teknesian.Helper.Config;
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
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AddressAddActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    ArrayList<HashMap<String, ArrayList<String>>> formList = new ArrayList<>();
    HashMap<String, String> CountryData;
    HashMap<String, String> CityData;
    SharedPreferences prefs;
    AutoCompleteTextView cityauto;
    AutoCompleteTextView actv;
    private Bundle extra;
    EditText addressname,ostan,city;
    Spinner title;
    public String lat,lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_add);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        Bundle bundle = getIntent().getExtras();
        String mokhtasat = null;
        String addressid = null;
        extra = getIntent().getExtras();
        try {
            lat= String.valueOf(extra.getDouble("lat"));
            lng= String.valueOf(extra.getDouble("lng"));

        }catch (Exception ignored){

        }
        Display mDisplay = this.getWindowManager().getDefaultDisplay();
        final int width = mDisplay.getWidth();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width * 1100 / 1200);
        addressname = findViewById(R.id.address);
        ostan = findViewById(R.id.ostan);
        title = findViewById(R.id.title);
        city = findViewById(R.id.city);
        if (bundle != null) {
            addressname.setText(bundle.getString("addressname"));
            addressid = bundle.getString("addressid");
            mokhtasat = bundle.getString("mokhtasat");
        }
        CountryData = new HashMap<String, String>();
        CityData = new HashMap<String, String>();
        final android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("افزودن آدرس");
        Button add = findViewById(R.id.addaddresstext);
        final String finalMokhtasat = mokhtasat;
        final String finalAddressid = addressid;
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(ostan.getText().toString().isEmpty() && city.getText().toString().isEmpty() && addressname.getText().toString().isEmpty() && title.getSelectedItem().toString().isEmpty())) {
                    new sendDatastart().execute();
                }

            }
        });

        new getDataTask(lat,lng).execute();
        try {
            JSONArray list = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < list.length(); i++) {
                JSONObject ostans = list.getJSONObject(i);
                CountryData.put(ostans.getString("name"), String.valueOf(i));
                JSONArray citiesjson = ostans.getJSONArray("Cities");
                for (int o = 0; o < citiesjson.length(); o++) {
                    JSONObject city1 = citiesjson.getJSONObject(o);
                    CityData.put(String.valueOf(i), city1.getString("name"));
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.select_dialog_item, CountryData.keySet().toArray(new String[0]));
        //Getting the instance of AutoCompleteTextView
        actv = findViewById(R.id.ostan);
        cityauto = findViewById(R.id.city);
        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String val = actv.getText() + "";
                String code = CountryData.get(val);
                Log.v("wrt",
                        "Selected Country Code: " + code);
                if (code != null) {
                    setCities(CityData.keySet().toArray(
                            new String[0]));
                } else {
                    actv.setError("استان نامعتبر");
                }

            }
        });

        actv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cityauto.setAdapter(null);
                String val = actv.getText() + "";
                String code = CountryData.get(val);
                Log.v("wrt",
                        "Selected Country Code: " + code);
                if (code != null) {
                    setCities(CityData.keySet().toArray(
                            new String[0]));
                } else {
                    actv.setError("استان نامعتبر");
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = AddressAddActivity.this.getAssets().open("Province.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private String Resultstart;
    private void setCities(String cData[]) {
        ArrayList<String> city = new ArrayList<>();
        CityData = new HashMap<>();

        try {
            JSONArray list = new JSONArray(loadJSONFromAsset());
            for (int e = 0; e < list.length(); e++) {
                JSONObject ostans = list.getJSONObject(e);
                String formula_value = ostans.getString("name");
                if (formula_value.equals(actv.getText().toString())) {
                    JSONArray citiesjson = ostans.getJSONArray("Cities");
                    for (int o = 0; o < citiesjson.length(); o++) {
                        JSONObject city12 = citiesjson.getJSONObject(o);
                        Log.e("cit", city12.getString("name"));
                        CityData.put(city12.getString("name"), String.valueOf(o));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, CityData.keySet().toArray(
                new String[1]));

        cityauto.setEnabled(true);
        cityauto.setThreshold(2);
        cityauto.setAdapter(adapter);
        cityauto.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String val = cityauto.getText() + "";
                    String code = CityData.get(val);
                    Log.v("ewr",
                            "Selected City Code: " + code);
                    if (code == null) {
                        cityauto.setError("شهر نامعتبر");
                    }
                }
            }
        });
    }
    public class sendDatastart extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(AddressAddActivity.this, "", getString(R.string.sending_alert), true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String id = prefs.getString(getString(R.string.id),"");
            String token = prefs.getString(getString(R.string.token),"");
            Resultstart = getRequeststart(id,token,addressname.getText().toString(),ostan.getText().toString(),city.getText().toString(),title.getSelectedItem().toString(),lat+","+lng);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            resultAlertstart(Resultstart);
        }
    }

    public static String requeststart(HttpResponse response) {
        String result = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            String str = "";
            while ((line = in.readLine()) != null) {
                str += line;
            }
            JSONObject json = new JSONObject(str);
            result=json.getString("message");
            if(result.equals("success"))
            {
                result= "200";
            }
            else
            if(result.equals("empty"))
                result= "400";

        } catch (Exception ex) {
            if(result==null)
                result = "Error";
        }
        return result;
    }
    public String getRequeststart(
            String id,
            String token,
            String address,
            String ostan,
            String city,
            String title,
            String latlng
    ) {
        String resultstart = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "addaddress");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("ostan", ostan));
            nameValuePairs.add(new BasicNameValuePair("city", city));
            nameValuePairs.add(new BasicNameValuePair("zone", ""));
            nameValuePairs.add(new BasicNameValuePair("address", address));
            nameValuePairs.add(new BasicNameValuePair("title", title));
            nameValuePairs.add(new BasicNameValuePair("latlng", latlng));
            requeststart.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse responsestart = client.execute(requeststart);
            resultstart = requeststart(responsestart);
        } catch (Exception ex) {
            resultstart = "Unable to connect.";
        }
        return resultstart;
    }
    public void resultAlertstart(String HasilProses) {
        if (HasilProses.trim().equalsIgnoreCase("200")) {
            startActivity(new Intent(AddressAddActivity.this,AddressActivity.class));
            finish();
        } else if (HasilProses.trim().equalsIgnoreCase("400")) {
            Toast.makeText(AddressAddActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AddressAddActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
    /////////////////////////////////

    public class getDataTask extends AsyncTask<Void, Void, Void> {
        String lat, lng;

        getDataTask(String lat, String lng) {
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            parseJSONData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {


        }
    }

    HashMap<String, String> values = new HashMap<>();

    public void parseJSONData() {

        try {
            // request data from menu detail API
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet("https://map.ir/reverse?lat="+lat+"&lon="+lng);
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
            String postalladdress=json.getString("postal_address");
            String ostan=json.getString("province");
            String city=json.getString("city");
            showerrorconnect(postalladdress,ostan,city);


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

    public void showerrorconnect(String address,String ostan,String city) {
        runOnUiThread(() -> {
                    addressname.setText(address);
                    cityauto.setText(city);
                    cityauto.setEnabled(true);
                    actv.setText(ostan);
                    actv.dismissDropDown();
                    cityauto.dismissDropDown();
                }
        );
    }

}
