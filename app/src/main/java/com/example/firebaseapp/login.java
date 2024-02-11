package com.example.firebaseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firebaseapp.userprofile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class login extends AppCompatActivity {
    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView textView;
    private TextView forgetPassword;
    private static final String Tag="login";

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent= new Intent(getApplicationContext(), userprofile.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth=FirebaseAuth.getInstance();
        editTextEmail=findViewById(R.id.editText_login_email);
        editTextPassword=findViewById(R.id.editText_login_password);
        buttonLogin=findViewById(R.id.btnLogin);
        progressBar=findViewById(R.id.progressBar);
        textView= findViewById(R.id.textView_signup);
        forgetPassword=findViewById(R.id.textView_forgetpassword);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), register.class);
                startActivity(intent);
                finish();
            }
        });

//forget password

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle "Forgot Password" button click
                openForgotPasswordActivity();
            }
        });



        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String email, password;
                email=String.valueOf(editTextEmail.getText());
                password=String.valueOf(editTextPassword.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(login.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(login.this, "Please enter a valid email", Toast.LENGTH_LONG).show();
                    editTextEmail.setError("Invalid Email");
                    editTextEmail.requestFocus();
                }
                else if (TextUtils.isEmpty(password)){
                    Toast.makeText(login.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {

                                    //get instance of the current user
                                    FirebaseUser firebaseUser= mAuth.getCurrentUser();
                                    //check if email is verified or not?
                                    if (firebaseUser.isEmailVerified()){
                                        Toast.makeText(getApplicationContext(), "Login successful.",
                                                Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), userprofile.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        firebaseUser.sendEmailVerification();
                                        mAuth.signOut();
                                        showAlertDialog();

                                    }

                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        editTextEmail.setError("User does not exist or no longer valid, please register again!!");
                                        editTextEmail.requestFocus();
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        editTextEmail.setError("Invalid credential, please check and try again!!");
                                        editTextEmail.requestFocus();
                                    } catch (Exception e) {
                                        Log.d(Tag, "Authentication failed!!", task.getException());
                                        Toast.makeText(login.this, e.getMessage(), Toast.LENGTH_LONG).show();

                                    }
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

    private void showAlertDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder(login.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. you cannot login without email verification");
        //open email if user clicks continue
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //create th e alert
        AlertDialog alertDialog= builder.create();
        //show the alert
        alertDialog.show();
    }
}
