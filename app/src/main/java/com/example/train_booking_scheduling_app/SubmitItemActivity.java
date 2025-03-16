package com.example.train_booking_scheduling_app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class SubmitItemActivity extends AppCompatActivity {
    private EditText itemNameEditText, descriptionEditText, dateEditText, contactEditText;
    private AutoCompleteTextView itemTypeDropdown;
    private Button submitButton, backButton;
    private FirebaseFirestore db;
    private String selectedItemType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_item);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();

        // Setup Dropdown
        setupItemTypeDropdown();

        // Setup Date Picker
        setupDatePicker();

        // Setup Submit Button
        setupSubmitButton();

        // Handle Window Insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initializeViews() {
        itemNameEditText = findViewById(R.id.itemNameInput);
        descriptionEditText = findViewById(R.id.descriptionInput);
        dateEditText = findViewById(R.id.dateInput);
        contactEditText = findViewById(R.id.contactInput);
        itemTypeDropdown = findViewById(R.id.itemTypeDropdown);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.backButton);
    }

    private void setupItemTypeDropdown() {
        String[] itemTypes = {"Lost Item", "Found Item"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                itemTypes
        );

        itemTypeDropdown.setAdapter(adapter);

        itemTypeDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedItemType = itemTypes[position]; // Save the selected item type
        });
    }

    private void setupDatePicker() {
        dateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    SubmitItemActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        String selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, year1);
                        dateEditText.setText(selectedDate);
                    },
                    year,
                    month,
                    day
            );
            datePickerDialog.show();
        });
    }

    private void setupSubmitButton() {
        submitButton.setOnClickListener(v -> {
            if (validateInputs()) {
                // Prepare data for Firebase
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("itemName", itemNameEditText.getText().toString());
                itemData.put("itemType", selectedItemType);
                itemData.put("description", descriptionEditText.getText().toString());
                itemData.put("date", dateEditText.getText().toString());
                itemData.put("contactNo", contactEditText.getText().toString());

                // Save to Firestore
                db.collection("items")
                        .add(itemData)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(SubmitItemActivity.this,
                                    "Item submitted successfully!",
                                    Toast.LENGTH_SHORT).show();
                            clearInputs();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SubmitItemActivity.this,
                                    "Error submitting item: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
            }
        });

        // Setup Back Button
        backButton.setOnClickListener(v -> finish());
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (itemNameEditText.getText().toString().trim().isEmpty()) {
            itemNameEditText.setError("Item name is required");
            isValid = false;
        }

        if (selectedItemType.isEmpty()) {
            itemTypeDropdown.setError("Please select an item type");
            isValid = false;
        }

        if (contactEditText.getText().toString().trim().isEmpty()) {
            contactEditText.setError("Contact number is required");
            isValid = false;
        }

        if (dateEditText.getText().toString().trim().isEmpty()) {
            dateEditText.setError("Date is required");
            isValid = false;
        }

        return isValid;
    }

    private void clearInputs() {
        itemNameEditText.setText("");
        descriptionEditText.setText("");
        dateEditText.setText("");
        contactEditText.setText("");
        itemTypeDropdown.setText("");
        selectedItemType = "";
    }
}