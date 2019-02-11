package ir.appsoar.teknesian.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import ir.appsoar.teknesian.R;

import ir.appsoar.teknesian.Activity.Fani.CalculateActivity;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class HamkaranActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hamkaran);
        @SuppressLint("CutPasteId") ImageView tamin= findViewById(R.id.taminimg);
        tamin.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        FragmentManager fm = getSupportFragmentManager();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        @SuppressLint("CutPasteId") ImageView[] imageViews={findViewById(R.id.backhead), findViewById(R.id.taminimg), findViewById(R.id.repairimg), findViewById(R.id.rules), findViewById(R.id.poshtibani), findViewById(R.id.learnimg), findViewById(R.id.calculate), findViewById(R.id.dastoorolamal), findViewById(R.id.contactusimg), findViewById(R.id.monagheseimg)};
        int[] drawables={R.drawable.hamkaranheader,R.drawable.exam,R.drawable.standard,R.drawable.rules,R.drawable.abzar,R.drawable.learn,R.drawable.news,R.drawable.dasturolamal,R.drawable.contactus,R.drawable.monaghese};
        imageViews[2].setOnClickListener(this);
        imageViews[3].setOnClickListener(this);
        imageViews[4].setOnClickListener(this);
        imageViews[8].setOnClickListener(this);
        imageViews[7].setOnClickListener(this);
        imageViews[5].setOnClickListener(this);
        imageViews[6].setOnClickListener(this);
        imageViews[9].setOnClickListener(this);
        int i=0;
        for (ImageView img:imageViews
                ) {
            Glide.with(this).load(drawables[i]).into(img);
            i++;

        }
    }
    @SuppressLint("StaticFieldLeak")
    public static SweetAlertDialog pDialog;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
                case R.id.taminimg :
                    Intent examintent=new Intent(HamkaranActivity.this,SafetyActivity.class);
                    examintent.putExtra("type1","آزمون ها");
                    examintent.putExtra("type","7");
                    startActivity(examintent);
                    break;
            case R.id.repairimg :
                Intent standardintent=new Intent(HamkaranActivity.this,SafetyActivity.class);
                standardintent.putExtra("type1","استاندارد");
                standardintent.putExtra("type","8");
                startActivity(standardintent);
                //startActivity(new Intent(HamkaranActivity.this,ForushgahActivity.class));
                //startActivity(new Intent(HamkaranActivity.this,FaniActivity.class));
                break;
            case R.id.rules :
                Intent ruleintent=new Intent(HamkaranActivity.this,SafetyActivity.class);
                ruleintent.putExtra("type1","قوانین");
                ruleintent.putExtra("type","0");
                startActivity(ruleintent);
                break;
            case R.id.poshtibani:
                /*Fragment azmoon = new CalculateFragment();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit_to_right, R.animator.enter, R.animator.exit_to_right);
                fragmentTransaction.add(R.id.mainframe, azmoon, "azmoon");
                fragmentTransaction.addToBackStack("azmoon");
                fragmentTransaction.commit();*/
                startActivity(new Intent(HamkaranActivity.this, CalculateActivity.class));
                break;
            case R.id.calculate :
                Intent newsintent=new Intent(HamkaranActivity.this,SafetyActivity.class);
                newsintent.putExtra("type1","اخبار");
                newsintent.putExtra("type","9");
                startActivity(newsintent);
                break;

            case R.id.contactusimg :
                Intent contactus=new Intent(HamkaranActivity.this,ContactusActivity.class);
                startActivity(contactus);
                break;
            case R.id.dastoorolamal :
                Intent dastur=new Intent(HamkaranActivity.this,SafetyActivity.class);
                dastur.putExtra("type","1");
                dastur.putExtra("type1","دستورالعمل");
                startActivity(dastur);
                break;
            case R.id.learnimg :
                Intent learn=new Intent(HamkaranActivity.this,SafetyActivity.class);
                learn.putExtra("type","2");
                learn.putExtra("type1","دوره آموزشی");
                startActivity(learn);
                break;
            case R.id.monagheseimg :
                Intent monaghese=new Intent(HamkaranActivity.this,SafetyActivity.class);
                monaghese.putExtra("type","5");
                monaghese.putExtra("type1","مناقصات");
                startActivity(monaghese);
                break;
            }

    }
}
