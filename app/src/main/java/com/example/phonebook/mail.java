package com.example.phonebook;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class mail extends AppCompatActivity {

    public EditText txtRecipientMail;
    public EditText txtSubject;
    public EditText txtBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mail);

        getSupportActionBar().setTitle("Mailing App"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action bar


        txtRecipientMail = findViewById(R.id.txtRecipientMailId);
        txtBody = findViewById(R.id.txtBodyId);
        txtSubject = findViewById(R.id.txtSubjectId);

        txtRecipientMail.setText(getIntent().getStringExtra("MAIL"));
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



    public void sendMessage(View v) throws InterruptedException {
        String subject = txtSubject.getText().toString().trim();
        String email = txtRecipientMail.getText().toString().trim();
        String body = txtBody.getText().toString();

        // Sending Mail

        JavaMailAPI javamailAPI = new JavaMailAPI(this,email,subject,body);

        javamailAPI.execute();





    }


}

