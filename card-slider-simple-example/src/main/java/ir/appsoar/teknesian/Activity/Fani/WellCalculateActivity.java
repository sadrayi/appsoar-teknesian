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

public class WellCalculateActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText ds;
    private EditText ws;
    EditText lift;
    private RadioButton posht;
    private RadioButton pahlu;
    private Double dc;
    private Double wc;
    private Double dsf;
    private Double wsf;
    private Double zf;
    private TextView answer;
    private Spinner doorkind;

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
        setContentView(R.layout.activity_well_calculate);
        init();
        Fonts.getInstance().allowUserSaveScreenshot(true);
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void init() {
        ds = findViewById(R.id.ds);
        ws = findViewById(R.id.ws);
        posht = findViewById(R.id.yes);
        pahlu = findViewById(R.id.pahlu);
        answer=findViewById(R.id.answer);
        doorkind=findViewById(R.id.doorkind);
        Button calculate = findViewById(R.id.dialogButtonOK);
        calculate.setOnClickListener(this);
    }
    private void getz(){
        switch (doorkind.getSelectedItemPosition()){
            case 0:
                zf=13.0;
                break;
            case 1:
                zf=17.0;
                break;
            case 2:
                zf=11.0;
                break;
            case 3:
                zf=14.0;
                break;
            case 4:
                zf=18.0;
                break;
        }
    }
    private void getinput() {
        dsf = Double.parseDouble(ds.getText().toString());
        wsf = Double.parseDouble(ws.getText().toString());
    }
    private void poshtcalc(){
        getinput();
        getz();
        dc= dsf-(33+zf);
        wc=wsf-40.0;
    }
    private void pahlucalc(){
        getinput();
        getz();
        dc= dsf-(14+zf);
        wc=wsf-70.0;
    }
    private int calculatea(){
        return ACalculate((dc*wc)/10000+0.05);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        try {
            if(posht.isChecked()){
                poshtcalc();
            }else if(pahlu.isChecked()){
                pahlucalc();
            }
            answer.setText(String.valueOf(calculatea())+" نفر");
        }catch (Exception ex){
            Log.e("error",ex.toString());
        }

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
