package com.example.chatapp10;

import Adapter.GroupMessageAdapter;
import Models.GroupMessage;
import Models.Message;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class GroupMessageActivity extends AppCompatActivity {
    private ProgressBar progress;
    private List<GroupMessage> mMessages;
    private EditText mMessage;
    private ImageView mSend;
    private ImageView mImage;
    private RecyclerView mRecyclerView;
    private ImageView mLocation;
    private String groupId;
    private String groupName;

    private GroupMessageAdapter adapter;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private int switchformessagereceivetone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        progress = findViewById(R.id.progress);
        mMessage = findViewById(R.id.message);
        mImage = findViewById(R.id.image);
        mSend = findViewById(R.id.send);
        mLocation = findViewById(R.id.location);
        mRecyclerView = findViewById(R.id.recycle_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(GroupMessageActivity.this));
        mMessages = new ArrayList<>();

        adapter = new GroupMessageAdapter(GroupMessageActivity.this,mMessages);
        mRecyclerView.setAdapter(adapter);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(GroupMessageActivity.this);
        mAuth = FirebaseAuth.getInstance();
        groupId = getIntent().getStringExtra("groupId");
        groupName = getIntent().getStringExtra("groupName");
        switchformessagereceivetone=0;

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("name");

        getSupportActionBar().setTitle(groupName);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readMessages();
        onImageClick();
        onSendButtonClick();
        onLocationClick();

    }

    private void onLocationClick() {
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("myLocation","Clicked myLocation  ");
                if(ContextCompat.checkSelfPermission(GroupMessageActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(GroupMessageActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                }
                if(ContextCompat.checkSelfPermission(GroupMessageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(GroupMessageActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                }

                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location!=null){
                            Log.d("myLocation","myLocation = "+location.toString());

                            try {

                                Geocoder geo = new Geocoder(GroupMessageActivity.this, Locale.getDefault());
                                List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (addresses.isEmpty()) {
                                    mMessage.setText("Waiting for Location");
                                }
                                else {
                                    if (addresses.size() > 0) {
                                        mMessage.setText(addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName());
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else{

                            Dialog dialog = new Dialog(GroupMessageActivity.this);
                            dialog.setContentView(R.layout.location_dialog);
                            dialog.setCancelable(true);
                            dialog.show();
                            Log.d("myLocation","myLocation = null");

                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("myLocation","fail  = "+e.toString());
                        Toast.makeText(GroupMessageActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private void onImageClick() {
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupMessageActivity.this,ShowGroupImageActivity.class);
                intent.putExtra("groupId",groupId);
                startActivity(intent);
            }
        });

    }

    private void readMessages() {
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("GroupMessages").child(groupId);
        Query query = ref.orderByChild("seconds");

        query.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("onChildAdded","onChildAdded  "+dataSnapshot);
                Log.d("onChildAdded","onChildAdded string  "+s);
                GroupMessage message = dataSnapshot.getValue(GroupMessage.class);
                if(switchformessagereceivetone==1 && !currentUser.getUid().equals(message.getFrom())){
                    MediaPlayer mediaPlayer = MediaPlayer.create(GroupMessageActivity.this,R.raw.messagealert);
                    mediaPlayer.start();
                }
                switchformessagereceivetone=1;
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mMessages.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    final GroupMessage groupMessage = snapshot.getValue(GroupMessage.class);
                    mMessages.add(groupMessage);

                    /*if(currentUser.getUid().equals(groupMessage.getFrom())){
                        mMessages.add(groupMessage);
                        adapter.notifyDataSetChanged();
                    }else{
                        Query toQuery = ref.child(groupMessage.getId()).child("to").orderByValue().equalTo(currentUser.getUid());
                        toQuery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChildren()){
                                    mMessages.add(groupMessage);
                                    adapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }*/
                }
                adapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mMessages.size()-1);
                progress.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_member,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.add_member:
                Intent intent = new Intent(GroupMessageActivity.this,AddGroupMember.class);
                intent.putExtra("groupId",groupId);
                startActivity(intent);
                return true;
            case R.id.groupPic:
                intent = new Intent(GroupMessageActivity.this,GroupProfilePicActivity.class);
                intent.putExtra("groupId",groupId);
                startActivity(intent);
                return true;
            case R.id.members:
                intent = new Intent(GroupMessageActivity.this,GroupMembersActivity.class);
                intent.putExtra("groupId",groupId);
                intent.putExtra("groupName",groupName);
                startActivity(intent);
                return true;
            case android.R.id.home:
                intent = new Intent(GroupMessageActivity.this,GroupsActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                Toast.makeText(GroupMessageActivity.this,"Under construction",Toast.LENGTH_SHORT).show();
                return true;
        }
        //return (super.onOptionsItemSelected(menuItem));
    }

    private void onSendButtonClick() {
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = mMessage.getText().toString();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(GroupMessageActivity.this,"Empty message",Toast.LENGTH_SHORT).show();
                }else{
                    long i = (long) (new Date().getTime()/1000);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat=  new SimpleDateFormat("dd-MMM-yyyy");
                    String date = simpleDateFormat.format(calendar.getTime());
                    simpleDateFormat=  new SimpleDateFormat("hh:mm:ss");
                    String time = simpleDateFormat.format(calendar.getTime());
                    final FirebaseUser currentUser = mAuth.getCurrentUser();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("GroupMessages");

                    String key = ref.child(groupId).push().getKey();
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("from",currentUser.getUid());
                    map.put("message",message);
                    map.put("date", time+"  "+date);
                    map.put("seconds",i);
                    map.put("imageUrl","default");
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

                    mRecyclerView.scrollToPosition(mMessages.size()-1);
                    mMessage.setText("");
                }
            }
        });
    }

}
