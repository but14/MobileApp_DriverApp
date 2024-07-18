package com.example.appdriver.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdriver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    EditText edtName, edtEmail , edt_phone, edt_pass, edt_rePass;

    Button btnRegis , btnLoginInRegis;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edt_phone = findViewById(R.id.edt_phone);
        edt_pass = findViewById(R.id.edt_pass);
        edt_rePass = findViewById(R.id.edt_rePass);
        btnRegis = findViewById(R.id.btnregis);
        btnLoginInRegis = findViewById(R.id.btnLoginInRegis);


        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edtName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edt_phone.getText().toString().trim();
                String pass = edt_pass.getText().toString().trim();
                String rePass = edt_rePass.getText().toString().trim();

                if(name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass.isEmpty() || rePass.isEmpty() ) {
                    edtName.setError("Các trường không được để trống");
                }
                else {
                    auth.createUserWithEmailAndPassword(email , pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                auth = FirebaseAuth.getInstance();

                                Toast.makeText(RegisterActivity.this, "Hãy tiếp tục", Toast.LENGTH_SHORT).show();
                                // khai báo Intent
                                Intent IntentVehicles = new Intent(RegisterActivity.this, MainVehicleRegis.class);
                                // đưa dữ liệu vào Bundle
                                Bundle myBd = new Bundle();
                                String userId = auth.getCurrentUser().getUid();
                                myBd.putString("UId_driver",userId);
                                myBd.putString("name" , name );
                                myBd.putString("email" , email );
                                myBd.putString("pass" , pass );
                                myBd.putString("phone" , phone );
                                // đưa Bundle vào Intent
                                IntentVehicles.putExtra("mypackage" , myBd);
                                // khởi động Intent
                                startActivity(IntentVehicles);
                                finish();
                            }else{
                                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        btnLoginInRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIt = new Intent(RegisterActivity.this , LoginActivity.class);
                startActivity(myIt);
                finish();
            }
        });
    }
}