package com.example.train_booking_scheduling_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
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

public class SeatBookingDownloadActivity extends AppCompatActivity {

    private TextView textTrainName, textTrainNumber, textFromTo, textSeatCount;
    private Button downloadSeatBookingButton, backToHomeButton;
    private static final int STORAGE_PERMISSION_CODE = 100;

    private String trainNumber, trainName, ticketNumber, startStation, endStation, numSeats, date, arrival, departure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_booking_download);

        // Initialize UI elements
        textTrainName = findViewById(R.id.textTrainName);
        textTrainNumber = findViewById(R.id.textTrainNumber);
        textFromTo = findViewById(R.id.textFromTo);
        textSeatCount = findViewById(R.id.textSeatCount);
        downloadSeatBookingButton = findViewById(R.id.downloadSeatBookingButton);
        backToHomeButton = findViewById(R.id.backToHomeButton);

        // Get seat booking details from Intent
        trainNumber = getIntent().getStringExtra("trainNumber");
        trainName = getIntent().getStringExtra("trainName");
        ticketNumber = getIntent().getStringExtra("ticketNumber");
        startStation = getIntent().getStringExtra("startStation");
        endStation = getIntent().getStringExtra("endStation");
        numSeats = getIntent().getStringExtra("numSeats");
        date = getIntent().getStringExtra("date");
        arrival = getIntent().getStringExtra("arrival");
        departure = getIntent().getStringExtra("departure");

        // Set text values
        textTrainName.setText("Train Name: " + trainName);
        textTrainNumber.setText("Train Number: " + trainNumber);
        textFromTo.setText("From: " + startStation + " → To: " + endStation);
        textSeatCount.setText("Seats: " + numSeats);

        // Check and request storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }

        // Download Seat Booking button click event
        downloadSeatBookingButton.setOnClickListener(v -> generatePDF());

        // Back to Home button click event
        backToHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(SeatBookingDownloadActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Generate PDF seat booking confirmation
    private void generatePDF() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(500, 700, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Draw title
        paint.setTextSize(20);
        paint.setColor(Color.BLACK);
        canvas.drawText("Train Seat Booking Confirmation", 100, 50, paint);

        // Draw seat booking details
        paint.setTextSize(16);
        int y = 100;
        canvas.drawText("Train Number: " + trainNumber, 50, y, paint);
        y += 40;
        canvas.drawText("Train Name: " + trainName, 50, y, paint);
        y += 40;
        canvas.drawText("Ticket Number: " + ticketNumber, 50, y, paint);
        y += 40;
        canvas.drawText("From: " + startStation, 50, y, paint);
        y += 40;
        canvas.drawText("To: " + endStation, 50, y, paint);
        y += 40;
        canvas.drawText("Number of Seats: " + numSeats, 50, y, paint);
        y += 40;
        canvas.drawText("Date: " + date, 50, y, paint);
        y += 40;
        canvas.drawText("Arrival Time: " + arrival, 50, y, paint);
        y += 40;
        canvas.drawText("Departure Time: " + departure, 50, y, paint);

        // Draw footer
        paint.setTextSize(14);
        paint.setColor(Color.BLUE);
        canvas.drawText("Thank you for choosing our service!", 120, 600, paint);

        pdfDocument.finishPage(page);

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsDir, "TrainTicket_" + ticketNumber + ".pdf");

        try {
            FileOutputStream fos = new FileOutputStream(file);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();

            Toast.makeText(this, "PDF Downloaded: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to download confirmation.", Toast.LENGTH_SHORT).show();
        }
    }

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
