package com.deus_tech.aria.gopro;


public interface GoProListener{


    public void onGoProNotFound();

    public void onGoProConnected();

    public void onGoProDisconnected();

    public void onGoProActionStarted(int _action);

    public void onGoProActionDone(int _action);

    public void onGoProActionError(int _action);


}//GoProListener