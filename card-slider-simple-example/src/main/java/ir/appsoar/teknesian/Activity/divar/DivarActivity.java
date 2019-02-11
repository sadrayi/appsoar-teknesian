package ir.appsoar.teknesian.Activity.divar;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import ir.appsoar.teknesian.R;

import java.util.ArrayList;

import ir.appsoar.teknesian.Activity.divar.Models.ListViewModel;
import ir.appsoar.teknesian.Activity.divar.fragments.AddDivarFragment;
import ir.appsoar.teknesian.Activity.divar.fragments.ForushgahFragment;
import ir.appsoar.teknesian.Activity.divar.fragments.ListViewFragment;
import ir.appsoar.teknesian.Activity.divar.fragments.MyDivarListViewFragment;
import ir.appsoar.teknesian.Activity.divar.fragments.divarDetailFragment;
import ir.appsoar.teknesian.Activity.divar.fragments.divarFilterFragment;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DivarActivity extends AppCompatActivity {
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));

    }

    public static int selected_agahi=0;
    public static String selectednews,id,token;
    public static ArrayList<ListViewModel> detailsList=new ArrayList<>();
    private TextView mTextMessage;
    private FragmentManager fm;
    private Fragment listview;
    private Fragment forushgah;
    private Fragment divar_detail;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.list_view:
                    if(fm!=null)
                    fm.popBackStack();
                   show_list_view("","","","");
                    return true;
                case R.id.category:
                    if(fm!=null)
                    fm.popBackStack();
                    show_filter_divar();
                    return true;
                case R.id.official_shop:
                    if(fm!=null)
                        fm.popBackStack();
                    show_forushgah();
                    return true;
                case R.id.my_divar:
                    if(fm!=null)
                        fm.popBackStack();
                    show_mydivar();
                    return true;
                case R.id.add_promotion:
                    if(fm!=null)
                        fm.popBackStack();
                    show_divar_add();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_divar);
        show_list_view("","","","");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        id= prefs.getString(getString(R.string.id),"0");
        token= prefs.getString(getString(R.string.token),"mlNxhAV54XBLNK8eIZroDDX6Yn1WbHsRQeTcA_qImyrjW2Wzgtp_Wu19w-DJINIu");
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    public void show_list_view(String cat,String subcat,String ostan,String city) {
        fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("cat", cat);
        bundle.putString("subcat", subcat);
        bundle.putString("city", city);
        bundle.putString("ostan", ostan);
        listview = new ListViewFragment();
        listview.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit_to_right, R.animator.enter, R.animator.exit_to_right);
        fragmentTransaction.add(R.id.divarframe, listview, "listview");
        fragmentTransaction.commit();
    }
    public void show_mydivar() {
        fm = getSupportFragmentManager();
        listview = new MyDivarListViewFragment();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit_to_right, R.animator.enter, R.animator.exit_to_right);
        fragmentTransaction.add(R.id.divarframe, listview, "listview");
        fragmentTransaction.commit();
    }
    public void show_divar_detail(int type) {
        fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        divar_detail = new divarDetailFragment();
        divar_detail.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit_to_right, R.animator.enter, R.animator.exit_to_right);
        fragmentTransaction.add(R.id.divarframe, divar_detail, "divar_detail");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    private void show_divar_add() {
        fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("type", "All");
        divar_detail = new AddDivarFragment();
        divar_detail.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit_to_right, R.animator.enter, R.animator.exit_to_right);
        fragmentTransaction.add(R.id.divarframe, divar_detail, "divar_detail");
        fragmentTransaction.commit();
    }
    private void show_forushgah() {
        fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("type", "all");
        forushgah = new ForushgahFragment();
        forushgah.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit_to_right, R.animator.enter, R.animator.exit_to_right);
        fragmentTransaction.add(R.id.divarframe, forushgah, "forushgah");
        fragmentTransaction.commit();
    }
    private void show_filter_divar() {
        fm = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("type", "all");
        forushgah = new divarFilterFragment();
        forushgah.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.enter, R.animator.exit_to_right, R.animator.enter, R.animator.exit_to_right);
        fragmentTransaction.add(R.id.divarframe, forushgah, "divarFilterFragment");
        fragmentTransaction.commit();
    }
}
