package ir.appsoar.teknesian.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ir.appsoar.teknesian.Activity.MapsActivity;
import ir.appsoar.teknesian.Activity.OrderManageActivity;
import ir.appsoar.teknesian.Helper.Config;

import static ir.appsoar.teknesian.Activity.OrderManageActivity.Bazididstart;
import static ir.appsoar.teknesian.Activity.OrderManageActivity.activity;
import static ir.appsoar.teknesian.Activity.OrderManageActivity.address_pic;


/**
 * Created by LapTop on 09/02/2018.
 */

public class OrderDetailFrag extends Fragment {

    private TextView saat;
    private static String token;
    private static String id;
    private long startTime = 0L;

    private Handler customHandler = new Handler();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_order_detail, parentViewGroup, false);
        ImageView map = rootView.findViewById(R.id.ts_map);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        token= prefs.getString(getString(R.string.token),"");
        id= prefs.getString(getString(R.string.id),"");
        String phonenumber = prefs.getString(getString(R.string.phone), "");
        OrderManageActivity.toolbar.setTitle(OrderManageActivity.title);
        OrderManageActivity.toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        TextView date1 = rootView.findViewById(R.id.textView67);
        saat= rootView.findViewById(R.id.textView66);
        TextView reqid = rootView.findViewById(R.id.textView61);
        TextView adress = rootView.findViewById(R.id.textView70);
        TextView name = rootView.findViewById(R.id.textView69);
        date1.setText(OrderManageActivity.eshterakstatus);
        saat.setText(OrderManageActivity.saat);

        adress.setText(OrderManageActivity.address);
        name.setText(OrderManageActivity.name);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.map_london);
        Glide.with(getActivity()).setDefaultRequestOptions(requestOptions).load(address_pic).into(map);
        reqid.setText(OrderManageActivity.reqid);
        if(OrderManageActivity.reqstatus.equals("2"))
        map.setOnClickListener(view -> {
            final Intent intent = new Intent(OrderManageActivity.activity, MapsActivity.class);
            intent.putExtra(MapsActivity.BUNDLE_IMAGE_ID,OrderManageActivity. point);
            startActivity(intent);
        });
        Button call = rootView.findViewById(R.id.imageView20);
        Button cancell = rootView.findViewById(R.id.imageView21);
        if(OrderManageActivity.reqstatus.equals("2"))
        call.setOnClickListener(view -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+ OrderManageActivity.phone));
            getContext().startActivity(callIntent);              });
        else {
            call.setVisibility(View.INVISIBLE);
        }

        if(OrderManageActivity.reqstatus.equals("2"))
        {

        }
        else {
        }
        if(OrderManageActivity.reqstatus.equals("2"))
        cancell.setOnClickListener(view -> {
            switch (OrderManageActivity.title) {
                case "درخواست تعمیر": {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_frame, new TamirFormFrag());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    break;
                }
                case "سرویس دوره ای": {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_frame, new ServiceFormFrag());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    break;
                }
                case "فروش آسانسور":
                    new sendData().execute();
                    break;
                default: {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_frame, new FactorFrag());
                    transaction.addToBackStack(null);
                    transaction.commit();
                    break;
                }
            }
        });
        else {
            cancell.setVisibility(View.INVISIBLE);

        }
        if(OrderManageActivity.Bazididstart!=null) {

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            try {
                Date date = df.parse(Bazididstart);
                startTime = date.getTime()+( 3600*1000*4)+( 1800*1000);

            } catch (ParseException e) {
                e.printStackTrace();
                startTime = new Date().getTime();
            }
        }
        else
        startTime =new Date().getTime();
        customHandler.postDelayed(updateTimerThread, 0);
        return rootView;
    }
    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            long timeInMilliseconds = new Date().getTime() - startTime;

            long timeSwapBuff = 0L;
            long updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            saat.setText("" + mins + ":"
                    + String.format("%02d", secs) + ":"
                    + String.format("%03d", milliseconds));
            customHandler.postDelayed(this, 0);
        }

    };

    //////////


    private static String Result;
    static String ResultFinish;

    static class sendData extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(OrderManageActivity.activity, "", OrderManageActivity.activity.getString(R.string.sending_alert), true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Result = getRequest(id,token);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            resultAlert(Result);
        }
    }
    public static String mojudi;
    public static JSONObject list;
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

        } catch (Exception ex) {
            if(result==null)
                result = "Error";
        }
        return result;
    }
    private static String getRequest(
            String mobile,
            String token
    ) {
        String result = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Config.ADMIN_PANEL_URL + "finishforush");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("requestid", OrderManageActivity.reqid));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = client.execute(request);
            result = request(response);
        } catch (Exception ex) {
            result = "Unable to connect.";
        }
        return result;
    }
    private static void resultAlert(String HasilProses) {
        if (HasilProses.trim().equalsIgnoreCase("success")) {
            activity.finish();
        }
        else {
            Toast.makeText(OrderManageActivity.activity, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
}
