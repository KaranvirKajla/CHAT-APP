package com.example.chatapp10;

import Adapter.UserAdapter;
import Models.User;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AddFriend extends AppCompatActivity {
    List<User> mUsers;
    ProgressBar progress;
    Context mContext;
    private UserAdapter userAdapter;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        recyclerView = findViewById(R.id.recycle_view);
        progress = findViewById(R.id.progress);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(AddFriend.this));
        mUsers = new ArrayList<>();
        userAdapter = new UserAdapter(AddFriend.this,mUsers);
        recyclerView.setAdapter(userAdapter);

        getSupportActionBar().setTitle("Find friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        readUsers();
    }

    private void readUsers() {

        final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users");
        final DatabaseReference friendref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("Friends");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    final User user = snapshot.getValue(User.class);
                    Log.d("friendadded","final " + user.toString());
                    if(user.getId().equals(currentUser.getUid()))continue;
                    Query query = friendref.orderByValue().equalTo(user.getEmail()).limitToFirst(1);
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.hasChildren()){
                                Log.d("friendadded","finalllllllllllllll " + user.getEmail().toString());
                                mUsers.add(user);
                                userAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                progress.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
