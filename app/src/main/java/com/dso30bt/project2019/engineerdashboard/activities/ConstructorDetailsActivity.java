package com.dso30bt.project2019.engineerdashboard.activities;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.dso30bt.project2019.engineerdashboard.R;
import com.dso30bt.project2019.engineerdashboard.models.Constructor;
import com.dso30bt.project2019.engineerdashboard.models.Pothole;
import com.dso30bt.project2019.engineerdashboard.models.Report;
import com.dso30bt.project2019.engineerdashboard.repository.UserImpl;
import com.dso30bt.project2019.engineerdashboard.utils.Constants;
import com.dso30bt.project2019.engineerdashboard.utils.NavUtil;
import com.dso30bt.project2019.engineerdashboard.utils.Utils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Joesta on 2019/07/15.
 */
public class ConstructorDetailsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    /*var*/
    private static final String TAG = "ConstructorDetailsActiv";
    private String mEmail = null;

    private List<Report> reportListList = null;

    /*widgets*/
    private TextView tvConstructorFirstName;
    private TextView tvConstructorLastName;
    private TextView tvConstructorCell;
    private TextView tvConstructorEmail;
    private TextView tvConstructorStatus;
    private LinearLayout linearLayoutBottomSheet;
    private ListView lvPotholes;

    private ArrayAdapter<String> adapter;
    private List<String> list = new ArrayList<>();
    private Constructor mConstructor;
    private UserImpl userImpl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_constructor_details);

        mEmail = getIntent().getStringExtra(Constants.EXTRA_EMAIL);

        intiUI();
        getConstructorDetails();
        getAllReports();
    }

    private void intiUI() {
        tvConstructorFirstName = findViewById(R.id.tvConstructorFirstName);
        tvConstructorLastName = findViewById(R.id.tvConstructorLastName);
        tvConstructorCell = findViewById(R.id.tvConstructorCellNumber);
        tvConstructorEmail = findViewById(R.id.tvConstructorEmail);
        tvConstructorStatus = findViewById(R.id.tvConstructorStatus);
        linearLayoutBottomSheet = findViewById(R.id.bottom_sheet);
        lvPotholes = findViewById(R.id.lvPotholes);
    }

    private void getConstructorDetails() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.CONSTRUCTOR_COLLECTION).document(mEmail)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        Log.d(TAG, "getConstructorDetails: Document id is " + documentSnapshot.getReference().getId());
                        mConstructor = documentSnapshot.toObject(Constructor.class);
                        tvConstructorFirstName.setText(mConstructor.getFirstName());
                        tvConstructorLastName.setText(mConstructor.getLastName());
                        tvConstructorCell.setText(mConstructor.getCellNumber());
                        tvConstructorEmail.setText(mConstructor.getEmailAddress());
                        tvConstructorStatus.setText(mConstructor.getStatus().getDescription());

                    } else {
                        Utils.showToast(ConstructorDetailsActivity.this, "Error reading user data");
                    }

                })
                .addOnFailureListener(e -> Utils.showToast(this, e.getLocalizedMessage()));
    }

    private void getAllReports() {
        reportListList = new ArrayList<>();
        userImpl = new UserImpl(this);

        userImpl.getReports(this::onFetchedReports);
    }

    private void onFetchedReports(List<Report> reportList) {
        this.reportListList = reportList;

        for (Report report : reportList) {
            list.add(report.getReportDate().toString());
        }

        loadPotholeList();
    }

    private void loadPotholeList() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        lvPotholes.setAdapter(adapter);
        lvPotholes.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        NavUtil.moveToNextActivity(ConstructorDetailsActivity.this, MainActivity.class);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Utils.showToast(this, mConstructor.getFirstName() + " " + mConstructor.getLastName());

        userImpl = new UserImpl(this);
        userImpl.assignReport(reportListList.get(position), mConstructor);

    }

    private boolean isPotholeAlreadyAssigned(final int position) {
        //return potholeList.get(position).getAssignedBy() != null;
        return false;
    }

    private boolean isConstructorAvailable() {
        //return !mConstructor.getStatus().equalsIgnoreCase(ConstructorStatusEnum.AVAILABLE.value);

        return false;
    }
}
