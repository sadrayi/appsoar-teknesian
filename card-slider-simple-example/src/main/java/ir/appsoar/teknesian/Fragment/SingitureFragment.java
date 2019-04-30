package ir.appsoar.teknesian.Fragment;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import ir.appsoar.teknesian.Activity.OrderManageActivity;
import ir.appsoar.teknesian.Adapter.BuildingHistoryAdapter;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Helper.HelperShamsi;
import ir.appsoar.teknesian.Models.BuildingHistoryModel;
import ir.appsoar.teknesian.R;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static ir.appsoar.teknesian.Activity.OrderManageActivity.reqid;
import static ir.appsoar.teknesian.Fragment.FactorFrag.id;
import static ir.appsoar.teknesian.Fragment.FactorFrag.latlng;
import static ir.appsoar.teknesian.Fragment.FactorFrag.sign;
import static ir.appsoar.teknesian.Fragment.FactorFrag.token;

public class SingitureFragment extends Fragment {
    private static SharedPreferences prefs;
    private static List<BuildingHistoryModel> catname;
    RecyclerView recyclerView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signiture, parentViewGroup, false);
        OrderManageActivity.toolbar.setTitle("امضا مشتری");
        prefs=PreferenceManager.getDefaultSharedPreferences(getContext());
        SignaturePad mSignaturePad =  rootView.findViewById(R.id.signature_pad);
        Button save=rootView.findViewById(R.id.dialogButtonOK);
        save.setOnClickListener(view->{
            FactorFrag.sign=mSignaturePad.getSignatureBitmap();
            new sendData().execute();
        });
        return rootView;
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
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            sign.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
            try {
                FileOutputStream fo = new FileOutputStream(file);
                fo.write(bytes.toByteArray());
                fo.flush();
                fo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart("id", new StringBody(id, Charset.forName("UTF-8")));
            multipartEntity.addPart("token", new StringBody(token, Charset.forName("UTF-8")));
            multipartEntity.addPart("requestid", new StringBody(reqid, Charset.forName("UTF-8")));
            multipartEntity.addPart("location", new StringBody(latlng, Charset.forName("UTF-8")));
            multipartEntity.addPart("sign", new FileBody(file));
            request.setEntity(multipartEntity);
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