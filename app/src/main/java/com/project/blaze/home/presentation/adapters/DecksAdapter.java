package com.project.blaze.home.presentation.adapters;

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
import com.project.blaze.home.dto.DeckModel;

public class DecksAdapter extends FirestoreRecyclerAdapter<DeckModel,DecksAdapter.DeckViewHolder> {

    public static final String TAG = "DecksAdapter";
    private DeckClickListener listener;

    public DecksAdapter(@NonNull FirestoreRecyclerOptions<DeckModel> options) {
        super(options);
    }

    public void setListener(DeckClickListener listener){
        this.listener = listener;

    }

    @Override
    protected void onBindViewHolder(@NonNull DeckViewHolder holder, int position, @NonNull DeckModel model) {
        String cardCnt = "("+model.getFlashcardCount()+")";
        holder.name.setText(model.getDeckName());
        holder.date.setText(model.getDateCreated());
        holder.cardCount.setText(cardCnt);

    }

    @NonNull
    @Override
    public DeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.deck_item,parent,false);
        return new DeckViewHolder(v,listener);
    }

     class DeckViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView name,date,cardCount;
        private MaterialCardView deckItem;
        private final DeckClickListener listener;

        public DeckViewHolder(@NonNull View itemView,DeckClickListener listener) {
            super(itemView);
            this.listener = listener;
            name = itemView.findViewById(R.id.txt_deck_name);
            date = itemView.findViewById(R.id.txt_date);
            cardCount = itemView.findViewById(R.id.txt_card_count);
            deckItem = itemView.findViewById(R.id.deck_item);

            deckItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getBindingAdapterPosition();
            listener.onDeckClick(getSnapshots().getSnapshot(pos));
        }
    }

    public interface DeckClickListener{
        void onDeckClick(DocumentSnapshot snapshot);
    }
}


