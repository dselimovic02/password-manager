package com.pwdmngr.passwordmanager.modules;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;


// Add listeners to all EditText buttons on which the state of Save button depends
public class SaveButton {
    public static void setSaveBtnState(Button btn, EditText... fields) {
        // Instance a text watcher
        final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Enable button if all fields are filled
                btn.setEnabled(areAllFieldsFilled(fields));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        // Add listener to each of fields
        for (EditText field : fields) {
            field.addTextChangedListener(textWatcher);
        }
    }


    private static boolean areAllFieldsFilled(EditText[] fields) {
        // Return false if any field is empty
        for(EditText field : fields) {
            if (TextUtils.isEmpty(String.valueOf(field.getText()).trim()))
                return false;
        }
        return true;
    }
}
