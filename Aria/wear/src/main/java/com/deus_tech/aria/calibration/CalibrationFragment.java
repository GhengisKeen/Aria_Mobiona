package com.deus_tech.aria.calibration;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.deus_tech.aria.MainActivity;
import com.deus_tech.aria.R;
import com.deus_tech.aria.smartphone.SmartphoneListener;
import com.deus_tech.aria.smartphone.SmartphoneManager;
import com.google.android.gms.wearable.DataMap;


public class CalibrationFragment extends Fragment implements View.OnClickListener{


    private MainActivity mainActivity;
    private TextView tvStatus;
    private Button bClose;


    //fragment lifecycle

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mainActivity = (MainActivity) getActivity();

        View rootView = null;

        if(mainActivity.isRound){
            rootView = inflater.inflate(R.layout.fragment_calibration_round, container, false);
        }else{
            rootView = inflater.inflate(R.layout.fragment_calibration_rect, container, false);
        }

        Typeface mainFont = Typeface.createFromAsset(mainActivity.getAssets(), getString(R.string.main_font));

        TextView tvTitle = (TextView) rootView.findViewById(R.id.tv_calibration_title);
        tvTitle.setTypeface(mainFont);

        tvStatus = (TextView) rootView.findViewById(R.id.tv_calibration_status);
        tvStatus.setTypeface(mainFont);

        bClose = (Button) rootView.findViewById(R.id.b_calibration_close);
        bClose.setTypeface(mainFont);
        bClose.setOnClickListener(this);

        return rootView;

    }//onCreateView


    public void onClick(View _view){

        if(_view == bClose) close();

    }//onClick


    //actions

    public void close(){

        mainActivity.changeCurrentView(SmartphoneManager.ROUTER_VIEW_DASHBOARD);

    }//close


}//CalibrationFragment
