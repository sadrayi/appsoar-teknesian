package ir.appsoar.teknesian.Dialoge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import ir.appsoar.teknesian.R;


public class DivarContactDialogue extends Dialog implements View.OnClickListener {

    private String phone;

    public DivarContactDialogue(Activity a,String phone) {
        super(a);
        // TODO Auto-generated constructor stub
        Activity c = a;
        this.phone=phone;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_contact_divar);
        ImageView bck = findViewById(R.id.bck);
        Button call = findViewById(R.id.calldivar);
        Button sms = findViewById(R.id.smsdivar);
        bck.setOnClickListener(this);
        call.setOnClickListener(this);
        sms.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bck:
                dismiss();
                break;
            case R.id.calldivar:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+ phone));
                getContext().startActivity(callIntent);
                break;
            case R.id.smsdivar:Uri SMS_URI = Uri.parse("smsto:"+ phone); //Replace the phone number
                Intent sms = new Intent(Intent.ACTION_VIEW,SMS_URI);
                sms.putExtra("sms_body",""); //Replace the message witha a vairable
                getContext().startActivity(sms);
                break;
            default:
                break;
        }
    }
}