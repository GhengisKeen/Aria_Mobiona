package com.deus_tech.aria2.music;

import android.content.Intent;
import android.content.pm.PackageManager;
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


public class MusicFragment extends Fragment implements View.OnClickListener, SmartphoneListener{


    private MainActivity mainActivity;
    private TextView tvStatus;
    private Button bClose;


    //fragment

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mainActivity = (MainActivity) getActivity();
        mainActivity.smartphoneManager.addListener(this);

        View rootView = null;

        if(mainActivity.isRound){
            rootView = inflater.inflate(R.layout.fragment_music_round, container, false);
        }else{
            rootView = inflater.inflate(R.layout.fragment_music_rect, container, false);
        }

        Typeface mainFont = Typeface.createFromAsset(mainActivity.getAssets(), getString(R.string.main_font));

        TextView tvTitle = (TextView) rootView.findViewById(R.id.tv_music_title);
        tvTitle.setTypeface(mainFont);

        Button bPlayer = (Button) rootView.findViewById(R.id.b_music_start);
        bPlayer.setTypeface(mainFont);
        bPlayer.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                String appToLaunch = "com.google.android.music";
                PackageManager pm = mainActivity.getPackageManager();
                Intent intent = pm.getLaunchIntentForPackage(appToLaunch);
                startActivity(intent);

            }
        });

        bClose = (Button) rootView.findViewById(R.id.b_music_close);
        bClose.setTypeface(mainFont);
        bClose.setOnClickListener(this);

        return rootView;

    }//onCreateView


    public void onStart(){

        super.onStart();

        //mainActivity.smartphoneManager.readSharedData(SmartphoneManager.GOPRO_PATH);

    }//onStart


    public void onDestroyView(){

        super.onDestroyView();

        mainActivity.smartphoneManager.removeListener(this);

    }//onDestroyView


    private void updateUi(){

        mainActivity.runOnUiThread(new Runnable(){

            public void run(){


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


    public void changeCurrentMusicMode(int _mode){

    }//changeCurrentMusicMode


    //smartphone events

    public void onApiConnected(){}


    public void onApiDisconnected(){}


    public void onSharedDataRead(String _path, DataMap _dataMap){}


    public void onSharedDataNotFound(String _path){}


    public void onSharedDataChanged(String _path, DataMap _dataMap){}


    public void onGestureReceived(String _gesture){

        if(_gesture.equals(SmartphoneManager.ARIA_MESSAGE_HOME)){

            close();

        }

    }//onGestureReceived


}//MusicFragment
