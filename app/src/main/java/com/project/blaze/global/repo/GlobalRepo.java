package com.project.blaze.global.repo;

import static com.project.blaze.auth.domain.CreateUserProfile.USERS;
import static com.project.blaze.home.repo.DeckRepo.DECKS;
import static com.project.blaze.home.repo.FlashcardRepo.BASE_PATH;
import static com.project.blaze.home.repo.FlashcardRepo.FLASHCARDS;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.project.blaze.home.dto.DeckModel;
import com.project.blaze.home.dto.FlashcardModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class GlobalRepo {

    public static final String TAG = "GlobalRepo";
    public static final String PUBLISHED = "Published decks";
    public static final String GLOBAL = "Global";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private String email;
    private CollectionReference deckRef, publishedDeckRef;
    private OnDeckGlobalisedListener globalisedListener;
    private OnDeckImportedListener importedListener;


    public void setEmail() {
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
    }

    //set listeners
    public void setListeners(OnDeckGlobalisedListener globalisedListener, OnDeckImportedListener importedListener)
    {
        this.globalisedListener  = globalisedListener;
        this.importedListener = importedListener;
    }


    public void publishAndGlobalise(DeckModel deckModel)
   {


       deckRef =  db.collection(USERS).document(email).collection(DECKS).document(deckModel.getDeckId()).collection(FLASHCARDS);
       publishedDeckRef = db.collection(USERS).document(email).collection(PUBLISHED);
       deckRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
           @Override
           public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
               Log.d(TAG, deckModel.getDeckName() + " deck Fetched");
               publishTheDeck(deckModel);
               for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots)
               {
                   FlashcardModel flashcard = documentSnapshot.toObject(FlashcardModel.class);
                   publishFlashcards(flashcard);
               }
               globaliseDeck(deckModel);
               globalisedListener.onDeckGlobalisedListener(true);
               
               
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Log.d(TAG, e.toString());
           }
       });
   }

   private void publishTheDeck(DeckModel deckModel)
   {
       publishedDeckRef.document(deckModel.getDeckId()).set(deckModel).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void unused) {
               Log.d(TAG,  deckModel.getDeckName() + "published");

           }
       });

   }
   private void publishFlashcards(FlashcardModel flashcard)
   {

       publishedDeckRef.document(flashcard.getDeckId()).collection(FLASHCARDS).document(flashcard.getId()).set(flashcard)
               .addOnSuccessListener(new OnSuccessListener<Void>() {
                   @Override
                   public void onSuccess(Void unused) {
                       Log.d(TAG, flashcard.getQuestion() + "Added to published");
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Log.d(TAG, e.toString());
                   }
               });
   }

   private void globaliseDeck(DeckModel deckModel)
   {
       if(email==null)
         email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

       db.collection(GLOBAL).document(GLOBAL+deckModel.getDeckId()).set(deckModel).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void unused) {
               Log.d(TAG, deckModel.getDeckName() + " deck Globalised");


           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               Log.d(TAG, e.toString());
               globalisedListener.onDeckGlobalisedListener(false);
           }
       });
   }

   public void importDeck(DeckModel deckModel)
   {
       //update import count globally
       globaliseDeck(deckModel);
       if(email==null)
       {
           email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
       }
       //get deck
       db.collection(USERS).document(deckModel.getEmail()).collection(PUBLISHED).document(deckModel.getDeckId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
             @Override
             public void onSuccess(DocumentSnapshot documentSnapshot) {
                 DeckModel importedDeck = documentSnapshot.toObject(DeckModel.class);
                 assert importedDeck != null;
                 Date currentDate = new Date();
                 SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
                 String date = sdf.format(currentDate);

                 importedDeck.setEmail(email);
                 importedDeck.setDateCreated(date);

                 //import deck
                 db.collection(USERS).document(email).collection(DECKS).document(deckModel.getDeckId()).set(importedDeck).addOnSuccessListener(new OnSuccessListener<Void>() {
                     @Override
                     public void onSuccess(Void unused) {
                         Log.d(TAG, importedDeck.getDeckName() + " imported");
                         beginImport(deckModel);
                     }
                 });
             }
         });
   }

    private void updateGlobal(DeckModel deckModel) {
        if(email==null)
            email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

        db.collection(GLOBAL).document().update("importCount",deckModel.getImportCount()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, deckModel.getDeckName() + " deck import count incremented");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }

    //import the deck
    private void beginImport(DeckModel deckModel)
    {
        CollectionReference thirdPartyRef = db.collection(USERS).document(deckModel.getEmail()).collection(PUBLISHED).document(deckModel.getDeckId()).collection(FLASHCARDS);
        //import flashcards in imported deck
        thirdPartyRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d(TAG, deckModel.getDeckName() + " deck Fetched");
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots)
                {
                    FlashcardModel flashcard = documentSnapshot.toObject(FlashcardModel.class);
                    importFlashcard(flashcard,deckModel);

                }
                //when the deck and all the flashcards in it are imported
                importedListener.onDeckImportedListener(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
                importedListener.onDeckImportedListener(false);
            }
        });

    }

    private void importFlashcard(FlashcardModel flashcard, DeckModel deckModel)
    {
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        if(flashcard.isHasImage())importImage(flashcard,deckModel);
        //reset flashcard
        flashcard.setGraduated(false);
        flashcard.setNextReview(-1);

        db.collection(USERS).document(email).collection(DECKS).document(flashcard.getDeckId()).collection(FLASHCARDS).document(flashcard.getId())
                .set(flashcard).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, flashcard.getQuestion() + "imported");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, e.toString());
            }
        });
    }

    private void importImage(FlashcardModel flashcard,DeckModel deckModel) {
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        StorageReference source = storage.getReference(BASE_PATH).child(deckModel.getEmail()).child(flashcard.getDeckId()).child(flashcard.getId());
        StorageReference destination = storage.getReference(BASE_PATH).child(email).child(flashcard.getDeckId()).child(flashcard.getId());

        final long ONE_MEGABYTE = 1024 * 1024;
        source.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                destination.putBytes(bytes).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "Image imported");
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, exception.toString());
            }
        });

    }

    public StorageReference getImage(FlashcardModel flashcard, String creatorEmail)
    {
        return storage.getReference(BASE_PATH).child(creatorEmail).child(flashcard.getDeckId()).child(flashcard.getId());
    }

    public interface  OnDeckGlobalisedListener
    {
        void onDeckGlobalisedListener(boolean success);
    }

    public interface OnDeckImportedListener
    {
        void onDeckImportedListener(boolean success);
    }




}
