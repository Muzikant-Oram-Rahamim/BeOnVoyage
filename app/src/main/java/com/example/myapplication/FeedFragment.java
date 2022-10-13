package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment implements FeedAdapter.onItemClick {

    // Add RecyclerView member;
    private RecyclerView recyclerView;
    private ArrayList<Map<String, Object>> posts = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment feed.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_feed, container, false);

        Context context = this.getContext();

//        Add the following lines to create RecyclerView
//        recyclerView = view.findViewById(R.id.feed_view);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//        recyclerView.setAdapter(new FeedAdapter(context, posts));

        FirebaseDb firebaseDb = FirebaseDb.getInstance();
        firebaseDb.getAllPosts(new FirebaseCallbacks() {
            @Override
            public void onPostsLoaded(ArrayList postsInDb) {
                posts = postsInDb;
                recyclerView = view.findViewById(R.id.feed_view);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                recyclerView.setAdapter(new FeedAdapter(context, posts));
            }
        });

        firebaseDb.listenForUpdates(new FirebaseCallbacks() {
            @Override
            public void newPosts() {
                firebaseDb.getAllPosts(new FirebaseCallbacks() {
                    @Override
                    public void onPostsLoaded(ArrayList posts) {
                        FeedAdapter feedAdapter = (FeedAdapter) recyclerView.getAdapter();
                        feedAdapter.insert(posts);
                        recyclerView.getAdapter().notifyItemInserted(0);
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onClick(int position) {

    }
}