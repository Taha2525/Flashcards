package com.example.flashcardsprobe;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private CardView flashcardCardView;
    private TextView cardContentTextView;
    private boolean isFrontOfCardShowing = true;
    private int currentCardIndex = 0;
    private ArrayList<Flashcard> flashcards;
    private int[] cardColors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFlashcards();
        initializeCardColors();

        flashcardCardView = findViewById(R.id.flashcardCardView);
        cardContentTextView = findViewById(R.id.cardContentTextView);
        Button prevButton = findViewById(R.id.prevButton);
        Button nextButton = findViewById(R.id.nextButton);
        Button editButton = findViewById(R.id.editButton);
        Button deleteButton = findViewById(R.id.deleteButton);
        Button addButton = findViewById(R.id.addButton);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewFlashcard();
            }
        });

        updateCardContent();

        flashcardCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCardContent();
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCardIndex > 0) {
                    currentCardIndex--;
                    updateCardContent();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentCardIndex < flashcards.size() - 1) {
                    currentCardIndex++;
                    updateCardContent();
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
        String[] frontTexts = {"Apple", "Book", "Computer", "Nature", "Ocean", "Sun", "Moon", "Star", "Mountain", "River"};
        String[] backTexts = {"Apfel", "Buch", "Computer", "Natur", "Ozean", "Sonne", "Mond", "Stern", "Berg", "Fluss"};
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
        Flashcard currentCard = flashcards.get(currentCardIndex);
        cardContentTextView.setText(isFrontOfCardShowing ? currentCard.getFrontText() : currentCard.getBackText());
        flashcardCardView.setCardBackgroundColor(getRandomColor());
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

    public static class Flashcard {
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