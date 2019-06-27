package ir.appsoar.teknesian.Fragment;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

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
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import co.ronash.pushe.Pushe;
import ir.appsoar.teknesian.Activity.FirstActivity;
import ir.appsoar.teknesian.Activity.LoginActivity;
import ir.appsoar.teknesian.Activity.RegisterActivity;
import ir.appsoar.teknesian.Activity.SplashActivity;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.R;

import ir.appsoar.teknesian.Activity.OrderManageActivity;

public class ServiceFormFrag extends Fragment {
    CheckBox[] checkBoxes=new CheckBox[30];
    Boolean withfan=true;
    EditText comment;
    private static SharedPreferences prefs;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.service_form, parentViewGroup, false);
        OrderManageActivity.toolbar.setTitle("فرم سرویس");
        for(int i=1;i<31;i++){
            int resID = getResources().getIdentifier("checkbox"+String.valueOf(i), "id", getActivity().getPackageName());
            checkBoxes[i-1]=rootView.findViewById(resID);
        }
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        RadioGroup radioGroup=rootView.findViewById(R.id.radiogroup);
        if(radioGroup.getCheckedRadioButtonId()==R.id.withoutfan){
            withfan=false;
        }
        comment=rootView.findViewById(R.id.comment);
        Button confirm= rootView.findViewById(R.id.dialogButtonOK);
        confirm.setOnClickListener(view -> {
            confirm.setEnabled(false);
            new sendDatastart().execute();
        });
        return rootView;
    }
    private String Resultstart;

    @SuppressLint("StaticFieldLeak")
    class sendDatastart extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            String id = prefs.getString(getString(R.string.id), "");
            String token = prefs.getString(getString(R.string.token), "");
            Resultstart = getRequeststart(id, token);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if(Resultstart.equals("404"))
                showerrorconnect();
            else
                resultAlertstart(Resultstart);
        }
    }

    private String requeststart(HttpResponse response) {
        String result = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            StringBuilder str = new StringBuilder();
            while ((line = in.readLine()) != null) {
                str.append(line);
            }
            JSONObject json = new JSONObject(str.toString());
            result = json.getString("message");
            if (result.equals("success")) {
                result = "200";

            } else if (result.equals("wrongtoken"))
                result = "400";
        } catch (Exception ex) {
            if (result == null)
                result = "Error";
        }
        return result;
    }

    private String getRequeststart(
            String phone,
            String token
    ) {
        String resultstart;

        HttpClient client = new DefaultHttpClient();
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "serviceformsave");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("id", phone));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("reqid", OrderManageActivity.reqid));
            nameValuePairs.add(new BasicNameValuePair("simboksel", String.valueOf(checkBoxes[0].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("tormoz", String.valueOf(checkBoxes[1].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("falakeharzgard", String.valueOf(checkBoxes[2].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("motor", String.valueOf(checkBoxes[3].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("kafshakcabin", String.valueOf(checkBoxes[4].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("corpicabin", String.valueOf(checkBoxes[5].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("tanzimtormoz", String.valueOf(checkBoxes[6].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("falakeharzgardsalem", String.valueOf(checkBoxes[7].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("motorsalem", String.valueOf(checkBoxes[8].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("kafshaksalem", String.valueOf(checkBoxes[9].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("fandarad", String.valueOf(checkBoxes[10].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("fansalem", withfan.toString()));
            nameValuePairs.add(new BasicNameValuePair("tanzifqataat", String.valueOf(checkBoxes[11].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("roghannezafat", String.valueOf(checkBoxes[12].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("roghandanrailcabin", String.valueOf(checkBoxes[13].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("roghandanrailvazne", String.valueOf(checkBoxes[14].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("platinqofl", String.valueOf(checkBoxes[15].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("sensormagent", String.valueOf(checkBoxes[16].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("griskarifalakeharzgard", String.valueOf(checkBoxes[17].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("terminaltabloachar", String.valueOf(checkBoxes[18].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("terminalcontactorachar", String.valueOf(checkBoxes[19].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("falakegoverner", String.valueOf(checkBoxes[20].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("sistemsout", String.valueOf(checkBoxes[21].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("namaieshgarshasi", String.valueOf(checkBoxes[22].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("namaieshgarshasicabin", String.valueOf(checkBoxes[23].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("roshanaicabin", String.valueOf(checkBoxes[24].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("cabintaraz", String.valueOf(checkBoxes[25].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("harkatcabin", String.valueOf(checkBoxes[26].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("fotuselsalem", String.valueOf(checkBoxes[27].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("fancabin", String.valueOf(checkBoxes[28].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("railcabinvazne", String.valueOf(checkBoxes[29].isChecked())));
            nameValuePairs.add(new BasicNameValuePair("comment", comment.getText().toString()));
            requeststart.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse responsestart = client.execute(requeststart);
            resultstart = requeststart(responsestart);
        } catch (Exception ex) {
            resultstart = "404";
        }
        return resultstart;
    }

    private void resultAlertstart(String HasilProses) {
        if (HasilProses.trim().equalsIgnoreCase("200")) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame,new FactorFrag());
            transaction.addToBackStack(null);
            transaction.commit();
        } else if (HasilProses.trim().equalsIgnoreCase("420")) {
            Toast.makeText(getContext(), "حساب کاربری شما غیر فعال می باشد.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        } else {
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        }
    }

    SweetAlertDialog pDialog1;
    public void showerrorconnect(){
        getActivity().runOnUiThread(() -> {
            pDialog1 = new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE);
            pDialog1.setContentText("در برقراری ارتباط با سرور مشکلی پیش آمده است لطفا اتصال اینترنت خود را چک نمایید.");
            pDialog1.setTitleText("عدم ارتباط");
            pDialog1.setConfirmText("سعی مجدد").setCancelText("خروج").setCancelClickListener(sweetAlertDialog -> getActivity().finish());
            pDialog1.setConfirmClickListener(sweetAlertDialog -> {new sendDatastart().execute();pDialog1.dismiss();});
            if (! getActivity().isFinishing()) {
                pDialog1.show();
            }
        });
    }
}