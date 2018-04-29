package com.webatron.rakesh.muzic;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SongsList extends AppCompatActivity {

    List<Songs> songs;
    List<String> uri;
    RecyclerView songslist;
    SongAdaptor adaptor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs_list);

        songslist = (RecyclerView)findViewById(R.id.songlist);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(SongsList.this);

        songslist.setLayoutManager(manager);
        songs = new ArrayList<>();
        uri = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri songuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor songcursor = contentResolver.query(songuri,null,null,null,null);
        if( songcursor != null && songcursor.moveToFirst()){
            int path = songcursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int tittle = songcursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artist = songcursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            do{
                String filepath = songcursor.getString(path);
                String songtittle = songcursor.getString(tittle);
                String artistname = songcursor.getString(artist);
                Songs song = new Songs(songtittle,filepath,artistname);
                songs.add(song);
                uri.add(song.getPath());

            }while (songcursor.moveToNext());
        }

        adaptor = new SongAdaptor(SongsList.this,songs,uri);
        songslist.setAdapter(adaptor);



    }
}
