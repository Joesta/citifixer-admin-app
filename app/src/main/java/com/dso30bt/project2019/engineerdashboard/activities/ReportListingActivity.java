package com.dso30bt.project2019.engineerdashboard.activities;

import android.os.Bundle;

import com.dso30bt.project2019.engineerdashboard.R;
import com.dso30bt.project2019.engineerdashboard.adapters.ConstructorAdapter;
import com.dso30bt.project2019.engineerdashboard.adapters.EngineersAdapter;
import com.dso30bt.project2019.engineerdashboard.adapters.ReportAdapter;
import com.dso30bt.project2019.engineerdashboard.adapters.UsersAdapter;
import com.dso30bt.project2019.engineerdashboard.repository.UserImpl;
import com.dso30bt.project2019.engineerdashboard.utils.NavUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Joesta on 2019/07/07.
 */
public class ReportListingActivity extends AppCompatActivity {

    private static final String TAG = "ReportListingActivity";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_manager_items);

        initUI();
        loadReports();
    }

    private void initUI() {
        // initializing recycler view
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        // initializing layout manager for recycler view
        RecyclerView.LayoutManager layoutManager
                = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void loadReports() {
        UserImpl mUserImpl = new UserImpl(this);
        mUserImpl.getReports(reportList -> {
            ReportAdapter adapter = new ReportAdapter(this, reportList);
            recyclerView.setAdapter(adapter);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotHome();
    }

    private void gotHome() {
        NavUtil.moveToNextActivity(ReportListingActivity.this, ManageUsersActivity.class);
    }
}
