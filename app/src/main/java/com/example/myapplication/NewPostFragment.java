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
        TextInputLayout petNameLayout = view.findViewById(R.id.new_post_pet_name_layout);
        TextInputEditText petNameField = view.findViewById(R.id.new_post_pet_name);

        TextInputLayout petAgeLayout = view.findViewById(R.id.new_post_pet_age_layout);
        TextInputEditText petAgeField = view.findViewById(R.id.new_post_pet_age);

        TextInputLayout petGenderLayout = view.findViewById(R.id.new_post_pet_gender_layout);
        AutoCompleteTextView petGenderField = view.findViewById(R.id.new_post_pet_gender);

        TextInputLayout petSizeLayout = view.findViewById(R.id.new_post_pet_size_layout);
        AutoCompleteTextView petSizeField = view.findViewById(R.id.new_post_pet_size);

        TextInputLayout postDescriptionLayout = view.findViewById(R.id.new_post_desc_layout);
        TextInputEditText postDescriptionField = view.findViewById(R.id.new_post_desc);

        Button postButton = (Button) view.findViewById(R.id.new_post_publish_post);

        CircularProgressIndicator loader = view.findViewById(R.id.new_post_loader);

        // Init pet genders dropdown
        String[] genders = getResources().getStringArray(R.array.pet_genders);
        ArrayAdapter<CharSequence> gendersArrayAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.pet_genders, R.layout.pet_dropdown_item);
        petGenderField.setAdapter(gendersArrayAdapter);
        petGenderField.setThreshold(1);

        // Init pet sizes dropdown
        String[] sizes = getResources().getStringArray(R.array.pet_genders);
        ArrayAdapter<CharSequence> sizesArrayAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.pet_sizes, R.layout.pet_dropdown_item);
        petSizeField.setAdapter(sizesArrayAdapter);
        petSizeField.setThreshold(1);

        Map<String, Object> currentUser = FirebaseDb.getCurrentUser();
        if (currentUser.containsKey("pet_name")) {
            petNameField.setText(Objects.requireNonNull(currentUser.get("pet_name")).toString());
        } else {
            petNameField.setEnabled(true);
        }
        if (currentUser.containsKey("pet_age")) {
            petAgeField.setText(Objects.requireNonNull(currentUser.get("pet_age")).toString());
        } else {
            petAgeField.setEnabled(true);
        }
        if (currentUser.containsKey("pet_gender")) {
            petGenderField.setText(Objects.requireNonNull(currentUser.get("pet_gender")).toString(), false);
        } else {
            petGenderField.setEnabled(true);
        }
        if (currentUser.containsKey("pet_size")) {
            petSizeField.setText(Objects.requireNonNull(currentUser.get("pet_size")).toString(), false);
        } else {
            petSizeField.setEnabled(true);
        }

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

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loader.setVisibility(View.VISIBLE);
                boolean hasErrors = false;
                Map<String, Object> postData = new HashMap<>();
                postData.put("email", FirebaseDb.getCurrentUser().get("email").toString());
                postData.put("timestamp", new Timestamp(System.currentTimeMillis()));
                if (petNameField.getEditableText().toString().isEmpty()) {
                    postDescriptionLayout.setError("Required");
                    hasErrors = true;
                } else {
                    postData.put("pet_name", petNameField.getEditableText().toString());
                }
                if (petAgeField.getEditableText().toString().isEmpty()) {
                    petAgeLayout.setError("Required");
                    hasErrors = true;
                } else {
                    postData.put("pet_age", petAgeField.getEditableText().toString());
                }
                if (petGenderField.getEditableText().toString().isEmpty()) {
                    petGenderLayout.setError("Required");
                    hasErrors = true;
                } else {
                    String pet_gender = petGenderField.getEditableText().toString();
                    String pet_gender_to_save;
                    switch (pet_gender) {
                        case "זכר":
                            pet_gender_to_save = "Male";
                            break;
                        case "נקבה":
                            pet_gender_to_save = "Female";
                            break;
                        default:
                            pet_gender_to_save = pet_gender;
                            break;
                    }
                    postData.put("pet_gender", pet_gender_to_save);
                }
                if (petSizeField.getEditableText().toString().isEmpty()) {
                    petSizeLayout.setError("Required");
                } else {
                    String pet_size = petSizeField.getEditableText().toString();
                    String pet_size_to_save;
                    switch (pet_size) {
                        case "קטן":
                            pet_size_to_save = "Small";
                            break;
                        case "בינוני":
                            pet_size_to_save = "Medium";
                            break;
                        case "גדול":
                            pet_size_to_save = "Large";
                            break;
                        default:
                            pet_size_to_save = pet_size;
                            break;
                    }
                    postData.put("pet_size", pet_size_to_save);
                }
                if (postDescriptionField.getEditableText().toString().isEmpty()) {
                    postDescriptionLayout.setError("Required");
                    hasErrors = true;
                } else {
                    postData.put("post_description", postDescriptionField.getEditableText().toString());
                }
//                if (bitmap == null) {
//                    Toast.makeText(getActivity(), "Please add an image", Toast.LENGTH_SHORT).show();
//                    hasErrors = true;
//                }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2 && requestCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            newPostImageView.setImageURI(imageUri);
        }

//        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
//            bitmap = (Bitmap) data.getExtras().get("data");
//            newPostImageView.setImageBitmap(bitmap);
//        }
    }
}