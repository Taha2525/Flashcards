package com.example.flashcardsprobe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;
import java.util.Random;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;


public class MainActivity extends AppCompatActivity {
    private CardView flashcardCardView;
    private TextView cardContentTextView;
    private boolean isFrontOfCardShowing = true;
    private int currentCardIndex = 0;
    private ArrayList<Flashcard> flashcards;
    private int[] cardColors;
    private ArrayList<Category> categories = new ArrayList<>();


    private void saveFlashcards() {
        SharedPreferences sharedPreferences = getSharedPreferences("Flashcards", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(flashcards);
        editor.putString("flashcardList", json);
        editor.apply();
    }

    private void loadFlashcards() {
        SharedPreferences sharedPreferences = getSharedPreferences("Flashcards", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("flashcardList", null);
        Type type = new TypeToken<ArrayList<Flashcard>>() {}.getType();
        flashcards = gson.fromJson(json, type);

        if (flashcards == null) {
            flashcards = new ArrayList<>();
        }
    }

    private void initializeCategories() {
        // Beispiel-Kategorien
        categories.add(new Category("Türkisch"));
        categories.add(new Category("Englisch"));
        categories.add(new Category("Italienisch"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadFlashcards();
        initializeCardColors();
        initializeCategories();
        if (flashcards.isEmpty()) {
            initializeFlashcards();
        }



        flashcardCardView = findViewById(R.id.flashcardCardView);
        cardContentTextView = findViewById(R.id.cardContentTextView);
        Button prevButton = findViewById(R.id.prevButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button editButton = findViewById(R.id.editButton);
        Button deleteButton = findViewById(R.id.deleteButton);


        flashcardCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Setzen Sie den Drehpunkt in die Mitte der Karte
                flashcardCardView.setPivotX(flashcardCardView.getWidth() / 2);
                flashcardCardView.setPivotY(flashcardCardView.getHeight() / 2);

                // Erstellen Sie die erste Hälfte der Flip-Animation
                ObjectAnimator flip1 = ObjectAnimator.ofFloat(flashcardCardView, "rotationY", 0f, 90f);
                flip1.setInterpolator(new AccelerateInterpolator());
                flip1.setDuration(500);

                // Erstellen Sie die zweite Hälfte der Flip-Animation
                final ObjectAnimator flip2 = ObjectAnimator.ofFloat(flashcardCardView, "rotationY", -90f, 0f);
                flip2.setInterpolator(new DecelerateInterpolator());
                flip2.setDuration(500);

                flip1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        if (!flashcards.isEmpty() && currentCardIndex >= 0 && currentCardIndex < flashcards.size()) {
                            // Wechseln Sie den Text in der Mitte der Animation
                            if (isFrontOfCardShowing) {
                                cardContentTextView.setText(flashcards.get(currentCardIndex).getBackText());
                            } else {
                                cardContentTextView.setText(flashcards.get(currentCardIndex).getFrontText());
                            }
                            isFrontOfCardShowing = !isFrontOfCardShowing;
                        }


                        // Beispielsweise Kamera-Distanz setzen
                        float scale = getResources().getDisplayMetrics().density * 8000;
                        flashcardCardView.setCameraDistance(scale);

                        // Starten Sie die zweite Hälfte der Animation
                        flip2.start();
                    }
                });

                // Starten Sie die erste Hälfte der Animation
                flip1.start();

                flashcardCardView.setCardElevation(0);

            }
        });


        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(MainActivity.this, view);
                popup.getMenuInflater().inflate(R.menu.fab_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        if (id == R.id.action_add_new_card) {
                            addNewFlashcard();
                            return true;
                        } else if (id == R.id.new_category) {
                            // Implementieren Sie die Logik für "Create new Group"
                            return true;
                        } else if (id == R.id.action_category) {
                            // Implementieren Sie die Logik für "View Groups"
                            return true;
                        } else if (id == R.id.action_add_to_category) {
                            assignCardToCategory();
                            return true;
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });




        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCardIndex > 0) {
                    // Animation für das aktuelle Element
                    ObjectAnimator outAnimator = ObjectAnimator.ofFloat(flashcardCardView, "translationX", 0, flashcardCardView.getWidth());
                    outAnimator.setDuration(200);
                    outAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            currentCardIndex--;
                            updateCardContent();

                            // Animation für das vorherige Element
                            flashcardCardView.setTranslationX(-flashcardCardView.getWidth());
                            ObjectAnimator inAnimator = ObjectAnimator.ofFloat(flashcardCardView, "translationX", -flashcardCardView.getWidth(), 0);
                            inAnimator.setDuration(200);
                            inAnimator.start();
                        }
                    });
                    outAnimator.start();
                }
            }
        });


        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCardIndex < flashcards.size() - 1) {
                    // Animation für das aktuelle Element
                    ObjectAnimator outAnimator = ObjectAnimator.ofFloat(flashcardCardView, "translationX", 0, -flashcardCardView.getWidth());
                    outAnimator.setDuration(200);
                    outAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            currentCardIndex++;
                            updateCardContent();

                            // Animation für das nächste Element
                            flashcardCardView.setTranslationX(flashcardCardView.getWidth());
                            ObjectAnimator inAnimator = ObjectAnimator.ofFloat(flashcardCardView, "translationX", flashcardCardView.getWidth(), 0);
                            inAnimator.setDuration(200);
                            inAnimator.start();
                        }
                    });
                    outAnimator.start();
                }
            }
        });


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editFlashcard(currentCardIndex);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFlashcard(currentCardIndex);
            }
        });
    }

    private void initializeFlashcards() {
        flashcards = new ArrayList<>();
        // Stellen Sie sicher, dass diese Texte korrekt sind
        String[] frontTexts = {};

        String[] backTexts = {};
        for (int i = 0; i < frontTexts.length; i++) {
            flashcards.add(new Flashcard(frontTexts[i], backTexts[i]));
        }
    }


    private void initializeCardColors() {
        cardColors = new int[]{
                getResources().getColor(R.color.cardColor1),
                getResources().getColor(R.color.cardColor2),
                getResources().getColor(R.color.cardColor3),
                getResources().getColor(R.color.cardColor4),
                getResources().getColor(R.color.cardColor5),
                getResources().getColor(R.color.cardColor6),
                getResources().getColor(R.color.cardColor7),
                getResources().getColor(R.color.cardColor8),
                getResources().getColor(R.color.cardColor9),
                getResources().getColor(R.color.cardColor10),
                getResources().getColor(R.color.cardColor11),
                getResources().getColor(R.color.cardColor12),
                getResources().getColor(R.color.cardColor13),
                getResources().getColor(R.color.cardColor14),
                getResources().getColor(R.color.cardColor15),
                getResources().getColor(R.color.cardColor16),
                getResources().getColor(R.color.cardColor17),
                getResources().getColor(R.color.cardColor18),
                getResources().getColor(R.color.cardColor19),
                getResources().getColor(R.color.cardColor20),
                getResources().getColor(R.color.cardColor21),
                getResources().getColor(R.color.cardColor22),
                getResources().getColor(R.color.cardColor23),
                getResources().getColor(R.color.cardColor24),
                getResources().getColor(R.color.cardColor25),
        };
    }


    private void addNewFlashcard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add new Flashcard");

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_edit_flashcard, null);
        builder.setView(customLayout);

        EditText editTextFront = customLayout.findViewById(R.id.editFrontText);
        EditText editTextBack = customLayout.findViewById(R.id.editBackText);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String frontText = editTextFront.getText().toString();
                String backText = editTextBack.getText().toString();

                if (!frontText.isEmpty() && !backText.isEmpty()) {
                    flashcards.add(new Flashcard(frontText, backText));
                    currentCardIndex = flashcards.size() - 1; // Zeigt die neu hinzugefügte Karte an
                    updateCardContent();
                    saveFlashcards(); // Verschieben Sie diese Zeile hierhin
                }
            }
        });



        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        saveFlashcards();
    }


    private void toggleCardContent() {
        if (isFrontOfCardShowing) {
            cardContentTextView.setText(flashcards.get(currentCardIndex).getBackText());
        } else {
            cardContentTextView.setText(flashcards.get(currentCardIndex).getFrontText());
        }
        isFrontOfCardShowing = !isFrontOfCardShowing;
    }

    private void updateCardContent() {
        if (!flashcards.isEmpty() && currentCardIndex >= 0 && currentCardIndex < flashcards.size()) {
            Flashcard currentCard = flashcards.get(currentCardIndex);
            cardContentTextView.setText(isFrontOfCardShowing ? currentCard.getFrontText() : currentCard.getBackText());
            flashcardCardView.setCardBackgroundColor(getRandomColor());
        } else {
            // Hier können Sie definieren, was passieren soll, wenn keine Karten vorhanden sind
            cardContentTextView.setText("Keine Karten vorhanden");
        }
    }



    private int getRandomColor() {
        Random random = new Random();
        return cardColors[random.nextInt(cardColors.length)];
    }

    private void deleteFlashcard(int index) {
        if (index < flashcards.size()) {
            flashcards.remove(index);
            currentCardIndex = Math.max(0, currentCardIndex - 1);
            updateCardContent();
        }
        saveFlashcards();
    }


    private void editFlashcard(int index) {
        Flashcard flashcard = flashcards.get(index);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Edit Flashcard");

        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_edit_flashcard, null);
        builder.setView(customLayout);

        EditText editTextFront = customLayout.findViewById(R.id.editFrontText);
        EditText editTextBack = customLayout.findViewById(R.id.editBackText);

        editTextFront.setText(flashcard.getFrontText());
        editTextBack.setText(flashcard.getBackText());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newFrontText = editTextFront.getText().toString();
                String newBackText = editTextBack.getText().toString();

                flashcard.setFrontText(newFrontText);
                flashcard.setBackText(newBackText);
                updateCardContent();
                saveFlashcards(); // Verschieben Sie diese Zeile hierhin
            }
        });

        builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void assignCardToCategory() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Assign Flashcard to Category");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        Spinner cardSpinner = new Spinner(this);
        ArrayAdapter<Flashcard> cardAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, flashcards);
        cardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cardSpinner.setAdapter(cardAdapter);

        Spinner categorySpinner = new Spinner(this);
        ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        layout.addView(cardSpinner);
        layout.addView(categorySpinner);

        builder.setView(layout);

        builder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.Flashcard selectedCard = (Flashcard) cardSpinner.getSelectedItem();
                Category selectedCategory = (Category) categorySpinner.getSelectedItem();
                if (selectedCard != null && selectedCategory != null) {
                    selectedCategory.addFlashcard(selectedCard);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    public class Flashcard {
        private String frontText;
        private String backText;

        public Flashcard(String frontText, String backText) {
            this.frontText = frontText;
            this.backText = backText;
        }

        public String getFrontText() {
            return frontText;
        }

        public void setFrontText(String frontText) {
            this.frontText = frontText;
        }

        public String getBackText() {
            return backText;
        }

        public void setBackText(String backText) {
            this.backText = backText;
        }
    }


}
