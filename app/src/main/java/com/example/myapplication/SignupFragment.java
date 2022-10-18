package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

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

    private TextInputEditText FnameField,LnameField,emailField,passwordField,validatePasswordField;
    private FirebaseAuth mAuth;
    private CircularProgressIndicator loader;
    private Boolean isGuide = false;
    private String city = "";
    private String details = "";
    private boolean status;

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


        //Auth
        mAuth = FirebaseAuth.getInstance();

        // Elements
        FnameField = view.findViewById(R.id.signup_firstname);
        LnameField = view.findViewById(R.id.signup_lastname);
        emailField = view.findViewById(R.id.signup_email);
        passwordField = view.findViewById(R.id.signup_password);
        validatePasswordField = view.findViewById(R.id.signup_validate_password);
        loader = view.findViewById(R.id.signup_loader);
        Button signupButton = view.findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();



            }
        });
        return view;


    }
    private void registerUser(){
        String email = emailField.getText().toString().trim();
        String firstN =FnameField.getText().toString().trim();
        String lastN =LnameField.getText().toString().trim();
        String password =passwordField.getText().toString().trim();

        if (firstN.isEmpty()){
            FnameField.setError("First name is required !");
            FnameField.requestFocus();
            return;
        }
        if (lastN.isEmpty()){
            LnameField.setError("Last name is required !");
            LnameField.requestFocus();
            return;
        }
        if (password.isEmpty()){
            passwordField.setError("Password is required !");
            passwordField.requestFocus();
            return;
        }
        if (email.isEmpty()){
            emailField.setError("Email is required !");
            emailField.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            emailField.setError("Please provide valid email !");
            emailField.requestFocus();
            return;
        }
        if (password.length() < 6){
            passwordField.setError("Min password length should be 6 characters !");
            passwordField.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            loader.setVisibility(View.VISIBLE);
                            FirebaseDb firebaseDb = FirebaseDb.getInstance();
                            status = firebaseDb.signUp(
                                    isGuide,
                                    city,
                                    details,
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
                                                    .addToBackStack("my_account")
                                                    .commit();
                                        }
                                    }
                            );
                        }
                    }
                });





    }

}