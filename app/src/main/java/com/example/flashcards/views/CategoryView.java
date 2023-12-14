package com.example.flashcards.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.flashcards.R;

public class CategoryView extends LinearLayout {

    private TextView categoryName;
    private TextView flashcardCount;

    public CategoryView(Context context) {
        super(context);
        init(context);
    }

    public CategoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.category_item, this, true);
        categoryName = findViewById(R.id.categoryName);
        flashcardCount = findViewById(R.id.flashcardCount);
    }

    public void setCategoryName(String name) {
        categoryName.setText(name);
    }

    public void setFlashcardCount(int count) {
        flashcardCount.setText(count + " Cards");
    }

    // Additional methods to handle category data
}
