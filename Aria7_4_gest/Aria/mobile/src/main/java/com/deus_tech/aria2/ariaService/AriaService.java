package com.deus_tech.aria2.ariaService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.deus_tech.aria2.MainActivity;
import com.deus_tech.aria2.R;
import com.deus_tech.aria2.gopro.GoProListener;
import com.deus_tech.aria2.gopro.GoProManager;
import com.deus_tech.aria2.music.MusicManager;
import com.deus_tech.aria2.smartwatch.SmartwatchListener;
import com.deus_tech.aria2.smartwatch.SmartwatchManager;
import com.deus_tech.ariasdk.Aria;
import com.deus_tech.ariasdk.AriaConnectionListener;
import com.deus_tech.ariasdk.ariaBleService.AriaBleService;
import com.deus_tech.ariasdk.ariaBleService.ArsListener;
import com.google.android.gms.wearable.DataMap;


public class AriaService extends Service implements AriaConnectionListener, ArsListener, SmartwatchListener, GoProListener{


    public final static int SERVICE_NOTIFICATION_ID = 8588;

    public final static String START_FOREGROUND_ACTION = "com.deus_tech.ariasdk.action.startForeground";
    public final static String CLOSE_APP_ACTION = "com.deus_tech.ariasdk.action.closeApp";

    public static int batterylevel;
    public Aria aria;
    public SmartwatchManager smartwatchManager;
    public GoProManager goProManager;
    public MusicManager musicManager;
    private AriaServiceBinder ariaServiceBinder;
    private boolean isClosing;
    private int currentMode;


    //service

    public void onCreate(){

        super.onCreate();

        ariaServiceBinder = new AriaServiceBinder(this);

        aria = Aria.getInstance(this);
        aria.addListener(this);

        smartwatchManager = new SmartwatchManager(this);
        smartwatchManager.addListener(this);

        goProManager = new GoProManager(this);
        goProManager.addListener(this);

        musicManager = new MusicManager(this);

    }//onCreate


    public int onStartCommand(Intent intent, int flags, int startId){

        if(intent.getAction().equals(com.deus_tech.aria2.ariaService.AriaService.START_FOREGROUND_ACTION)){

            startForeground();

        }else if (intent.getAction().equals(com.deus_tech.aria2.ariaService.AriaService.CLOSE_APP_ACTION)){

            isClosing = true;
            closeApp();

        }

        return START_NOT_STICKY;

    }//onStartCommand


    public IBinder onBind(Intent intent){

        return ariaServiceBinder;

    }//onBind


    //actions

    public void startForeground(){

        startForeground(AriaService.SERVICE_NOTIFICATION_ID, this.getNotification());

    }//startForeground


    public void closeApp(){

        //1. disconnect aria2
        if(aria != null){
            aria.disconnect();
        }

        //2. finish the activity
        if(ariaServiceBinder.getMainActivity() != null){
            ariaServiceBinder.getMainActivity().finish();
        }

        //3. stop foreground
        stopForeground(true);

    }//closeApp


    public void stopForegroundIfAriaIsDisconnected(){

        if(aria == null || aria.getStatus() == Aria.STATUS_NONE){
            stopForeground(true);
        }

    }//stopForegroundIfAriaIsDisconnected


    //notification

    private Notification getNotification(){

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Intent closeIntent = new Intent(this, AriaService.class);
        closeIntent.setAction(com.deus_tech.aria2.ariaService.AriaService.CLOSE_APP_ACTION);
        PendingIntent pendingCloseIntent = PendingIntent.getService(this, 0, closeIntent, 0);


        Notification notification = new NotificationCompat.Builder(this)
                .setShowWhen(false)
                .setContentTitle(this.getNotificationTitle())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_notification_close, "Close", pendingCloseIntent)
                .build();

        return notification;

    }//getNotification


    private String getNotificationTitle(){

        if(aria.getStatus() == Aria.STATUS_READY){

            return "Aria connected";

        }else{

            return "Aria is not connected";

        }

    }//getNotificationTitle


    private void updateNotification(){

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(AriaService.SERVICE_NOTIFICATION_ID, getNotification());

    }//updateNotification


    //smartwatch

    public void onApiConnected(){}


    public void onApiDisconnected(){}


    public void onSharedDataRead(String _path, DataMap _dataMap){}


    public void onSharedDataNotFound(String _path){}


    public void onSharedDataChanged(String _path, DataMap _dataMap){

        if(_path.equals(SmartwatchManager.ROUTER_PATH) == false) return;

        currentMode = _dataMap.getInt(SmartwatchManager.ROUTER_VIEW, -1);

        if(currentMode == SmartwatchManager.ROUTER_VIEW_GOPRO){

            goProManager.connect();
            DataMap map = new DataMap();
            map.putInt(SmartwatchManager.GOPRO_STATUS, SmartwatchManager.GOPRO_STATUS_SEARCHING);
            smartwatchManager.writeSharedData(SmartwatchManager.GOPRO_PATH, map);

        }

    }//onSharedDataChanged


    //aria2

    public void onDiscoveryStarted(){}


    public void onDiscoveryFinished(boolean _found){}


    public void onConnected(){}


    public void onReady(){

        aria.getArs().addListener(this);
        updateNotification();

    }//onReady


    public void onDisconnected(){

        aria.getArs().removeListener(this);
        if(isClosing == false){
            updateNotification();
        }

        DataMap map = new DataMap();
        map.putInt(SmartwatchManager.ROUTER_VIEW, SmartwatchManager.ROUTER_VIEW_INTRO);
        smartwatchManager.writeSharedData(SmartwatchManager.ROUTER_PATH, map);

    }//onDisconnected


    public void onGesturePerformed(int _gesture){

        if(_gesture == AriaBleService.GESTURE_HOME){

            smartwatchManager.sendMessage(SmartwatchManager.ARIA_MESSAGE_HOME);

        }else if(_gesture == AriaBleService.GESTURE_BACK){

            smartwatchManager.sendMessage(SmartwatchManager.ARIA_MESSAGE_BACK);

        }else if(_gesture == AriaBleService.GESTURE_ENTER){

            smartwatchManager.sendMessage(SmartwatchManager.ARIA_MESSAGE_ENTER);

        }else if(_gesture == AriaBleService.GESTURE_UP){

            smartwatchManager.sendMessage(SmartwatchManager.ARIA_MESSAGE_UP);

        }else if(_gesture == AriaBleService.GESTURE_DOWN){

            smartwatchManager.sendMessage(SmartwatchManager.ARIA_MESSAGE_DOWN);

        }



        if(currentMode == SmartwatchManager.ROUTER_VIEW_GOPRO){

            onGestureForGoPro(_gesture);

        }else if(currentMode == SmartwatchManager.ROUTER_VIEW_MUSIC){

            onGestureForMusic(_gesture);

        }

    }//onGesturePerformed


    private void onGestureForGoPro(int _gesture){

        if(_gesture == AriaBleService.GESTURE_HOME){

            //none
            goProManager.doAction(GoProManager.ACTION_END_CAPTURE);

        }else if(_gesture == AriaBleService.GESTURE_BACK){

            //none

        }else if(_gesture == AriaBleService.GESTURE_ENTER){

            goProManager.doAction(GoProManager.ACTION_START_CAPTURE);

        }else if(_gesture == AriaBleService.GESTURE_UP){

            //none

        }else if(_gesture == AriaBleService.GESTURE_DOWN){

            goProManager.doAction(GoProManager.ACTION_CHANGE_MODE);

        }

    }//onGestureForGoPro


    private void onGestureForMusic(int _gesture){

        if(_gesture == AriaBleService.GESTURE_HOME){

            //none

        }else if(_gesture == AriaBleService.GESTURE_BACK){

            //none

        }else if(_gesture == AriaBleService.GESTURE_ENTER){

            musicManager.togglePause();

        }else if(_gesture == AriaBleService.GESTURE_UP){

            //none

        }else if(_gesture == AriaBleService.GESTURE_DOWN){

            musicManager.next();

        }

    }//onGestureForMusic


    public void onBatteryValueUpdated(int _batteryValue){
        batterylevel = _batteryValue;
    }

    public int getBatteryLevel(){
        return batterylevel;
    }

    //goPro

    public void onGoProNotFound(){

        DataMap map = new DataMap();
        map.putInt(SmartwatchManager.GOPRO_STATUS, SmartwatchManager.GOPRO_STATUS_NOT_FOUND);
        smartwatchManager.writeSharedData(SmartwatchManager.GOPRO_PATH, map);

    }//onGoProNotFound


    public void onGoProConnected(){

        DataMap map = new DataMap();
        map.putInt(SmartwatchManager.GOPRO_STATUS, SmartwatchManager.GOPRO_STATUS_CONNECTED);
        smartwatchManager.writeSharedData(SmartwatchManager.GOPRO_PATH, map);

        goProManager.doAction(GoProManager.ACTION_TURN_ON);

    }//onGoProConnected


    public void onGoProDisconnected(){

        DataMap map = new DataMap();
        map.putInt(SmartwatchManager.GOPRO_STATUS, SmartwatchManager.GOPRO_STATUS_NOT_FOUND);
        smartwatchManager.writeSharedData(SmartwatchManager.GOPRO_PATH, map);

    }//onGoProDisconnected


    public void onGoProActionStarted(int _action){}


    public void onGoProActionDone(int _action){}//onGoProActionDone


    public void onGoProActionError(int _action){

        DataMap map = new DataMap();
        map.putInt(SmartwatchManager.GOPRO_STATUS, SmartwatchManager.GOPRO_STATUS_NOT_FOUND);
        smartwatchManager.writeSharedData(SmartwatchManager.GOPRO_PATH, map);

        goProManager.connect();

    }//onGoProActionError


}//ConnectionService