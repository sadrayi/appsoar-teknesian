package ir.appsoar.teknesian.Fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ir.appsoar.teknesian.R;

/*
import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.MapView;
import com.cedarstudios.cedarmapssdk.listeners.OnTilesConfigured;
import com.mapbox.mapboxsdk.maps.MapboxMap;*/


public class MapsFragment extends Fragment {

    //private MapView mMapView;
    //private MapboxMap mMapboxMap;
    public static String BUNDLE_IMAGE_ID="latlng";
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {


/*com.mapbox.mapboxsdk.geometry.LatLng sydney = new com.mapbox.mapboxsdk.geometry.LatLng(-34, 151);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        // SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()                .findFragmentById(R.id.map);
        final View[] rootView = new View[1];
        CedarMaps.getInstance()
                .setClientID("astanlift-4643291497598649385")
                .setClientSecret("bfoFFGFzdGFubGlmdMOFKW76TgvVtRCU1p4Dik-EjFxqNRXOpOkJzFbXyL4y")
                .setContext(getContext());
        CedarMaps.getInstance().prepareTiles(new OnTilesConfigured() {
            @Override
            public void onSuccess() {
                 rootView[0] = inflater.inflate(R.layout.activity_maps, parentViewGroup, false);

                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                mMapView = (MapView) rootView[0].findViewById(R.id.mapView);
                mMapView.getMapAsync(new com.mapbox.mapboxsdk.maps.OnMapReadyCallback() {
                    @Override
                    public void onMapReady(MapboxMap mapboxMap) {
                        mMapboxMap = mapboxMap;

                        mMapboxMap.setMaxZoomPreference(17);
                        mMapboxMap.setMinZoomPreference(6);


                    }
                });
            }

            @Override
            public void onFailure(@NonNull String error) {
                Log.e("majid",error);
            }
        });
        return rootView[0];*/
        return inflater.inflate(R.layout.activity_maps, parentViewGroup, false);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */




}
