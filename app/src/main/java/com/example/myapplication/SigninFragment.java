package com.example.myapplication;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Patterns;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SigninFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SigninFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private TextInputEditText emailField,passwordField;
    private CircularProgressIndicator loader;
    private Button signinButton;
    private FirebaseAuth mAuth;


    public SigninFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SigninFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SigninFragment newInstance(String param1, String param2) {
        SigninFragment fragment = new SigninFragment();
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
        View view = inflater.inflate(R.layout.fragment_signin, container, false);

        //Auth
        mAuth = FirebaseAuth.getInstance();

        // Elements
        emailField = view.findViewById(R.id.signin_email);
        passwordField = view.findViewById(R.id.signin_password);
        loader = view.findViewById(R.id.signin_loader);
        signinButton = view.findViewById(R.id.signin_button);

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view2) {
                userLogin();
            }
        });

        return view;
    }

    private void userLogin(){
        String email = emailField.getText().toString().trim();
        String password =passwordField.getText().toString().trim();

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

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        loader.setVisibility(View.VISIBLE);
                        FirebaseDb firebaseDb = FirebaseDb.getInstance();
                        firebaseDb.signIn(
                                emailField.getEditableText().toString(),
                                passwordField.getEditableText().toString(),
                                new FirebaseCallbacks() {
                                    @Override
                                    public void onSignIn() {
                                        System.out.println("Signed In");

                                        Map<String, String> creds = new HashMap<>();
                                        creds.put("Email", emailField.getEditableText().toString());
                                        creds.put("Password", passwordField.getEditableText().toString());

                                        ((MainActivity) getActivity()).updateMenuOnSignIn();


                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        fragmentManager
                                                .beginTransaction()
                                                .replace(R.id.fragment_container, new FeedFragment())
                                                .commit();
                                        fragmentManager.popBackStackImmediate();
                                    }

                                    @Override
                                    public void onSignInFailed(String errorMessage) {
                                        loader.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                    }
                });

    }

}