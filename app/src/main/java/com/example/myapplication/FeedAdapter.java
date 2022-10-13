package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedAdapterHolder> {

    LayoutInflater inflater;
    ArrayList<Map<String, Object>> posts;
    FirebaseDb firebaseDb = FirebaseDb.getInstance();

    public FeedAdapter(Context context, ArrayList<Map<String, Object>> postData) {
        this.inflater = LayoutInflater.from(context);
        this.posts = postData;
    }

    public void insert(ArrayList<Map<String, Object>> newPosts) {
        this.posts = newPosts;
    }

    @NonNull
    @Override
    public FeedAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_feed_item, parent, false);
        return new FeedAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedAdapterHolder holder, @SuppressLint("RecyclerView") int position) {

        char petGender = posts.get(position).get("pet_gender").toString().charAt(0);
        char petSize = posts.get(position).get("pet_size").toString().charAt(0);

        String userEmail = "";

        if (posts.get(position).get("email") != null) {
            userEmail = posts.get(position).get("email").toString();
        }

        holder.petName.setText(posts.get(position).get("pet_name").toString());
        holder.postDesc.setText(posts.get(position).get("post_description").toString() + "\n\nClick for details.");
        holder.feedPetAgeView.setText(posts.get(position).get("pet_age").toString());
        holder.feedPetGenderView.setText(String.valueOf(petGender));
        holder.feedPetSizeView.setText(String.valueOf(petSize));

        if (firebaseDb.isSignedIn() && firebaseDb.getCurrentUser().get("email").toString().equals(userEmail)) {
             holder.makeContact.setVisibility(View.GONE);
        }

        holder.makeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otherUserEmail = posts.get(position).get("email").toString();
                FirebaseDb firebaseDb = FirebaseDb.getInstance();
                firebaseDb.checkChatBetweenUsers(otherUserEmail, new FirebaseCallbacks() {
                    @Override
                    public void startChat() {
                        FragmentManager fragmentManager = ((FragmentActivity) view.getContext()).getSupportFragmentManager();
                        Bundle args = new Bundle();
                        args.putString("otherUserEmail", otherUserEmail);
                        ChatFragment chatFragment = new ChatFragment();
                        chatFragment.setArguments(args);
                        fragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, chatFragment)
                                .commit();
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class FeedAdapterHolder extends RecyclerView.ViewHolder {

        LinearLayout container;
        TextView petName, postDesc, feedPetAgeView, feedPetGenderView, feedPetSizeView;
        LinearLayout feedDetails;
        Button makeContact;

        public FeedAdapterHolder(View itemView) {
            super(itemView);
            container           = (LinearLayout)    itemView.findViewById(R.id.feed_item_container);
            petName             = (TextView)        itemView.findViewById(R.id.feed_pet_name);
            postDesc            = (TextView)        itemView.findViewById(R.id.feed_post_desc);
            feedDetails         = (LinearLayout)    itemView.findViewById(R.id.feed_details);
            feedPetAgeView      = (TextView)        itemView.findViewById(R.id.feed_item_age);
            feedPetGenderView   = (TextView)        itemView.findViewById(R.id.feed_item_gender);
            feedPetSizeView     = (TextView)        itemView.findViewById(R.id.feed_item_size);
            makeContact         = (Button)          itemView.findViewById(R.id.feed_make_contact);

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (feedDetails.getVisibility() == View.GONE) {
                        feedDetails.setVisibility(View.VISIBLE);
                    } else {
                        feedDetails.setVisibility(View.GONE);
                    }
                }
            });

        }
    }

    public interface onItemClick {
        void onClick(int position);
    }

}
