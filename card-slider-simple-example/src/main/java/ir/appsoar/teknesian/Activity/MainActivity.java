package ir.appsoar.teknesian.Activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.ramotion.cardslider.CardSliderLayoutManager;
import com.ramotion.cardslider.CardSnapHelper;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Helper.HelperShamsi;
import ir.appsoar.teknesian.cards.SliderAdapter;
import ir.appsoar.teknesian.json.JsonUtils;
import ir.appsoar.teknesian.utils.DecodeBitmapTask;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.appsoar.teknesian.Helper.Config.Pic_Url;


public class MainActivity extends AppCompatActivity {
    public MainActivity() {
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private static String[] pic;
    private final int[] pics = {R.drawable.p15, R.drawable.p15, R.drawable.p15, R.drawable.p15, R.drawable.p15};
    private static String[] descriptions;
    private static String[] commenttext;
    private static String[] countries;
    private static String[] places;
    private static String[] temperatures;
    private static String[] reqid;
    private static String[] phone;
    private static String[] point;
    private static String[] times;

    private SliderAdapter sliderAdapter;

    private CardSliderLayoutManager layoutManger;
    private RecyclerView recyclerView;
    private TextSwitcher temperatureSwitcher;
    private TextSwitcher placeSwitcher;
    private TextSwitcher clockSwitcher;
    private TextSwitcher descriptionsSwitcher;
    private TextSwitcher comment;

    private TextView country1TextView;
    private TextView country2TextView;
    private int countryOffset1;
    private int countryOffset2;
    private long countryAnimDuration;
    private int currentPosition;
    /////
    public static String title;
    public static String name;


    public static String address;
    public static String saat;
    public static String date;
    /////////

    private DecodeBitmapTask decodeMapBitmapTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter intentFilter = new IntentFilter(
                "finishactivity");

        BroadcastReceiver mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent
                String status = intent.getStringExtra("status");
                //log our message value
                if (status != null) {
                    Log.i("InchooTutorial", status);
                    if (status.equals("0")) {
                        MainActivity.this.finish();
                    }

                }
            }
        };
        this.registerReceiver(mReceiver, intentFilter);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (JsonUtils.isNetworkAvailable(MainActivity.this)) {
            new MainActivity.sendData().execute();
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.failed_connect_network), Toast.LENGTH_SHORT).show();
        }
        Button start = findViewById(R.id.dialogButtonOK);

        start.setOnClickListener(view ->{
            start.setEnabled(false);
            pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
            pDialog.setTitleText("هشدار")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        new sendDatastart().execute();
                        pDialog.dismiss();
                    })
                    .setCancelClickListener(sweetAlertDialog -> pDialog.dismiss())
                    .setConfirmText("تایید")
                    .setCancelText("خیر")
                    .setContentText("آیا از شروع این خدمت مطمئن هستید؟").setCancelable(false);
            pDialog.show();
        });

    }

    private void initRecyclerViewtreaded() {
        runOnUiThread(() -> {
            recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setAdapter(sliderAdapter);
            recyclerView.setHasFixedSize(true);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        onActiveCardChange();
                    }
                }
            });

            layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

            new CardSnapHelper().attachToRecyclerView(recyclerView);
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing() && decodeMapBitmapTask != null) {
            decodeMapBitmapTask.cancel(true);
        }
    }

    private void initSwitchers() {
        runOnUiThread(() -> {
            temperatureSwitcher = findViewById(R.id.ts_temperature);
            temperatureSwitcher.setFactory(new TextViewFactory(R.style.TemperatureTextView, true));
            temperatureSwitcher.setCurrentText(temperatures[0]);

            placeSwitcher = findViewById(R.id.ts_place);
            placeSwitcher.setFactory(new TextViewFactory(R.style.PlaceTextView, false));
            placeSwitcher.setCurrentText(places[0]);

            clockSwitcher = findViewById(R.id.ts_clock);
            clockSwitcher.setFactory(new TextViewFactory(R.style.ClockTextView, false));
            clockSwitcher.setCurrentText(times[0]);

            descriptionsSwitcher = findViewById(R.id.ts_description);
            comment = findViewById(R.id.ts_comment);
            descriptionsSwitcher.setInAnimation(MainActivity.this, android.R.anim.fade_in);
            descriptionsSwitcher.setOutAnimation(MainActivity.this, android.R.anim.fade_out);
            descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
            descriptionsSwitcher.setCurrentText((descriptions[0]));
            comment.setInAnimation(MainActivity.this, android.R.anim.fade_in);
            comment.setOutAnimation(MainActivity.this, android.R.anim.fade_out);
            comment.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));
            comment.setCurrentText((commenttext[0]));


            TextView descriptionsTextView = (TextView) descriptionsSwitcher
                    .getCurrentView();
            descriptionsTextView.setTypeface(Typeface.createFromAsset(getAssets(), "vazir.ttf"));
            TextView commentTextView = (TextView) comment
                    .getCurrentView();
            commentTextView.setTypeface(Typeface.createFromAsset(getAssets(), "vazir.ttf"));
            TextView clockSwitcherTextView = (TextView) clockSwitcher.getCurrentView();
            clockSwitcherTextView.setTypeface(Typeface.createFromAsset(getAssets(), "vazir.ttf"));
            TextView temperatureTextView = (TextView) temperatureSwitcher.getCurrentView();
            temperatureTextView.setTypeface(Typeface.createFromAsset(getAssets(), "vazir.ttf"));
            TextView placeTextView = (TextView) placeSwitcher.getCurrentView();
            placeTextView.setTypeface(Typeface.createFromAsset(getAssets(), "vazir.ttf"));

        });
    }

    private void initCountryText() {
        runOnUiThread(() -> {
            countryAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
            countryOffset1 = getResources().getDimensionPixelSize(R.dimen.left_offset);
            countryOffset2 = getResources().getDimensionPixelSize(R.dimen.card_width);
            country1TextView = findViewById(R.id.tv_country_1);
            country2TextView = findViewById(R.id.tv_country_2);

            country1TextView.setX(countryOffset1);
            country2TextView.setX(countryOffset2);
            country1TextView.setText(countries[0]);
            country2TextView.setAlpha(0f);

            country1TextView.setTypeface(Typeface.createFromAsset(getAssets(), "vazir.ttf"));
            country2TextView.setTypeface(Typeface.createFromAsset(getAssets(), "vazir.ttf"));
        });
    }


    private void setCountryText(String text, boolean left2right) {
        final TextView invisibleText;
        final TextView visibleText;
        if (country1TextView.getAlpha() > country2TextView.getAlpha()) {
            visibleText = country1TextView;
            invisibleText = country2TextView;
        } else {
            visibleText = country2TextView;
            invisibleText = country1TextView;
        }

        final int vOffset;
        if (left2right) {
            invisibleText.setX(0);
            vOffset = countryOffset2;
        } else {
            invisibleText.setX(countryOffset2);
            vOffset = 0;
        }

        invisibleText.setText(text);

        final ObjectAnimator iAlpha = ObjectAnimator.ofFloat(invisibleText, "alpha", 1f);
        final ObjectAnimator vAlpha = ObjectAnimator.ofFloat(visibleText, "alpha", 0f);
        final ObjectAnimator iX = ObjectAnimator.ofFloat(invisibleText, "x", countryOffset1);
        final ObjectAnimator vX = ObjectAnimator.ofFloat(visibleText, "x", vOffset);

        final AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(iAlpha, vAlpha, iX, vX);
        animSet.setDuration(countryAnimDuration);
        animSet.start();
    }

    private void onActiveCardChange() {
        final int pos = layoutManger.getActiveCardPosition();
        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
            return;
        }
        onActiveCardChange(pos);
    }

    private void onActiveCardChange(int pos) {
        int animH[] = new int[]{R.anim.slide_in_right, R.anim.slide_out_left};
        int animV[] = new int[]{R.anim.slide_in_top, R.anim.slide_out_bottom};

        final boolean left2right = pos < currentPosition;
        if (left2right) {
            animH[0] = R.anim.slide_in_left;
            animH[1] = R.anim.slide_out_right;

            animV[0] = R.anim.slide_in_bottom;
            animV[1] = R.anim.slide_out_top;
        }
        setCountryText(countries[pos % countries.length], left2right);

        Button dial = findViewById(R.id.dialnumber);
        dial.setOnClickListener(view->{
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+ phone[pos % countries.length]));
            startActivity(callIntent);
        });
        temperatureSwitcher.setInAnimation(MainActivity.this, animH[0]);
        temperatureSwitcher.setOutAnimation(MainActivity.this, animH[1]);
        temperatureSwitcher.setText(temperatures[pos % temperatures.length]);
        placeSwitcher.setInAnimation(MainActivity.this, animV[0]);
        placeSwitcher.setOutAnimation(MainActivity.this, animV[1]);
        placeSwitcher.setText(places[pos % places.length]);

        TextView descriptionsTextView = (TextView) descriptionsSwitcher
                .getCurrentView();
        descriptionsTextView.setTypeface(Typeface.createFromAsset(getAssets(), "vazir.ttf"));
        TextView commentTextView = (TextView) comment
                .getCurrentView();
        commentTextView.setTypeface(Typeface.createFromAsset(getAssets(), "vazir.ttf"));
        TextView clockSwitcherTextView = (TextView) clockSwitcher.getCurrentView();
        clockSwitcherTextView.setTypeface(Typeface.createFromAsset(getAssets(), "vazir.ttf"));
        TextView temperatureTextView = (TextView) temperatureSwitcher.getCurrentView();
        temperatureTextView.setTypeface(Typeface.createFromAsset(getAssets(), "vazir.ttf"));
        TextView placeTextView = (TextView) placeSwitcher.getCurrentView();
        placeTextView.setTypeface(Typeface.createFromAsset(getAssets(), "vazir.ttf"));
        clockSwitcher.setInAnimation(MainActivity.this, animV[0]);
        clockSwitcher.setOutAnimation(MainActivity.this, animV[1]);
        clockSwitcher.setText(times[pos % times.length]);
        descriptionsSwitcher.setText((descriptions[pos % descriptions.length]));
        comment.setText((commenttext[pos % commenttext.length]));
        currentPosition = pos;
    }


    private class TextViewFactory implements ViewSwitcher.ViewFactory {

        @StyleRes
        final int styleId;
        final boolean center;

        TextViewFactory(@StyleRes int styleId, boolean center) {
            this.styleId = styleId;
            this.center = center;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View makeView() {
            final TextView textView = new TextView(MainActivity.this);

            if (center) {
                textView.setGravity(Gravity.CENTER);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                textView.setTextAppearance(MainActivity.this, styleId);
            } else {
                textView.setTextAppearance(styleId);
            }

            return textView;
        }

    }


    private class OnCardClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final CardSliderLayoutManager lm = (CardSliderLayoutManager) recyclerView.getLayoutManager();

            if (Objects.requireNonNull(lm).isSmoothScrolling()) {
                return;
            }

            final int activeCardPosition = lm.getActiveCardPosition();
            if (activeCardPosition == RecyclerView.NO_POSITION) {
                return;
            }

            final int clickedPosition = recyclerView.getChildAdapterPosition(view);
            if (clickedPosition == activeCardPosition) {
                final Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                intent.putExtra(MapsActivity.BUNDLE_IMAGE_ID, point[activeCardPosition % pics.length]);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(intent);
                } else {
                    final CardView cardView = (CardView) view;
                    final View sharedView = cardView.getChildAt(cardView.getChildCount() - 1);
                    final ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(MainActivity.this, sharedView, "shared");
                    startActivity(intent, options.toBundle());
                }
            } else if (clickedPosition > activeCardPosition) {
                recyclerView.smoothScrollToPosition(clickedPosition);
                onActiveCardChange(clickedPosition);
            }
        }


    }

    private String Result;

    @SuppressLint("StaticFieldLeak")
    class sendData extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.sending_alert), true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            Result = getRequest(prefs.getString(getString(R.string.id), ""), prefs.getString(getString(R.string.token), ""));
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            dialog.dismiss();
            resultAlert(Result);
        }
    }

    private static SharedPreferences prefs;

    private String request(HttpResponse response) {
        String result = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            StringBuilder str = new StringBuilder();
            while ((line = in.readLine()) != null) {
                str.append(line);
            }
            JSONObject responseobj = new JSONObject(str.toString());
            JSONArray jsonArray = responseobj.getJSONArray("list");
            try {
                JSONObject objJson;
                pic = new String[jsonArray.length()];
                countries = new String[jsonArray.length()];
                places = new String[jsonArray.length()];
                temperatures = new String[jsonArray.length()];
                phone = new String[jsonArray.length()];
                point = new String[jsonArray.length()];
                times = new String[jsonArray.length()];
                reqid = new String[jsonArray.length()];
                descriptions = new String[jsonArray.length()];
                commenttext = new String[jsonArray.length()];
                if (jsonArray.length() > 0)
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        if (objJson.has("address_pic"))
                            pic[i] = Pic_Url + objJson.getString("address_pic");
                        else
                            pic[i] = "";
                        if (objJson.has("kind")) {
                            if (objJson.getString("kind").equals("عقد قرارداد سرویس ماهیانه"))
                                countries[i] = "عقد قرارداد";
                            else
                                countries[i] = objJson.getString("kind");
                        } else
                            countries[i] = "درخواست نامشخص";
                        if (objJson.has("requester"))
                            places[i] = objJson.getString("requester");
                        else
                            places[i] = "ناشناس";
                        if (objJson.has("address"))
                            descriptions[i] = objJson.getString("address");
                        else
                            descriptions[i] = "مکان نامشخص";
                        if (objJson.has("comment"))
                            commenttext[i] = "توضیحات: "+objJson.getString("comment");
                        else
                            commenttext[i] = "بدون توضیح";
                        if (objJson.has("eshterakstatus"))
                            temperatures[i] = objJson.getString("eshterakstatus");
                        else
                            temperatures[i] = "غیرمشترک";
                        if (objJson.has("phone"))
                            phone[i] = objJson.getString("phone");
                        else
                            phone[i] = "0";
                        if (objJson.has("latlng"))
                            point[i] = objJson.getString("latlng");
                        else
                            point[i] = "36.299633268684566,59.57661580427589";
                        if (objJson.has("_id"))
                            reqid[i] = objJson.getString("_id");
                        else
                            reqid[i] = "0";
                        if (objJson.has("requestdate")) {
                            String georgiandate = objJson.getString("requestdate");
                            times[i] = HelperShamsi.shamsiFull(HelperShamsi.gregorianToShamsi(georgiandate.split("T")[0]));

                        } else
                            times[i] = "بدون تاریخ";
                    }
                else {
                    finish();
                }
                result = "200";
            } catch (JSONException e) {
                e.printStackTrace();
                finish();
            }
            if (responseobj.getString("message").equals("list")) {

                sliderAdapter = new SliderAdapter(MainActivity.this, pic, temperatures.length, new OnCardClickListener());
                initRecyclerViewtreaded();
                initCountryText();
                initSwitchers();
            } else {
                String Bazididstart = jsonArray.getJSONObject(0).getString("bazdidstart");
                Intent orderdetail = new Intent(MainActivity.this, OrderManageActivity.class);
                orderdetail.putExtra(MainActivity.this.getString(R.string.request_id), reqid[currentPosition % temperatures.length]);
                orderdetail.putExtra(MainActivity.this.getString(R.string.cat_name), countries[currentPosition % countries.length]);
                orderdetail.putExtra(MainActivity.this.getString(R.string.saat), times[currentPosition % times.length]);
                orderdetail.putExtra(MainActivity.this.getString(R.string.bazdidstart), Bazididstart);
                orderdetail.putExtra(MainActivity.this.getString(R.string.date), times[currentPosition % times.length]);
                orderdetail.putExtra(MainActivity.this.getString(R.string.address), descriptions[currentPosition % descriptions.length]);
                orderdetail.putExtra(MainActivity.this.getString(R.string.eshterakstatus), temperatures[currentPosition % descriptions.length]);
                orderdetail.putExtra(MainActivity.this.getString(R.string.address_pic), pic[currentPosition % descriptions.length]);
                orderdetail.putExtra(MainActivity.this.getString(R.string.point), point[currentPosition % temperatures.length]);
                orderdetail.putExtra(MainActivity.this.getString(R.string.phone), phone[currentPosition % places.length]);
                orderdetail.putExtra(MainActivity.this.getString(R.string.name), places[currentPosition % places.length]);
                orderdetail.putExtra(MainActivity.this.getString(R.string.reqstats), "2");
                startActivity(orderdetail);
                finish();
            }


        } catch (Exception ignored) {
        }
        return result;
    }

    private String getRequest(
            String id,
            String token
    ) {
        String result;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Config.ADMIN_PANEL_URL + "getservices");

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = client.execute(request);
            result = request(response);
        } catch (Exception ex) {
            result = "Unable to connect.";
        }
        return result;
    }

    private SweetAlertDialog pDialog;

    private void resultAlert(String HasilProses) {
        if (!HasilProses.trim().equalsIgnoreCase("200")) {

            pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE);
            pDialog.setTitleText("خطا")
                    .setConfirmClickListener(sweetAlertDialog -> {
                        pDialog.dismiss();
                        finish();
                    })
                    .setConfirmText("تایید")
                    .setContentText("سرویسی موجود نیست بعدا تلاش نمایید.").setCancelable(false);
            pDialog.show();

        }
    }

    ////////////////////
    private String Resultstart;

    @SuppressLint("StaticFieldLeak")
    class sendDatastart extends AsyncTask<Void, Void, Void> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(MainActivity.this, "", getString(R.string.sending_alert), true);
        }

        @Override
        protected Void doInBackground(Void... params) {
            String id = prefs.getString(getString(R.string.id), "");
            String token = prefs.getString(getString(R.string.token), "");
            Resultstart = getRequeststart(id, token, reqid[currentPosition]);
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
            result = json.getString("status");
            if (result.equals("success"))
                result = "200";
            else
                result = "400";

        } catch (Exception ex) {
            if (result == null)
                result = "Error";
        }
        return result;
    }

    private String getRequeststart(
            String id,
            String token,
            String reqid
    ) {
        String resultstart;

        HttpClient client = new DefaultHttpClient();
        HttpPost requeststart = new HttpPost(Config.ADMIN_PANEL_URL + "startservice");
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("requestid", reqid));
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
            Intent orderdetail = new Intent(MainActivity.this, OrderManageActivity.class);
            orderdetail.putExtra(MainActivity.this.getString(R.string.request_id), reqid[currentPosition % temperatures.length]);
            orderdetail.putExtra(MainActivity.this.getString(R.string.cat_name), countries[currentPosition % countries.length]);
            orderdetail.putExtra(MainActivity.this.getString(R.string.saat), times[currentPosition % times.length]);
            orderdetail.putExtra(MainActivity.this.getString(R.string.date), times[currentPosition % times.length]);
            orderdetail.putExtra(MainActivity.this.getString(R.string.address), descriptions[currentPosition % descriptions.length]);
            orderdetail.putExtra(MainActivity.this.getString(R.string.eshterakstatus), temperatures[currentPosition % descriptions.length]);
            orderdetail.putExtra(MainActivity.this.getString(R.string.address_pic), pic[currentPosition % descriptions.length]);
            orderdetail.putExtra(MainActivity.this.getString(R.string.point), point[currentPosition % temperatures.length]);
            orderdetail.putExtra(MainActivity.this.getString(R.string.phone), phone[currentPosition % places.length]);
            orderdetail.putExtra(MainActivity.this.getString(R.string.name), places[currentPosition % places.length]);
            orderdetail.putExtra(MainActivity.this.getString(R.string.reqstats), "2");
            startActivity(orderdetail);
            finish();
        } else if (HasilProses.trim().equalsIgnoreCase("400")) {
            Toast.makeText(MainActivity.this, "سرویسی موجود نیست", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(MainActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}