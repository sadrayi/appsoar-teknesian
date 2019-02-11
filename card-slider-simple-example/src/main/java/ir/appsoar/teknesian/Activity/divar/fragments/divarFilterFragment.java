package ir.appsoar.teknesian.Activity.divar.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.widget.Spinner;

import ir.appsoar.teknesian.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ir.appsoar.teknesian.Activity.divar.DivarActivity;
import ir.appsoar.teknesian.Helper.Config;


public class divarFilterFragment extends Fragment {
    String subcatselected,catselected;
    Spinner takhasos;
    Button show;
    List<String> list;
    String Resultstart;
    AutoCompleteTextView cityauto, actv, accat, acsubcat;
    Bundle bundle;
    HashMap<String, String> CountryData, CityData, Category, Subcategory,subcatid;
    View view;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.filter_divar_fragment, container, false);
        init();
        createlayout();
        return view;
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    void createlayout() {

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
                setCities(CityData.keySet().toArray(
                        new String[0]));
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
        show.setOnClickListener(view -> {
            DivarActivity divar =(DivarActivity)getActivity();
            show.requestFocus();
            Objects.requireNonNull(divar).show_list_view(catselected,subcatselected,actv.getText().toString(),cityauto.getText().toString());
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

    void init() {
        show = view.findViewById(R.id.dialogButtonOK);
        CountryData = new HashMap<>();
        Category = new HashMap<>();
        Subcategory = new HashMap<>();
        subcatid = new HashMap<>();
        CityData = new HashMap<>();
        actv = view.findViewById(R.id.ostan);
        cityauto = view.findViewById(R.id.city);
        accat = view.findViewById(R.id.category);
        acsubcat = view.findViewById(R.id.subcategory);
    }

    public String loadJSONFromAsset() {
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

    public static <T, E> E getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getKey().toString())) {
                return entry.getValue();
            }
        }
        return null;
    }

    private void setCities(String[] cData) {
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.autocompleterow, CityData.keySet().toArray(
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

    private void setSubacats(String[] cData) {
        String catkey = getKeyByValue(Category, accat.getText().toString());
        catselected=catkey;
        HashMap<String, String> filtereddata = new HashMap<>();
        filtereddata.put("همه", catkey);
        if (Subcategory.size() > 0) {
            for (Map.Entry<String, String> entry : Subcategory.entrySet()) {
                if (catkey.equals(entry.getValue())) {
                    filtereddata.put(entry.getKey(), catkey);
                }
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Objects.requireNonNull(getContext()),
                    R.layout.autocompleterow, filtereddata.keySet().toArray(
                    new String[1]));
            if (filtereddata.size() > 0) {
                acsubcat.setEnabled(true);
                acsubcat.setThreshold(1);
                acsubcat.setAdapter(adapter);
                acsubcat.setOnFocusChangeListener((view, hasFocus) -> {
                    if (!hasFocus) {
                        String val = acsubcat.getText() + "";
                        subcatselected=getKeyByValue(subcatid,val);
                        String code = filtereddata.get(val);
                        Log.v("ewr",
                                "Selected City Code: " + code);
                        if (code == null) {
                            acsubcat.setError("زیرگروه نامعتبر");
                        }
                    }
                });
                acsubcat.setOnItemClickListener((adapterView, view, i, l) -> {
                    String val = acsubcat.getText() + "";
                    subcatselected=getKeyByValue(subcatid,val);
                    String code = filtereddata.get(val);
                    Log.v("ewr",
                            "Selected City Code: " + code);
                    if (code == null) {
                        acsubcat.setError("زیرگروه نامعتبر");
                    }
                });
                acsubcat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        String val = acsubcat.getText() + "";
                        subcatselected=getKeyByValue(subcatid,val);
                        String code = filtereddata.get(val);
                        Log.v("ewr",
                                "Selected City Code: " + code);
                        if (code == null) {
                            acsubcat.setError("زیرگروه نامعتبر");
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
            else
                acsubcat.setEnabled(false);
        }
    }


    public class sendDatastart extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

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

    public String requeststart(HttpResponse response) {
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
        } catch (Exception ex) {
            if (result == null)
                result = "Error";
        }
        return result;
    }

    public String getRequeststart(
    ) {
        String resultstart;

        HttpClient client = new DefaultHttpClient();
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "divar/getcategories");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(6);
            requeststart.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse responsestart = client.execute(requeststart);
            resultstart = requeststart(responsestart);
        } catch (Exception ex) {
            resultstart = "Unable to connect.";
        }
        return resultstart;
    }

    public void resultAlertstart() {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>
                (Objects.requireNonNull(getContext()), R.layout.autocompleterow, Category.keySet().toArray(new String[0]));
        accat.setThreshold(1);//will start working from first character
        accat.setAdapter(adapter1);//setting the adapter data into the AutoCompleteTextView
        accat.setOnItemClickListener((parent, view, position, id) -> {
            String val = accat.getText() + "";
            String code = Category.get(val);
            Log.v("wrt",
                    "Selected Country Code: " + code);
            if (code != null) {
                setSubacats(CityData.keySet().toArray(
                        new String[0]));
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
                    setSubacats(CityData.keySet().toArray(
                            new String[0]));
                } else {
                    accat.setError("گروه نامعتبر");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

    }
}