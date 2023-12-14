package com.example.flashcards.models;

public class Flashcard {
    private String frontText;
    private String backText;
    private String categoryId; // Assuming category is identified by a String ID

    public Flashcard(String frontText, String backText, String categoryId) {
        this.frontText = frontText;
        this.backText = backText;
        this.categoryId = categoryId;
    }

    // Getters and setters
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
