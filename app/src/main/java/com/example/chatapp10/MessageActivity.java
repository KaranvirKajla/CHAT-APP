package com.example.chatapp10;

import Adapter.MessageAdapter;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
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

public class MessageActivity extends AppCompatActivity {

    private ProgressBar progress;
    private List<Message> mMessages;
    private MessageAdapter messageAdapter;
    private FirebaseAuth mAuth;
    private String friendEmail;
    private EditText mMessage;
    private ImageView mSend;
    private ImageView mImage;
    private RecyclerView mRecyclerView;
    private ImageView mLocation;

    private int switchformessagereceivetone;


    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        mMessage = findViewById(R.id.message);
        progress = findViewById(R.id.progress);
        mImage = findViewById(R.id.image);

        mMessages = new ArrayList<>();
        mSend = findViewById(R.id.send);
        mRecyclerView = findViewById(R.id.recycle_view);
        mLocation = findViewById(R.id.location);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        messageAdapter = new MessageAdapter(MessageActivity.this,mMessages);
        mRecyclerView.setAdapter(messageAdapter);
        mAuth = FirebaseAuth.getInstance();


        friendEmail = getIntent().getStringExtra("friendEmail");
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MessageActivity.this);






        getSupportActionBar().setTitle(friendEmail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        readMessages();
        onSendClick();
        onImageClick();
        onLocationClick();
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    private void onLocationClick() {

        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("myLocation","Clicked myLocation  ");
                if(ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                }
                if(ContextCompat.checkSelfPermission(MessageActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MessageActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                }

                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location!=null){
                            Log.d("myLocation","myLocation = "+location.toString());

                            try {

                                Geocoder geo = new Geocoder(MessageActivity.this,Locale.getDefault());
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

                            Dialog dialog = new Dialog(MessageActivity.this);
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
                        Toast.makeText(MessageActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }


    private void onImageClick() {
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MessageActivity.this,ShowImageActivity.class);
                intent.putExtra("friendEmail",friendEmail);
                startActivity(intent);
            }
        });

    }

    private void readMessages() {
        mMessages.clear();
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        final String myEmail = currentUser.getEmail();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Messages");
        Query query = ref.orderByChild("seconds");

        //switchformessagereceivetone=0;
        query.limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               Log.d("onChildAdded","onChildAdded  "+dataSnapshot);
                Log.d("onChildAdded","onChildAdded string  "+s);
               Message message = dataSnapshot.getValue(Message.class);
               if(switchformessagereceivetone==1 && currentUser.getEmail().equals(message.getTo())){
                   MediaPlayer mediaPlayer = MediaPlayer.create(MessageActivity.this,R.raw.messagealert);
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





                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    if((message.getFrom().equals(myEmail) && message.getTo().equals(friendEmail)) || (message.getFrom().equals(friendEmail) && message.getTo().equals(myEmail))){
                        mMessages.add(message);
                    }
                }
                //Log.d("ActivityMessage","messagecoung" + mMessages.size());
                messageAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mMessages.size()-1);

               /* int messageSize = mMessages.size()-1;
                if(messageSize>0){
               Message message = mMessages.get(messageSize);
               if(switchformessagereceivetone==1 && message.getTo().equals(currentUser.getEmail())){

                   //Toast.makeText(MessageActivity.this,"Meessage received",Toast.LENGTH_LONG).show();
                   MediaPlayer mediaPlayer =MediaPlayer.create(MessageActivity.this,R.raw.messagealert);
                   mediaPlayer.start();

               }}*/
             //   switchformessagereceivetone=1;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        progress.setVisibility(View.GONE);
    }

    private void onSendClick() {
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = mMessage.getText().toString();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(MessageActivity.this,"Empty Message",Toast.LENGTH_SHORT).show();
                }else{
                    long i = (long) (new Date().getTime()/1000);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat=  new SimpleDateFormat("dd-MMM-yyyy");
                    String date = simpleDateFormat.format(calendar.getTime());
                    simpleDateFormat=  new SimpleDateFormat("hh:mm:ss");
                    String time = simpleDateFormat.format(calendar.getTime());
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Messages");
                    HashMap<String,Object> map = new HashMap<>();
                    map.put("message",message);
                    map.put("date", time+"  "+date);
                    map.put("to",friendEmail);
                    map.put("from",mAuth.getCurrentUser().getEmail());
                    map.put("seconds",i);
                    map.put("imageUrl","default");
                    // map.put("id",message.get)
                    ref.push().setValue(map);


                    mRecyclerView.scrollToPosition(mMessages.size()-1);
                    mMessage.setText("");
                }
            }
        });
    }
}
