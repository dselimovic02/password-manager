package com.pwdmngr.passwordmanager;

import android.content.ClipboardManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
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
import com.pwdmngr.passwordmanager.modules.Toast;
import com.pwdmngr.passwordmanager.util.DatabaseHandler;

import java.io.IOException;
import java.io.Serializable;
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

public class PwdView extends AppCompatActivity {

    private  PwdModel pwdData;
    private TextView title, username, pwd, url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pwd_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.pwd_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Disable system back button
        BackPressed.disable(getOnBackPressedDispatcher(), this);

        try {
            pwdData = retrievePwdData();
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableEntryException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException(e);
        }

        setupViews();

        setupButtons();
    }

    private PwdModel retrievePwdData() throws InvalidAlgorithmParameterException, NoSuchPaddingException, UnrecoverableEntryException, IllegalBlockSizeException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, BadPaddingException, InvalidKeyException, NoSuchProviderException {
        // Retrieve data from intent
        Intent i = getIntent();
        int id = i.getIntExtra("id", -1);

        // Retrieve data from database
        DatabaseHandler db = new DatabaseHandler(this);
        db.openDatabase();

        return db.getPassword(id);
    }

    private void setupViews() {
        // Populate TextViews with the data
        title = findViewById(R.id.pwd_view_title);
        title.setText(pwdData.getTitle());

        username = findViewById(R.id.username);
        username.setText(pwdData.getUsername());

        pwd = findViewById(R.id.password);
        pwd.setText(pwdData.getPwd());

        url = findViewById(R.id.url);
        url.setText(pwdData.getUrl());
    }

    private void setupButtons() {
        setupCopyButtons();

        // Toggle password visibility
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

        // Go to website when button is clicked
        ImageButton redirectBtn = findViewById(R.id.redirect);
        redirectBtn.setOnClickListener(v -> {
            String website = String.valueOf(url.getText());
            if (!website.startsWith("http://") && !website.startsWith("https://"))
                website = "http://" + website;

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(website));
            startActivity(browserIntent);
        });

        // Return to home page when back button is clicked
        Button backBtn = findViewById(R.id.btn_back);
        backBtn.setOnClickListener(v -> {
            Intent i1 = new Intent(PwdView.this, MainActivity.class);
            startActivity(i1);
        });

        // Redirect to edit page
        Button editBtn = findViewById(R.id.btn_edit);
        editBtn.setOnClickListener(v -> {
            Intent i2 = new Intent(PwdView.this, EditPassword.class);
            i2.putExtra("pwdData", (Serializable) pwdData);
            startActivity(i2);
        });
    }

    private void setupCopyButtons() {
        ImageButton copyUsernameBtn = findViewById(R.id.copy_username),
                copyPwdButton = findViewById(R.id.copy_pwd),
                copyUrlBtn = findViewById(R.id.copy_url);

        setCopyBehvaiour(copyUsernameBtn, username);
        setCopyBehvaiour(copyPwdButton, pwd);
        setCopyBehvaiour(copyUrlBtn, url);

    }

    private void setCopyBehvaiour(ImageButton btn, TextView textView) {
        btn.setOnClickListener(v -> {
            // Copy text to clipboard
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("The fuck?", textView.getText());
            clipboard.setPrimaryClip(clip);

            // Notify user
            Toast.showShortWithCustomText(this, "Text copied to clipboard!");
        });
    }
}