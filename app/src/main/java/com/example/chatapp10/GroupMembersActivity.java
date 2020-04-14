package com.example.chatapp10;

import Adapter.GroupMemberAdapter;
import Models.User;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class GroupMembersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView image;
    private ProgressBar progress;
    private GroupMemberAdapter adapter;
    private List<User> mMembers;
    private String groupId;
    private String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_members);

        groupId = getIntent().getStringExtra("groupId");
        groupName  = getIntent().getStringExtra("groupName");
        mMembers = new ArrayList<>();
        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupMembersActivity.this));
        adapter = new GroupMemberAdapter(GroupMembersActivity.this,mMembers,groupId);

        recyclerView.setAdapter(adapter);
        progress = findViewById(R.id.progress);
        image = findViewById(R.id.image);

        getSupportActionBar().setTitle(groupName);
        //getSupportActionBar().setTitle("GroupMembersActivity");

        readImage();
        readMembers();
    }

    private void readImage() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("imageUrl");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Picasso.get().load(dataSnapshot.getValue(String.class)).placeholder(R.mipmap.ic_group).into(image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMembers() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference  memRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Members");
        //final DatabaseReference  creatorRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("creator");

        /*creatorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String id = dataSnapshot.getValue(String.class);
                ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        mMembers.add(user);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/


        memRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String id = snapshot.getValue(String.class);

                    ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            mMembers.add(user);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    progress.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

}
