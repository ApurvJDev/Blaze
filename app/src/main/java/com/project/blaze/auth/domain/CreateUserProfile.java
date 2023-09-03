package com.project.blaze.auth.domain;

import com.google.firebase.firestore.FirebaseFirestore;

public class CreateUserProfile {
    public static final String USERS = "users";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final OnUserInit onUserInit;

    public CreateUserProfile(OnUserInit onUserInit) {
        this.onUserInit = onUserInit;
    }

    public void init(String email,String username)
    {
        Prof prof = new Prof(email,username);
        db.collection(USERS).document(email).set(prof).addOnSuccessListener(unused -> onUserInit.userRegistered());
    }
}
