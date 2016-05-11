package com.deus_tech.aria2.test;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.deus_tech.aria2.MainActivity;
import com.deus_tech.aria2.R;
import com.deus_tech.aria2.ariaService.AriaService;
import com.deus_tech.aria2.smartwatch.SmartwatchManager;
import com.deus_tech.ariasdk.ariaBleService.AriaBleService;
import com.deus_tech.ariasdk.ariaBleService.ArsListener;


public class TestFragment extends Fragment implements View.OnKeyListener, ArsListener{

    private MainActivity mainActivity;
    private AriaService ariaService;
    private TextView tvBattery;
    private TextView textViewGesture;
    private String lastGesture;

    //fragment

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_test, container, false);

        init();
        initUi(rootView);

        return rootView;

    }//onCreateView


    public void init(){

        mainActivity = ((MainActivity) getActivity());

        ariaService = mainActivity.ariaService;
        ariaService.aria.getCas().startExecution();
        ariaService.aria.getArs().addListener(this);

    }//init


    public void initUi(View _rootView){

        _rootView.setKeepScreenOn(true);
        _rootView.setFocusableInTouchMode(true);
        _rootView.requestFocus();
        _rootView.setOnKeyListener(this);

        Typeface mainFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.main_font));

        TextView title = (TextView) _rootView.findViewById(R.id.tv_test_title);
        title.setTypeface(mainFont);

        tvBattery = (TextView) _rootView.findViewById(R.id.tv_battery);

        ariaService.aria.getArs().readBattery();

        textViewGesture = (TextView) _rootView.findViewById(R.id.tv_test_gesture);
        textViewGesture.setTypeface(mainFont);

    }//initUi


    public void onDestroyView(){

        super.onDestroyView();

        ariaService.aria.getArs().removeListener(this);

    }//onDestroyView


    public void updateUi(){

        mainActivity.runOnUiThread(new Runnable(){

            public void run(){

                textViewGesture.setText(lastGesture);
                Animation showColor = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), R.anim.fade_in);
                textViewGesture.startAnimation(showColor);

            }//run

        });

    }//updateUi


    public boolean onKey(View v, int keyCode, KeyEvent event){

        if(event.getAction()!=KeyEvent.ACTION_DOWN) return true;

        if(keyCode == KeyEvent.KEYCODE_BACK){

            mainActivity.changeCurrentView(SmartwatchManager.ROUTER_VIEW_DASHBOARD);
            return true;

        }else{

            return false;

        }

    }//onKey


    //aria2

    public void onGesturePerformed(int _gesture){

        if(_gesture == AriaBleService.GESTURE_HOME){
            lastGesture = "Home";
        }else if(_gesture == AriaBleService.GESTURE_ENTER){
            lastGesture = "Enter";
        }else if(_gesture == AriaBleService.GESTURE_BACK){
            lastGesture = "Back";
        }else if(_gesture == AriaBleService.GESTURE_UP){
            lastGesture = "Up";
        }else if(_gesture == AriaBleService.GESTURE_DOWN){
            lastGesture = "Down";
        }

        updateUi();

    }//onGesturePerformed


    public void onBatteryValueUpdated(int _batteryValue){
        final int batval=_batteryValue;
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvBattery.setText(batval + "%");

            }
        });
    }//onBatteryValueUpdated


}//TestFragment
