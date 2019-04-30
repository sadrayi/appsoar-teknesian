package ir.appsoar.teknesian.Dialoge;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import ir.appsoar.teknesian.Fragment.FactorFrag;
import ir.appsoar.teknesian.Helper.Config;

import static ir.appsoar.teknesian.Activity.OrderManageActivity.reqid;


/**
 * Created by LapTop on 24/09/2017.
 */

public class FinishFactorDialog extends Dialog implements
        View.OnClickListener {

    private Activity c;
    private String id;
    private String token;
    private String itemid;
    private String latlng;

    public FinishFactorDialog(Activity a, String mobile, String token,String latlng) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.id=mobile;
        this.itemid=itemid;
        this.token=token;
        this.latlng=latlng;
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
        content.setText("آیا از اتمام خدمت شماره"+reqid+" مطمئنید ؟");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taid:
                break;
            case R.id.cancel:
                dismiss();
                               break;
            default:
                break;
        }
        dismiss();
    }

}