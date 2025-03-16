package com.example.train_booking_scheduling_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SeatBookingSummaryActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private TextView trainNameTextView, trainNumberTextView, ticketNumberTextView, startStationTextView, endStationTextView, numSeatsTextView;
    private TextView dateTextView, arrivalTextView, departureTextView;
    private Button confirmButton, backButton;

    private int trainNumber, numSeats;
    private String trainName, ticketNumber, startStation, endStation, date, arrival, departure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_booking_summary);

        db = FirebaseFirestore.getInstance();

        // Initialize UI components
        trainNameTextView = findViewById(R.id.trainNameTextView);
        trainNumberTextView = findViewById(R.id.trainNumberTextView);
        ticketNumberTextView = findViewById(R.id.ticketNumberTextView);
        startStationTextView = findViewById(R.id.startStationTextView);
        endStationTextView = findViewById(R.id.endStationTextView);
        numSeatsTextView = findViewById(R.id.numSeatsEditText);
        dateTextView = findViewById(R.id.dateTextView);
        arrivalTextView = findViewById(R.id.arrivalTextView);
        departureTextView = findViewById(R.id.departureTextView);
        confirmButton = findViewById(R.id.confirmButton);
        backButton = findViewById(R.id.backButton);

        // Get booking details from intent
        Intent intent = getIntent();
        trainNumber = intent.getIntExtra("trainNumber", -1);
        trainName = intent.getStringExtra("trainName");
        ticketNumber = intent.getStringExtra("ticketNumber");
        startStation = intent.getStringExtra("startStation");
        endStation = intent.getStringExtra("endStation");
        numSeats = intent.getIntExtra("numSeats", 0);

        // Fetch date, arrival, and departure from Firestore
        fetchTrainDetails();

        // Back button
        backButton.setOnClickListener(view -> onBackPressed());

        // Confirm button
        confirmButton.setOnClickListener(view -> showConfirmationDialog());
    }

    private void fetchTrainDetails() {
        db.collection("Train").whereEqualTo("number", trainNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Get date, arrival, and departure from Firestore
                            date = document.getString("date");
                            arrival = document.getString("arrival");
                            departure = document.getString("departure");

                            // Update UI with fetched data
                            trainNameTextView.setText("Train Name: " + trainName);
                            trainNumberTextView.setText("Train Number: " + trainNumber);
                            ticketNumberTextView.setText("Ticket Number: " + ticketNumber);
                            startStationTextView.setText(startStation);
                            endStationTextView.setText(endStation);
                            numSeatsTextView.setText("Number of Seats: " + numSeats);
                            dateTextView.setText("Date: " + date);
                            arrivalTextView.setText("Arrival: " + arrival);
                            departureTextView.setText("Departure: " + departure);
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch train details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Booking")
                .setMessage("Are you sure you want to book these seats?")
                .setPositiveButton("Yes", (dialog, which) -> updateBookingStatus())
                .setNegativeButton("No", null)
                .show();
    }

    private void updateBookingStatus() {
        // Create a map for the seat booking details
        Map<String, Object> seatBooking = new HashMap<>();
        seatBooking.put("trainNumber", trainNumber);
        seatBooking.put("trainName", trainName);
        seatBooking.put("ticketNumber", ticketNumber);
        seatBooking.put("startStation", startStation);
        seatBooking.put("endStation", endStation);
        seatBooking.put("numSeats", numSeats);
        seatBooking.put("date", date);
        seatBooking.put("arrival", arrival);
        seatBooking.put("departure", departure);

        // Use the ticketNumber as the document ID to avoid duplicate bookings
        db.collection("Seats").document(ticketNumber)
                .set(seatBooking)
                .addOnSuccessListener(documentReference -> {
                    // Update availableSeats in the Train collection
                    updateAvailableSeats(trainNumber, numSeats);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to confirm booking", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateAvailableSeats(int trainNumber, int numSeats) {
        // Fetch the current availableSeats value
        db.collection("Train").whereEqualTo("number", trainNumber)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            // Get the current availableSeats value
                            int currentAvailableSeats = document.getLong("availableSeats").intValue();

                            // Calculate the new availableSeats value
                            int newAvailableSeats = currentAvailableSeats - numSeats;

                            // Ensure availableSeats does not go below 0
                            if (newAvailableSeats < 0) {
                                Toast.makeText(this, "Not enough seats available", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // Update the availableSeats field in Firestore
                            db.collection("Train").document(document.getId())
                                    .update("availableSeats", newAvailableSeats)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Booking Confirmed!", Toast.LENGTH_SHORT).show();
                                        goToPDFDownloadPage();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Failed to update available seats", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Toast.makeText(this, "Failed to fetch train details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void goToPDFDownloadPage() {
        Intent intent = new Intent(this, SeatBookingDownloadActivity.class);
        // Pass all required data to the next activity
        intent.putExtra("trainNumber", String.valueOf(trainNumber));
        intent.putExtra("trainName", trainName);
        intent.putExtra("ticketNumber", ticketNumber);
        intent.putExtra("startStation", startStation);
        intent.putExtra("endStation", endStation);
        intent.putExtra("numSeats", String.valueOf(numSeats));
        intent.putExtra("date", date);
        intent.putExtra("arrival", arrival);
        intent.putExtra("departure", departure);
        startActivity(intent);
        finish();
    }
}