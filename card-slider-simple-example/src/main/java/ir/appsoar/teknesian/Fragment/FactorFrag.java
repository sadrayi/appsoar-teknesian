package ir.appsoar.teknesian.Fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ir.appsoar.teknesian.Activity.OrderManageActivity;
import ir.appsoar.teknesian.Adapter.AdapterFactor;
import ir.appsoar.teknesian.Dialoge.FinishFactorDialog;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Models.ItemFactor;

import static ir.appsoar.teknesian.Activity.OrderManageActivity.reqid;


/**
 * Created by LapTop on 09/02/2018.
 */

public class FactorFrag extends Fragment {
    private TextView factortxt;
    private TextView kalatxt;
    private static List<ItemFactor> arrayItemCategory;
    public static ArrayList<Map> factoritems = new ArrayList<>();
    public static String token;
    public static Bitmap sign;
    private static RecyclerView recyclerView;
    public static String id;
    public static String latlng;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        clearData();
        View rootView = inflater.inflate(R.layout.fragment_factor, parentViewGroup, false);
        Button factor = rootView.findViewById(R.id.imageView14);
        Button kala = rootView.findViewById(R.id.imageView);
        OrderManageActivity.toolbar.setTitle("فاکتور");
        if(OrderManageActivity.reqstatus.equals("2"))
        factor.setOnClickListener(view -> {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            AddFactorFrag map = new AddFactorFrag();
            final Bundle bundle = new Bundle();
            bundle.putString("kind","خدمات");
            bundle.putString("type","new");
            map.setArguments(bundle);
            transaction.replace(R.id.main_frame,map);
            transaction.addToBackStack(null);
            transaction.commit();

        });
        else {
            factor.setVisibility(View.INVISIBLE);
            factortxt.setVisibility(View.INVISIBLE);
        }
        if(OrderManageActivity.reqstatus.equals("2"))
        kala.setOnClickListener(view -> {
            latlng=prefs.getString(getString(R.string.lastlatlng),null);
            Log.i("***********prefs", latlng);
            if(latlng!=null) {
               SweetAlertDialog pDialog1 = new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE);
                pDialog1.setContentText("آیا مایل به اخذ امضا از مشتری هستید .");
                pDialog1.setTitleText("اتمام سرویس");
                pDialog1.setConfirmText("بله")
                        .setCancelText("خیر")
                        .setCancelClickListener(sweetAlertDialog -> new sendData().execute());
                pDialog1.setConfirmClickListener(sweetAlertDialog -> {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_frame,new SingitureFragment());
                    transaction.addToBackStack(FactorFrag.class.getName());
                    transaction.commit();
                    pDialog1.dismissWithAnimation();
                });
                if (! getActivity().isFinishing()) {

                    pDialog1.show();

                }/*
                FinishFactorDialog cdd = new FinishFactorDialog(getActivity(), FactorFrag.id, FactorFrag.token,prefs.getString(getString(R.string.lastlatlng),null));
                cdd.show();*/
            }
            else
            {
                final SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
                pDialog .setTitleText("خطا")
                        .setConfirmText("تایید")
                        .setConfirmClickListener(sweetAlertDialog -> pDialog.dismiss())
                        .setContentText("موقعیت در دسترس نیست.").setCancelable(false);
                pDialog.show();
            }
        });
        else {
            kala.setVisibility(View.INVISIBLE);
            kalatxt.setVisibility(View.INVISIBLE);
        }
        recyclerView = rootView.findViewById(R.id.recyclerfactor);

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        token=prefs.getString(getString(R.string.token),"");
        id=prefs.getString(getString(R.string.id),"");
        new sendData1().execute();

        return rootView;
    }

    private static String Result1;
    static String ResultFinish;
    private static int dpToPx() {
        Resources r = OrderManageActivity.activity.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics()));
    }
    static class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        GridSpacingItemDecoration(int spacing) {
            this.spanCount = 1;
            this.spacing = spacing;
            this.includeEdge = true;
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            int column = position % spanCount;

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount;
                outRect.right = (column + 1) * spacing / spanCount;

                if (position < spanCount) {
                    outRect.top = spacing;
                }
                outRect.bottom = spacing;
            } else {
                outRect.left = column * spacing / spanCount;
                outRect.right = spacing - (column + 1) * spacing / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacing;
                }
            }
        }
    }
    public static void clearData() {
        factoritems.clear();
    }
    public static class sendData1 extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(OrderManageActivity.activity, "", OrderManageActivity.activity.getString(R.string.sending_alert), true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Result1 = getRequest1(id,token);
            return null;
        }

        @Override
        protected void onPostExecute(Void result1) {
            dialog.dismiss();
            resultAlert1(Result1);
        }
    }
    private static SharedPreferences prefs;
    public static String mojudi;
    public static JSONObject list;
    private static String request1(HttpResponse response) {
        String result1 = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            StringBuilder str = new StringBuilder();
            while ((line = in.readLine()) != null) {
                str.append(line);
            }
            JSONObject json = new JSONObject(str.toString());
            result1=json.getString("status");
            JSONArray namearray1 = json.getJSONArray("items");
            for (int i = 0; i < namearray1.length(); i++) {
                JSONObject costarray1 = new JSONObject(namearray1.get(i).toString());
                String cost="0";
                String title="";
                String quantity="0";
                String comment="";
                String itemid="";

                if(costarray1.has("percost")) {
                    cost = costarray1.getString("percost");
                }
                if(costarray1.has("title")) {

                    title = costarray1.getString("title");
                }
                if(costarray1.has("id")) {

                    itemid = costarray1.getString("id");
                }
                if(costarray1.has("comment")) {

                    comment = costarray1.getString("comment");
                }
                if(costarray1.has("quantity")) {

                    quantity = costarray1.getString("quantity");
                }
                Map<String,String> factoritem = new HashMap<>();
                factoritem.put("title", title);
                factoritem.put("percost", cost);
                factoritem.put("quantity", quantity);
                factoritem.put("comment", comment);
                factoritem.put("id", itemid);
                factoritems.add(factoritem);
            }

        } catch (Exception ex) {
            if(result1==null)
                result1 = "Error";
        }
        return result1;
    }
    private static String getRequest1(
            String mobile,
            String token
    ) {
        String result1 = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost request1 = new HttpPost(Config.ADMIN_PANEL_URL + "getfactoritems");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("requestid", OrderManageActivity.reqid));
            //OrderManageActivity.reqid
            nameValuePairs.add(new BasicNameValuePair("token", token));
            request1.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response1 = client.execute(request1);
            result1 = request1(response1);
        } catch (Exception ex) {
            result1 = "Unable to connect.";
        }
        return result1;
    }
    private static void resultAlert1(String HasilProses) {
        if (HasilProses.trim().equalsIgnoreCase("success")) {
            AdapterFactor adapterCategory = new AdapterFactor(OrderManageActivity.activity);
            recyclerView.setAdapter(adapterCategory);

            recyclerView.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(OrderManageActivity.activity);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(dpToPx()));
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        else {
        }
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

            Result = getRequest(id,token, reqid);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            resultAlert(Result);
        }
    }

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
            String reqid
    ) {
        String result = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Config.ADMIN_PANEL_URL + "finishfactor");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("requestid", reqid));
            nameValuePairs.add(new BasicNameValuePair("location",latlng));
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
            FactorFrag.clearData();
            getActivity().finish();
        } else if (HasilProses.trim().equalsIgnoreCase("400")) {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
}