package com.project.blaze.home.presentation.adapters;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
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

     class DeckViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {

        private final TextView name,date,cardCount;
        private MaterialCardView deckItem;
        private ImageView imgMore;
        private final DeckClickListener listener;

        public DeckViewHolder(@NonNull View itemView,DeckClickListener listener) {
            super(itemView);
            this.listener = listener;
            name = itemView.findViewById(R.id.txt_deck_name);
            date = itemView.findViewById(R.id.txt_date);
            cardCount = itemView.findViewById(R.id.txt_card_count);
            deckItem = itemView.findViewById(R.id.deck_item);
            imgMore = itemView.findViewById(R.id.img_more);

            deckItem.setOnClickListener(this);
            imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopUpMenu(view);
                }
            });
        }

        @Override
        public void onClick(View view) {


                int pos = getBindingAdapterPosition();
                listener.onDeckClick(getSnapshots().getSnapshot(pos));


        }

         private void showPopUpMenu(View view) {
             PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
             popupMenu.inflate(R.menu.deck_pop_up);
             popupMenu.setOnMenuItemClickListener(this);
             popupMenu.show();
         }

         @Override
         public boolean onMenuItemClick(MenuItem item) {
            if(item.getItemId() == R.id.item_publish_deck)
            {
                int pos = getBindingAdapterPosition();
                listener.onPublishDeck(getSnapshots().getSnapshot(pos));
                return true;
            }
             return false;
         }
     }

    public interface DeckClickListener{
        void onDeckClick(DocumentSnapshot snapshot);

        void onPublishDeck(DocumentSnapshot snapshot);
    }
}


