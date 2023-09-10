package com.project.blaze.global.presentation.adapters;

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
import com.project.blaze.home.dto.DeckModel;

public class GlobalDeckAdapter extends FirestoreRecyclerAdapter<DeckModel, GlobalDeckAdapter.GlobalDeckViewHolder> {
    private OnGlobalDeckClickListener listener;

    public GlobalDeckAdapter(@NonNull FirestoreRecyclerOptions<DeckModel> options) {
        super(options);
    }

    public void setListener(OnGlobalDeckClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull GlobalDeckAdapter.GlobalDeckViewHolder holder, int position, @NonNull DeckModel model) {
        String cardCnt = "("+model.getFlashcardCount()+")";
        holder.name.setText(model.getDeckName());
        holder.date.setText(model.getDateCreated());
        holder.cardCount.setText(cardCnt);
        holder.creator.setText(model.getEmail());
    }

    @NonNull
    @Override
    public GlobalDeckAdapter.GlobalDeckViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.global_deck_item,parent,false);
        return new GlobalDeckViewHolder(v,listener);
    }

    class GlobalDeckViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
       private final OnGlobalDeckClickListener listener;
        private final TextView name,date,cardCount,creator;
        private final MaterialCardView deckItem;
        private final ImageView imgImport;
        public GlobalDeckViewHolder(@NonNull View itemView,OnGlobalDeckClickListener listener) {
            super(itemView);
            this.listener = listener;
            name = itemView.findViewById(R.id.txt_deck_name);
            date = itemView.findViewById(R.id.txt_date);
            cardCount = itemView.findViewById(R.id.txt_card_count);
            deckItem = itemView.findViewById(R.id.deck_item);
            imgImport = itemView.findViewById(R.id.img_download);
            creator = itemView.findViewById(R.id.txt_creator);
            deckItem.setOnClickListener(this);
            imgImport.setOnClickListener(view -> {
                int pos = getBindingAdapterPosition();
                listener.onDeckImport(getSnapshots().getSnapshot(pos));
            });
        }

        @Override
        public void onClick(View view) {
            int pos = getBindingAdapterPosition();
            listener.onGlobalDeckClick(getSnapshots().getSnapshot(pos));
        }
    }

    public interface OnGlobalDeckClickListener
    {
        void onGlobalDeckClick(DocumentSnapshot snapshot);

        void onDeckImport(DocumentSnapshot snapshot);
    }
}
