package ir.appsoar.teknesian.Activity.divar.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bumptech.glide.Glide;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import ir.appsoar.teknesian.R;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ir.appsoar.teknesian.Activity.divar.DivarActivity;
import ir.appsoar.teknesian.Helper.Config;


public class AddDivarFragment extends Fragment implements IPickResult {
    private String subcatselected;
    private String catselected;
    private Button show;
    private File photo;
    private EditText title;
    private EditText price;
    private EditText detail;
    private RadioGroup radioGroup;
    private RadioButton tawafogh,darj;
    private ImageButton imag;
    private AutoCompleteTextView cityauto;
    private AutoCompleteTextView actv;
    private AutoCompleteTextView accat;
    private AutoCompleteTextView acsubcat;
    private HashMap<String, String> CountryData;
    private HashMap<String, String> CityData;
    private HashMap<String, String> Category;
    private HashMap<String, String> Subcategory;
    private HashMap<String, String> subcatid;
    private View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_divar_add, container, false);
        init();
        createlayout();
        return view;
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    private void createlayout() {
        imag.setOnClickListener(view -> {

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
            PickImageDialog.build(setup)

                    .setOnPickResult(r -> {
                        Glide.with(Objects.requireNonNull(getContext())).load(r.getUri()).into(imag);
                        photo = new File(r.getPath());
                    })
                    .setOnPickCancel(() -> {
                    }).show(Objects.requireNonNull(getActivity()).getSupportFragmentManager());
        });
        new sendDatastart().execute();
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
                (Objects.requireNonNull(getContext()), R.layout.autocompleterow, CountryData.keySet().toArray(new String[0]));
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
        show.setOnClickListener((View view) -> {
            if ((catselected != null) && !title.getText().toString().equals("") && !detail.getText().toString().equals("") && !price.getText().toString().equals("") && !cityauto.getText().toString().toString().equals("") && !actv.getText().toString().toString().equals("") && (photo != null)) {
                if (subcatselected == null)
                    subcatselected = "-1";
                new sendData().execute();
            } else {
                SweetAlertDialog pDialog1 = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                pDialog1
                        .setTitleText("خطا")
                        .setContentText("پر کردن تمام فیلد ها الزامی است.")
                        .setConfirmClickListener(Dialog::dismiss)
                        .setConfirmText("تایید").show();
            }
        });
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {

            if (tawafogh.isChecked()) {
                price.setEnabled(false);
                darj.setChecked(false);
                price.setText("توافقی");
            } if (darj.isChecked()) {
                price.setEnabled(true);
                tawafogh.setChecked(false);
                price.setText("");
            }


        });
        accat.setOnTouchListener((v, event) -> {
            accat.showDropDown();
            return false;
        });

        acsubcat.setOnTouchListener((v, event) -> {
            acsubcat.showDropDown();
            return false;
        });
    }

    private void init() {
        imag = view.findViewById(R.id.image);
        show = view.findViewById(R.id.dialogButtonOK);
        CountryData = new HashMap<>();
        Category = new HashMap<>();
        title = view.findViewById(R.id.title);
        price = view.findViewById(R.id.price);
        detail = view.findViewById(R.id.detail);
        radioGroup = view.findViewById(R.id.radiogriup);
        tawafogh = view.findViewById(R.id.tawafoghi);
        darj = view.findViewById(R.id.darj);
        Subcategory = new HashMap<>();
        subcatid = new HashMap<>();
        CityData = new HashMap<>();
        actv = view.findViewById(R.id.ostan);
        cityauto = view.findViewById(R.id.city);
        accat = view.findViewById(R.id.category);
        acsubcat = view.findViewById(R.id.subcategory);
    }

    private String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = Objects.requireNonNull(getActivity()).getAssets().open("Province.json");
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

    private static <T, E> E getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getKey().toString())) {
                return entry.getValue();
            }
        }
        return null;
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
                    CityData.put("همه", "-1");
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
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                R.layout.autocompleterow, CityData.keySet().toArray(
                new String[1]));
        cityauto.setEnabled(true);
        cityauto.setThreshold(0);
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

    private void setSubacats() {
        String catkey = getKeyByValue(Category, accat.getText().toString());
        catselected = catkey;
        HashMap<String, String> filtereddata = new HashMap<>();
        filtereddata.put("همه", catkey);
        if (Subcategory.size() > 0) {
            for (Map.Entry<String, String> entry : Subcategory.entrySet()) {
                assert catkey != null;
                if (catkey.equals(entry.getValue())) {
                    filtereddata.put(entry.getKey(), catkey);
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(Objects.requireNonNull(getContext()),
                    R.layout.autocompleterow, filtereddata.keySet().toArray(
                    new String[1]));
            if (filtereddata.size() > 0) {
                acsubcat.setEnabled(true);
                acsubcat.setThreshold(1);
                acsubcat.setAdapter(adapter);
                acsubcat.setOnFocusChangeListener((view, hasFocus) -> {
                    if (!hasFocus) {
                        String val = acsubcat.getText() + "";
                        subcatselected = getKeyByValue(subcatid, val);
                        String code = filtereddata.get(val);
                        Log.v("ewr",
                                "Selected City Code: " + code);
                        if (code == null) {
                            acsubcat.setError("زیرگروه نامعتبر");
                        }
                    }
                });
            } else
                acsubcat.setEnabled(false);
        }
    }


    String Resultstart;
    @SuppressLint("StaticFieldLeak")
    class sendDatastart extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            Resultstart = getRequeststart();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            resultAlertstart();
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
            JSONArray json = new JSONArray(str.toString());
            for (int i = 0; i < json.length(); i++) {
                JSONObject categories = json.getJSONObject(i);
                Category.put(categories.getString("catname"), categories.getString("catid"));
                JSONArray citiesjson = categories.getJSONArray("subcats");
                for (int o = 0; o < citiesjson.length(); o++) {
                    JSONObject city1 = citiesjson.getJSONObject(o);
                    Subcategory.put(city1.getString("subcatname"), categories.getString("catid"));
                    subcatid.put(city1.getString("subcatname"), city1.getString("_id"));
                }
            }
        } catch (Exception ignored) {
        }
        return result;
    }

    private String getRequeststart(
    ) {
        String resultstart;

        HttpClient client = new DefaultHttpClient();
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "divar/getcategories");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            requeststart.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse responsestart = client.execute(requeststart);
            resultstart = requeststart(responsestart);
        } catch (Exception ex) {
            resultstart = "Unable to connect.";
        }
        return resultstart;
    }

    private void resultAlertstart() {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>
                (Objects.requireNonNull(getContext()),R.layout.autocompleterow, Category.keySet().toArray(new String[0]));
        accat.setThreshold(0);//will start working from first character
        accat.setAdapter(adapter1);//setting the adapter data into the AutoCompleteTextView
        accat.setOnItemClickListener((parent, view, position, id) -> {
            String val = accat.getText() + "";
            String code = Category.get(val);
            Log.v("wrt",
                    "Selected Country Code: " + code);
            if (code != null) {
                setSubacats();
            } else {
                accat.setError("گروه نامعتبر");
            }
        });
        accat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                acsubcat.setAdapter(null);
                String val = accat.getText() + "";
                String code = Category.get(val);
                Log.v("wrt",
                        "Selected Country Code: " + code);
                if (code != null) {
                    setSubacats();
                } else {
                    accat.setError("گروه نامعتبر");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }

    @Override
    public void onPickResult(PickResult r) {
        if (r.getError() == null) {
            //If you want the Uri.
            //Mandatory to refresh image from Uri.
            //getImageView().setImageURI(null);

            //Setting the real returned image.
            //getImageView().setImageURI(r.getUri());

            //If you want the Bitmap.
            Glide.with(Objects.requireNonNull(getContext())).load(r.getUri()).into(imag);
            //Image path
            //r.getPath();
        } else {
            //Handle possible errors
            //TODO: do what you have to do with r.getError();
            SweetAlertDialog pDialog = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.WARNING_TYPE);
            pDialog
                    .setTitleText("هشدار")
                    .setContentText("انتخاب تصویر اجباری است.")
                    .setConfirmClickListener(sweetAlertDialog -> pDialog.dismiss()).setConfirmText("تایید")
                    .setCancelable(true);
            pDialog.show();
        }
    }

    ///////////
    ////////////
    ////////
    private SweetAlertDialog activdialuge;
    private String Result;

    @SuppressLint("StaticFieldLeak")
    class sendData extends AsyncTask<Void, Void, Void> {
        sendData() {
            activdialuge = new SweetAlertDialog(Objects.requireNonNull(getContext()), SweetAlertDialog.PROGRESS_TYPE);
            activdialuge
                    .setTitleText("ثبت آگهی")
                    .setContentText("درحال ارسال اطلاعات آگهی ...")
                    .setCancelable(false);
            activdialuge.show();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            Result = getRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
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


        } catch (Exception ignored) {
        }
        return result;
    }

    private String getRequest(
    ) {
        String result;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Config.ADMIN_PANEL_URL + "divar/addproduct");

        try {
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart("id", new StringBody(DivarActivity.id, Charset.forName("UTF-8")));
            multipartEntity.addPart("token", new StringBody(DivarActivity.token, Charset.forName("UTF-8")));
            multipartEntity.addPart("title", new StringBody(title.getText().toString(), Charset.forName("UTF-8")));
            multipartEntity.addPart("ostan", new StringBody(actv.getText().toString(), Charset.forName("UTF-8")));
            multipartEntity.addPart("city", new StringBody(cityauto.getText().toString(), Charset.forName("UTF-8")));
            multipartEntity.addPart("catid", new StringBody(catselected, Charset.forName("UTF-8")));
            multipartEntity.addPart("subcatid", new StringBody(subcatselected, Charset.forName("UTF-8")));
            multipartEntity.addPart("detail", new StringBody(detail.getText().toString(), Charset.forName("UTF-8")));
            multipartEntity.addPart("price", new StringBody(price.getText().toString(), Charset.forName("UTF-8")));
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
            activdialuge.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            activdialuge.setContentText("آگهی مد نظر با موفقیت ثبت شد.");
            activdialuge.showCancelButton(false);
            activdialuge.setConfirmText("تایید");
            activdialuge.setConfirmClickListener(sweetAlertDialog -> {
                activdialuge.dismiss();
                DivarActivity divarActivity = (DivarActivity) getActivity();
                assert divarActivity != null;
                divarActivity.show_mydivar();
            });
        } else {
            activdialuge.changeAlertType(SweetAlertDialog.WARNING_TYPE);
            activdialuge.setContentText("خطایی رخ داده است مجددا تلاش نمایید.");
            activdialuge.showCancelButton(false);
            activdialuge.setConfirmText("تایید");
            activdialuge.setConfirmClickListener(sweetAlertDialog -> activdialuge.dismiss());
        }
    }

}