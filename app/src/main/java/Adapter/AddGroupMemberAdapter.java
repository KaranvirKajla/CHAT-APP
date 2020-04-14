package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.chatapp10.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.User;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddGroupMemberAdapter extends RecyclerView.Adapter<AddGroupMemberAdapter.ViewHolder>{
    private Context mContext;
    private List<User> mUsers;
    private String groupId;

    public AddGroupMemberAdapter(Context mContext, List<User> mUsers, String groupId) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.groupId = groupId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new AddGroupMemberAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final User user = mUsers.get(position);
        holder.email.setText(user.getEmail());
        holder.name.setText(user.getName());
        Picasso.get().load(user.getImageUrl()).placeholder(R.mipmap.ic_person).into(holder.imageProfile);

        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference ref= FirebaseDatabase.getInstance().getReference().child("Users").child(user.getId()).child("Groups");
                ref.push().setValue(groupId);
                DatabaseReference membersRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Members");
                membersRef.push().setValue(user.getId());

                holder.add.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView imageProfile;
        public TextView email;
        public TextView name;
        public Button add;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            add = itemView.findViewById(R.id.add);
        }
    }
}
