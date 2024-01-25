package com.example.androidapp36.Application;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.ContextCompat;

import com.example.androidapp36.Controller.PhotoListController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class photos implements Serializable {
    public String nameofpic;
    public Calendar lastmodified;
    public ArrayList<PhotoTag> tags;
    public String location;

    public ArrayList<Album> album;
    public String caption;

    public static final String storeFile = "data/photo.ser";
    private static final int REQUEST_MEDIA_PERMISSION = 100;

    public photos(Uri f, Album Album) {

        this.caption = "";
        this.lastmodified = updatetime();
        tags = new ArrayList<PhotoTag>();
        String s = f.toString();
        location = s;
        if(album == null){
            album = new ArrayList<Album>();
        }
        album.add(Album);
        File filelocation= new File(storeFile);
    }

    public void addtag(PhotoTag tag){
        tags.add(tag);
        lastmodified=updatetime();
    }

    public void setName(String name){
        nameofpic = name;
    }

    public String getName(){
        return nameofpic;
    }

    public Calendar updatetime(){
        this.lastmodified=Calendar.getInstance();
        this.lastmodified.set(Calendar.MILLISECOND,0);
        return lastmodified;
    }

    public static void SerializePhoto(ArrayList<photos> userlist) {
        File loc = new File("data/photo.ser");
        try {
            loc.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(loc);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(userlist);
            out.close();
            fileOut.close();
        } catch (IOException e) {
        }
    }

    public static ArrayList<photos> deserializePhoto(File photofile){
        File loc=photofile;
        ArrayList<photos> albumlist=new ArrayList<photos>();
        try{
            loc.createNewFile();
            FileInputStream fileIn = new FileInputStream(loc);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            try{
                albumlist= (ArrayList<photos>) in.readObject();
            }
            catch(ClassNotFoundException i){
            }
            in.close();

            fileIn.close();
        }catch(FileNotFoundException d){
        }catch(IOException e){
        }
        return albumlist;
    }

    public boolean equals(Object o){
        if(o==null||!(o instanceof photos)){
            return false;
        }
        photos other=(photos) o;
        return this.location.equals(other.location);
    }

    public Uri getlocation(){
        return Uri.parse(location);
    }
    public Calendar getlastmodified(){
        return lastmodified;
    }
    private static boolean hasMediaPermission(Context context) {
        int permissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }
    private static void requestMediaPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ((PhotoListController)context).requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_MEDIA_PERMISSION);
        }
    }

}
