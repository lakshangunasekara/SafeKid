package com.project.safekid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class CreateFamily extends AppCompatActivity {
    Button next;
    EditText familyName;
    FirebaseFirestore db;
    public static final String emailToPass = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_family);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Create a Family");
        Intent ints = getIntent();
        String email = ints.getStringExtra(SelectProfile.emailToPass2);

        next = findViewById(R.id.btnGoToDashboard);
        next.setEnabled(false);
        familyName = findViewById(R.id.textFamilyName);
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("parents").document(email);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot doc = task.getResult();
                if(doc.get("familyName")!=null){
                    Intent ints = getIntent();
                    String email = ints.getStringExtra(SelectProfile.emailToPass);

                    Intent j = new Intent(CreateFamily.this, ParentDashboard.class);
                    j.putExtra(emailToPass, email);
                    startActivity(j);
                }
            }
        });


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ints = getIntent();
                String email = ints.getStringExtra(SelectProfile.emailToPass);

                Map<String, Object> data = new HashMap<>();
                data.put("familyName", familyName.getText().toString());

                db.collection("parents").document(email)
                        .set(data, SetOptions.merge());

                Intent j = new Intent(CreateFamily.this, ParentDashboard.class);
                j.putExtra(emailToPass, email);
                startActivity(j);
            }
        });

        familyName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(familyName.getText().toString().isEmpty() || familyName.getText().toString().length()<6){
                    next.setEnabled(false);
                }else{
                    next.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
    }
}