package ir.appsoar.teknesian.Fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ir.appsoar.teknesian.Activity.ActivityCart;
import ir.appsoar.teknesian.Activity.AddressActivity;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Helper.DBHelper;


public class Sample2Fragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    private Toolbar toolbar;
    private int index;

    public Sample2Fragment() { }

    public static Sample2Fragment newInstance(int i) {
        Sample2Fragment f = new Sample2Fragment();
        Bundle args = new Bundle();
        args.putInt("INDEX", i);
        f.setArguments(args);
        return f;
    }
    private DBHelper dbhelper;
    private SharedPreferences prefs;
    String comment;
    ActionMenuItemView add;
    private TextView addresstxt;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sample_2, container, false);
        setHasOptionsMenu(true);
        toolbar = view.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String kind = "فروش قطعه";
        String eshterakstatus = prefs.getString(getString(R.string.eshterakstatus), "غیرمشترک");
        addresstxt= view.findViewById(R.id.body);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) index = args.getInt("INDEX", 0);
        dbhelper = new DBHelper(getContext());

        try {
            dbhelper.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }

        ViewCompat.setElevation(getView(), 10f);
        ViewCompat.setElevation(toolbar, 4f);
        toolbar.inflateMenu(R.menu.fragment);
        toolbar.setTitle(AddressActivity.addresstitle[index]);
        toolbar.setNavigationOnClickListener(v -> getActivity().onBackPressed());
        addresstxt.setText(AddressActivity.address[index]);
    }
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_favorite:
                new sendDatastart().execute();
                return true;
        }
        return false;
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
            Resultstart = getRequeststart(id,token);
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
            String id,
            String token
    ) {
        String resultstart = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "regfactor");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("ostan", AddressActivity.ostan[index]));
            nameValuePairs.add(new BasicNameValuePair("city", AddressActivity.city[index]));
            nameValuePairs.add(new BasicNameValuePair("zone", AddressActivity.zone[index]));
            nameValuePairs.add(new BasicNameValuePair("address", AddressActivity.address[index]));
            nameValuePairs.add(new BasicNameValuePair("addressid", AddressActivity.addressid[index]));
            nameValuePairs.add(new BasicNameValuePair("address_picname", AddressActivity.address_picname[index]));
            nameValuePairs.add(new BasicNameValuePair("latlng", AddressActivity.latlng[index]));
            for(int r = 0; r<ActivityCart.productid.size(); r++)
            {
                nameValuePairs.add(new BasicNameValuePair("productid",ActivityCart.productid.get(r).toString()));
                nameValuePairs.add(new BasicNameValuePair("productname", ActivityCart.product_name.get(r)));
                nameValuePairs.add(new BasicNameValuePair("price", ActivityCart.Price.get(r)));
                nameValuePairs.add(new BasicNameValuePair("sherkatname",ActivityCart.sherkat_name.get(r)));
                nameValuePairs.add(new BasicNameValuePair("quantity",ActivityCart.Quantity.get(r).toString()));
                nameValuePairs.add(new BasicNameValuePair("sherkatid", ActivityCart.sherkat_id.get(r)));

            }
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
            dbhelper.deleteAllData();
            if(ActivityCart.activity!=null)
                ActivityCart.activity.finish();
            clearData();
            SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
            pDialog.setTitleText("تایید")
                    .setConfirmText("تایید")
                    .setConfirmClickListener(sweetAlertDialog -> getActivity().finish())
                    .setContentText("درخواست با موفقیت ثبت شد.").setCancelable(false);
            pDialog.show();
        } else if (HasilProses.trim().equalsIgnoreCase("400")) {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }
    private void clearData() {
        ActivityCart.productid.clear();
        ActivityCart.product_name.clear();
        ActivityCart.productid.clear();
        ActivityCart.sherkat_name.clear();
        ActivityCart.sherkat_id.clear();
        ActivityCart.Quantity.clear();
        ActivityCart.Sub_total_price.clear();
    }

}
