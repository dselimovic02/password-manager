package com.pwdmngr.passwordmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pwdmngr.passwordmanager.adapter.PwdAdapter;
import com.pwdmngr.passwordmanager.model.PwdModel;
import com.pwdmngr.passwordmanager.modules.BackPressed;
import com.pwdmngr.passwordmanager.util.DatabaseHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView pwdRecyclerView;
    private PwdAdapter pwdAdapter;
    private List<PwdModel> pwdList;
    private FloatingActionButton fab;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Disable system back button
        BackPressed.disable(getOnBackPressedDispatcher(), this);

        pwdList = new ArrayList<>();
        db = new DatabaseHandler(this);
        db.openDatabase();

        pwdRecyclerView = findViewById(R.id.pwdRecyclerView);
        pwdRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pwdAdapter = new PwdAdapter(this);
        pwdRecyclerView.setAdapter(pwdAdapter);

        fab = findViewById(R.id.newBtn);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, AddNewPassword.class);
            startActivity(i);
        });

        pwdList = db.getAllPasswords();
        Collections.reverse(pwdList);
        pwdAdapter.update(pwdList);
    }
}