package com.example.flashcards;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
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

    private CategoryManager categoryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this);
        flashcardManager = new FlashcardManager(sharedPreferencesHelper);
        categoryManager = new CategoryManager(sharedPreferencesHelper);


        flashcardCardView = findViewById(R.id.flashcardCardView);
        cardContentTextView = findViewById(R.id.cardContentTextView);
        recyclerView = findViewById(R.id.recyclerView);
        backButton = findViewById(R.id.backButton);

        selectedCategoryId = getIntent().getStringExtra("SELECTED_CATEGORY_ID");
        Log.d("FlashcardApp", "Received Category ID: " + selectedCategoryId);

        setupRecyclerView();
        setupButtons();
        updateCardContent();
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
        Button editButton = findViewById(R.id.editButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> navigateBack());
        flashcardCardView.setOnClickListener(v -> flipCard());
        prevButton.setOnClickListener(v -> navigateFlashcards(false));
        nextButton.setOnClickListener(v -> navigateFlashcards(true));
        editButton.setOnClickListener(v -> editFlashcard(currentCardIndex));
        deleteButton.setOnClickListener(v -> deleteFlashcard(currentCardIndex));
        fabAdd.setOnClickListener(v -> showAddFlashcardDialog());
    }

    private void deleteFlashcard(int index) {
        flashcardManager.deleteFlashcard(index);
        flashcardAdapter.notifyDataSetChanged();
        if (currentCardIndex >= flashcardManager.getFlashcards().size()) {
            currentCardIndex = Math.max(0, flashcardManager.getFlashcards().size() - 1);
        }
        updateCardContent();
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

    private void navigateFlashcards(boolean toNext) {
        int nextIndex = toNext ? findNextFlashcardIndex() : findPrevFlashcardIndex();
        if (nextIndex < 0) {
            return;
        }
        slideOutAndInAnimation(toNext);
        currentCardIndex = nextIndex;
        updateCardContent();
        flashcardAdapter.notifyDataSetChanged();
    }

    /*
    * private void navigateFlashcards(boolean next) {
        int newCardIndex = next ? findNextFlashcardIndex() : findPrevFlashcardIndex();
        if (newCardIndex != -1) {
            currentCardIndex = newCardIndex;
            updateCardContent();
            slideOutAndInAnimation(next);
        }
    }
    * */

    private void navigateBack() {
        finish();
    }

    private int findNextFlashcardIndex() {
        List<Flashcard> flashcards = flashcardManager.getFlashcards();
        for (int i = currentCardIndex + 1; i < flashcards.size(); i++) {
            if (selectedCategoryId.equals(flashcards.get(i).getCategoryId())) {
                return i;
            }
        }
        return -1;
    }

    private int findPrevFlashcardIndex() {
        List<Flashcard> flashcards = flashcardManager.getFlashcards();
        for (int i = currentCardIndex - 1; i >= 0; i--) {
            if (selectedCategoryId.equals(flashcards.get(i).getCategoryId())) {
                return i;
            }
        }
        return -1;
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
            isFrontOfCardShowing = true; // Reset the flag
            return; // Early return to prevent showing cards from other categories
        }

        if (currentCardIndex < 0 || currentCardIndex >= filteredFlashcards.size()) {
            cardContentTextView.setText("Invalid flashcard index: " + currentCardIndex);
        } else {
            Flashcard currentCard = filteredFlashcards.get(currentCardIndex);
            cardContentTextView.setText(isFrontOfCardShowing ? currentCard.getFrontText() : currentCard.getBackText());
            isFrontOfCardShowing = true; // Reset the flag for the next card
        }
    }

}