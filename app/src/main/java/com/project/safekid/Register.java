package com.project.safekid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private FirebaseAuth auth;
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;
    TextView redirectText;
    EditText signUpEmail, signUpPassword;
    Button signUpButton;
    LinearLayout googleSignUp;
    FirebaseFirestore firebaseFirestore;
    public static String emailToPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();
        signUpEmail = findViewById(R.id.registerPageEmailText);
        signUpPassword = findViewById(R.id.registerPagePasswordText);
        signUpButton = findViewById(R.id.btnSignUp);
        redirectText = findViewById(R.id.textView8);
        googleSignUp = findViewById(R.id.btnSignUpWithGoogle);

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        googleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, 100);
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = signUpEmail.getText().toString();
                String password = signUpPassword.getText().toString();

                if (email.isEmpty()){
                    signUpEmail.setError("Email field is mandatory");
                }else if (password.isEmpty()){
                    signUpPassword.setError("Password field is mandatory");
                }else{
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                firebaseFirestore = FirebaseFirestore.getInstance();
                                Map<String, Object> data = new HashMap<>();
                                data.put("email", email);

                                firebaseFirestore.collection("parents").document(email)
                                        .set(data, SetOptions.merge());
                                Toast.makeText(Register.this, "Registered successfully", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Register.this, Login.class));

                            }else{
                                Toast.makeText(Register.this, "Registration failed: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }


        });

        redirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,Login.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
});

}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                task.getResult(ApiException.class);
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
                Toast.makeText(Register.this, "Login successful", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Register.this, SelectProfile.class);
                intent.putExtra(emailToPass, account.getEmail());
                startActivity(intent);
            }catch(ApiException e){
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }


        }
    }
}