package com.example.flashcards;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.flashcards.models.Category;
import com.example.flashcards.utils.SharedPreferencesHelper;

import java.util.UUID;

public class HomeActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private Button enterButton;
    private CategoryManager categoryManager;
    private FlashcardManager flashcardManager;
    private Button addCategoryButton;
    private Button editCategoryButton;
    private Button deleteCategoryButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        categorySpinner = findViewById(R.id.homeCategorySpinner);
        enterButton = findViewById(R.id.enterButton);
        addCategoryButton = findViewById(R.id.addCategoryButton);
        editCategoryButton = findViewById(R.id.editCategoryButton);
        deleteCategoryButton = findViewById(R.id.deleteCategoryButton);



        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this);
        categoryManager = new CategoryManager(sharedPreferencesHelper);
        flashcardManager = new FlashcardManager(sharedPreferencesHelper);

        setupCategoryButtons();
        setupCategorySpinner();
        setupEnterButton();
    }

    private void setupCategoryButtons() {
        addCategoryButton.setOnClickListener(v -> addCategory());
        editCategoryButton.setOnClickListener(v -> editCategory());
        deleteCategoryButton.setOnClickListener(v -> deleteCategory());
    }

    private void deleteCategory() {
        Category selectedCategory = (Category) categorySpinner.getSelectedItem();
        if (selectedCategory == null) {
            Toast.makeText(this, "No category selected", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoryName = selectedCategory.getName();
        new AlertDialog.Builder(this)
                .setTitle("Delete Category")
                .setMessage("Are you sure you want to delete the category '" + categoryName + "' and all its flashcards?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // Delete all flashcards in the category
                    flashcardManager.deleteFlashcardsByCategory(selectedCategory.getId());

                    // Delete the category
                    categoryManager.deleteCategory(categoryManager.getCategories().indexOf(selectedCategory));
                    updateCategorySpinner();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }




    private void addCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Category");

        View customView = getLayoutInflater().inflate(R.layout.add_category_dialog, null);
        EditText categoryNameEditText = customView.findViewById(R.id.categoryName);

        builder.setView(customView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String categoryName = categoryNameEditText.getText().toString();
            Category newCategory = new Category(UUID.randomUUID().toString(), categoryName);
            categoryManager.addCategory(newCategory);
            updateCategorySpinner();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void editCategory() {
        Category selectedCategory = (Category) categorySpinner.getSelectedItem();
        if (selectedCategory == null) {
            Toast.makeText(this, "No category selected", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Category");

        View customView = getLayoutInflater().inflate(R.layout.add_category_dialog, null);
        EditText categoryNameEditText = customView.findViewById(R.id.categoryName);
        categoryNameEditText.setText(selectedCategory.getName());

        builder.setView(customView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            String categoryName = categoryNameEditText.getText().toString();
            selectedCategory.setName(categoryName);
            categoryManager.updateCategory(categoryManager.getCategories().indexOf(selectedCategory), selectedCategory);
            updateCategorySpinner();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void setupCategorySpinner() {
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, categoryManager.getCategories());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }

    private void updateCategorySpinner() {
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(
                this, R.layout.spinner_item, categoryManager.getCategories());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);
    }



    private void setupEnterButton() {
        enterButton.setOnClickListener(v -> {
            Category selectedCategory = (Category) categorySpinner.getSelectedItem();
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.putExtra("SELECTED_CATEGORY_ID", selectedCategory.getId());
            intent.putExtra("SELECTED_CATEGORY_NAME", selectedCategory.getName());
            startActivity(intent);
        });
    }

}
