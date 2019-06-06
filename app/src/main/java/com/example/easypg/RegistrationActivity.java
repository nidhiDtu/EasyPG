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

import java.util.ArrayList;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    EditText name,phone,pgname,pincode,date;
    Button submit;

    DatabaseReference database;
    PG pg;
    int aboutIntent=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Intent intent=getIntent();
        aboutIntent=intent.getIntExtra("aboutIntent",1);

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(aboutIntent==2){
            DatabaseReference database=FirebaseDatabase.getInstance().getReference("PG").child("0");
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    pg=dataSnapshot.getValue(PG.class);
                    if(pg.getDetails()!=null){
                        name.setText(pg.getDetails().getName());
                        phone.setText(pg.getDetails().getPhone());
                        pgname.setText(pg.getDetails().getPGName());
                        date.setText(pg.getDetails().getDateCreated());
                        pincode.setText(pg.getDetails().getPincode());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(RegistrationActivity.this,databaseError.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void init() {
        //for a single node of PG
        database=FirebaseDatabase.getInstance().getReference("PG").child("0");

        name=findViewById(R.id.name_edittext);
        phone=findViewById(R.id.phone_edittext);
        pgname=findViewById(R.id.pg_name);
        pincode=findViewById(R.id.pincode);
        date=findViewById(R.id.date_created);
        submit=findViewById(R.id.submit);

        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.submit:
                addManager();
                break;
        }
    }

    private void addManager() {

        String name=this.name.getText().toString();
        String phone=this.phone.getText().toString();
        String pgname=this.pgname.getText().toString();
        String pincode=this.phone.getText().toString();
        String date=this.date.getText().toString();

        PG.PGDetails pgDetails=new PG.PGDetails(name,phone,pgname,pincode,date);

        //id added is taken 0 as for the demo purpose the no. of pgs is only one
        pg=new PG("0", pgDetails,new ArrayList<Tenant>(),new ArrayList<Tenant>());

        String id=database.setValue(pg).toString();
        if(id!=""){
            Toast.makeText(getApplicationContext(),"manager added successfully!",Toast.LENGTH_LONG).show();
        }
        pg.setId(id);

        Intent intent1=new Intent();
        if(aboutIntent==2){
            setResult(2,intent1);
        }else if(aboutIntent==1){
            setResult(1,intent1);
        }

        Intent intent=new Intent(getApplicationContext(),ManagerPortalActivity.class);
        startActivity(intent);
        finish();
    }
}
