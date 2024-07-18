package com.example.appdriver.bar;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appdriver.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Locale;

public class Ride_Activity extends AppCompatActivity {
    TextView txtIdChuyenDi,txtViTriDon,txtViTriTraKhach,txtSoDienThoaiKhach,txtTenKhachHang,txtSoTienPhaiTra,txtThoiGianTaoChuyen,txtTrangThai;
    Button btnBack;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ride);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnBack = findViewById(R.id.btnBack);
        txtIdChuyenDi = findViewById(R.id.txtIdChuyenDi);
        txtSoDienThoaiKhach = findViewById(R.id.txtSoDienThoaiKhach);
        txtSoTienPhaiTra = findViewById(R.id.txtSoTienPhaiTra);
        txtTrangThai = findViewById(R.id.txtTrangThai);
        txtViTriDon = findViewById(R.id.txtViTriDon);
        txtViTriTraKhach= findViewById(R.id.txtViTriTraKhach);
        txtTenKhachHang = findViewById(R.id.txtTenKhachHang);
        txtThoiGianTaoChuyen = findViewById(R.id.txtThoiGianTaoChuyen);

        Intent myIt = getIntent();
        String Id_Ride = myIt.getStringExtra("ID_RIDE");

        DocumentReference docRef = db.collection("rides").document(Id_Ride);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // pickup_location
                        GeoPoint geoPointP = document.getGeoPoint("pickup_location");
                        double latitude = geoPointP.getLatitude();
                        double longitude = geoPointP.getLongitude();
                        String pickup_location = getAddressByLALO(latitude,longitude);
                        txtViTriDon.setText(pickup_location);


                        // dropoff_location
                        GeoPoint geoPointD = document.getGeoPoint("dropoff_location");
                        double latitude1 = geoPointD.getLatitude();
                        double longitude1 = geoPointD.getLongitude();
                        String dropoff_location = getAddressByLALO(latitude1,longitude1);
                        txtViTriTraKhach.setText(dropoff_location);

                        // Information user
                        String IdUser = document.getString("user_ref");
                        DocumentReference docRef = db.collection("users").document(IdUser);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        String phoneUser = document.getString("phone_number");
                                        String nameUser = document.getString("name");
                                        txtTenKhachHang.setText(nameUser);
                                        txtSoDienThoaiKhach.setText(phoneUser);
                                    } else {
                                        Log.d(TAG, "No such document");
                                    }
                                } else {
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });

                        // Fare , Distance
                        String fareString = document.getString("fare");
                        txtSoTienPhaiTra.setText(fareString + " " + "VND");

//                         Thời gian tạo Chuyến đi này
                        String date = document.getString("created_at");
//                      String Created_at = getDate(date) ;
                        txtThoiGianTaoChuyen.setText(date);

                        // status
                        String status = document.getString("status");
                        txtTrangThai.setText(status);
                    }
                    else {
                        Toast.makeText(Ride_Activity.this, "KHông có dữ liệu nào", Toast.LENGTH_SHORT).show();
                        Log.e("Errors", "No such document");
                    }
                } else {
                    Toast.makeText(Ride_Activity.this, "KHông có dữ liệu nào okeok", Toast.LENGTH_SHORT).show();
                    Log.e("Errors", "get failed with ", task.getException());
                }
            }
        });




        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



//    private String getDate(String time1) {
//        int time = Integer.parseInt(time1);
//        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//        cal.setTimeInMillis(time * 1000);
//        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
//        return date;
//    }

    private String getAddressByLALO(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(Ride_Activity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }




}