package ir.appsoar.teknesian.Network;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import io.socket.client.Socket;


public class SocketClient {

    private static final String TAG = "SOCKET HELPER";

    private static Socket socket;


    public static Socket SocketClient(){
        return socket;
    }

    public SocketClient intialize(String phneNumber , LatLng latLng){
        final String stringQuery = String.format("phoneNumber=%s&type=peyk&lat=%.3f&lng=%.3f",phneNumber , latLng.latitude , latLng.longitude);
        /*try {
            //socket = IO.socket(Configuration.URL , new IO.Options(){{this.query = stringQuery;}});
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/
        return this;

    }

    public SocketClient setListeners(){



        return this;
    }

    public void start(){
        socket.connect();
    }

    public void finish(){
        if(socket.connected()){
            socket.disconnect();
        }else{
            Log.e(TAG, "finish: socket that is disconected...");
        }
    }





    public void getrequest(String mobile){
        socket.emit("requestClient",mobile );

    }



}
