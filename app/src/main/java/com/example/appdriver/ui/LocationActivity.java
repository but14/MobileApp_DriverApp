package com.example.appdriver.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appdriver.R;
import com.google.firebase.auth.FirebaseAuth;

public class LocationActivity extends AppCompatActivity {
    Button btn_relax, btn_cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_location);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btn_cont = findViewById(R.id.btn_cont);
        btn_relax = findViewById(R.id.btn_relax);
        btn_relax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(LocationActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        btn_cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LocationActivity.this, "Bạn đã hoàn thành một chuyến đi dài.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LocationActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }
}