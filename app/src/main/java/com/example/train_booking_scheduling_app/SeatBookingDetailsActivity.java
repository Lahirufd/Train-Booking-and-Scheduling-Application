package com.example.train_booking_scheduling_app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SeatBookingDetailsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private EditText ticketNumberEditText, numSeatsEditText;
    private TextView trainNameTextView, trainNumberTextView, startStationTextView, endStationTextView, availableSeatsTextView;
    private Button nextButton;

    private int trainNumber;
    private String trainName, startStation, endStation;
    private int availableSeats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_booking_details);

        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        ticketNumberEditText = findViewById(R.id.ticketNumberEditText);
        numSeatsEditText = findViewById(R.id.numSeatsEditText);
        trainNameTextView = findViewById(R.id.trainNameTextView);
        trainNumberTextView = findViewById(R.id.trainNumberTextView);
        startStationTextView = findViewById(R.id.startStationTextView);
        endStationTextView = findViewById(R.id.endStationTextView);
        availableSeatsTextView = findViewById(R.id.availableSeatsTextView);
        nextButton = findViewById(R.id.submitButton);
        Button backButton = findViewById(R.id.backButton);

        // Get train details from intent
        Intent intent = getIntent();
        trainNumber = intent.getIntExtra("trainNumber", -1);
        trainName = intent.getStringExtra("trainName");
        startStation = intent.getStringExtra("startStation");
        endStation = intent.getStringExtra("endStation");
        availableSeats = intent.getIntExtra("availableSeats", 0);

        // Set train details
        trainNameTextView.setText("Train Name: " + trainName);
        trainNumberTextView.setText("Train Number: " + trainNumber);
        startStationTextView.setText("Start Station: " + startStation);
        endStationTextView.setText("End Station: " + endStation);
        availableSeatsTextView.setText("Available Seats: " + availableSeats);

        Log.d("SeatBooking", "Available seats received: " + availableSeats);

        nextButton.setOnClickListener(view -> validateAndProceed());

        // Back button
        backButton.setOnClickListener(view -> onBackPressed());
    }

    private void validateAndProceed() {
        String ticketNumber = ticketNumberEditText.getText().toString().trim();
        String numSeatsStr = numSeatsEditText.getText().toString().trim();

        // Validation: Check if input fields are empty
        if (ticketNumber.isEmpty() || numSeatsStr.isEmpty()) {
            Toast.makeText(this, "Please enter all details", Toast.LENGTH_SHORT).show();
            return;
        }

        int numSeats = Integer.parseInt(numSeatsStr);

        // Validation: Check if entered seats are valid
        if (numSeats <= 0) {
            Toast.makeText(this, "Enter a valid number of seats", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validation: Check if entered seats exceed available seats
        if (numSeats > availableSeats) {
            Toast.makeText(this, "Not enough seats available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the ticket number exists in Firestore's Tickets collection
        db.collection("Tickets").whereEqualTo("ticketNumber", ticketNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String ticketStart = document.getString("startStation");
                            String ticketEnd = document.getString("endStation");
                            int ticketPassengers = document.getLong("passengers").intValue();

                            // Validation: Ensure ticket route matches the train route
                            if (!startStation.equals(ticketStart) || !endStation.equals(ticketEnd)) {
                                Toast.makeText(this, "Ticket route does not match train route", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Validation: Ensure ticket allows booking that many seats
                            if (numSeats > ticketPassengers) {
                                Toast.makeText(this, "Cannot book more seats than allowed on ticket", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Check if the ticket number is already used for booking in the Seats collection
                            db.collection("Seats").whereEqualTo("ticketNumber", ticketNumber)
                                    .get()
                                    .addOnCompleteListener(seatTask -> {
                                        if (seatTask.isSuccessful()) {
                                            if (!seatTask.getResult().isEmpty()) {
                                                // Ticket number is already used for booking
                                                Toast.makeText(this, "Ticket number already used for booking", Toast.LENGTH_SHORT).show();
                                            } else {
                                                // Ticket number is valid and not used for booking
                                                // Pass validated data to the summary page
                                                Intent intent = new Intent(this, SeatBookingSummaryActivity.class);
                                                intent.putExtra("trainNumber", trainNumber);
                                                intent.putExtra("trainName", trainName);
                                                intent.putExtra("ticketNumber", ticketNumber);
                                                intent.putExtra("startStation", startStation);
                                                intent.putExtra("endStation", endStation);
                                                intent.putExtra("numSeats", numSeats);
                                                startActivity(intent);
                                            }
                                        } else {
                                            Toast.makeText(this, "Failed to check ticket booking status", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Invalid ticket number", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}