package com.webatron.rakesh.muzic;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    ImageButton play,pause,next,previous;
    private MediaPlayer player;
    String path;
    double starttime = 0;
    double stoptime = 0;
    int currentpos;
    int forwardtime = 5000;
    int backwardtime = 5000;
    SeekBar bar;
    int pathint;
    boolean doublebacktoexitClickedonce = false;
    List<String> uri;
    TextView starttext,finishtext;
    Handler myHandler = new Handler();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static int oneTimeonly = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = (ImageButton) findViewById(R.id.play);
        pause = (ImageButton) findViewById(R.id.pause);
        next = (ImageButton) findViewById(R.id.next);
        previous = (ImageButton) findViewById(R.id.previous);

        starttext = (TextView)findViewById(R.id.starttime);
        finishtext = (TextView)findViewById(R.id.finishtime);

        Intent serviceintent = new Intent(MainActivity.this,SongBack.class);
        stopService(serviceintent);

        uri = new ArrayList<>();
        Intent intent = getIntent();
        path = intent.getStringExtra("Position");
        uri = intent.getStringArrayListExtra("Path");
        bar = (SeekBar) findViewById(R.id.seekbar);

        pathint = Integer.parseInt(path);
        Log.i("length",""+uri.size());
        Log.i("Position",path);
        //bar.setProgress(0);
        //player.reset();
        player = MediaPlayer.create(MainActivity.this, Uri.parse(uri.get(Integer.parseInt(path))));

        playSong();

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if(fromUser)
                player.seekTo(progress);

                currentpos=progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    player.stop();
                    player.release();
                    pathint = pathint+1;
                    player = MediaPlayer.create(MainActivity.this, Uri.parse(uri.get(pathint)));
                    playSong();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
                player.release();
                pathint = pathint-1;
                player = MediaPlayer.create(MainActivity.this, Uri.parse(uri.get(pathint)));
                playSong();
            }
        });


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                playSong();
            }
        });



        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.INVISIBLE);
                player.pause();

            }
        });

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        //player.stop();
        /*Intent serviceintent = new Intent(MainActivity.this,SongBack.class);
        stopService(serviceintent);
        Intent intent = new Intent(MainActivity.this,SongsList.class);
        startActivity(intent);
        finish();*/
        if(doublebacktoexitClickedonce){
            super.onBackPressed();
            player.stop();
            Intent serviceintent = new Intent(MainActivity.this,SongBack.class);
            stopService(serviceintent);
            Intent intent = new Intent(MainActivity.this,SongsList.class);
            startActivity(intent);
            finish();
            return;
        }
        this.doublebacktoexitClickedonce = true;
        Toast.makeText(MainActivity.this,"Click again to exit",Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doublebacktoexitClickedonce = false;
            }
        },2000);

    }

    public void playSong(){

        play.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.VISIBLE);

        player.start();

        stoptime = player.getDuration();
        starttime = player.getCurrentPosition();

        bar.setMax((int)stoptime);


        finishtext.setText(String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes((long)stoptime),
                TimeUnit.MILLISECONDS.toSeconds((long)stoptime) -
                        TimeUnit.MINUTES.toSeconds((TimeUnit.MILLISECONDS.toMinutes((long)stoptime)))));

        starttext.setText(String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes((long)starttime),
                TimeUnit.MILLISECONDS.toSeconds((long)starttime) -
                        TimeUnit.MINUTES.toSeconds((TimeUnit.MILLISECONDS.toMinutes((long)starttime)))));

        bar.setProgress((int)starttime);
        myHandler.postDelayed(UpdateSongTime,100);
    }


    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            starttime = player.getCurrentPosition();
            bar.setProgress((int)starttime);
            starttext.setText(String.format("%02d : %02d", TimeUnit.MILLISECONDS.toMinutes((long)starttime),
                    TimeUnit.MILLISECONDS.toSeconds((long)starttime) -
                            TimeUnit.MINUTES.toSeconds((TimeUnit.MILLISECONDS.toMinutes((long)starttime)))));
            myHandler.postDelayed(this,100);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        player.stop();

        sharedPreferences = getSharedPreferences("SongDetails",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //editor.putStringSet("path",(Set<String>)uri);
        editor.putString("Position",path);
        editor.putString("CurrentPosition", String.valueOf(currentpos));
        editor.apply();

        Intent serviceintent = new Intent(MainActivity.this,SongBack.class);
        serviceintent.putStringArrayListExtra("Path",(ArrayList<String>)uri);
        serviceintent.putExtra("Position",path);
        serviceintent.putExtra("CurrentPosition",currentpos);
        //Bundle b = new Bundle();
        //b.putDouble("current",player.getCurrentPosition());
        //serviceintent.putExtras(b);
        serviceintent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        serviceintent.putExtra("Command","Play");
        startService(serviceintent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent serviceintent = new Intent(MainActivity.this,SongBack.class);
        stopService(serviceintent);
        if(player.isPlaying()){
            Toast.makeText(MainActivity.this,"Restart",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Intent serviceintent = new Intent(MainActivity.this,SongBack.class);
        stopService(serviceintent);
    }
}
