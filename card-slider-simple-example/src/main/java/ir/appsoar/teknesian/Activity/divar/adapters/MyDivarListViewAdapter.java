package ir.appsoar.teknesian.Activity.divar.adapters;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import ir.appsoar.teknesian.R;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ir.appsoar.teknesian.Activity.divar.DivarActivity;
import ir.appsoar.teknesian.Activity.divar.Models.ListViewModel;
import ir.appsoar.teknesian.Dialoge.DivarVipDialogue;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.Helper.HelperShamsi;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static ir.appsoar.teknesian.Helper.MoneyFormater.Mooneyformatter;


public class MyDivarListViewAdapter extends RecyclerView.Adapter<MyDivarListViewAdapter.ViewHolder> {
    private int lastPosition = -1;
    private ArrayList<ListViewModel> arrayList;
    private DivarActivity _ctx;

    public MyDivarListViewAdapter(DivarActivity ctx, ArrayList<ListViewModel> arrayList) {
        this.arrayList = arrayList;
        _ctx = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mydivar_listitem, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("CheckResult")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        DivarActivity.selected_agahi = position;
        holder.price.setText(Mooneyformatter(arrayList.get(position).getPrice()));
        holder.title.setText(arrayList.get(position).getTitle());
        holder.show.setOnClickListener(view -> _ctx.show_divar_detail(position));
        if (arrayList.get(position).getKind().equals("standard"))
            holder.kind.setText("آگهی معمولی");
        else if (arrayList.get(position).getKind().equals("vip"))
            holder.kind.setText("آگهی معمولی");
        setAnimation(holder.itemView, position);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -365);
        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date tomorrow = (calendar.getTime());
        Date date = new Date();
        try {
            date = df.parse(arrayList.get(position).getCreatedate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] created = arrayList.get(position).getCreatedate().split("-");
        holder.created.setText(HelperShamsi.gregorianToShamsiDate(arrayList.get(position).getCreatedate().split("T")[0]));

        if (arrayList.get(position).getVaziat().equals("active")) {
            holder.vaziattxt.setOnClickListener(view -> {
                SweetAlertDialog pDialog1 = new SweetAlertDialog(_ctx, SweetAlertDialog.WARNING_TYPE);
                pDialog1
                        .setTitleText("غیرفعال کردن")
                        .setContentText("آیا از غیر فعال کردن این آگهی مطمئن هستید ؟\n پس از غیر فعال کردن امکان فعالسازی وجود ندارد.")
                        .setConfirmClickListener(sweetAlertDialog -> {
                            DivarActivity.selectednews = arrayList.get(position).getId();
                            new sendData().execute();
                            pDialog1.dismiss();
                            holder.vaziattxt.setVisibility(View.GONE);
                        })
                        .setCancelClickListener(sweetAlertDialog ->
                                pDialog1.dismiss())
                        .setCancelText("لغو")
                        .setConfirmText("تایید").show();
            });
            DivarVipDialogue cc;
            if (arrayList.get(position).getKind().equals("standard")) {
                cc = new DivarVipDialogue(_ctx, "pay", arrayList.get(position).getId());
                holder.vip.setBackground(_ctx.getResources().getDrawable(R.drawable.curve_shape_btn));
            } else
                cc = new DivarVipDialogue(_ctx, "deactive", arrayList.get(position).getId());
            Objects.requireNonNull(cc.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            holder.vip.setOnClickListener(view -> cc.show());
            if (tomorrow.after(date)) {
                holder.kind.setText("آگهی منقضی");
                holder.vaziattxt.setVisibility(View.GONE);
            }
            if (arrayList.get(position).getVerify().equals("false")) {
                holder.kind.setText("تایید نشده");
            }else {
                if (arrayList.get(position).getKind().equals("vip")) {
                    holder.kind.setText("ویژه");
                }else {
                    holder.kind.setText("تایید شده");
                }
            }
        } else {
            holder.kind.setText("حذف شده");
            holder.vaziattxt.setVisibility(View.GONE);
            DivarVipDialogue cc = new DivarVipDialogue(_ctx, "deactive", arrayList.get(position).getId());
            Objects.requireNonNull(cc.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            holder.vip.setOnClickListener(view -> cc.show());
        }

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_loading);
        requestOptions.error(R.drawable.ic_loading);
        requestOptions.centerCrop();
        RequestOptions.bitmapTransform(new RoundedCornersTransformation(_ctx, 15, 2));
        Glide.with(_ctx)
                .setDefaultRequestOptions(requestOptions)
                .load(Config.Pic_Url + "divar_pic/" + arrayList.get(position).getImage())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, created, kind, vaziattxt;
        ImageView imageView;
        Button show, vip;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            vaziattxt = view.findViewById(R.id.vaziattxt);
            created = view.findViewById(R.id.date);
            kind = view.findViewById(R.id.kind);
            price = view.findViewById(R.id.price);
            show = view.findViewById(R.id.moshahede);
            vip = view.findViewById(R.id.promotion);
            imageView = view.findViewById(R.id.product_pic);

        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(_ctx.getBaseContext(), R.anim.item_animation_fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
    ///////////////////////////
    ///////////////////////////

    private SweetAlertDialog activdialuge;
    private String Result;

    @SuppressLint("StaticFieldLeak")
    class sendData extends AsyncTask<Void, Void, Void> {
        sendData() {
            activdialuge = new SweetAlertDialog(_ctx, SweetAlertDialog.PROGRESS_TYPE);
            activdialuge
                    .setTitleText("غیر فعالسازی")
                    .setContentText("درحال غیرفعالسازی آگهی ...")
                    .setCancelable(false);
            activdialuge.show();
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(Void... params) {

            Result = getRequest(DivarActivity.id, DivarActivity.token, DivarActivity.selectednews);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            resultAlert(Result);
        }
    }

    public static SharedPreferences prefs;

    private static String request(HttpResponse response) {
        String result = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line;
            StringBuilder str = new StringBuilder();
            while ((line = in.readLine()) != null) {
                str.append(line);
            }
            JSONObject json = new JSONObject(str.toString());
            result = json.getString("message");
            if (result.equals("success"))
                result = "200";
            else
                result = "400";

        } catch (Exception ex) {
            if (result == null)
                result = "Error";
        }
        return result;
    }

    private String getRequest(
            String id,
            String token,
            String reqid
    ) {
        String result;

        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(Config.ADMIN_PANEL_URL + "divar/deactive");
        try {
            List<NameValuePair> nameValuePairs;
            nameValuePairs = new ArrayList<>(6);
            nameValuePairs.add(new BasicNameValuePair("id", id));
            nameValuePairs.add(new BasicNameValuePair("token", token));
            nameValuePairs.add(new BasicNameValuePair("productid", reqid));
            request.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
            HttpResponse response = client.execute(request);
            result = request(response);
        } catch (Exception ex) {
            result = "Unable to connect.";
        }
        return result;
    }

    private void resultAlert(String HasilProses) {
        if (HasilProses.trim().equalsIgnoreCase("200")) {
            activdialuge.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
            activdialuge.setContentText("آگهی مد نظر غیرفعال شد.");
            activdialuge.showCancelButton(false);
            activdialuge.setConfirmText("تایید");
            activdialuge.setConfirmClickListener(sweetAlertDialog -> activdialuge.dismiss());
        } else {
            activdialuge.changeAlertType(SweetAlertDialog.WARNING_TYPE);
            activdialuge.setContentText("خطایی رخ داده است مجددا تلاش نمایید.");
            activdialuge.showCancelButton(false);
            activdialuge.setConfirmText("تایید");
            activdialuge.setConfirmClickListener(sweetAlertDialog -> activdialuge.dismiss());
        }
    }
}


