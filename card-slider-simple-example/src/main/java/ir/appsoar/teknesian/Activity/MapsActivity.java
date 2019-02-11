package ir.appsoar.teknesian.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import ir.appsoar.teknesian.R;

import java.util.Objects;

import ir.map.sdk_common.MaptexLatLng;
import ir.map.sdk_map.wrapper.MaptexBitmapDescriptorFactory;
import ir.map.sdk_map.wrapper.MaptexCameraPosition;
import ir.map.sdk_map.wrapper.MaptexCameraUpdateFactory;
import ir.map.sdk_map.wrapper.MaptexMap;
import ir.map.sdk_map.wrapper.MaptexMarker;
import ir.map.sdk_map.wrapper.MaptexMarkerOptions;
import ir.map.sdk_map.wrapper.SupportMaptexFragment;

public class MapsActivity extends FragmentActivity  {


    public static String BUNDLE_IMAGE_ID="latlng";
    private MaptexMap maptexMap;
    private MaptexMarker mTehran;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cedarmap);
        Bundle bundle=getIntent().getExtras();
        final MaptexLatLng sydney;
        if( bundle != null) {
            String markerpoint = bundle.getString(BUNDLE_IMAGE_ID);
            assert markerpoint != null;
            String[] markerlatlng = markerpoint.split(",");
            sydney = new MaptexLatLng(Float.valueOf(markerlatlng[0]),Float.valueOf(markerlatlng[1]));

        }
        else
            sydney = new MaptexLatLng(Float.valueOf("36.3309698133826"),Float.valueOf("59.53737337142229"));

        /////////
        ((SupportMaptexFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.myMapView)))
                .getMaptexAsync(map -> {
                    maptexMap = map;

                    if (maptexMap != null) {

                        maptexMap.setMaxZoomPreference(17);
                        MaptexCameraPosition position = new MaptexCameraPosition.Builder()
                                .target(sydney) // Sets the new camera position
                                .bearing(360) // Rotate the camera
                                .tilt(30) // Set the camera tilt
                                .zoom(14)
                                .build(); // Creates a CameraPosition from the builder

                        maptexMap.animateCamera(MaptexCameraUpdateFactory
                                .newCameraPosition(position));

                        MaptexMarkerOptions currentMarker = new MaptexMarkerOptions()
                                .position(sydney).icon(MaptexBitmapDescriptorFactory.fromResource(R.mipmap.ic_marker));

                        maptexMap.addMarker(currentMarker);
                    }

                });
        ///////
        /*CedarMaps.getInstance()
                .setClientID("astanlift-4643291497598649385")
                .setClientSecret("bfoFFGFzdGFubGlmdMOFKW76TgvVtRCU1p4Dik-EjFxqNRXOpOkJzFbXyL4y")
                .setContext(this);
        CedarMaps.getInstance().prepareTiles(new OnTilesConfigured() {
            @Override
            public void onSuccess() {
                setContentView(R.layout.cedarmap);
                mMapView = findViewById(R.id.mapView);
                mMapView.getMapAsync(mapboxMap -> {
                    mMapboxMap = mapboxMap;

                    mMapboxMap.setMaxZoomPreference(17);
                    mMapboxMap.setMinZoomPreference(6);
                    CameraPosition position = new CameraPosition.Builder()
                            .target(sydney) // Sets the new camera position
                            .bearing(360) // Rotate the camera
                            .tilt(30) // Set the camera tilt
                            .build(); // Creates a CameraPosition from the builder

                    mapboxMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(position), 7000);

                    MarkerOptions currentMarker = new MarkerOptions()
                            .position(sydney);

                    mMapboxMap.addMarker(currentMarker);
                });

            }

            @Override
            public void onFailure(@NonNull String error) {

            }
        });*/

       // mapFragment.getMapAsync(this);
    }



}
