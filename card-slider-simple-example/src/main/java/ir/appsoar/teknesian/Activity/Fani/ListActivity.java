package ir.appsoar.teknesian.Activity.Fani;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import ir.appsoar.teknesian.R;

import ir.appsoar.teknesian.Fonts;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ListActivity extends AppCompatActivity {
    EditText tawaghofcount, floorheight, speed, overhead, deminsion;
    RadioButton yes, no;
    TextView gaverni, traveltime, taligh, travelcable, rail, railwazne;
    Button dialogButtonOK;
    Double tawaghofcountd, floorheightd, speedd, overheadd, deminsiond,traveltimeresponse,gaverniresponse, talighresponse, travelcableresponse, railresponse;

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
        setContentView(R.layout.activity_list);
        init();
        createview();
    }

    void init() {
        tawaghofcount = findViewById(R.id.tawaghofcount);
        floorheight = findViewById(R.id.floorheight);
        speed = findViewById(R.id.speed);
        overhead = findViewById(R.id.overhead);
        deminsion = findViewById(R.id.deminsion);
        yes = findViewById(R.id.yes);
        no = findViewById(R.id.no);
        gaverni = findViewById(R.id.gaverni);
        traveltime = findViewById(R.id.traveltime);
        taligh = findViewById(R.id.taligh);
        travelcable = findViewById(R.id.travelcable);
        rail = findViewById(R.id.rail);
        railwazne = findViewById(R.id.railwazne);
        dialogButtonOK = findViewById(R.id.dialogButtonOK);
    }

    void createview() {
        dialogButtonOK.setOnClickListener(view -> {
            if(!tawaghofcount.getText().toString().equals("")&&!floorheight.getText().toString().equals("")&&!speed.getText().toString().equals("")&&!overhead.getText().toString().equals("")&&!deminsion.getText().toString().equals("")){
                GetDataFromUi();
                traveltimecalculate();
                gavernicalculate();
                railcalculate();
                talighcalculate();
                travelcablecalculate();
            }else {
                SweetAlertDialog pDialog1 = new SweetAlertDialog(ListActivity.this, SweetAlertDialog.WARNING_TYPE);
                pDialog1
                        .setTitleText("خطا")
                        .setContentText("پر کردن تمام فیلد ها الزامی است.")
                        .setConfirmClickListener(Dialog::dismiss)
                        .setConfirmText("تایید").show();
            }

        });
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    void GetDataFromUi() {
        tawaghofcountd = Double.parseDouble(tawaghofcount.getText().toString())-1;
        floorheightd = Double.parseDouble(floorheight.getText().toString());
        speedd = Double.parseDouble(speed.getText().toString());
        overheadd = Double.parseDouble(overhead.getText().toString());
        deminsiond = Double.parseDouble(deminsion.getText().toString());
    }
    @SuppressLint("SetTextI18n")
    void traveltimecalculate(){
        Double x=tawaghofcountd*floorheightd;
        Double traveltime1=x/speedd;
        traveltimeresponse=traveltime1+10;
        traveltime.setText(String.valueOf(traveltimeresponse)+ " ثانیه");
    }
    @SuppressLint("SetTextI18n")
    void gavernicalculate(){
        Double x=tawaghofcountd*floorheightd;
        Double p1=x+overheadd;
        Double p2=p1+2.7;
        gaverniresponse=p2*2;
        gaverni.setText(String.valueOf(Math.round(gaverniresponse*100)/100)+ " متر");
    }
    @SuppressLint("SetTextI18n")
    void talighcalculate(){
        Double x=tawaghofcountd*floorheightd;
        Double p1=x+overheadd;
        talighresponse=p1+4;
        taligh.setText(String.valueOf(talighresponse)+ " متر");
    }
    @SuppressLint("SetTextI18n")
    void travelcablecalculate(){
        Double x=tawaghofcountd*floorheightd;
        Double p1=x+overheadd;
        Double p2=p1+10;
        if(yes.isChecked())
        {
            travelcableresponse=p2;
            travelcable.setText(String.valueOf(travelcableresponse)+ " متر");

        }else if(no.isChecked()){
            travelcableresponse=p2*2;
            travelcable.setText(String.valueOf(travelcableresponse)+ " متر");
        }
    }
    @SuppressLint("SetTextI18n")
    void railcalculate(){
        Double x=tawaghofcountd*floorheightd;
        Double p1=x+overheadd;
        Double p11=p1+deminsiond;
        Double p2=p11-0.1;
        Double p3=p2/5;
        try{
            String[] stringval=p3.toString().split("\\.");
        int A = Integer.parseInt(stringval[1].substring(0, 1));
        int r = Integer.parseInt(stringval[0]);
        if(A==0){
            rail.setText(String.valueOf(r*2));
            railwazne.setText(rail.getText().toString());
        }else if(A<6){
            rail.setText(String.valueOf((r*2)+1));
            railwazne.setText(rail.getText().toString());
        }else if(A<10){
            rail.setText(String.valueOf((r*2)+2));
            railwazne.setText(rail.getText().toString());
        }}
        catch (Exception ignored){

        }
    }
}

