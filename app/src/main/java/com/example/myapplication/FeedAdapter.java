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
        String userEmail = "";

        if (posts.get(position).get("Email") != null) {
            userEmail = posts.get(position).get("Email").toString();
        }

       /* holder.firstN.setText(posts.get(position).get("First name").toString());
        holder.lastN.setText(posts.get(position).get("Last name").toString());
        holder.city.setText(posts.get(position).get("City").toString());
        holder.details.setText(posts.get(position).get("Details").toString());

        */


        if (firebaseDb.isSignedIn() && firebaseDb.getCurrentUser().get("Email").toString().equals(userEmail)) {
             holder.makeContact.setVisibility(View.GONE);
        }

        holder.makeContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otherUserEmail = posts.get(position).get("Email").toString();
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
        TextView firstN, lastN, city, details;
        LinearLayout feedDetails;
        Button makeContact;

        public FeedAdapterHolder(View itemView) {
            super(itemView);
            container           = (LinearLayout)    itemView.findViewById(R.id.feed_item_container);
            feedDetails         = (LinearLayout)    itemView.findViewById(R.id.feed_details);
            firstN      = (TextView)        itemView.findViewById(R.id.my_account_firstname);
            lastN   = (TextView)        itemView.findViewById(R.id.my_account_lastname);
            city     = (TextView)        itemView.findViewById(R.id.post_city);
            details     = (TextView)        itemView.findViewById(R.id.post_details);
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
