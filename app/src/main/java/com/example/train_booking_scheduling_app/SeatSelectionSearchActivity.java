package com.example.train_booking_scheduling_app;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class SeatSelectionSearchActivity extends AppCompatActivity {
    private Spinner startStationSpinner, endStationSpinner;
    private Button travelDateButton, searchButton, backButton;
    private Calendar calendar;
    private String selectedDate = "";
    private FirebaseFirestore db;
    private static final String TAG = "SeatSelectionSearch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection_search);

        db = FirebaseFirestore.getInstance();
        initializeViews();
        calendar = Calendar.getInstance();
        loadStations();
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
        travelDateButton.setOnClickListener(view -> showDatePicker());
        searchButton.setOnClickListener(view -> searchTrains());
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
                        setupSpinner(startStationSpinner, stationNames, "Select Start Station");
                        setupSpinner(endStationSpinner, stationNames, "Select End Station");
                    }
                }
            }
        });
    }

    private void setupSpinner(Spinner spinner, List<String> stationNames, String hint) {
        ArrayList<String> items = new ArrayList<>();
        items.add(hint);
        items.addAll(stationNames);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime());
            travelDateButton.setText(selectedDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void searchTrains() {
        String startStation = startStationSpinner.getSelectedItem().toString();
        String endStation = endStationSpinner.getSelectedItem().toString();

        if (startStation.contains("Select") || endStation.contains("Select") || selectedDate.isEmpty()) {
            showError("Please select all options");
            return;
        }

        Intent intent = new Intent(this, SeatSelectionResultsActivity.class);
        intent.putExtra("startStation", startStation);
        intent.putExtra("endStation", endStation);
        intent.putExtra("travelDate", selectedDate);
        startActivity(intent);
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}