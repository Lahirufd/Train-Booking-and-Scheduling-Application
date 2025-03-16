package com.example.train_booking_scheduling_app;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class SeatBooking {
    private int trainNumber, numSeats;
    private String ticketNumber, startStation, endStation;

    // No-argument constructor required for Firestore
    public SeatBooking() {}

    public SeatBooking(int trainNumber, String ticketNumber, int numSeats, String startStation, String endStation) {
        this.trainNumber = trainNumber;
        this.ticketNumber = ticketNumber;
        this.numSeats = numSeats;
        this.startStation = startStation;
        this.endStation = endStation;
    }

    // Getters and setters for Firestore serialization
    public int getTrainNumber() { return trainNumber; }
    public void setTrainNumber(int trainNumber) { this.trainNumber = trainNumber; }

    public int getNumSeats() { return numSeats; }
    public void setNumSeats(int numSeats) { this.numSeats = numSeats; }

    public String getTicketNumber() { return ticketNumber; }
    public void setTicketNumber(String ticketNumber) { this.ticketNumber = ticketNumber; }

    public String getStartStation() { return startStation; }
    public void setStartStation(String startStation) { this.startStation = startStation; }

    public String getEndStation() { return endStation; }
    public void setEndStation(String endStation) { this.endStation = endStation; }
}


