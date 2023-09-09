package com.project.blaze.queue.presentation.adapters;

import static com.project.blaze.home.presentation.adapters.FlashcardAdapter.GRAD;
import static com.project.blaze.home.presentation.adapters.FlashcardAdapter.NOT_GRAD;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.project.blaze.R;
import com.project.blaze.home.dto.FlashcardModel;
import com.project.blaze.queue.domain.MinutesToDateConverter;

public class QueueAdapter extends FirestoreRecyclerAdapter<FlashcardModel, QueueAdapter.QueueViewHolder> {

    private OnFlashCardClickListener listener;
    public QueueAdapter(@NonNull FirestoreRecyclerOptions<FlashcardModel> options) {
        super(options);
    }

    public void setListener(OnFlashCardClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull QueueViewHolder holder, int position, @NonNull FlashcardModel model) {
        holder.question.setText(model.getQuestion());
        holder.reviewDate.setText(cardReviewDate(model));
        holder.status.setVisibility(View.INVISIBLE);
        if(model.isGraduated())
            holder.grad.setText(GRAD);
        else holder.grad.setText(NOT_GRAD);
    }

    private String cardReviewDate(FlashcardModel model) {
        MinutesToDateConverter converter = new MinutesToDateConverter();
        return  converter.convertMinutesToDate(model.getNextReview());
    }

    @NonNull
    @Override
    public QueueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.flashcard_item,parent,false);
        return new QueueViewHolder(v,listener);
    }

     class QueueViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private OnFlashCardClickListener listener;
        private final TextView question,status,reviewDate,grad;
        private final ImageView img_edit;
        private final MaterialCardView cardItem;

        public QueueViewHolder(@NonNull View itemView, OnFlashCardClickListener listener) {
            super(itemView);
            this.listener = listener;
            question = itemView.findViewById(R.id.txt_question);
            status = itemView.findViewById(R.id.txt_seen_status);
            reviewDate = itemView.findViewById(R.id.txt_next_review_date);
            grad = itemView.findViewById(R.id.txt_graduated);
            img_edit = itemView.findViewById(R.id.img_edit);
            cardItem = itemView.findViewById(R.id.flash_card);

            cardItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getBindingAdapterPosition();
            listener.onFlashCardClick(getSnapshots().getSnapshot(pos));
        }
    }

    public interface OnFlashCardClickListener
    {
        void onFlashCardClick(DocumentSnapshot documentSnapshot);
    }

}
