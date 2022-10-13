package com.example.myapplication;

import java.util.ArrayList;
import java.util.Map;

interface FirebaseCallbacks {
    default void onSignup(boolean isExists) {}
    default void foundUser(Map<String, Object> foundUser) {}
    default void onSignIn() {}
    default void onSignInFailed(String errorMessage) {}
    default void onAccountDataUpdated() {}
    default void onSuccessfullPost() {}
    default void onFailedPost() {}
    default void onPostsLoaded(ArrayList posts) {}
    default void newPosts() {}
    default void onChatsLoaded(ArrayList<Map<String, Object>> chatsInDb) {}
    default void signedOut() {}
    default void startChat() {}
    default void chatCreated(String chatId) {}
    default void messageSent() {}
    default void newMessage(ArrayList messages) {}
    default void userUpdated() {}
}
