package com.deus_tech.aria2.calibration;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deus_tech.aria2.MainActivity;
import com.deus_tech.aria2.R;
import com.deus_tech.aria2.smartwatch.SmartwatchManager;

public class CalibrationEndFragment extends Fragment implements View.OnKeyListener{


    private MainActivity mainActivity;


    //fragment

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_end_calibration, container, false);

        init();
        initUi(rootView);

        return rootView;

    }//onCreateView


    public void init(){

        mainActivity = ((MainActivity) getActivity());

    }//init


    private void initUi(View _rootView){

        _rootView.setKeepScreenOn(true);
        _rootView.setFocusableInTouchMode(true);
        _rootView.requestFocus();
        _rootView.setOnKeyListener(this);

        Typeface mainFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.main_font));

        TextView title = (TextView) _rootView.findViewById(R.id.tv_end_cal_title);
        title.setTypeface(mainFont);

        TextView status = (TextView) _rootView.findViewById(R.id.tv_end_cal_status);
        status.setTypeface(mainFont);

    }//initUi


    public boolean onKey(View v, int keyCode, KeyEvent event){

        if(event.getAction()!=KeyEvent.ACTION_DOWN) return true;

        if(keyCode == KeyEvent.KEYCODE_BACK){

            mainActivity.changeCurrentView(SmartwatchManager.ROUTER_VIEW_DASHBOARD);
            return true;

        }else{

            return false;

        }

    }//onKey


}//CalibrationEndFragment
