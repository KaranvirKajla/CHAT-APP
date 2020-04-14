package com.example.chatapp10;

import Adapter.AddGroupMemberAdapter;
import Models.User;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddGroupMember extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ProgressBar progress;
    private List<User> mUsers;
    private AddGroupMemberAdapter adapter;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        recyclerView = findViewById(R.id.recycle_view);
        progress  = findViewById(R.id.progress);
        mUsers = new ArrayList<>();
        groupId = getIntent().getStringExtra("groupId");

        adapter = new AddGroupMemberAdapter(AddGroupMember.this,mUsers,groupId);


        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddGroupMember.this));

        getSupportActionBar().setTitle("Add Member");


        readUsers();
    }


    private void readUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Members");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    final User user = snapshot.getValue(User.class);

                    Query query = membersRef.orderByValue().equalTo(user.getId());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.hasChildren()){
                                mUsers.add(user);
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        progress.setVisibility(View.GONE);
    }
}
