package ir.appsoar.teknesian.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ir.appsoar.teknesian.R;

import ir.appsoar.teknesian.Activity.OrderManageActivity;

public class ServiceFormFrag extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.service_form, parentViewGroup, false);
        OrderManageActivity.toolbar.setTitle("فرم سرویس");

        Button confirm= rootView.findViewById(R.id.dialogButtonOK);
        confirm.setOnClickListener(view -> {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.main_frame,new FactorFrag());
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return rootView;
    }

}