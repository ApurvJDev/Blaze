package com.project.blaze.home.repo;

import static com.project.blaze.auth.domain.CreateUserProfile.USERS;
import static com.project.blaze.home.presentation.ReviewFragment.AGAIN;
import static com.project.blaze.home.repo.DeckRepo.DECKS;
import static com.project.blaze.home.repo.FlashcardRepo.FLASHCARDS;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.project.blaze.home.dto.FlashcardModel;
import com.project.blaze.home.helper.MyIntentBuilder;
import com.project.blaze.home.helper.SMTwo;

import java.util.Objects;

public class ReviewRepo {
    public static final String TAG = "ReviewRepo";
    public static final String QUEUE = "Queue";
    public static final String METADATA = "Metadata";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String email, recallRating,flashcardId, deckId;
    private Long nextInterval = -1L;
    private  OnScheduleListener listener;
    private boolean graduated;
    private FlashcardModel flashcard;
    private boolean modifyInterval = true;
    private Context context;


    public ReviewRepo() {
    }

    public  void setEmail()
    {
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();

    }

    public void setListener(OnScheduleListener listener) {
        this.listener = listener;
    }

    public void setRecallRating(String recallRating) {
        this.recallRating = recallRating;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    //


    public void setContext(Context context) {
        this.context = context;
    }

    public void setModifyInterval(boolean modifyInterval) {
        this.modifyInterval = modifyInterval;
    }

    public FlashcardModel getFlashcard() {
        return flashcard;
    }

    public void beginReview(FlashcardModel card)
    {
        if(flashcard==null)
        {
            if(modifyInterval && card.getNextReview()==-1)
            {
                scheduleCard(card);
            }
            else {
                rescheduleCard(card);
            }
        }
        else {
            if(modifyInterval && flashcard.getNextReview()==-1)
            {
                scheduleCard(card);
            }
            else {
                rescheduleCard(card);
            }
        }

    }

    //add to queue on 1st review
    public void scheduleCard(FlashcardModel card)
    {
        Log.d(TAG, "First review and schedule");
        flashcardId = card.getId();
        flashcard = card;
        SMTwo.ReviewResult reviewResult = runSM2Algo(false,recallRating,AGAIN,card.getNextReview(),card.getEaseFactor());
        graduated = false;
        nextInterval = reviewResult.getTimeMinutes();
        flashcard.setRecallAbility(recallRating);
        flashcard.setNextReview(nextInterval);
        flashcard.setGraduated(false);
        flashcard.setEaseFactor(reviewResult.getEasinessFactor());

        updateFlashCard();


    }

    //rescheduling the card
    public void rescheduleCard(FlashcardModel card)
    {
        Log.d(TAG, "Rescheduled");
        flashcardId = card.getId();
        flashcard = card;
        SMTwo.ReviewResult reviewResult = runSM2Algo(card.isGraduated(),recallRating,card.getRecallAbility(), card.getNextReview(), card.getEaseFactor());
        graduated = reviewResult.isGraduated();
        nextInterval = reviewResult.getTimeMinutes();
        flashcard.setRecallAbility(recallRating);
        flashcard.setNextReview(nextInterval);
        flashcard.setGraduated(graduated);
        flashcard.setEaseFactor(reviewResult.getEasinessFactor());

        updateFlashCard();

    }

    public void saveMetadata (FlashcardModel card)
    {
        db.collection(USERS).document(email).collection(METADATA).document(card.getId()).get().addOnSuccessListener(metaDocumentSnapshot -> {
            if (metaDocumentSnapshot.exists() && modifyInterval) {
                db.collection(USERS).document(email).collection(QUEUE).document(card.getId()).get().addOnSuccessListener(queueDocumentSnapshot -> {
                    if (queueDocumentSnapshot.exists()) {
                        deleteFromQueue(card);
                        Log.d(TAG, "Removed card from queue");
                    }
                    updateCardSchedule(metaDocumentSnapshot.toObject(FlashcardModel.class));
                    createUpdateMetadata(card);

                });


            } else {
                createUpdateMetadata(card);
            }
        });
    }

    private void createUpdateMetadata(FlashcardModel card)
    {
        db.collection(USERS).document(email).collection(METADATA).document(card.getId()).set(card).addOnSuccessListener(unused ->
                Log.d(TAG, "Metadata added")).addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }

    public void deleteMetadata(FlashcardModel card)
    {
        db.collection(USERS).document(email).collection(METADATA).document(card.getId()).delete().addOnSuccessListener(unused ->
                Log.d(TAG, card.getQuestion() + " Metadata deleted"));
    }

    private void updateCardSchedule(FlashcardModel card)
    {
        //cancel previous alarms
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        MyIntentBuilder intentBuilder = new MyIntentBuilder(context);
        Intent intent = intentBuilder.buildIntentWithExtras(card);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,card.getPid(),intent,0);
        alarmManager.cancel(pendingIntent);
        Log.d(TAG, "Previous alarms cancelled");

    }

    private SMTwo.ReviewResult runSM2Algo(boolean graduated, String Tag, String previousLearningTag, long previousMinutes, double previousEasinessfactor) {
        SMTwo smTwo = new SMTwo();
        return  smTwo.reviewCard(graduated,Tag,previousLearningTag,previousMinutes,previousEasinessfactor);
    }


    //update the flashcard
    private void updateFlashCard()
    {
        if(email==null)
        {
            email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        }
        db.collection(USERS).document(email).collection(DECKS).document(deckId)
                .collection(FLASHCARDS).document(flashcard.getId()).set(flashcard).addOnSuccessListener(unused -> {
                    Log.d(TAG, "Flashcard Review detail updated!");
                    listener.onScheduleReady(nextInterval,graduated);

                }).addOnFailureListener(e -> Log.d(TAG, e.toString()));
    }

    //add to queue
    public void addToQueue(FlashcardModel qFlashcard)
    {
        email = Objects.requireNonNull(mAuth.getCurrentUser()).getEmail();
        DocumentReference queueRef = db.collection(USERS).document(email).collection(QUEUE).document(qFlashcard.getId());
        DocumentReference flashRef =  db.collection(USERS).document(email).collection(DECKS).document(qFlashcard.getDeckId())
                .collection(FLASHCARDS).document(qFlashcard.getId());
        flashRef.get().addOnSuccessListener(documentSnapshot -> {
            FlashcardModel latestCard = documentSnapshot.toObject(FlashcardModel.class);
            assert latestCard != null;
            queueRef.set(latestCard).addOnSuccessListener(unused ->
                    Log.d(TAG, "Card added to queue successfully")).addOnFailureListener(e -> Log.d(TAG, e.toString()));
        });


    }

    public void deleteFromQueue(FlashcardModel card)
    {
        db.collection(USERS).document(email).collection(QUEUE).document(card.getId()).delete().addOnSuccessListener(unused ->
                Log.d(TAG, card.getQuestion() + " from Queue deleted"));
    }



    //remove from queue on review


    public interface OnScheduleListener
    {

        void onScheduleReady(Long nextInterval, boolean graduated);
    }

}
