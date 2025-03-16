package com.example.train_booking_scheduling_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class BookingActivity extends AppCompatActivity {
    private AutoCompleteTextView fromSpinner, toSpinner;
    private EditText passengerCountEditText;
    private TextView ticketPriceText;
    private MaterialButton confirmBookingButton;
    private FirebaseFirestore db;
    private List<String> stations = new ArrayList<>();
    private String selectedFromStation = "";
    private String selectedToStation = "";
    private int ticketPrice = 0;
    private int numberOfPassengers = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize views
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        passengerCountEditText = findViewById(R.id.passengerCountEditText);
        ticketPriceText = findViewById(R.id.ticketPriceText);
        confirmBookingButton = findViewById(R.id.confirmBookingButton);
        Button backButton = findViewById(R.id.backButton);

        // Load stations from Firebase
        loadStations();

        // Set up listeners
        setupSpinnerListeners();
        setupPassengerCountListener();
        setupConfirmButton();

        // Back button
        backButton.setOnClickListener(view -> onBackPressed());
    }

    private void loadStations() {
        db.collection("stations")
                .document("station_list")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> stationNames = (List<String>) documentSnapshot.get("name");
                        if (stationNames != null) {
                            stations.addAll(stationNames);
                            setupSpinnerAdapters();
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(BookingActivity.this, "Error loading stations", Toast.LENGTH_SHORT).show()
                );
    }

    private void setupSpinnerAdapters() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                stations
        );

        fromSpinner.setAdapter(adapter);
        toSpinner.setAdapter(adapter);
    }

    private void setupSpinnerListeners() {
        fromSpinner.setOnItemClickListener((parent, view, position, id) -> {
            selectedFromStation = parent.getItemAtPosition(position).toString();
            calculateTicketPrice();
        });

        toSpinner.setOnItemClickListener((parent, view, position, id) -> {
            selectedToStation = parent.getItemAtPosition(position).toString();
            calculateTicketPrice();
        });
    }

    private void setupPassengerCountListener() {
        passengerCountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    numberOfPassengers = Integer.parseInt(s.toString());
                    calculateTicketPrice();
                } else {
                    numberOfPassengers = 0;
                    updatePriceDisplay(0);
                }
            }
        });
    }

    private void calculateTicketPrice() {
        if (!selectedFromStation.isEmpty() && !selectedToStation.isEmpty()) {
            db.collection("Ticket_Prices")
                    .whereEqualTo("startStation", selectedFromStation)
                    .whereEqualTo("endStation", selectedToStation)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            ticketPrice = document.getLong("price").intValue();
                            int totalPrice = ticketPrice * numberOfPassengers;
                            updatePriceDisplay(totalPrice);
                            break;
                        }
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(BookingActivity.this, "Error calculating price", Toast.LENGTH_SHORT).show()
                    );
        }
    }

    private void updatePriceDisplay(int totalPrice) {
        ticketPriceText.setText(String.format("Ticket Price: %d LKR", totalPrice));
    }

    private void setupConfirmButton() {
        confirmBookingButton.setOnClickListener(v -> {
            if (validateBooking()) {
                // Creating Intent to navigate to BookingDetailsActivity
                Intent intent = new Intent(BookingActivity.this, BookingDetailsActivity.class);

                // Passing booking details via Intent
                intent.putExtra("fromStation", selectedFromStation);
                intent.putExtra("toStation", selectedToStation);
                intent.putExtra("passengers", numberOfPassengers);
                intent.putExtra("totalFare", ticketPrice * numberOfPassengers);

                // Start BookingDetailsActivity
                startActivity(intent);
            }
        });
    }

    private boolean validateBooking() {
        if (selectedFromStation.isEmpty() || selectedToStation.isEmpty()) {
            Toast.makeText(this, "Please select both stations", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedFromStation.equals(selectedToStation)) {
            Toast.makeText(this, "Start and end stations cannot be the same", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (numberOfPassengers <= 0) {
            Toast.makeText(this, "Please enter number of passengers", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (ticketPrice <= 0) {
            Toast.makeText(this, "Invalid ticket price", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}