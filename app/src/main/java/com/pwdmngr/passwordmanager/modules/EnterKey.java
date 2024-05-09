package com.pwdmngr.passwordmanager.modules;

import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


// Change the behaviour of enter key on keyboard
public class EnterKey {
    public static void setBehaviour(EditText editText, Context context) {
        editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard(editText, context);
                editText.clearFocus();
                return true;
            }
            return false;
        });
    }

    private static void hideKeyboard(EditText editText, Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
