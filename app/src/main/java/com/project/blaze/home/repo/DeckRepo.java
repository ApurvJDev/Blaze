package com.project.blaze.home.repo;

import static com.project.blaze.auth.domain.CreateUserProfile.USERS;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.blaze.home.dto.DeckModel;

import java.util.Objects;

public class DeckRepo {


    public static final String DECKS = "Decks";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private  final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public static final String TAG = "DeckRepo";
    private String deckName, date;
    private final String email;
    private int cardCount;

    public DeckRepo() {
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
    }
    public void setDeckDetails(String deckName,String date, int cardCount)
    {
        this.deckName = deckName;
        this.date = date;
        this.cardCount = cardCount;
        createDeck();
    }

    private void createDeck()
    {

        DocumentReference deckRef = db.collection(USERS).document(email).collection(DECKS).document();
        DeckModel deck = new DeckModel();
        deck.setDeckId(deckRef.getId());
        deck.setDeckName(deckName);
        deck.setDateCreated(date);
        deck.setFlashcardCount(cardCount);
        deckRef.set(deck).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Deck created successfully!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.d(TAG, e.toString());
            }
        });
    }

}
