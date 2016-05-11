package com.deus_tech.aria2;


import android.content.Intent;

import com.deus_tech.aria2.smartphone.SmartphoneManager;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WearMessageListenerService extends WearableListenerService{


    public void onMessageReceived(MessageEvent messageEvent){

        String path = messageEvent.getPath();
        String text = new String(messageEvent.getData());

        if(path.equals(SmartphoneManager.ARIA_MESSAGE_PATH) && text.equals(SmartphoneManager.ARIA_MESSAGE_HOME)){

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

        }else{

            super.onMessageReceived(messageEvent);

        }

    }//onMessageReceived


}//WearMessageListenerService
