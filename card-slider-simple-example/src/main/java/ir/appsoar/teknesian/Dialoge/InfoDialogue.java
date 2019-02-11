package ir.appsoar.teknesian.Dialoge;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import ir.appsoar.teknesian.R;

import ir.appsoar.teknesian.Activity.OrderManageActivity;


/**
 * Created by LapTop on 24/09/2017.
 */

public class InfoDialogue extends Dialog implements
        View.OnClickListener {

    private String sherkatname;
    private String address;
    private String phone1;

    public InfoDialogue(Activity a, String sherkatname, String address,String phone) {
        super(a);
        // TODO Auto-generated constructor stub
        Activity c = a;
        this.sherkatname=sherkatname;
        this.address=address;
        this.phone1 = phone;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.info_dialog);
        Button yes = findViewById(R.id.taid);
        Button no = findViewById(R.id.cancel);
        yes.setText("تماس");
        no.setText("بستن");
        yes.setOnClickListener(this);
        no.setOnClickListener(this);
        TextView name = findViewById(R.id.txt_dia3);
        TextView addresstxt = findViewById(R.id.txt_dia2);
        name.setText("نام شرکت : "+sherkatname);
        addresstxt.setText("آدرس شرکت : "+address);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.taid:
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+ phone1));
                getContext().startActivity(callIntent);
                break;
            case R.id.cancel:
                dismiss();
                               break;
            default:
                break;
        }
        dismiss();
    }
}