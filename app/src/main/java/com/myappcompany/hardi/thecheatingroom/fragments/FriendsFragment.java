package com.myappcompany.hardi.thecheatingroom.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.myappcompany.hardi.thecheatingroom.R;
import com.myappcompany.hardi.thecheatingroom.activities.ChatActivity;
import com.myappcompany.hardi.thecheatingroom.activities.ProfileActivity;
import com.myappcompany.hardi.thecheatingroom.activities.UsersActivity;
import com.myappcompany.hardi.thecheatingroom.model.Friends;
import com.myappcompany.hardi.thecheatingroom.model.Users;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);
        mFriendsList = mMainView.findViewById(R.id.friends_list);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");


        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> friendsFirebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Friends>()
                .setQuery(mFriendsDatabase, Friends.class)
                .build();

        FirebaseRecyclerAdapter<Friends, FriendsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(friendsFirebaseRecyclerOptions) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull Friends model) {
                        final String list_user_id = getRef(position).getKey();

                        mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final String username = dataSnapshot.child("name").getValue().toString();
                                String status = dataSnapshot.child("status").getValue().toString();
                                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                                if(dataSnapshot.hasChild("online")){
                                    String userOnline = dataSnapshot.child("online").getValue().toString();
                                    holder.setUserOnline(userOnline);
                                }

                                holder.tv_usersSingle_username.setText(username);
                                holder.tv_usersSingle_status.setText(status);

                                if (!thumb_image.equals("default")) {
                                    Picasso.get().load(thumb_image).placeholder(R.drawable.avatar).into(holder.iv_usersSingle_image);
                                }

                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CharSequence options[] = new CharSequence[]{"Open Profile", "Send message"};
                                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                        builder.setTitle("Select Options");
                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int i) {
                                                //Click Event for each item
                                                if(i == 0){
                                                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                                                    intent.putExtra("user_id", list_user_id);
                                                    startActivity(intent);
                                                }

                                                if(i == 1){
                                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                                    intent.putExtra("user_id", list_user_id);
                                                    intent.putExtra("user_name", username);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                        builder.show();
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_sigle_item, viewGroup, false);
                        return new FriendsViewHolder(view);
                    }
                };

        mFriendsList.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_usersSingle_username, tv_usersSingle_status;
        private CircleImageView iv_usersSingle_image;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_usersSingle_username = itemView.findViewById(R.id.user_single_name);
            tv_usersSingle_status = itemView.findViewById(R.id.user_single_status);
            iv_usersSingle_image = itemView.findViewById(R.id.user_single_image);
        }

        public void setUserOnline(String online_status){
            ImageView userOnlineView = itemView.findViewById(R.id.user_single_online_icon);
            if(online_status.equals("true")){
                userOnlineView.setVisibility(View.VISIBLE);
            } else {
                userOnlineView.setVisibility(View.INVISIBLE);
            }
        }
    }
}
