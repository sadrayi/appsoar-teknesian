package ir.appsoar.teknesian.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
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

import ir.appsoar.teknesian.Adapter.FactorAdapter;
import ir.appsoar.teknesian.Helper.GlobalVariable;
import ir.appsoar.teknesian.Models.FactorDetailModel;
import ir.appsoar.teknesian.Models.OrderInfoModel;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.appsoar.teknesian.Helper.Config.ADMIN_PANEL_URL;
import static ir.appsoar.teknesian.Helper.MoneyFormater.Mooneyformatter;

public class FactorActivity extends AppCompatActivity {
    private TextView req_kind;
    private TextView req_status;
    private TextView req_address;
    private TextView teknesian_name;
    private TextView payment_status;
    private TextView factortotalcost;
    private TextView req_comment;
    private SharedPreferences prefs;
    private Button but6;
    private SweetAlertDialog pDialog1 ;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factor);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        req_kind= findViewById(R.id.khedmatkin);
        req_status= findViewById(R.id.req_status);
        req_address= findViewById(R.id.req_address);
        teknesian_name= findViewById(R.id.teknesian_name);
        factortotalcost= findViewById(R.id.factortotalcost);
        payment_status= findViewById(R.id.payment_status);
        req_comment= findViewById(R.id.req_comment);
        requestid= GlobalVariable.request_id;
        but6 = findViewById(R.id.button4);
        new sendDatastart().execute();
    }

    private String requestid;
    private String cost;

    private void requestPayment() {
        Long pay = Long.parseLong(cost);
        Log.d("majid", String.valueOf(pay));
        if (pay > 100) {

            pDialog1 = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
            pDialog1.setContentText("آیا مطمئنید می خواهید مبلغ" + pay + "را پرداخت کنید ؟ " );
            pDialog1.setTitleText("پرداخت");
            pDialog1.setConfirmText("بله").setCancelText("لغو").setCancelClickListener(sweetAlertDialog -> pDialog1.dismiss());
            pDialog1.setConfirmClickListener(sweetAlertDialog -> {
                String url = "http://snapplift.com:3100/teknesian/zarinpay?reqid="+requestid+"&userid="+prefs.getString("id","0")+"&kind=factorpay";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            });
            pDialog1.show();

        }


    }

    private String Resultstart;
    @SuppressLint("StaticFieldLeak")
    class sendDatastart extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(FactorActivity.this, "", getString(R.string.sending_alert), true);
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
    private static OrderInfoModel ordersDetail;
    private static List<FactorDetailModel> factorDetail;
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
                JSONObject jsonobj=json.getJSONObject("list");
                JSONObject factorobj1=null;
                if(json.has("factor"))
                factorobj1=json.getJSONObject("factor");
                JSONArray factorar=null;
                if(factorobj1!=null)
                if(factorobj1.has("items"))
                factorar=factorobj1.getJSONArray("items");
                ordersDetail=new OrderInfoModel();
                if(jsonobj.has("kind"))
                    ordersDetail.setCatName(jsonobj.getString("kind"));
                else
                    ordersDetail.setCatName("درخواست تعمیر");

                if(jsonobj.has("address"))
                    ordersDetail.setRequestAddressPosti(jsonobj.getString("address"));
                else
                    ordersDetail.setRequestAddressPosti("نامشخص");
                if(jsonobj.has("comment"))
                    ordersDetail.setRequestComment(jsonobj.getString("comment"));
                else
                    ordersDetail.setRequestComment("ندارد");
                if(jsonobj.has("status"))
                    ordersDetail.setRequestStatus(jsonobj.getString("status"));
                else
                    ordersDetail.setRequestStatus("");
                if(json.has("teknesian"))
                    ordersDetail.setTeknesianId(json.getString("teknesian"));
                else
                    ordersDetail.setTeknesianId("تخصیص نیافته");
                FactorDetailModel factorDetail=new FactorDetailModel();
                FactorActivity.factorDetail=new ArrayList<>();
                if(factorar!=null)
                for(int g=0;g<factorar.length();g++)
                {
                    JSONObject factorobj=factorar.getJSONObject(g);

                    if(factorobj.has("percost"))
                        factorDetail.setFactorItemsCost(factorobj.getString("percost"));
                    else
                        factorDetail.setFactorItemsCost("0");

                    if(factorobj.has("title"))
                        factorDetail.setFactorItemsName(factorobj.getString("title"));
                    else
                        factorDetail.setFactorItemsName("بدون عنوان");

                    if(factorobj.has("quantity"))
                        factorDetail.setFactorItemsCount(factorobj.getString("quantity"));
                    else
                        factorDetail.setFactorItemsCount("0");
                    if(factorobj1.has("_id"))
                        factorDetail.setFactorId(factorobj1.getString("_id"));
                    else
                        factorDetail.setFactorId("0");
                    if(factorobj1.has("factorsum"))
                        factorDetail.setFactorCost(factorobj1.getString("factorsum"));
                    else
                        factorDetail.setFactorCost("0");
                    if(factorobj1.has("status"))
                        factorDetail.setFactorStatus(factorobj1.getString("status"));
                    else
                        factorDetail.setFactorStatus("0");
                    FactorActivity.factorDetail.add(factorDetail);
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
        HttpPost requeststart = new HttpPost(ADMIN_PANEL_URL + "getrequestdetail");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("requestid", GlobalVariable.request_id));
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
            Toast.makeText(FactorActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(FactorActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
    private void initaddressview(){
        if(ordersDetail!=null){
            req_kind.setText(ordersDetail.getCatName());
            switch (ordersDetail.getRequestStatus()) {
                case "bazdid":
                    req_status.setText("در انتظار بازدید");
                    break;
                case "cancelled":
                    req_status.setText("کنسل شده");
                    break;
                case "factoradded":
                    req_status.setText("در انتظار پرداخت");
                    break;
                case "paid":
                    req_status.setText("پرداخت شده");
                    break;
                default:
                    req_status.setText("در انتظار تایید");
                    break;
            }
            req_address.setText(ordersDetail.getRequestAddressPosti());
            teknesian_name.setText(ordersDetail.getTeknesianId());
            req_comment.setText(ordersDetail.getRequestComment());
            LinearLayoutManager layoutManager = new LinearLayoutManager(FactorActivity.this);
            if (factorDetail.size()>0) {
                String factorid = factorDetail.get(0).getFactorId();
                String factor_status = factorDetail.get(0).getFactorStatus();
                if (factor_status.equals("paid"))
                    payment_status.setText("پرداخت شده");
                else
                    payment_status.setText("در انتظار پرداخت");
                cost = factorDetail.get(0).getFactorCost();
                factortotalcost.setText(Mooneyformatter(factorDetail.get(0).getFactorCost()));
                RecyclerView recyclerView = findViewById(R.id.factorrecycler);
                recyclerView.setLayoutManager(layoutManager);
                FactorAdapter recyclerViewAdapter = new FactorAdapter(factorDetail, FactorActivity.this);

                recyclerView.setAdapter(recyclerViewAdapter);
                if (ordersDetail.getRequestStatus().equals("paid"))
                    but6.setText("پرداخت شده");
                but6.setOnClickListener(view -> {
                    if (ordersDetail.getRequestStatus().equals("factoradded"))
                        requestPayment();
                });
            }
    }

}

}
