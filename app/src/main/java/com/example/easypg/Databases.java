package com.example.easypg;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public abstract class Databases {
    private static DatabaseReference pgDB;
    private static DatabaseReference tenantsDB;
    private static DatabaseReference onBoardDB;
    private static DatabaseReference notOnBoardDB;

    private static StorageReference storageReference;

    public static DatabaseReference getPgDB() {
        if(pgDB==null)
            pgDB= FirebaseDatabase.getInstance().getReference("PG");
        return pgDB;
    }

    public static DatabaseReference getTenantsDB() {
        if(tenantsDB==null)
            tenantsDB= FirebaseDatabase.getInstance().getReference("Tenants");
        return tenantsDB;
    }

    public static DatabaseReference getOnBoardDB() {
        if(onBoardDB==null)
            onBoardDB= FirebaseDatabase.getInstance().getReference("PG").child("0").child("OnBoardTenants");
        return onBoardDB;
    }

    public static DatabaseReference getNotOnBoardDB() {
        if(notOnBoardDB==null)
            notOnBoardDB= FirebaseDatabase.getInstance().getReference("PG").child("0").child("NotOnBoardTenants");
        return notOnBoardDB;
    }

    public static StorageReference getStorageReference() {
        if (storageReference==null)
            storageReference= FirebaseStorage.getInstance().getReference();
        return storageReference;
    }
}
