package com.example.train_booking_scheduling_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TrainSchedulesActivity extends AppCompatActivity {
    private Spinner startStationSpinner, endStationSpinner;
    private Button travelDateButton, searchButton, backButton;
    private Calendar calendar;
    private String selectedDate = "";
    private FirebaseFirestore db;
    private static final String TAG = "TrainSchedulesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_schedules);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize UI elements
        initializeViews();

        // Initialize Calendar instance
        calendar = Calendar.getInstance();

        // Load station names from Firestore
        loadStations();

        // Set up button click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        startStationSpinner = findViewById(R.id.startStationSpinner);
        endStationSpinner = findViewById(R.id.endStationSpinner);
        travelDateButton = findViewById(R.id.travelDate);
        searchButton = findViewById(R.id.searchButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupClickListeners() {
        // Set Date Picker for travel date
        travelDateButton.setOnClickListener(view -> showDatePicker());

        // Handle search button click
        searchButton.setOnClickListener(view -> searchTrains());

        // Handle back button click
        backButton.setOnClickListener(view -> onBackPressed());
    }

    private void loadStations() {
        DocumentReference docRef = db.collection("stations").document("station_list");

        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    List<String> stationNames = (List<String>) document.get("name");
                    if (stationNames != null && !stationNames.isEmpty()) {
                        setupStartStationSpinner(stationNames);
                        setupEndStationSpinner(stationNames);
                    } else {
                        Log.e(TAG, "Station names list is null or empty");
                        showError("No stations available");
                    }
                } else {
                    Log.e(TAG, "No such document");
                    showError("Could not load stations");
                }
            } else {
                Log.e(TAG, "Error fetching stations", task.getException());
                showError("Error loading stations");
            }
        });
    }

    private void setupStartStationSpinner(List<String> stationNames) {
        ArrayList<String> startStationItems = new ArrayList<>();
        startStationItems.add("Select Start Station"); // Custom hint for start station
        startStationItems.addAll(stationNames);

        ArrayAdapter<String> startAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                startStationItems
        );
        startAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        runOnUiThread(() -> {
            startStationSpinner.setAdapter(startAdapter);
            startStationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        String selected = parent.getItemAtPosition(position).toString();
                        Log.d(TAG, "Start station selected: " + selected);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        });
    }

    private void setupEndStationSpinner(List<String> stationNames) {
        ArrayList<String> endStationItems = new ArrayList<>();
        endStationItems.add("Select End Station"); // Custom hint for end station
        endStationItems.addAll(stationNames);

        ArrayAdapter<String> endAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                endStationItems
        );
        endAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        runOnUiThread(() -> {
            endStationSpinner.setAdapter(endAdapter);
            endStationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0) {
                        String selected = parent.getItemAtPosition(position).toString();
                        Log.d(TAG, "End station selected: " + selected);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        });
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    updateSelectedDate();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void updateSelectedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        selectedDate = sdf.format(calendar.getTime());
        travelDateButton.setText(selectedDate);
    }

    private void searchTrains() {
        // Get selected stations
        String startStation = startStationSpinner.getSelectedItem().toString();
        String endStation = endStationSpinner.getSelectedItem().toString();

        // Validate selections
        if (startStation.equals("Select Start Station") || endStation.equals("Select End Station")) {
            showError("Please select both start and end stations");
            return;
        }

        if (selectedDate.isEmpty()) {
            showError("Please select a travel date");
            return;
        }

        if (startStation.equals(endStation)) {
            showError("Start and end stations cannot be the same");
            return;
        }

        // Log search details
        Log.d(TAG, "Search Details - Start: " + startStation + ", End: " + endStation + ", Date: " + selectedDate);

        // Pass search details to the next activity
        Intent intent = new Intent(this, TrainSchedulesSearchResultsActivity.class);
        intent.putExtra("startStation", startStation);
        intent.putExtra("endStation", endStation);
        intent.putExtra("travelDate", selectedDate);
        startActivity(intent);
    }

    private void showError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }
}