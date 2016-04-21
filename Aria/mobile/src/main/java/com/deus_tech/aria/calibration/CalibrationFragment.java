package com.deus_tech.aria.calibration;


import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.VideoView;

import com.deus_tech.aria.MainActivity;
import com.deus_tech.aria.R;
import com.deus_tech.aria.ariaService.AriaService;
import com.deus_tech.aria.smartwatch.SmartwatchManager;
import com.deus_tech.ariasdk.ariaBleService.AriaBleService;
import com.deus_tech.ariasdk.calibrationBleService.CalibrationBleService;
import com.deus_tech.ariasdk.calibrationBleService.CasListener;

import java.util.Timer;
import java.util.TimerTask;

public class CalibrationFragment extends Fragment implements CasListener, View.OnKeyListener{


    private MainActivity mainActivity;
    private AriaService ariaService;
    private boolean isCalibrationFinished;

    private TextView textViewGesture;
    private TextView textViewIteration;
    private TextView textViewStatus;
    private VideoView videoGesture;
    private final int delayFeedback = 200;


    //fragment

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_calibration, container, false);

        init();
        initUi(rootView);

        return rootView;

    }//onCreateView


    public void init(){

        mainActivity = ((MainActivity) getActivity());

        ariaService = mainActivity.ariaService;
        ariaService.aria.getCas().addCasListener(this);

    }//init


    private void initUi(View _rootView){

        _rootView.setKeepScreenOn(true);
        _rootView.setFocusableInTouchMode(true);
        _rootView.requestFocus();
        _rootView.setOnKeyListener(this);

        Typeface mainFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.main_font));

        textViewGesture = (TextView) _rootView.findViewById(R.id.tv_cal_gesture);
        textViewGesture.setTypeface(mainFont);

        textViewIteration = (TextView) _rootView.findViewById(R.id.tv_cal_iteration);
        textViewIteration.setTypeface(mainFont);

        textViewStatus = (TextView) _rootView.findViewById(R.id.tv_cal_status);
        textViewStatus.setTypeface(mainFont);

        videoGesture = (VideoView) _rootView.findViewById(R.id.vv_cal_gesture);

        updateUi();

    }//initUi


    public void onDestroyView(){

        super.onDestroyView();

        CalibrationBleService cas = ariaService.aria.getCas();

        if(cas.getCalibrationStatus() != CalibrationBleService.CALIBRATION_MODE_NONE){

            cas.stopCalibration();

        }

        cas.removeCasListener(this);

        if(isCalibrationFinished == false){
            mainActivity.changeCurrentView(SmartwatchManager.ROUTER_VIEW_DASHBOARD);
        }

    }//onDestroyView


    public void updateUi(){

        this.getActivity().runOnUiThread(new Runnable(){

            public void run(){

                CalibrationBleService cas = ariaService.aria.getCas();

                int gestureIndex = cas.getGestureIndex();
                int gestureIteration = cas.getGestureIteration();
                int maxIteration = cas.getIterationsNumber();
                int status = cas.getGestureStatus();

                if(gestureIndex == 1){
                    textViewGesture.setText("Gesture \"Enter\"");
                }else if(gestureIndex == 2){
                    textViewGesture.setText("Gesture \"Down\"");
                }else if(gestureIndex == 3){
                    textViewGesture.setText("Gesture \"Home\"");
                }

                textViewIteration.setText(gestureIteration + " of " + maxIteration);


                if(status == CalibrationBleService.GESTURE_STATUS_RECORDING){

                    int bgColor = mainActivity.getResources().getColor(R.color.bg2);
                    textViewStatus.setBackgroundColor(bgColor);
                    textViewStatus.setText("Perform the gesture");

                    Uri uri = null;

                    if(gestureIndex == 1){
                        uri = Uri.parse("android.resource://" + mainActivity.getPackageName() + "/" + R.raw.video_1);
                    }else if(gestureIndex == 2){
                        uri = Uri.parse("android.resource://" + mainActivity.getPackageName() + "/" + R.raw.video_2);
                    }else if(gestureIndex == 3){
                        uri = Uri.parse("android.resource://" + mainActivity.getPackageName() + "/" + R.raw.video_3);
                    }else if(gestureIndex == 4){
                        uri = Uri.parse("android.resource://" + mainActivity.getPackageName() + "/" + R.raw.video_4);
                    }else if(gestureIndex == 5){
                        uri = Uri.parse("android.resource://" + mainActivity.getPackageName() + "/" + R.raw.video_5);
                    }

                    videoGesture.setZOrderMediaOverlay(true);
                    videoGesture.setZOrderOnTop(true);
                    videoGesture.setVideoURI(uri);
                    videoGesture.start();

                }else if(status == CalibrationBleService.GESTURE_STATUS_OK){

                    int okColor = mainActivity.getResources().getColor(R.color.bgOk);
                    textViewStatus.setBackgroundColor(okColor);
                    textViewStatus.setText("Ok");

                }else if(status == CalibrationBleService.GESTURE_STATUS_ERROR1){

                    int errorColor = mainActivity.getResources().getColor(R.color.bgError);
                    textViewStatus.setBackgroundColor(errorColor);
                    textViewStatus.setText("Error");

                }

            }

        });

    }//updateUi


    public boolean onKey(View v, int keyCode, KeyEvent event){

        if(event.getAction()!=KeyEvent.ACTION_DOWN) return true;

        if(keyCode == KeyEvent.KEYCODE_BACK){

            videoGesture.setVisibility(View.INVISIBLE);
            mainActivity.changeCurrentView(SmartwatchManager.ROUTER_VIEW_DASHBOARD);
            return true;

        }else{

            return false;

        }

    }//onKey


    //aria

    public void onCalibrationStarted(){}


    public void onCalibrationStepStarted(int _gestureIndex, int _gestureIteration){}


    public void onCalibrationStepRecording(int _gestureIndex, int _gestureIteration){

        updateUi();

    }//onCalibrationStepRecording


    public void onCalibrationStepDone(int _gestureIndex, int _gestureIteration){

        updateUi();

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask(){

            public void run(){
                ariaService.aria.getCas().nextCalibrationStep();
            }

        }, delayFeedback);

    }//onCalibrationStepDone


    public void onCalibrationStepError(int _gestureIndex, int _gestureIteration){

        updateUi();

        Timer myTimer = new Timer();
        myTimer.schedule(new TimerTask(){

            public void run(){
                ariaService.aria.getCas().repeatCalibrationStep();
            }

        }, delayFeedback);

    }//onCalibrationStepError


    public void onCalibrationFinished(){

        isCalibrationFinished = true;
        mainActivity.changeCurrentView(SmartwatchManager.ROUTER_VIEW_END_CALIBRATION);

    }//onCalibrationFinished


}//CalibrationFragment