package com.example.train_booking_scheduling_app;

public class Train {
    private String name;
    private int number;
    private String departure;
    private String arrival;
    private String startStation;
    private String endStation;
    private String date;
    private int availableSeats;

    // Default constructor (required for Firestore)
    public Train() {
    }

    // Parameterized constructor
    public Train(String name, int number, String departure, String arrival, String startStation, String endStation, String date, int availableSeats) {
        this.name = name;
        this.number = number;
        this.departure = departure;
        this.arrival = arrival;
        this.startStation = startStation;
        this.endStation = endStation;
        this.date = date;
        this.availableSeats = availableSeats;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}