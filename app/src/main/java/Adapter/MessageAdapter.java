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
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.Message;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
    FirebaseAuth mAuth ;
    List<Message> mMessages;
    Context mContext;

    public MessageAdapter(Context mContext, List<Message> mMessages) {
        this.mMessages = mMessages;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.message_item,parent,false);

        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = mMessages.get(position);
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String myEmail = currentUser.getEmail();
        if(!message.getImageUrl().equals("default")){
            Picasso.get().load(message.getImageUrl()).placeholder(R.mipmap.ic_person).into(holder.image);
        }else{

            // holder.image.setVisibility(View.GONE);
            holder.image.setImageDrawable(null);
        }
        holder.message.setText(message.getMessage());
        holder.date.setText(message.getDate());
        Log.d("adapterMessage","myEmail = "+myEmail);
        if(message.getFrom().equals(myEmail)){
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            date  = itemView.findViewById(R.id.date);
            cardView = itemView.findViewById(R.id.card);
            image  = itemView.findViewById(R.id.imageMessage);
        }
    }
}
