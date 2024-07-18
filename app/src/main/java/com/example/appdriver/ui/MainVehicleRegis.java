package com.example.appdriver.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appdriver.Object.Driver;
import com.example.appdriver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class MainVehicleRegis extends AppCompatActivity {

    EditText edtBienSo;
    String ItemClick;
    String tokenUser;

    List<String> listItem = new ArrayList<>();
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;
    Button buttonRegisVe;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_vehicle_regis);
        autoCompleteTextView = findViewById(R.id.edt_Vehicle);

        buttonRegisVe = findViewById(R.id.btnregisVeh);
        edtBienSo = findViewById(R.id.edtBienSo);

        getVehicles();

        adapterItems = new ArrayAdapter<String>(this, R.layout.list_vehicle, listItem);
        autoCompleteTextView.setAdapter(adapterItems);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ItemClick = adapterView.getItemAtPosition(position).toString();
            }
        });

        buttonRegisVe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIt = getIntent();
                // lấy bundle khỏi INtent
                Bundle myBd = myIt.getBundleExtra("mypackage");
                // lấy data khỏi Bundle
                String name = myBd.getString("name");
                String email = myBd.getString("email");
                String pass = myBd.getString("pass");
                String password_hash = pass ;
                String phone_number = myBd.getString("phone");
                Timestamp created_at = new Timestamp(new Date());
                String UId_driver =  myBd.getString("UId_driver");
                Boolean status = true;
                String vehicle_ref = ItemClick;
                String license_plate = edtBienSo.getText().toString();

                Driver driver = new Driver(name, email, phone_number,  password_hash, created_at, status , vehicle_ref, license_plate);
                db.collection("drivers").document(UId_driver)
                        .set(driver)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainVehicleRegis.this, "Đăng kí thành công", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainVehicleRegis.this, "Đăng kí thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
                Intent regisVeIntent = new Intent(MainVehicleRegis.this,HomeActivity.class);
                startActivity(regisVeIntent);
                finish();
            }
        });

    }

    private void getVehicles() {
        db.collection("vehicles").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String vehicleType = document.getString("vehicle_type");
                        if (vehicleType != null) {
                            listItem.add(vehicleType);
                        }
                    }
                }
            }
        });
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        tokenUser = task.getResult();
                    }
                });
    }


}