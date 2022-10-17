package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyAccountFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyAccountFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyAccountFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyAccountFragment newInstance(String param1, String param2) {
        MyAccountFragment fragment = new MyAccountFragment();
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
        View view = inflater.inflate(R.layout.fragment_my_account, container, false);

        TextInputEditText emailField = view.findViewById(R.id.my_account_email);
        TextInputEditText passwordField = view.findViewById(R.id.my_account_password);
        TextInputEditText passwordValidateField = view.findViewById(R.id.my_account_validate_password);
        TextInputEditText firstnameField = view.findViewById(R.id.my_account_firstname);
        TextInputEditText lastnameField = view.findViewById(R.id.my_account_lastname);


        Button updateButton = view.findViewById(R.id.my_account_button);

        Map<String, Object> currentUser = FirebaseDb.getCurrentUser();

        if (currentUser == null) {
            emailField.setText("This screen is for signed-ing users!");
        } else {
            emailField.setText((String)currentUser.get("email"));
            if (currentUser.containsKey("First name")) {
                firstnameField.setText(currentUser.get("First name").toString());
            }
            if (currentUser.containsKey("Last name")) {
                lastnameField.setText(currentUser.get("Last name").toString());
            }

        }

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> newUserData = new HashMap<>();
                if (!passwordField.getEditableText().toString().equals("")) {
                    if (!passwordField.getEditableText().toString().equals(passwordValidateField.getEditableText().toString())) {
                        System.out.println("Passwords don't match");
                        Toast.makeText(getActivity(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                    } else {
                        newUserData.put("password", passwordField.getEditableText().toString());
                    }
                }
                if (!firstnameField.getEditableText().toString().equals("")) {
                    newUserData.put("firstname", firstnameField.getEditableText().toString());
                }
                if (!lastnameField.getEditableText().toString().equals("")) {
                    newUserData.put("lastname", lastnameField.getEditableText().toString());
                }

                
                FirebaseDb firebaseDb = FirebaseDb.getInstance();
                firebaseDb.updateUserData(newUserData, new FirebaseCallbacks() {
                    @Override
                    public void userUpdated() {
                        Toast.makeText(getActivity(), "Details Saved", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return view;
    }
}