package com.example.firebaseapp;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class forgetPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private ProgressBar progressBar;
    private static final String Tag = "forgetPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.editText_forget_email);
        progressBar=findViewById(R.id.progressBar);

        Button resetPasswordButton = findViewById(R.id.btn_reset_password);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle "Reset Password" button click
                String email;
                email=String.valueOf(emailEditText.getText());

                if(TextUtils.isEmpty(email)){
                    Toast.makeText(forgetPassword.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(forgetPassword.this, "Please enter a valid email", Toast.LENGTH_LONG).show();
                    emailEditText.setError("Invalid Email");
                    emailEditText.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });
    }



    private void resetPassword(String email) {
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(forgetPassword.this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(forgetPassword.this, "Reset password link is send successfully, please check your email!!",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(forgetPassword.this, login.class);
                            startActivity(intent);
                            finish();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                emailEditText.setError("User does not exist or no longer valid, please register again!!");
                                emailEditText.requestFocus();
                            } catch (Exception e) {
                                Log.e(Tag, "Authentication failed!!", task.getException());
                                Toast.makeText(forgetPassword.this, e.getMessage(), Toast.LENGTH_LONG).show();

                            }
                        }
                        progressBar.setVisibility(View.GONE);
                    });

    }
}
