package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
//oram-changes- new
    MaterialToolbar materialToolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FrameLayout frameLayout;
    MenuItem currentMenuItem;

    FirebaseDb firebaseDb = FirebaseDb.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        materialToolbar = findViewById(R.id.toolbar);
        frameLayout = findViewById(R.id.main_frame_layout);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigation);

        hideSignedInMenuItems();

        boolean isRightToLeft = getResources().getBoolean(R.bool.is_right_to_left);

        // Set the custom toolbar as the app's toolbar
        setSupportActionBar(materialToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        navigationView.bringToFront();

        // Enable drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                materialToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        // Add the drawer listener
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Add the feed fragment
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, new FeedFragment()).commit();

    }

    @Override
    // Close the drawer when back is clicked if it opened
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        
        FirebaseDb firebaseDb = FirebaseDb.getInstance();
        FragmentManager fragmentManager = getSupportFragmentManager();

        this.uncheckMenuItems();
        item.setChecked(true);


        FeedFragment feedFragment = new FeedFragment();
        NewPostFragment newPostFragment = new NewPostFragment();
        ContactsFragment contactsFragment = new ContactsFragment();
        SignupFragment signupFragment = new SignupFragment();
        SigninFragment signinFragment = new SigninFragment();
        MyAccountFragment myAccountFragment = new MyAccountFragment();

        // Manage fragments change
        switch (item.getItemId()) {
            case (R.id.feed):
                fragmentManager.popBackStackImmediate();
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, feedFragment)
                        .addToBackStack("feed")
                        .commit();
                break;
            case (R.id.new_post):
                if (firebaseDb.isSignedIn()) {
                    fragmentManager
                            .beginTransaction()
                            .replace(R.id.fragment_container, newPostFragment)
                            .addToBackStack("new_post")
                            .commit();
                } else {
                    Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show();
                    // TODO:
                    // If the user is not signed in, display a popup requires signup
                    // The popup should have three options: go to signup, go to signin, close the popup
                }
                break;
            case (R.id.chat_nav):
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, contactsFragment)
                        .addToBackStack("chat")
                        .commit();
                break;
            case (R.id.signup_nav):
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, signupFragment)
                        .addToBackStack("signup")
                        .commit();
                break;
            case (R.id.signin_nav):
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, signinFragment)
                        .addToBackStack("signin")
                        .commit();
                break;
            case (R.id.my_account_nav):
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_container, myAccountFragment)
                        .addToBackStack("my_account")
                        .commit();
                break;
            case (R.id.signout):
                FirebaseDb.getInstance().signOut(new FirebaseCallbacks() {
                    @Override
                    public void signedOut() {
                        fragmentManager.popBackStackImmediate();
                        fragmentManager
                                .beginTransaction()
                                .replace(R.id.fragment_container, feedFragment)
                                .addToBackStack("feed")
                                .commit();
                    }
                });
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void hideSignedInMenuItems() {
        MenuItem newPostItem = navigationView.getMenu().findItem(R.id.new_post);
        MenuItem chatItem = navigationView.getMenu().findItem(R.id.chat_nav);
        MenuItem myAccountItem = navigationView.getMenu().findItem(R.id.my_account_nav);
        MenuItem signInItem = navigationView.getMenu().findItem(R.id.signin_nav);
        MenuItem signUpItem = navigationView.getMenu().findItem(R.id.signup_nav);
        MenuItem signOutItem = navigationView.getMenu().findItem(R.id.signout);

        newPostItem.setVisible(false);
        chatItem.setVisible(false);
        myAccountItem.setVisible(false);
        signInItem.setVisible(true);
        signUpItem.setVisible(true);
        signOutItem.setVisible(false);
    }

    public void updateMenuOnSignIn() {
        MenuItem newPostItem = navigationView.getMenu().findItem(R.id.new_post);
        MenuItem chatItem = navigationView.getMenu().findItem(R.id.chat_nav);
        MenuItem myAccountItem = navigationView.getMenu().findItem(R.id.my_account_nav);
        MenuItem signInItem = navigationView.getMenu().findItem(R.id.signin_nav);
        MenuItem signUpItem = navigationView.getMenu().findItem(R.id.signup_nav);
        MenuItem signOutItem = navigationView.getMenu().findItem(R.id.signout);

        newPostItem.setVisible(true);
        chatItem.setVisible(true);
        myAccountItem.setVisible(true);
        signInItem.setVisible(false);
        signUpItem.setVisible(false);
        signOutItem.setVisible(true);
    }

    private void uncheckMenuItems() {
        Menu menu = navigationView.getMenu();
        for (int i =0; i< menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isChecked()) {
                menuItem.setChecked(false);
            }
        }
    }

}