package com.project.safekid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class JoinFamily extends AppCompatActivity {
    Button joinFamily;
    EditText childName;
    FirebaseFirestore db;
    public static final String parentEmailToPass = "email";
    public static final String childIDToPass = "ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_family);
        setTitle("Join a Family");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        joinFamily = findViewById(R.id.btnJoinFamily);
        childName = findViewById(R.id.textInputEditText);

        joinFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();
                Intent i = getIntent();
                String parentEmail = i.getStringExtra(SelectProfile.emailToPass);
                String childID = childName.getText().toString();

                Map<String, Object> data = new HashMap<>();
                data.put("name", childName.getText().toString());


                db.collection("parents").document(parentEmail).collection("children").document(childID)
                        .set(data , SetOptions.merge());

                Intent j = new Intent(JoinFamily.this, ChildDashboard.class);
                j.putExtra(parentEmailToPass, parentEmail);
                j.putExtra(childIDToPass, childID);
                startActivity(j);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}