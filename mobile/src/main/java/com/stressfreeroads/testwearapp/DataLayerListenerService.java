package com.stressfreeroads.testwearapp;

/**
 * Created by singh on 5/22/2018.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by uwe on 23.03.15.
 */
public class DataLayerListenerService extends WearableListenerService {

    private static final String LOG_TAG = "WearableListener";

    private static Handler handler;
    private static int currentValue=0;
    File file;
    private static FileOutputStream outputStream;
    Long x;

    public static Handler getHandler() {
        return handler;
    }

    public  static void setHandler(Handler handler) {
        DataLayerListenerService.handler = handler;
        // send current value as initial value.
        if(handler!=null)
            handler.sendEmptyMessage(currentValue);
        DataLayerListenerService dataLayerListenerService = new DataLayerListenerService();
        dataLayerListenerService.initFile();

    }

    private void initFile(){
        Long x = System.currentTimeMillis() / 1000;
        try {
            System.out.println("Name of the file : "+x.toString());
            outputStream = openFileOutput(x.toString() + ".txt", MODE_PRIVATE);   //create file in directory context.getFilesDir()
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);

        String id = peer.getId();
        String name = peer.getDisplayName();

        Log.d(LOG_TAG, "Connected peer name & ID: " + name + "|" + id);
    }
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        Log.d(LOG_TAG, "received a message from wear: " + messageEvent.getPath());
        // save the new heartbeat value
        currentValue = Integer.parseInt(messageEvent.getPath());
            try {
                outputStream.write(messageEvent.getPath().getBytes("UTF-8")); //revert back using Arrays.tostring(bytes)
                outputStream.close();
                file = new File(getApplicationContext().getFilesDir().getPath()+"/"+x.toString() + ".txt");
            } catch (Exception e) {
                e.printStackTrace();
            }
        if(handler!=null) {
            // if a handler is registered, send the value as new message
            handler.sendEmptyMessage(currentValue);
        }
    }


}