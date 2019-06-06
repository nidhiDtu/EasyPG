package com.example.easypg;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddTenantActivity extends AppCompatActivity implements View.OnClickListener {

    EditText name,phone,room,rent;
    Button save;
    DatabaseReference database;
    DatabaseReference tenantDatabase;
    String id="";
    Tenant tenant;
    Tenant.TenantDetails details;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tenant);

        Intent intent=getIntent();
        id=intent.getStringExtra("phone");

        init();
    }

    private void init() {

        database=FirebaseDatabase.getInstance().getReference("PG").child("0").child("NotOnBoardTenants");
        name=findViewById(R.id.name_edittext);
        phone=findViewById(R.id.phone_edittext);
        room=findViewById(R.id.room);
        rent=findViewById(R.id.rentAmt);
        save=findViewById(R.id.save);

        save.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(id!=null){
            tenantDatabase=database.child(id);

            tenantDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(!id.isEmpty()){
                        tenant=dataSnapshot.getValue(Tenant.class);
                        if(tenant!=null && tenant.getDetails()!=null){
                            name.setText(tenant.getDetails().getName());
                            phone.setText(tenant.getDetails().getPhone());
                            room.setText(tenant.getDetails().getRoom());
                            rent.setText(tenant.getDetails().getRentAmount());
                        }else{
                            Toast.makeText(AddTenantActivity.this,"tenant is null",Toast.LENGTH_LONG).show();
                        }

                    }else{
                        Toast.makeText(AddTenantActivity.this,"id is null",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(AddTenantActivity.this,"The read failed: " + databaseError.getCode(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {



        switch (view.getId()){
            case R.id.save:
                if (tenant!=null){
                    updateTenant();
                    finish();
                }else{
                    addtenant();
                    finish();
                }
                break;
        }
    }

    private void updateTenant() {
        //update new tenant and push it in the database
        String name=this.name.getText().toString();
        String phone=this.phone.getText().toString();
        String room=this.room.getText().toString();
        String rentamt=this.rent.getText().toString();

        tenantDatabase=database.child(id);

        Tenant.TenantDetails tenantDetails=new Tenant.TenantDetails();
        tenantDetails.setName(name);
        tenantDetails.setPhone(phone);
        tenantDetails.setRoom(room);
        tenantDetails.setRentAmount(rentamt);

        Tenant tenant1=new Tenant(tenantDetails);

        tenantDatabase.removeValue();
        String id=tenantDatabase.setValue(tenant1).toString();
        tenant.setId(id);

        if(id!=null)
        Toast.makeText(getApplicationContext(),id+" tenant updated successfully!",Toast.LENGTH_LONG).show();
        Intent intent=new Intent();
        intent.putExtra("phone",phone);
        setResult(4,intent);
        finish();
    }

    private void addtenant() {
        //create new tenant and push it in the database
        String name=this.name.getText().toString();
        String phone="+91"+this.phone.getText().toString();
        String room=this.room.getText().toString();
        String rentamt=this.rent.getText().toString();

        details=new Tenant.TenantDetails(name,phone,room,rentamt);
        tenant=new Tenant(details);

        String id=database.child(phone).setValue(tenant).toString();
        tenant.setId(id);

        if(id!=null)
        Toast.makeText(getApplicationContext(),id+" tenant added successfully!",Toast.LENGTH_LONG).show();
        Intent intent=new Intent();
        setResult(3,intent);
        finish();
    }
}
