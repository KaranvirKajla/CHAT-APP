package com.example.chatapp10;

import Models.Group;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity {

    EditText mName,mDescription;
    Button mCreateGroup;


    String name,description;


    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mName = findViewById(R.id.name);
        mDescription = findViewById(R.id.description);
        mCreateGroup = findViewById(R.id.create_group);


        mAuth = FirebaseAuth.getInstance();

        onCreateButtonClick();
    }

    private void onCreateButtonClick() {
        mCreateGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 name = mName.getText().toString();
                 description = mDescription.getText().toString();
                if(TextUtils.isEmpty(name)){
                    Toast.makeText(CreateGroupActivity.this,"Name should not be empty",Toast.LENGTH_LONG).show();
                }else{
                    createGroup();
                }
            }


        });
    }

    private void createGroup() {
       ProgressDialog pd = new ProgressDialog(CreateGroupActivity.this);
       pd.setMessage("Creating group..");
       pd.show();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups");
        String key = ref.push().getKey();
        HashMap<String,Object> map = new HashMap<>();
        map.put("name",name);
        map.put("description",description);
        map.put("creator",currentUser.getUid());
        map.put("id",key);
        map.put("imageUrl","default");

        ref.child(key).setValue(map);

        ref.child(key).child("Members").push().setValue(currentUser.getUid());


        ref = FirebaseDatabase.getInstance().getReference().child("Users");

        ref.child(currentUser.getUid()).child("Groups").push().setValue(key);

        pd.dismiss();


        startActivity(new Intent(CreateGroupActivity.this,GroupsActivity.class));
        finish();

    }
}
