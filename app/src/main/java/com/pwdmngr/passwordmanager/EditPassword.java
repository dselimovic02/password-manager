package com.pwdmngr.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.pwdmngr.passwordmanager.model.PwdModel;
import com.pwdmngr.passwordmanager.modules.BackPressed;
import com.pwdmngr.passwordmanager.modules.EnterKey;
import com.pwdmngr.passwordmanager.modules.PasswordGenerator;
import com.pwdmngr.passwordmanager.modules.SaveButton;
import com.pwdmngr.passwordmanager.modules.Toast;
import com.pwdmngr.passwordmanager.util.DatabaseHandler;

public class EditPassword extends AppCompatActivity {

    private int id;
    private PwdModel pwdData;
    private DatabaseHandler db;
    private EditText title, username, pwd, url;
    private String titleText, usernameText, pwdText, urlText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.edit_pwd);

        // Disable system back button
        BackPressed.disable(getOnBackPressedDispatcher(), this);


        // Get password model from intent
        Intent i = getIntent();
        pwdData = (PwdModel) i.getSerializableExtra("pwdData");

        // Set up data display
        setupViews();

        // Set up buttons and their behaviour
        setupButtons();
    }

    private void setupViews() {
        // Initialize views
        title = findViewById(R.id.title);
        username = findViewById(R.id.username);
        pwd = findViewById(R.id.password);
        url = findViewById(R.id.url);

        // Change enter key behaviour to done behaviour
        EnterKey.setBehaviour(title, EditPassword.this);
        EnterKey.setBehaviour(username, EditPassword.this);
        EnterKey.setBehaviour(pwd, EditPassword.this);
        EnterKey.setBehaviour(url, EditPassword.this);

        // Extract data from password model
        titleText = String.valueOf(pwdData.getTitle());
        usernameText = String.valueOf(pwdData.getUsername());
        pwdText = String.valueOf(pwdData.getPwd());
        urlText = String.valueOf(pwdData.getUrl());

        // Populate views with existing data
        title.setText(titleText);
        username.setText(usernameText);
        pwd.setText(pwdText);
        url.setText(urlText);
    }

    private void setupButtons() {
        // Toggle Password visibility
        setTogglePasswordVisibility();

        // Setup generate new password button

        setGenerateNewPasswordButton();

        // Return to password view when back button is clicked
        setBackButtonBehaviour();

        // Delete password from database and return to main activity
        setDeleteButtonBehaviour();

        // Update the password in database and return to main activity
        setSaveButtonBehaviour();
    }

    private void setTogglePasswordVisibility() {
        ToggleButton toggleBtn = findViewById(R.id.show_hide_btn);
        toggleBtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Show password
                pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                // Hide password
                pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    private void setGenerateNewPasswordButton() {
        // Generate a strong password
        ImageButton generatePwdBtn = findViewById(R.id.generate);
        generatePwdBtn.setOnClickListener(v -> {
            pwd.setText(PasswordGenerator.generate());
        });
    }

    private void setBackButtonBehaviour() {
        Button backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> {
            Intent i1 = new Intent(EditPassword.this, PwdView.class);
            int id = pwdData.getId();
            i1.putExtra("id", id);
            startActivity(i1);
        });
    }

    private void setDeleteButtonBehaviour() {
        Button deleteBtn = findViewById(R.id.btn_delete);
        deleteBtn.setOnClickListener(v -> {
            // Instance the alert dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(EditPassword.this);
            builder.setTitle("Delete Password");
            builder.setMessage("Are you sure you want to delete this password?");

            // Delete the password if user confirms
            builder.setPositiveButton("Confirm", (dialog, which) -> {

                // Get password id
                id = pwdData.getId();

                // Send request to database to delete the password
                db = new DatabaseHandler(this);
                db.openDatabase();
                db.deletePwd(id);
                db.close();

                // Notify user
                notifyUser("Password deleted successfully");

                // Return to main activity
                Intent i = new Intent(EditPassword.this, MainActivity.class);
                startActivity(i);
            });

            // Dismiss if user cancels
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void setSaveButtonBehaviour() {
        Button saveBtn = findViewById(R.id.btn_save);
        saveBtn.setEnabled(false); // Save button is disabled by default

        // Add listeners to EditText fields which alter the state of save button
        SaveButton.setSaveBtnState(saveBtn, title, username, pwd, url);

        saveBtn.setOnClickListener(v -> {

            // Get new values
            String new_title = getValue(title, titleText);
            String new_username = getValue(username, usernameText);
            String new_pwd = getValue(pwd, pwdText);
            String new_url = getValue(url, urlText);

            // Get password id
            id = pwdData.getId();

            // Update the row with new data
            db = new DatabaseHandler(this);
            db.openDatabase();
            db.update(id, new_title, new_username, new_pwd, new_url);
            db.close();

            // Notify the user of the changes
            notifyUser("Password edited successfully");

            // Return to main
            Intent i = new Intent(EditPassword.this, MainActivity.class);
            startActivity(i);
        });
    }


    // Return value if it has changed so it can be updated in the database
    // Else return null (DatabaseHandler skips null values)
    private String getValue(EditText input, String originalText) {
        String inputText = String.valueOf(input.getText()).trim();

        if (inputText.equals(originalText))
            return null;

        return inputText;
    }

    private void notifyUser(String text) {
        Toast.showShortWithCustomText(EditPassword.this, text);
    }
}