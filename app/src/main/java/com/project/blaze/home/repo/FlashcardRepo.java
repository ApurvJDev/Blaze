package com.project.blaze.home.repo;

import static com.project.blaze.auth.domain.CreateUserProfile.USERS;
import static com.project.blaze.home.repo.DeckRepo.DECKS;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.blaze.home.dto.DeckModel;
import com.project.blaze.home.dto.FlashcardModel;

import java.util.Objects;

public class FlashcardRepo {

    public static final String TAG = "FlashcardRepo";
    public static final String FLASHCARDS = "Flashcards";
    public static final String BASE_PATH = "Users";
    private  String email;
    private boolean flashCardAdded;
    private FlashcardModel flashcardModel;
    private DeckModel deck;
    private String deckId;
    private Uri imageUri = null;

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference myStorageRef;
    private OnSuccessfulListener listener;

    public FlashcardRepo() {


    }

    public void setListener(OnSuccessfulListener listener)
    {
        this.listener = listener;
    }

    public void setEmail()
    {
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
    }

    public void setDeckId(String deckId)
    {
        this.deckId = deckId;
        myStorageRef = storage.getReference(BASE_PATH).child(email).child(deckId);
    }

    public void uploadImg(Uri uri) {
        imageUri = uri;
    }

    public  void createFlashCard(FlashcardModel flashCard)
    {
        flashcardModel = flashCard;
        flashcardModel.setDeckId(deckId);
        flashCardAdded = false;

        DocumentReference flashCardRef = db.collection(USERS).document(email).collection(DECKS)
                .document(deckId).collection(FLASHCARDS).document();
        flashCard.setId(flashCardRef.getId());
        flashCardRef.set(flashCard).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "FlashCard added successfully");
                updateDeck();  // Increment the flashcard count of the deck
                if(listener!=null)
                    listener.onSuccess(true);
                flashCardAdded = true;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onSuccess(false);
                Log.d(TAG, e.toString());
            }
        });

        if(imageUri!=null && flashcardModel.isHasImage())
        {
            uploadImage();
        }
    }

    private void uploadImage()
    {
        if(imageUri!=null)
        {
            StorageReference fileReference = myStorageRef.child(flashcardModel.getId());
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                Log.d(TAG, "Flashcard with image created!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    });
        }
    }

    public void updateDeck()
    {

        DocumentReference deckRef = db.collection(USERS).document(email).collection(DECKS)
                .document(deckId);

            deckRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    deck = documentSnapshot.toObject(DeckModel.class);
                    assert deck != null;
                    deck.setFlashcardCount(deck.getFlashcardCount() + 1);
                    deckRef.set(deck).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "Deck updated ");
                        }
                    });
                }
            });



    }


    public interface OnSuccessfulListener
    {
        void onSuccess(boolean success);
    }
}
