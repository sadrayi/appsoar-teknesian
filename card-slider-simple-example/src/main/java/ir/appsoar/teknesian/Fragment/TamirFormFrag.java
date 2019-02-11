package ir.appsoar.teknesian.Fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ir.appsoar.teknesian.Activity.OrderManageActivity;
import ir.appsoar.teknesian.Helper.Config;

public class TamirFormFrag extends Fragment {
    private static SharedPreferences prefs;
    private Spinner tamirkindspin;
    private EditText saieredit;
    private EditText eghdamatedit;
    private Spinner teknesianneedspin;
    private Spinner spareneedspin;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_tamirform, parentViewGroup, false);
        OrderManageActivity.toolbar.setTitle("فرم تعمیر");
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
         tamirkindspin= rootView.findViewById(R.id.tamirkind);
         saieredit= rootView.findViewById(R.id.saier);
        eghdamatedit= rootView.findViewById(R.id.eghdamat);
         teknesianneedspin= rootView.findViewById(R.id.teknesianneed);
         spareneedspin= rootView.findViewById(R.id.spareneed);
        Button confirm= rootView.findViewById(R.id.dialogButtonOK);
        confirm.setOnClickListener(view -> new sendDatastart().execute());
        tamirkindspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(position==7)
                saieredit.setEnabled(true);
                else
                    saieredit.setEnabled(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });
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
            String tamirkindtxt=tamirkindspin.getSelectedItem().toString();
            if(tamirkindtxt.equals("سایر"))
                tamirkindtxt=saieredit.getText().toString();
            Resultstart = getRequeststart(id,token,OrderManageActivity.reqid,tamirkindtxt,spareneedspin.getSelectedItem().toString(),teknesianneedspin.getSelectedItem().toString(),eghdamatedit.getText().toString());
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
                result= "200";
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
            String reqid,
            String tamirkind,
            String spareneed,
            String teknesianneed,
            String eghdamat
    ) {
        String resultstart = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "tamirformsave");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("reqid", reqid));
            nameValuePairs.add(new BasicNameValuePair("tamirkind", tamirkind));
            nameValuePairs.add(new BasicNameValuePair("spareneed", spareneed));
            nameValuePairs.add(new BasicNameValuePair("teknesianneed", teknesianneed));
            nameValuePairs.add(new BasicNameValuePair("eghdamat", eghdamat));
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
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame,new FactorFrag());
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (HasilProses.trim().equalsIgnoreCase("400")) {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

}