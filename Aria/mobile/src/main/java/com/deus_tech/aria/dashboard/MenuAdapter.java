package com.deus_tech.aria.dashboard;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.deus_tech.aria.R;

import java.util.List;


public class MenuAdapter extends ArrayAdapter<ItemModel>{


    public MenuAdapter(Context _context, int _resourceId, List<ItemModel> _objects){

        super(_context, _resourceId, _objects);

    }//constructor


    public View getView(int _position, View _view, ViewGroup _parent){

        ItemModel item = getItem(_position);

        ViewHolder holder;

        if(_view == null){

            holder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            _view = inflater.inflate(R.layout.item_menu, _parent, false);

            Typeface mainFont = Typeface.createFromAsset(getContext().getAssets(), getContext().getString(R.string.main_font));
            holder.textView = (TextView) _view.findViewById(R.id.tv_menu_item);
            holder.textView.setTypeface(mainFont);

            _view.setTag(holder);

        }else{

            holder = (ViewHolder) _view.getTag();

        }

        holder.textView.setText(item.getName());

        if(item.isSelected()){
            holder.textView.setBackgroundResource(R.drawable.bg_item_selected);
            holder.textView.setTextColor(getContext().getResources().getColor(R.color.text_button));
        }else{
            holder.textView.setBackgroundResource(android.R.color.transparent);
            holder.textView.setTextColor(getContext().getResources().getColor(R.color.text));
        }

        return _view;

    }//getView


    private static class ViewHolder{

        TextView textView;

    }//ViewHolder


}//MenuAdapter