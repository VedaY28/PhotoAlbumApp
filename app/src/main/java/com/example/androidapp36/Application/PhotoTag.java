package com.example.androidapp36.Application;

import java.io.Serializable;

public class PhotoTag implements Serializable {
    public String tagval;
    public String tagname;

    public PhotoTag(String name, String value){
        tagval=value;
        tagname=name;
    }

    public String getName(){
        return tagname;
    }

    public void setName(String name){
        tagname=name;
    }

    public String getVal(){
        return tagval;
    }

    public void setVal(String val){
        tagval = val;
    }

    public String toString(){
        return tagname + ": " +tagval;
    }

//    public boolean equals(Object o){
//        if(o==null||!(o instanceof PhotoTag)){
//            return false;
//        }
//        PhotoTag other=(PhotoTag) o;
//        return this.tagname.equals(other.tagname)&&this.tagval.equals(other.tagval);
//    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PhotoTag photoTag = (PhotoTag) obj;
        return tagname.equals(photoTag.tagname) && tagval.equals(photoTag.tagval);
    }
}

