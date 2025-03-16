package com.example.train_booking_scheduling_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TicketDownloadActivity extends AppCompatActivity {

    private TextView textFrom, textTo, textPassengerCount, totalAmount, textPaymentStatus;
    private Button downloadTicketButton, backToHomeButton;

    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_download);

        // Initialize UI elements
        textFrom = findViewById(R.id.textFrom);
        textTo = findViewById(R.id.textTo);
        textPassengerCount = findViewById(R.id.textPassengerCount);
        totalAmount = findViewById(R.id.totalAmount);
        textPaymentStatus = findViewById(R.id.textPaymentStatus);
        downloadTicketButton = findViewById(R.id.downloadTicketButton);
        backToHomeButton = findViewById(R.id.backToHomeButton);

        // Get ticket details from Intent
        String fromStation = getIntent().getStringExtra("fromStation");
        String toStation = getIntent().getStringExtra("toStation");
        int passengers = getIntent().getIntExtra("passengers", 0);
        int fare = getIntent().getIntExtra("totalFare", 0);
        String ticketNumber = getIntent().getStringExtra("ticketNumber");

        // Set text values
        textFrom.setText("From: " + fromStation);
        textTo.setText("To: " + toStation);
        textPassengerCount.setText("Passengers: " + passengers);
        totalAmount.setText("Total Amount: Rs " + fare);
        textPaymentStatus.setText("Payment Successful! 🎉");

        // Check and request storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }

        // Download Ticket button click event
        downloadTicketButton.setOnClickListener(v -> generatePDF(fromStation, toStation, passengers, fare, ticketNumber));

        // Back to Home button click event
        backToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(TicketDownloadActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Optional: Call finish() if you want to close the current activity
        });
    }

    // Generate PDF ticket and save it
    private void generatePDF(String from, String to, int passengers, int fare, String ticketNumber) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        // Create a new page
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(400, 600, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Draw title
        paint.setTextSize(18);
        paint.setColor(Color.BLACK);
        canvas.drawText("Train Booking Ticket", 120, 50, paint);

        // Draw ticket details
        paint.setTextSize(14);
        canvas.drawText("Ticket No: " + ticketNumber, 50, 100, paint);
        canvas.drawText("From: " + from, 50, 150, paint);
        canvas.drawText("To: " + to, 50, 200, paint);
        canvas.drawText("Passengers: " + passengers, 50, 250, paint);
        canvas.drawText("Total Fare: Rs " + fare, 50, 300, paint);

        // Draw footer
        paint.setTextSize(12);
        paint.setColor(Color.BLUE);
        canvas.drawText("Safe Travels!", 150, 400, paint);

        // Finish the page
        pdfDocument.finishPage(page);

        // Save the PDF file
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, "TrainTicket_" + ticketNumber + ".pdf");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();

            Toast.makeText(this, "Ticket downloaded: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to download ticket.", Toast.LENGTH_SHORT).show();
        }
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}