package com.example.train_booking_scheduling_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;
import java.util.List;

public class TrainSchedulesSearchResultsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TrainTileAdapter adapter;
    private List<Train> trainList;
    private FirebaseFirestore db; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_schedules_search_results);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        TextView startStationDisplay = findViewById(R.id.startStationDisplay);
        TextView endStationDisplay = findViewById(R.id.endStationDisplay);
        TextView selectedDateDisplay = findViewById(R.id.selectedDateDisplay);
        TextView resultsCount = findViewById(R.id.resultsCount);
        Button backButton = findViewById(R.id.backButton);
        Button mapButton = findViewById(R.id.mapButton);

        // Get search details from the intent
        String startStation = getIntent().getStringExtra("startStation");
        String endStation = getIntent().getStringExtra("endStation");
        String travelDate = getIntent().getStringExtra("travelDate");

        // Check if the data is received
        if (startStation == null || endStation == null || travelDate == null) {
            Toast.makeText(this, "Error: Missing search details", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if data is missing
            return;
        }

        // Display search details
        startStationDisplay.setText(startStation);
        endStationDisplay.setText(endStation);
        selectedDateDisplay.setText("Selected Date: " + travelDate);

        // Back button
        backButton.setOnClickListener(view -> onBackPressed());

        // RecyclerView setup
        recyclerView = findViewById(R.id.resultsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        trainList = new ArrayList<>();
        adapter = new TrainTileAdapter(trainList);
        recyclerView.setAdapter(adapter);

        // Fetch train data from Firestore
        fetchTrainData(startStation, endStation, travelDate, resultsCount);

        // Map button logic (Fixed)
        mapButton.setOnClickListener(view -> {
            String startLocation = startStationDisplay.getText().toString().trim();
            String endLocation = endStationDisplay.getText().toString().trim();

            if (startLocation.isEmpty() || endLocation.isEmpty()) {
                Toast.makeText(TrainSchedulesSearchResultsActivity.this, "Start or End station is missing!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create Google Maps URI
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + endLocation + "&mode=t");

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            // Try launching Google Maps App
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                // Open Google Maps in browser instead of Play Store
                Uri webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=" + startLocation + "&destination=" + endLocation + "&travelmode=transit");
                Intent webIntent = new Intent(Intent.ACTION_VIEW, webUri);
                startActivity(webIntent);
            }
        });
    }

    private void fetchTrainData(String startStation, String endStation, String travelDate, TextView resultsCount) {
        Log.d("FirestoreQuery", "Searching for trains from " + startStation + " to " + endStation + " on " + travelDate);

        // Reference to the "Train" collection in Firestore (Correcting the collection name)
        CollectionReference trainsRef = db.collection("Train");

        // Query Firestore for matching trains
        trainsRef.whereEqualTo("startStation", startStation.trim())
                .whereEqualTo("endStation", endStation.trim())
                .whereEqualTo("date", travelDate.trim())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        trainList.clear();
                        QuerySnapshot result = task.getResult();
                        if (result != null) {
                            for (QueryDocumentSnapshot document : result) {
                                Train train = document.toObject(Train.class);
                                trainList.add(train);
                                Log.d("FirestoreData", "Train found: " + train.getName());
                            }
                        }
                        adapter.notifyDataSetChanged(); // Update RecyclerView

                        // Update the results count
                        resultsCount.setText("Found " + trainList.size() + " trains");

                        // Check if no trains were found
                        if (trainList.isEmpty()) {
                            Toast.makeText(TrainSchedulesSearchResultsActivity.this, "No trains found", Toast.LENGTH_SHORT).show();
                            Log.d("FirestoreData", "No matching trains found.");
                        }
                    } else {
                        Log.e("Firestore", "Failed to read data", task.getException());
                        Toast.makeText(TrainSchedulesSearchResultsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
