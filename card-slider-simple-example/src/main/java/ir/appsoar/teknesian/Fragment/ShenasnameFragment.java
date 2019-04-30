package ir.appsoar.teknesian.Fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Helper.HelperShamsi;
import ir.appsoar.teknesian.R;

public class ShenasnameFragment extends Fragment {
    private static SharedPreferences prefs;

    public static String asansorkind;
    public static String stopcount;
    public static String bluk;
    public static String asansorzarfit;
    public static String asansorfloordoorkind;
    public static String asansorcabindoorkind;
    public static String gearboxengine;
    public static String karbariasansor;
    public static String tablofarman;
    public static String breaksystem;
    public static String salenasb;
    public static String standardvaziat;
    public static String standarddate;

    public TextView asansorkindtxt;
    public TextView stopcounttxt;
    public TextView bluktxt;
    public TextView asansorzarfittxt;
    public TextView asansorfloordoorkindtxt;
    public TextView asansorcabindoorkindtxt;
    public TextView gearboxenginetxt;
    public TextView karbariasansortxt;
    public TextView tablofarmantxt;
    public TextView breaksystemtxt;
    public TextView salenasbtxt;
    public TextView standardvaziattxt;
    public TextView standarddatetxt;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shenasname, parentViewGroup, false);
        OrderManageActivity.toolbar.setTitle("شناسنامه ساختمان");

        asansorkindtxt=rootView.findViewById(R.id.asansorkind);
        stopcounttxt=rootView.findViewById(R.id.tedadtavaghof);
        bluktxt=rootView.findViewById(R.id.blockname);
        asansorzarfittxt=rootView.findViewById(R.id.zarfiat);
        asansorfloordoorkindtxt=rootView.findViewById(R.id.doorkind);
        asansorcabindoorkindtxt=rootView.findViewById(R.id.cabindoorkind);
        gearboxenginetxt=rootView.findViewById(R.id.motor);
        karbariasansortxt=rootView.findViewById(R.id.asansorkarbari);
        tablofarmantxt=rootView.findViewById(R.id.tablofarman);
        breaksystemtxt=rootView.findViewById(R.id.tormozkind);
        salenasbtxt=rootView.findViewById(R.id.nasbyear);
        standardvaziattxt=rootView.findViewById(R.id.standardkind);
        standarddatetxt=rootView.findViewById(R.id.standardend);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        new sendDatastart().execute();
        Button confirm= rootView.findViewById(R.id.dialogButtonOK);
        confirm.setOnClickListener(view -> {
            if (getFragmentManager() != null) {
                getFragmentManager().popBackStack();
            }
        });
        return rootView;
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
            Resultstart = getRequeststart(id,token,OrderManageActivity.reqid);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            asansorkindtxt.setText(asansorkind);
            stopcounttxt.setText(stopcount);
            bluktxt.setText(bluk);
            asansorzarfittxt.setText(asansorzarfit);
            asansorfloordoorkindtxt.setText(asansorfloordoorkind);
            asansorcabindoorkindtxt.setText(asansorcabindoorkind);
            gearboxenginetxt.setText(gearboxengine);
            karbariasansortxt.setText(karbariasansor);
            tablofarmantxt.setText(tablofarman);
            breaksystemtxt.setText(breaksystem);
            salenasbtxt.setText(salenasb);
            standardvaziattxt.setText(standardvaziat);
            standarddatetxt.setText(HelperShamsi.gregorianToShamsiDate(standarddate));
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
            if(result.equals("success")){
                result= "200";
                if (json.has("asansorkind"))
                    asansorkind=json.getString("asansorkind");
                if (json.has("stopcount"))
                    stopcount=json.getString("stopcount");
                if (json.has("bluk"))
                    bluk=json.getString("bluk");
                if (json.has("asansorzarfit"))
                    asansorzarfit=json.getString("asansorzarfit");
                if (json.has("asansorfloordoorkind"))
                    asansorfloordoorkind=json.getString("asansorfloordoorkind");
                if (json.has("asansorcabindoorkind"))
                    asansorcabindoorkind=json.getString("asansorcabindoorkind");
                if (json.has("gearboxengine"))
                    gearboxengine=json.getString("gearboxengine");
                if (json.has("karbariasansor"))
                    karbariasansor=json.getString("karbariasansor");
                if (json.has("tablofarman"))
                    tablofarman=json.getString("tablofarman");
                if (json.has("breaksystem"))
                    breaksystem=json.getString("breaksystem");
                if (json.has("salenasb"))
                    salenasb=json.getString("salenasb");
                if (json.has("standardvaziat"))
                    standardvaziat=json.getString("standardvaziat");
                if (json.has("standarddate"))
                    standarddate=json.getString("standarddate");
            }
            else
                result= "400";

        } catch (Exception ex) {
            if(result==null)
                result = "Error";
        }
        return result;
    }
    private String getRequeststart(
            String id,
            String token,
            String reqid
    ) {
        String resultstart = "";

        HttpClient client = new DefaultHttpClient();
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "asansorshenasname");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("reqid", reqid));
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
        } else if (HasilProses.trim().equalsIgnoreCase("400")) {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

}