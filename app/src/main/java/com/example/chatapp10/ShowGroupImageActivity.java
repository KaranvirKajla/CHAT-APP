package com.example.chatapp10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ShowGroupImageActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Uri imageUri;
    private ImageView mImage;
    private EditText mMessage;
    private Button mSend;
    private String imageUrl;

    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        mImage = findViewById(R.id.image);
        mMessage= findViewById(R.id.message);
        mSend = findViewById(R.id.send);

        mAuth = FirebaseAuth.getInstance();
        groupId = getIntent().getStringExtra("groupId");


        CropImage.activity().start(ShowGroupImageActivity.this);
        onSendButtonClick();
    }

    private void onSendButtonClick() {
        final ProgressDialog pd = new ProgressDialog(ShowGroupImageActivity.this);
        pd.setMessage("Sending...");

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.show();
                final FirebaseUser currentUser = mAuth.getCurrentUser();
                if(imageUri!=null){
                    final StorageReference filePath = FirebaseStorage.getInstance().getReference("ImageMessages").child(System.currentTimeMillis()+ "." +getFileExtension(imageUri));
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
                            long i = (long) (new Date().getTime()/1000);
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat simpleDateFormat=  new SimpleDateFormat("dd-MMM-yyyy");
                            String date = simpleDateFormat.format(calendar.getTime());
                            simpleDateFormat=  new SimpleDateFormat("hh:mm:ss");
                            String time = simpleDateFormat.format(calendar.getTime());
                            Uri downloadUri = task.getResult();
                            imageUrl = downloadUri.toString();
                            String message = mMessage.getText().toString();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("GroupMessages");
                            String key = ref.child(groupId).push().getKey();
                            HashMap<String,Object> map = new HashMap<>();
                            map.put("from",currentUser.getUid());
                            map.put("message",message);
                            map.put("date", time+"  "+date);
                            map.put("seconds",i);
                            map.put("imageUrl",imageUrl);
                            map.put("id",key);

                            ref.child(groupId).child(key).setValue(map);
                            final DatabaseReference toRef = ref.child(groupId).child(key).child("to");
                            DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Members");

                            membersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                        String id = snapshot.getValue(String.class);
                                        if(!id.equals(currentUser.getUid()))
                                            toRef.push().setValue(id);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                    pd.dismiss();
                            Intent intent = new Intent(ShowGroupImageActivity.this,GroupMessageActivity.class);
                            intent.putExtra("groupId",groupId);
                            startActivity(intent);
                            finish();
                        }
                    });
                }else{
                    pd.dismiss();
                    Toast.makeText(ShowGroupImageActivity.this,"No image seleced Try again..",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ShowGroupImageActivity.this,GroupMessageActivity.class);
                    intent.putExtra("groupId",groupId);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(ShowGroupImageActivity.this.getContentResolver().getType(uri));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            mImage.setImageURI(imageUri);
        }else{
            Intent intent = new Intent(ShowGroupImageActivity.this,GroupMessageActivity.class);
            intent.putExtra("groupId",groupId);
            startActivity(intent);
            finish();
        }
    }
}
