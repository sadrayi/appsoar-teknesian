package ir.appsoar.teknesian.Dialoge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ir.appsoar.teknesian.R;


public class DivarVipDialogue extends Dialog implements View.OnClickListener {

    private Activity c;
    private String vaziat;
    private String pid;

    public DivarVipDialogue(Activity a, String vaziat,String pid) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.pid=pid;
        this.vaziat=vaziat;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_vip_divar);
        ImageView bck = findViewById(R.id.bck);
        TextView payam = findViewById(R.id.tv1);
        Button pay = findViewById(R.id.paybut);
        bck.setOnClickListener(this);
        pay.setOnClickListener(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(c);
        if(vaziat.equals("pay"))
        {
            payam.setText("شما با پرداخت ۱۵ هزار تومان می توانید آگهی خود را ویژه کنید تا در اولویت نمایش قرار گیرد .");
            pay.setText("پرداخت");
        }
        if(vaziat.equals("deactive"))
        {
            payam.setText("آگهی شما امکان ویژه شدن را ندارد.");
            pay.setText("بستن");
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bck:
                dismiss();
                break;
            case R.id.paybut:
                if(vaziat.equals("deactive"))
                    dismiss();
                else
                {
                    String url = "http://snapplift.com:3100/teknesian/divar/zarinpay?pid="+pid+"&id="+prefs.getString("id","0");
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    c.startActivity(i);
                    dismiss();
                }

                break;
        }
    }

    private static SharedPreferences prefs;
    ////////////

}