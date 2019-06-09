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

    private EditText name,phone,room,rent;
    private Button save;
    private DatabaseReference tenantDatabase;
    private Tenant tenant;
    private Tenant.TenantDetails details;

    private DatabaseReference notOnBoardDB;
    private DatabaseReference onBoardDB;
    private DatabaseReference tenants;
    private DatabaseReference pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tenant);

        init();
    }

    private void init() {

        onBoardDB=Databases.getOnBoardDB();
        notOnBoardDB=Databases.getNotOnBoardDB();
        tenants=Databases.getTenantsDB();
        pg=Databases.getPgDB();
        name=findViewById(R.id.name_edittext);
        phone=findViewById(R.id.phone_edittext);
        room=findViewById(R.id.room);
        rent=findViewById(R.id.rentAmt);
        save=findViewById(R.id.save);

        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.save:
                addtenant();
                break;
        }
    }


    private void addtenant() {
        //create new tenant and push it in the database
        String name=this.name.getText().toString();
        String phone="+91"+this.phone.getText().toString();
        String room=this.room.getText().toString();
        String rentamt=this.rent.getText().toString();

        if(name.isEmpty()){
            this.name.setError("Name can not be empty!");
            this.name.requestFocus();
            return;
        }
        if (phone.isEmpty()||phone.length()<12){
            this.phone.setError("Please add valid phone number!");
            this.phone.requestFocus();
            return;
        }
        if(room.isEmpty()){
            this.room.setError("value can not be empty!");
            this.room.requestFocus();
            return;
        }
        if(rentamt.isEmpty()){
            this.rent.setError("Value can not be empty|");
            this.rent.requestFocus();
            return;
        }

        details=new Tenant.TenantDetails(name,phone,room,rentamt);
        tenant=new Tenant(phone,details);

        String id=notOnBoardDB.child(phone).setValue(tenant).toString();
        tenant.setId(id);

        if(id!=null)
        Toast.makeText(getApplicationContext(),id+" tenant added successfully!",Toast.LENGTH_LONG).show();
        finish();
    }
}
