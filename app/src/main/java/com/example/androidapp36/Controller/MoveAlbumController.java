package com.example.androidapp36.Controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp36.Application.Album;
import com.example.androidapp36.Application.photos;
import com.example.androidapp36.R;

import java.io.File;
import java.util.ArrayList;

public class MoveAlbumController extends AppCompatActivity{
    Album curAlbum;
    ArrayList<Album> albumlist;
    photos photo;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        File dir=getFilesDir();
        curAlbum= (Album) intent.getSerializableExtra("curAlbum");
        photo= (photos) intent.getSerializableExtra("photo");
        File file=new File(dir+"/album.ser");
        albumlist=Album.DeserializeAlbum(file);

        setContentView(R.layout.activity_move_albums);
        ListView Move =(ListView) findViewById(R.id.MoveAlbumList);
        ListAdapter adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, albumlist);
        Move.setAdapter(adapter);
        Move.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(Album i:albumlist){
                    if(i.equals(curAlbum)){
                        for(photos j: i.Photos){
                            if(j.equals(photo)){
                                i.deletephoto(j);
                            }
                        }
                    }
                }
                Album.SerializeAlbum(file, albumlist);
                albumlist.get(position).addphoto(photo,file);
                curAlbum=albumlist.get(position);
                toAlbum();
            }
        });
    }
    public void toAlbum(){
        Intent intent = new Intent(this , AlbumListController.class);
        startActivity(intent);
    }
}
