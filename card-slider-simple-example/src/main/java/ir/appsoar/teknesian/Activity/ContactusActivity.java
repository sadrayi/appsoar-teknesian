package ir.appsoar.teknesian.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import ir.appsoar.teknesian.Helper.Config;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ContactusActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private EditText phone;
    private EditText email;
    private EditText comment;
    private EditText name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        phone= findViewById(R.id.phone);
        name= findViewById(R.id.name);
        email= findViewById(R.id.email);
        Button send = findViewById(R.id.dialogButtonOK);
        comment= findViewById(R.id.comment);
        send.setOnClickListener(view -> new sendDatastart().execute());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        phone.setText(prefs.getString(getString(R.string.phone),"09"));
        name.setText(prefs.getString(getString(R.string.username),"09"));
        Glide.with(this).load(R.drawable.contactheader).into((ImageView)findViewById(R.id.backhead));
    }
    private String Resultstart;
    @SuppressLint("StaticFieldLeak")
    class sendDatastart extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(ContactusActivity.this, "", getString(R.string.sending_alert), true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Resultstart = getRequeststart(comment.getText().toString(),name.getText().toString(),phone.getText().toString(),email.getText().toString());
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
    private String getRequeststart(
            String comment,
            String name,
            String phone,
            String email
    ) {
        String resultstart;

        HttpClient client = new DefaultHttpClient();
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "addmessage");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("phone", phone));
            nameValuePairs.add(new BasicNameValuePair("email", email));
            nameValuePairs.add(new BasicNameValuePair("name", name));
            nameValuePairs.add(new BasicNameValuePair("comment", comment));
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
            finish();
            Toast.makeText(ContactusActivity.this,"با موفقیت ارسال شد", Toast.LENGTH_SHORT).show();
        } else if (HasilProses.trim().equalsIgnoreCase("400")) {
            Toast.makeText(ContactusActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ContactusActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

}
