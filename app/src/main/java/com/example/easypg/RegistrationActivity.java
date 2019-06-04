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

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    EditText name,phone,pgname,pincode,date;
    Button submit;

    DatabaseReference database;
    PG pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        init();
    }

    private void init() {
        //for a single node of PG
        database=FirebaseDatabase.getInstance().getReference("PG");

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
        pg=new PG("", pgDetails,null,null);

        String id=database.child("0").setValue(pg).toString();
        if(id!=""){
            Toast.makeText(getApplicationContext(),"manager added successfully!",Toast.LENGTH_LONG).show();
        }
        pg.setId(id);

        Intent intent=new Intent(getApplicationContext(),ManagerPortalActivity.class);
        startActivity(intent);
        finish();
    }
}
