package com.project.safekid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.LocaleManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChildDashboard extends AppCompatActivity {

    // Initializing variables
    private SensorManager sensorManager;
    FirebaseFirestore db;
    private Sensor lightSensor, accelerometerSensor, proximitySensor, ambientTemperatureSensor;

    FusedLocationProviderClient fusedLocationProviderClient;
    private  final  static int REQUEST_CODE=100;
    CardView sosCard;


    //OnCreate Method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_dashboard);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        ambientTemperatureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        db = FirebaseFirestore.getInstance();

        sosCard = findViewById(R.id.SOSCard);
        sosCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getIntent();
                String parentEmail = i.getStringExtra(JoinFamily.parentEmailToPass);
                String childID = i.getStringExtra(JoinFamily.childIDToPass);
                Map<String, Object> data = new HashMap<>();
                data.put("isEmergency", true);
                data.put("childWhoIsInAnEmergency", childID);

                db.collection("parents").document(parentEmail).set(data, SetOptions.merge())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("EmergencyAlert ", "DocumentSnapshot successfully written!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("EmergencyAlert", "Error writing document", e);
                            }
                        });
            }
        });

        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();

    }

    private void getLastLocation() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location !=null){
                                Geocoder geocoder=new Geocoder(ChildDashboard.this, Locale.getDefault());
                                List<Address> addresses= null;
                                try {
                                    addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                                    double latitude = addresses.get(0).getLatitude();
                                    double longitude = addresses.get(0).getLongitude();

                                    Intent i = getIntent();
                                    String parentEmail = i.getStringExtra(JoinFamily.parentEmailToPass);
                                    String childID = i.getStringExtra(JoinFamily.childIDToPass);

                                    Map<String, Object> data = new HashMap<>();
                                    data.put("latitude", latitude);
                                    data.put("longitude", longitude);

                                    db.collection("parents").document(parentEmail).collection("children").document(childID).set(data, SetOptions.merge())
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d("GeoLocation ", "DocumentSnapshot successfully written!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w("GeoLocation", "Error writing document", e);
                                                }
                                            });

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }else {
            askPermission();
        }
    }


    private void askPermission() {
        ActivityCompat.requestPermissions(ChildDashboard.this, new String[]
                {android.Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
            else {
                Toast.makeText(this, "Required Permission", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // SensorEventListener function to update light sensor data in the database
    private SensorEventListener lightSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            Intent i = getIntent();
            String parentEmail = i.getStringExtra(JoinFamily.parentEmailToPass);
            String childID = i.getStringExtra(JoinFamily.childIDToPass);

            float lux = sensorEvent.values[0];
            Map<String, Object> data = new HashMap<>();
            data.put("ambientLight", lux);

            db.collection("parents").document(parentEmail).collection("children").document(childID).set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ambientLightSensor ", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("ambientLightSensor", "Error writing document", e);
                        }
                    });
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
        }
    };

    // SensorEventListener function to update accelerometer sensor data in the database
    private SensorEventListener accelerometerSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Intent i = getIntent();
            String parentEmail = i.getStringExtra(JoinFamily.parentEmailToPass);
            String childID = i.getStringExtra(JoinFamily.childIDToPass);

            Map<String, Object> data = new HashMap<>();
            data.put("accelerationXAxis", event.values[0]);
            data.put("accelerationYAxis", event.values[1]);
            data.put("accelerationZAxis", event.values[2]);

            db.collection("parents").document(parentEmail).collection("children").document(childID).set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("acceleration ", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("acceleration ", "Error writing document", e);
                        }
                    });
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    // SensorEventListener function to update proximity sensor data in the database
    private SensorEventListener proximitySensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Intent i = getIntent();
            String parentEmail = i.getStringExtra(JoinFamily.parentEmailToPass);
            String childID = i.getStringExtra(JoinFamily.childIDToPass);

            Map<String, Object> data = new HashMap<>();
            data.put("proximity", event.values[0]);

            db.collection("parents").document(parentEmail).collection("children").document(childID).set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("proximitySensor ", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("proximitySensor ", "Error writing document", e);
                        }
                    });
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    // SensorEventListener function to update ambient temperature sensor data in the database
    private SensorEventListener ambientTemperatureSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Intent i = getIntent();
            String parentEmail = i.getStringExtra(JoinFamily.parentEmailToPass);
            String childID = i.getStringExtra(JoinFamily.childIDToPass);

            Map<String, Object> data = new HashMap<>();
            data.put("ambientTemperature", event.values[0]);

            db.collection("parents").document(parentEmail).collection("children").document(childID).set(data, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("ambientTemperature ", "DocumentSnapshot successfully written!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("ambientTemperature ", "Error writing document", e);
                        }
                    });
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    // Registering sensor listeners
    @Override
    public void onResume(){
        super.onResume();
        sensorManager.registerListener(lightSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(accelerometerSensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(proximitySensorEventListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        //sensorManager.registerListener(ambientTemperatureSensorEventListener, ambientTemperatureSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    // Unregistering sensor listeners
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(lightSensorEventListener);
        sensorManager.unregisterListener(accelerometerSensorEventListener);
        sensorManager.unregisterListener(proximitySensorEventListener);
        //sensorManager.unregisterListener(ambientTemperatureSensorEventListener);
    }
}


