package com.example.train_booking_scheduling_app;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class SearchFoundItemActivity extends AppCompatActivity {

    private EditText itemNameEditText;
    private Button searchButton;
    private LinearLayout searchResultsLayout;
    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search_found_item);

        db = FirebaseFirestore.getInstance();

        itemNameEditText = findViewById(R.id.editTextText);
        searchButton = findViewById(R.id.button6);
        searchResultsLayout = findViewById(R.id.searchResultsLayout);

        Button backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());

        searchButton.setOnClickListener(v -> searchFoundItem());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void searchFoundItem() {
        String searchItemName = itemNameEditText.getText().toString().trim();
        searchResultsLayout.removeAllViews();

        db.collection("items")
                .whereEqualTo("itemType", "Found Item")
                .whereEqualTo("itemName", searchItemName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean itemFound = false;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                itemFound = true;
                                addResultCard(
                                        document.getString("itemName"),
                                        document.getString("description"),
                                        document.getString("date"),
                                        document.getString("contactNo")
                                );
                            }

                            if (!itemFound) {
                                Toast.makeText(SearchFoundItemActivity.this,
                                        "No Found Item found with this name",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SearchFoundItemActivity.this,
                                    "Error searching for item",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void addResultCard(String itemName, String description, String date, String contactNo) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.item_result_card, searchResultsLayout, false);

        TextView itemNameTextView = cardView.findViewById(R.id.itemName);
        TextView descriptionTextView = cardView.findViewById(R.id.description);
        TextView dateTextView = cardView.findViewById(R.id.date);
        TextView contactNoTextView = cardView.findViewById(R.id.contactNo);

        itemNameTextView.setText("Item Name: " + itemName);
        descriptionTextView.setText("Description: " + description);
        dateTextView.setText("Date: " + date);
        contactNoTextView.setText("Contact Number: " + contactNo);

        searchResultsLayout.addView(cardView);
    }
}