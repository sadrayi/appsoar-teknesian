package ir.appsoar.teknesian.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.github.nitrico.mapviewpager.MapViewPager;

import java.util.List;

import ir.appsoar.teknesian.Activity.AddressActivity;
import ir.appsoar.teknesian.Fragment.Sample2Fragment;
import ir.map.sdk_map.wrapper.MaptexCameraPosition;

import static ir.appsoar.teknesian.Activity.AddressActivity.addresstitle;

public class Sample2Adapter extends MapViewPager.MultiAdapter {



    public Sample2Adapter(FragmentManager fm) {
        super(fm);

        // camera positions
    }

    @Override
    public int getCount() {
        return addresstitle.length;
    }

    @Override
    public Fragment getItem(int position) {
        return Sample2Fragment.newInstance(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return addresstitle[position];
    }

    @Override
    public String getMarkerTitle(int page, int position) {

        return addresstitle[page];
    }

    @Override
    public List<MaptexCameraPosition> getCameraPositions(int page) {

        return AddressActivity.LONDON.get(page);
    }

}
