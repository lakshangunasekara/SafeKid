package com.project.safekid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;


public class SelectProfile extends AppCompatActivity {

    CardView cardParent, cardChild;
    public static final String emailToPass = "email";
    public static final String emailToPass2 = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Select a Profile");

        cardParent = findViewById(R.id.cardParent);
        cardChild = findViewById(R.id.cardChild);


        cardParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ik = getIntent();
                String email = ik.getStringExtra(Login.emailToPass);
                String gmail = ik.getStringExtra(Register.emailToPass);

                Intent j = new Intent(SelectProfile.this, CreateFamily.class);
                if (email!= null){
                    j.putExtra(emailToPass2, email);
                } else{
                    j.putExtra(emailToPass2, gmail);
                }
                startActivity(j);
            }
        });

        cardChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ck = getIntent();
                String email = ck.getStringExtra(Login.emailToPass);
                String gmail = ck.getStringExtra(Register.emailToPass);

                Intent k = new Intent(SelectProfile.this, JoinFamily.class);
                if (email!= null){
                    k.putExtra(emailToPass2, email);
                } else{
                    k.putExtra(emailToPass2, gmail);
                }
                startActivity(k);
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
