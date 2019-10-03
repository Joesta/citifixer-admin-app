package com.dso30bt.project2019.engineerdashboard.activities;

import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.dso30bt.project2019.engineerdashboard.R;
import com.dso30bt.project2019.engineerdashboard.adapters.ConstructorAdapter;
import com.dso30bt.project2019.engineerdashboard.adapters.EngineersAdapter;
import com.dso30bt.project2019.engineerdashboard.adapters.ReportAdapter;
import com.dso30bt.project2019.engineerdashboard.adapters.RoadUsersAdapter;
import com.dso30bt.project2019.engineerdashboard.adapters.RoadUsersConstructorsAdapter;
import com.dso30bt.project2019.engineerdashboard.repository.UserImpl;
import com.dso30bt.project2019.engineerdashboard.utils.NavUtil;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Joesta on 2019/07/07.
 */
public class MainListActivity extends AppCompatActivity {

    private static final String TAG = "MainListActivity";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private UserImpl mUserImpl = new UserImpl(this);
    private int userPrivilegeCode;
    private Class<? extends AppCompatActivity> destination;
    private SearchView searchView;
    private MenuItem mMenuItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_list);

        initUI();

        userPrivilegeCode = getIntent().getIntExtra("userType", 0);
        checkUserPrivilege(userPrivilegeCode);
    }

    private void checkUserPrivilege(int userPrivilegeCode) {
        if (userPrivilegeCode == 677) {
            // users
            getRoadUsers();
        } else if (userPrivilegeCode == 700) {
            // constructors
            getConstructors();
        } else if (userPrivilegeCode == 755) {
            // engineers
            getEngineers();
        } else {
            if (userPrivilegeCode == 777) {
                // all users
                getAllUsers();
            } else {
                // fetch reports
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

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
        ((LinearLayoutManager) layoutManager).getOrientation());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dividerItemDecoration.setDrawable(getDrawable(R.drawable.recycler_view_divider));
        } else {
            dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider));
        }
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void getAllUsers() {
        mUserImpl.getAllUsers(usersList -> {
            mAdapter = new RoadUsersConstructorsAdapter(this, usersList);
            this.setTitle("Filter & Remove Users");
            recyclerView.setAdapter(mAdapter);
        });
    }
    private void getRoadUsers() {
        mUserImpl = new UserImpl(this);
        mUserImpl.getRoadUsers(roadUsersList -> {
            mAdapter = new RoadUsersAdapter(this, roadUsersList);
            this.setTitle("Road Users");
            recyclerView.setAdapter(mAdapter);
        });
    }

    private void getConstructors() {
        mUserImpl.getConstructors(constructorsList -> {
            mAdapter = new ConstructorAdapter(this, constructorsList);
            this.setTitle("Constructors");
            recyclerView.setAdapter(mAdapter);
        });
    }

    private void getEngineers() {
        mUserImpl.getEngineers(engineersList -> {
            mAdapter = new EngineersAdapter(this, engineersList);
            this.setTitle("Engineers");
            recyclerView.setAdapter(mAdapter);
        });
    }

    private void fetchReports() {
        mUserImpl.getReports(reportList -> {
            // report adapter
            mAdapter = new ReportAdapter(this, reportList);
            this.setTitle("Reports");
            recyclerView.setAdapter(mAdapter);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        decideNavigation();
    }

    private void decideNavigation() {
        if (userPrivilegeCode == 677 || userPrivilegeCode == 700 || userPrivilegeCode == 755 || userPrivilegeCode == 777) {
            destination = ManageUsersActivity.class;
        } else {
            destination = ManageReportsActivity.class;
        }

        NavUtil.moveToNextActivity(MainListActivity.this, destination);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        mMenuItem = menu.getItem(0); // just hides the filter for any other operations except for removing users.
        if (userPrivilegeCode != 777) {
            mMenuItem.setVisible(false);
        }

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                 ((RoadUsersConstructorsAdapter) mAdapter).getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                 ((RoadUsersConstructorsAdapter) mAdapter).getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
