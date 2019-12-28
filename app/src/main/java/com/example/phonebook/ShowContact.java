package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ShowContact extends AppCompatActivity {

    public TextView txtName;
    public TextView txtNumber;
    public TextView txtEmail;
    public ImageView imgContact;
    private Animator currentAnimator;

    private int shortAnimationDuration;

    private static final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact);

        getSupportActionBar().setTitle("Show Contact"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar

        txtName = findViewById(R.id.txtShowName);
        txtNumber = findViewById(R.id.txtShowNumber);
        txtEmail = findViewById(R.id.txtShowEmail);
        imgContact = findViewById(R.id.imageView);


        int id=getIntent().getIntExtra("SESSION_ID",-1);
        ContactType contact = new ContactType();

        contact = MainActivity.StoreContacts.get(id);


        txtName.setText("Name : " + contact.Name);
        txtEmail.setText("Email : " + contact.Email);
        txtNumber.setText("Phone Number : " + contact.Number);
        imgContact.setImageBitmap(contact.Image);

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        switch (item.getItemId()) {
            case R.id.mail:
            {
                Intent intent2 = new Intent(getBaseContext(), mail.class);
                String[] parts = txtEmail.getText().toString().split(":");
                intent2.putExtra("MAIL", parts[1]);
                startActivity(intent2);
                return true;
            }
            case R.id.call:{
                MakePhoneCall();
                return true;
            }
            case R.id.msg:{
                Intent intent1 = new Intent(getBaseContext(), msg.class);
                String[] parts = txtNumber.getText().toString().split(":");
                intent1.putExtra("NUMBER", parts[1]);
                startActivity(intent1);
                return true;
            }
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;


    }

    private void MakePhoneCall() {
        String PhoneNumber = txtNumber.getText().toString().trim();
        if(PhoneNumber.length() > 0)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
            }
            else{
                String dial = "tel:" + PhoneNumber;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }
    }


}
