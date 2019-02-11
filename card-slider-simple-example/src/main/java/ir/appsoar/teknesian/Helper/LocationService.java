package ir.appsoar.teknesian.Helper;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import ir.appsoar.teknesian.R;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import ir.appsoar.teknesian.Fonts;

public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "locationmanager";
    public static final String BROADCAST_finish= "finishactivity";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;

    Intent intent;
    Intent finishintent;
    private Socket mSocket;
    SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        intent = new Intent(BROADCAST_ACTION);
        finishintent = new Intent(BROADCAST_finish);
        Fonts app = (Fonts) getApplication();
        mSocket = app.getSocket();
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on("successreg", onNewMessage);
        mSocket.on("locationrequest", onNewMessage);
        mSocket.connect();

    }
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if(publoc!=null) {
                Log.e("lacationrequested", "lacationrequested");
                JSONObject location = new JSONObject();
                try {
                    location.put("location", publoc.getLatitude() + "," + publoc.getLongitude());
                    mSocket.emit("teklocation", location);
                    prefs.edit().putString(getString(R.string.lastlatlng),publoc.getLatitude() + "," + publoc.getLongitude()).commit();

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



    @Override
    public void onStart(Intent intent, int startId) {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }



    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }



    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);     
        super.onDestroy();
        connected=false;
        mSocket.off();
        mSocket.disconnect();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    public static Location publoc;

    boolean locationset=false;
    boolean connected=false;
    public class MyLocationListener implements LocationListener
    {

        public void onLocationChanged(final Location loc)
        {
            Log.i("***********************", "Location changed");
            publoc=loc;
            intent.putExtra("status", "1");
            prefs.edit().putString(getString(R.string.lastlatlng),loc.getLatitude()+","+loc.getLongitude()).commit();
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

        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "شیفت کاری غیر فعال شد.", Toast.LENGTH_SHORT ).show();
            intent.putExtra("status", "0");
            finishintent.putExtra("status", "0");
            sendBroadcast(intent);
            sendBroadcast(finishintent);
            stopSelf();
        }


        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }


        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.i("statuschanged", String.valueOf(status));
/*
            Toast.makeText( getApplicationContext(), "statuschanged", Toast.LENGTH_SHORT).show();
*/

        }

    }
}