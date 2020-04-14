package com.example.chatapp10;

import Models.Group;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class GroupProfilePicActivity extends AppCompatActivity {
    private CircleImageView mImage;
    private EditText mDescription;
    private Button mSave;
    private EditText mName;

    private Uri imageUri;
    private String imageUrl;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile_pic);
        groupId = getIntent().getStringExtra("groupId");

        mImage = findViewById(R.id.image);
        mName = findViewById(R.id.name);
        mDescription = findViewById(R.id.description);
        mSave = findViewById(R.id.save);

        getSupportActionBar().setTitle("Change Group Profile");
        readData();

        onImageClick();

        onSaveButtonClick();
    }

    private void readData() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                Picasso.get().load(group.getImageUrl()).placeholder(R.mipmap.ic_group).into(mImage);
                mName.setText(group.getName());
                mDescription.setText(group.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void onSaveButtonClick() {
        mSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(GroupProfilePicActivity.this);
                pd.setMessage("Saving...");
                pd.show();
                if(imageUri!=null){
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference("GroupProfilePic").child(groupId);
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
                            imageUrl =  downloadUri.toString();

                            String name = mName.getText().toString();
                            if(TextUtils.isEmpty((name))){
                                Toast.makeText(GroupProfilePicActivity.this,"Group name cannot be empty",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId);
                                HashMap<String, Object> map = new HashMap();
                                map.put("name",name);
                                map.put("imageUrl", imageUrl);
                                map.put("description", mDescription.getText().toString());
                                ref.updateChildren(map);
                            }
                            pd.dismiss();

                        }
                    });
                }else{
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId);
                    HashMap<String, Object> map = new HashMap();
                    String name= mName.getText().toString();
                    if(TextUtils.isEmpty(name)){
                        Toast.makeText(GroupProfilePicActivity.this,"Name cannot be empty",Toast.LENGTH_SHORT).show();
                    }else{
                    map.put("name",name);
                    map.put("description", mDescription.getText().toString());
                    ref.updateChildren(map);

                    pd.dismiss();
                    Intent intent = new Intent(GroupProfilePicActivity.this,GroupMessageActivity.class);
                    intent.putExtra("groupId",groupId);
                    startActivity(intent);
                    finish();
                    }
                }


            }
        });
    }

    /*private String getFileExtension(Uri uri) {
        return  MimeTypeMap.getSingleton().getExtensionFromMimeType(GroupProfilePicActivity.this.getContentResolver().getType(uri));
    }*/

    private void onImageClick() {
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(GroupProfilePicActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            mImage.setImageURI(imageUri);
        }else{
            Intent intent = new Intent(GroupProfilePicActivity.this,GroupMessageActivity.class);
            intent.putExtra("groupId",groupId);
            startActivity(intent);
            finish();
        }
    }
}
