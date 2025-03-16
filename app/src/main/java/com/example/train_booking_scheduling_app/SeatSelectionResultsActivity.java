package com.example.train_booking_scheduling_app;

import android.content.Intent;
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

public class SeatSelectionResultsActivity extends AppCompatActivity implements TrainTileAdapter.OnTrainSelectListener {
    private RecyclerView recyclerView;
    private TrainTileAdapter adapter;
    private List<Train> trainList;
    private FirebaseFirestore db; // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection_results);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        TextView startStationDisplay = findViewById(R.id.startStationDisplay);
        TextView endStationDisplay = findViewById(R.id.endStationDisplay);
        TextView selectedDateDisplay = findViewById(R.id.selectedDateDisplay);
        TextView resultsCount = findViewById(R.id.resultsCount);
        Button backButton = findViewById(R.id.backButton);

        // Get search details from the intent
        String startStation = getIntent().getStringExtra("startStation");
        String endStation = getIntent().getStringExtra("endStation");
        String travelDate = getIntent().getStringExtra("travelDate");

        // Check if the data is received
        if (startStation == null || endStation == null || travelDate == null) {
            Toast.makeText(this, "Error: Missing search details", Toast.LENGTH_SHORT).show();
            Log.e("IntentData", "Missing search details: startStation=" + startStation + ", endStation=" + endStation + ", travelDate=" + travelDate);
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
        adapter = new TrainTileAdapter(trainList, this); // Pass `this` as listener
        recyclerView.setAdapter(adapter);

        // Fetch train data from Firestore
        fetchTrainData(startStation, endStation, travelDate, resultsCount);
    }

    private void fetchTrainData(String startStation, String endStation, String travelDate, TextView resultsCount) {
        Log.d("FirestoreQuery", "Searching for trains from " + startStation + " to " + endStation + " on " + travelDate);

        // Reference to the "Train" collection in Firestore
        CollectionReference trainsRef = db.collection("Train");

        // Query Firestore for matching trains
        trainsRef.whereEqualTo("startStation", startStation.trim())
                .whereEqualTo("endStation", endStation.trim())
                .whereEqualTo("date", travelDate.trim())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        trainList.clear(); // Clear previous results
                        QuerySnapshot result = task.getResult();
                        if (result != null && !result.isEmpty()) {
                            for (QueryDocumentSnapshot document : result) {
                                Train train = document.toObject(Train.class);
                                trainList.add(train);
                                Log.d("FirestoreData", "Train found: " + train.getName() + " (" + train.getNumber() + ")");
                            }
                            resultsCount.setText("Found " + trainList.size() + " trains");
                        } else {
                            resultsCount.setText("No trains found");
                            Toast.makeText(SeatSelectionResultsActivity.this, "No trains found", Toast.LENGTH_SHORT).show();
                            Log.w("FirestoreData", "No matching trains found.");
                        }
                        adapter.notifyDataSetChanged(); // Update RecyclerView
                    } else {
                        Log.e("Firestore", "Failed to read data", task.getException());
                        Toast.makeText(SeatSelectionResultsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onTrainSelected(Train selectedTrain) {
        if (selectedTrain == null) {
            Log.e("TrainSelection", "Selected train is null");
            Toast.makeText(this, "Error: Train selection failed", Toast.LENGTH_SHORT).show();
            return;
        }

        int trainNumber = selectedTrain.getNumber();
        String trainName = selectedTrain.getName();
        String startStation = selectedTrain.getStartStation();
        String endStation = selectedTrain.getEndStation();
        int availableSeats = selectedTrain.getAvailableSeats();

        if (trainNumber <= 0) {
            Log.e("TrainSelection", "Invalid train number: " + trainNumber);
            Toast.makeText(this, "Error: Invalid train selection", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("TrainSelection", "Train selected: " + trainName + " (ID: " + trainNumber + ")");

        Intent intent = new Intent(this, SeatBookingDetailsActivity.class);
        intent.putExtra("trainNumber", trainNumber);
        intent.putExtra("trainName", trainName);
        intent.putExtra("startStation", startStation);
        intent.putExtra("endStation", endStation);
        intent.putExtra("availableSeats", availableSeats);
        startActivity(intent);
    }

}
