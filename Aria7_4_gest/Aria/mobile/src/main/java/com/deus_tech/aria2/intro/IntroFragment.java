package com.deus_tech.aria2.intro;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v4.content.ContextCompat;


import com.deus_tech.aria2.MainActivity;
import com.deus_tech.aria2.ariaService.AriaService;
import com.deus_tech.aria2.smartwatch.SmartwatchManager;
import com.deus_tech.ariasdk.Aria;
import com.deus_tech.ariasdk.AriaConnectionListener;
import com.deus_tech.aria2.R;

public class IntroFragment extends Fragment implements View.OnClickListener, AriaConnectionListener{


    private MainActivity mainActivity;
    private AriaService ariaService;

    private Button bConnect;
    private TextView tvStatus;
    private ProgressBar pbLoader;


    //fragment

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_intro, container, false);

        init();
        initUi(rootView);

        return rootView;

    }//onCreateView


    public void init(){

        mainActivity = ((MainActivity) getActivity());

        ariaService = mainActivity.ariaService;
        ariaService.aria.addListener(this);

    }//init


    public void initUi(View _rootView){

        Typeface mainFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.main_font));

        bConnect = (Button) _rootView.findViewById(R.id.b_intro_connect);
        bConnect.setTypeface(mainFont);
        bConnect.setOnClickListener(this);

        tvStatus = (TextView) _rootView.findViewById(R.id.tv_intro_status);
        tvStatus.setTypeface(mainFont);
        tvStatus.setText("");

        pbLoader = (ProgressBar) _rootView.findViewById(R.id.pb_intro_loader);
        pbLoader.setVisibility(View.INVISIBLE);

        updateUi();

    }//initUi


    public void onDestroyView(){

        super.onDestroyView();

        ariaService.aria.removeListener(this);

    }//onDestroyView


    public void onClick(View v){

        if(v.getId() == R.id.b_intro_connect) onClickConnect();

    }//onClick


    private void onClickConnect(){

        if(ariaService.aria.getStatus() == Aria.STATUS_NONE){

            ariaService.aria.startDiscovery();

        }

    }//onClickConnect


    public void updateUi(){

        this.getActivity().runOnUiThread(new Runnable(){

            public void run(){

                switch(ariaService.aria.getStatus()){

                    case Aria.STATUS_NONE:
                       /* if (ContextCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this.getActivity(), new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, 0);
                            return;
                        }*/

                        tvStatus.setText("");
                        pbLoader.setVisibility(View.INVISIBLE);
                        break;

                    case Aria.STATUS_DISCOVERING:

                        tvStatus.setText("Searching for Aria...");
                        pbLoader.setVisibility(View.VISIBLE);
                        break;

                    case Aria.STATUS_FOUND:

                        tvStatus.setText("Aria found! Connecting...");
                        pbLoader.setVisibility(View.VISIBLE);
                        break;

                    case Aria.STATUS_CONNECTING:

                        tvStatus.setText("Connecting...");
                        pbLoader.setVisibility(View.VISIBLE);
                        break;

                    case Aria.STATUS_CONNECTED:

                        tvStatus.setText("Aria connected! Starting...");
                        pbLoader.setVisibility(View.VISIBLE);
                        break;

                }

            }//run

        });

    }//updateUi


    //aria2

    public void onDiscoveryStarted(){

        updateUi();

    }//onDiscoveryStarted


    public void onDiscoveryFinished(boolean _found){

        updateUi();

        if(_found == true){
            ariaService.aria.connect();
        }

    }//onDiscoveryFinished


    public void onConnected(){

        updateUi();

    }//onConnected


    public void onReady(){

        mainActivity.changeCurrentView(SmartwatchManager.ROUTER_VIEW_DASHBOARD);

    }//onReady


    public void onDisconnected(){}//onDisconnected


}//IntroFragment