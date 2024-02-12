package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView textView;
    private TextView forgetPassword;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        TextInputLayout textInputLayoutEmail = findViewById(R.id.editText_login_email);
        editTextEmail = textInputLayoutEmail.getEditText();

        TextInputLayout textInputLayoutPassword = findViewById(R.id.editText_login_password);
        editTextPassword = textInputLayoutPassword.getEditText();

        buttonLogin = findViewById(R.id.btnLogin);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.textView_signup);
        forgetPassword = findViewById(R.id.textView_forgetpassword);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, register.class);
                startActivity(intent);
                finish();
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openForgotPasswordActivity();
            }
        });

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(login.this, "Enter email", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(login.this, "Please enter a valid email", Toast.LENGTH_LONG).show();
                    editTextEmail.setError("Invalid Email");
                    editTextEmail.requestFocus();
                    progressBar.setVisibility(View.GONE);
                    return;
                } else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(login.this, "Enter password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                                        Toast.makeText(getApplicationContext(), "Login successful.", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), userprofile.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        sendEmailVerification();
                                    }
                                } else {
                                    Log.d(TAG, "Authentication failed!", task.getException());
                                    Toast.makeText(login.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
    }

    private void openForgotPasswordActivity() {
        Intent intent = new Intent(login.this, forgetPassword.class);
        startActivity(intent);
    }

    private void sendEmailVerification() {
        AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You cannot login without email verification.");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
