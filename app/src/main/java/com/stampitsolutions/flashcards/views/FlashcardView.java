package com.stampitsolutions.flashcards.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.stampitsolutions.flashcards.R;

public class FlashcardView extends LinearLayout {

    private TextView cardText;

    public FlashcardView(Context context) {
        super(context);
        init(context);
    }

    public FlashcardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.card_item, this, true);
        cardText = findViewById(R.id.cardText);
    }

    public void setCardText(String text) {
        cardText.setText(text);
    }

}
