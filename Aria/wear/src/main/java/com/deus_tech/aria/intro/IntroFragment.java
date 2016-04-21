package com.deus_tech.aria.intro;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deus_tech.aria.MainActivity;
import com.deus_tech.aria.R;
import com.deus_tech.aria.smartphone.SmartphoneManager;


public class IntroFragment extends Fragment implements View.OnClickListener{


    private MainActivity mainActivity;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        mainActivity = (MainActivity) getActivity();

        View rootView = null;

        if(mainActivity.isRound){
            rootView = inflater.inflate(R.layout.fragment_intro_round, container, false);
        }else{
            rootView = inflater.inflate(R.layout.fragment_intro_rect, container, false);
        }

        Typeface mainFont = Typeface.createFromAsset(mainActivity.getAssets(), getString(R.string.main_font));

        TextView tvStatus = (TextView) rootView.findViewById(R.id.tv_intro_status);
        tvStatus.setTypeface(mainFont);
        tvStatus.setOnClickListener(this);

        return rootView;

    }//onCreateView


    public void onDestroyView(){

        super.onDestroyView();

    }//onDestroyView


    public void onClick(View v){

        mainActivity.changeCurrentView(SmartphoneManager.ROUTER_VIEW_DASHBOARD);

    }//onClick


}//IntroFragment
