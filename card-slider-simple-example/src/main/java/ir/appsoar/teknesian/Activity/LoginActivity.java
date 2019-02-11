package ir.appsoar.teknesian.Activity;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

import at.markushi.ui.CircleButton;
import ir.appsoar.teknesian.Helper.Config;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


@SuppressWarnings("StringConcatenationInLoop")
public class
LoginActivity extends AppCompatActivity implements View.OnClickListener {
        @Override
        protected void attachBaseContext(Context newBase) {
            super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
        }

    private ToneGenerator tg;
    private TextView number;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
            number= findViewById(R.id.textView57);
            CircleButton butt0 = findViewById(R.id.textView32);
            CircleButton butt1 = findViewById(R.id.textView18);
            CircleButton butt2 = findViewById(R.id.textView23);
            CircleButton butt3 = findViewById(R.id.textView24);
            CircleButton butt4 = findViewById(R.id.textView30);
            CircleButton butt5 = findViewById(R.id.textView35);
            CircleButton butt6 = findViewById(R.id.textView28);
            CircleButton butt7 = findViewById(R.id.textView27);
            CircleButton butt8 = findViewById(R.id.textView26);
            CircleButton butt9 = findViewById(R.id.textView25);
            CircleButton backspace = findViewById(R.id.textView33);
            CircleButton done = findViewById(R.id.textView34);
            butt0.setOnClickListener(this);
            butt1.setOnClickListener(this);
            butt2.setOnClickListener(this);
            butt3.setOnClickListener(this);
            butt4.setOnClickListener(this);
            butt5.setOnClickListener(this);
            butt6.setOnClickListener(this);
            butt7.setOnClickListener(this);
            butt8.setOnClickListener(this);
            butt9.setOnClickListener(this);
            backspace.setOnClickListener(this);
            done.setOnClickListener(this);
            prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        }

        private String lastpressed="";
        @Override
        public void onClick(View view) {
            tg.startTone(ToneGenerator.TONE_PROP_BEEP);
            switch (view.getId()) {
                case R.id.textView32:
                    lastpressed="0";
                    break;
                case R.id.textView18:
                    lastpressed="1";
                    break;
                case R.id.textView23:
                    lastpressed="2";
                    break;
                case R.id.textView24:
                    lastpressed="3";
                    break;
                case R.id.textView30:
                    lastpressed="4";
                    break;
                case R.id.textView35:
                    lastpressed="5";
                    break;
                case R.id.textView28:
                    lastpressed="6";
                    break;
                case R.id.textView27:
                    lastpressed="7";
                    break;
                case R.id.textView26:
                    lastpressed="8";
                    break;
                case R.id.textView25:
                    lastpressed="9";
                    break;
                case R.id.textView33:
                    lastpressed="-1";
                    break;
                case R.id.textView34:
                    lastpressed="done";
                    break;
            }
            if(!lastpressed.equals("done"))
            {
                String[] phonenumberarray;
                if(!lastpressed.equals("-1"))
                {
                    phonenumberarray =number.getText().toString().split("X");
                    if(phonenumberarray[0].length()<11)
                    {
                        phonenumber= phonenumberarray[0]+lastpressed;
                        int lenght=phonenumber.length();
                        for(int i=0;i<11-(lenght);i++)
                            phonenumber+="X";
                        number.setText(phonenumber);
                    }
                }
                else
                {
                    phonenumberarray =number.getText().toString().split("X");
                    if(phonenumberarray.length>0&& phonenumberarray[0].length()>2)
                    {
                        phonenumber= phonenumberarray[0].substring(0, phonenumberarray[0].length() - 1);
                        int lenght=phonenumber.length();
                        for(int i=0;i<11-(lenght);i++)
                            phonenumber+="X";
                        number.setText(phonenumber);
                    }
                }
            }
            else
            {
                if(!number.getText().toString().contains("X"))
                {
                    new LoginActivity.sendData().execute();
                }
            }
        }
        private String Result;
        private String phonenumber;

    @SuppressLint("StaticFieldLeak")
    class sendData extends AsyncTask<Void, Void, Void> {

            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                dialog = ProgressDialog.show(LoginActivity.this, "", getString(R.string.sending_alert), true);
            }

            @Override
            protected Void doInBackground(Void... params) {

                Result = getRequest();
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
                result=json.getString("message");


                switch (result) {
                    case "success":
                        result = "200";
                        break;
                    case "notfound":
                        result = "300";
                        break;
                    default:
                        result = "400";
                        break;
                }


            } catch (Exception ignored) {
            }
            return result;
        }
        private String getRequest(
        ) {
            String result;

            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost(Config.ADMIN_PANEL_URL + "getnumber");

            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                nameValuePairs.add(new BasicNameValuePair("phone", phonenumber));
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
                Intent i = new Intent(LoginActivity.this, VerifyCodeActivity.class);
                prefs.edit().putString(getString(R.string.phone),phonenumber).apply();
                startActivity(i);
                finish();
            } else if (HasilProses.trim().equalsIgnoreCase("300")) {
                Toast.makeText(LoginActivity.this, R.string.wrongnumber, Toast.LENGTH_SHORT).show();
            }else if (HasilProses.trim().equalsIgnoreCase("400")) {
                Toast.makeText(LoginActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
            }
        }
    }
