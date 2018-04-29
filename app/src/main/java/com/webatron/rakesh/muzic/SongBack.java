package com.webatron.rakesh.muzic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.renderscript.RenderScript;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SongBack extends Service {

    private MediaPlayer player;
    String path,command,position;
    double starttime = 0;
    double stoptime = 0;
    int forwardtime = 5000;
    int backwardtime = 5000;
    SeekBar bar;
    int pathint,currentpos;
    List<String> uri;
    int mstartMode;
    IBinder mBinder;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean mAllowRebind;
    TextView starttext,finishtext;
    Handler myHandler = new Handler();

    public SongBack() {

    }

    @Override
    public IBinder onBind(Intent intent) {

        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {

            sharedPreferences = getSharedPreferences("SongDetails",MODE_PRIVATE);
            editor = sharedPreferences.edit();
            Toast.makeText(getApplicationContext(), "ServiceStarted", Toast.LENGTH_SHORT).show();
            uri = intent.getStringArrayListExtra("Path");
            command = intent.getStringExtra("Command");

            path = sharedPreferences.getString("Position",null);
            currentpos = Integer.parseInt(sharedPreferences.getString("CurrentPosition",null));

            player = MediaPlayer.create(getApplicationContext(), Uri.parse(uri.get(Integer.parseInt(path))));
            Log.i("Cureent posito", "" + position);
            playSong();
            showNotification();


       }else if(intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {

            if(player!=null){
                player.stop();
                player.release();
                player =null;
            }

            sharedPreferences = getSharedPreferences("SongDetails",MODE_PRIVATE);
            editor = sharedPreferences.edit();
            Toast.makeText(getApplicationContext(), "ServiceStarted", Toast.LENGTH_SHORT).show();

            command = intent.getStringExtra("Command");

            path = sharedPreferences.getString("Position",null);
            pathint = Integer.parseInt(path);


            uri = intent.getStringArrayListExtra("Path");

            if(pathint == 0){
                pathint=0;
                Toast.makeText(getApplicationContext(),"End of List",Toast.LENGTH_SHORT).show();
            }
            else if(pathint>0) {

                pathint = pathint-1;
            }
            player = MediaPlayer.create(getApplicationContext(), Uri.parse(uri.get(pathint)));


            editor.putString("Position", String.valueOf(pathint));
            editor.apply();

            player.start();
            showNotification();

        } else if (intent.getAction().equals(Constants.ACTION.PLAYPAUSE_ACTION)) {

            if(player != null){
                if(player.isPlaying()){
                    player.pause();
                    currentpos = player.getCurrentPosition();
                    showNotification();

                }else{
                    sharedPreferences = getSharedPreferences("SongDetails",MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    Toast.makeText(getApplicationContext(), "ServiceStarted", Toast.LENGTH_SHORT).show();
                    uri = intent.getStringArrayListExtra("Path");
                    command = intent.getStringExtra("Command");

                    path = sharedPreferences.getString("Position",null);
                    //currentpos = Integer.parseInt(sharedPreferences.getString("CurrentPosition",null));

                    player.start();
                    //player = MediaPlayer.create(getApplicationContext(), Uri.parse(uri.get(Integer.parseInt(path))));
                    Log.i("Cureent posito", "" + position);
                    playSong();
                    showNotification();
                }
            }


        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {

            if(player!=null){
                player.stop();
                player.release();
                player =null;
            }

            sharedPreferences = getSharedPreferences("SongDetails",MODE_PRIVATE);
            editor = sharedPreferences.edit();
            Toast.makeText(getApplicationContext(), "ServiceStarted", Toast.LENGTH_SHORT).show();

            command = intent.getStringExtra("Command");

            path = sharedPreferences.getString("Position",null);
            pathint = Integer.parseInt(path);


            uri = intent.getStringArrayListExtra("Path");



            if(pathint == uri.size()-1){
                pathint = pathint;
            }else if(pathint<(uri.size()-1)){
                pathint = pathint+1;
            }
            player = MediaPlayer.create(getApplicationContext(), Uri.parse(uri.get(pathint)));


            editor.putString("Position", String.valueOf(pathint));
            editor.apply();

            player.start();
            showNotification();


        } else if (intent.getAction().equals(Constants.ACTION.STOP_ACTION)) {

            if(player!=null){
                player.stop();
                player.release();
                player =null;
            }

            Toast.makeText(getApplicationContext(),"Stopped",Toast.LENGTH_SHORT).show();

        }




        return Service.START_STICKY;
    }


    public void showNotification(){
        int resourceid = player.isPlaying()? R.drawable.ic_action_pause : R.drawable.ic_action_play;
        String resourceString = player.isPlaying()? "Pause":"Play";
        Intent notificationIntent = new Intent(this, SongsList.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);



        Intent previousIntent = new Intent(this, SongBack.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        previousIntent.putStringArrayListExtra("Path",(ArrayList<String>)uri);
        previousIntent.putExtra("Position",path);
        currentpos = player.getCurrentPosition();
        previousIntent.putExtra("CurrentPosition",currentpos);

        previousIntent.putExtra("Command","Play");
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent nextIntent = new Intent(this, SongBack.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        nextIntent.putStringArrayListExtra("Path",(ArrayList<String>)uri);
        nextIntent.putExtra("Position",path);
        currentpos = player.getCurrentPosition();
        nextIntent.putExtra("CurrentPosition",currentpos);

        nextIntent.putExtra("Command","Play");
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        NotificationCompat.Action prev = new NotificationCompat.Action.Builder(R.drawable.ic_action_prev,"Prev",ppreviousIntent).build();
        android.app.Notification notification = new android.support.v4.app.NotificationCompat.Builder(this)
                .setContentTitle("MuZic")
                .setContentText("MUzic")
                .setShowWhen(true)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_audiotrack_black_24dp)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_action_prev,"Prev",
                        ppreviousIntent)
                .addAction(resourceid, resourceString,
                       retrievePlaybackAction(Constants.ACTION.PLAYPAUSE_ACTION))
                .addAction(R.drawable.ic_action_next, "Next",
                        pnextIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,notification);

        if(resourceString == "Play")
        {
            stopForeground(true);

        }
    }

    private final PendingIntent retrievePlaybackAction(final String action) {
        Intent stopIntent = new Intent(this, SongBack.class);
        stopIntent.setAction(action);

        return PendingIntent.getService(this, 0, stopIntent, 0);
    }


    public void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
        stopForeground(true);
    }


    public void pause(){
    player.pause();

    }


    public void playSong(){

        //play.setVisibility(View.INVISIBLE);
        //pause.setVisibility(View.VISIBLE);
        player.seekTo(currentpos);
        player.start();


        stoptime = player.getDuration();
        starttime = player.getCurrentPosition();

       // bar.setMax((int)stoptime);


        //finishtext.setText(String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes((long)stoptime),
          //      TimeUnit.MILLISECONDS.toSeconds((long)stoptime) -
            //            TimeUnit.MINUTES.toSeconds((TimeUnit.MILLISECONDS.toMinutes((long)stoptime)))));

        //starttext.setText(String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes((long)starttime),
//                TimeUnit.MILLISECONDS.toSeconds((long)starttime) -
  //                      TimeUnit.MINUTES.toSeconds((TimeUnit.MILLISECONDS.toMinutes((long)starttime)))));

//        bar.setProgress((int)starttime);
    //    myHandler.postDelayed(UpdateSongTime,100);
    }




}
