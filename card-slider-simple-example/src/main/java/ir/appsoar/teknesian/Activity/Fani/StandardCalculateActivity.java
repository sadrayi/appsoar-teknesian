package ir.appsoar.teknesian.Activity.Fani;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import ir.appsoar.teknesian.Activity.ImageViewer;
import ir.appsoar.teknesian.Activity.SafetyDetailsActivity;
import ir.appsoar.teknesian.R;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Objects;

import ir.appsoar.teknesian.Fonts;
import ir.appsoar.teknesian.Helper.Config;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class StandardCalculateActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText mobile;
    private EditText phone;
    private EditText sherkatname;
    private EditText email;
    private Button sample;
    private Button pay;
    private String id;
    private String token;
    private ImageButton image;
    private File photo;
    private SharedPreferences prefs;
    @Override
    protected void onResume() {
        super.onResume();
        Fonts.getInstance().registerScreenshotObserver();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Fonts.getInstance().unregisterScreenshotObserver();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard_calculate);
        init();
        creatlayout();
        Fonts.getInstance().allowUserSaveScreenshot(true);
    }
    private void init(){
        mobile=findViewById(R.id.mobile);
        phone=findViewById(R.id.phone);
        sherkatname=findViewById(R.id.sherkatname);
        email=findViewById(R.id.email);
        sample=findViewById(R.id.sample);
        image=findViewById(R.id.image);
        pay=findViewById(R.id.pay);
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        id=prefs.getString(getString(R.string.id),"0");
        token=prefs.getString(getString(R.string.token),"0");
    }
    private void creatlayout(){
        mobile.setText(prefs.getString(getString(R.string.phone),"0"));
        mobile.setEnabled(false);
        image.setOnClickListener(view -> {

            PickSetup setup = new PickSetup()
                    .setTitle("انتخاب تصویر کاربری")
                    .setProgressText("درحال انتخاب")
                    .setCancelText("لغو")
                    .setFlip(true)
                    .setMaxSize(500)
                    .setCameraButtonText("گرفتن عکس")
                    .setGalleryButtonText("انتخاب از گالری")
                    .setButtonOrientation(LinearLayout.VERTICAL)
                    .setSystemDialog(false);
            PickImageDialog.build(setup)

                    .setOnPickResult(r -> {
                        Glide.with(this).load(r.getUri()).into(image);
                        photo = new File(r.getPath());
                    })
                    .setOnPickCancel(() -> {
                        //TODO: do what you have to if user clicked cancel
                    }).show(Objects.requireNonNull(this).getSupportFragmentManager());
        });
        sample.setOnClickListener(this);
        pay.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sample:
                Intent imageview=new Intent(StandardCalculateActivity.this,ImageViewer.class);
                imageview.putExtra("url",Config.Pic_Url + "sample.jpg");
                startActivity(imageview);
                break;
            case R.id.pay:
                if(isEmailValid(email.getText().toString())&&!sherkatname.getText().toString().equals("")&&!phone.getText().toString().equals("")&&photo!=null)
                {
                    SweetAlertDialog pdialog=new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE);
                    pdialog.setContentText("محاسبه استاندارد ۴۸ ساعت زمان خواهد برد و ۵۰.۰۰۰ تومان هزینه در بر خواهد داشت.\n آیا از پرداخت هزینه اطمینان دارید ؟")
                            .setConfirmText("تایید")
                            .setTitleText("ثبت اطلاعات")
                            .setConfirmClickListener(sweetAlertDialog -> new sendData().execute())
                            .setCancelText("خیر")
                            .setCancelClickListener(SweetAlertDialog-> pdialog.dismiss());
                    pdialog.show();
                }
                else {
                    SweetAlertDialog pdialog=new SweetAlertDialog(this,SweetAlertDialog.WARNING_TYPE);
                    pdialog.setContentText("تمام فیلد ها را تکمیل نمایید.")
                            .setConfirmText("تایید")
                            .setTitleText("خطا")
                            .setConfirmClickListener(sweetAlertDialog -> pdialog.dismiss());
                    pdialog.show();
                }
                break;
        }
    }
    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    private SweetAlertDialog activdialuge;
    private String Result;
/////////
    @SuppressLint("StaticFieldLeak")
class sendData extends AsyncTask<Void, Void, Void> {
        sendData() {
            activdialuge = new SweetAlertDialog(Objects.requireNonNull(StandardCalculateActivity.this), SweetAlertDialog.PROGRESS_TYPE);
            activdialuge
                    .setTitleText("ثبت درخواست")
                    .setContentText("درحال ارسال اطلاعات ...")
                    .setCancelable(false);
            activdialuge.show();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            Result = getRequest();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            resultAlert(Result);
        }
    }
    private String reqid = "";
    private String request(HttpResponse response) {
        String result = "";


        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            StringBuilder str = new StringBuilder();
            while ((line = in.readLine()) != null) {
                str.append(line);
            }
            JSONObject json1 = new JSONObject(str.toString());
            result = json1.getString("message");
            if(result.equals("success")){
                reqid=json1.getString("id");
            }

        } catch (Exception ignored) {
        }
        return result;
    }

    private String getRequest(
    ) {
        String result;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Config.ADMIN_PANEL_URL + "standardreq/addreq");

        try {
            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart("id", new StringBody(id, Charset.forName("UTF-8")));
            multipartEntity.addPart("token", new StringBody(token, Charset.forName("UTF-8")));
            multipartEntity.addPart("sherkatname", new StringBody(sherkatname.getText().toString(), Charset.forName("UTF-8")));
            multipartEntity.addPart("telephone", new StringBody(phone.getText().toString(), Charset.forName("UTF-8")));
            multipartEntity.addPart("email", new StringBody(email.getText().toString(), Charset.forName("UTF-8")));
            if (photo != null)
                multipartEntity.addPart("image", new FileBody(photo));
            request.setEntity(multipartEntity);
            HttpResponse response = client.execute(request);
            result = request(response);
        } catch (Exception ex) {
            result = "Unable to connect.";
        }
        return result;
    }

    private void resultAlert(String HasilProses) {
        if (HasilProses.trim().equalsIgnoreCase("success")) {
            activdialuge.dismiss();
            String url = "http://snapplift.com:3100/teknesian/standardreq/zarinpay?pid="+reqid;
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } else {
            activdialuge.changeAlertType(SweetAlertDialog.WARNING_TYPE);
            activdialuge.setContentText("خطایی رخ داده است مجددا تلاش نمایید.");
            activdialuge.showCancelButton(false);
            activdialuge.setConfirmText("تایید");
            activdialuge.setConfirmClickListener(sweetAlertDialog -> activdialuge.dismiss());
        }
    }


}
