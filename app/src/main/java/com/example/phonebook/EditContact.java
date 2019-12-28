package com.example.phonebook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class EditContact extends AppCompatActivity {

    public EditText txtName;
    public EditText txtPhone;
    public EditText txtEmail;
    public static Bitmap bitmap=null;
    public String ConatctId;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int PICK_FROM_GALLARY = 2;
    public static final int IMAGEREQUESTCODE = 8242008;


    Uri outPutfileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_contact);

        getSupportActionBar().setTitle("Edit Contact"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);

        int id=getIntent().getIntExtra("SESSION_ID",-1);
        ContactType contact = new ContactType();

        contact = MainActivity.StoreContacts.get(id);

        txtName.setText(contact.Name);
        txtEmail.setText(contact.Email);
        txtPhone.setText(contact.Number);
        ConatctId = contact.Id;
        //Define Button in the Xml file and get them
        Button galleryButton= findViewById(R.id.btnGallery);
        Button cameraButton= findViewById(R.id.btncamera);

        //Listener's on the button
        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, PICK_FROM_GALLARY);
            }
        });



        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Camera permission required for Marshmallow version

                // permission has been granted, continue as usual
                Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                outPutfileUri = Uri.fromFile(file);
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outPutfileUri);
                startActivityForResult(captureIntent, PICK_FROM_CAMERA);
            }
        });
    }

    @Override
    protected final void onActivityResult(final int requestCode,
                                          final int resultCode, final Intent i) {
        super.onActivityResult(requestCode, resultCode, i);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_FROM_GALLARY:
//                    Toast.makeText(this, i.getData().toString(), Toast.LENGTH_SHORT).show();
                    manageImageFromUri(i.getData());
//                    if(bitmap!= null){
//                        Toast.makeText(this, "Bitmap generated!", Toast.LENGTH_SHORT).show();
//                    }
                    break;
            }
        } else {
            // manage result not ok !
        }

    }

    private void manageImageFromUri(Uri imageUri) {
        try {
            String paths = imageUri.getPath();
            Toast.makeText(this, imageUri.toString(), Toast.LENGTH_SHORT).show();
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver() , Uri.parse(paths));
//            if(bitmap!= null){
//                Toast.makeText(this, "Bitmap generated!", Toast.LENGTH_SHORT).show();
//            }
        } catch (Exception e) {
            Toast.makeText(this, "Exception", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void Save(View v){
        String DisplayName = txtName.getText().toString();
        String MobileNumber = txtPhone.getText().toString();
        String emailID = txtEmail.getText().toString();
//

        ArrayList<ContentProviderOperation> ops = new ArrayList < ContentProviderOperation > ();

        String where = ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
                ContactsContract.Data.MIMETYPE + " = ? AND " +
                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE) + " = ? ";

        String[] params = new String[] {DisplayName,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_HOME)};

        ops.add(ContentProviderOperation.newInsert(
                ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                .build());


        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, params)
                    .withValue(ContactsContract.CommonDataKinds.Phone.DATA, MobileNumber)
                    .build());

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);





    }
}
