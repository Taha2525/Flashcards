package com.example.flashcards.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.flashcards.models.Flashcard;
import com.example.flashcards.models.Category;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferencesHelper {

    private static final String PREFERENCES_FILE = "FlashcardsPreferences";
    private static final String FLASHCARDS_KEY = "FlashcardsKey";
    private static final String CATEGORIES_KEY = "CategoriesKey";
    private final SharedPreferences sharedPreferences;
    private final Gson gson;

    public SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public void saveFlashcards(List<Flashcard> flashcards) {
        String json = gson.toJson(flashcards);
        sharedPreferences.edit().putString(FLASHCARDS_KEY, json).apply();
    }

    public List<Flashcard> loadFlashcards() {
        String json = sharedPreferences.getString(FLASHCARDS_KEY, "");
        Type type = new TypeToken<ArrayList<Flashcard>>() {}.getType();
        return gson.fromJson(json, type) != null ? gson.fromJson(json, type) : new ArrayList<>();
    }

    public void saveCategories(List<Category> categories) {
        String json = gson.toJson(categories);
        sharedPreferences.edit().putString(CATEGORIES_KEY, json).apply();
    }

    public List<Category> loadCategories() {
        String json = sharedPreferences.getString(CATEGORIES_KEY, "");
        Type type = new TypeToken<ArrayList<Category>>() {}.getType();
        return gson.fromJson(json, type) != null ? gson.fromJson(json, type) : new ArrayList<>();
    }
}
