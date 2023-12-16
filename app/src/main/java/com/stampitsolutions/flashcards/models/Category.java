package com.stampitsolutions.flashcards.models;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String id;
    private String name;
    private List<String> flashcardIds; // Storing IDs of flashcards

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
        this.flashcardIds = new ArrayList<>();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getFlashcardIds() {
        return flashcardIds;
    }

    @Override
    public String toString() {
        return name; // Assuming "name" is the field containing the category name
    }

    public void addFlashcardId(String flashcardId) {
        if (!flashcardIds.contains(flashcardId)) {
            flashcardIds.add(flashcardId);
        }
    }

}
