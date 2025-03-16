package com.example.train_booking_scheduling_app;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find all CardView tiles
        CardView scheduleCard = findViewById(R.id.scheduleCard);
        CardView bookingCard = findViewById(R.id.bookingCard);
        CardView lostFoundCard = findViewById(R.id.lostFoundCard);
        CardView seatSelectionCard = findViewById(R.id.seatSelectionCard);

        // Set up click listeners for each tile using lambda expressions
        scheduleCard.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, TrainSchedulesActivity.class);
            startActivity(intent);
        });

        bookingCard.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, BookingActivity.class);
            startActivity(intent);
        });

        lostFoundCard.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LostAndFoundActivity.class);
            startActivity(intent);
        });

        seatSelectionCard.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SeatSelectionSearchActivity.class);
            startActivity(intent);
        });

        // Find the logout button
        Button logoutButton = findViewById(R.id.logoutButton);

        // Set up click listener for the logout button
        logoutButton.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Optional: Call finish() to close the current activity
        });
    }
}