package com.example.androidapp36.Application;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
//hello
public class User implements Serializable {
    public ArrayList<Album> Albums;
    public String Username;
    public static final String storeDir = "ser";
    public static final String storeFile = "users.ser";

    public User(String username) throws IOException, ClassNotFoundException {
        this.Username = username;

        File filelocation = new File(storeFile);
        filelocation.createNewFile();
        try {
            FileInputStream fileIn = new FileInputStream(filelocation);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            ArrayList<User> userlist = new ArrayList<User>();
            try {
                userlist = (ArrayList<User>) in.readObject();
            } catch (ClassNotFoundException i) {
            }
            in.close();
            fileIn.close();
            userlist.add(this);
            SerializeUser(filelocation, userlist);
        } catch (IOException e) {
        }
    }

    public String getUsername() {
        return Username;
    }

    public static void SerializeUser(File loc, ArrayList<User> userlist) {
        try {
            FileOutputStream fileOut = new FileOutputStream(loc);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(userlist);
            out.close();
            fileOut.close();
        } catch (IOException e) {
        }
        return;
    }

    public String toString() {
        return Username;
    }
}