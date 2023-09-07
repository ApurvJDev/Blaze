package com.project.blaze.home.presentation.adapters;

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

public class FlashcardAdapter extends FirestoreRecyclerAdapter<FlashcardModel, FlashcardAdapter.FlashCardViewHolder> {


    private FlashCardClickListener listener;
    public static final String SEEN = "Seen";
    public static final String UNSEEN = "Unseen";
    public static final String GRAD = "Graduated";
    public static final String NOT_GRAD = "Not Graduated";

    public FlashcardAdapter(@NonNull FirestoreRecyclerOptions<FlashcardModel> options) {
        super(options);
    }

    public void setListener(FlashCardClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull FlashCardViewHolder holder, int position, @NonNull FlashcardModel model) {
        holder.question.setText(model.getQuestion());
        if(model.getNextReview() == -1)
        {
            holder.reviewDate.setText("");
            holder.status.setText(UNSEEN);
        }
        else {
             if(model.getNextReview()<20) holder.grad.setText(NOT_GRAD);
             else holder.grad.setText(GRAD);

            holder.reviewDate.setText(String.valueOf(model.getNextReview()));
            holder.status.setText(SEEN);
        }

    }

    @NonNull
    @Override
    public FlashCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.flashcard_item,parent,false);
        return new FlashCardViewHolder(v,listener);
    }

    class FlashCardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView question,status,reviewDate,grad;
        private final ImageView img_edit;
        private final MaterialCardView cardItem;
        private final FlashCardClickListener  listener;

        public FlashCardViewHolder(@NonNull View itemView, FlashCardClickListener listener) {
            super(itemView);
            this.listener = listener;
            question = itemView.findViewById(R.id.txt_question);
            status = itemView.findViewById(R.id.txt_seen_status);
            reviewDate = itemView.findViewById(R.id.txt_next_review_date);
            grad = itemView.findViewById(R.id.txt_graduated);
            img_edit = itemView.findViewById(R.id.img_edit);
            cardItem = itemView.findViewById(R.id.flash_card);

            cardItem.setOnClickListener(this);
            img_edit.setOnClickListener(v ->{
                int pos = getBindingAdapterPosition();
                listener.onEditClick(getSnapshots().getSnapshot(pos));
            });
        }

        @Override
        public void onClick(View view) {
            int pos = getBindingAdapterPosition();
            listener.onCardClick(getSnapshots().getSnapshot(pos));
        }
    }

    public interface FlashCardClickListener{
        void onCardClick(DocumentSnapshot snapshot);

        void onEditClick(DocumentSnapshot snapshot);
    }

}
