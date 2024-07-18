package com.example.appdriver.ui;

import static android.content.ContentValues.TAG;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appdriver.R;
import com.example.appdriver.bar.HistoryActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class InforRideActivity extends AppCompatActivity {

    Button btnRoad,btn_next;
    EditText edt_IdRide,edt_Pickup,edt_Dropoff,edt_phoneUser,edt_nameUser,edt_fare,edt_created,edt_status;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    String phoneUser ;
    String nameUser ;
    String userDestination;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_infor_ride);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnRoad = findViewById(R.id.btnRoad);
        btn_next = findViewById(R.id.btn_next);

        edt_IdRide = findViewById(R.id.edt_IdRide);
        edt_Pickup = findViewById(R.id.edt_Pickup);
        edt_Dropoff = findViewById(R.id.edt_Dropoff);
        edt_phoneUser = findViewById(R.id.edt_phoneUser);
        edt_nameUser = findViewById(R.id.edt_nameUser);
        edt_fare = findViewById(R.id.edt_fare);
        edt_created = findViewById(R.id.edt_created);
        edt_status = findViewById(R.id.edt_status);


        // gồm hiển thị thông tin của chuyến đi , và 1 nút có thể khi click vào nó sẽ dẫn người tài xế này đi tới chỗ người dùng bằng gg maaps.
        // Khi nhấp vào InforRideActivity( Thông tinc chuyến đi) này , thì 2 sự kiện
        //                                                          là 1 là set status của driver_Location của tài xế đó bằng True nghĩa là đang bận.
        //                                                          là 2 là hiển thị lên Thông tin chuyến đi này thông qua ID_RIDE( gồm vị trí đến , vị trí đón , nois chung là thông tin về chuyến đi này.)
        // khi người tài xế này tới và đón khách , và sau đó chở khách đi
        //Trong lúc này bên phía User sẽ phải hiển thị thông tin chuyến đi và thông tin của nguười tài xế đó như ( phone , name , biển số xe,...)
        // Khi đến nơi, tài xế sẽ nhấn Hoàn thành chuyến đi , sau đó tafi xế đó sẽ nhận được Bill thanh toán( giá tiền, v trí đón ,...) còn bên User nhận được giá tiền cần thanh toán đồ .
        // Sau khi tài xế nhần hoàn thành thì  update status của Bill là true ( nghĩa là đã hoàn thành ) .
        // Sau đó bên Driver sẽ xóa vị trí cũ trong Db , và có 1 Activity mới gồm 2 nút là tiếp tục và nghỉ ngơi . để có thể gửi lại Location_Driver hoặc là sẽ đăng xuất khỏi thiết bị.


        getUidDriverAndUpdateStatus();
        Intent myIt = getIntent();
        String Id_Ride_Message = myIt.getStringExtra("Id_Ride_Message");
        String Id_Ride = Id_Ride_Message.split(" ")[8];
        edt_IdRide.setText(Id_Ride);

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
                        edt_Pickup.setText(pickup_location);
                        userDestination = pickup_location;


                        // dropoff_location
                        GeoPoint geoPointD = document.getGeoPoint("dropoff_location");
                        double latitude1 = geoPointD.getLatitude();
                        double longitude1 = geoPointD.getLongitude();
                        String dropoff_location = getAddressByLALO(latitude1,longitude1);
                        edt_Dropoff.setText(dropoff_location);

                        // Information user
                        String IdUser = document.getString("user_ref");
                        DocumentReference docRef = db.collection("users").document(IdUser);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        phoneUser = document.getString("phone_number");
                                        nameUser = document.getString("name");
                                        edt_phoneUser.setText(phoneUser);
                                        edt_nameUser.setText(nameUser);
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
                        edt_fare.setText(fareString);

//                         Thời gian tạo Chuyến đi này
                        String date = document.getString("created_at");
//                      String Created_at = getDate(date) ;
                        edt_created.setText(date);

                        // status
                        String status = document.getString("status");
                        edt_status.setText(status);
                    }
                    else {
                        Toast.makeText(InforRideActivity.this, "KHông có dữ liệu nào", Toast.LENGTH_SHORT).show();
                        Log.e("Errors", "No such document");
                    }
                } else {
                    Toast.makeText(InforRideActivity.this, "KHông có dữ liệu nào okeok", Toast.LENGTH_SHORT).show();
                    Log.e("Errors", "get failed with ", task.getException());
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("rides").document(Id_Ride)
                        .update(
                                "status", "completed",
                                "completed_at", new Date());
                db.collection("driver_locations").document()
                        .delete();
                Intent myIt = new Intent(InforRideActivity.this , HomeActivity.class);
                startActivity(myIt);
            }
        });
        btnRoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userLocation = "Long Khánh,Đồng nai";
                userDestination = "tân bình ,Thành phố hồ chí minh";
                if(userLocation.equals("") || userDestination.equals("")){
                    Toast.makeText(InforRideActivity.this, "Enter your location and destination", Toast.LENGTH_SHORT).show();
                }else{
                    directionByGoogleMaps(userLocation , userDestination);
                }
            }
        });

    }


    private void getUidDriverAndUpdateStatus() {
        FirebaseUser driver = FirebaseAuth.getInstance().getCurrentUser();
        final String current = driver.getUid();//getting unique user id

        db.collection("driver_locations")
                .whereEqualTo("driver_ref", current)//looks for the corresponding value with the field
                // in the database
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                // These values must exactly match the fields you have in your db
                                document.getReference().update("status", true);
                            }
                        }else {
                            Log.e("Errors", "Update status is failed");
                        }
                    }
                });
    }

    private void directionByGoogleMaps(String from , String to){
        try {
            Uri uri = Uri.parse("https://www.google.com/maps/dir/" + from + "/" + to);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }catch (ActivityNotFoundException e){
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
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
        Geocoder geocoder = new Geocoder(InforRideActivity.this, Locale.getDefault());
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
