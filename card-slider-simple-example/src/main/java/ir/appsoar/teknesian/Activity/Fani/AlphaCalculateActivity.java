package ir.appsoar.teknesian.Activity.Fani;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import ir.appsoar.teknesian.R;

import ir.appsoar.teknesian.Fonts;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class AlphaCalculateActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText ds;
    private EditText dp;
    private EditText l;
    private EditText h;
    private Float dsf;
    private Float dpf;
    private Float lf;
    private Float hf;
    private TextView answer;
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
        setContentView(R.layout.activity_alpha_calculate);
        init();
        Fonts.getInstance().allowUserSaveScreenshot(true);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void init() {
        ds = findViewById(R.id.ds);
        ImageView detail = findViewById(R.id.detail);
        dp = findViewById(R.id.dp);
        l = findViewById(R.id.l);
        h = findViewById(R.id.h);
        answer=findViewById(R.id.answer);
        Button calculate = findViewById(R.id.dialogButtonOK);
        calculate.setOnClickListener(this);
        Glide.with(this).load(R.drawable.alphadegree).into(detail);
    }

    private void getinput() {
        dsf = Float.parseFloat(ds.getText().toString());
        dpf = Float.parseFloat(dp.getText().toString());
        lf = Float.parseFloat(l.getText().toString());
        hf = Float.parseFloat(h.getText().toString());
    }

    @Override
    public void onClick(View view) {
        try {
            getinput();
            Float p1=((dsf + dpf) / 2);
            Float p2=lf -  p1;
            Float test=(hf * hf +p2*p2);
            Double radical = Math.pow(test, (0.5));

            Double p=3*Math.PI/2;
            Double p31=Double.parseDouble(String.valueOf(dsf - dpf));
            Double p32=( 2 * radical);
            Double p3 =p31/p32;
            Double p4=hf / radical;
            Double alpha =p - Math.acos(p3) - Math.acos(p4);
            alpha=(alpha*57.295779513);
            if (alpha.toString().equals("NaN"))
                alpha=0.0;
            answer.setText(String.valueOf(Math.round(alpha))+" درجه");
        }catch (Exception ex){
            Log.e("error",ex.toString());
        }

    }
}
