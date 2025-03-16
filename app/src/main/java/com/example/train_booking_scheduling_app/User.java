package com.example.train_booking_scheduling_app;

public class User {
    private String name, email, telephone, address, password;

    public User() {
        // Default constructor required for Firebase
    }

    public User(String name, String email, String telephone, String address, String password) {
        this.name = name;
        this.email = email;
        this.telephone = telephone;
        this.address = address;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getAddress() {
        return address;
    }

    public String getPassword() {
        return password;
    }
}