package com.pwdmngr.passwordmanager.modules;

import android.content.Context;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.lifecycle.LifecycleOwner;

public class BackPressed {
    public static void disable(OnBackPressedDispatcher dispatcher, Context context) {
        dispatcher.addCallback((LifecycleOwner) context, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
            }
        });
    }
}
