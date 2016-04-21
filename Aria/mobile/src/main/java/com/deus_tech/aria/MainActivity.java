package com.deus_tech.aria;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.deus_tech.aria.ariaService.AriaServiceBinder;
import com.deus_tech.aria.ariaService.AriaService;
import com.deus_tech.aria.calibration.CalibrationEndFragment;
import com.deus_tech.aria.calibration.CalibrationFragment;
import com.deus_tech.aria.calibration.CalibrationStartFragment;
import com.deus_tech.aria.gopro.GoProFragment;
import com.deus_tech.aria.intro.IntroFragment;
import com.deus_tech.aria.dashboard.DashboardFragment;
import com.deus_tech.aria.music.MusicFragment;
import com.deus_tech.aria.smartwatch.SmartwatchListener;
import com.deus_tech.aria.smartwatch.SmartwatchManager;
import com.deus_tech.aria.test.TestFragment;
import com.deus_tech.ariasdk.Aria;
import com.google.android.gms.wearable.DataMap;


public class MainActivity extends FragmentActivity implements ServiceConnection, SmartwatchListener{


    public AriaService ariaService;
    private int currentView;
    private boolean isActive;


    //activity

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

    }//onCreate


    protected void onStart(){

        super.onStart();

        isActive = true;

        startAndBindAriaService();

    }//onStart


    protected void onStop(){

        super.onStop();

        isActive = false;

        ariaService.smartwatchManager.removeListener(this);
        stopAndUnbindAriaService();

    }//onStop


    public boolean isActive(){

        return isActive;

    }//isActive


    public void changeCurrentView(int _currentView){

        DataMap map = new DataMap();
        map.putInt(SmartwatchManager.ROUTER_VIEW, _currentView);
        ariaService.smartwatchManager.writeSharedData(SmartwatchManager.ROUTER_PATH, map);

    }//changeCurrentView


    private void doRouting(int _viewToShow){

        boolean isSameView = (_viewToShow == currentView);

        if(_viewToShow == -1 || _viewToShow == SmartwatchManager.ROUTER_VIEW_INTRO){

            showFragment(new IntroFragment(), isSameView);

        }else if(_viewToShow == SmartwatchManager.ROUTER_VIEW_DASHBOARD){

            showFragment(new DashboardFragment(), isSameView);

        }else if(_viewToShow == SmartwatchManager.ROUTER_VIEW_GOPRO){

            showFragment(new GoProFragment(), isSameView);

        }else if(_viewToShow == SmartwatchManager.ROUTER_VIEW_MUSIC){

            showFragment(new MusicFragment(), isSameView);

        }else if(_viewToShow == SmartwatchManager.ROUTER_VIEW_START_CALIBRATION){

            showFragment(new CalibrationStartFragment(), isSameView);

        }else if(_viewToShow == SmartwatchManager.ROUTER_VIEW_CALIBRATION){

            showFragment(new CalibrationFragment(), isSameView);

        }else if(_viewToShow == SmartwatchManager.ROUTER_VIEW_END_CALIBRATION){

            showFragment(new CalibrationEndFragment(), isSameView);

        }else if(_viewToShow == SmartwatchManager.ROUTER_VIEW_TEST){

            showFragment(new TestFragment(), isSameView);

        }

        currentView = _viewToShow;

    }//doRouting


    public void showFragment(final Fragment _fragment, final boolean _isSameView){

        runOnUiThread(new Runnable(){

            public void run(){

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStackImmediate();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                if(_isSameView == false){
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                }

                transaction.replace(R.id.container, _fragment);
                transaction.commit();

            }
        });

    }//showFragment


    // aria service

    public void startAndBindAriaService(){

        Intent ariaIntent = new Intent(this, AriaService.class);
        ariaIntent.setAction(AriaService.START_FOREGROUND_ACTION);

        startService(ariaIntent);
        bindService(ariaIntent, this, Context.BIND_AUTO_CREATE);

    }//startAndBindAriaService


    public void stopAndUnbindAriaService(){

        unbindService(this);
        ariaService.stopForegroundIfAriaIsDisconnected();

    }//stopAndUnbindAriaService


    public void onServiceConnected(ComponentName name, IBinder service){

        AriaServiceBinder ariaBinder = (AriaServiceBinder) service;
        ariaBinder.setMainActivity(this);
        ariaService = ariaBinder.getService();

        ariaService.smartwatchManager.addListener(this);
        ariaService.smartwatchManager.connect();

        if(ariaService.smartwatchManager.isConnected() && ariaService.aria.getStatus() == Aria.STATUS_READY){
            ariaService.smartwatchManager.readSharedData(SmartwatchManager.ROUTER_PATH);
        }else if(ariaService.smartwatchManager.isConnected()){
            changeCurrentView(SmartwatchManager.ROUTER_VIEW_INTRO);
        }

    }//onServiceConnected


    public void onServiceDisconnected(ComponentName name){}//onServiceDisconnected


    //smartphone manager

    public void onApiConnected(){

        Log.d("debug", "[main] onApiConnected");

        if(ariaService.aria.getStatus() == Aria.STATUS_READY){
            ariaService.smartwatchManager.readSharedData(SmartwatchManager.ROUTER_PATH);
        }else{
            changeCurrentView(SmartwatchManager.ROUTER_VIEW_INTRO);
        }

    }//onApiConnected


    public void onApiDisconnected(){

        Log.d("debug", "[main] onApiDisconnected");

    }//onApiDisconnected


    public void onSharedDataRead(String _path, DataMap _dataMap){

        Log.d("debug", "[main] onSharedDataRead: "+_path);

        if(_path.equals(SmartwatchManager.ROUTER_PATH) == false) return;

        int viewToShow = _dataMap.getInt(SmartwatchManager.ROUTER_VIEW, -1);
        doRouting(viewToShow);

    }//onSharedDataRead


    public void onSharedDataNotFound(String _path){

        Log.d("debug", "[main] onSharedDataNotFound");

        if(_path.equals(SmartwatchManager.ROUTER_PATH) == false) return;

        doRouting(-1);

    }//onSharedDataNotFound


    public void onSharedDataChanged(String _path, DataMap _dataMap){

        Log.d("debug", "[main] onSharedDataChanged: "+_path);

        if(_path.equals(SmartwatchManager.ROUTER_PATH) == false) return;

        int viewToShow = _dataMap.getInt(SmartwatchManager.ROUTER_VIEW, -1);
        doRouting(viewToShow);

    }//onSharedDataChanged


}//MainActivity