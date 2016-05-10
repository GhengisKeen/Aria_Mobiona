package com.deus_tech.aria;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;

import com.deus_tech.aria.ariaservice.AriaService;
import com.deus_tech.aria.ariaservice.AriaServiceBinder;
import com.deus_tech.aria.calibration.CalibrationFragment;
import com.deus_tech.aria.dashboard.DashboardFragment;
import com.deus_tech.aria.gopro.GoProFragment;
import com.deus_tech.aria.intro.IntroFragment;
import com.deus_tech.aria.music.MusicFragment;
import com.deus_tech.aria.smartphone.SmartphoneManager;
import com.deus_tech.aria.test.TestFragment;


public class MainActivity extends FragmentActivity implements ServiceConnection{


    private int currentView;
    public boolean isRound;
    public AriaService ariaService;

    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        setActivityFlags();

        setContentView(R.layout.activity_main_stub);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub_main);
        stub.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {

            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {

                stub.onApplyWindowInsets(windowInsets);
                isRound = windowInsets.isRound();

                return windowInsets;
            }
        });
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub watchViewStub) {

            }
        });

    }//onCreate


    private void setActivityFlags(){

        final Window windows = getWindow();
        windows.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        windows.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        windows.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        windows.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        windows.addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);

    }//setActivityFlags


    protected void onStart(){

        super.onStart();
        startAndBindAriaService();
    }//onStart


    protected void onStop(){

        super.onStop();
        stopAndUnbindAriaService(); 


    }//onStop


   //changeCurrentView


    private void doRouting(int _viewToShow){

        boolean isSameView = (_viewToShow == currentView);

        if(_viewToShow == -1 || _viewToShow == SmartphoneManager.ROUTER_VIEW_INTRO){

            showFragment(new IntroFragment(), isSameView);

        }else if(_viewToShow == SmartphoneManager.ROUTER_VIEW_DASHBOARD){

            showFragment(new DashboardFragment(), isSameView);

        }else if(_viewToShow == SmartphoneManager.ROUTER_VIEW_GOPRO){

            showFragment(new GoProFragment(), isSameView);

        }else if(_viewToShow == SmartphoneManager.ROUTER_VIEW_MUSIC){

            showFragment(new MusicFragment(), isSameView);

        }else if(_viewToShow == SmartphoneManager.ROUTER_VIEW_START_CALIBRATION || _viewToShow == SmartphoneManager.ROUTER_VIEW_CALIBRATION || _viewToShow == SmartphoneManager.ROUTER_VIEW_END_CALIBRATION){

            showFragment(new CalibrationFragment(), isSameView);

        }else if(_viewToShow == SmartphoneManager.ROUTER_VIEW_TEST){

            showFragment(new TestFragment(), isSameView);

        }

        currentView = _viewToShow;

    }//doRouting


    private void showFragment(final Fragment _fragment, final boolean _isSameView){

        runOnUiThread(new Runnable() {

            public void run() {

                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.popBackStackImmediate();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                if (_isSameView == false) {
                    transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
                }

                transaction.replace(R.id.container, _fragment);
                transaction.commit();

            }
        });

    }//showFragment


    //smartphone manager


    //onSharedDataNotFound




    public void onGestureReceived(String _gesture){

        Log.d("debug", "[main] onGestureReceived");

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 50};
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

    }//onGestureReceived
    public void startAndBindAriaService(){

        Intent ariaIntent = new Intent(this, AriaService.class);
        ariaIntent.setAction(AriaService.START_FOREGROUND_ACTION);
        startService(ariaIntent);
        bindService(ariaIntent, this, Context.BIND_AUTO_CREATE);

    }
    public void stopAndUnbindAriaService(){

        unbindService(this);
        ariaService.stopForegroundIfAriaIsDisconnected();

    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        AriaServiceBinder ariaBinder = (AriaServiceBinder) service;
        ariaBinder.setMainActivity(this);
        ariaService = ariaBinder.getService();
        if(ariaService!=null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new IntroFragment()).commit();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }
    public void onAriaConnected(){
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new DashboardFragment()).commit();
    }
}//RouterActivity
