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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import ir.appsoar.teknesian.R;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;

import ir.appsoar.teknesian.Helper.Config;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity implements IPickResult {
    private EditText nametxt;
    private EditText phonetxt;
    private EditText ostan;
    private EditText city;
    private EditText takhasos;
    private HashMap<String, String> CountryData;
    private HashMap<String, String> CityData;
    private SharedPreferences prefs;
    private AutoCompleteTextView cityauto;
    private AutoCompleteTextView actv;
    boolean edit;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        nametxt = findViewById(R.id.name);
        phonetxt = findViewById(R.id.phone);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (!prefs.getString(getString(R.string.name), "0").equals("0"))
            edit = false;
        phonetxt.setText(prefs.getString(getString(R.string.phone), "0"));
        token = prefs.getString(getString(R.string.token), "0");
        Button registerbut = findViewById(R.id.dialogButtonOK);
        ostan = findViewById(R.id.ostan2);
        takhasos = findViewById(R.id.takhasosreg);
        city = findViewById(R.id.city2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        CountryData = new HashMap<>();
        CityData = new HashMap<>();
        if (edit) {

            toolbar.setTitle("ویرایش");
            registerbut.setText("ویرایش");
        }else {
            toolbar.setTitle("ثبت نام");
        }
        registerbut.setOnClickListener(view -> {
            if (!city.getText().toString().equals("") && !takhasos.getText().toString().equals("") && !nametxt.getText().toString().equals("")) {

                SweetAlertDialog regdialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.NORMAL_TYPE);
                regdialog.setTitleText("تاییدیده")
                        .setContentText("آیا تمایل به افزودن تصویر دارید ؟")
                        .setConfirmText("بله")
                        .setCancelText("خیر")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            PickSetup setup = new PickSetup()
                                    .setTitle("انتخاب تصویر کاربری")
                                    .setProgressText("درحال انتخاب")
                                    .setCancelText("لغو")
                                    .setFlip(true)
                                    .setMaxSize(500)
                                    .setCameraButtonText("گرفتن عکس")
                                    .setGalleryButtonText("انتخاب از گالری")
                                    .setButtonOrientation(LinearLayout.VERTICAL)
                                    .setSystemDialog(false);
                            PickImageDialog.build(setup).show(RegisterActivity.this);
                        })
                        .setCancelClickListener(sweetAlertDialog -> new sendData().execute()).show();
            } else {
                SweetAlertDialog pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.WARNING_TYPE);
                pDialog
                        .setTitleText("هشدار")
                        .setContentText("ابتدا تمام قسمت ها را تکمیل فرمایید.")
                        .setConfirmClickListener(sweetAlertDialog -> pDialog.dismiss()).setConfirmText("تایید")
                        .setCancelable(true);
                pDialog.show();
            }
        });
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>
                (this, android.R.layout.select_dialog_item, CountryData.keySet().toArray(new String[0]));
        //Getting the instance of AutoCompleteTextView
        actv = findViewById(R.id.ostan2);
        cityauto = findViewById(R.id.city2);

        actv.setThreshold(1);//will start working from first character
        actv.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        actv.setOnItemClickListener((parent, view, position, id) -> {
            String val = actv.getText() + "";
            String code = CountryData.get(val);
            Log.v("wrt",
                    "Selected Country Code: " + code);
            if (code != null) {
                setCities();
            } else {
                actv.setError("استان نامعتبر");
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
                    setCities();
                } else {
                    actv.setError("استان نامعتبر");
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private File photo;

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {

            photo = new File(r.getPath());
            new sendData().execute();

        } else {
            SweetAlertDialog pDialog = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.WARNING_TYPE);
            pDialog
                    .setTitleText("هشدار")
                    .setContentText("انتخاب تصویر اجباری است.")
                    .setConfirmClickListener(sweetAlertDialog -> pDialog.dismiss()).setConfirmText("تایید")
                    .setCancelable(true);
            pDialog.show();
        }
    }

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = RegisterActivity.this.getAssets().open("Province.json");
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

    private void setCities() {
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


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, CityData.keySet().toArray(
                new String[1]));

        cityauto.setEnabled(true);
        cityauto.setThreshold(2);
        cityauto.setAdapter(adapter);
        cityauto.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                String val = cityauto.getText() + "";
                String code = CityData.get(val);
                Log.v("ewr",
                        "Selected City Code: " + code);
                if (code == null) {
                    cityauto.setError("شهر نامعتبر");
                }
            }
        });
    }

    private String token;
    private String Result;

    @SuppressLint("StaticFieldLeak")
    class sendData extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(RegisterActivity.this, "", getString(R.string.sending_alert), true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Result = getRequest(phonetxt.getText().toString(), token, nametxt.getText().toString(), cityauto.getText().toString(), ostan.getText().toString(), takhasos.getText().toString());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            resultAlert(Result);
        }
    }

    private String request(HttpResponse response) {
        String result = "";

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            StringBuilder str = new StringBuilder();
            while ((line = in.readLine()) != null) {
                str.append(line);
            }
            JSONObject json1 = new JSONObject(str.toString());
            result = json1.getString("message");
            if (json1.has("profile_pic"))
                prefs.edit().putString(getString(R.string.profile_pic), json1.getString("profile_pic")).apply();
            prefs.edit().putString(getString(R.string.username), nametxt.getText().toString()).apply();


        } catch (Exception ex) {
            if (result == null)
                result = "Error";
        }
        return result;
    }

    private String getRequest(
            String mobile,
            String token,
            String name,
            String city,
            String ostan,
            String takhasos
    ) {
        String result;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Config.ADMIN_PANEL_URL + "registeration");

        try {
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart("phone", new StringBody(mobile, Charset.forName("UTF-8")));
            multipartEntity.addPart("ostan", new StringBody(ostan, Charset.forName("UTF-8")));
            multipartEntity.addPart("city", new StringBody(city, Charset.forName("UTF-8")));
            multipartEntity.addPart("takhasos", new StringBody(takhasos, Charset.forName("UTF-8")));
            multipartEntity.addPart("token", new StringBody(token, Charset.forName("UTF-8")));
            multipartEntity.addPart("name", new StringBody(name, Charset.forName("UTF-8")));
            if (photo != null)
                multipartEntity.addPart("image", new FileBody(photo));
            request.setEntity(multipartEntity);
            HttpResponse response = client.execute(request);
            result = request(response);
        } catch (Exception ex) {
            result = "Unable to connect.";
        }
        return result;
    }

    private void resultAlert(String HasilProses) {
        if (HasilProses.trim().equalsIgnoreCase("success")) {
            Intent i = new Intent(RegisterActivity.this, FirstActivity.class);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

}


