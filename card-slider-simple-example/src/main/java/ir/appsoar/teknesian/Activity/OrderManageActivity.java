package ir.appsoar.teknesian.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import ir.appsoar.teknesian.R;

import java.util.Objects;

import ir.appsoar.teknesian.Fragment.OrderDetailFrag;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class OrderManageActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static Toolbar toolbar;
    public static String title;
    public static String name;
    public static String phone;
    public static String address;
    public static String point;
    public static String saat;
    public static String address_pic;
    public static String Bazididstart;
    public static String eshterakstatus;
    public static String reqid;
    public static String reqstatus;
    @SuppressLint("StaticFieldLeak")
    public static Activity activity=null;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_manage);
        Bundle bundle=getIntent().getExtras();
        IntentFilter intentFilter = new IntentFilter(
                "locationmanager");

        BroadcastReceiver mReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                //extract our message from intent
                String status = intent.getStringExtra("status");
                //log our message value
                if (status != null) {
                    Log.i("InchooTutorial", status);
                    if (status.equals("0")) {
                        OrderManageActivity.this.finish();
                    }

                }
            }
        };
        this.registerReceiver(mReceiver, intentFilter);

        activity=this;
        assert bundle != null;
        title=bundle.getString(getString(R.string.cat_name));
        address=bundle.getString(getString(R.string.address));
        point=bundle.getString(getString(R.string.point));
        saat=bundle.getString(getString(R.string.saat));
        Bazididstart=bundle.getString(getString(R.string.bazdidstart));
        eshterakstatus=bundle.getString(getString(R.string.eshterakstatus));
        reqid=bundle.getString(getString(R.string.request_id));
        reqstatus=bundle.getString(getString(R.string.reqstats));
        phone=bundle.getString(getString(R.string.phone));
        name=bundle.getString(getString(R.string.name));
        address_pic=bundle.getString(getString(R.string.address_pic));
        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_frame,new OrderDetailFrag());
        transaction.commit();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onPause() {
        super.onPause();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
}
