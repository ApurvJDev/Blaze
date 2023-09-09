package com.project.blaze.home.domain;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.blaze.home.dto.FlashcardModel;
import com.project.blaze.home.repo.ReviewRepo;

public class ReviewViewModel extends ViewModel {
   private final ReviewRepo reviewRepo = new ReviewRepo();
   private final MutableLiveData<FlashcardModel> flashcardFromQueueLive = new MutableLiveData<>();


   public void setFlashcardFromQueueLive(FlashcardModel flashcard)
   {
       flashcardFromQueueLive.setValue(flashcard);
   }

   public void setEmail()
   {
       reviewRepo.setEmail();
   }

   public void setListener(ReviewRepo.OnScheduleListener listener)
   {
       reviewRepo.setListener(listener);
   }

   public void setContext(Context context)
   {
       reviewRepo.setContext(context);
   }

   public void setRecallRating(String rating)
   {
       reviewRepo.setRecallRating(rating);
   }


   public void setDeckId(String deckId)
   {
       reviewRepo.setDeckId(deckId);
   }

   public void setModifyInterval(boolean modify)
   {
       reviewRepo.setModifyInterval(modify);
   }

   public void saveMetadata(FlashcardModel flashcard)
   {
       reviewRepo.saveMetadata(flashcard);
   }

   public void deleteMetadata(FlashcardModel flashcard){
       reviewRepo.deleteMetadata(flashcard);
   }
    public void deleteFromQueue(FlashcardModel flashcard)
    {
        reviewRepo.deleteFromQueue(flashcard);
    }



   public void beginReview(FlashcardModel flashCard)
   {
       reviewRepo.beginReview(flashCard);
   }

   public FlashcardModel getFlashcard()
   {
       return reviewRepo.getFlashcard();
   }

    public LiveData<FlashcardModel> getFlashcardFromQueueLive() {
        return flashcardFromQueueLive;
    }
}
