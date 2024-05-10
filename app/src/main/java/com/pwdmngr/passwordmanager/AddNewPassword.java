package com.pwdmngr.passwordmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.pwdmngr.passwordmanager.model.PwdModel;
import com.pwdmngr.passwordmanager.modules.BackPressed;
import com.pwdmngr.passwordmanager.modules.EnterKey;
import com.pwdmngr.passwordmanager.modules.PasswordGenerator;
import com.pwdmngr.passwordmanager.modules.SaveButton;
import com.pwdmngr.passwordmanager.util.DatabaseHandler;

import com.pwdmngr.passwordmanager.modules.Toast;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AddNewPassword extends AppCompatActivity {

    private Button backBtn, saveBtn;
    private ToggleButton toggleBtn;
    private ImageButton generatePwdBtn;
    private EditText title, username, pwd, url;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_pwd);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_pwd), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Disable system back button
        BackPressed.disable(getOnBackPressedDispatcher(), this);

        db = new DatabaseHandler(this);
        db.openDatabase();


        // Get EditText references
        title = findViewById(R.id.title);
        username = findViewById(R.id.username);
        pwd = findViewById(R.id.password);
        url = findViewById(R.id.url);

        // Toggle Password visibility

        toggleBtn = findViewById(R.id.show_hide_btn);
        toggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Show password
                    pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    // Hide password
                    pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        // Change enter key behaviour to done behaviour
        // Change enter key behaviour to done behaviour
        EnterKey.setBehaviour(title, AddNewPassword.this);
        EnterKey.setBehaviour(username, AddNewPassword.this);
        EnterKey.setBehaviour(pwd, AddNewPassword.this);
        EnterKey.setBehaviour(url, AddNewPassword.this);

        // Generate a strong password
        generatePwdBtn = findViewById(R.id.generate);
        generatePwdBtn.setOnClickListener(v -> {
            pwd.setText(PasswordGenerator.generate());
        });

        saveBtn = findViewById(R.id.btn_save);
        saveBtn.setEnabled(false); // Make Save button disabled by default

        // Add listeners to EditText fields which alter the state of save button
        SaveButton.setSaveBtnState(saveBtn, title, username, pwd, url);

        // Save data on click
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create data model
                PwdModel newPwd = new PwdModel();

                newPwd.setTitle(String.valueOf(title.getText()));
                newPwd.setUsername(String.valueOf(username.getText()));
                newPwd.setPwd(String.valueOf(pwd.getText()));
                newPwd.setUrl(String.valueOf(url.getText()));

                // Insert data model into db
                try {
                    db.insertPassword(newPwd);
                } catch (InvalidAlgorithmParameterException | NoSuchPaddingException |
                         UnrecoverableEntryException | IllegalBlockSizeException |
                         CertificateException | NoSuchAlgorithmException | KeyStoreException |
                         IOException | BadPaddingException | InvalidKeyException |
                         NoSuchProviderException e) {
                    throw new RuntimeException(e);
                }

                // Notify the user
                Toast.showShortWithCustomText(AddNewPassword.this, "Password added successfully");

                // Return to MainActivity
                Intent i = new Intent(AddNewPassword.this, MainActivity.class);
                startActivity(i);
            }
        });

        backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Return to MainActivity
                Intent i = new Intent(AddNewPassword.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}