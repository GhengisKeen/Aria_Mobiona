package com.deus_tech.aria.dashboard;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.deus_tech.aria.MainActivity;
import com.deus_tech.aria.R;
import com.deus_tech.aria.ariaService.AriaService;
import com.deus_tech.aria.smartwatch.SmartwatchListener;
import com.deus_tech.aria.smartwatch.SmartwatchManager;
import com.deus_tech.ariasdk.AriaConnectionListener;
import com.deus_tech.ariasdk.ariaBleService.AriaBleService;
import com.deus_tech.ariasdk.ariaBleService.ArsListener;
import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;

public class DashboardFragment extends Fragment implements View.OnClickListener, AriaConnectionListener, ArsListener, SmartwatchListener, AdapterView.OnItemClickListener{


    private MainActivity mainActivity;
    private AriaService ariaService;

    private ListView lvMenu;
    private MenuAdapter menuAdapter;
    private int menuIndex;
    private ItemModel menuSelectedModel;


    //fragment

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);

        init();
        initUi(rootView);

        return rootView;

    }//onCreateView


    public void init(){

        mainActivity = ((MainActivity) getActivity());

        ariaService = mainActivity.ariaService;
        ariaService.aria.addListener(this);
        ariaService.aria.getArs().addListener(this);

        ariaService.smartwatchManager.addListener(this);

    }//init


    private void initUi(View _rootView){

        Typeface mainFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.main_font));

        Button bDisconnect;bDisconnect = (Button) _rootView.findViewById(R.id.b_dashboard_disconnect);
        bDisconnect.setTypeface(mainFont);
        bDisconnect.setOnClickListener(this);

        lvMenu = (ListView) _rootView.findViewById(R.id.lv_dashboard_menu);
        initList();

    }//initUi


    private void initList(){

        menuIndex = -1;

        ArrayList<ItemModel> list = new ArrayList<ItemModel>();
        list.add(new ItemModel("GoPro"));
        list.add(new ItemModel("Music"));
        list.add(new ItemModel("Calibrate"));
        list.add(new ItemModel("Test"));

        menuAdapter = new MenuAdapter(this.getActivity(), R.layout.item_menu, list);
        lvMenu.setAdapter(menuAdapter);
        lvMenu.setOnItemClickListener(this);

    }//initList


    public void onStart(){

        super.onStart();

        mainActivity.ariaService.smartwatchManager.readSharedData(SmartwatchManager.DASHBOARD_PATH);

    }//onStart


    public void onDestroyView(){

        super.onDestroyView();

        ariaService.aria.removeListener(this);
        ariaService.aria.getArs().removeListener(this);

        ariaService.smartwatchManager.removeListener(this);

    }//onDestroyView


    private void updateUi(){

        this.getActivity().runOnUiThread(new Runnable(){

            public void run(){

                if(menuSelectedModel != null){
                    menuSelectedModel.setSelected(false);
                }

                if(menuIndex < 4){
                    menuSelectedModel = (ItemModel) lvMenu.getItemAtPosition(menuIndex);
                    menuSelectedModel.setSelected(true);
                }

                menuAdapter.notifyDataSetChanged();
                lvMenu.smoothScrollToPosition(menuIndex);

            }//run

        });

    }//updateUi


    public void onClick(View v){

        if(v.getId() == R.id.b_dashboard_disconnect) onClickDisconnect();

    }//onClick


    public void onItemClick(AdapterView<?> parent, View view, int position, long id){

        changeCurrentMenuIndex(position);
        openCurrentItem();

    }//onItemClick


    public void onClickDisconnect(){

        ariaService.aria.disconnect();

    }//onClickDisconnect


    //actions

    private void changeCurrentMenuIndex(int _newMenuIndex){

        menuIndex = _newMenuIndex;

        DataMap map = new DataMap();
        map.putInt(SmartwatchManager.DASHBOARD_MENU_ITEM, menuIndex);
        mainActivity.ariaService.smartwatchManager.writeSharedData(SmartwatchManager.DASHBOARD_PATH, map);

    }//changeCurrentMenuIndex


    private void openCurrentItem(){

        if(menuIndex == 0){
            mainActivity.changeCurrentView(SmartwatchManager.ROUTER_VIEW_GOPRO);
        }else if(menuIndex == 1){
            mainActivity.changeCurrentView(SmartwatchManager.ROUTER_VIEW_MUSIC);
        }else if(menuIndex == 2){
            mainActivity.changeCurrentView(SmartwatchManager.ROUTER_VIEW_START_CALIBRATION);
        }else if(menuIndex == 3){
            mainActivity.changeCurrentView(SmartwatchManager.ROUTER_VIEW_TEST);
        }

    }//openCurrentItem


    //aria

    public void onDiscoveryStarted(){}//onDiscoveryStarted


    public void onDiscoveryFinished(boolean _found){}//onDiscoveryFinished


    public void onConnected(){}//onConnected


    public void onReady(){}//onReady


    public void onDisconnected(){

        mainActivity.changeCurrentView(SmartwatchManager.ROUTER_VIEW_INTRO);

    }//onDisconnected


    public void onGesturePerformed(int _gesture){}//onGesturePerformed


    public void onBatteryValueUpdated(int _batteryValue){}//onBatteryValueUpdated


    //smartwatch events

    public void onApiConnected(){}


    public void onApiDisconnected(){}


    public void onSharedDataRead(String _path, DataMap _dataMap){

        if(_path.equals(SmartwatchManager.DASHBOARD_PATH) == false) return;

        menuIndex = _dataMap.getInt(SmartwatchManager.DASHBOARD_MENU_ITEM, -1);
        updateUi();

    }//onSharedDataRead


    public void onSharedDataNotFound(String _path){}


    public void onSharedDataChanged(String _path, DataMap _dataMap){

        if(_path.equals(SmartwatchManager.DASHBOARD_PATH) == false) return;

        menuIndex = _dataMap.getInt(SmartwatchManager.DASHBOARD_MENU_ITEM, -1);
        updateUi();

    }//onSharedDataChanged


}//DashboardFragment
