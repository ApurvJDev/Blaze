package com.project.blaze.home.domain;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.blaze.home.dto.FlashcardModel;
import com.project.blaze.home.repo.FlashCardRetrieveRepo;
import com.project.blaze.home.repo.FlashcardRepo;

public class FlashCardViewModel extends ViewModel {

    private final FlashcardModel flashcardModel = new FlashcardModel();
    private final MutableLiveData<FlashcardModel> liveUi = new MutableLiveData<>();
    private final MutableLiveData<Uri> liveImgUri = new MutableLiveData<>();
    private final MutableLiveData<Boolean> flashcardSavedLive = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isUpdateLive = new MutableLiveData<>();

    private final FlashcardRepo flashcardRepo = new FlashcardRepo();



    public void setUpdateLive(boolean update)
    {
        isUpdateLive.setValue(update);
    }
    public void setOnSuccessfulListener(FlashcardRepo.OnSuccessfulListener listener)
    {
        flashcardRepo.setListener(listener);
    }

    public void setDeckId(String deckId)
    {
        flashcardRepo.setEmail();
        flashcardRepo.setDeckId(deckId);

    }

    public void setQuestion(String question)
    {
        flashcardModel.setQuestion(question);
    }
    public void setAnswer(String answer)
    {
        flashcardModel.setAnswer(answer);
    }
    public void setMCQ(boolean checked)
    {
        if(!checked)flashcardModel.setOptionsList(null);
        flashcardModel.setMcq(checked);
    }

    public void setHasImage(boolean bool)
    {
        flashcardModel.setHasImage(bool);
    }
    public void setLiveImgUri(Uri uri){
        liveImgUri.setValue(uri);

    }
    public void setMcqOptions(String mcqOptions)
    {
        flashcardModel.setOptionsList(mcqOptions);
    }

    public void setImage()
    {
        flashcardRepo.uploadImg(liveImgUri.getValue());
        flashcardModel.setHasImage(true);

    }

    public void resetImage()
    {
        flashcardRepo.uploadImg(liveImgUri.getValue());
        flashcardModel.setHasImage(false);
    }

    public void setFlashcardSaved()
    {
        flashcardSavedLive.setValue(true);

    }

    public void resetFlashcardSaved()
    {
        flashcardSavedLive.setValue(false);
    }
    public void createFlashcard()
    {
        flashcardRepo.createFlashCard(flashcardModel);
        flashcardSavedLive.setValue(false);
    }

    public FlashcardModel getFlashCard()
    {
        return flashcardModel;
    }

    public LiveData<FlashcardModel> getUiSate()
    {
        liveUi.setValue(flashcardModel);
        return liveUi;
    }

    public String getAnswer()
    {
        return flashcardModel.getAnswer();
    }
    public MutableLiveData<Uri> getLiveImgUri() {
        return liveImgUri;
    }

    public LiveData<Boolean> actionSave()
    {
        return  flashcardSavedLive;
    }

    public LiveData<Boolean> getIsUpdateLive() {
        return isUpdateLive;
    }
}
