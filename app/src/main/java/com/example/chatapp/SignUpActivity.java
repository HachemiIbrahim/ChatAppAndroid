package com.example.chatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {

    private EditText Username;
    private EditText FullName;
    private EditText Email;
    private EditText Password;
    private TextView LoginText;
    private Button SignUpButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        Username = findViewById(R.id.username);
        FullName = findViewById(R.id.fullname);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        SignUpButton = findViewById(R.id.sign_up_button);
        LoginText = findViewById(R.id.login_text);

        progressDialog = new ProgressDialog(this);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();


        LoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        SignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textUsername = Username.getText().toString();
                String textFullName = FullName.getText().toString();
                String textEmail = Email.getText().toString();
                String textPassword = Password.getText().toString();

                if (TextUtils.isEmpty(textUsername) || TextUtils.isEmpty(textFullName) ||
                        TextUtils.isEmpty(textEmail) || TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(SignUpActivity.this, "Empty Credentials ! ", Toast.LENGTH_SHORT).show();
                } else if (textPassword.length() < 6) {
                    Toast.makeText(SignUpActivity.this, "weak password", Toast.LENGTH_SHORT).show();
                } else {
                    RegisterNewUser(textUsername, textFullName, textEmail, textPassword);
                }
            }
        });
    }

    private void RegisterNewUser(String textUsername, String textFullName, String textEmail, String textPassword) {

        progressDialog.setMessage("Please wait");
        progressDialog.show();

        auth.createUserWithEmailAndPassword(textEmail, textPassword).
                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        HashMap<String, Object> map = new HashMap<>();

                        map.put("Username", textUsername);
                        map.put("Name", textFullName);
                        map.put("Email", textEmail);
                        map.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        map.put("imageURL" , "default");

                        database.child("Users").child(auth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            progressDialog.dismiss();
                                            EmailVerification();

                                        }
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(SignUpActivity.this, "error happened", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void EmailVerification() {
        auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(SignUpActivity.this, "Please verify your account to use the app", Toast.LENGTH_SHORT).show();
                if(auth.getCurrentUser().isEmailVerified()) {
                    Toast.makeText(SignUpActivity.this, "Account created successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUpActivity.this, "error happened", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        }
    }

}