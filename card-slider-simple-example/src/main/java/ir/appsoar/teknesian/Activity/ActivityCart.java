package ir.appsoar.teknesian.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ir.appsoar.teknesian.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ir.appsoar.teknesian.Adapter.AdapterCart;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Helper.DBHelper;
import ir.appsoar.teknesian.Models.ItemCart;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.appsoar.teknesian.Helper.MoneyFormater.Mooneyformatter;

public class ActivityCart extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private RecyclerView recyclerView;
    private ProgressBar prgLoading;
    private TextView txtTotalLabel;
    private TextView txtTotal;
    private TextView txtAlert;
    private RelativeLayout lytOrder;
    private DBHelper dbhelper;
    private ir.appsoar.teknesian.Adapter.AdapterCart AdapterCart;
    private static double Tax;
    @SuppressLint("StaticFieldLeak")
    public static ActivityCart activity;
    public static ArrayList<Integer> productid = new ArrayList<>();
    public static ArrayList<String> sherkat_id = new ArrayList<>();
    public static ArrayList<String> sherkat_name = new ArrayList<>();
    public static ArrayList<String> product_name = new ArrayList<>();
    public static ArrayList<Integer> Quantity = new ArrayList<>();
    public static ArrayList<String> Price = new ArrayList<>();
    public static ArrayList<Integer> Sub_total_price = new ArrayList<>();
    private List<ItemCart> arrayItemCart;
    private int Total_price;
    private final int CLEAR_ONE_ORDER = 1;
    private int FLAG;
    private int ID;
    private String TaxCurrencyAPI;
    private int IOConnect = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Config.ENABLE_RTL_MODE) {
            setContentView(R.layout.activity_cart_rtl);
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        } else {
            setContentView(R.layout.activity_cart);
            Log.d("Log", "Working in Normal Mode, RTL Mode is Disabled");
        }
        activity=this;
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_cart);
        }

        prgLoading = findViewById(R.id.prgLoading);
        recyclerView = findViewById(R.id.recycler_view);
        txtTotalLabel = findViewById(R.id.txtTotalLabel);
        txtTotal = findViewById(R.id.txtTotal);
        txtAlert = findViewById(R.id.txtAlert);
        Button btn_reservation = findViewById(R.id.btn_reservation);
        btn_reservation.setOnClickListener(view -> {
            dbhelper.close();
            Intent i = new Intent(ActivityCart.this, AddressActivity.class);
            startActivity(i);
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(dpToPx()));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        lytOrder = findViewById(R.id.lytOrder);

        TaxCurrencyAPI = Config.ADMIN_PANEL_URL + "/api/get-tax-and-currency.php";

        AdapterCart = new AdapterCart();
        dbhelper = new DBHelper(this);

        dbhelper.openDataBase();

        new getTaxCurrency().execute();

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, final int position) {

                new Handler().postDelayed(() -> showClearDialog(CLEAR_ONE_ORDER, productid.get(position)), 400);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;

            case R.id.clear:
                int CLEAR_ALL_ORDER = 0;
                showClearDialog(CLEAR_ALL_ORDER, 1111);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showClearDialog(int flag, int id) {
        FLAG = flag;
        ID = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        switch (FLAG) {
            case 0:
                builder.setMessage(getString(R.string.clear_all_order));
                break;
            case 1:
                builder.setMessage(getString(R.string.clear_one_order));
                break;
        }
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            switch (FLAG) {
                case 0:
                    dbhelper.deleteAllData();
                    clearData();
                    new getDataTask().execute();
                    break;
                case 1:
                    dbhelper.deleteData(ID);
                    clearData();
                    new getDataTask().execute();
                    break;
            }
        });

        builder.setNegativeButton("No", (dialog, which) -> dialog.cancel());
        AlertDialog alert = builder.create();
        alert.show();

    }

    @SuppressLint("StaticFieldLeak")
    class getTaxCurrency extends AsyncTask<Void, Void, Void> {

        getTaxCurrency() {
            if (!prgLoading.isShown()) {
                prgLoading.setVisibility(View.VISIBLE);
                txtAlert.setVisibility(View.GONE);
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            parseJSONDataTax();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            prgLoading.setVisibility(View.GONE);
            if (IOConnect == 0) {
                new getDataTask().execute();
            } else {
                txtAlert.setVisibility(View.VISIBLE);
                txtAlert.setText(R.string.failed_connect_network);
            }

        }
    }

    private void parseJSONDataTax() {

        try {
            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet(TaxCurrencyAPI);
            HttpResponse response = client.execute(request);
            InputStream atomInputStream = response.getEntity().getContent();

            BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

            String line;
            StringBuilder str = new StringBuilder();
            while ((line = in.readLine()) != null) {
                str.append(line);
            }

            JSONObject json = new JSONObject(str.toString());
            JSONArray data = json.getJSONArray("data");

            JSONObject object_tax = data.getJSONObject(0);
            JSONObject tax = object_tax.getJSONObject("tax_n_currency");

            Tax = Double.parseDouble(tax.getString("Value"));

            JSONObject object_currency = data.getJSONObject(1);
            JSONObject currency = object_currency.getJSONObject("tax_n_currency");

            String currency1 = currency.getString("Value");


        } catch (MalformedURLException | JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            IOConnect = 1;
            e.printStackTrace();
        }
    }

    private void clearData() {
        productid.clear();
        product_name.clear();
        productid.clear();
        sherkat_name.clear();
        sherkat_id.clear();
        Quantity.clear();
        Sub_total_price.clear();
    }

    @SuppressLint("StaticFieldLeak")
    class getDataTask extends AsyncTask<Void, Void, Void> {

        getDataTask() {
            if (!prgLoading.isShown()) {
                prgLoading.setVisibility(View.VISIBLE);
                lytOrder.setVisibility(View.GONE);
                txtAlert.setVisibility(View.GONE);
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            getDataFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            txtTotal.setText(Mooneyformatter(String.valueOf(Total_price)));
            txtTotalLabel.setText(getString(R.string.total_order) );
            prgLoading.setVisibility(View.GONE);
            if (productid.size() > 0) {
                lytOrder.setVisibility(View.VISIBLE);
                recyclerView.setAdapter(AdapterCart);
            } else {
                txtAlert.setVisibility(View.VISIBLE);
            }
        }
    }

    private void getDataFromDatabase() {

        Total_price = 0;
        clearData();
        ArrayList<ArrayList<Object>> data = dbhelper.getAllData();

        for (int i = 0; i < data.size(); i++) {
            ArrayList<Object> row = data.get(i);

            productid.add(Integer.parseInt(row.get(0).toString()));
            product_name.add(row.get(1).toString());
            sherkat_id.add(row.get(4).toString());
            sherkat_name.add(row.get(5).toString());
            Quantity.add(Integer.parseInt(row.get(2).toString()));
            Price.add((row.get(3).toString()));

            NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
            DecimalFormat formatData = (DecimalFormat)numberFormat;
            formatData.applyPattern("#.##");
            Sub_total_price.add(Integer.parseInt(formatData.format(Integer.parseInt(row.get(3).toString()))));

            Total_price += Sub_total_price.get(i)*Quantity.get(i);
        }

        Total_price += (Total_price * (Tax / 100));

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat formatData = (DecimalFormat)numberFormat;
        formatData.applyPattern("#.##");
        Total_price = Integer.parseInt(formatData.format(Total_price));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dbhelper.close();
        finish();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

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

    private int dpToPx() {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, r.getDisplayMetrics()));
    }

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {

            this.clickListener = clickListener;

            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }

            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

}