package ir.appsoar.teknesian.Fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

import ir.appsoar.teknesian.Activity.OrderManageActivity;
import ir.appsoar.teknesian.Adapter.BuildingHistoryAdapter;
import ir.appsoar.teknesian.Adapter.OrdersAdapter;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Helper.HelperShamsi;
import ir.appsoar.teknesian.Models.BuildingHistoryModel;
import ir.appsoar.teknesian.Models.CatnameModel;
import ir.appsoar.teknesian.R;

public class BuildingHistoryFragment extends Fragment {
    private static SharedPreferences prefs;
    private static List<BuildingHistoryModel> catname;
    RecyclerView recyclerView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_building_history, parentViewGroup, false);
        OrderManageActivity.toolbar.setTitle("تاریخچه ساختمان");
        prefs=PreferenceManager.getDefaultSharedPreferences(getContext());
        recyclerView=rootView.findViewById(R.id.recyclertarikhche);
        new sendDatastart().execute();

        return rootView;
    }
    private String Resultstart;
    class sendDatastart extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(getActivity(), "", getString(R.string.sending_alert), true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String id = prefs.getString(getString(R.string.id),"");
            String token = prefs.getString(getString(R.string.token),"");
            Resultstart = getRequeststart(id,token,OrderManageActivity.reqid);
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
            if(result.equals("success")){
                result= "200";
                if(json.has("data")){
                    JSONArray array=json.getJSONArray("data");
                    catname=new ArrayList<>();
                    for(int q=0;q<array.length();q++){
                        JSONObject json1=array.getJSONObject(q);
                        BuildingHistoryModel building=new BuildingHistoryModel(
                                json1.getString("id"),
                                HelperShamsi.gregorianToShamsiDate(json1.getString("date")),
                                json1.getString("kind"),
                                json1.getString("teknesian")
                        );
                        catname.add(building);
                    }
                }
            }
            else
                result= "400";

        } catch (Exception ex) {
            if(result==null)
                result = "Error";
        }
        return result;
    }
    private String getRequeststart(
            String id,
            String token,
            String reqid
    ) {
        String resultstart = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "buildinghistory");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("reqid", reqid));
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
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
    private void initaddressview(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(layoutManager);
        if(catname!=null) {
            BuildingHistoryAdapter recyclerViewAdapter = new BuildingHistoryAdapter(catname, getContext());

            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }
}