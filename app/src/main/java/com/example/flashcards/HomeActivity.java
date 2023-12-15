package com.example.flashcards;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcards.models.Category;
import com.example.flashcards.utils.SharedPreferencesHelper;

public class HomeActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private Button enterButton;
    private CategoryManager categoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        categorySpinner = findViewById(R.id.homeCategorySpinner);
        enterButton = findViewById(R.id.enterButton);

        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this);
        categoryManager = new CategoryManager(sharedPreferencesHelper);

        setupCategorySpinner();
        setupEnterButton();
    }

    private void setupCategorySpinner() {
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryManager.getCategories());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void setupEnterButton() {
        enterButton.setOnClickListener(v -> {
            Category selectedCategory = (Category) categorySpinner.getSelectedItem();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.putExtra("SELECTED_CATEGORY_ID", selectedCategory.getId());
            startActivity(intent);
        });
    }

}
