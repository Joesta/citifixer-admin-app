package com.dso30bt.project2019.engineerdashboard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dso30bt.project2019.engineerdashboard.R;
import com.dso30bt.project2019.engineerdashboard.models.Engineer;
import com.dso30bt.project2019.engineerdashboard.utils.NavUtil;
import com.dso30bt.project2019.engineerdashboard.utils.SharedPreferenceManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    private DrawerLayout mDrawer;
    private Menu navMenu;
    private TextView navHeaderName;
    private TextView navHeaderEmail;
    private ImageView headerImageProPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        loadEngineerFromDB();

    }

    private void loadEngineerFromDB() {
        FirebaseFirestore.getInstance().collection("engineers")
                .document(SharedPreferenceManager.getEmail(this))
                .get()
                .addOnCompleteListener(this, task -> {
                    if (task.isComplete()) {
                        DocumentSnapshot snapshot = task.getResult();
                        Engineer engineer = snapshot.toObject(Engineer.class);
                        setNavHeaderInfo(engineer.getFirstName(), engineer.getEmailAddress(), engineer.getImageUrl());
                    }
                });
    }

    private void initUI() {
        //gets toolbar
        Toolbar toolbar = getToolbar();

        // initializing drawer layout
        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        // initializing navigation view and register item selected listener
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navMenu = navigationView.getMenu();

        // get header layout from navigation view
        LinearLayout navHeaderParentLayout = (LinearLayout) navigationView.getHeaderView(0);

        // initializing header widget from navigation header parent layout
        navHeaderName = navHeaderParentLayout.findViewById(R.id.navHeaderFirstName);
        navHeaderEmail = navHeaderParentLayout.findViewById(R.id.navHeaderEmail);
        headerImageProPic = navHeaderParentLayout.findViewById(R.id.navHeaderProPic);

        setListenerFor(R.id.usersCard, R.id.reportsCard);
    }

    private Toolbar getToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int itemId = menuItem.getItemId();

        if (itemId == R.id.nav_profile) {
            gotoUserProfileActivity();
        } else if (itemId == R.id.nav_share) {
            shareApp();

        } else if (itemId == R.id.nav_about) {
            gotoAboutAppActivity();

        } else if (itemId == R.id.nav_logout) {
            logout();
        }

        //DrawerLayout drawer = findViewById(R.id.drawer_layout);
        mDrawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void setNavHeaderInfo(String name, String email, String... imageUrl) {

        navHeaderName.setText(name);
        navHeaderEmail.setText(email);

        if (imageUrl.length > 0) {
            if (!imageUrl[0].isEmpty() && !imageUrl[0].equals("")) {
                // set navigation header image
                Picasso
                        .get()
                        .load(imageUrl[0])
                        .into(headerImageProPic);
            }
        }
    }

    /***
     * take user to their profile activity
     */
    private void gotoUserProfileActivity() {
        NavUtil.moveToNextActivity(MainActivity.this, UserProfileActivity.class);
    }

    /***
     * logout user and clear stored user information saved locally
     */
    private void logout() {
        gotoLogin();
    }

    /**
     * login
     */
    private void gotoLogin() {
        SharedPreferenceManager.clearSavedLoginInfo(MainActivity.this);
        NavUtil.moveToNextActivity(MainActivity.this, LoginActivity.class);
        finish();
    }

    /**
     * takes user to about app
     */
    private void gotoAboutAppActivity() {
        NavUtil.moveToNextActivity(MainActivity.this, AboutActivity.class);
    }

    /**
     * share app
     */
    private void shareApp() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Try City-Fixer App for convenient way of locating and fixing Potholes");
        sharingIntent.setType("text/plain");
        startActivity(sharingIntent);
    }

    public void setListenerFor(int usersCard, int reportsCard) {
        findViewById(usersCard).setOnClickListener(this);
        findViewById(reportsCard).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.usersCard:
                NavUtil.moveToNextActivity(this, ManageUsersActivity.class);
                break;
            case R.id.reportsCard:
                NavUtil.moveToNextActivity(this, ManageReportsActivity.class);
                break;
            default:
                break;
        }
    }
}