package com.example.easypg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddTenantActivity extends AppCompatActivity implements View.OnClickListener {

    EditText name,phone,room,rent;
    Button save;
    DatabaseReference database;
    String id;
    Tenant tenant;
    Tenant.TenantDetails details;
    boolean create=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tenant);

        Intent intent=getIntent();
        id=intent.getStringExtra("id");

        init();
    }

    private void init() {
        database=FirebaseDatabase.getInstance().getReference("NotOnBoardTenants");
        name=findViewById(R.id.name_edittext);
        phone=findViewById(R.id.phone_edittext);
        room=findViewById(R.id.room);
        rent=findViewById(R.id.rentAmt);
        save=findViewById(R.id.save);
        if(id!=null){
            //get tenant from database and spread it on edittexts
            create=false;
        }else {
            create=true;
        }

        save.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save:
                if(create)
                addtenant();
                else updateTenant();
                break;
        }
    }

    private void updateTenant() {
    }

    private void addtenant() {
        //create new tenant and push it in the database
        String name=this.name.getText().toString();
        String phone=this.phone.getText().toString();
        String room=this.room.getText().toString();
        String rentamt=this.rent.getText().toString();

        details=new Tenant.TenantDetails(name,phone,room,rentamt);
        tenant=new Tenant(details);

        String id=database.child(phone).setValue(tenant).toString();
        tenant.setId(id);

        Toast.makeText(getApplicationContext(),"tenant added successfully!",Toast.LENGTH_LONG).show();
    }
}
