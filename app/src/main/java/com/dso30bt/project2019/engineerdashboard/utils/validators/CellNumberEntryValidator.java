package com.dso30bt.project2019.engineerdashboard.utils.validators;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

/**
 * Created by Joesta on 2019/10/01.
 */
public class CellNumberEntryValidator implements TextWatcher {

    private TextView tvInputEntry;

    public CellNumberEntryValidator(TextView tvInputEntry) {
        this.tvInputEntry = tvInputEntry;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
