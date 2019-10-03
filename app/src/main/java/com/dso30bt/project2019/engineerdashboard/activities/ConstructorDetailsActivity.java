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

    private List<Report> reportList = null;

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
        userImpl = new UserImpl(this);

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
        FirebaseFirestore.getInstance()
                .collection(Constants.CONSTRUCTOR_COLLECTION)
                .document(mEmail)
                .addSnapshotListener((snapshot, error) -> {

                    if (error != null) {
                        Utils.showToast(this, error.getLocalizedMessage());
                        return;
                    }

                    if (snapshot.exists()) {
                        Log.d(TAG, "getConstructorDetails: Document id is " + snapshot.getReference().getId());
                        mConstructor = snapshot.toObject(Constructor.class);
                        tvConstructorFirstName.setText(mConstructor.getFirstName());
                        tvConstructorLastName.setText(mConstructor.getLastName());
                        tvConstructorCell.setText(mConstructor.getCellNumber());
                        tvConstructorEmail.setText(mConstructor.getEmailAddress());
                        tvConstructorStatus.setText(mConstructor.getStatus().getDescription());
                    } else {
                        Utils.showToast(getApplicationContext(), "User profile no longer exist!");
                    }
                });


    }

    private void getAllReports() {
        reportList = new ArrayList<>();
        userImpl = new UserImpl(this);

        userImpl.getReports(this::onFetchedReports);
    }

    private void onFetchedReports(List<Report> reportList) {
        this.reportList = reportList;

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < this.reportList.size(); i++) {
            Report currentReport = this.reportList.get(i);
            if (currentReport.getConstructor() == null) {
                sb.append(currentReport.getReportDate().toString());
                list.add(sb.toString());
                sb.delete(0, sb.length()); // clear
            }
        }

        loadPotholeList();
    }

    private void loadPotholeList() {
        if (list.size() == 0) {
            return;
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        lvPotholes.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        lvPotholes.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        NavUtil.moveToNextActivity(ConstructorDetailsActivity.this, MainActivity.class);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // check if the selected report wa already assigned
        Report selectedReport = reportList.get(position);
        boolean reportAlreadyAssigned = isReportAlreadyAssigned(selectedReport);


        if (reportAlreadyAssigned) {
            Utils.showToast(this, "Report is already assigned.");
        } else {
            boolean constructorAvailable = isConstructorAvailable();
            if (constructorAvailable) {
                userImpl.assignReport(selectedReport, mConstructor);
            } else {
                Utils.showToast(getApplicationContext(), "Cannot assign report to " + getAssignedConstructorNames(mConstructor) +
                        ". Please try again when their status changed to available.");
            }
        }
    }

    private String getAssignedConstructorNames(Constructor constructor) {
        String firstName = constructor.getFirstName();
        String lastName = constructor.getLastName();

        return firstName + " " + lastName;
    }

    private boolean isReportAlreadyAssigned(Report selectedReport) {
        int terminator = 0;
        List<Report> constructorReportList = mConstructor.getReportList();
        // traverse through constructor list to check is the selected report was already assigned to this constructor
        for (Report report : constructorReportList) {
            if (selectedReport.getReportDate().equals(report.getReportDate())) {
                terminator++;
            }
        }

        // check reason for loop termination
        if (terminator > 0) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isPotholeAlreadyAssigned(final int position) {
        //return potholeList.get(position).getAssignedBy() != null;
        return false;
    }

    private boolean isConstructorAvailable() {
        return mConstructor.getStatus().getDescription().equalsIgnoreCase("available");
    }
}
