package com.example.easypg;

import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class ShiftOfTenant {


    public static void copyTenantFromOnBoardToTenant(Tenant tenant) {
        /*
         * copying/ updating on onBoardTenants by value*/
        Tenant.TenantDetails tenantDetails=tenant.getDetails();

        DatabaseReference tenantRef=Databases.getOnBoardDB().child(tenant.getId());
        tenantRef.child("id").setValue(tenant.getId());
        DatabaseReference childTenant=tenantRef.child("details");
        childTenant.child("name").setValue(tenantDetails.getName());
        childTenant.child("phone").setValue(tenantDetails.getPhone());
        childTenant.child("room").setValue(tenantDetails.getRoom());
        childTenant.child("rentAmount").setValue(tenantDetails.getRentAmount());

//        /*
//        * copying/ updating on tenants by value*/
//        DatabaseReference tenantsRef=Databases.getTenantsDB().child(PhoneAuthActivity.uid).child("details");
//        tenantsRef.child("name").setValue(tenantDetails.getName());
//        tenantsRef.child("phone").setValue(tenantDetails.getPhone());
//        tenantsRef.child("room").setValue(tenantDetails.getRoom());
//        tenantsRef.child("rentAmount").setValue(tenantDetails.getRentAmount());
//        tenantsRef.child("pgId").setValue("0");

    }
}
