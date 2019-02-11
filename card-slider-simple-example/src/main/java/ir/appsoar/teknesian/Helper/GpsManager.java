package ir.appsoar.teknesian.Helper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ir.appsoar.teknesian.Fonts;

public class GpsManager extends Service
{
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    private Boolean locationrequested=false;
    private static Location mLastLocation;
    private class LocationListener implements android.location.LocationListener
    {
        LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            if(locationrequested) {
                Log.e("lacationrequested", "lacationrequested");
                JSONObject location1 = new JSONObject();
                try {
                    location1.put("location", mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
                    mSocket.emit("teklocation", location1);
                    locationrequested=false;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    private LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }
    private Socket mSocket;
    private static final String BROADCAST_ACTION = "locationmanager";
    private static final String BROADCAST_finish= "finishactivity";
    @Override
    public void onCreate()
    {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        new Intent(BROADCAST_ACTION);
        new Intent(BROADCAST_finish);
        Fonts app = (Fonts) getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("successreg", onNewMessage);
        mSocket.on("locationrequest", onNewMessage);
        mSocket.connect();
    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(mLastLocation!=null) {
                Log.e("lacationrequested", "lacationrequested");
                JSONObject location = new JSONObject();
                try {
                    location.put("location", mLastLocation.getLatitude() + "," + mLastLocation.getLongitude());
                    mSocket.emit("teklocation", location);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else
                locationrequested=true;
        }
    };
    private Emitter.Listener onConnect = args -> {
    };

    private Emitter.Listener onDisconnect = args -> {

    };

    private Emitter.Listener onConnectError = args -> {

    };

    @Override
    public void onDestroy()
    {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (LocationListener mLocationListener : mLocationListeners) {
                try {
                    mLocationManager.removeUpdates(mLocationListener);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}