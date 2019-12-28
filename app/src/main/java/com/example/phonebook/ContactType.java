package com.example.phonebook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

public class ContactType {
    public String Name;
    public String Number;
    public String Email;
    public Bitmap Image;
    public String Id;


    public ContactType(){

    }

    public ContactType(String n, String e, String p, Bitmap i, String id){
        if(n == null){
            this.Name = "Name not set";
        }
        else{
            this.Name = n;

        }
        if(e == null){
            this.Email = "Image Not set";
        }
        else{
            this.Email = e;

        }
        if( p == null){
            this.Number = "Number not set";
        }
        else{
            this.Number = p;
        }

        this.Image = i;
        this.Id = id;
    }

}
