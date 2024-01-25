package com.example.androidapp36.Controller;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp36.Application.Album;
import com.example.androidapp36.Application.PhotoTag;
import com.example.androidapp36.Application.RecyclerAdapter;
import com.example.androidapp36.Application.photos;
import com.example.androidapp36.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

public class PhotoListController extends AppCompatActivity {
    RecyclerView ListOfPhotos;
    Button add;
    TextView textView;
    Album curAlbum;
    ArrayList<photos> photographs = new ArrayList<photos>();
    ArrayList<String> allTags = new ArrayList<>();
    ArrayList<Uri> uri = new ArrayList<>();
    RecyclerAdapter adapter;

    Button Back;
    private static final int Read_Permission = 101;
    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String text = intent.getStringExtra(Intent.EXTRA_TEXT);

        setContentView(R.layout.activity_photo_list);
        TextView CurrName = (TextView) findViewById(R.id.CurrAlbumName);
        CurrName.setText(text);
        textView = findViewById(R.id.totalPhotos);
        curAlbum = (Album) intent.getSerializableExtra("curAlbum");

        File dir = this.getFilesDir();

        Iterator<photos> iterator = curAlbum.Photos.iterator();
        while (iterator.hasNext()) {
            photos i = iterator.next();
            photographs.add(i);
            uri.add(i.getlocation());
        }

        ListOfPhotos = findViewById(R.id.PhotoList);
        add = findViewById(R.id.addPhoto);
        Back = findViewById(R.id.Back);
        adapter = new RecyclerAdapter(uri,this);
        adapter.setAlbum(curAlbum);
        ListOfPhotos.setLayoutManager(new GridLayoutManager(PhotoListController.this,5));
        ListOfPhotos.setAdapter(adapter);

        File file = new File(dir + "/album.ser");
        ArrayList<Album> allist = Album.DeserializeAlbum(file);

        for (Album i : allist) {
            for (photos j : i.Photos) {
                for (PhotoTag k : j.tags) {
                    allTags.add(k.getVal());
                }
            }
        }
        System.out.print(allTags);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, allTags);
        AutoCompleteTextView val1 = findViewById(R.id.TagVal1);
        val1.setAdapter(adapter1);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, allTags);
        AutoCompleteTextView val2 = findViewById(R.id.TagVal2);
        val2.setAdapter(adapter2);

//        MultiAutoCompleteTextView multiAutoCompleteTextView = findViewById(R.id.TagVal1);
//
//        multiAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
//                // Not needed for filtering
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
//                // Filter the adapter based on the current input
//                adapter.getFilter().filter(charSequence);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                // Not needed for filtering
//            }
//        });






        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Read_Permission);
        }

        CheckBox and = findViewById(R.id.andBox);
        CheckBox or = findViewById(R.id.orBox);

        and.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                or.setChecked(false); // Uncheck Option 2
            }
        });

        or.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                and.setChecked(false); // Uncheck Option 1
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.setType("image/*");

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                }
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE);
            }
        });

        Button filter = (Button) findViewById(R.id.Filter);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter();
            }
            });
        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlbumPage();
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                //Uri selectedImage = data.getData();

                if (data.getClipData() != null) {
                    int countOfImages = data.getClipData().getItemCount();
                    for (int i = 0; i < countOfImages; i++) {
                        Uri currentImage = data.getClipData().getItemAt(i).getUri();
                        try {
                            ContentResolver contentResolver = getContentResolver();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "image_name.jpg");
                            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + curAlbum.getName());
                            Uri lri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                            OutputStream outputStream = contentResolver.openOutputStream(lri);
                            InputStream inputStream = contentResolver.openInputStream(currentImage);

                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }

                            inputStream.close();
                            outputStream.close();
                            Uri newUri = Uri.parse(lri.toString());

                            uri.add(newUri);

                            photos photo = new photos(newUri, curAlbum);
                            photographs.add(photo);
                            File dir = this.getFilesDir();
                            File file = new File(dir + "/album.ser");

                            ArrayList<Album> al = AlbumListController.DeserializeAlbum(file);
                            curAlbum.addphoto(photo, file);
                            curAlbum.Photos.add(photo);

                        } catch (IOException | RuntimeException e) {
                            e.printStackTrace();
                            Log.e("YourTag", "Error message", e);
                            Toast.makeText(this, "An error occurred.", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                adapter.setAlbum(curAlbum);
                adapter.notifyDataSetChanged();
                textView.setText("Photos (" + uri.size()+")");
            }
            else if(data.getData() != null){
                try {
                    String imageUrl = data.getData().getPath();
                    if(data.getData() == null){
                        Toast.makeText(this, "Please choose an image", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Uri newUri = Uri.parse(imageUrl);
                    uri.add(newUri);
                    photos photo=new photos(newUri, curAlbum);
                    photographs.add(photo);
                }catch (RuntimeException e) {
                    Toast.makeText(this, "Please choose an image", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                }
            }
            adapter.notifyDataSetChanged();
            //textView.setText("Photos (" + uri.size()+")");
        }else{
            Toast.makeText(this,"Please choose an image", Toast.LENGTH_LONG).show();
        }

    }
    public void filter() {
        EditText tagname = findViewById(R.id.TagName);
        EditText tagval = findViewById(R.id.TagVal1);

        EditText tagname2 = findViewById(R.id.TagName2);
        EditText tagval2 = findViewById(R.id.TagVal2);

        CheckBox and = findViewById(R.id.andBox);
        CheckBox or = findViewById(R.id.orBox);


        boolean missingInfo = false;
        tagname.setError(null);
        tagname2.setError(null);
        tagval.setError(null);
        tagval2.setError(null);

        if(!and.isChecked() && !or.isChecked()) {
            if(tagname2.length() != 0 || tagval2.length() != 0) {
                tagname2.setError("Please input only in tag 1 fields");
                tagval2.setError("Please input only in tag 1 fields");
                missingInfo = true;
            }
            else {
                if(tagname.length() == 0) {
                    tagname.setError("Please input tag info");
                    missingInfo = true;
                }
                if(tagval.length() == 0) {
                    tagval.setError("Please input tag info");
                    missingInfo = true;
                }
            }
            if(missingInfo) {
                return;
            }
        }
        else {
            if(tagname.length() == 0) {
                tagname.setError("Please input tag info");
                missingInfo = true;
            }
            if(tagval.length() == 0) {
                tagval.setError("Please input tag info");
                missingInfo = true;
            }
            if(tagname2.length() == 0) {
                tagname2.setError("Please input tag info");
                missingInfo = true;
            }
            if(tagval2.length() == 0) {
                tagval2.setError("Please input tag info");
                missingInfo = true;
            }
            if(missingInfo) {
                return;
            }
        }

        File dir = getFilesDir();
        File file = new File(dir + "/album.ser");

        ArrayList<photos> newAllist = new ArrayList<>();
        ArrayList<Album> allist = Album.DeserializeAlbum(file);

        for (Album i : allist) {
            for (photos j : i.Photos) {
                boolean tagMatched = false;

                boolean firstTagMatched = false;
                for (PhotoTag k : j.tags) {
                    if (k.tagname.equalsIgnoreCase(tagname.getText().toString()) &&
                            k.tagval.toLowerCase().startsWith(tagval.getText().toString().toLowerCase())) {
                        firstTagMatched = true;
                        break;
                    }
                }

                boolean secondTagMatched = false;
                for (PhotoTag k : j.tags) {
                    if (k.tagname.equalsIgnoreCase(tagname2.getText().toString()) &&
                            k.tagval.toLowerCase().startsWith(tagval2.getText().toString().toLowerCase())) {
                        secondTagMatched = true;
                        break;
                    }
                }

                if (and.isChecked()) {
                    tagMatched = firstTagMatched && secondTagMatched;
                } else if (or.isChecked()) {
                    tagMatched = firstTagMatched || secondTagMatched;
                }
                else{
                    tagMatched = firstTagMatched;
                }

                if (tagMatched) {
                    newAllist.add(j);
                }
            }
        }

        photographs = newAllist;
        curAlbum.Photos = newAllist;

        ListOfPhotos.setAdapter(adapter);
        uri = new ArrayList<>();
        for (photos i : newAllist) {
            uri.add(i.getlocation());
        }

        adapter.setAlbum(curAlbum);
        adapter.notifyDataSetChanged();
        textView.setText("Photos (" + uri.size() + ")");
        Intent intent = getIntent();
        startActivity(intent);
    }


    public void PhotoListPage(){
        Intent intent = new Intent(this , PhotoListController.class);
        startActivity(intent);
    }
    public void AlbumPage(){
        Intent intent = new Intent(this , AlbumListController.class);
        startActivity(intent);
    }
}