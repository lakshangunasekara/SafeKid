package com.project.safekid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.StartupTime;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.project.safekid.databinding.ActivityMapsBinding;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChildInfo extends AppCompatActivity  {

    TextView text;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FirebaseFirestore db;
    String childID, parentEmail;
    public static final String childIDToPass="id";
    public static final String parentEmailToPass="email";
    TextView textViewProximity, textViewAmbLight, textViewAccelerometer, textViewAmbTemp;
    String proximity, accelerometerX, accelerometerY, accelerometerZ, ambientLight, ambientTemp;
    CardView checkLocationCard;
    FusedLocationProviderClient fusedLocationProviderClient;
    TextView country,city,address,longitude,latitude;
    private  final  static int REQUEST_CODE=100;
    BottomNavigationView bottomNavigationView;
    Bundle bundle;
    FamilyFragment familyFragment;
    BottomSheetDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Getting Textviews

        textViewProximity = findViewById(R.id.proxSensor);
        textViewAmbLight = findViewById(R.id.ambLightSensor);
        textViewAccelerometer = findViewById(R.id.accelerometerSensor);
        //textViewAmbTemp = findViewById(R.id.ambTempSensor);
        checkLocationCard = findViewById(R.id.checkLocationCard);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);

        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        city = findViewById(R.id.city);
        address = findViewById(R.id.address);
        country = findViewById(R.id.country);


        Intent i = getIntent();
        childID = i.getStringExtra(FamilyFragment.childIDToPass);
        parentEmail= i.getStringExtra(FamilyFragment.parentEmailToPass);

        // Method for refresh data
        setTitle(childID);
        refreshAllData();

        dialog = new BottomSheetDialog(this);
        createDialog();

        // Bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.edit:
                        dialog.show();
                        return true;
                    case R.id.refresh:
                        refreshAllData();
                        return true;

                    case R.id.delete:
                        db.collection("parents").document(parentEmail).collection("children").document(childID)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Delete Child", "DocumentSnapshot successfully deleted!");
                                        Intent j = new Intent(ChildInfo.this, ParentDashboard.class);
                                        j.putExtra(parentEmailToPass, parentEmail);
                                        Toast.makeText(ChildInfo.this, "Child deleted!", Toast.LENGTH_SHORT).show();
                                        startActivity(j);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Delete Child", "Error deleting document", e);
                                    }
                                });

                        return true;
                }
                return false;
            }
        });

        // OnClickListener for the check location card
        checkLocationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j = new Intent(ChildInfo.this, MapsActivity.class);
                j.putExtra(childIDToPass, childID);
                j.putExtra(parentEmailToPass, parentEmail);
                startActivity(j);
            }
        });




    }

    // Method to create dialog to edit name
    private void createDialog() {
        View view = getLayoutInflater().inflate(R.layout.bottom_dialog, null, false);
        EditText newName = view.findViewById(R.id.newName);
        Button save = view.findViewById(R.id.saveChanges);
        save.setEnabled(false);

        newName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(newName.getText().toString().isEmpty()){
                    save.setEnabled(false);
                }else{
                    save.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        // OnClickListener for the save button
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();
                DocumentReference docRef = db.collection("parents").document(parentEmail).collection("children").document(childID);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot doc = task.getResult();

                            Map<String, Object> data = new HashMap<>();
                            data.put("accelerationXAxis", doc.get("accelerationXAxis").toString());
                            data.put("accelerationYAxis", doc.get("accelerationYAxis").toString());
                            data.put("accelerationZAxis", doc.get("accelerationZAxis").toString());
                            data.put("ambientLight", doc.get("ambientLight").toString());
                            data.put("proximity", doc.get("proximity").toString());
                            //data.put("ambientTemperature", doc.get("ambientTemperature").toString());
                            data.put("longitude", doc.get("longitude"));
                            data.put("latitude", doc.get("latitude"));

                            db.collection("parents").document(parentEmail).collection("children").document(newName.getText().toString()).set(data, SetOptions.merge())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d("UpdateName ", "DocumentSnapshot successfully written!");
                                            db.collection("parents").document(parentEmail).collection("children").document(childID)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("Delete Child", "DocumentSnapshot successfully deleted!");
                                                            Intent j = new Intent(ChildInfo.this, ParentDashboard.class);
                                                            j.putExtra(parentEmailToPass, parentEmail);
                                                            Toast.makeText(ChildInfo.this, "Changes saved", Toast.LENGTH_SHORT).show();
                                                            startActivity(j);

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("Delete Child", "Error deleting document", e);
                                                        }
                                                    });
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@org.checkerframework.checker.nullness.qual.NonNull Exception e) {
                                            Log.w("UpdateName ", "Error writing document", e);
                                        }
                                    });


                        }else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });
                setTitle(newName.getText().toString());
                dialog.dismiss();
                Toast.makeText(ChildInfo.this, "Changes saved", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setContentView(view);
    }

    private void refreshAllData() {
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("parents").document(parentEmail).collection("children").document(childID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public  void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()){
                        proximity = doc.get("proximity").toString();
                        ambientLight = doc.get("ambientLight").toString();
                        accelerometerX = doc.get("accelerationXAxis").toString();
                        accelerometerY = doc.get("accelerationYAxis").toString();
                        accelerometerZ = doc.get("accelerationZAxis").toString();
                        //ambientTemp = doc.get("ambientTemperature").toString();


                        textViewProximity.setText("Proximity Sensor: "+proximity);
                        textViewAmbLight.setText("Ambient Light Sensor: "+ambientLight);
                        textViewAccelerometer.setText("Accelerometer Sensor: \n\tX:"+accelerometerX+ " \n\tY:"+ accelerometerY+" \n\tZ:"+accelerometerZ);
                        //textViewAmbTemp.setText("Ambient Temperature Sensor: "+ambientTemp);

                        Geocoder geocoder=new Geocoder(ChildInfo.this);

                        try {
                            List<Address> addresses = geocoder.getFromLocation((double) doc.get("latitude"), (double) doc.get("longitude"),1);
                            latitude.setText("Lagitude :" +addresses.get(0).getLatitude());
                            longitude.setText("Longitude :"+addresses.get(0).getLongitude());
                            address.setText("Address :"+addresses.get(0).getAddressLine(0));
                            city.setText("City :"+addresses.get(0).getLocality());
                            country.setText("Country :"+addresses.get(0).getCountryName());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(ChildInfo.this, "Getting data", Toast.LENGTH_SHORT).show();

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