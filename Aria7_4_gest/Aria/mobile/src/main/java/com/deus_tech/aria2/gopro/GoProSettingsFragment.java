package com.deus_tech.aria2.gopro;


import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.deus_tech.aria2.MainActivity;
import com.deus_tech.aria2.R;
import com.deus_tech.aria2.util.Util;

public class GoProSettingsFragment extends Fragment implements View.OnKeyListener, View.OnFocusChangeListener, TextView.OnEditorActionListener{


    private MainActivity mainActivity;

    private EditText editTextGoProSsid;
    private EditText editTextGoProPassword;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        init();
        initUi(rootView);

        return rootView;

    }//onCreateView


    private void init(){

        mainActivity = ((MainActivity) getActivity());

    }//init


    private void initUi(View _rootView){

        _rootView.setFocusableInTouchMode(true);
        _rootView.requestFocus();
        _rootView.setOnKeyListener(this);

        Typeface mainFont = Typeface.createFromAsset(getActivity().getAssets(), getString(R.string.main_font));

        TextView tvTitle = (TextView) _rootView.findViewById(R.id.tv_gopro_set_title);
        tvTitle.setTypeface(mainFont);

        TextView tvSSid = (TextView) _rootView.findViewById(R.id.tv_gopro_set_ssid);
        tvSSid.setTypeface(mainFont);

        editTextGoProSsid = (EditText)_rootView.findViewById(R.id.et_gopro_set_ssid);
        editTextGoProSsid.setOnFocusChangeListener(this);
        editTextGoProSsid.setOnEditorActionListener(this);
        editTextGoProSsid.setText(Util.loadStringPreference(getActivity(), R.string.gopro_ssid));
        editTextGoProSsid.setTypeface(mainFont);

        TextView tvPassword = (TextView) _rootView.findViewById(R.id.tv_gopro_set_password);
        tvPassword.setTypeface(mainFont);

        editTextGoProPassword = (EditText)_rootView.findViewById(R.id.et_gopro_set_password);
        editTextGoProPassword.setOnFocusChangeListener(this);
        editTextGoProPassword.setOnEditorActionListener(this);
        editTextGoProPassword.setText(Util.loadStringPreference(getActivity(), R.string.gopro_password));
        editTextGoProPassword.setTypeface(mainFont);

    }//initUi


    public void onFocusChange(View _view, boolean _hasFocus){

        if( (_view == editTextGoProSsid || _view == editTextGoProPassword) && _hasFocus == false ){

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(_view.getWindowToken(), 0);

            //save values
            if(_view == editTextGoProSsid){
                Util.saveStringPreference(getActivity(), R.string.gopro_ssid, editTextGoProSsid.getText().toString());
            }else if(_view == editTextGoProPassword){
                Util.saveStringPreference(getActivity(), R.string.gopro_password, editTextGoProPassword.getText().toString());
            }

        }

    }//onFocusChange


    public boolean onEditorAction(TextView _textView, int _actionId, KeyEvent _event){

        if(_actionId == EditorInfo.IME_ACTION_DONE){

            _textView.clearFocus();

        }

        return true;

    }//onEditorAction


    public boolean onKey(View v, int keyCode, KeyEvent event){

        if(event.getAction()!=KeyEvent.ACTION_DOWN) return true;

        if(keyCode == KeyEvent.KEYCODE_BACK){

            mainActivity.showFragment(new GoProFragment(), false);
            return true;

        }else{

            return false;

        }

    }//onKey


}//GoProSettingsFragment
