package com.example.myapplication;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatAdapterHolder> {

    LayoutInflater inflater;
    ArrayList<Map<String, Object>> messages;
    FirebaseDb firebaseDb = FirebaseDb.getInstance();

    public ChatAdapter(Context context, ArrayList<Map<String, Object>> messagesData) {
        this.inflater = LayoutInflater.from(context);
        this.messages = messagesData;
    }

    public void insert(ArrayList<Map<String, Object>> newMessages) {
        this.messages = newMessages;
    }

    @NonNull
    @Override
    public ChatAdapterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.single_chat_message, parent, false);
        return new ChatAdapterHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull ChatAdapterHolder holder, int position) {

        holder.messageTime.setText(messages.get(position).get("timestamp").toString());
        holder.message.setText(messages.get(position).get("message").toString());

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class ChatAdapterHolder extends RecyclerView.ViewHolder {

        LinearLayout container;
        TextView message, messageTime;

        public ChatAdapterHolder(@NonNull View itemView) {
            super(itemView);
            container       = (LinearLayout)    itemView.findViewById(R.id.chat_message_container);
            message         = (TextView)        itemView.findViewById(R.id.chat_message);
            messageTime     = (TextView)        itemView.findViewById(R.id.chat_message_time);
        }

    }
}
