package com.project.blaze.home.dto;

import com.google.firebase.firestore.Exclude;

public class DeckModel {

    @Exclude
    private String deckId;
    private String deckName;
    private String dateCreated;
    private Integer flashcardCount;

    private String email;

    public DeckModel() {
    }

    public DeckModel(String deckId, String deckName, String dateCreated, Integer flashcardCount) {
        this.deckId = deckId;
        this.deckName = deckName;
        this.dateCreated = dateCreated;
        this.flashcardCount = flashcardCount;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public String getDeckName() {
        return deckName;
    }

    public void setDeckName(String deckName) {
        this.deckName = deckName;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Integer getFlashcardCount() {
        return flashcardCount;
    }

    public void setFlashcardCount(Integer flashcardCount) {
        this.flashcardCount = flashcardCount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
