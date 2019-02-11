package ir.appsoar.teknesian.Dialoge;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
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
import ir.appsoar.teknesian.Fragment.FactorFrag;
import ir.appsoar.teknesian.Helper.Config;


/**
 * Created by LapTop on 24/09/2017.
 */

public class DeleteFactorItemDialog extends Dialog implements
        View.OnClickListener {

    private String id;
    private String token;
    private String itemid;

    public DeleteFactorItemDialog(Activity a, String itemid, String mobile, String token) {
        super(a);
        // TODO Auto-generated constructor stub
        Activity c = a;
        this.id=mobile;
        this.itemid=itemid;
        this.token=token;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        Button yes = findViewById(R.id.taid);
        Button no = findViewById(R.id.cancel);
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        TextView content = findViewById(R.id.txt_dia);
        content.setText("آیا مطمئنید میخواهید ردیف فوق را حذف نمایید ؟");


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taid:
                new sendData().execute();
                break;
            case R.id.cancel:
                dismiss();
                               break;
            default:
                break;
        }
        dismiss();
    }
    private String Result;
    class sendData extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(getContext(), "", getContext().getString(R.string.sending_alert), true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Result = getRequest(id,token, OrderManageActivity.reqid,itemid);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            resultAlert(Result);
        }
    }    public static SharedPreferences prefs;

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
            String id,
            String token,
            String reqid,
            String itemid
    ) {
        String result = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Config.ADMIN_PANEL_URL + "removefactoritem");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("requestid", reqid));
            nameValuePairs.add(new BasicNameValuePair("itemid", itemid));
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
            dismiss();
            FactorFrag.clearData();
            new FactorFrag.sendData().execute();
        } else if (HasilProses.trim().equalsIgnoreCase("400")) {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
}