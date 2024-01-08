package com.stampitsolutions.flashcards;

import com.stampitsolutions.flashcards.models.Category;
import com.stampitsolutions.flashcards.utils.SharedPreferencesHelper;
import java.util.List;

public class CategoryManager {

    private SharedPreferencesHelper sharedPreferencesHelper;
    private List<Category> categories;

    public CategoryManager(SharedPreferencesHelper sharedPreferencesHelper) {
        this.sharedPreferencesHelper = sharedPreferencesHelper;
        categories = sharedPreferencesHelper.loadCategories();
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        categories.add(category);
        saveCategories();
    }

    public void updateCategory(int index, Category category) {
        if (index >= 0 && index < categories.size()) {
            categories.set(index, category);
            saveCategories();
        }
    }

    public void deleteCategory(int index) {
        if (index >= 0 && index < categories.size()) {
            categories.remove(index);
            saveCategories();
        }
    }

    private void saveCategories() {
        sharedPreferencesHelper.saveCategories(categories);
    }

}
