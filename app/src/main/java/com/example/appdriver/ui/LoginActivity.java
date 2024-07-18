package com.example.appdriver.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appdriver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    EditText edtPass, edtEmail ;
    Button btnLogin , btnRegisInLogin;
    FirebaseAuth auth;
    String idToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        btnRegisInLogin = findViewById(R.id.btnRegisInLogin);
        auth = FirebaseAuth.getInstance();


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString().trim();
                String pass = edtPass.getText().toString().trim();
                if(email.isEmpty()){
                    edtEmail.setError("Email không được trống");
                }
                if(pass.isEmpty()){
                    edtPass.setError("Password không được trống");
                }
                else{
                    auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "Login thành công", Toast.LENGTH_SHORT).show();
                                Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(homeActivity);
                                finish();
                            }else{
                                Toast.makeText(LoginActivity.this, "Login thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });


        btnRegisInLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIt = new Intent(LoginActivity.this , RegisterActivity.class);
                startActivity(myIt);
                finish();
            }
        });

    }

    public void getToken(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            currentUser.getIdToken(true)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            idToken = task.getResult().getToken();
                            // Ở đây, bạn có thể sử dụng idToken cho mục đích của mình
                        } else {
                            System.out.println("Không có Token Invalid");
                        }
                    });
        }
    }

    public void signInToken(){
        FirebaseMessaging.getInstance().subscribeToTopic("Có khách nè")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "khoong co quyen", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String token = task.getResult().toString();
                        Log.d(TAG, token);
                        Toast.makeText(LoginActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }


}