package com.deus_tech.aria;

import android.os.Bundle;
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

import com.deus_tech.aria.calibration.CalibrationFragment;
import com.deus_tech.aria.dashboard.DashboardFragment;
import com.deus_tech.aria.gopro.GoProFragment;
import com.deus_tech.aria.intro.IntroFragment;
import com.deus_tech.aria.music.MusicFragment;
import com.deus_tech.aria.smartphone.SmartphoneListener;
import com.deus_tech.aria.smartphone.SmartphoneManager;
import com.google.android.gms.wearable.DataMap;

import com.deus_tech.aria.test.TestFragment;


public class MainActivity extends FragmentActivity implements SmartphoneListener{


    public SmartphoneManager smartphoneManager;
    private int currentView;
    public boolean isRound;


    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        setActivityFlags();

        setContentView(R.layout.activity_main_stub);

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub_main);
        stub.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener(){

            @Override
            public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets){

                stub.onApplyWindowInsets(windowInsets);
                isRound = windowInsets.isRound();

                return windowInsets;
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

        smartphoneManager = new SmartphoneManager(this);
        smartphoneManager.addListener(this);
        smartphoneManager.connect();

    }//onStart


    protected void onStop(){

        super.onStop();

        smartphoneManager.disconnect();
        smartphoneManager.removeListener(this);

    }//onStop


    public void changeCurrentView(int _currentView){

        DataMap map = new DataMap();
        map.putInt(SmartphoneManager.ROUTER_VIEW, _currentView);
        smartphoneManager.writeSharedData(SmartphoneManager.ROUTER_PATH, map);

    }//changeCurrentView


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


    //smartphone manager

    public void onApiConnected(){

        Log.d("debug", "[main] onApiConnected");

        smartphoneManager.readSharedData(SmartphoneManager.ROUTER_PATH);

    }//onApiConnected


    public void onApiDisconnected(){

        Log.d("debug", "[main] onApiDisconnected");

        doRouting(-1);

    }//onApiDisconnected


    public void onSharedDataRead(String _path, DataMap _dataMap){

        Log.d("debug", "[main] onSharedDataRead");

        if(_path.equals(SmartphoneManager.ROUTER_PATH) == false) return;

        int viewToShow = _dataMap.getInt(SmartphoneManager.ROUTER_VIEW, -1);
        doRouting(viewToShow);

    }//onSharedDataRead


    public void onSharedDataNotFound(String _path){

        if(_path.equals(SmartphoneManager.ROUTER_PATH) == false) return;

        Log.d("debug", "[main] onSharedDataNotFound");
        doRouting(-1);

    }//onSharedDataNotFound


    public void onSharedDataChanged(String _path, DataMap _dataMap){

        Log.d("debug", "[main] onSharedDataChanged");

        if(_path.equals(SmartphoneManager.ROUTER_PATH) == false) return;

        int viewToShow = _dataMap.getInt(SmartphoneManager.ROUTER_VIEW, -1);
        doRouting(viewToShow);

    }//onSharedDataChanged


    public void onGestureReceived(String _gesture){

        Log.d("debug", "[main] onGestureReceived");

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 50};
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

    }//onGestureReceived


}//RouterActivity
