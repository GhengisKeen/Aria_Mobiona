package com.deus_tech.aria.music;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.deus_tech.aria.MainActivity;
import com.deus_tech.aria.R;
import com.deus_tech.aria.ariaService.AriaService;
import com.deus_tech.aria.gopro.GoProSettingsFragment;
import com.deus_tech.aria.smartwatch.SmartwatchManager;
import com.deus_tech.ariasdk.ariaBleService.ArsListener;


public class MusicFragment extends Fragment implements View.OnKeyListener, View.OnClickListener{

    private MainActivity mainActivity;
    private AriaService ariaService;


    //fragment

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_music, container, false);

        init();
        initUi(rootView);

        return rootView;

    }//onCreateView


    public void init(){

        mainActivity = ((MainActivity) getActivity());

        ariaService = mainActivity.ariaService;

    }//init


    public void initUi(View _rootView){

        _rootView.setFocusableInTouchMode(true);
        _rootView.requestFocus();
        _rootView.setOnKeyListener(this);

        Typeface mainFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.main_font));

        TextView tvTitle = (TextView) _rootView.findViewById(R.id.tv_music_title);
        tvTitle.setTypeface(mainFont);

        Button bStart = (Button) _rootView.findViewById(R.id.b_music_start);
        bStart.setTypeface(mainFont);
        bStart.setOnClickListener(this);

        updateUi();

    }//initUi


    public void onDestroyView(){

        super.onDestroyView();

    }//onDestroyView


    public void onClick(View v){

        if(v.getId() == R.id.b_music_start) openPlayer();

    }//onClick


    public void openPlayer(){

        Intent intent = new Intent("android.intent.action.MUSIC_PLAYER");
        startActivity(intent);

    }//openPlayer


    public void updateUi(){

        this.getActivity().runOnUiThread(new Runnable(){

            public void run(){


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


}//MusicFragment
