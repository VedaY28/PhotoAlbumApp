package com.example.androidapp36.Controller;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp36.Application.Album;
import com.example.androidapp36.Application.PhotoTag;
import com.example.androidapp36.Application.photos;
import com.example.androidapp36.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class TagController extends AppCompatActivity {
    Button Back;
    Button Add;
    Button Delete;
    Album curAlbum = null;
    File file;
    photos photo;
    PhotoTag tag;
    File photofile;
    EditText tagname;
    EditText tagVal;
    ArrayList<PhotoTag> taglist;

    Map<String, List<String>> tagTypeMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_controller);
        Back = findViewById(R.id.TagBack);

        Intent intent = getIntent();
        ArrayList<String> uris=(ArrayList<String>) intent.getSerializableExtra("Arraylist");

        // Add default tags "Person" and "Location"
        tagTypeMap.put("Person", new ArrayList<>());
        tagTypeMap.put("Location", new ArrayList<>());

        EditText tagname = findViewById(R.id.TagName);
        EditText tagVal = findViewById(R.id.TagVal1);
        ListView Tags = findViewById(R.id.TagsList);
        int position = -1;

        photo=(photos) intent.getSerializableExtra("photo");
        int newPosition= (int) intent.getSerializableExtra("Position");

        String urij=(String) intent.getSerializableExtra("clicked_item");

        Uri clickedItem = Uri.parse(urij);
        curAlbum= (Album) intent.getSerializableExtra("curAlbum");

        File dir=this.getFilesDir();
        Album album= curAlbum;

        for(PhotoTag i : photo.tags){
            if(i.tagname.equals("Person")){
                tagTypeMap.get("Person").add(i.getVal());
            }
            else if(i.tagname.equals("Location")){
                tagTypeMap.get("Location").add(i.getVal());
            }
        }

        file=new File(dir+"/album.ser");
        photofile=new File(dir+"photo.ser");
        try {
            file.createNewFile();
        }catch(IOException e){
        }

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CurrentPhotoPage(clickedItem,uris, newPosition);
            }
        });
        updatelist();

        Tags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               tag=photo.tags.get(position);
               EditText tagname = findViewById(R.id.TagName);
               EditText tagVal = findViewById(R.id.TagVal1);
               tagname.setText(tag.tagname);
               tagVal.setText(tag.tagval);
           }
        });

        Add = findViewById(R.id.AddTag);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addtag();
            }
        });

        //when deleting a tag
        Delete = findViewById(R.id.DeleteTag);
        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteTag();
            }
        });

        Tags.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectTag(position);
            }
        });

    }

    private void selectTag(int position) {
        System.out.println(position);
        PhotoTag Tag = photo.tags.get(position);

        if (Tag == null || Tag.getName() == null || Tag.getVal() == null) {
            return;
        }else{
            EditText Tagname = (EditText) findViewById((R.id.TagName));
            EditText TagVal = (EditText) findViewById((R.id.TagVal1));
            Tagname.setText(Tag.getName().toString());
            TagVal.setText(Tag.getVal().toString());
        }
    }

    public void addtag() {
        EditText tagname = findViewById(R.id.TagName);
        EditText tagVal = findViewById(R.id.TagVal1);

        String type = tagname.getText().toString();
        String value = tagVal.getText().toString();

//        if (tagname == null || tagVal == null || tagname.length() == 0 || tagVal.length() == 0)
        if (tagname.length() == 0 || tagVal.length() == 0) {
            tagname.setError("Please select a tag or create one");
            tagVal.setError("Please select a tag or create one");
            return;
        }

        if (tagTypeMap.containsKey(type)) {

            if (!tagTypeMap.get(type).contains(value)) {
                tagTypeMap.get(type).add(value);

                int count = 0;
                if (tagname.getText().toString().toLowerCase().equals("location") || tagname.getText().toString().toLowerCase().equals("person")) {
                    PhotoTag tag = new PhotoTag(tagname.getText().toString(), tagVal.getText().toString());

                    if (!photo.tags.contains(tag)) {
                        photo.tags.add(tag);

                        ArrayList<Album> allist = Album.DeserializeAlbum(file);
                        for (Album i : allist) {
                            if (i.equals(curAlbum)) {
                                for (photos j : i.Photos) {
                                    if (j.equals(photo)) {
                                        if (!curAlbum.Photos.get(i.Photos.indexOf(j)).tags.contains(tag)) {
                                            j.tags.add(tag);
                                            curAlbum.Photos.get(i.Photos.indexOf(j)).tags.add(tag);
                                        }
                                    }
                                }
                            }
                        }
                        Album.SerializeAlbum(file, allist);
                        updatelist();
                    }
                }
            } else {
            }
        }
    }


    public void DeleteTag() {
        tagname = findViewById(R.id.TagName);
        tagVal = findViewById(R.id.TagVal1);

        String type = tagname.getText().toString();
        String value = tagVal.getText().toString();

        if (tagname.length() == 0 || tagVal.length() == 0) {
            tagname.setError("Please select a tag or create one");
            tagVal.setError("Please select a tag or create one");
            return;
        }

        PhotoTag tagToDelete = new PhotoTag(tagname.getText().toString(), tagVal.getText().toString());

        if (tagTypeMap.containsKey(type)) {
            if (tagTypeMap.get(type).contains(value)) {

                tagTypeMap.get(type).remove(value);

                ArrayList<Album> allist = Album.DeserializeAlbum(file);
                for (Album i : allist) {
                    if (i.equals(curAlbum)) {
                        for (photos j : i.Photos) {
                            if (j.equals(photo)) {
                                for (int k = 0; k < curAlbum.Photos.get(i.Photos.indexOf(j)).tags.size(); k++) {
                                    if (tagToDelete.equals(curAlbum.Photos.get(i.Photos.indexOf(j)).tags.get(k))) {
                                        j.tags.remove(tagToDelete);
                                        curAlbum.Photos.get(i.Photos.indexOf(j)).tags.remove(k);

                                        Album.SerializeAlbum(file, allist);

                                        updatelist();

                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else{

            }
        }

    }




    public void updatelist(){
            ListView listView = findViewById(R.id.TagsList);

            // Create a list to hold all tags
            List<String> allTags = new ArrayList<>();

            boolean person = false;
            boolean location = false;

            allTags.add("Person: ");
            allTags.add("Location: ");

            if(tagTypeMap.get("Person").size() == 0){
                allTags.set(0, allTags.get(0) + "n\\a");
            }
            else{
                allTags.set(0, allTags.get(0) + tagTypeMap.get("Person").get(0));
                for(int i = 1; i < tagTypeMap.get("Person").size(); i++){
                    allTags.set(0, allTags.get(0) + ", " + tagTypeMap.get("Person").get(i));
                }
            }
            if(tagTypeMap.get("Location").size() == 0){
                allTags.set(1, allTags.get(1) + "n\\a");
            }
            else{
                allTags.set(1, allTags.get(1) + tagTypeMap.get("Location").get(0));
                for(int i = 1; i < tagTypeMap.get("Location").size(); i++){
                    allTags.set(1, allTags.get(1) + ", " + tagTypeMap.get("Location").get(i));
                }
            }


            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, allTags);
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();


    }
    public void CurrentPhotoPage(Uri clickedItem ,ArrayList<String> uris, int position){
        Intent intent = new Intent(this , CurrentPhotoController.class);

        intent.putExtra("curAlbum",curAlbum);
        intent.putExtra("clicked_item", clickedItem.toString());
        intent.putExtra("photo", photo);
        intent.putExtra("file", file);
        intent.putExtra("Position", position);
        intent.putExtra("Arraylist", uris);
        startActivity(intent);
    }
}