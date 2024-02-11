package com.example.firebaseapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class uploadProfilePicture extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView imageViewUploadProfilePic;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri uriImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_picture);

        Button buttonUpload = findViewById(R.id.btn_upload_profile_pic);
        Button buttonChooseProfilePic = findViewById(R.id.btn_choose_profile_pic);
        progressBar = findViewById(R.id.progressBar);
        imageViewUploadProfilePic = findViewById(R.id.imageView_upload_profile);
        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Display pics");

        Picasso.get().load(firebaseUser.getPhotoUrl()).into(imageViewUploadProfilePic);

        buttonChooseProfilePic.setOnClickListener(v -> openFileChooser());

        buttonUpload.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            uploadPic();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("All Yours");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
    }

    private void uploadPic() {
        if (uriImage != null) {
            StorageReference fileReference = storageReference.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid() + "." + getFileExtension());

            fileReference.putFile(uriImage)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                                firebaseUser = mAuth.getCurrentUser();
                                UserProfileChangeRequest updateProfilePic = new UserProfileChangeRequest.Builder().setPhotoUri(uri).build();
                                firebaseUser.updateProfile(updateProfilePic);

                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(uploadProfilePicture.this, "Profile picture uploaded.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(uploadProfilePicture.this, userprofile.class));
                                finish();
                            }).addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(uploadProfilePicture.this, "Failed to get download URL.", Toast.LENGTH_LONG).show();
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(uploadProfilePicture.this, "Failed to upload image. " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(uploadProfilePicture.this, "No file selected.", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    private String getFileExtension() {
        if (uriImage != null) {
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uriImage));
        } else {
            return "";
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            imageViewUploadProfilePic.setImageURI(uriImage);
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
            mAuth.signOut();
            Intent intent = new Intent(uploadProfilePicture.this, login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (id == R.id.action_refresh) {
            startActivity(getIntent());
            finish();
        } else if (id == R.id.action_editProfile) {
            Intent intent = new Intent(uploadProfilePicture.this, updateProfile.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
}
