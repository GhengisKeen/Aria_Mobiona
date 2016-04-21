package com.deus_tech.aria.gopro;


import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

public class GoProRequest extends AsyncTask<String, Void, Boolean>{


    GoProRequestListener listener;


    public void setListener(GoProRequestListener _listener){

        listener = _listener;

    }//setListener


    protected void onPreExecute(){

        super.onPreExecute();

    }//onPreExecute


    protected void onPostExecute(Boolean result){

        super.onPostExecute(result);

    }//onPostExecute


    protected Boolean doInBackground(String... urls){

        try{

            HttpGet request = new HttpGet(urls[0]);
            HttpClient client = new DefaultHttpClient();
            HttpResponse response = client.execute(request);

            int status = response.getStatusLine().getStatusCode();

            if(status == 200){

                if(listener != null){
                    listener.onGoProRequestSucceed(this);
                }

                return true;

            }else{

                if(listener != null){
                    listener.onGoProRequestError(this);
                }

            }


        }catch(IOException e){

            if(listener != null){
                listener.onGoProRequestError(this);
            }
            e.printStackTrace();

        }

        return false;

    }//doInBackground


}//GoProRequestManager
