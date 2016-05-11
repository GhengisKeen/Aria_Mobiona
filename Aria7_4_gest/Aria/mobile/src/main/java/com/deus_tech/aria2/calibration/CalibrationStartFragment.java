package com.deus_tech.aria2.calibration;


import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import com.deus_tech.aria2.MainActivity;
import com.deus_tech.aria2.R;
import com.deus_tech.aria2.ariaService.AriaService;
import com.deus_tech.aria2.smartwatch.SmartwatchManager;
import com.deus_tech.ariasdk.ariaBleService.ArsListener;
import com.deus_tech.ariasdk.calibrationBleService.CasListener;
import com.deus_tech.ariasdk.calibrationBleService.CalibrationBleService;


public class CalibrationStartFragment extends Fragment implements View.OnClickListener, View.OnKeyListener, CasListener, ArsListener{


    private MainActivity mainActivity;
    private AriaService ariaService;
    private VideoView videoGesture;
    private TextView tvBattery;

    private boolean isCalibrationStarting;


    //fragment

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_start_calibration, container, false);

        init();
        initUi(rootView);

        return rootView;

    }//onCreateView


    public void init(){

        mainActivity = ((MainActivity) getActivity());

        ariaService = mainActivity.ariaService;
        ariaService.aria.getCas().addCasListener(this);
        ariaService.aria.getArs().addListener(this);

        isCalibrationStarting = false;

    }//init


    private void initUi(View _rootView){

        _rootView.setKeepScreenOn(true);
        _rootView.setFocusableInTouchMode(true);
        _rootView.requestFocus();
        _rootView.setOnKeyListener(this);

        Typeface mainFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.main_font));
        tvBattery = (TextView) _rootView.findViewById(R.id.tv_battery);
        ariaService.aria.getArs().readBattery();

        TextView title = (TextView) _rootView.findViewById(R.id.tv_start_cal_title);
        title.setTypeface(mainFont);

        Button buttonStart = (Button) _rootView.findViewById(R.id.b_start_cal_start);
        buttonStart.setTypeface(mainFont);
        buttonStart.setOnClickListener(this);

        Button buttonEnter = (Button) _rootView.findViewById(R.id.b_enter);
        buttonEnter.setTypeface(mainFont);
        buttonEnter.setOnClickListener(this);

        Button buttonHome = (Button) _rootView.findViewById(R.id.b_home);
        buttonHome.setTypeface(mainFont);
        buttonHome.setOnClickListener(this);

        Button buttonUp = (Button) _rootView.findViewById(R.id.b_up);
        buttonUp.setTypeface(mainFont);
        buttonUp.setOnClickListener(this);

        Button buttonDown = (Button) _rootView.findViewById(R.id.b_down);
        buttonDown.setTypeface(mainFont);
        buttonDown.setOnClickListener(this);

        videoGesture = (VideoView) _rootView.findViewById(R.id.vv_cal_gesture);
        videoPlay(1);

    }//initUi


    public void onDestroyView(){

        super.onDestroyView();

        CalibrationBleService cas = ariaService.aria.getCas();

        if(isCalibrationStarting == false){

            cas.startSleep();

        }

        cas.removeCasListener(this);
        ariaService.aria.getArs().removeListener(this);

    }//onDestroyView

    public void videoPlay(final int i_video){
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Uri uri;
                if(i_video==1) uri = Uri.parse("android.resource://" + mainActivity.getPackageName() + "/" + R.raw.video_1);
                else if(i_video==2) uri = Uri.parse("android.resource://" + mainActivity.getPackageName() + "/" + R.raw.video_2);
                else if(i_video==3) uri = Uri.parse("android.resource://" + mainActivity.getPackageName() + "/" + R.raw.video_3);
                else if(i_video==4) uri = Uri.parse("android.resource://" + mainActivity.getPackageName() + "/" + R.raw.video_4);
                else if(i_video==5) uri = Uri.parse("android.resource://" + mainActivity.getPackageName() + "/" + R.raw.video_5);
                else uri = Uri.parse("android.resource://" + mainActivity.getPackageName() + "/" + R.raw.video_1);

                videoGesture.setZOrderMediaOverlay(true);
                videoGesture.setZOrderOnTop(true);
                videoGesture.setVideoURI(uri);
                videoGesture.start();
            }
        });
    }

    public void onClick(View v){

        if (v.getId()== R.id.b_start_cal_start){
            videoGesture.setVisibility(View.INVISIBLE);

            startCalibration();

        }
        else if (v.getId()==R.id.b_home)    videoPlay(2);
        else if (v.getId()==R.id.b_enter)   videoPlay(1);
        else if (v.getId()==R.id.b_up)      videoPlay(3);
        else if (v.getId()==R.id.b_down)    videoPlay(4);

    }//onClick


    public boolean onKey(View v, int keyCode, KeyEvent event){

        if(event.getAction()!=KeyEvent.ACTION_DOWN) return true;

        if(keyCode == KeyEvent.KEYCODE_BACK){

            mainActivity.changeCurrentView(SmartwatchManager.ROUTER_VIEW_DASHBOARD);
            return true;

        }else{

            return false;

        }

    }//onKey


    //actions

    public void startCalibration(){

        ariaService.aria.getCas().startCalibration();

    }//startCalibration


    //calibration

    public void onCalibrationStarted(){

        ariaService.aria.getCas().nextCalibrationStep();

    }//onCalibrationStarted


    public void onCalibrationStepStarted(int _gestureIndex, int _gestureIteration){}


    public void onCalibrationStepRecording(int _gestureIndex, int _gestureIteration){

        isCalibrationStarting = true;

        mainActivity.changeCurrentView(SmartwatchManager.ROUTER_VIEW_CALIBRATION);

    }//onCalibrationStepRecording


    public void onCalibrationStepDone(int _gestureIndex, int _gestureIteration){}


    public void onCalibrationStepError(int _gestureIndex, int _gestureIteration){}


    public void onCalibrationFinished(){}


    public void onGesturePerformed(int _gesture){}//onGesturePerformed


    public void onBatteryValueUpdated(int _batteryValue){
        final int batval=_batteryValue;
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvBattery.setText(batval + "%");

            }
        });
    }//onBatteryValueUpdated



}//CalibrationStartFragment

