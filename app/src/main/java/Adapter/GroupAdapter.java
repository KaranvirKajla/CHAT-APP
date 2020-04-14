package Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chatapp10.GroupMessageActivity;
import com.example.chatapp10.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

import Models.Group;
import Models.User;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder>{
    FirebaseAuth mAuth;

    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users");
    public Context mContext;
    public List<Group> mGroups;

    public GroupAdapter(Context mContext, List<Group> mGroups) {
        this.mContext = mContext;
        this.mGroups = mGroups;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_item,parent,false);
        return new GroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GroupAdapter.ViewHolder holder, int position) {
        final Group group = mGroups.get(position);
        Log.d("karan","   "+group.getName());

        holder.name.setText(group.getName());

        Picasso.get().load(group.getImageUrl()).placeholder(R.mipmap.ic_group).into(holder.imageProfile);

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(mContext, GroupMessageActivity.class);
                intent.putExtra("groupId",group.getId());
                mContext.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public CircleImageView imageProfile;
        public LinearLayout linearLayout;
        public TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linear_layout);
            imageProfile = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
        }
    }
}
