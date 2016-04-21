package com.deus_tech.aria.gopro;


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
import com.deus_tech.aria.dashboard.AppModel;
import com.deus_tech.aria.smartphone.SmartphoneListener;
import com.deus_tech.aria.smartphone.SmartphoneManager;
import com.google.android.gms.wearable.DataMap;


public class GoProFragment extends Fragment implements View.OnClickListener, SmartphoneListener{


    private MainActivity mainActivity;
    private TextView tvStatus;
    private Button bClose;
    private int currentStatus;


    //fragment

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mainActivity = (MainActivity) getActivity();
        mainActivity.smartphoneManager.addListener(this);

        View rootView = null;

        if(mainActivity.isRound){
            rootView = inflater.inflate(R.layout.fragment_gopro_round, container, false);
        }else{
            rootView = inflater.inflate(R.layout.fragment_gopro_rect, container, false);
        }

        Typeface mainFont = Typeface.createFromAsset(mainActivity.getAssets(), getString(R.string.main_font));

        TextView tvTitle = (TextView) rootView.findViewById(R.id.tv_gopro_title);
        tvTitle.setTypeface(mainFont);

        tvStatus = (TextView) rootView.findViewById(R.id.tv_gopro_status);
        tvStatus.setTypeface(mainFont);

        bClose = (Button) rootView.findViewById(R.id.b_gopro_close);
        bClose.setTypeface(mainFont);
        bClose.setOnClickListener(this);

        return rootView;

    }//onCreateView


    public void onStart(){

        super.onStart();

        mainActivity.smartphoneManager.readSharedData(SmartphoneManager.GOPRO_PATH);

    }//onStart


    public void onDestroyView(){

        super.onDestroyView();

        mainActivity.smartphoneManager.removeListener(this);

    }//onDestroyView


    private void updateUi(){

        mainActivity.runOnUiThread(new Runnable(){

            public void run(){

                switch(currentStatus){

                    case SmartphoneManager.GOPRO_STATUS_SEARCHING:
                        tvStatus.setText("Searching for GoPro");
                        break;
                    case SmartphoneManager.GOPRO_STATUS_NOT_FOUND:
                        tvStatus.setText("GoPro not found");
                        break;
                    case SmartphoneManager.GOPRO_STATUS_CONNECTED:
                        tvStatus.setText("GoPro connected");
                        break;
                    case SmartphoneManager.GOPRO_STATUS_READY:
                        tvStatus.setText("GoPro ready");
                        break;
                    default:
                        tvStatus.setText("GoPro not connected");
                        break;

                }

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

    public void onApiConnected(){}


    public void onApiDisconnected(){}


    public void onSharedDataRead(String _path, DataMap _dataMap){

        if(_path.equals(SmartphoneManager.GOPRO_PATH) == false) return;

        currentStatus = _dataMap.getInt(SmartphoneManager.GOPRO_STATUS, -1);
        updateUi();

    }//onSharedDataRead


    public void onSharedDataNotFound(String _path){}


    public void onSharedDataChanged(String _path, DataMap _dataMap){

        if(_path.equals(SmartphoneManager.GOPRO_PATH) == false) return;

        currentStatus = _dataMap.getInt(SmartphoneManager.GOPRO_STATUS, -1);
        updateUi();

    }//onSharedDataChanged


    public void onGestureReceived(String _gesture){

        if(_gesture.equals(SmartphoneManager.ARIA_MESSAGE_HOME)){

            close();

        }

    }//onGestureReceived


}//GoProFragment
