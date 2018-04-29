package com.webatron.rakesh.muzic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rakesh on 5/2/18.
 */

public class SongAdaptor extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Songs> songs;
    List<String>uri;

    public SongAdaptor(Context context, List<Songs> songs,List<String>uri) {
        this.context = context;
        this.songs = songs;
        this.uri = uri;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.song_layout,parent,false);


        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Songs song = songs.get(position);
        ViewHolder hold = (ViewHolder)holder;
        hold.songname.setText(song.getName());
        hold.artist.setText(song.getArtist());
    }


    @Override
    public int getItemCount() {
        return songs.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView songname,artist;
        public ViewHolder(View itemView) {
            super(itemView);

            songname = (TextView)itemView.findViewById(R.id.songname);
            artist = (TextView)itemView.findViewById(R.id.artist);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Songs song = songs.get(getPosition());
            Toast.makeText(context,"Song: "+song.getName(),Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context,MainActivity.class);
            intent.putExtra("Position",String.valueOf(getPosition()));
            intent.putStringArrayListExtra("Path",(ArrayList<String>) uri);
            context.startActivity(intent);
            ((Activity)context).finish();

        }
    }
}
