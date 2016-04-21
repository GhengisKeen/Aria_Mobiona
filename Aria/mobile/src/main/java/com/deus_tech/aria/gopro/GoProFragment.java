package com.deus_tech.aria.gopro;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import com.deus_tech.aria.MainActivity;
import com.deus_tech.aria.R;
import com.deus_tech.aria.ariaService.AriaService;
import com.deus_tech.aria.smartwatch.SmartwatchListener;
import com.deus_tech.aria.smartwatch.SmartwatchManager;
import com.deus_tech.ariasdk.ariaBleService.ArsListener;
import com.google.android.gms.wearable.DataMap;


public class GoProFragment extends Fragment implements View.OnKeyListener, View.OnClickListener, SmartwatchListener, GoProListener{


    private MainActivity mainActivity;
    private AriaService ariaService;

    private TextView tvMode;
    private int currentStatus;


    //fragment

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_gopro, container, false);

        init();
        initUi(rootView);

        return rootView;

    }//onCreateView


    public void init(){

        mainActivity = ((MainActivity) getActivity());

        ariaService = mainActivity.ariaService;
        ariaService.smartwatchManager.addListener(this);

    }//init


    public void initUi(View _rootView){

        _rootView.setFocusableInTouchMode(true);
        _rootView.requestFocus();
        _rootView.setOnKeyListener(this);

        Typeface mainFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.main_font));

        TextView tvTitle = (TextView) _rootView.findViewById(R.id.tv_gopro_title);
        tvTitle.setTypeface(mainFont);

        tvMode = (TextView) _rootView.findViewById(R.id.tv_gopro_mode);
        tvMode.setTypeface(mainFont);

        ImageButton ibSettings = (ImageButton) _rootView.findViewById(R.id.ib_gopro_settings);
        ibSettings.setOnClickListener(this);

        updateUi();

    }//initUi


    public void onStart(){

        super.onStart();

        mainActivity.ariaService.goProManager.addListener(this);
        mainActivity.ariaService.smartwatchManager.readSharedData(SmartwatchManager.GOPRO_PATH);

    }//onStart


    public void onStop(){

        super.onStop();

        mainActivity.ariaService.goProManager.removeListener(this);

    }//onStop


    public void onDestroyView(){

        super.onDestroyView();

        ariaService.smartwatchManager.removeListener(this);

    }//onDestroyView


    public void onClick(View v){

        if(v.getId() == R.id.ib_gopro_settings) openSettings();

    }//onClick


    public void openSettings(){

        mainActivity.showFragment(new GoProSettingsFragment(), false);

    }//openSettings


    public void updateUi(){

        mainActivity.runOnUiThread(new Runnable(){

            public void run(){

                switch(currentStatus){

                    case SmartwatchManager.GOPRO_STATUS_SEARCHING:
                        tvMode.setText("Searching for GoPro");
                        break;
                    case SmartwatchManager.GOPRO_STATUS_NOT_FOUND:
                        tvMode.setText("GoPro not found");
                        break;
                    case SmartwatchManager.GOPRO_STATUS_CONNECTED:
                        tvMode.setText("GoPro connected");
                        break;
                    case SmartwatchManager.GOPRO_STATUS_READY:
                        tvMode.setText("GoPro ready");
                        break;
                    default:
                        tvMode.setText("GoPro not connected");
                        break;

                }

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


    //smartwatch

    public void onApiConnected(){}


    public void onApiDisconnected(){}


    public void onSharedDataRead(String _path, DataMap _dataMap){

        if(_path.equals(SmartwatchManager.GOPRO_PATH) == false) return;

        currentStatus = _dataMap.getInt(SmartwatchManager.GOPRO_STATUS, -1);
        updateUi();

    }//onSharedDataRead


    public void onSharedDataNotFound(String _path){}


    public void onSharedDataChanged(String _path, DataMap _dataMap){

        if(_path.equals(SmartwatchManager.GOPRO_PATH) == false) return;

        currentStatus = _dataMap.getInt(SmartwatchManager.GOPRO_STATUS, -1);
        updateUi();

    }//onSharedDataChanged


    //gopro

    public void onGoProNotFound(){}


    public void onGoProConnected(){}


    public void onGoProDisconnected(){}


    public void onGoProActionStarted(int _action){}


    public void onGoProActionDone(int _action){

        mainActivity.runOnUiThread(new Runnable(){

            public void run(){

                Animation showColor = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), R.anim.fade_in);
                tvMode.startAnimation(showColor);

            }//run

        });

    }//onGoProActionDone


    public void onGoProActionError(int _action){}


}//GoProFragment
