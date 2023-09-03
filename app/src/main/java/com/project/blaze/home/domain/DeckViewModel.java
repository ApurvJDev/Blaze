package com.project.blaze.home.domain;

import androidx.lifecycle.ViewModel;

import com.project.blaze.home.repo.DeckRepo;

public class DeckViewModel extends ViewModel {

    private final DeckRepo deckRepo = new DeckRepo();

    public void createDeck(String deckName, String date, int count){
        deckRepo.setDeckDetails(deckName,date,count);
    }
}
