package ir.appsoar.teknesian.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

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
import java.util.List;

import ir.appsoar.teknesian.Adapter.OrdersAdapter;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Models.CatnameModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by LapTop on 07/08/2017.
 */

public class Requests_Frag extends AppCompatActivity {

    private static SharedPreferences prefs;
    private static List<CatnameModel> catname;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_tarikhche_frag);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        new sendDatastart().execute();
    }

    private String Resultstart;
    @SuppressLint("StaticFieldLeak")
    class sendDatastart extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(Requests_Frag.this, "", getString(R.string.sending_alert), true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String id = prefs.getString(getString(R.string.id),"");
            String token = prefs.getString(getString(R.string.token),"");
            Resultstart = getRequeststart(id,token);
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
            result=json.getString("message");
            if(result.equals("success"))
            {
                catname=new ArrayList<>();
                result= "200";
                JSONArray jsonArray=json.getJSONArray("list");
                for(int h=0;h<jsonArray.length();h++)
                {
                    JSONObject jsonobj=jsonArray.getJSONObject(h);
                    CatnameModel res=new CatnameModel();
                    if(jsonobj.has("_id"))
                        res.setRequestId(jsonobj.getString("_id"));
                    else
                        res.setRequestId("0");

                    if(jsonobj.has("kind"))
                        res.setCatName(jsonobj.getString("kind"));
                    else
                        res.setCatName("درخواست تعمیر");
                    catname.add(res);
                }
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
    private String getRequeststart(
            String id,
            String token
    ) {
        String resultstart;

        HttpClient client = new DefaultHttpClient();
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "getrequestlist");
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
            Toast.makeText(Requests_Frag.this, R.string.error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(Requests_Frag.this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
    private void initaddressview(){

        RecyclerView recyclerView = findViewById(R.id.recyclertarikhche);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        if(catname!=null) {
            OrdersAdapter recyclerViewAdapter = new OrdersAdapter(catname, this);

            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

}
