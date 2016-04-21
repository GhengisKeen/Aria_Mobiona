package com.deus_tech.aria.dashboard;


import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.deus_tech.aria.MainActivity;
import com.deus_tech.aria.R;

import java.util.List;


public class MenuAdapter extends ArrayAdapter<AppModel>{


    public MenuAdapter(Context _context, int _resourceId, List<AppModel> _objects){

        super(_context, _resourceId, _objects);

    }//constructor


    public View getView(int _position, View _view, ViewGroup _parent){

        AppModel item = getItem(_position);

        ViewHolder holder;

        if(_view == null){

            holder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());

            MainActivity main = (MainActivity)getContext();

            if(main.isRound){
                _view = inflater.inflate(R.layout.item_menu_round, _parent, false);
            }else{
                _view = inflater.inflate(R.layout.item_menu_rect, _parent, false);
            }

            Typeface mainFont = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.main_font));
            holder.textView = (TextView) _view.findViewById(R.id.tv_item_label);
            holder.textView.setTypeface(mainFont);

            holder.imageView = (ImageView) _view.findViewById(R.id.iv_item_icon);

            _view.setTag(holder);

        }else{

            holder = (ViewHolder) _view.getTag();

        }

        holder.textView.setText(item.getName());
        if(item.getIcon() != null){
            holder.imageView.setImageDrawable(item.getIcon());
        }else{
            holder.imageView.setImageResource(android.R.color.transparent);
        }

        if(item.isSelected()){
            _view.setBackgroundResource(R.color.bg2);
        }else{
            _view.setBackgroundResource(R.color.bg1);
        }

        return _view;

    }//getView


    private static class ViewHolder{

        TextView textView;
        ImageView imageView;

    }//ViewHolder


}//MenuAdapter