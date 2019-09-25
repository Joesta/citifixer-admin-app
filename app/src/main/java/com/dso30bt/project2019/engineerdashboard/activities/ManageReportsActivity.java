package com.dso30bt.project2019.engineerdashboard.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.dso30bt.project2019.engineerdashboard.R;
import com.dso30bt.project2019.engineerdashboard.repository.UserImpl;
import com.dso30bt.project2019.engineerdashboard.utils.NavUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

/**
 * Created by Joesta on 2019/07/07.
 */
public class ManageReportsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSION_STORAGE_READ_WRITE_REQUEST_CODE = 7;
    private RxPermissions rxPermissions = new RxPermissions(this);
    private UserImpl mUserImpl = new UserImpl(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_reports);

        setListenerFor(R.id.btnListReports, R.id.btnRemoveReports, R.id.btnAssignReport, R.id.btnGenerateReports);
    }

    private void setListenerFor(int btnListReports, int btnRemoveReports, int btnAssignReport, int btnGenerateReports) {
        findViewById(btnListReports).setOnClickListener(this);
        findViewById(btnRemoveReports).setOnClickListener(this);
        findViewById(btnAssignReport).setOnClickListener(this);
        findViewById(btnGenerateReports).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotHome();
    }

    private void gotHome() {
        NavUtil.moveToNextActivity(ManageReportsActivity.this, MainActivity.class);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnListReports:
                NavUtil.moveToNextActivity(this, MainListActivity.class);
                break;
            case R.id.btnRemoveReports:
                showDialog();
                break;
            case R.id.btnAssignReport:
                assignReports();
                break;
            case R.id.btnGenerateReports:
                checkReadWritePermission();
                break;
            default:
                break;
        }
    }

    private void assignReports() {

    }

    private void deleteReports() {
        mUserImpl.deleteAllReports();
    }

    public void showDialog() {
        buildDialog()
                .setCancelable(false)
                .setTitle("Caution")
                .setMessage("This action cannot be undone. Are you sure you want to delete all reports?")
                .setPositiveButton("No", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("Yes", ((dialog, which) -> deleteReports()))
                .create()
                .show();
    }

    public AlertDialog.Builder buildDialog() {
        return new AlertDialog.Builder(this);
    }

    @SuppressLint("CheckResult")
    private void checkReadWritePermission() {
        rxPermissions.request(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        generateReports();
                    } else {
                        requestPermission();
                    }
                });
    }

    /***
     * request for storage permission
     */
    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_STORAGE_READ_WRITE_REQUEST_CODE);
    }

    private void generateReports() {
        mUserImpl.getReports(mUserImpl::generateReport);
    }
}
