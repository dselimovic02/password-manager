package com.pwdmngr.passwordmanager.modules;

import android.content.Context;

// Display a toast component with custom text
public class Toast {
    public static void showShortWithCustomText(Context context, String text) {
        int duration = android.widget.Toast.LENGTH_SHORT;
        android.widget.Toast toast = android.widget.Toast.makeText(context, text, duration);
        toast.show();
    }
}
