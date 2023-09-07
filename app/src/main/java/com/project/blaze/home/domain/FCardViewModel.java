package com.project.blaze.home.domain;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.storage.StorageReference;
import com.project.blaze.home.dto.FlashcardModel;
import com.project.blaze.home.repo.FlashCardRetrieveRepo;

public class FCardViewModel extends ViewModel {
    private String deckId;
    private final FlashCardRetrieveRepo retrieveRepo = new FlashCardRetrieveRepo();
    private final MutableLiveData<FlashcardModel> flashcardLive = new MutableLiveData<>();
    private Uri imageUri = null;


    public  void setListener(FlashCardRetrieveRepo.OnFlashcardRetrievedListener listener)
    {
        retrieveRepo.setListener(listener);
    }


    public void setFlashcardLive(FlashcardModel flashcard)
    {
        flashcardLive.setValue(flashcard);
    }

    public void setEmail()
    {
        retrieveRepo.setEmail();
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
        retrieveRepo.setDeckId(deckId);

    }
    public void setFlashcardId(String flashcardId)
    {
        retrieveRepo.setFlashcardId(flashcardId);
    }

    public void retrieveFlashcard()
    {
        retrieveRepo.getFlashCard();
    }

    public void setImageUri(Uri uri)
    {
        imageUri = uri;
    }

    public void updateFlashcard(FlashcardModel flashcard)
    {
        retrieveRepo.updateFlashCard(flashcard);
        if(imageUri!=null)
            updateImage();
    }

    public void updateImage()
    {
        retrieveRepo.updateImage(imageUri);
    }



    public StorageReference getImage()
    {
        return retrieveRepo.getImage();
    }

    public LiveData<FlashcardModel> getFlashcardLive() {
        return flashcardLive;
    }

    public String getDeckId() {
        return deckId;
    }
}
