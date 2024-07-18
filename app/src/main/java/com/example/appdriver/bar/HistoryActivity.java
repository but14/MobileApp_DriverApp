package com.example.appdriver.bar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appdriver.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<String> listRide ;
    ArrayAdapter<String> myadapter;
    ListView lv;
    TextView txt_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        lv = findViewById(R.id.lv);

        listRide = new ArrayList<>();

        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        String driver_ref = currentFirebaseUser.getUid();

        db.collection("rides")
                .whereEqualTo("driver_ref", driver_ref)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String rideId = document.getId();
                        listRide.add(rideId);
                        Toast.makeText(HistoryActivity.this, rideId, Toast.LENGTH_SHORT).show();
                        myadapter = new ArrayAdapter<>(HistoryActivity.this, android.R.layout.simple_list_item_1, listRide );
                        lv.setAdapter(myadapter);
                    }
                });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent myIt = new Intent(HistoryActivity.this , Ride_Activity.class);
                String ID_RIDE = listRide.get(position).toString();
                myIt.putExtra("ID_RIDE" , ID_RIDE);
                startActivity(myIt);
            }
        });





//        DocumentReference docRef = db.collection("rides").document("Id nhận vào của chuyến hàng");
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
//                    } else {
//                        Log.d(TAG, "No such document");
//                    }
//                } else {
//                    Log.d(TAG, "get failed with ", task.getException());
//                }
//            }
//        });

//  khi nhấn vào xem Lịch sư chuyến xe , thì sẽ hiển thị ra 1 list các Id của chuyến đó => khi click vào Id này sẽ hiển thị ra cái Bill của thông tin chuyến xe, trong đây có 1 nút để nhấn quay lại ( chỉ cần finish() ).
        // xem lại Adapter custom
        // nút đăng xuất
        // hiển thị tên người dùng
        // đổi lại giao diện qua cái khác
        //


    }
}
