package com.example.androidapp36.Application;

import com.example.androidapp36.Controller.AlbumListController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Album implements Serializable{

    public String albumName;
    public Calendar firstphoto;
    public Calendar lastPhoto;
    public ArrayList<photos> Photos;

    public static final String storeDir ="ser";
    public static final String storeFile ="data/album.ser";

    public Album(String albumName) throws IOException {
        this.albumName = albumName;
        this.Photos = new ArrayList<photos>();
        File filelocation = new File(storeFile);

        try {
            System.out.println(filelocation.createNewFile());
        } catch (IOException e) {
        }
    }

    public void setAlbumName(String newAlbumName){
        albumName = newAlbumName;

    }

    public String getName(){
        return albumName;

    }

    public void addphoto(photos photo, File f){
        ArrayList<Album> a= DeserializeAlbum(f);

        for(int i=0; i<a.size();i++){
            if(a.get(i).equals(this)){
                if(a.get(i).firstphoto==null||a.get(i).firstphoto.after(photo.lastmodified)){
                    a.get(i).firstphoto=photo.lastmodified;
                }
                if(a.get(i).lastPhoto==null||a.get(i).lastPhoto.before(photo.lastmodified)){
                    a.get(i).lastPhoto=photo.lastmodified;
                }
                a.get(i).Photos.add(photo);
            }
        }
        AlbumListController.SerializeAlbum(f, a);
    }

    public void deletephoto(photos photo){
        for(int i=0; i<Photos.size(); i++){
            if (Photos.get(i).location.equals(photo.location)){
                Photos.remove(i);
            }
        }
        if(Photos.size()>0){
            lastPhoto=Photos.get(0).lastmodified;
            firstphoto=Photos.get(0).lastmodified;
            for(int i=0; i<Photos.size(); i++){
                if(Photos.get(i).lastmodified.before(firstphoto)){
                    firstphoto=Photos.get(i).lastmodified;
                }
                if(Photos.get(i).lastmodified.after(lastPhoto)){
                    lastPhoto=Photos.get(i).lastmodified;
                }
            }
        }
        else{
            if(Photos.size()==0){
                lastPhoto=null;
                firstphoto=null;
            }
        }
    }
    public void deletePhoto(int positon){
        deletephoto(Photos.get(positon));
    }
    public void deleteAll(){

        for(int i=0; i<Photos.size(); i++){
            Photos.remove(i);
        }
        if(Photos.size()>0){
            lastPhoto=Photos.get(0).lastmodified;
            firstphoto=Photos.get(0).lastmodified;
            for(int i=0; i<Photos.size(); i++){
                if(Photos.get(i).lastmodified.before(firstphoto)){
                    firstphoto=Photos.get(i).lastmodified;
                }
                if(Photos.get(i).lastmodified.after(lastPhoto)){
                    lastPhoto=Photos.get(i).lastmodified;

                }
            }}
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

    public String toString(){
        if(firstphoto==null){
            return this.albumName+ ":    Number of Photos: "+Photos.size();
        }
        else{
            return this.albumName+ ":    Number of Photos: "+Photos.size();
        }
    }

    public boolean equals(Object o){
        if(o==null||!(o instanceof Album)){
            return false;
        }
        Album other=(Album) o;
        return albumName.equals(other.albumName);
    }
}
