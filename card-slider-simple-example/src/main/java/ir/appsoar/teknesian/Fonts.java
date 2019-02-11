package ir.appsoar.teknesian;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import ir.appsoar.teknesian.MyAidl;
import ir.appsoar.teknesian.R;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import ir.appsoar.teknesian.ScreenShotService.ASS;
import ir.map.sdk_map.MapSDK;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static ir.appsoar.teknesian.Helper.Config.SocketUrl;

/**
 * Created by LapTop on 30/07/2017.
 */


public class Fonts extends Application {
    ///////////////////// screen shot manager
    private static final String TAG = Fonts.class.getSimpleName();
    private ServiceConnection mConnection;
    private MyAidl mServiceBinder;

    private static Fonts instance;


    ///////////////
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(SocketUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return mSocket;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("vazir.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        MapSDK.init(getBaseContext());
        instance = this;

        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.e(TAG, "onServiceConnected");
                mServiceBinder = MyAidl.Stub.asInterface(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.e(TAG, "onServiceDisconnected");
            }
        };
        Intent intent = new Intent(instance, ASS.class);
        intent.setAction("abc.def.ghi");
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    public static Fonts getInstance() {
        return instance;
    }

    public void registerScreenshotObserver() {
        try {
            if (mServiceBinder == null) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            int count = 0;
                            while (count != 10) {
                                Thread.sleep(400);
                                if (mServiceBinder != null) {
                                    mServiceBinder.registerScreenShotObserver();
                                    break;
                                }
                                count++;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString(), e);
                            e.printStackTrace();
                        }
                    }
                }.start();
            } else {
                mServiceBinder.registerScreenShotObserver();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            e.printStackTrace();
        }
    }
    public void unregisterScreenshotObserver() {
        try {
            if (mServiceBinder == null) {
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            int count = 0;
                            while (count != 10) {
                                Thread.sleep(400);
                                if (mServiceBinder != null) {
                                    mServiceBinder.unregisterScreenShotObserver();
                                    break;
                                }
                                count++;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.toString(), e);
                            e.printStackTrace();
                        }
                    }
                }.start();
            } else {
                mServiceBinder.unregisterScreenShotObserver();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
            e.printStackTrace();
        }
    }

    public void allowUserSaveScreenshot(boolean enable){
        try {
            mServiceBinder.setScreenShotEnable(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}