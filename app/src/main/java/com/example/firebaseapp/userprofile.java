package com.example.firebaseapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class userprofile extends AppCompatActivity {
    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDob, textViewGender, textViewMobile;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userprofile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("All Yours");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        textViewWelcome = findViewById(R.id.textView_welcome);
        textViewFullName = findViewById(R.id.textView_show_fullName);
        textViewEmail = findViewById(R.id.textView_profile_Email);
        textViewDob = findViewById(R.id.textView_profile_Dob);
        textViewGender = findViewById(R.id.textView_profile_Gender);
        textViewMobile = findViewById(R.id.textView_profile_Mobile);
        progressBar = findViewById(R.id.progressbar);
        mAuth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.imageView_profileDp);

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(userprofile.this, uploadProfilePicture.class);
                startActivity(intent);
            }
        });

        if (firebaseUser == null) {
            Toast.makeText(userprofile.this, "User data is not available at the moment.", Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            checkIfEmailVerified(firebaseUser);
            showUserProfile(firebaseUser);
        }
    }

    private void checkIfEmailVerified(FirebaseUser firebaseUser) {
        if (!firebaseUser.isEmailVerified()) {
            showEmailNotVerifiedDialog();
        }
    }

    private void showEmailNotVerifiedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(userprofile.this);
        builder.setTitle("Email not verified");
        builder.setMessage("Please verify your email now. You cannot log in without email verification.");

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

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userId = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");

        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadwriteUserDetails readUserDetails = snapshot.getValue(ReadwriteUserDetails.class);

                if (readUserDetails != null) {
                    setProfileData(readUserDetails);
                    setProfilePicture(firebaseUser);
                } else {
                    Toast.makeText(userprofile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(userprofile.this, "Something went wrong!", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void setProfileData(ReadwriteUserDetails readUserDetails) {
        textViewWelcome.setText(String.format("Welcome %s!", readUserDetails.getFullName()));
        textViewFullName.setText(readUserDetails.getFullName());
        textViewEmail.setText(readUserDetails.getEmail());
        textViewDob.setText(readUserDetails.getDob());
        textViewGender.setText(readUserDetails.getGender());
        textViewMobile.setText(readUserDetails.getMobile());
    }

    private void setProfilePicture(FirebaseUser firebaseUser) {
        Uri photoUrl = firebaseUser.getPhotoUrl();

        if (photoUrl != null) {
            // Load profile picture with Picasso
            Picasso.get().load(photoUrl).into(imageView, new Callback() {
                @Override
                public void onSuccess() {
                    // Image loaded successfully
                     Toast.makeText(userprofile.this, "Profile picture updated successfully", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onError(Exception e) {
                    // Handle error
                    e.printStackTrace();
                }
            });
        } else {
            Log.d("userprofile", "No profile picture available for the user.");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logout();
        } else if (id == R.id.action_refresh) {
            startActivity(getIntent());
            finish();
        } else if (id == R.id.action_editProfile) {
            Intent intent = new Intent(userprofile.this, updateProfile.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuth.signOut();
        Intent intent = new Intent(userprofile.this, login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
