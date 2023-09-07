package com.project.blaze.home.repo;

import static com.project.blaze.auth.domain.CreateUserProfile.USERS;
import static com.project.blaze.home.repo.DeckRepo.DECKS;
import static com.project.blaze.home.repo.FlashcardRepo.BASE_PATH;
import static com.project.blaze.home.repo.FlashcardRepo.FLASHCARDS;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.blaze.home.dto.FlashcardModel;

import java.util.Objects;

public class FlashCardRetrieveRepo {
    public static final String TAG = "FlashcardRetrieveRepo";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private OnFlashcardRetrievedListener listener;
    private String email;
    private String deckId, flashcardId;

    public FlashCardRetrieveRepo() {
    }

    public void setListener(OnFlashcardRetrievedListener listener) {
        this.listener = listener;
    }


    public void setEmail() {
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public void setFlashcardId(String flashcardId) {
        this.flashcardId = flashcardId;
    }

    public void getFlashCard()
    {
        db.collection(USERS).document(email).collection(DECKS).document(deckId)
                .collection(FLASHCARDS).document(flashcardId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists())
                        {
                            Log.d(TAG, "Flashcard Retrieved!");
                            FlashcardModel flashcard = documentSnapshot.toObject(FlashcardModel.class);
                            listener.onRetrieved(flashcard);


                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public void updateFlashCard(FlashcardModel flashcard)
    {
        db.collection(USERS).document(email).collection(DECKS).document(deckId)
                .collection(FLASHCARDS).document(flashcardId).set(flashcard).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "Flashcard Updated!");
                        listener.onUpdate(true);
                    }
                }).addOnFailureListener(e -> {
                    Log.d(TAG, e.toString());
                    listener.onUpdate(false);
                });
    }

    public void updateImage(Uri imageUri)
    {
        StorageReference  myStorageRef = storage.getReference(BASE_PATH).child(email).child(deckId);
        StorageReference fileReference = myStorageRef.child(flashcardId);
        fileReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Log.d(TAG, "Flashcard with image updates");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, e.toString());
                    }
                });
    }

    public StorageReference getImage()
    {
        return storage.getReference(BASE_PATH).child(email).child(deckId).child(flashcardId);
    }

    public interface OnFlashcardRetrievedListener
    {
        void onRetrieved(FlashcardModel card);

        void onUpdate(boolean success);

    }











}
