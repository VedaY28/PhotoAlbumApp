package com.example.androidapp36.Controller;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp36.Application.Album;
import com.example.androidapp36.Application.User;
import com.example.androidapp36.Application.photos;
import com.example.androidapp36.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class AlbumListController extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 123;
    File file=null;
    File photofile=null;
    ArrayList<Album> Alblist=new ArrayList<Album>();
    Album curAlbum=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (shouldAskPermissions()) {
            askPermissions();
        }
        ListView AlbumsList = findViewById(R.id.Albums);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_list);
        TextView currName = findViewById(R.id.Name);
        updatelist();
        Button Add = (Button) findViewById(R.id.AddAlbum);
        Button Edit = (Button) findViewById(R.id.EditAlbum);
        File dir=this.getFilesDir();
        file=new File(dir+"/album.ser");
        photofile=new File(dir+"photo.ser");
        try {
            file.createNewFile();
        }catch(IOException e){
        }
        EditText AlbumName =  ( EditText) findViewById((R.id.AlbumName));
//
        //TEST
        Button edit = (Button) findViewById(R.id.EditAlbum);
        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TextView name= (TextView) findViewById(R.id.AlbumName);
                String s=name.getText().toString();
                try {
                    editAlbum(curAlbum, s);
                }catch (IOException e){
                };
                updatelist();
            }
        });
        Button Delete = (Button) findViewById(R.id.Delete);
        Delete.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                try {
                    delete(v.getContext());
                }catch(IOException e){}
                updatelist();
            }
            });
        Add.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){
                if(AlbumName.length() == 0){
                    AlbumName.setError("Enter Username");
                }
                else {
                    try{
                        File loc=new File("album.ser");
                        ArrayList<Album> allist=DeserializeAlbum(file);
                        System.out.println("file name " + file.getName());
                        Album al=new Album(AlbumName.getText().toString());
                        int count=0;
                        for(int i=0; i<allist.size(); i++){
                            if(allist.get(i).equals(al)){
                                count=1;
                            }
                        }
                        if(count==0){
                            allist.add(al);
                        }
                        SerializeAlbum(file, allist);
                        Alblist=allist;
                        loadAlbums();
                        updatelist();
                    }catch(IOException e){
                    };
                }
                updatelist();
            }
        });
        Alblist=DeserializeAlbum(file);
        for(Album i:Alblist){
        }
        updatelist();

        ListView Albumlist= (ListView) findViewById(R.id.AlbumList);
        ArrayAdapter<Album> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Alblist);
        Albumlist.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Albumlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    selectAlbum(position);

                }catch (IOException e){}
            }
        });

    }
    public void selectAlbum(int position)throws IOException{
        Album Album=Alblist.get(position);
        if(Album == null ){
            return;
        }
        if(Album==curAlbum){
            String curName = curAlbum.getName();
            System.out.println(curName);
            gotophotolist(curName);
        }
        else{
            curAlbum=Album;
            EditText AlbumName =  ( EditText) findViewById((R.id.AlbumName));
            AlbumName.setText(curAlbum.getName());
        }

    }
    public void delete(Context context) throws IOException {
        Album album = curAlbum;
        if (album == null) {
            return;
        }
        File dir = file;
        dir.createNewFile();
        ArrayList<Album> albumlist = new ArrayList<Album>();
        ContentResolver contentResolver = getContentResolver();
        try {
            FileInputStream fileIn = new FileInputStream(dir);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            try {
                albumlist = (ArrayList<Album>) in.readObject();
            } catch (ClassNotFoundException g) {
            }
            in.close();
            fileIn.close();
            for (int i = 0; i < albumlist.size(); i++) {
                if (albumlist.get(i).equals(album)) {
                    album.deleteAll();
                    albumlist.remove(i);
                }
            }
            Album.SerializeAlbum(dir, albumlist);
            Alblist = albumlist;

            // Update the photo file to remove the album from each photo that belongs to it
            File p = photofile;
            ArrayList<photos> snaps = photos.deserializePhoto(p);
            for (int i = 0; i < snaps.size(); i++) {
                for (int j = 0; j < snaps.get(i).album.size(); j++) {
                    if (snaps.get(i).album.get(j).equals(album)) {
                        snaps.get(i).setName("");
                        snaps.get(i).album.remove(j);
                    }
                }
            }
            photos.SerializePhoto(snaps);
            loadAlbums();
            updatelist();
        } catch (IOException e) {
        }
    }
    public void updatelist(){
        ListView Albumlist= (ListView) findViewById(R.id.AlbumList);
        ArrayAdapter<Album> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Alblist);
        Albumlist.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        Albumlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    selectAlbum(position);
                }catch (IOException e){}
            }
        });
    }
    public void loadAlbums() throws IOException {
        User loggedin = null;
        Album curAlbum;
        ListView AlbumsList = (ListView) findViewById(R.id.AlbumList);
        File dir=new File("data/album.ser");
        System.out.println(dir);

        dir.createNewFile();
        ArrayList<Album> albumlist=new ArrayList<Album>();
        try{
            FileInputStream fileIn = new FileInputStream(dir);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            try{
                albumlist= (ArrayList<Album>) in.readObject();
            }
            catch(ClassNotFoundException g){
            }
            in.close();
            fileIn.close();
        }catch(IOException e){};
        List<Album> list = new ArrayList<>();
        ListAdapter adapter = new ArrayAdapter<>(this, R.layout.list_item, list);
        AlbumsList.setAdapter(adapter);
    }

    public void PhotoListPage(){
        Intent intent = new Intent(this , PhotoListController.class);
        startActivity(intent);
    }
    protected boolean shouldAskPermissions() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.MANAGE_EXTERNAL_STORAGE"

        };
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }
    public static void SerializeAlbum(File loc, ArrayList<Album> userlist){
        try {
            FileOutputStream fileOut = new FileOutputStream(loc);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(userlist);
            out.close();
            fileOut.close();
        }
        catch(IOException e){
        }
        return;
    }
    public static ArrayList<Album> DeserializeAlbum(File loc){
        ArrayList<Album> albumlist=new ArrayList<Album>();
        try{
            FileInputStream fileIn = new FileInputStream(loc);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            try{
                albumlist= (ArrayList<Album>) in.readObject();
            }
            catch(ClassNotFoundException i){
            }
            in.close();
            fileIn.close();
        }catch(IOException d){}
        return albumlist;
    }
    public void gotophotolist(String name){

        Intent intent = new Intent(this , PhotoListController.class);
        intent.putExtra(Intent.EXTRA_TEXT, name);
        intent.putExtra("curAlbum",curAlbum);
        startActivity(intent);
    }

    public void editAlbum(Album album, String name) throws IOException {
        if(album == null){
            return;
        }
        File dir=file;
        dir.createNewFile();
        ArrayList<Album> albumlist = new ArrayList<Album>();
        try{
            FileInputStream fileIn = new FileInputStream(dir);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            try{
                albumlist= (ArrayList<Album>) in.readObject();
            }
            catch(ClassNotFoundException g){
                System.out.print("ok");
            }
            in.close();
            fileIn.close();
            for(int i=0; i<albumlist.size(); i++){
                if(albumlist.get(i).equals(album) ){//== selectedIndex){
                    albumlist.get(i).setAlbumName(name);
                }
            }
            Album.SerializeAlbum(dir, albumlist);
            Alblist=albumlist;
            updatelist();
            loadAlbums();
        }catch(IOException e){};
    }
}