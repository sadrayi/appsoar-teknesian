package ir.appsoar.teknesian.Activity.Fani;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import ir.appsoar.teknesian.R;

import ir.appsoar.teknesian.Fonts;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static ir.appsoar.teknesian.Helper.MohasebeA.ACalculate;

public class CabinCalculateActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText dc;
    private EditText wc;
    private EditText x;
    private EditText y1;
    private Float dcf;
    private Float wcf;
    private Float xf;
    private Float y1f;
    private Double zf;
    private Double af;
    private TextView answer;
    private Spinner z;
    private RadioButton yes;
    private RadioButton no;
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
        setContentView(R.layout.activity_cabin_calculate);
        init();
        Fonts.getInstance().allowUserSaveScreenshot(true);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void init() {
        dc = findViewById(R.id.dc);
        wc = findViewById(R.id.wc);
        x = findViewById(R.id.x);
        y1 = findViewById(R.id.y1);
        z = findViewById(R.id.z);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        answer=findViewById(R.id.answer);
        Button calculate = findViewById(R.id.dialogButtonOK);
        calculate.setOnClickListener(this);
    }

    private void getinput() {
        dcf = Float.parseFloat(dc.getText().toString());
        wcf = Float.parseFloat(wc.getText().toString());
        xf = Float.parseFloat(x.getText().toString());
        y1f = Float.parseFloat(y1.getText().toString());
    }
    private void getz(){
        if (z.getSelectedItemPosition()==4){
            zf=0.064;
        }
        else {
            if(y1f<70.0){
                switch (z.getSelectedItemPosition()){
                    case 0:
                        zf=0.014;
                        break;
                    case 1:
                        zf=0.018;
                        break;
                    case 2:
                        zf=0.0;
                        break;
                    case 3:
                        zf=0.014;
                        break;
                    case 4:
                        zf=0.064;
                        break;
                }
            }else if(y1f<80){
                switch (z.getSelectedItemPosition()){
                    case 0:
                        zf=0.016;
                        break;
                    case 1:
                        zf=0.021;
                        break;
                    case 2:
                        zf=0.0;
                        break;
                    case 3:
                        zf=0.016;
                        break;
                    case 4:
                        zf=0.064;
                        break;
                }
            }else if(y1f<90){
                switch (z.getSelectedItemPosition()){
                    case 0:
                        zf=0.018;
                        break;
                    case 1:
                        zf=0.024;
                        break;
                    case 2:
                        zf=0.0;
                        break;
                    case 3:
                        zf=0.018;
                        break;
                    case 4:
                        zf=0.064;
                        break;
                }
            }else if(y1f<100){
                switch (z.getSelectedItemPosition()){
                    case 0:
                        zf=0.02;
                        break;
                    case 1:
                        zf=0.0266;
                        break;
                    case 2:
                        zf=0.0;
                        break;
                    case 3:
                        zf=0.02;
                        break;
                    case 4:
                        zf=0.064;
                        break;
                }
            }else if(y1f<110){
                switch (z.getSelectedItemPosition()){
                    case 0:
                        zf=0.022;
                        break;
                    case 1:
                        zf=0.029;
                        break;
                    case 2:
                        zf=0.0;
                        break;
                    case 3:
                        zf=0.022;
                        break;
                    case 4:
                        zf=0.064;
                        break;
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        try {
            getinput();
            getz();
            if(no.isChecked()){
                af=((dcf*wcf)/10000)+((xf*y1f)/10000)+(zf);
            }else if(yes.isChecked()) {
                af=Double.parseDouble(String.valueOf((dcf*wcf)/10000));
            }
            answer.setText(String.valueOf(ACalculate(af)+" نفر"));

        }catch (Exception ex){
            Log.e("error",ex.toString());
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
