package com.deus_tech.aria.dashboard;

import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deus_tech.aria.R;

import java.util.ArrayList;

/**
 * Created by user on 5/9/2016.
 */
public class DashBoardAdapter extends WearableListView.Adapter{
    private ArrayList<AppModel>appModels;
    public DashBoardAdapter(ArrayList<AppModel>appModels){
        this.appModels=appModels;
    }
    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.wearable_list_item_layout,parent,false);
        return new DashBoardItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
       if(holder instanceof DashBoardItemViewHolder){
           DashBoardItemViewHolder holder1=(DashBoardItemViewHolder)holder;
           holder1.circledImageView.setImageDrawable(appModels.get(position).getIcon());
           holder1.textView.setText(appModels.get(position).getName());
       }
    }

    @Override
    public int getItemCount() {
        return appModels.size();
    }
    public class DashBoardItemViewHolder extends WearableListView.ViewHolder{
        CircledImageView circledImageView;
        TextView textView;
        public DashBoardItemViewHolder(View itemView) {
            super(itemView);
            circledImageView= (CircledImageView) itemView.findViewById(R.id.circle);
            textView=(TextView)itemView.findViewById(R.id.name);
        }
    }
}
