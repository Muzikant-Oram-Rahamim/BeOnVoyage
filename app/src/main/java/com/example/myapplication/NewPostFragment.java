package com.example.myapplication;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewPostFragment extends Fragment {

    final int CAMERA_REQUEST = 1;
    ImageView newPostImageView;
    Bitmap bitmap;
    //    private DatabaseReference root = FirebaseDatabase.getInstance().getReference("image");
    private StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri imageUri;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewPost.
     */
    // TODO: Rename and change types and number of parameters
    public static NewPostFragment newInstance(String param1, String param2) {
        NewPostFragment fragment = new NewPostFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_new_post, container, false);

//        Get fragment's elements


        TextView postCityField = view.findViewById(R.id.post_city);
        TextView postDetailsField = view.findViewById(R.id.post_details);

        Button postButton = (Button) view.findViewById(R.id.new_post_publish_post);

        CircularProgressIndicator loader = view.findViewById(R.id.new_post_loader);


        Map<String, Object> currentUser = FirebaseDb.getCurrentUser();
        postCityField.setText(currentUser.get("City").toString());
        postDetailsField.setText(currentUser.get("Details").toString());

        /*
        // Upload image
        newPostImageView = view.findViewById(R.id.new_post_image);
        Button takePictureButton = view.findViewById(R.id.new_post_take_picture);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        });

         */

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loader.setVisibility(View.VISIBLE);
                boolean hasErrors = false;
                Map<String, Object> postData = new HashMap<>();
                postData.put("Email", FirebaseDb.getCurrentUser().get("Email").toString());
                postData.put("timestamp", new Timestamp(System.currentTimeMillis()));


                if (!hasErrors) {
                    FirebaseDb firebaseDb = FirebaseDb.getInstance();
                    firebaseDb.addPost(postData, new FirebaseCallbacks() {
                        @Override
                        public void onSuccessfullPost() {
                            loader.setVisibility(View.GONE);
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager
                                    .beginTransaction()
                                    .replace(R.id.fragment_container, new FeedFragment())
                                    .commit();
                            fragmentManager.popBackStackImmediate();
                        }

                        @Override
                        public void onFailedPost() {
                            loader.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        return view;

    }
}
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && requestCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            newPostImageView.setImageURI(imageUri);
        }


    }
}

 */