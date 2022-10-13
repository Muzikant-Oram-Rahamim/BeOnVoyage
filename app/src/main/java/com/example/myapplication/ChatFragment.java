package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    private String otherUserEmail = null;
    private RecyclerView recyclerView;
    private ArrayList<Map<String, Object>> contacts = new ArrayList<>();
    private FirebaseDb firebaseDb = FirebaseDb.getInstance();
//    private ArrayList<Map<String, Object>> messages = new ArrayList<>();

    public ChatFragment() {
        // Required empty public constructor
    }

    public ChatFragment newInstance(String otherUserEmail) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = this.getArguments();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            otherUserEmail = bundle.getString("otherUserEmail", "");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        Context context = this.getContext();

        recyclerView = view.findViewById(R.id.messages_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new ChatAdapter(context, firebaseDb.getAllMessages()));

        TextView otherUserEmailView = view.findViewById(R.id.messages_other_contact);
        otherUserEmailView.setText(otherUserEmail);

        TextInputEditText messageView = view.findViewById(R.id.new_message);
        Button sendMessage = view.findViewById(R.id.send_message);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDb.addNewMessage(otherUserEmail, messageView.getEditableText().toString(), new FirebaseCallbacks() {
                    @Override
                    public void messageSent() {
                        messageView.setText("");
                    }
                });
            }
        });

        firebaseDb.listenForNewMessages(otherUserEmail, new FirebaseCallbacks() {
            @Override
            public void newMessage(ArrayList newMessages) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(context);
                layoutManager.setStackFromEnd(true);
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setAdapter(new ChatAdapter(context, firebaseDb.getAllMessages()));
                recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
            }
        });

        return view;
    }
}