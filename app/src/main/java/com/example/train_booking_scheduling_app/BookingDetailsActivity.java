package com.example.train_booking_scheduling_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class BookingDetailsActivity extends AppCompatActivity {

    private TextView textFrom, textTo, textPassengerCount, totalAmount;
    private Button confirmBookingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        // Initialize UI elements
        textFrom = findViewById(R.id.textFrom);
        textTo = findViewById(R.id.textTo);
        textPassengerCount = findViewById(R.id.textPassengerCount);
        totalAmount = findViewById(R.id.totalAmount);
        confirmBookingButton = findViewById(R.id.confirmBookingButton);
        Button backButton = findViewById(R.id.backButton);

        // Retrieve booking details from Intent
        Intent intent = getIntent();
        String fromStation = intent.getStringExtra("fromStation");
        String toStation = intent.getStringExtra("toStation");
        int passengers = intent.getIntExtra("passengers", 0);
        int totalFare = intent.getIntExtra("totalFare", 0);

        // Display the booking details
        textFrom.setText("From: " + fromStation);
        textTo.setText("To: " + toStation);
        textPassengerCount.setText("Passengers: " + passengers);
        totalAmount.setText("Total Amount: " + totalFare + " LKR");

        // Handle confirm booking button
        confirmBookingButton.setOnClickListener(v -> {
            // Navigate to the payment activity
            Intent paymentIntent = new Intent(BookingDetailsActivity.this, CardPaymentActivity.class);
            paymentIntent.putExtra("fromStation", fromStation);
            paymentIntent.putExtra("toStation", toStation);
            paymentIntent.putExtra("passengers", passengers);
            paymentIntent.putExtra("totalFare", totalFare);
            startActivity(paymentIntent);
        });

        // Back button
        backButton.setOnClickListener(view -> onBackPressed());
    }
}