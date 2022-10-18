package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Map;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsAdapterHolder> {

    LayoutInflater inflater;
    ArrayList<Map<String, Object>> contacts;
    FirebaseDb firebaseDb = FirebaseDb.getInstance();

    public ContactsAdapter(Context context, ArrayList<Map<String, Object>> contactsData) {
        this.inflater = LayoutInflater.from(context);
        this.contacts = contactsData;
    }

    @NonNull
    @Override
    public ContactsAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_contact_item, parent, false);
        return new ContactsAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsAdapterHolder holder, @SuppressLint("RecyclerView") int position) {

        String otherUserEmail = ((ArrayList) contacts.get(position).get("contacts")).get(0).toString();
        if (firebaseDb.getCurrentUser().get("Email").toString().equals(otherUserEmail)) {
            otherUserEmail = ((ArrayList) contacts.get(position).get("contacts")).get(1).toString();
        }

        holder.contactName.setText(otherUserEmail);

        String finalOtherUserEmail = otherUserEmail;
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                firebaseDb.checkChatBetweenUsers(finalOtherUserEmail, new FirebaseCallbacks() {
                    @Override
                    public void startChat() {
                        FragmentManager fragmentManager = ((FragmentActivity) view.getContext()).getSupportFragmentManager();
                        Bundle args = new Bundle();
                        args.putString("otherUserEmail", finalOtherUserEmail);
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
        return contacts.size();
    }

    class ContactsAdapterHolder extends RecyclerView.ViewHolder {

        LinearLayout container;
        TextView contactName, lastMessage;

        public ContactsAdapterHolder(@NonNull View itemView) {
            super(itemView);
            container       = (LinearLayout)    itemView.findViewById(R.id.contact_item_container);
            contactName     = (TextView)        itemView.findViewById(R.id.contact_name);
            lastMessage     = (TextView)        itemView.findViewById(R.id.last_message);
        }

    }

}
