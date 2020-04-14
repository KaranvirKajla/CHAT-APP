package Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatapp10.MessageActivity;
import com.example.chatapp10.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.GroupMessage;
import Models.Message;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class GroupMessageAdapter extends RecyclerView.Adapter<GroupMessageAdapter.ViewHolder>{
    FirebaseAuth mAuth ;
    List<GroupMessage> mMessages;
    Context mContext;

    public GroupMessageAdapter(Context mContext, List<GroupMessage> mMessages) {
        this.mMessages = mMessages;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_message_item,parent,false);

        return new GroupMessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final GroupMessage message = mMessages.get(position);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String id = currentUser.getUid();
        if(!message.getImageUrl().equals("default")){
            Picasso.get().load(message.getImageUrl()).placeholder(R.mipmap.ic_person).into(holder.image);
        }else{

            // holder.image.setVisibility(View.GONE);
            holder.image.setImageDrawable(null);
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(message.getFrom()).child("name");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(String.class);
                holder.creator.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.message.setText(message.getMessage());
        holder.date.setText(message.getDate());
        Log.d("adapterMessage","id = "+id);
        if(message.getFrom().equals(id)){
            Log.d("adapterMessage",message.getFrom() + " "+message.getMessage());
            holder.cardView.setBackgroundColor(Color.GREEN);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.cardView.setLayoutParams(params);
        }else{

            holder.cardView.setBackgroundColor(Color.WHITE);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.cardView.setLayoutParams(params);

        }

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView message;
        public TextView date;
        public CardView cardView;
        public ImageView image;
        public TextView creator;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            date  = itemView.findViewById(R.id.date);
            cardView = itemView.findViewById(R.id.card);
            image  = itemView.findViewById(R.id.imageMessage);
            creator = itemView.findViewById(R.id.creator);
        }
    }
}
