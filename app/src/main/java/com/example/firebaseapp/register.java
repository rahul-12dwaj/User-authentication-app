package com.example.firebaseapp;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class register extends AppCompatActivity {
    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDob, editTextRegisterMobile, editTextRegisterPassword, editTextRegisterConfirmPassword;
    private ProgressBar progressbar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radiobuttonRegisterGenderSelected;
    private FirebaseAuth mAuth;
   // private FirebaseUser firebaseUser;
    private static final String Tag="register";
    private DatePickerDialog picker;






    @Override

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        /*getSupportActionBar().setTitle("Register");*/
        Toast.makeText(register.this, "Now you can register yourself!", Toast.LENGTH_LONG).show();
        mAuth = FirebaseAuth.getInstance();
        editTextRegisterFullName= findViewById(R.id.editText_register_fullName);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterDob = findViewById(R.id.editText_register_dob);
        editTextRegisterPassword = findViewById(R.id.editText_register_password);
        editTextRegisterMobile=findViewById(R.id.editText_register_mobile);
        editTextRegisterConfirmPassword = findViewById(R.id.editText_register_confirm_password);
        progressbar = findViewById(R.id.progressBar);
        radioGroupRegisterGender= findViewById(R.id.radio_button_register_gender);
        radioGroupRegisterGender.clearCheck();

        //setting up date picker
        editTextRegisterDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar= Calendar.getInstance();
                int day= calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);
                //date picker dialog
                picker=new DatePickerDialog(register.this, new DatePickerDialog.OnDateSetListener() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDob.setText(String.format("%d/%d/%d", dayOfMonth, month + 1, year));
                    }
                }, year, month, day);
                picker.show();




            }
        });

        Button buttonregister = findViewById(R.id.registerpageButton);
        buttonregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
                radiobuttonRegisterGenderSelected = findViewById(selectGenderId);
                String textFullName = String.valueOf(editTextRegisterFullName.getText());
                String textEmail = String.valueOf(editTextRegisterEmail.getText());
                String textMobile = String.valueOf(editTextRegisterMobile.getText());
                String textDob = String.valueOf(editTextRegisterDob.getText());
                String textPassword = String.valueOf(editTextRegisterPassword.getText());
                String textConfirmPassword = String.valueOf(editTextRegisterConfirmPassword.getText());
                String textGender;


                //validate mobile number using matcher and pattern(regular expression)
                String mobileRegex="[6-9][0-9]{9}";
                Matcher mobileMatcher;
                Pattern mobilePattern=Pattern.compile(mobileRegex);
                mobileMatcher=mobilePattern.matcher(textMobile);

                //validate password is strong
                String pwdRegex ="^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$";
                Matcher pwdMatcher;
                Pattern pwdPattern=Pattern.compile(pwdRegex);
                pwdMatcher=pwdPattern.matcher(textPassword);




                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(register.this, "Please enter your Full Name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full name is required");
                    editTextRegisterFullName.requestFocus();
                } else if ((TextUtils.isEmpty(textEmail))) {
                    Toast.makeText(register.this, "Please enter a valid email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is required");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(register.this, "Please enter a valid email", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Invalid Email");
                    editTextRegisterEmail.requestFocus();
                } else if (TextUtils.isEmpty(textMobile)) {
                    Toast.makeText(register.this, "Please enter a valid mobile No.", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Mobile No. is required");
                    editTextRegisterMobile.requestFocus();
                } else if (!mobileMatcher.find()) {
                    Toast.makeText(register.this, "Please enter a valid mobile No.", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Invalid Mobile Number");
                    editTextRegisterMobile.requestFocus();
                } else if (TextUtils.isEmpty(textDob)) {
                    Toast.makeText(register.this, "Please enter your DOB", Toast.LENGTH_LONG).show();
                    editTextRegisterDob.setError("DOB is required");
                    editTextRegisterDob.requestFocus();
                } else if (!pwdMatcher.find()) {
                    Toast.makeText(register.this, "Password is too weak! Use a combination of letters, numbers, and special characters", Toast.LENGTH_LONG).show();
                    editTextRegisterPassword.setError("Password is too weak");
                    editTextRegisterPassword.requestFocus();
                } else if (TextUtils.isEmpty(textPassword)) {
                    Toast.makeText(register.this, "Please enter a strong Password", Toast.LENGTH_LONG).show();
                    editTextRegisterPassword.setError("Password is required");
                    editTextRegisterPassword.requestFocus();
                } else if (TextUtils.isEmpty(textConfirmPassword)) {
                    Toast.makeText(register.this, "Please enter your password again for confirmation", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPassword.setError("Confirm your password");
                    editTextRegisterConfirmPassword.requestFocus();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId() == -1) {
                    Toast.makeText(register.this, "Please select a gender", Toast.LENGTH_LONG).show();
                    radiobuttonRegisterGenderSelected.setError("Select gender");
                    radiobuttonRegisterGenderSelected.requestFocus();
                } else if (textMobile.length() != 10) {
                    Toast.makeText(register.this, "Please enter a valid Mobile No.", Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("Number Invalid");
                    editTextRegisterMobile.requestFocus();
                } else if (textPassword.length() < 6) {
                    Toast.makeText(register.this, "Please enter a strong password", Toast.LENGTH_LONG).show();
                    editTextRegisterPassword.setError("Password should contain 6 characters");
                    editTextRegisterPassword.requestFocus();
                } else if (!textPassword.equals(textConfirmPassword)) {
                    Toast.makeText(register.this, "Please enter similar password", Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPassword.setError("Password confirmation failed");
                    editTextRegisterConfirmPassword.requestFocus();
                    editTextRegisterPassword.clearComposingText();
                    editTextRegisterConfirmPassword.clearComposingText();
                }  else {
                    textGender = String.valueOf(radiobuttonRegisterGenderSelected.getText());
                    progressbar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDob, textMobile, textGender, textPassword);
                }
            }
        });
    }//creating user using credential given
    private void registerUser(String textFullName, String textEmail, String textDob, String textMobile, String textGender, String textPassword) {
        mAuth.createUserWithEmailAndPassword(textEmail, textPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressbar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Create a User object with the user details
                                ReadwriteUserDetails userDetails = new ReadwriteUserDetails(textFullName, textEmail, textGender, textMobile, textDob);

                                // Store user details in the Realtime Database
                                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users").child(user.getUid());
                                referenceProfile.setValue(userDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.sendEmailVerification();

                                            {
                                                // Check if the email is verified
                                                if (!user.isEmailVerified()) {
                                                    // The email is not verified, show a message or take appropriate action
                                                    Toast.makeText(register.this, "Email not verified. Please verify your email.", Toast.LENGTH_SHORT).show();
                                                    showAlertDialog();
                                                } else {
                                                    // The email is verified, proceed with the registration flow
                                                    Toast.makeText(register.this, "Account created!!", Toast.LENGTH_SHORT).show();

                                                    // Intent to navigate to the user profile or any other desired activity
                                                    Intent intent = new Intent(getApplicationContext(), userprofile.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        } else {
                                            Toast.makeText(register.this, "Registration Failed, Please try again!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }  else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            }  catch (FirebaseAuthWeakPasswordException e) {
                                editTextRegisterPassword.setError("Your password is too weak!! Kindly use combination of alphabets, numbers, special character.");
                                editTextRegisterPassword.requestFocus();
                            }  catch (FirebaseAuthInvalidCredentialsException e){
                                editTextRegisterEmail.setError("Invalid email or already registered!!");
                                editTextRegisterEmail.requestFocus();
                            }  catch (FirebaseAuthUserCollisionException e){
                                editTextRegisterEmail.setError("User is already registered!!");
                                editTextRegisterEmail.requestFocus();
                            } catch (Exception e){
                                Log.d(Tag, "Authentication failed!!", task.getException());
                                Toast.makeText(register.this, e.getMessage(), Toast.LENGTH_LONG).show();

                            }

                            // If sign-in fails, display a message to the user.
                            Toast.makeText(register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void showAlertDialog() {
            AlertDialog.Builder builder=new AlertDialog.Builder(register.this);
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


