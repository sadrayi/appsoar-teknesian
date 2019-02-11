package ir.appsoar.teknesian.Activity.Fani;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import ir.appsoar.teknesian.R;

import ir.appsoar.teknesian.Fonts;

public class CalculateActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView standard;
    private ImageView cabin;
    private ImageView alpha;
    private ImageView list;
    private ImageView engine;
    private ImageView well;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate);
        init();
        initOnclick();
        Fonts.getInstance().allowUserSaveScreenshot(true);
    }

    @SuppressLint("CheckResult")
    private void init() {
        RequestOptions requestOptions = new RequestOptions();
        well = findViewById(R.id.well);
        standard = findViewById(R.id.standard);
        cabin = findViewById(R.id.cabin);
        alpha = findViewById(R.id.alpha);
        list = findViewById(R.id.list);
        engine = findViewById(R.id.engine);
        Glide.with(this).load(getResources().getDrawable(R.drawable.bg_page)).apply(requestOptions).into((ImageView) findViewById(R.id.backlearn));

        Glide.with(this).load(getResources().getDrawable(R.drawable.well)).apply(requestOptions).into((well));
        Glide.with(this).load(getResources().getDrawable(R.drawable.cabin)).apply(requestOptions).into(((ImageView) findViewById(R.id.cabin)));

        Glide.with(this).load(getResources().getDrawable(R.drawable.alpha)).apply(requestOptions).into(alpha);

        Glide.with(this).load(getResources().getDrawable(R.drawable.engine)).apply(requestOptions).into((engine));

        Glide.with(this).load(getResources().getDrawable(R.drawable.list)).apply(requestOptions).into((list));
        Glide.with(this).load(getResources().getDrawable(R.drawable.standardreqnew)).apply(requestOptions).into((standard));


    }

    private void initOnclick() {
        standard.setOnClickListener(this);
        cabin.setOnClickListener(this);
        list.setOnClickListener(this);
        well.setOnClickListener(this);
        alpha.setOnClickListener(this);
        engine.setOnClickListener(this);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.alpha:
                startActivity(new Intent(this, AlphaCalculateActivity.class));
                break;
            case R.id.cabin:
                startActivity(new Intent(this, CabinCalculateActivity.class));
                break;
            case R.id.well:
                startActivity(new Intent(this, WellCalculateActivity.class));
                break;
            case R.id.engine:
                startActivity(new Intent(this, EngineCalculateActivity.class));
                break;
            case R.id.standard:
                startActivity(new Intent(this, StandardCalculateActivity.class));
                break;
            case R.id.list:
                startActivity(new Intent(this, ListActivity.class));
                break;
        }
    }
}
