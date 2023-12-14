package com.example.flashcardsprobe;

import com.example.flashcardsprobe.MainActivity;

import java.util.ArrayList;
import java.util.List;

// Datei: Category.java
public class Category {
    private String name;
    private List<MainActivity.Flashcard> flashcards;

    public Category(String name) {
        this.name = name;
        this.flashcards = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<MainActivity.Flashcard> getFlashcards() {
        return flashcards;
    }

    public void addFlashcard(MainActivity.Flashcard flashcard) {
        flashcards.add(flashcard);
    }
}
