package com.example.easypg;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public abstract class Databases {
    private DatabaseReference pgDB;
    private DatabaseReference tenantsDB;
    private DatabaseReference onBoardDB;
    private DatabaseReference notOnBoardDB;

    StorageReference storageReference;

    public DatabaseReference getPgDB() {
        if(pgDB==null)
            pgDB= FirebaseDatabase.getInstance().getReference("PG");
        return pgDB;
    }

    public DatabaseReference getTenantsDB() {
        if(tenantsDB==null)
            tenantsDB= FirebaseDatabase.getInstance().getReference("Tenants");
        return tenantsDB;
    }

    public DatabaseReference getOnBoardDB() {
        if(onBoardDB==null)
            onBoardDB= FirebaseDatabase.getInstance().getReference("PG").child("0").child("OnBoardTenants");
        return onBoardDB;
    }

    public DatabaseReference getNotOnBoardDB() {
        if(notOnBoardDB==null)
            notOnBoardDB= FirebaseDatabase.getInstance().getReference("PG").child("0").child("NotOnBoardTenants");
        return notOnBoardDB;
    }

    public StorageReference getStorageReference() {
        if (storageReference==null)
            storageReference= FirebaseStorage.getInstance().getReference();
        return storageReference;
    }
}
