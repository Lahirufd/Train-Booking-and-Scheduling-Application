package com.example.train_booking_scheduling_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {
    private EditText nameField, emailField, telephoneField, addressField, passwordField;
    private Button signupButton;
    private TextView loginLink;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI components
        nameField = findViewById(R.id.nameField);
        emailField = findViewById(R.id.signupEmailField);
        telephoneField = findViewById(R.id.telephoneField);
        addressField = findViewById(R.id.addressField);
        passwordField = findViewById(R.id.signupPasswordField);
        signupButton = findViewById(R.id.signupButton);
        loginLink = findViewById(R.id.loginLink);

        // Firebase Initialization
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Signup Button Logic
        signupButton.setOnClickListener(view -> {
            String name = nameField.getText().toString().trim();
            String email = emailField.getText().toString().trim();
            String telephone = telephoneField.getText().toString().trim();
            String address = addressField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            // Validate fields
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(telephone) ||
                    TextUtils.isEmpty(address) || TextUtils.isEmpty(password)) {
                Toast.makeText(SignupActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Store user in Firebase Database
            String userId = databaseReference.push().getKey();
            User user = new User(name, email, telephone, address, password); // Storing password too

            databaseReference.child(userId).setValue(user).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(SignupActivity.this, "Signup Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    finish(); // Close SignupActivity
                } else {
                    Toast.makeText(SignupActivity.this, "Database Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Redirect to login page
        loginLink.setOnClickListener(view -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
        });
    }
}