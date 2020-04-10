package com.example.chatapp10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

public class ProfilePicActivity extends AppCompatActivity {
    private String imageUrl;
    private FirebaseAuth mAuth;
    private Uri imageUri;
    private CircleImageView imageView;
    private Button change;
    private Button save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic);

        imageView = findViewById(R.id.imageView);
        change = findViewById(R.id.change);
        save = findViewById(R.id.save);
        mAuth = FirebaseAuth.getInstance();

        getSupportActionBar().setTitle("Change profile pic");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readImage();

        onChangeButtonClick();
        onSaveButtonClick();
    }


    private void readImage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("imageUrl");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String url = dataSnapshot.getValue().toString();
                Picasso.get().load(url).placeholder(R.mipmap.ic_person).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            imageView.setImageURI(imageUri);
        } else {

        }
    }

    private void onChangeButtonClick() {
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(ProfilePicActivity.this);
            }
        });
    }

    private void onSaveButtonClick() {
        final ProgressDialog pd = new ProgressDialog(ProfilePicActivity.this);
        pd.setMessage("Saving...");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(imageUri!=null){
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference("Profile").child(currentUser.getUid());
                    StorageTask uploadTask = filePath.putFile(imageUri);
                    uploadTask.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {
                            if(!task.isSuccessful()){throw task.getException();}
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            Uri downloadUri = task.getResult();
                            imageUrl = downloadUri.toString();
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            DatabaseReference ref =  FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("imageUrl",imageUrl);
                            ref.updateChildren(map);
                            pd.dismiss();
                            Toast.makeText(ProfilePicActivity.this,"Profile Pic successfully updated",Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    pd.dismiss();
                    Toast.makeText(ProfilePicActivity.this,"Try again",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));
    }
}
