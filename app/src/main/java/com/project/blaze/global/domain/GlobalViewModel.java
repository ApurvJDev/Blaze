package com.project.blaze.global.domain;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.storage.StorageReference;
import com.project.blaze.global.repo.GlobalRepo;
import com.project.blaze.home.dto.DeckModel;
import com.project.blaze.home.dto.FlashcardModel;

public class GlobalViewModel extends ViewModel {
    private final GlobalRepo globalRepo = new GlobalRepo();
    private final MutableLiveData<DeckModel> viewDeckModelLive = new MutableLiveData<>();
    private String creatorEmail;
    private final MutableLiveData<FlashcardModel> viewFlashcardLive = new MutableLiveData<>();
    public void setEmail()
    {
        globalRepo.setEmail();
    }

    public void setListeners(GlobalRepo.OnDeckGlobalisedListener deckGlobalisedListener, GlobalRepo.OnDeckImportedListener importedListener)
    {
        globalRepo.setListeners(deckGlobalisedListener,importedListener);
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public void setViewDeckModelLive(DeckModel deckModel)
    {
        viewDeckModelLive.setValue(deckModel);
    }

    public void setViewFlashcardLive(FlashcardModel flashcardModel)
    {
        viewFlashcardLive.setValue(flashcardModel);
    }
    public void publishAndGlobalise(DeckModel deck)
    {
        globalRepo.publishAndGlobalise(deck);
    }
    public void importDeck(DeckModel deck)
    {
        //increment the import count
        deck.setImportCount(deck.getImportCount()+1);
        globalRepo.importDeck(deck);
    }

    public LiveData<DeckModel> getViewDeckModelLive() {
        return viewDeckModelLive;
    }

    public LiveData<FlashcardModel> getViewFlashcardLive() {
        return viewFlashcardLive;
    }

    public StorageReference getImage(FlashcardModel flashcard)
    {
        return  globalRepo.getImage(flashcard,creatorEmail);
    }
}
