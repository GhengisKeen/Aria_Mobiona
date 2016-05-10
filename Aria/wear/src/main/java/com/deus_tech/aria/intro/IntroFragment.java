package com.deus_tech.aria.intro;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.wearable.view.CircledImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deus_tech.aria.MainActivity;
import com.deus_tech.aria.R;
import com.deus_tech.aria.ariaservice.AriaService;
import com.deus_tech.aria.view.CircularProgressView;
import com.deus_tech.ariasdk.Aria;
import com.deus_tech.ariasdk.AriaConnectionListener;


public class IntroFragment extends Fragment implements View.OnClickListener,AriaConnectionListener{


    private MainActivity mainActivity;
    private CircularProgressView progressView;
    private AriaService ariaService;
    TextView tvStatus;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mainActivity = (MainActivity) getActivity();

        View rootView = null;

        if(mainActivity.isRound){
            rootView = inflater.inflate(R.layout.fragment_intro_round, container, false);
        }else{
            rootView = inflater.inflate(R.layout.fragment_intro_rect, container, false);
        }

        Typeface mainFont = Typeface.createFromAsset(mainActivity.getAssets(), getString(R.string.main_font));

        tvStatus = (TextView) rootView.findViewById(R.id.tv_intro_status);
        tvStatus.setTypeface(mainFont);
        tvStatus.setOnClickListener(this);
        CircledImageView circledImageView=(CircledImageView)rootView.findViewById(R.id.connect);
        circledImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // search for Aria device and connect
                connectToAria();
            }
        });
        progressView=(CircularProgressView)rootView.findViewById(R.id.progressBar);
        //progressView.startAnimation();
        init();

        return rootView;

    }//onCreateView

    public void init(){

        mainActivity = ((MainActivity) getActivity());

        ariaService = mainActivity.ariaService;
        ariaService.aria.addListener(this);

    }
    private void connectToAria() {
        if(ariaService.aria.getStatus() == Aria.STATUS_NONE){
            ariaService.aria.startDiscovery();

        }


    }


    public void onDestroyView(){
        super.onDestroyView();
        ariaService.aria.removeListener(this);

    }//onDestroyView


    public void onClick(View v){

        //mainActivity.changeCurrentView(SmartphoneManager.ROUTER_VIEW_DASHBOARD);

    }//onClick


    @Override
    public void onDiscoveryStarted() {
      updateUi();
    }

    @Override
    public void onDiscoveryFinished(boolean _found) {
       updateUi();
        if(_found == true){
            ariaService.aria.connect();
        }

    }

    @Override
    public void onConnected() {
      updateUi();
    }

    @Override
    public void onReady() {
       //updateUi();
        mainActivity.onAriaConnected();
    }

    @Override
    public void onDisconnected() {

    }
    public void updateUi(){

        this.getActivity().runOnUiThread(new Runnable(){

            public void run(){

                switch(ariaService.aria.getStatus()){

                    case Aria.STATUS_NONE:

                        tvStatus.setText("");
                        progressView.setVisibility(View.INVISIBLE);
                        break;

                    case Aria.STATUS_DISCOVERING:

                        tvStatus.setText("Searching for Aria...");
                        progressView.setVisibility(View.VISIBLE);
                        progressView.startAnimation();
                        break;

                    case Aria.STATUS_FOUND:

                        tvStatus.setText("Aria found! Connecting...");
                        progressView.setVisibility(View.VISIBLE);
                        break;

                    case Aria.STATUS_CONNECTING:

                        tvStatus.setText("Connecting...");
                        progressView.setVisibility(View.VISIBLE);
                        break;

                    case Aria.STATUS_CONNECTED:

                        tvStatus.setText("Aria connected! Starting...");
                        progressView.setVisibility(View.VISIBLE);
                        break;

                }

            }//run

        });

    }//updateUi


}//IntroFragment
