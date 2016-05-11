package com.deus_tech.aria2.dashboard;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.deus_tech.aria2.MainActivity;
import com.deus_tech.aria2.R;
import com.deus_tech.aria2.smartphone.SmartphoneListener;
import com.deus_tech.aria2.smartphone.SmartphoneManager;
import com.google.android.gms.wearable.DataMap;

import java.util.ArrayList;
import java.util.List;


public class DashboardFragment extends Fragment implements AdapterView.OnItemClickListener, SmartphoneListener{


    private MainActivity mainActivity;

    private ListView lvMenu;
    private MenuAdapter menuAdapter;
    private int menuIndex;
    private AppModel menuSelectedModel;


    //fragment

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mainActivity = (MainActivity) getActivity();
        mainActivity.smartphoneManager.addListener(this);

        View rootView = null;

        if(mainActivity.isRound){
            rootView = inflater.inflate(R.layout.fragment_dashboard_round, container, false);
        }else{
            rootView = inflater.inflate(R.layout.fragment_dashboard_rect, container, false);
        }

        lvMenu = (ListView) rootView.findViewById(R.id.lv_dashboard_menu);
        initList();

        return rootView;

    }//onCreateView


    private void initList(){

        menuIndex = -1;

        ArrayList<AppModel> list = new ArrayList<AppModel>();

        //1. default actions
        list.add(new AppModel("GoPro", null, getResources().getDrawable(R.mipmap.ic_launcher)));
        list.add(new AppModel("Music", null, getResources().getDrawable(R.mipmap.ic_launcher)));
        list.add(new AppModel("Calibrate", null, getResources().getDrawable(R.mipmap.ic_launcher)));
        list.add(new AppModel("Test", null, getResources().getDrawable(R.mipmap.ic_launcher)));

        //2. grab applications
        PackageManager pm = mainActivity.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for(ApplicationInfo packageInfo : packages){

            if(pm.getLaunchIntentForPackage(packageInfo.packageName) != null){

                String label = (String) pm.getApplicationLabel(packageInfo);
                Drawable icon = pm.getApplicationIcon(packageInfo);
                AppModel p = new AppModel(label, packageInfo.packageName, icon);

                list.add(p);

            }

        }

        //3. set adapter
        if(mainActivity.isRound){
            menuAdapter = new MenuAdapter(mainActivity, R.layout.item_menu_round, list);
        }else{
            menuAdapter = new MenuAdapter(mainActivity, R.layout.item_menu_rect, list);
        }

        lvMenu.setAdapter(menuAdapter);

        lvMenu.setOnItemClickListener(this);

    }//initList


    public void onStart(){

        super.onStart();

        mainActivity.smartphoneManager.readSharedData(SmartphoneManager.DASHBOARD_PATH);

    }//onStart


    public void onDestroyView(){

        super.onDestroyView();

        mainActivity.smartphoneManager.removeListener(this);

    }//onDestroyView


    private void updateUi(){

        mainActivity.runOnUiThread(new Runnable(){

            public void run(){

                if(menuSelectedModel != null){
                    menuSelectedModel.setSelected(false);
                }

                if(menuIndex >= 0){
                    menuSelectedModel = (AppModel) lvMenu.getItemAtPosition(menuIndex);
                    menuSelectedModel.setSelected(true);
                }

                menuAdapter.notifyDataSetChanged();
                lvMenu.smoothScrollToPosition(menuIndex);

            }//run

        });

    }//updateUi


    public void onItemClick(AdapterView<?> parent, View view, int position, long id){

        changeCurrentMenuIndex(position);
        openCurrentItem();

    }//onItemClick


    //actions

    private void changeCurrentMenuIndex(int _newMenuIndex){

        menuIndex = _newMenuIndex;

        DataMap map = new DataMap();
        map.putInt(SmartphoneManager.DASHBOARD_MENU_ITEM, menuIndex);
        mainActivity.smartphoneManager.writeSharedData(SmartphoneManager.DASHBOARD_PATH, map);

    }//changeCurrentMenuIndex


    private void openCurrentItem(){

        if(menuIndex == 0){
            mainActivity.changeCurrentView(SmartphoneManager.ROUTER_VIEW_GOPRO);
        }else if(menuIndex == 1){
            mainActivity.changeCurrentView(SmartphoneManager.ROUTER_VIEW_MUSIC);
        }else if(menuIndex == 2){
            mainActivity.changeCurrentView(SmartphoneManager.ROUTER_VIEW_START_CALIBRATION);
        }else if(menuIndex == 3){
            mainActivity.changeCurrentView(SmartphoneManager.ROUTER_VIEW_TEST);
        }else if(menuIndex > 3){
            String appToLaunch = menuAdapter.getItem(menuIndex).getPackageName();
            PackageManager pm = mainActivity.getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(appToLaunch);
            startActivity(intent);
        }

    }//openCurrentItem


    //smartphone events

    public void onApiConnected(){}


    public void onApiDisconnected(){}


    public void onSharedDataRead(String _path, DataMap _dataMap){

        if(_path.equals(SmartphoneManager.DASHBOARD_PATH) == false) return;

        menuIndex = _dataMap.getInt(SmartphoneManager.DASHBOARD_MENU_ITEM, -1);
        updateUi();

    }//onSharedDataRead


    public void onSharedDataNotFound(String _path){}


    public void onSharedDataChanged(String _path, DataMap _dataMap){

        if(_path.equals(SmartphoneManager.DASHBOARD_PATH) == false) return;

        menuIndex = _dataMap.getInt(SmartphoneManager.DASHBOARD_MENU_ITEM, -1);
        updateUi();

    }//onSharedDataChanged


    public void onGestureReceived(String _gesture){

        if(_gesture.equals(SmartphoneManager.ARIA_MESSAGE_UP)){

            if(menuIndex > 0){
                changeCurrentMenuIndex(menuIndex - 1);
            }

        }else if(_gesture.equals(SmartphoneManager.ARIA_MESSAGE_DOWN)){

            if(menuIndex < menuAdapter.getCount()-1){
                changeCurrentMenuIndex(menuIndex + 1);
            }

        }else if(_gesture.equals(SmartphoneManager.ARIA_MESSAGE_ENTER)){

            openCurrentItem();

        }else if(_gesture.equals(SmartphoneManager.ARIA_MESSAGE_HOME)){

            changeCurrentMenuIndex(0);

        }

    }//onGestureReceived


}//DashboardFragment
