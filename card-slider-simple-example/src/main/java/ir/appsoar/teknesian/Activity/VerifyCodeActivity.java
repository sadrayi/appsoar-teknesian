package ir.appsoar.teknesian.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import co.ronash.pushe.Pushe;
import ir.appsoar.teknesian.Helper.Config;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class VerifyCodeActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private int position=0;
    private TextView[] digits=new TextView[5];
    private View[] underline=new View[5];
    public static String token;
    private ToneGenerator tg;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);
        digits[0]= findViewById(R.id.textView57);
        digits[1]= findViewById(R.id.textView37);
        digits[2]= findViewById(R.id.textView39);
        digits[3]= findViewById(R.id.textView40);
        digits[4]= findViewById(R.id.textView41);
        underline[0]= findViewById(R.id.view7);
        underline[1]= findViewById(R.id.view);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        phonenumber=prefs.getString(getString(R.string.phone),"");
        TextView number= findViewById(R.id.textView56);
        number.setText(phonenumber);
        underline[2]= findViewById(R.id.view8);
        underline[3]= findViewById(R.id.view9);
        underline[4]= findViewById(R.id.view10);
        tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);

        TextView changenumber = findViewById(R.id.textView17);
        changenumber.setOnClickListener(view -> {
            startActivity(new Intent(VerifyCodeActivity.this,LoginActivity.class));
            finish();
        });
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
    }
    private String lastpressed="";
    @Override
    public void onClick(View view) {
        tg.startTone(ToneGenerator.TONE_PROP_BEEP);
        switch (view.getId()) {
            case R.id.textView32:
                if(position<5) {
                    underline[position].setBackgroundColor(Color.parseColor("#a9cb2d"));
                    digits[position].setText("0");
                    lastpressed+="0";
                    position++;
                }
                break;
            case R.id.textView18:
                if(position<5) {
                    underline[position].setBackgroundColor(Color.parseColor("#a9cb2d"));
                    digits[position].setText("1");
                    lastpressed+="1";
                    position++;
                }
                break;
            case R.id.textView23:
                if(position<5) {
                    underline[position].setBackgroundColor(Color.parseColor("#a9cb2d"));
                    digits[position].setText("2");
                    lastpressed+="2";
                    position++;
                }
                break;
            case R.id.textView24:
                if(position<5) {
                    underline[position].setBackgroundColor(Color.parseColor("#a9cb2d"));
                    digits[position].setText("3");
                    lastpressed+="3";
                    position++;
                }
                break;
            case R.id.textView30:
                if(position<5) {
                    underline[position].setBackgroundColor(Color.parseColor("#a9cb2d"));
                    digits[position].setText("4");
                    lastpressed+="4";
                    position++;
                }                break;
            case R.id.textView35:
                if(position<5) {
                    underline[position].setBackgroundColor(Color.parseColor("#a9cb2d"));
                    digits[position].setText("5");
                    lastpressed+="5";
                    position++;
                }                break;
            case R.id.textView28:
                if(position<5) {
                    underline[position].setBackgroundColor(Color.parseColor("#a9cb2d"));
                    digits[position].setText("6");
                    lastpressed+="6";
                    position++;
                }                break;
            case R.id.textView27:
                if(position<5) {
                    underline[position].setBackgroundColor(Color.parseColor("#a9cb2d"));
                    digits[position].setText("7");
                    lastpressed+="7";
                    position++;
                }                break;
            case R.id.textView26:
                if(position<5) {
                    underline[position].setBackgroundColor(Color.parseColor("#a9cb2d"));
                    digits[position].setText("8");
                    lastpressed+="8";
                    position++;
                }                break;
            case R.id.textView25:
                if(position<5) {
                    underline[position].setBackgroundColor(Color.parseColor("#a9cb2d"));
                    digits[position].setText("9");
                    lastpressed+="9";
                    position++;
                }                break;
            case R.id.textView33:
                if(position>0)
                {
                    position--;
                    digits[position].setText("X");
                    if(position!=0)
                    underline[position].setBackgroundColor(Color.parseColor("#d7deed"));
                    lastpressed = lastpressed.substring(0, lastpressed.length() - 1);
                }
                break;
            case R.id.textView34:
                if(position==5) {
                    new VerifyCodeActivity.sendData().execute();
                    Toast.makeText(VerifyCodeActivity.this, lastpressed, Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }
    private String regkind = "";
    private String Result;
    private String phonenumber;

    @SuppressLint("StaticFieldLeak")
    class sendData extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(VerifyCodeActivity.this, "", getString(R.string.sending_alert), true);
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
    public static String name = "";
    public static String mojudi = "";
    public static String id = "";

    private String request(HttpResponse response) {
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
            if(result.equals("success")) {
                result = "200";
                if(json.has("_id"))
                prefs.edit().putString(getString(R.string.id), json.getString("_id")).apply();
                if(json.has("sherkatno"))
                    prefs.edit().putString(getString(R.string.sherkatno), json.getString("sherkatno")).apply();
                if(json.has("token"))
                prefs.edit().putString(getString(R.string.token), json.getString("token")).apply();
                if(json.has("lastname"))
                prefs.edit().putString(getString(R.string.username), json.getString("lastname")).apply();
                if(json.has("onlinestatus"))
                prefs.edit().putString(getString(R.string.onlinestatus), json.getString("onlinestatus")).apply();
                if(json.has("profile_pic"))
                prefs.edit().putString(getString(R.string.profile_pic), json.getString("profile_pic")).apply();
                regkind = json.getString("regkind");

                if(json.has("status"))
                    if(json.getString("status").equals("inactive"))
                    {
                        result="420";
                    }
            }

        } catch (Exception ex) {
            if(result==null)
                result = "Error";
        }
        return result;
    }

    private String getRequest(
    ) {
        String result ;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Config.ADMIN_PANEL_URL + "verifycode");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("phone", phonenumber));
            nameValuePairs.add(new BasicNameValuePair("activecode", lastpressed));
            nameValuePairs.add(new BasicNameValuePair("deviceid", Pushe.getPusheId(VerifyCodeActivity.this)));
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
            if(regkind.equals("new"))
            {
                Intent i = new Intent(VerifyCodeActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();}
            else if(regkind.equals("old"))
            {
                Intent i = new Intent(VerifyCodeActivity.this, FirstActivity.class);
                startActivity(i);
                finish();
            }
        }
        else if (HasilProses.trim().equalsIgnoreCase("wrongcode")) {
            Toast.makeText(VerifyCodeActivity.this, R.string.wrongcode, Toast.LENGTH_SHORT).show();
        }

        else if (HasilProses.trim().equalsIgnoreCase("420")) {
            Toast.makeText(VerifyCodeActivity.this,"حساب کاربری شما غیر فعال می باشد.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(VerifyCodeActivity.this,LoginActivity.class));
            finish();
        }
        else {
            Toast.makeText(VerifyCodeActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

}
