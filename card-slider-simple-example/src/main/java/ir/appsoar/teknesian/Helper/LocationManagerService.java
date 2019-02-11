package ir.appsoar.teknesian.Helper;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import ir.appsoar.teknesian.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ir.appsoar.teknesian.Fonts;


@SuppressLint("Registered")
public class LocationManagerService extends Service {
    public static final String BROADCAST_ACTION = "locationmanager";
    public static final String BROADCAST_finish= "finishactivity";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public LocationService.MyLocationListener listener;

    Intent intent;
    Intent finishintent;
    private Socket mSocket;
    SharedPreferences prefs;
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    public static Location publoc;
    public static Location mLastLocation;

    boolean locationset=false;
    boolean connected=false;
    private class LocationListener implements android.location.LocationListener {

        public LocationListener(String provider) {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location loc) {
            Log.e(TAG, "onLocationChanged: " + loc);
            mLastLocation.set(loc);
            Log.i("***********************", "Location changed");
            Log.i("**********majid", loc.getLatitude()+","+loc.getLongitude());
            prefs.edit().putString(getString(R.string.lastlatlng),loc.getLatitude()+","+loc.getLongitude()).apply();
            publoc=loc;
            intent.putExtra("status", loc.getLatitude()+","+loc.getLongitude());
            sendBroadcast(intent);
            if(connected&&!locationset)
            {
                JSONObject location = new JSONObject();
                try {
                    location.put("lastname", prefs.getString(getString(R.string.name), "بدون نام"));
                    location.put("_id", prefs.getString(getString(R.string.id), "0"));
                    location.put("token", prefs.getString(getString(R.string.token), " "));
                    location.put("lat", loc.getLatitude());
                    location.put("lng", loc.getLongitude());
                    mSocket.emit("login", location);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            locationset=true;
        }

        @Override
        public void onProviderDisabled(String provider) {
            if(!provider.equals("network")){
            Log.e(TAG, "onProviderDisabled: " + provider);
            Toast.makeText( getApplicationContext(), "شیفت کاری غیر فعال شد.", Toast.LENGTH_SHORT ).show();
            intent.putExtra("status", "0");
            finishintent.putExtra("status", "0");
            sendBroadcast(intent);
            sendBroadcast(finishintent);
            stopSelf();
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

        intent = new Intent(BROADCAST_ACTION);
        finishintent = new Intent(BROADCAST_finish);
        Fonts app = (Fonts) getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on("successreg", onNewMessage);
        mSocket.on("locationrequest", onNewMessage);
        mSocket.connect();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }

        connected=false;
        mSocket.off();
        mSocket.disconnect();
        Log.v("STOP_SERVICE", "DONE");
        if(locationManager!=null)
        locationManager.removeUpdates(listener);
    }

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

    ///////////////SOCKET FUNCS///////
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(publoc!=null) {
                Log.e("lacationrequested", "lacationrequested");
                JSONObject location = new JSONObject();
                try {
                    location.put("location", publoc.getLatitude() + "," + publoc.getLongitude());
                    mSocket.emit("teklocation", location);
                    Log.e("majidsadrayi",publoc.getLatitude() + "," + publoc.getLongitude());
                    prefs.edit().putString(getString(R.string.lastlatlng),publoc.getLatitude() + "," + publoc.getLongitude()).apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            connected=true;
            if (locationset) {
                JSONObject location = new JSONObject();
                try {
                    location.put("lastname", prefs.getString(getString(R.string.name), "بدون نام"));
                    location.put("_id", prefs.getString(getString(R.string.id), "0"));
                    location.put("token", prefs.getString(getString(R.string.token), " "));
                    location.put("lat", publoc.getLatitude());
                    location.put("lng", publoc.getLongitude());
                    mSocket.emit("login", location);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    };


}
