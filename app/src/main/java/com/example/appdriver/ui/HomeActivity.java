package com.example.appdriver.ui;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.appdriver.Object.Location_driver;
import com.example.appdriver.R;
import com.example.appdriver.bar.HistoryActivity;
import com.example.appdriver.bar.SettingActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private DrawerLayout drawerLayout;
    private GoogleMap mMap;
    private View bookingCard;

    Button btnHistory,btnLogOut;

    // current Location
    private final int FINE_PERMISSION_CODE = 1;
//    Location currentLocation;
    Double latitude;
    Double longitude;
    FusedLocationProviderClient fusedLocationProviderClient;

    //searchView
    private SearchView mapSearchView;

    TextView tvName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(HomeActivity.this, HistoryActivity.class));
                } else if (id == R.id.nav_settings) {
                    startActivity(new Intent(HomeActivity.this, SettingActivity.class));
                } else if (id == R.id.nav_logout) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        // get Location
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocationAndsendLocation();

        // get INFOR BY UID
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String Id_Driver = currentFirebaseUser.getUid();
        getDriverInfo(Id_Driver);

        // SearchView
        mapSearchView = findViewById(R.id.searchView);
        mapSearchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String location = mapSearchView.getQuery().toString();
                List<Address> addressList = null;
                if(location != null){
                    // đổi từ địa điểm sang vị trí địa lí
                    Geocoder geocoder = new Geocoder(HomeActivity.this);
                    try {
                        addressList = geocoder.getFromLocationName(location, 1);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
//                    if(addressList != null && addressList.isEmpty()){
                        Address address= addressList.get(0); LatLng local = new LatLng(address.getLatitude() , address.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(local).title("Vị trí của bạn"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(local,10));
//                        }else{
//                            Toast.makeText(HomeActivity.this, "Hãy thử nhập lại vị trí", Toast.LENGTH_SHORT).show();
//                        }
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        mapFragment.getMapAsync(HomeActivity.this);

        bookingCard = findViewById(R.id.bookingCard);

        btnHistory = findViewById(R.id.btnHistory);
        btnLogOut = findViewById(R.id.btnLogOut);


        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIt = new Intent(HomeActivity.this , HistoryActivity.class);
                startActivity(myIt);
            }
        });
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


    }

    private void getLastLocationAndsendLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this , new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION} , FINE_PERMISSION_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location != null){
                    longitude =  location.getLongitude() ;
                    latitude = location.getLatitude();
                    // insert database
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;

                    GeoPoint location_driver = new GeoPoint(latitude, longitude);
                    String driver_ref = currentFirebaseUser.getUid();
                    Timestamp created_at = new Timestamp(new Date());

                    FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {
                                if (!task.isSuccessful()) {
                                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                    return;
                                }
                                String token = task.getResult();
                                Log.d(TAG, token);
                                Location_driver locationDriver = new Location_driver(location_driver , driver_ref ,created_at, token , false);

                                db.collection("driver_locations").document()
                                        .set(locationDriver)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                            }
                                        });
                            }
                        });
                }
            }
        });
    }


    private void getDriverInfo(String driverId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference driverRef = db.collection("drivers").document(driverId);
        driverRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String userName = document.getString("name");
//                        Toast.makeText(HomeActivity.this, userName, Toast.LENGTH_SHORT).show();
//                        tvName.setText(userName);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == FINE_PERMISSION_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocationAndsendLocation();
            }else{
                Toast.makeText(this, "Quyền truy cập vị trí bị từ chối, cho phép để bắt đầu", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
