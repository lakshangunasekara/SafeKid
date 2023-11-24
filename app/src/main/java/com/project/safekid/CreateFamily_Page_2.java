package com.project.safekid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

public class CreateFamily_Page_2 extends AppCompatActivity {
    Button goNext;
    TextView textView;
    FirebaseFirestore db;
    public static final String emailToPass="email";

    //private String randomNumGenerator(){}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_family_page2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Create a Family");

        textView = findViewById(R.id.randomCode);
        db = FirebaseFirestore.getInstance();
        int random = new Random().nextInt(89999999)+10000000;
        textView.setText( String.valueOf(random));
        goNext = findViewById(R.id.btnGoToDashboard);

        Intent i = getIntent();
        String email = i.getStringExtra(CreateFamily.emailToPass);

        goNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateFamily_Page_2.this, ParentDashboard.class));
            }
        });
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
    }
}