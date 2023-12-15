package com.example.flashcards;

import com.example.flashcards.models.Flashcard;
import com.example.flashcards.utils.SharedPreferencesHelper;
import java.util.List;

public class FlashcardManager {

    private SharedPreferencesHelper sharedPreferencesHelper;
    private List<Flashcard> flashcards;

    public FlashcardManager(SharedPreferencesHelper sharedPreferencesHelper) {
        this.sharedPreferencesHelper = sharedPreferencesHelper;
        flashcards = sharedPreferencesHelper.loadFlashcards();
    }

    public List<Flashcard> getFlashcards() {
        return flashcards;
    }

    public void addFlashcard(Flashcard flashcard) {
        flashcards.add(flashcard);
        saveFlashcards();
    }

    public void updateFlashcard(int index, Flashcard flashcard) {
        if (index >= 0 && index < flashcards.size()) {
            flashcards.set(index, flashcard);
            saveFlashcards();
        }
    }

    public void deleteFlashcard(int index) {
        if (index >= 0 && index < flashcards.size()) {
            flashcards.remove(index);
            saveFlashcards();
        }
    }

    private void saveFlashcards() {
        sharedPreferencesHelper.saveFlashcards(flashcards);
    }
}