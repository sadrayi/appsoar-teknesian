package ir.appsoar.teknesian.Dialoge;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import ir.appsoar.teknesian.R;


class DivarDeactiveDialogue extends Dialog implements View.OnClickListener {

    private String vaziat;

    public DivarDeactiveDialogue(Activity a, String vaziat) {
        super(a);
        // TODO Auto-generated constructor stub
        Activity c = a;
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
        if(vaziat.equals("active"))
        {
            payam.setText("آیا از فعال کردن این کالا مطمئن هستید ؟");
            pay.setText("فعال کردن");
        }
        if(vaziat.equals("deactive"))
        {
            payam.setText("آیا از غیرفعال کردن این کالا مطمئن هستید ؟");
            pay.setText("غیرفعال کردن");
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

                break;
        }
    }
}