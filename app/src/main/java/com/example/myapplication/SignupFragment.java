package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SignupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SignupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SignupFragment newInstance(String param1, String param2) {
        SignupFragment fragment = new SignupFragment();
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
        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        // Elements
        TextInputEditText FnameField = view.findViewById(R.id.signup_firstname);
        TextInputEditText LnameField = view.findViewById(R.id.signup_lastname);
        TextInputEditText emailField = view.findViewById(R.id.signup_email);
        TextInputEditText passwordField = view.findViewById(R.id.signup_password);
        TextInputEditText validatePasswordField = view.findViewById(R.id.signup_validate_password);
        CircularProgressIndicator loader = view.findViewById(R.id.signup_loader);
        Button signupButton = view.findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loader.setVisibility(View.VISIBLE);
                FirebaseDb firebaseDb = FirebaseDb.getInstance();
                boolean status;
                status = firebaseDb.signUp(
                        FnameField.getEditableText().toString(),
                        LnameField.getEditableText().toString(),
                        emailField.getEditableText().toString(),
                        passwordField.getEditableText().toString(),
                        validatePasswordField.getEditableText().toString(),
                        new FirebaseCallbacks() {
                            @Override
                            public void onSignup(boolean isExists) {
                                if (isExists) {
                                    Toast.makeText(getActivity(), "This email is already in use", Toast.LENGTH_SHORT).show();
                                    loader.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onSignIn() {
                                System.out.println("Signed In");
                                ((MainActivity)getActivity()).updateMenuOnSignIn();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager
                                        .beginTransaction()
                                        .replace(R.id.fragment_container, new MyAccountFragment())
                                        .commit();
                                fragmentManager.popBackStackImmediate();
                            }
                        }
                );
            }
        });
        return view;
    }
}