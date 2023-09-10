package com.project.blaze.global.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.blaze.R;
import com.project.blaze.home.dto.FlashcardModel;

public class ViewFlashcardsAdapter extends FirestoreRecyclerAdapter<FlashcardModel, ViewFlashcardsAdapter.ViewFlashcardsViewholder> {
    private OnViewFCardClick listener;
    public ViewFlashcardsAdapter(@NonNull FirestoreRecyclerOptions<FlashcardModel> options) {
        super(options);
    }

    public void setListener(OnViewFCardClick listener) {
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewFlashcardsAdapter.ViewFlashcardsViewholder holder, int position, @NonNull FlashcardModel model) {
       holder.question.setText(model.getQuestion());
    }

    @NonNull
    @Override
    public ViewFlashcardsAdapter.ViewFlashcardsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_global_flashcard_item,parent,false);
        return new ViewFlashcardsViewholder(v,listener);
    }

    class ViewFlashcardsViewholder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView question;
        private final OnViewFCardClick listener;
        private MaterialCardView card;
        public ViewFlashcardsViewholder(@NonNull View itemView, OnViewFCardClick listener) {
            super(itemView);
            question = itemView.findViewById(R.id.txt_view_question);
            card = itemView.findViewById(R.id.flash_card);
            card.setOnClickListener(this);
            this.listener = listener;
        }

        @Override
        public void onClick(View view) {
            int pos = getBindingAdapterPosition();
            listener.onViewFCardClicked(getSnapshots().getSnapshot(pos));
        }
    }

    public interface OnViewFCardClick
    {
        void onViewFCardClicked(DocumentSnapshot snapshot);
    }
}
