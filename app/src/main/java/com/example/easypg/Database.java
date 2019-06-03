package com.example.easypg;

import com.google.firebase.database.FirebaseDatabase;

public abstract class Database {

    @org.jetbrains.annotations.Contract(pure = true)
    public static FirebaseDatabase getDB() {
        return FirebaseDatabase.getInstance();
    }
}
