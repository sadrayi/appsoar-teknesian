package ir.appsoar.teknesian.Activity.Fani;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ir.appsoar.teknesian.R;

import ir.appsoar.teknesian.Fonts;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class EngineCalculateActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText p;
    private EditText q;
    private EditText s1;
    private EditText s;
    private EditText v;
    private EditText i;
    private EditText hg;
    private EditText hm;
    private EditText hd;
    private Float pf;
    private Float qf;
    private Float s1f;
    private Float sf;
    private Float vf;
    private Float If;
    private Float hgf;
    private Float hmf;
    private Float hdf;
    Float zf;
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
        Fonts.getInstance().allowUserSaveScreenshot(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_engine_calculate);
        init();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void init() {
        p = findViewById(R.id.p);
        s1 = findViewById(R.id.s1);
        s = findViewById(R.id.s);
        q = findViewById(R.id.q);
        v = findViewById(R.id.v);
        i = findViewById(R.id.i);
        hg = findViewById(R.id.hg);
        hm = findViewById(R.id.hm);
        hd = findViewById(R.id.hd);
        answer=findViewById(R.id.answer);
        Button calculate = findViewById(R.id.dialogButtonOK);
        calculate.setOnClickListener(this);
    }

    private void getinput() {
        pf = Float.parseFloat(p.getText().toString());
        qf = Float.parseFloat(q.getText().toString());
        s1f = Float.parseFloat(s1.getText().toString());
        sf = Float.parseFloat(s.getText().toString());
        vf = Float.parseFloat(v.getText().toString());
        If = Float.parseFloat(i.getText().toString());
        hgf = Float.parseFloat(hg.getText().toString());
        hmf = Float.parseFloat(hm.getText().toString());
        hdf = Float.parseFloat(hd.getText().toString());
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        try {
            getinput();
            Float p1=pf+qf-(pf+(qf/2))-s1f;
            Float p2=(p1)/If;
            Float p3=p2+sf;
            Float p4=p3/hdf;
            Double gh = 9.81;
            Double p5=p4*vf* gh;
            Float p6=hgf*hmf;
            Double wf = p5 / p6;
            answer.setText(String.format("%.2f", wf /1000)+ " Kw");

        }catch (Exception ex){
            Log.e("error",ex.toString());
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
