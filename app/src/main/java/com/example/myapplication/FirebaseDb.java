package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDb {

    private static Map<String, Object> currentUser = null;
    private static FirebaseDb firebaseDb = null;
    private static Map<String, Object> posts = new HashMap<>();
    private static Map<String, Object> chats = new HashMap<>();
    private static String lastChatID = null;
    private static ArrayList<Map<String, Object>> messages = new ArrayList<>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FirebaseDb getInstance() {
        if (firebaseDb == null) {
            firebaseDb = new FirebaseDb();
        }
        return firebaseDb;
    }

    public void listenForUpdates(FirebaseCallbacks callbacks) {
        CollectionReference collectionReference = db.collection("posts");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listing failed: " + e);
                    return;
                }

                callbacks.newPosts();

            }
        });
    }

    public boolean isSignedIn() {
        if (currentUser == null)
            return false;
        return true;
    }

    public static Map<String, Object> getCurrentUser() {
        return currentUser;
    }

    public void isUserExists(String email, FirebaseCallbacks callback) {
        db.collection("users").document(email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData() != null && documentSnapshot.getData().get("email").equals(email)) {
                            callback.foundUser(documentSnapshot.getData());
                        } else {
                            callback.foundUser(documentSnapshot.getData());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Found nothing");
                    }
                });
    }

    public boolean signUp(String email, String password, String validatePassword, FirebaseCallbacks callback) {

        if (!password.equals(validatePassword)) {
            return false;
        }

        this.isUserExists(email, new FirebaseCallbacks() {
            @Override
            public void foundUser(Map<String, Object> foundUser) {
                if (foundUser != null && !foundUser.isEmpty()) {
                    callback.onSignup(true);
                } else {
                    Map<String, Object> user = new HashMap<>();
                    user.put("email", email);
                    user.put("password", password);

                    db.collection("users")
                            .document(email)
                            .set(user, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    System.out.println("Succeed");
                                    currentUser = user;
                                    callback.onSignIn();
                                    }
                                })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("Failed");
                                }
                            });
                }
            }
        });

        return true;

    }

    public void signIn(String email, String password, FirebaseCallbacks callbacks) {
        this.isUserExists(email, new FirebaseCallbacks() {
            @Override
            public void foundUser(Map<String, Object> foundUser) {
                if (foundUser == null || foundUser.isEmpty()) {
                    callbacks.onSignInFailed("Could not find user");
                } else {
                    if (!foundUser.get("password").equals(password)) {
                        callbacks.onSignInFailed("Wrong password");
                    } else {
                        currentUser = foundUser;
                        callbacks.onSignIn();
                    }
                }
            }
        });
    }

    public void signOut(FirebaseCallbacks callbacks) {
        currentUser = null;
        callbacks.signedOut();
    }

    public void updateUserData(Map<String, Object> newUserData, FirebaseCallbacks callbacks) {

        db.collection("users")
                .document((String) currentUser.get("email"))
                .update(newUserData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        currentUser = newUserData;
                        callbacks.userUpdated();
                    }
                });
    }

    public void addPost(Map<String, Object> postData, FirebaseCallbacks callbacks) {
        db.collection("posts")
                .add(postData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        callbacks.onSuccessfullPost();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbacks.onFailedPost();
                    }
                });
    }

    public void getAllPosts(FirebaseCallbacks callbacks) {
        db.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Map<String, Object> newPosts = new HashMap<>();
                        ArrayList<Map<String, Object>> postsInDb = new ArrayList();
                        for (DocumentSnapshot doc : queryDocumentSnapshots) {
                            newPosts.put(doc.getId(), doc.getData());
                            postsInDb.add(doc.getData());
                        }
                        posts = newPosts;
                        callbacks.onPostsLoaded(postsInDb);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Failed");
                        System.out.println(e);
                    }
                });
    }

    public void getChats(FirebaseCallbacks callbacks) {
        db.collection("chats")
                .orderBy("last_message_date")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            Map<String, Object> newChats = new HashMap<>();
                            ArrayList<Map<String, Object>> chatsInDb = new ArrayList();
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                newChats.put(doc.getId(), doc.getData());
                                chatsInDb.add(doc.getData());
                            }
                            chats = newChats;
                            callbacks.onChatsLoaded(chatsInDb);
                        }
                    }
                });
    }

    private void createNewChat(String otherUser, FirebaseCallbacks callbacks) {
        Map<String, Object> chatProperties = new HashMap<>();
        chatProperties.put("contacts", Arrays.asList(currentUser.get("email").toString(), otherUser));
        chatProperties.put("last_message_date", new Timestamp(System.currentTimeMillis()));
        chatProperties.put("messages", new ArrayList<Map<String, Object>>());
        db.collection("chats")
                .add(chatProperties)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        callbacks.chatCreated(documentReference.getId());
                    }
                });
    }

    public void checkChatBetweenUsers(String otherUser, FirebaseCallbacks callbacks) {
        db.collection("chats")
                .whereArrayContains("contacts", currentUser.get("email").toString())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.size() == 0) {
                            createNewChat(otherUser, new FirebaseCallbacks() {
                                @Override
                                public void chatCreated(String chatId) {
                                    lastChatID = chatId;
                                }
                            });
                        } else {
                            Map<String, Object> chat = null;
                            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                                ArrayList contactsInDoc = (ArrayList) doc.getData().get("contacts");
                                if (contactsInDoc.contains(otherUser)) {
                                    lastChatID = doc.getId();
                                    messages = (ArrayList<Map<String, Object>>)doc.get("messages");
                                    break;
                                }
                            }
                        }
                        callbacks.startChat();
                    }
                });
    }

    public ArrayList<Map<String, Object>> getAllMessages() {
        try {
            Map<String, Object> currentChat = (Map<String, Object>) chats.get(lastChatID);
            return (ArrayList<Map<String, Object>>) currentChat.get("messages");
        } catch (NullPointerException exception) {
            return new ArrayList<>();
        }
    }

    public void addNewMessage(String otherUserEmail, String message, FirebaseCallbacks callbacks) {
        Map<String, Object> messageObj = new HashMap<>();
        messageObj.put("message", message);
        messageObj.put("sender", currentUser.get("email").toString());
        messageObj.put("timestamp", new Timestamp(System.currentTimeMillis()));
        db.collection("chats")
                .document(lastChatID)
                .update("messages", FieldValue.arrayUnion(messageObj))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callbacks.messageSent();
                    }
                });
    }

    public void listenForNewMessages(String otherUserEmail, FirebaseCallbacks callbacks) {
        Query query = db.collection("chats")
                .whereArrayContains("contacts", currentUser.get("email").toString());
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listing failed: " + e);
                    return;
                }

                System.out.println("Got new message(s)!");
                getChats(new FirebaseCallbacks() {
                    @Override
                    public void onChatsLoaded(ArrayList<Map<String, Object>> chatsInDb) {
                        Map<String, Object> currentChat = (Map<String, Object>) chats.get(lastChatID);
                        callbacks.newMessage((ArrayList) currentChat.get("messages"));
                    }
                });

            }
        });
    }

}
