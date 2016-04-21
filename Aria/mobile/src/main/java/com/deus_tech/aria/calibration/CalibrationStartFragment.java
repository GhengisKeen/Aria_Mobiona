package com.deus_tech.aria.calibration;


import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.deus_tech.aria.MainActivity;
import com.deus_tech.aria.R;
import com.deus_tech.aria.ariaService.AriaService;
import com.deus_tech.aria.smartwatch.SmartwatchManager;
import com.deus_tech.aria.util.Util;
import com.deus_tech.ariasdk.Aria;
import com.deus_tech.ariasdk.calibrationBleService.CasListener;
import com.deus_tech.ariasdk.calibrationBleService.CalibrationBleService;


public class CalibrationStartFragment extends Fragment implements View.OnClickListener, View.OnKeyListener, CasListener{


    private MainActivity mainActivity;
    private AriaService ariaService;
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

        isCalibrationStarting = false;

    }//init


    private void initUi(View _rootView){

        _rootView.setKeepScreenOn(true);
        _rootView.setFocusableInTouchMode(true);
        _rootView.requestFocus();
        _rootView.setOnKeyListener(this);

        Typeface mainFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.main_font));

        TextView title = (TextView) _rootView.findViewById(R.id.tv_start_cal_title);
        title.setTypeface(mainFont);

        Button buttonStart = (Button) _rootView.findViewById(R.id.b_start_cal_start);
        buttonStart.setTypeface(mainFont);
        buttonStart.setOnClickListener(this);

    }//initUi


    public void onDestroyView(){

        super.onDestroyView();

        CalibrationBleService cas = ariaService.aria.getCas();

        if(isCalibrationStarting == false){

            cas.stopCalibration();

        }

        cas.removeCasListener(this);

    }//onDestroyView


    public void onClick(View v){

        if(v.getId() == R.id.b_start_cal_start) startCalibration();

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


}//CalibrationStartFragment
