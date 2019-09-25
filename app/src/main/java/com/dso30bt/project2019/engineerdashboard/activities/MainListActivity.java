package com.dso30bt.project2019.engineerdashboard.activities;

import android.os.Bundle;
import android.view.View;

import com.dso30bt.project2019.engineerdashboard.R;
import com.dso30bt.project2019.engineerdashboard.adapters.ConstructorAdapter;
import com.dso30bt.project2019.engineerdashboard.adapters.EngineersAdapter;
import com.dso30bt.project2019.engineerdashboard.adapters.ReportAdapter;
import com.dso30bt.project2019.engineerdashboard.adapters.UsersAdapter;
import com.dso30bt.project2019.engineerdashboard.repository.UserImpl;
import com.dso30bt.project2019.engineerdashboard.utils.NavUtil;
import com.dso30bt.project2019.engineerdashboard.utils.Utils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Joesta on 2019/07/07.
 */
public class MainListActivity extends AppCompatActivity {

    private static final String TAG = "MainListActivity";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private UserImpl mUserImpl;
    private int usersType;
    private Class<? extends AppCompatActivity> destination;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        initUI();

        usersType = getIntent().getIntExtra("userType", 0);
        checkUserType(usersType);
    }

    private void checkUserType(int usersType) {
        if (usersType == 677) {
            // users
            getUsers();
        } else if (usersType == 700) {
            // constructors
            getConstructors();
        } else {
            if (usersType == 755) {
                //engineers
                getEngineers();
            } else {
                // load reports
                fetchReports();
            }
        }
    }

    private void initUI() {
        // initializing recycler view
        recyclerView = findViewById(R.id.recyclerView_);
        recyclerView.setHasFixedSize(true);

        // initializing layout manager for recycler view
        RecyclerView.LayoutManager layoutManager
                = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void getUsers() {
        mUserImpl = new UserImpl(this);
        mUserImpl.getUsers(userList -> {
            UsersAdapter adapter = new UsersAdapter(this, userList);
            recyclerView.setAdapter(adapter);
        });
    }

    private void getConstructors() {
        mUserImpl = new UserImpl(this);
        mUserImpl.getConstructors(constructorsList -> {
            ConstructorAdapter adapter = new ConstructorAdapter(this, constructorsList);
            recyclerView.setAdapter(adapter);
        });
    }

    private void getEngineers() {
        mUserImpl = new UserImpl(this);
        mUserImpl.getEngineers(engineersList -> {
            EngineersAdapter adapter = new EngineersAdapter(this, engineersList);
            recyclerView.setAdapter(adapter);
        });
    }

    private void fetchReports() {
        mUserImpl = new UserImpl(this);
        mUserImpl.getReports(reportList -> {
            // report adapter
            ReportAdapter adapter = new ReportAdapter(this, reportList);
            recyclerView.setAdapter(adapter);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        decideNavigation();
    }

    private void decideNavigation() {

        if (usersType == 677 || usersType == 700 || usersType == 755) {
            destination = ManageUsersActivity.class;
        } else {
            destination = ManageReportsActivity.class;
        }
        NavUtil.moveToNextActivity(MainListActivity.this, destination);
    }
}
