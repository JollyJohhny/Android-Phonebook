package com.example.phonebook;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.phonebook.R;

public class msg extends AppCompatActivity {

    private EditText txtPhoneNumber;
    private EditText txtMessageBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        getSupportActionBar().setTitle("Messaging App"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar


        txtPhoneNumber = findViewById(R.id.txtPhoneNumberId);
        txtMessageBody = findViewById(R.id.txtMessageBodyId);

        String num =getIntent().getStringExtra("NUMBER");

        txtPhoneNumber.setText(num);
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

    public void sendMessage(View v)
    {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);

        if(permissionCheck == PackageManager.PERMISSION_GRANTED)
        {
            MyMessage();
        }
        else
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},0);
        }
    }

    private void MyMessage() {
        String number = txtPhoneNumber.getText().toString().trim();
        String text = txtMessageBody.getText().toString();
        if(!txtPhoneNumber.getText().toString().equals("") && !txtMessageBody.toString().equals(""))
        {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number,null,text,null,null);

            Toast.makeText(this,"Message Sent",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this,"Please Enter Number and Message!",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case 0:
                if(grantResults.length >=0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    MyMessage();
                }
                else{
                    Toast.makeText(this,"You don't have permission!",Toast.LENGTH_SHORT);
                }
                break;
        }

    }
}
