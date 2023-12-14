package com.example.flashcards;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
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
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private CardView flashcardCardView;
    private TextView cardContentTextView;
    private RecyclerView recyclerView;
    private FlashcardAdapter flashcardAdapter;
    private boolean isFrontOfCardShowing = true;
    private int currentCardIndex = 0;
    private FlashcardManager flashcardManager;
    private CategoryManager categoryManager;
    private Spinner categorySpinner;
    private ArrayAdapter<Category> categoryAdapter;
    private List<Category> categories;
    private TextView currentCategoryTextView;
    private String selectedCategoryId;

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
        categorySpinner = findViewById(R.id.categorySpinner);
        currentCategoryTextView = findViewById(R.id.currentCategoryTextView);
        selectedCategoryId = null;
        setupRecyclerView();
        setupButtons();
        updateCardContent();
        setupCategorySpinner();
    }

    private void setupCategorySpinner() {
        categories = categoryManager.getCategories();
        categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                Category selectedCategory = (Category) categorySpinner.getSelectedItem();
                if (selectedCategory != null) {
                    currentCategoryTextView.setText("Current Category: " + selectedCategory.getName());
                    // Update the selected category and refresh the RecyclerView
                    selectedCategoryId = selectedCategory.getId();
                    refreshRecyclerView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                currentCategoryTextView.setText(""); // Clear the text if nothing is selected
                // Set the selected category to null to show all flashcards
                selectedCategoryId = null;
                refreshRecyclerView();
            }
        });
    }

    private void refreshRecyclerView() {
        List<Flashcard> filteredFlashcards = getFilteredFlashcards();

        if (filteredFlashcards.isEmpty()) {
            // Handle the case where no flashcards match the selected category
            cardContentTextView.setText("No flashcards in this category");
        } else {
            // Set the adapter with the filtered flashcards
            flashcardAdapter = new FlashcardAdapter(filteredFlashcards);
            recyclerView.setAdapter(flashcardAdapter);
            flashcardAdapter.notifyDataSetChanged();
            currentCardIndex = 0; // Reset the current card index
            updateCardContent();
        }
    }

    private List<Flashcard> getFilteredFlashcards() {
        List<Flashcard> allFlashcards = flashcardManager.getFlashcards();
        List<Flashcard> filteredFlashcards = new ArrayList<>();

        if (selectedCategoryId == null) {
            // Show all flashcards when no category is selected
            return allFlashcards;
        }

        // Filter flashcards based on the selected category
        for (Flashcard flashcard : allFlashcards) {
            if (selectedCategoryId.equals(flashcard.getCategoryId())) {
                filteredFlashcards.add(flashcard);
            }
        }

        return filteredFlashcards;
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        flashcardAdapter = new FlashcardAdapter(flashcardManager.getFlashcards());
        recyclerView.setAdapter(flashcardAdapter);
    }

    private void setupButtons() {
        Button prevButton = findViewById(R.id.prevButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button editButton = findViewById(R.id.editButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        FloatingActionButton fab = findViewById(R.id.fab_add);

        flashcardCardView.setOnClickListener(v -> flipCard());
        prevButton.setOnClickListener(v -> navigateFlashcards(false));
        nextButton.setOnClickListener(v -> navigateFlashcards(true));
        editButton.setOnClickListener(v -> editFlashcard(currentCardIndex));
        deleteButton.setOnClickListener(v -> deleteFlashcard(currentCardIndex));
        fab.setOnClickListener(this::showPopupMenu);
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, view);
        popupMenu.getMenuInflater().inflate(R.menu.fab_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_add_new_card) {
                showAddFlashcardDialog();
                return true;
            } else if (itemId == R.id.new_category) {
                // Call the method to show dialog for adding a new category
                showAddCategoryDialog();
                return true;
            } else if (itemId == R.id.action_category) {
                // Logic to view category
                return true;
            } else if (itemId == R.id.action_add_to_category) {
                // Logic to add flashcard to category
                return true;
            }
            return false;
        });
        popupMenu.show();
    }



    private void showAddFlashcardDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Flashcard");

        // Custom layout for the dialog
        View customView = getLayoutInflater().inflate(R.layout.add_flashcard_dialog, null);
        EditText editFrontText = customView.findViewById(R.id.addFrontText);
        EditText editBackText = customView.findViewById(R.id.addBackText);

        builder.setView(customView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            // Add the new flashcard
            Flashcard newFlashcard = new Flashcard(editFrontText.getText().toString(),
                    editBackText.getText().toString(),
                    ""); // Category ID can be set if needed
            flashcardManager.addFlashcard(newFlashcard);
            flashcardAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Category");

        View customView = getLayoutInflater().inflate(R.layout.add_category_dialog, null);
        EditText categoryName = customView.findViewById(R.id.categoryName);

        builder.setView(customView);
        builder.setPositiveButton("Add", (dialog, which) -> {
            String newCategoryId = generateCategoryId(); // Generate a unique ID for the category
            String newCategoryName = categoryName.getText().toString();

            Category newCategory = new Category(newCategoryId, newCategoryName);
            categoryManager.addCategory(newCategory);
            // Update RecyclerView or any other UI components
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private String generateCategoryId() {
        // Implement a method to generate a unique ID for each category
        // This could be a simple counter, a UUID, or any other mechanism you prefer
        return UUID.randomUUID().toString();
    }
    private void flipCard() {
        AnimationUtils.flipCard(flashcardCardView, this::toggleCardContent);
    }

    private void navigateFlashcards(boolean next) {
        if (next) {
            // Find the index of the next flashcard in the selected category
            int nextCardIndex = findNextFlashcardIndex(selectedCategoryId, currentCardIndex);
            if (nextCardIndex != -1) {
                slideOutAndInAnimation(true);
                currentCardIndex = nextCardIndex;
                updateCardContent();
            }
        } else {
            // Find the index of the previous flashcard in the selected category
            int prevCardIndex = findPrevFlashcardIndex(selectedCategoryId, currentCardIndex);
            if (prevCardIndex != -1) {
                slideOutAndInAnimation(false);
                currentCardIndex = prevCardIndex;
                updateCardContent();
            }
        }
    }

    private int findNextFlashcardIndex(String categoryId, int currentIndex) {
        List<Flashcard> flashcards = flashcardManager.getFlashcards();
        if (categoryId != null) {
            for (int i = currentIndex + 1; i < flashcards.size(); i++) {
                Flashcard flashcard = flashcards.get(i);
                if (categoryId.equals(flashcard.getCategoryId())) {
                    return i;
                }
            }
        }
        return -1; // No next flashcard in the selected category
    }

    private int findPrevFlashcardIndex(String categoryId, int currentIndex) {
        List<Flashcard> flashcards = flashcardManager.getFlashcards();
        if (categoryId != null) {
            for (int i = currentIndex - 1; i >= 0; i--) {
                Flashcard flashcard = flashcards.get(i);
                if (categoryId.equals(flashcard.getCategoryId())) {
                    return i;
                }
            }
        }
        return -1; // No previous flashcard in the selected category
    }

    private void slideOutAndInAnimation(boolean toNext) {
        ObjectAnimator outAnimator = ObjectAnimator.ofFloat(flashcardCardView, "translationX", 0, toNext ? -flashcardCardView.getWidth() : flashcardCardView.getWidth());
        outAnimator.setDuration(200);
        outAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                // Update index and card content
                currentCardIndex += toNext ? 1 : -1;
                updateCardContent();

                // Slide-in animation
                flashcardCardView.setTranslationX(toNext ? flashcardCardView.getWidth() : -flashcardCardView.getWidth());
                ObjectAnimator inAnimator = ObjectAnimator.ofFloat(flashcardCardView, "translationX", flashcardCardView.getWidth(), 0);
                inAnimator.setDuration(200);
                inAnimator.start();
            }
        });
        outAnimator.start();
    }



    private void toggleCardContent() {
        if (currentCardIndex < 0 || currentCardIndex >= flashcardManager.getFlashcards().size()) {
            // Index is out of bounds, handle appropriately
            // For example, you might want to show a message or hide the card view
            return;
        }

        Flashcard currentCard = flashcardManager.getFlashcards().get(currentCardIndex);
        String content = isFrontOfCardShowing ? currentCard.getBackText() : currentCard.getFrontText();
        cardContentTextView.setText(content);
        isFrontOfCardShowing = !isFrontOfCardShowing;
    }


    private void updateCardContent() {
        List<Flashcard> flashcards = flashcardManager.getFlashcards();

        if (flashcards.isEmpty()) {
            cardContentTextView.setText("No flashcards available");
            isFrontOfCardShowing = true; // Reset the flag
        } else {
            if (currentCardIndex < 0 || currentCardIndex >= flashcards.size()) {
                cardContentTextView.setText("Invalid flashcard index: " + currentCardIndex);
            } else {
                Flashcard currentCard = flashcards.get(currentCardIndex);
                String content = isFrontOfCardShowing ? currentCard.getFrontText() : currentCard.getBackText();
                cardContentTextView.setText(content);
                isFrontOfCardShowing = true; // Reset the flag for the next card
            }
        }
    }



    private void editFlashcard(int index) {
        showEditFlashcardDialog(index);
    }

    private void showEditFlashcardDialog(int index) {
        Flashcard flashcard = flashcardManager.getFlashcards().get(index);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Flashcard");

        // Custom layout for the dialog
        View customView = getLayoutInflater().inflate(R.layout.edit_flashcard_dialog, null);
        EditText editFrontText = customView.findViewById(R.id.editFrontText);
        EditText editBackText = customView.findViewById(R.id.editBackText);

        editFrontText.setText(flashcard.getFrontText());
        editBackText.setText(flashcard.getBackText());

        builder.setView(customView);
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Save the edited flashcard
            flashcard.setFrontText(editFrontText.getText().toString());
            flashcard.setBackText(editBackText.getText().toString());
            flashcardAdapter.notifyDataSetChanged();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void deleteFlashcard(int index) {
        flashcardManager.deleteFlashcard(index);
        if (currentCardIndex >= flashcardManager.getFlashcards().size()) {
            currentCardIndex = Math.max(0, flashcardManager.getFlashcards().size() - 1);
        }
        updateCardContent();
        flashcardAdapter.notifyDataSetChanged();
    }

    // Additional methods for managing flashcards and categories...
}
