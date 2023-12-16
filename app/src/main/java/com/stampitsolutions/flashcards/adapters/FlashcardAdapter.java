package com.stampitsolutions.flashcards.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.stampitsolutions.flashcards.R;
import com.stampitsolutions.flashcards.models.Flashcard;
import com.stampitsolutions.flashcards.views.FlashcardView;
import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder> {

    private List<Flashcard> flashcards;
    private String selectedCategoryId;
    public FlashcardAdapter(List<Flashcard> flashcards) {
        this.flashcards = flashcards;
    }

    // Update the constructor to accept the selected category ID
    public FlashcardAdapter(List<Flashcard> flashcards, String selectedCategoryId) {
        this.flashcards = flashcards;
        this.selectedCategoryId = selectedCategoryId;
    }

    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new FlashcardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardViewHolder holder, int position) {
        Flashcard flashcard = flashcards.get(position);
        if (selectedCategoryId == null || selectedCategoryId.equals(flashcard.getCategoryId())) {
            // Only bind the flashcard view if it belongs to the selected category or no category is selected
            holder.flashcardView.setCardText(flashcard.getFrontText()); // Or any other data you want to display
        } else {
            // Hide the view if the flashcard does not belong to the selected category
            holder.flashcardView.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return flashcards.size();
    }

    public void addFlashcard(Flashcard flashcard) {
        flashcards.add(flashcard);
        notifyItemInserted(flashcards.size() - 1);
    }

    public void updateFlashcard(int position, Flashcard flashcard) {
        flashcards.set(position, flashcard);
        notifyItemChanged(position);
    }

    public void removeFlashcard(int position) {
        flashcards.remove(position);
        notifyItemRemoved(position);
    }

    static class FlashcardViewHolder extends RecyclerView.ViewHolder {
        FlashcardView flashcardView;

        FlashcardViewHolder(View itemView) {
            super(itemView);
            flashcardView = (FlashcardView) itemView;
        }
    }
}
