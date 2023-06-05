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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ktx.FirebaseDatabaseKtxRegistrar;

public class LoginActivity extends AppCompatActivity {

    private EditText Email;
    private EditText Password;
    private TextView SignUpText;
    private Button LoginButton;
    private TextView ForgetPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        SignUpText = findViewById(R.id.signup_text);
        LoginButton = findViewById(R.id.login_button);
        ForgetPassword = findViewById(R.id.forget_password_text);

        progressDialog = new ProgressDialog(this);

        auth = FirebaseAuth.getInstance();

        SignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this , SignUpActivity.class));
            }
        });

        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = Email.getText().toString();
                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this, "Enter Your Email", Toast.LENGTH_SHORT).show();
                }else {
                    auth.sendPasswordResetEmail(textEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "a reset password email has been sent", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = Email.getText().toString();
                String textPassword = Password.getText().toString();

                if (TextUtils.isEmpty(textEmail) || TextUtils.isEmpty(textPassword) ){
                    Toast.makeText(LoginActivity.this, "Empty Credentials ! ", Toast.LENGTH_SHORT).show();
                }else {
                    LoginUser(textEmail , textPassword);
                }
            }
        });
    }

    private void LoginUser(String textEmail, String textPassword) {
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        auth.signInWithEmailAndPassword(textEmail , textPassword).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "login successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "error happened", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }
}