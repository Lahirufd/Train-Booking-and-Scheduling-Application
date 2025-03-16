package com.example.train_booking_scheduling_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CardPaymentActivity extends AppCompatActivity {

    private TextView totalAmountText;
    private EditText cardNumber, expiryDate, cvv, cardHolderName;
    private Button payButton;
    private FirebaseFirestore db;  // Firestore instance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_payment);

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        totalAmountText = findViewById(R.id.totalAmountText);
        cardNumber = findViewById(R.id.cardNumber);
        expiryDate = findViewById(R.id.expiryDate);
        cvv = findViewById(R.id.cvv);
        cardHolderName = findViewById(R.id.cardHolderName);
        payButton = findViewById(R.id.payButton);

        // Get booking details from intent
        Intent intent = getIntent();
        String fromStation = intent.getStringExtra("fromStation");
        String toStation = intent.getStringExtra("toStation");
        int passengers = intent.getIntExtra("passengers", 0);
        int totalAmount = intent.getIntExtra("totalFare", 0);

        totalAmountText.setText("Total Amount: Rs " + totalAmount);

        // Handle pay button click with confirmation dialog
        payButton.setOnClickListener(v -> {
            showConfirmationDialog(fromStation, toStation, passengers, totalAmount);
        });
    }

    // Method to show confirmation dialog
    private void showConfirmationDialog(String fromStation, String toStation, int passengers, int totalFare) {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Payment")
                .setMessage("Are you sure you want to proceed with the payment?")
                .setPositiveButton("Confirm", (dialog, which) -> {
                    saveTicketToFirestore(fromStation, toStation, passengers, totalFare); // Proceed with payment
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss(); // Close dialog
                })
                .show();
    }

    // Method to save ticket to Firestore
    private void saveTicketToFirestore(String fromStation, String toStation, int passengers, int totalFare) {
        String ticketNumber = "A00" + (int) (Math.random() * 1000); // Generate unique ticket number

        // Create ticket data
        Map<String, Object> ticket = new HashMap<>();
        ticket.put("startStation", fromStation);
        ticket.put("endStation", toStation);
        ticket.put("price", totalFare);
        ticket.put("ticketNumber", ticketNumber);
        ticket.put("passengers", passengers);

        // Save ticket to Firestore
        db.collection("Tickets").add(ticket)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(CardPaymentActivity.this, "Payment Successful! Ticket saved.", Toast.LENGTH_SHORT).show();

                    // Navigate to TicketDownloadActivity
                    Intent ticketIntent = new Intent(CardPaymentActivity.this, TicketDownloadActivity.class);
                    ticketIntent.putExtra("fromStation", fromStation);
                    ticketIntent.putExtra("toStation", toStation);
                    ticketIntent.putExtra("passengers", passengers);
                    ticketIntent.putExtra("totalFare", totalFare);
                    ticketIntent.putExtra("ticketNumber", ticketNumber);
                    startActivity(ticketIntent);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CardPaymentActivity.this, "Error saving ticket. Try again.", Toast.LENGTH_SHORT).show();
                });
    }
}
