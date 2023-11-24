package com.project.safekid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.project.safekid.databinding.ActivityMapsBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.EventListener;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Initializing variables
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FirebaseFirestore db;
    String childID, parentEmail;

    // OnCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Location");

        Intent i = getIntent();
        childID = i.getStringExtra(ChildInfo.childIDToPass);
        parentEmail= i.getStringExtra(ChildInfo.parentEmailToPass);

        // Assigning values to the variables
        db = FirebaseFirestore.getInstance();
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    // OnMapReady method
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DocumentReference docRef = db.collection("parents").document(parentEmail).collection("children").document(childID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public  void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        double longitude = (double) doc.get("longitude");

                        double latitude = (double) doc.get("latitude");
                        LatLng marker = new LatLng(latitude ,  longitude);
                        mMap.addMarker(new MarkerOptions().position(marker).title("Location of "+childID));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 3.0f));
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                    }else {
                        Log.d("TAG", "No such document");
                    }
                }else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}