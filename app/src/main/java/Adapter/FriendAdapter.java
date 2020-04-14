package Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.chatapp10.MessageActivity;
import com.example.chatapp10.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.User;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;


public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>{
    List<User> mFriends;
    Context mContext;

    public FriendAdapter(Context mContext,List<User> mFriends) {
        this.mFriends = mFriends;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.friend_item,parent,false);
        return new FriendAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User friend = mFriends.get(position);
        holder.email.setText(friend.getEmail());
        holder.name.setText(friend.getName());

        Picasso.get().load(friend.getImageUrl()).placeholder(R.mipmap.ic_person).into(holder.imageView);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("friendEmail", friend.getEmail());
                mContext.startActivity(intent);
            }
        });


        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder alertadd = new AlertDialog.Builder(mContext);
                LayoutInflater factory = LayoutInflater.from(mContext);
                final View view = factory.inflate(R.layout.profile_pic_dialog, null);

// change the ImageView image source
                final ImageView dialogImageView = (ImageView) view.findViewById(R.id.image);

                Picasso.get().load(friend.getImageUrl()).placeholder(R.mipmap.ic_person).into(dialogImageView);
                alertadd.setView(view);
                alertadd.setTitle(friend.getName());
                alertadd.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dlg, int sumthin) {
                            dlg.dismiss();
                    }
                });

                alertadd.show();
                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mFriends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView imageView;
        public TextView email;
        public TextView name;
        public LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            linearLayout = itemView.findViewById(R.id.linear_layout);
        }
    }
}
