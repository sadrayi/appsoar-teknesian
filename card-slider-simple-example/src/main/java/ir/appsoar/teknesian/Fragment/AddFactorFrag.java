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
import android.widget.Button;
import android.widget.EditText;
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


/**
 * Created by LapTop on 09/02/2018.
 */

public class AddFactorFrag extends Fragment {
    private String nametxt;
    private String counttxt;
    private String costtxt;
    private String type;
    private String itemid;
    private EditText name;
    private EditText count;
    private EditText cost;
    private EditText comment;
    private String commenttxt;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_factor, parentViewGroup, false);
        Bundle bundle = getArguments();
        if (bundle != null)
        {
            type = bundle.getString("type");
            costtxt = bundle.getString("cost");
            counttxt = bundle.getString("count");
            nametxt = bundle.getString("name");
            commenttxt = bundle.getString("comment");
            itemid = bundle.getString("itemid");
        }
        Button taid = rootView.findViewById(R.id.imageView13);

            cost= rootView.findViewById(R.id.editText);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        name= rootView.findViewById(R.id.editText3);
        count= rootView.findViewById(R.id.editText2);
        comment= rootView.findViewById(R.id.editText1);
        if(type.equals("new"))
            OrderManageActivity.toolbar.setTitle("افزودن آیتم");
        else {
            OrderManageActivity.toolbar.setTitle("ویرایش آیتم");
            count.setText(counttxt);
            cost.setText(costtxt);
            comment.setText(commenttxt);
            name.setText(nametxt);
            taid.setText("تایید و ویرایش");
        }
        taid.setOnClickListener(view -> {
            costtxt = cost.getText().toString();
            nametxt = name.getText().toString();
            counttxt = count.getText().toString();
            commenttxt = comment.getText().toString();
            phonenumber = prefs.getString(getString(R.string.id),"0");
            token = prefs.getString(getString(R.string.token),"0");

            if (counttxt.equalsIgnoreCase("")||
                    costtxt.equalsIgnoreCase("")||
                    nametxt.equalsIgnoreCase("")
                    ) {
                Toast.makeText(getContext(), R.string.form_alert, Toast.LENGTH_SHORT).show();

            } else {
                new sendData().execute();
            }
        });

        return rootView;
    }
    private String Result;
    private String phonenumber;
    private String token;
    class sendData extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(getContext(), "", getString(R.string.sending_alert), true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Result = getRequest(phonenumber,token,nametxt,itemid,counttxt,costtxt,commenttxt,OrderManageActivity.reqid);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            resultAlert(Result);
        }
    }    private static SharedPreferences prefs;

    private static String request(HttpResponse response) {
        String result = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            StringBuilder str = new StringBuilder();
            while ((line = in.readLine()) != null) {
                str.append(line);
            }
            JSONObject json = new JSONObject(str.toString());
            result=json.getString("status");
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
    private String getRequest(
            String mobile,
            String token,
            String name,
            String itemid,
            String count,
            String cost,
            String comment,
            String reqid
    ) {
        String result = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Config.ADMIN_PANEL_URL + "addfactoritem");
        if(type.equals("edit"))
            request = new HttpPost(Config.ADMIN_PANEL_URL + "editfactoritem");

            try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("id", mobile));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("requestid", reqid));
            nameValuePairs.add(new BasicNameValuePair("title", name));
            nameValuePairs.add(new BasicNameValuePair("itemid", itemid));
            nameValuePairs.add(new BasicNameValuePair("quantity", count));
            nameValuePairs.add(new BasicNameValuePair("percost", cost));
            nameValuePairs.add(new BasicNameValuePair("comment", comment));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = client.execute(request);
            result = request(response);
        } catch (Exception ex) {
            result = "Unable to connect.";
        }
        return result;
    }
    private void resultAlert(String HasilProses) {
        if (HasilProses.trim().equalsIgnoreCase("200")) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame,new FactorFrag());
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (HasilProses.trim().equalsIgnoreCase("400")) {
        } else {
        }
    }
}
