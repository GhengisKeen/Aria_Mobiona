package com.deus_tech.aria2.test;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.deus_tech.aria2.MainActivity;
import com.deus_tech.aria2.R;
import com.deus_tech.aria2.smartphone.SmartphoneListener;
import com.deus_tech.aria2.smartphone.SmartphoneManager;
import com.google.android.gms.wearable.DataMap;


public class TestFragment extends Fragment implements View.OnClickListener, SmartphoneListener{


    private MainActivity mainActivity;
    private TextView tvStatus;
    private Button bClose;

    private String status;


    //fragment

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mainActivity = (MainActivity) getActivity();
        mainActivity.smartphoneManager.addListener(this);

        View rootView = null;

        if(mainActivity.isRound){
            rootView = inflater.inflate(R.layout.fragment_test_round, container, false);
        }else{
            rootView = inflater.inflate(R.layout.fragment_test_rect, container, false);
        }

        Typeface mainFont = Typeface.createFromAsset(mainActivity.getAssets(), getString(R.string.main_font));

        TextView tvTitle = (TextView) rootView.findViewById(R.id.tv_test_title);
        tvTitle.setTypeface(mainFont);

        tvStatus = (TextView) rootView.findViewById(R.id.tv_test_status);
        tvStatus.setTypeface(mainFont);

        bClose = (Button) rootView.findViewById(R.id.b_test_close);
        bClose.setTypeface(mainFont);
        bClose.setOnClickListener(this);

        return rootView;

    }//onCreateView


    public void onStart(){

        super.onStart();

    }//onStart


    public void onDestroyView(){

        super.onDestroyView();

        mainActivity.smartphoneManager.removeListener(this);

    }//onDestroyView


    private void updateUi(){

        mainActivity.runOnUiThread(new Runnable(){

            public void run(){

                tvStatus.setText(status);

            }//run

        });

    }//updateUi


    public void onClick(View _view){

        if(_view == bClose) close();

    }//onClick


    //actions

    public void close(){

        mainActivity.changeCurrentView(SmartphoneManager.ROUTER_VIEW_DASHBOARD);

    }//close


    //smartphone events

    public void onApiConnected(){}//onApiConnected


    public void onApiDisconnected(){}//onApiDisconnected


    public void onSharedDataRead(String _path, DataMap _dataMap){}//onSharedDataRead


    public void onSharedDataNotFound(String _path){}//onSharedDataNotFound


    public void onSharedDataChanged(String _path, DataMap _dataMap){}//onSharedDataChanged


    public void onGestureReceived(String _gesture){

        if(_gesture.equals(SmartphoneManager.ARIA_MESSAGE_BACK)){
            status = "Back";
        }else if(_gesture.equals(SmartphoneManager.ARIA_MESSAGE_HOME)){
            status = "Home";
        }else if(_gesture.equals(SmartphoneManager.ARIA_MESSAGE_ENTER)){
            status = "Enter";
        }else if(_gesture.equals(SmartphoneManager.ARIA_MESSAGE_DOWN)){
            status = "Down";
        }else if(_gesture.equals(SmartphoneManager.ARIA_MESSAGE_UP)){
            status = "Up";
        }

        updateUi();

    }//onGestureReceived


}//TestFragment
