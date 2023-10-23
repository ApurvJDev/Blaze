package com.project.blaze.home.dto;


import com.google.firebase.firestore.Exclude;

public class FlashcardModel {

    private String id;

    private int pid;
    private String deckId;
    private String question;
    private String answer;
    private boolean mcq;
    private String optionsList;
    private boolean hasImage;
    private String recallAbility = "";
    private long nextReview = -1;

    private boolean graduated;
    private double easeFactor = 2.5;


    public FlashcardModel() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isMcq() {
        return mcq;
    }

    public void setMcq(boolean mcq) {
        this.mcq = mcq;
    }

    public String getOptionsList() {
        return optionsList;
    }

    public void setOptionsList(String optionsList) {
        this.optionsList = optionsList;
    }

    public boolean isHasImage() {
        return hasImage;
    }

    public void setHasImage(boolean hasImage) {
        this.hasImage = hasImage;
    }


    public String getRecallAbility() {
        return recallAbility;
    }

    public void setRecallAbility(String recallAbility) {
        this.recallAbility = recallAbility;
    }

    public long getNextReview() {
        return nextReview;
    }

    public void setNextReview(long nextReview) {
        this.nextReview = nextReview;
    }

    public String getDeckId() {
        return deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public boolean isGraduated() {
        return graduated;
    }

    public void setGraduated(boolean graduated) {
        this.graduated = graduated;
    }

    public double getEaseFactor() {
        return easeFactor;
    }

    public void setEaseFactor(double easeFactor) {
        this.easeFactor = easeFactor;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

}
