package com.example.chatapp10;

import Adapter.MessageAdapter;
import Models.Message;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
        messageAdapter = new MessageAdapter(MessageActivity.this,mMessages);
        mRecyclerView.setAdapter(messageAdapter);
        mAuth = FirebaseAuth.getInstance();

        friendEmail = getIntent().getStringExtra("friendEmail");

        getSupportActionBar().setTitle(friendEmail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        readMessages();
        onSendClick();
        onImageClick();
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
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String myEmail = currentUser.getEmail();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Messages");
        Query query = ref.orderByChild("seconds");
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
                Log.d("ActivityMessage","messagecoung" + mMessages.size());
                messageAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(mMessages.size()-1);
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
