package com.example.phonebook;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phonebook.Extra.LoadingDialog;
import com.example.phonebook.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
//import android.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {
    ListView listView ;
    private LoadingDialog loadingDialog;
    public static ArrayList<ContactType> StoreContacts;
    ContactAdapter myAdapter;
    Cursor cursor ;
    String name, phonenumber , email, contactId;
    Uri image;
    public  static final int RequestPermissionCode  = 1 ;

    private static final int REQUEST_CALL = 1;
    boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingDialog = new LoadingDialog(this, R.style.DialogLoadingTheme);
        loadingDialog.show();

        listView = findViewById(R.id.ListId);

        EnableRuntimePermission();

        FloatingActionButton fab = findViewById(R.id.fab);
        StoreContacts = new ArrayList<ContactType>();
        GetContactsIntoArrayList();

        Collections.sort(StoreContacts, new Comparator<ContactType>(){
            public int compare(ContactType s1, ContactType s2) {
                return s1.Name.compareToIgnoreCase(s2.Name);
            }
        });

        myAdapter = new ContactAdapter(this, StoreContacts);
        listView.setAdapter(myAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showPopup(view , position);
            }
        });
    }
    public void GetContactsIntoArrayList(){

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
        while (cursor.moveToNext()) {

            name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

            email = getEmail(contactId);
            if(email == ""){
                email = "Not set";
            }

            Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactId));
            InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(), my_contact_Uri,true);
            BufferedInputStream buf = new BufferedInputStream(photo_stream);
            Bitmap my_btmp = BitmapFactory.decodeStream(buf);

//            Bitmap my_btmp = openPhoto(contactId);
            ContactType c = new ContactType(name,email,phonenumber,my_btmp,contactId);
            StoreContacts.add(c);

        }

        cursor.close();
        loadingDialog.dismiss();

    }



    public String getEmail(String contactId) {
        String emailStr = "";
        final String[] projection = new String[]{ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Email.TYPE};

        Cursor emailq = managedQuery(ContactsContract.CommonDataKinds.Email.CONTENT_URI, projection, ContactsContract.Data.CONTACT_ID + "=?", new String[]{contactId}, null);

        if (emailq.moveToFirst()) {
            final int contactEmailColumnIndex = emailq.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);

            while (!emailq.isAfterLast()) {
                emailStr = emailStr + emailq.getString(contactEmailColumnIndex) ;
                emailq.moveToNext();
            }
        }
        return emailStr;
    }


    public void EnableRuntimePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                MainActivity.this,
                Manifest.permission.READ_CONTACTS))
        {

//            Toast.makeText(MainActivity.this,"CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.READ_CONTACTS}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String[] per, int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this,"Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(MainActivity.this,"Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    public void AddContact(View v){
        Intent intent = new Intent(this, AddContact.class);
        startActivity(intent);

    }

    public void showPopup(View v,final int i){

        PopupMenu popup= new PopupMenu(this,v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ContactType contact = new ContactType();
                contact = StoreContacts.get(i);
                switch (item.getItemId()) {
                    case R.id.mail:
                    {
                        Intent intent2 = new Intent(getBaseContext(), mail.class);

                        intent2.putExtra("MAIL", contact.Email);
                        startActivity(intent2);
                        return true;
                    }
                    case R.id.call:{
                        MakePhoneCall(contact.Number);
                        return true;
                    }
                    case R.id.msg:{
                        Intent intent1 = new Intent(getBaseContext(), msg.class);
                        intent1.putExtra("NUMBER", contact.Number);
                        startActivity(intent1);
                        return true;
                    }
                    case R.id.view:{
                        Intent intent2 = new Intent(getBaseContext(), ShowContact.class);
                        intent2.putExtra("SESSION_ID", i);
                        startActivity(intent2);
                        return true;
                    }
                    case R.id.del:{
                        final String num = contact.Number;
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("Confirmation");
                        builder.setMessage("Do you really want to delete this contact?");
                        builder.setPositiveButton("Confirm",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteContact(MainActivity.this,num);
                                    }

                                });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        return true;
                    }
                    default:
                        // Do nothing
                        return false;
                }
            }
        });
        popup.inflate(R.menu.popup_menu);
        popup.show();


    }

    private void MakePhoneCall(String number) {
        if(number.length() > 0)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }
            else{
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
    }


    public boolean deleteContact(Context ctx, String phoneNumber) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null,
                null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    String lookupKey =
                            cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    Uri uri = Uri.withAppendedPath(
                            ContactsContract.Contacts.CONTENT_LOOKUP_URI,
                            lookupKey);
                    ctx.getContentResolver().delete(uri, null, null);
                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        Toast.makeText(MainActivity.this,"Contact deleted!", Toast.LENGTH_LONG).show();
        Intent intent = getIntent();
        finish();
        startActivity(intent);

        return false;
    }

}


