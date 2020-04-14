package com.example.chatapp10;

import Adapter.GroupAdapter;
import Models.Group;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageView add;
    private ProgressBar progress;

    private FirebaseAuth mAuth;

    private List<Group> mGroups;
    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        add = findViewById(R.id.add_friend);
        progress = findViewById(R.id.progress);


        mAuth = FirebaseAuth.getInstance();
        mGroups = new ArrayList<>();
        adapter = new GroupAdapter(GroupsActivity.this,mGroups);

        recyclerView = findViewById(R.id.recycle_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupsActivity.this));
        recyclerView.setAdapter(adapter);

        getSupportActionBar().setTitle("Your Groups");


        onAddClick();
        readGroups();
    }

    private void readGroups() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final DatabaseReference groupRef  = FirebaseDatabase.getInstance().getReference().child("Groups");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("Groups");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGroups.clear();
                Log.d("groupss","onDataChange");
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    String groupId = snapshot.getValue(String.class);
                    Log.d("groupss","groupId = "+groupId);
                    groupRef.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Group group = dataSnapshot.getValue(Group.class);
                            Log.d("groupss"," groupppppppppppppppppppppppppppppppppppppppppp nnnaammee = "+ group.getName());
                            mGroups.add(group);
                            adapter.notifyDataSetChanged();
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

    private void onAddClick() {
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GroupsActivity.this,CreateGroupActivity.class));
            }
        });

    }
}
