package com.deus_tech.aria.music;


import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class MusicManager{


    public final static String SERVICE_CMD = "com.android.music.musicservicecommand";
    public final static String CMD_NAME = "command";

    public static final String CMD_TOGGLE_PAUSE = "togglepause";
    public final static String CMD_PAUSE = "pause";
    public final static String CMD_PREVIOUS = "previous";
    public final static String CMD_NEXT = "next";
    public final static String CMD_STOP = "stop";
    public final static String CMD_PLAY = "play";


    private Context context;


    public MusicManager(Context _context){

        context = _context;

    }//MusicManager


    private void sendBroadcast(String _command){

        Intent i = new Intent(MusicManager.SERVICE_CMD);
        i.putExtra(MusicManager.CMD_NAME, _command);
        context.sendBroadcast(i);

    }//sendBroadcast


    public void play(){

        sendBroadcast(MusicManager.CMD_PLAY);

    }//play


    public void pause(){

        sendBroadcast(MusicManager.CMD_PAUSE);

    }//pause


    public void togglePause(){

        sendBroadcast(MusicManager.CMD_TOGGLE_PAUSE);

    }//togglePause


    public void stop(){

        sendBroadcast(MusicManager.CMD_STOP);

    }//stop


    public void next(){

        sendBroadcast(MusicManager.CMD_NEXT);

    }//next


    public void previous(){

        sendBroadcast(MusicManager.CMD_PREVIOUS);

    }//previous


}//MusicManager
