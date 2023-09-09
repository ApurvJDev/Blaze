package com.project.blaze.home.helper;

import android.content.Context;
import android.content.Intent;

import com.project.blaze.AlertReceiver;
import com.project.blaze.home.dto.FlashcardModel;

public class MyIntentBuilder {
    private Intent intent;
    private final Context context;
    public static final String ID = "id";
    public static final String PID = "pid";
    public static final String DECK_ID = "deckId";
    public static final String QUESTION = "question";
    public static final String ANS = "answer";
    public static final String MCQ = "mcq";
    public static final String OPTIONS = "options";
    public static final String HAS_IMAGE = "hasImage";
    public static final String RECALL_ABILITY = "recallAbility";
    public static final String NEXT_REVIEW = "nextReview";
    public static final String GRADUATED = "graduated";
    public static final String EASE_FACTOR = "easeFactor";

    public MyIntentBuilder(Context context) {
        this.context = context;
    }

    public Intent buildIntentWithExtras(FlashcardModel card)
    {
        intent = new Intent(context, AlertReceiver.class);
        intent.putExtra(ID,card.getId());
        intent.putExtra(PID,card.getPid());
        intent.putExtra(DECK_ID,card.getDeckId());
        intent.putExtra(QUESTION,card.getQuestion());
        intent.putExtra(ANS,card.getAnswer());
        intent.putExtra(MCQ,card.isMcq());
        intent.putExtra(OPTIONS,card.getOptionsList());
        intent.putExtra(HAS_IMAGE,card.isHasImage());
        intent.putExtra(RECALL_ABILITY,card.getRecallAbility());
        intent.putExtra(NEXT_REVIEW,card.getNextReview());
        intent.putExtra(GRADUATED,card.isGraduated());
        intent.putExtra(EASE_FACTOR,card.getEaseFactor());
        return intent;
    }

    public FlashcardModel getFlashcardToSchedule(Intent i)
    {
        FlashcardModel flashcard = new FlashcardModel();
        flashcard.setId(i.getStringExtra(ID));
        flashcard.setPid(i.getIntExtra(PID,0));
        flashcard.setDeckId(i.getStringExtra(DECK_ID));
        flashcard.setQuestion(i.getStringExtra(QUESTION));
        flashcard.setAnswer(i.getStringExtra(ANS));
        flashcard.setMcq(i.getBooleanExtra(MCQ,false));
        flashcard.setOptionsList(i.getStringExtra(OPTIONS));
        flashcard.setHasImage(i.getBooleanExtra(HAS_IMAGE,false));
        flashcard.setRecallAbility(i.getStringExtra(RECALL_ABILITY));
        flashcard.setNextReview(i.getLongExtra(NEXT_REVIEW,-1));
        flashcard.setGraduated(i.getBooleanExtra(GRADUATED,false));
        flashcard.setEaseFactor(i.getDoubleExtra(EASE_FACTOR,2.5));

        return flashcard;
    }

}
