package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.chatapp10.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.User;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupMemberAdapter extends RecyclerView.Adapter<GroupMemberAdapter.ViewHolder>{
    private Context mContext;
    private List<User> mMembers;
    private String groupId;

    public GroupMemberAdapter(Context mContext, List<User> mMembers, String groupId) {
        this.mContext = mContext;
        this.mMembers = mMembers;
        this.groupId = groupId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.member_item,parent,false);
        return new GroupMemberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = mMembers.get(position);

        Picasso.get().load(user.getImageUrl()).placeholder(R.mipmap.ic_group).into(holder.image);
        holder.name.setText(user.getName());
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("creator");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String creatorId = dataSnapshot.getValue(String.class);
                if(user.getId().equals(creatorId)){
                    holder.creator.setText("Creator");
                }else{
                    holder.creator.setText("");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return mMembers.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView creator;
        public CircleImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            creator = itemView.findViewById(R.id.creator);
            image = itemView.findViewById(R.id.image);
        }
    }
}
