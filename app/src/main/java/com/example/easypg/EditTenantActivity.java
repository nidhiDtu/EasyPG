package com.example.easypg;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditTenantActivity extends AppCompatActivity {

    EditText name,phone;
    TextView room,rent;
    ImageView profile;
    Button save;

    String id;
    Tenant tenant;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tenant);

        Intent intent=getIntent();
        id= intent.getStringExtra("phone");

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseReference.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    tenant=dataSnapshot.getValue(Tenant.class);
                    name.setText(tenant.getDetails().getName());
                    phone.setText(tenant.getDetails().getPhone());
                    room.setText(tenant.getDetails().getRoom());
                    rent.setText(tenant.getDetails().getRentAmount());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditTenantActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        databaseReference= FirebaseDatabase.getInstance().getReference("PG").child("0").child("NotOnBoardTenants");
        name=findViewById(R.id.name_edittext);
        phone=findViewById(R.id.phone_edittext);
        room=findViewById(R.id.room);
        rent=findViewById(R.id.rentAmt);
        profile=findViewById(R.id.profilepic);
        save=findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newName=name.getText().toString();
                String newPhone=phone.getText().toString();

                final Tenant newTenant=new Tenant(new Tenant.TenantDetails
                        (newName,newPhone,tenant.getDetails().getRoom(),
                                tenant.getDetails().getRentAmount(),
                                tenant.getDetails().getPgId()));

                //updating in Tenants
                final DatabaseReference tenants=FirebaseDatabase.getInstance()
                        .getReference("Tenants");
                tenants.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot child:dataSnapshot.getChildren()){
                            Tenant ref=child.getValue(Tenant.class);
                            if(ref.getDetails().getPhone()==id && !dataSnapshot.hasChild(ref.getId())){
                                tenants.child(ref.getId()).removeValue();
                                tenants.child(ref.getId()).setValue(newTenant);
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                //updating tenant in onBoardtenants
                DatabaseReference onboard=FirebaseDatabase.getInstance()
                        .getReference("PG").child("0").child("OnBoardTenants");

                onboard.child(id).removeValue();
                id=onboard.child(newPhone).setValue(newTenant).toString();

                finish();
            }
        });
        profile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //upload profile
                return true;
            }
        });
    }
}
