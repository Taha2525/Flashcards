package com.example.flashcards;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashcards.adapters.FlashcardAdapter;
import com.example.flashcards.models.Category;
import com.example.flashcards.models.Flashcard;
import com.example.flashcards.utils.AnimationUtils;
import com.example.flashcards.utils.SharedPreferencesHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button backButton;
    private CardView flashcardCardView;
    private TextView cardContentTextView;
    private RecyclerView recyclerView;
    private FlashcardAdapter flashcardAdapter;
    private boolean isFrontOfCardShowing = true;
    private int currentCardIndex = 0;
    private FlashcardManager flashcardManager;
    private String selectedCategoryId;

    private int[] cardColors = new int[]{
            R.color.cardColor1, // Lemon Yellow
            R.color.cardColor2, // Light Pink
            R.color.cardColor3, // Sky Blue
            R.color.cardColor4, // Lavender
            R.color.cardColor5, // Turquoise Blue
            R.color.newColor1,
            R.color.newColor2,
            R.color.newColor3,
            R.color.newColor4,
            R.color.newColor5,
            R.color.newColor6,
            R.color.newColor7,
            R.color.newColor8,
            R.color.newColor9,
            R.color.newColor10
    };
    private int currentColorIndex = 0;


    private CategoryManager categoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences preferences = getSharedPreferences("AppPreferences", MODE_PRIVATE);
        boolean isFirstLaunch = preferences.getBoolean("FirstLaunch", true);

        if (isFirstLaunch) {
            Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
            startActivity(intent);
            finish(); // Close MainActivity
            return; // Prevent further initialization in this launch
        }

        initManagers();
        initViews();
        setupRecyclerView();
        setupButtons();
        updateCardContent();
    }

    private void initManagers() {
        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this);
        flashcardManager = new FlashcardManager(sharedPreferencesHelper);
        categoryManager = new CategoryManager(sharedPreferencesHelper);
    }

    private void initViews() {
        flashcardCardView = findViewById(R.id.flashcardCardView);
        cardContentTextView = findViewById(R.id.cardContentTextView);
        recyclerView = findViewById(R.id.recyclerView);
        TextView currentCategoryTextView = findViewById(R.id.currentCategoryTextView);

        String selectedCategoryName = getIntent().getStringExtra("SELECTED_CATEGORY_NAME");
        currentCategoryTextView.setText(selectedCategoryName != null ? "Category: " + selectedCategoryName : "Category: Not Set");

        selectedCategoryId = getIntent().getStringExtra("SELECTED_CATEGORY_ID");
        Log.d("FlashcardApp", "Received Category ID: " + selectedCategoryId);
    }


    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        flashcardAdapter = new FlashcardAdapter(getFilteredFlashcards());
        recyclerView.setAdapter(flashcardAdapter);
    }

    private List<Flashcard> getFilteredFlashcards() {
        List<Flashcard> allFlashcards = flashcardManager.getFlashcards();
        List<Flashcard> filteredFlashcards = new ArrayList<>();
        Log.d("FlashcardApp", "Filtering for category ID: " + selectedCategoryId);
        for (Flashcard flashcard : allFlashcards) {
            Log.d("FlashcardApp", "Flashcard Category ID: " + flashcard.getCategoryId());
            if (selectedCategoryId != null && selectedCategoryId.equals(flashcard.getCategoryId())) {
                filteredFlashcards.add(flashcard);
            }
        }
        Log.d("FlashcardApp", "Filtered Flashcards count: " + filteredFlashcards.size());
        return filteredFlashcards;
    }


    private void setupButtons() {
        Button prevButton = findViewById(R.id.prevButton);
        Button nextButton = findViewById(R.id.nextButton);
        ImageView editButton = findViewById(R.id.editIcon);
        ImageView deleteButton = findViewById(R.id.deleteIcon);
        ImageView backButton = findViewById(R.id.backButton);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);

        backButton.setOnClickListener(v -> navigateBack());
        flashcardCardView.setOnClickListener(v -> flipCard());
        prevButton.setOnClickListener(v -> navigateFlashcards(false));
        nextButton.setOnClickListener(v -> navigateFlashcards(true));

        editButton.setOnClickListener(v -> editFlashcard(currentCardIndex));
        deleteButton.setOnClickListener(v -> deleteFlashcard(currentCardIndex));
        fabAdd.setOnClickListener(v -> showAddFlashcardDialog());
    }

    private void deleteFlashcard(int index) {
        if (index < 0 || index >= flashcardManager.getFlashcards().size()) {
            Toast.makeText(this, "Invalid flashcard index", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Flashcard")
                .setMessage("Are you sure you want to delete this flashcard?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    flashcardManager.deleteFlashcard(index);
                    flashcardAdapter.notifyDataSetChanged();
                    if (currentCardIndex >= flashcardManager.getFlashcards().size()) {
                        currentCardIndex = Math.max(0, flashcardManager.getFlashcards().size() - 1);
                    }
                    updateCardContent();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void showAddFlashcardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Flashcard");

        View customView = getLayoutInflater().inflate(R.layout.add_flashcard_dialog, null);
        EditText addFrontText = customView.findViewById(R.id.addFrontText);
        EditText addBackText = customView.findViewById(R.id.addBackText);
        Spinner spinnerCategory = customView.findViewById(R.id.spinnerCategory);

        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoryManager.getCategories());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);

        builder.setView(customView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            Flashcard newFlashcard = new Flashcard(
                    addFrontText.getText().toString(),
                    addBackText.getText().toString(),
                    ((Category) spinnerCategory.getSelectedItem()).getId()
            );
            flashcardManager.addFlashcard(newFlashcard);
            flashcardAdapter.notifyDataSetChanged();
            updateCardContent();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void editFlashcard(int index) {
        Flashcard flashcard = flashcardManager.getFlashcards().get(index);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Flashcard");

        View customView = getLayoutInflater().inflate(R.layout.edit_flashcard_dialog, null);
        EditText editFrontText = customView.findViewById(R.id.editFrontText);
        EditText editBackText = customView.findViewById(R.id.editBackText);

        editFrontText.setText(flashcard.getFrontText());
        editBackText.setText(flashcard.getBackText());

        builder.setView(customView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            flashcard.setFrontText(editFrontText.getText().toString());
            flashcard.setBackText(editBackText.getText().toString());
            // updateflashcard with index and flashcard updated
            flashcardManager.updateFlashcard(index, flashcard);

            flashcardAdapter.notifyDataSetChanged();
            updateCardContent();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void flipCard() {
        AnimationUtils.flipCard(flashcardCardView, this::toggleCardContent);
    }

    private void navigateBack() {
        finish();
    }

    private void navigateFlashcards(boolean next) {
        List<Flashcard> filteredFlashcards = getFilteredFlashcards();

        if (next) {
            int nextCardIndex = findNextFlashcardIndex(filteredFlashcards, currentCardIndex);
            if (nextCardIndex != -1) {
                slideOutAndInAnimation(true);
                currentCardIndex = nextCardIndex;
                updateCardContent();
            }
        } else {
            int prevCardIndex = findPrevFlashcardIndex(filteredFlashcards, currentCardIndex);
            if (prevCardIndex != -1) {
                slideOutAndInAnimation(false);
                currentCardIndex = prevCardIndex;
                updateCardContent();
            }
        }
    }

    private int findNextFlashcardIndex(List<Flashcard> flashcards, int currentIndex) {
        for (int i = currentIndex + 1; i < flashcards.size(); i++) {
            return i;
        }
        return -1; // No next flashcard in the selected category
    }

    private int findPrevFlashcardIndex(List<Flashcard> flashcards, int currentIndex) {
        for (int i = currentIndex - 1; i >= 0; i--) {
            return i;
        }
        return -1; // No previous flashcard in the selected category
    }


    private void slideOutAndInAnimation(boolean toNext) {
        ObjectAnimator outAnimator = ObjectAnimator.ofFloat(flashcardCardView, "translationX", 0, toNext ? -flashcardCardView.getWidth() : flashcardCardView.getWidth());
        outAnimator.setDuration(200).addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                flashcardCardView.setTranslationX(toNext ? flashcardCardView.getWidth() : -flashcardCardView.getWidth());
                ObjectAnimator.ofFloat(flashcardCardView, "translationX", flashcardCardView.getWidth(), 0).setDuration(200).start();
            }
        });
        outAnimator.start();
    }

    private void toggleCardContent() {
        if (currentCardIndex < 0 || currentCardIndex >= flashcardManager.getFlashcards().size()) {
            return;
        }
        Flashcard currentCard = flashcardManager.getFlashcards().get(currentCardIndex);
        cardContentTextView.setText(isFrontOfCardShowing ? currentCard.getBackText() : currentCard.getFrontText());
        isFrontOfCardShowing = !isFrontOfCardShowing;
    }

    private void updateCardContent() {
        List<Flashcard> filteredFlashcards = getFilteredFlashcards();

        if (filteredFlashcards.isEmpty()) {
            cardContentTextView.setText("No flashcards available in this category");
            isFrontOfCardShowing = true;
            currentCardIndex = -1;
        } else {
            if (currentCardIndex < 0 || currentCardIndex >= filteredFlashcards.size()) {
                currentCardIndex = 0;
            }
            Flashcard currentCard = filteredFlashcards.get(currentCardIndex);
            cardContentTextView.setText(currentCard.getFrontText());
            isFrontOfCardShowing = true;

            // Change the card's background color
            int colorId = cardColors[currentColorIndex % cardColors.length];
            flashcardCardView.setCardBackgroundColor(getResources().getColor(colorId, null));
            currentColorIndex++;
        }
    }



}
