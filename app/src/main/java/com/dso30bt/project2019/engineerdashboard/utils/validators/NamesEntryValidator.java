package com.dso30bt.project2019.engineerdashboard.utils.validators;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import com.dso30bt.project2019.engineerdashboard.utils.Constants;

/**
 * Created by Joesta on 2019/10/01.
 */
public class NamesEntryValidator implements TextWatcher {

    private final EditText inputEntry;

    public NamesEntryValidator(EditText inputEntry) {
        this.inputEntry = inputEntry;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String input = s.toString();
        if (!TextUtils.isEmpty(input) && TextUtils.getTrimmedLength(input) != 0) {

            boolean matches = input.matches(Constants.NAMES_PATTERN);
            if (!matches) {
                inputEntry.setError("Must be at least 3 characters long. \n" +
                        "Must contain letter A-Z.\n" +
                        "Must not contain special characters");
                inputEntry.requestFocus();
            } else {
                inputEntry.clearFocus();
            }

        } else {
            inputEntry.setError("Field Required");
            inputEntry.requestFocus();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
