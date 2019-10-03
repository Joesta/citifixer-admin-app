package com.dso30bt.project2019.engineerdashboard.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.dso30bt.project2019.engineerdashboard.R;
import com.dso30bt.project2019.engineerdashboard.models.Engineer;
import com.dso30bt.project2019.engineerdashboard.models.Role;
import com.dso30bt.project2019.engineerdashboard.repository.UserImpl;
import com.dso30bt.project2019.engineerdashboard.utils.NavUtil;
import com.dso30bt.project2019.engineerdashboard.utils.validators.NamesEntryValidator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Joesta on 2019/07/07.
 */
public class ManageUsersActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ManageUsersActivity";

    private EditText firstNameText;
    private EditText lastNameText;
    private EditText idNumberText;
    private EditText cellNumberText;
    private EditText emailText;
    private EditText passwordText;
    private EditText confirmPasswordText;

    // spinner
    private Spinner spinnerGender;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_users);

        setListenerFor(R.id.btnListUsers, R.id.btnListConstructors, R.id.btnRemoveUsers, R.id.btnListEngineer, R.id.btnAddEngineer);
    }

    private void setListenerFor(int btnListUsers, int btnListConstructors, int btnRemoveUsers, int btnListEngineer, int btnAddEngineer) {
        findViewById(btnListUsers).setOnClickListener(this);
        findViewById(btnListConstructors).setOnClickListener(this);
        findViewById(btnListEngineer).setOnClickListener(this);
        findViewById(btnAddEngineer).setOnClickListener(this);
        findViewById(btnRemoveUsers).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        gotHome();
    }

    private void gotHome() {
        NavUtil.moveToNextActivity(ManageUsersActivity.this, MainActivity.class);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btnListUsers:
                NavUtil.moveToNextActivity(this, MainListActivity.class, "677");
                break;
            case R.id.btnListConstructors:
                NavUtil.moveToNextActivity(this, MainListActivity.class, "700");
                break;
            case R.id.btnRemoveUsers:
                NavUtil.moveToNextActivity(this, MainListActivity.class, "777");
                break;
            case R.id.btnListEngineer:
                NavUtil.moveToNextActivity(this, MainListActivity.class, "755");
                break;
            case R.id.btnAddEngineer:
                showAddDialog();
                break;
            default:
                break;
        }
    }

    private void showAddDialog() {
        View editView = LayoutInflater.from(this).inflate(R.layout.layout_add_engineer, null);
        getInputReferenceFromView(editView);
        buildDialog()
                .setCancelable(false)
                .setTitle("Add Engineer")
                .setView(editView)
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("Clear", (dialog, which) -> clearInputEntries())
                .setPositiveButton("Add", (dialog, which) -> getInputEntries())
                .create().show();
    }

    private void getInputReferenceFromView(View editView) {
        firstNameText = editView.findViewById(R.id.etFirstName);
        lastNameText = editView.findViewById(R.id.etLastName);
        idNumberText = editView.findViewById(R.id.etIdNumber);
        // @Todo - validate values
        cellNumberText = editView.findViewById(R.id.etCellNumber);
        emailText = editView.findViewById(R.id.etEmail);
        passwordText = editView.findViewById(R.id.etPassword);
        confirmPasswordText = editView.findViewById(R.id.etConfirmPassword);
        spinnerGender = editView.findViewById(R.id.spinnerGender);
    }

    private void clearInputEntries() {
        firstNameText.setText("");
        lastNameText.setText("");
        idNumberText.setText("");
        cellNumberText.setText("");
        emailText.setText("");
        passwordText.setText("");
        confirmPasswordText.setText("");
    }

    private void getInputEntries() {
        String firstName = getStringFromView(firstNameText);
        String lastName = getStringFromView(lastNameText);
        String idNumber = getStringFromView(idNumberText);
        String cellNumber = getStringFromView(cellNumberText);
        String emailAddress = getStringFromView(emailText);
        String password = getStringFromView(passwordText);

         String gender = spinnerGender.getSelectedItem().toString();

        // @Todo - validate values
        //validateEntries();
        createEngineer(firstName, lastName, idNumber, gender, cellNumber, emailAddress, password, new Role(777, "Engineer"));
    }

    private void validateEntries() {
        NamesEntryValidator validator = new NamesEntryValidator(firstNameText);
        firstNameText.addTextChangedListener(validator);
    }


    private void createEngineer(String firstName, String lastName, String idNumber, String gender, String cellNumber, String emailAddress, String password, Role role) {
        Engineer engineer = new Engineer();
        engineer.setFirstName(firstName);
        engineer.setLastName(lastName);
        engineer.setIdNumber(idNumber);
        engineer.setGender(gender);
        engineer.setCellNumber(cellNumber);
        engineer.setEmailAddress(emailAddress);
        engineer.setPassword(password);
        engineer.setRole(role);
        engineer.setImageUrl("");


        saveEngineer(engineer);
    }

    private void saveEngineer(Engineer engineer) {
        UserImpl userImpl = new UserImpl(this);
        userImpl.addEngineer(engineer);
    }

    private String getStringFromView(EditText view) {
        return view.getText().toString();
    }

    private AlertDialog.Builder buildDialog() {
        return new AlertDialog.Builder(this);
    }
}
