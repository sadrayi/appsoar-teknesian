package ir.appsoar.teknesian.Activity.divar.fragments;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import ir.appsoar.teknesian.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;

import ir.appsoar.teknesian.Activity.divar.DivarActivity;
import ir.appsoar.teknesian.Activity.divar.Models.ListViewModel;
import ir.appsoar.teknesian.Activity.divar.adapters.MyDivarListViewAdapter;
import ir.appsoar.teknesian.Helper.Config;
import ir.appsoar.teknesian.json.Data;

import static ir.appsoar.teknesian.Activity.divar.DivarActivity.detailsList;


public class MyDivarListViewFragment extends Fragment {

    private RecyclerView recyclerView;
    private View view;
    private TextView txtAlert;
    private String CategoryAPI;
    private SwipeRefreshLayout swipeRefreshLayout = null;
    private int IOConnect = 0;
    private ProgressBar prgLoading;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mydivar_listview, container, false);

        init();
        new getDataTask().execute();
        swipelistner();
        return view;
    }


    private void init() {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        recyclerView = view.findViewById(R.id.rcl_daneshjooyan);
        prgLoading = view.findViewById(R.id.prgLoading);
        txtAlert = view.findViewById(R.id.txtAlert);
            CategoryAPI = Config.ADMIN_PANEL_URL + "divar/getteknesianproduct?id="+DivarActivity.id+"&token='"+DivarActivity.token+"'";
    }

    private void set_data() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(mLayoutManager);
        MyDivarListViewAdapter adapter = new MyDivarListViewAdapter((DivarActivity) getActivity(), detailsList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void swipelistner() {
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            IOConnect = 0;
            detailsList.clear();
            new getDataTask().execute();
        }, 3000));

    }

    @SuppressLint("StaticFieldLeak")
    class getDataTask extends AsyncTask<Void, Void, Void> {

        getDataTask() {
            if (!prgLoading.isShown()) {
                prgLoading.setVisibility(View.VISIBLE);
                txtAlert.setVisibility(View.GONE);
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            parseJSONData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            prgLoading.setVisibility(View.GONE);

            if ((detailsList.size() > 0) && (IOConnect == 0)) {
                set_data();
            } else {
                txtAlert.setVisibility(View.VISIBLE);
            }
        }
    }

    private void parseJSONData() {

        detailsList.clear();

        try {

            HttpClient client = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 15000);
            HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
            HttpUriRequest request = new HttpGet(CategoryAPI);
            HttpResponse response = client.execute(request);
            InputStream atomInputStream = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(atomInputStream));

            String line;
            StringBuilder str = new StringBuilder();
            while ((line = in.readLine()) != null) {
                str.append(line);
            }
            detailsList = new ArrayList<>();
            JSONObject recieved = new JSONObject(str.toString());
            JSONArray data = recieved.getJSONArray("data");
            ListViewModel itemForushgah;
            for (int i = 0; i < data.length(); i++) {
                JSONObject category = data.getJSONObject(i);
                itemForushgah = new ListViewModel();
                itemForushgah.setId((category.getString("_id")));
                if (category.has("title")) {
                    itemForushgah.setTitle(category.getString("title"));

                } else {
                    itemForushgah.setTitle("");

                }
                if (category.has("ostan")) {
                    itemForushgah.setOstan(category.getString("ostan"));

                } else {
                    itemForushgah.setOstan("");

                }
                if (category.has("city")) {
                    itemForushgah.setCity(category.getString("city"));

                } else {
                    itemForushgah.setCity("");

                }
                if (category.has("detail")) {
                    itemForushgah.setDetail(category.getString("detail"));

                } else {
                    itemForushgah.setDetail("");

                }
                if (category.has("mobile")) {
                    itemForushgah.setPhone(category.getString("mobile"));

                } else {
                    itemForushgah.setPhone("");

                }
                if (category.has("price")) {
                    itemForushgah.setPrice(category.getString("price"));

                } else {
                    itemForushgah.setPrice("");

                }
                if (category.has("product_pic")) {
                    itemForushgah.setImage(category.getString("product_pic"));
                } else {
                    itemForushgah.setImage(" ");
                }
                if (category.has("created")) {
                    itemForushgah.setCreatedate(category.getString("created"));
                } else {
                    itemForushgah.setCreatedate((new Data().toString()));
                }
                if (category.has("status")) {
                    itemForushgah.setVaziat(category.getString("status"));
                } else {
                    itemForushgah.setVaziat("inactive");
                }
                if (category.has("kind")) {
                    itemForushgah.setKind(category.getString("kind"));
                } else {
                    itemForushgah.setKind("kind");
                }
                if (category.has("verify")) {
                    itemForushgah.setVerify(category.getString("verify"));
                } else {
                    itemForushgah.setVerify("false");
                }
                detailsList.add(itemForushgah);
            }

        } catch (MalformedURLException | JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            IOConnect = 1;
            e.printStackTrace();
        }
    }

}
