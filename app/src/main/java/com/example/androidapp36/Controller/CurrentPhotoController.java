package com.example.androidapp36.Controller;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp36.Application.Album;
import com.example.androidapp36.Application.PhotoTag;
import com.example.androidapp36.Application.photos;
import com.example.androidapp36.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class CurrentPhotoController extends AppCompatActivity {
    Uri uri;
    ArrayList<Uri> urilist = new ArrayList<Uri>();
    ArrayList<Album> Alblist = new ArrayList<Album>();
    photos photo;
    Album curAlbum;
    File file=null;
    File photofile=null;
    public ArrayList<Album> album;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent=getIntent();
        ArrayList<String> uris=(ArrayList<String>) intent.getSerializableExtra("Arraylist");

        int currentPhoto = 0;
        for(String i:uris){
            urilist.add(Uri.parse(i));
        }

        String u= (String) intent.getSerializableExtra("clicked_item");

        File dir=this.getFilesDir();
        file=new File(dir+"/album.ser");
        photofile=new File(dir+"photo.ser");
        try {
            file.createNewFile();
        }catch(IOException e){
        }

        uri=Uri.parse(u);
        int position = -1;
        curAlbum = (Album) intent.getSerializableExtra("curAlbum");
        int newPosition = intent.getIntExtra("Position", position);

        setContentView(R.layout.activity_current_photo);
        ImageView image = (ImageView) findViewById(R.id.imageView2);
        image.setImageURI(uri);
        TextView tagtext = (TextView) findViewById(R.id.tagdisplay);
        String tag = "";

        for(photos i : curAlbum.Photos){
            if(i.getlocation().equals(uri)) {
                boolean person = false;
                boolean location = false;
                for(PhotoTag j : i.tags){
                    if(j.tagname.equals("Person") && !person){
                        person = true;
                        String beginning = "Person: " + j.tagval;
                        tag = tag + "" + beginning;
                    }
                    else if(j.tagname.equals("Person")){
                        String thisone = ", " + j.tagval;
                        tag = tag + "" + thisone;
                    }
                }
                for (PhotoTag j : i.tags){
                    if(j.tagname.equals("Location") && !location){
                        location = true;
                        if(person){
                            tag = tag + "\n";
                        }
                        String beginning = "Location: " + j.tagval;
                        tag = tag + "" + beginning;
                    }
                    else if(j.tagname.equals("Location")){
                        String thisone = ", " + j.tagval;
                        tag = tag + "" + thisone;
                    }
                }
            }
        }
        tagtext.setText(tag);
        Button Move = (Button) findViewById(R.id.movefolder);
        Move.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                toMove();
            }
        });
        Button Back = (Button) findViewById(R.id.Photoback);
        Back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                PhotoList();
            }
        });

        Button EditTags = (Button) findViewById(R.id.editTags);
        EditTags.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                for(Uri i:urilist){
                    uris.add(i.toString());
                }
                TagsPage(uris,newPosition);
            }
        });
        Button Next= (Button) findViewById(R.id.Next);
        Next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int p = newPosition+2;
                if(p>urilist.size()){
                    return;
                }
                long i = urilist.stream().filter(x -> x.equals(uri)).count();
                int k=(int) i;
                uri = urilist.get(newPosition+1);

                ImageView image=(ImageView) findViewById(R.id.imageView2);
                image.setImageURI(uri);
                Intent intent=getIntent();
                ArrayList<String> uris=new ArrayList<String>();
                for(Uri j:urilist){
                    uris.add(j.toString());
                }
                String urij=uri.toString();
                intent.putExtra("curAlbum", curAlbum);
                intent.putExtra("Position" , newPosition+1);
                intent.putExtra("clicked_item", uri.toString());
                intent.putExtra("Arraylist", uris);
                startActivity(intent);
            }
        });
        Button prev= (Button) findViewById(R.id.Prev);
        prev.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int p = newPosition-1;
                if(p<0){
                    return;
                }
                long i = urilist.stream().filter(x -> x.equals(uri)).count();
                int k=(int)i;

                uri = urilist.get(newPosition-1);

                ImageView image=(ImageView) findViewById(R.id.imageView2);
                image.setImageURI(uri);
                Intent intent=getIntent();
                ArrayList<String> uris=new ArrayList<String>();
                for(Uri j:urilist){
                    uris.add(j.toString());
                }
                String urij=uri.toString();
                intent.putExtra("curAlbum", curAlbum);
                intent.putExtra("Position" , newPosition-1);
                intent.putExtra("clicked_item", uri.toString());
                intent.putExtra("Arraylist", uris);
                startActivity(intent);
            }
        });

        //Delete photo
        Button Delete = findViewById(R.id.DeletePhoto);

        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    deletePhoto(newPosition, view);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                PhotoList();
            }
        });
    }

    public void deletePhoto(int position, View view) throws IOException {
        if (curAlbum == null) {
            return;
        }

        ArrayList<Album> albumList = Album.DeserializeAlbum(file);

        Album albumToDelete = albumList.stream()
                .filter(album -> album.equals(curAlbum))
                .findFirst()
                .orElse(null);

        if (albumToDelete != null) {
            albumToDelete.Photos.removeIf(photo -> Uri.parse(photo.location).equals(uri));
            curAlbum.Photos.removeIf(photo -> Uri.parse(photo.location).equals(uri));
            Album.SerializeAlbum(file, albumList);

            PhotoList();

            ContentResolver contentResolver = getContentResolver();
            contentResolver.delete(uri, null, null);
        }
    }

    public void loadAlbums() throws IOException {

        File dir = file;
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
        ListAdapter adapter = new ArrayAdapter<Album>(this, R.layout.list_item, albumlist);
        ListView AlbumsList = (ListView) findViewById(R.id.AlbumList);
        AlbumsList.setAdapter(adapter);
    }
    public void updatelist(){
        ListView Albumlist= (ListView) findViewById(R.id.AlbumList);
        ArrayAdapter<Album> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, Alblist);
        Albumlist.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void PhotoList(){
        Intent intent = new Intent(this , PhotoListController.class);
        intent.putExtra("curAlbum", curAlbum);
        startActivity(intent);
    }
    public void toMove(){
        Intent intent = new Intent(this , MoveAlbumController.class);
        intent.putExtra("curAlbum", curAlbum);
        for(photos i:curAlbum.Photos){
            if(i.getlocation().equals(uri)) {
                intent.putExtra("photo", i);
            }
        }
        startActivity(intent);
    }
    public void TagsPage( ArrayList<String> uris, int position){
        Intent intent = new Intent(this , TagController.class);
        photos photo;
        for(photos i:curAlbum.Photos){
            if(i.getlocation().equals(uri)){
                intent.putExtra("photo", i);
                break;
            }
        }
        String urij=uri.toString();
        intent.putExtra("curAlbum", curAlbum);
        intent.putExtra("file", file);
        intent.putExtra("Position", position);
        intent.putExtra("clicked_item", urij);
        intent.putExtra("Arraylist", uris);
        startActivity(intent);
    }
}