package com.example.easypg;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TenantDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    TextView name,phone,room,rent;
    Button edit;
    String id;//phone
    Tenant tenant;
    DatabaseReference database;//PG/0/NotOnBoardTenants db
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_details);

        Intent intent=getIntent();
        id=intent.getStringExtra("phone");
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        database=FirebaseDatabase.getInstance().getReference("PG").child("0").child("NotOnBoardTenants").child(id);

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tenant=dataSnapshot.getValue(Tenant.class);
                name.setText(tenant.getDetails().getName());
                phone.setText(tenant.getDetails().getPhone());
                room.setText(tenant.getDetails().getRoom());
                rent.setText(tenant.getDetails().getRentAmount());

                if(firebaseUser!=null)
                    copyTenantFromOnBoardToTenant(firebaseUser.getUid());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TenantDetailsActivity.this,"The read failed: " + databaseError.getCode(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void init() {
        name=findViewById(R.id.name);
        phone=findViewById(R.id.phone);
        room=findViewById(R.id.room);
        rent=findViewById(R.id.rentAmt);
        edit=findViewById(R.id.edit_button);
        database=FirebaseDatabase.getInstance().getReference("PG").child("0").child("NotOnBoardTenants");

        edit.setOnClickListener(this);
    }

    private void copyTenantFromOnBoardToTenant(String authkey) {
        /*
        * searched in not on board and copy to tenant and onboard*/
        DatabaseReference onBoard=FirebaseDatabase.getInstance().getReference("PG").child("0").child("OnBoardTenants");
        DatabaseReference tenants=FirebaseDatabase.getInstance().getReference("Tenants");

        Tenant newTenant=new Tenant(new Tenant.TenantDetails(tenant.getDetails().name,tenant.getDetails()
                .phone,tenant.getDetails().room,tenant.getDetails().getRentAmount(),"0"));
        String id1=onBoard.child(id).setValue(tenant).toString();
        String id2=tenants.child(authkey).setValue(newTenant).toString();

        if(id1!=null && id2!=null){
            Toast.makeText(TenantDetailsActivity.this,"Copied successfully!",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(TenantDetailsActivity.this,"Can't copy!",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_button:
                Intent intent=new Intent(TenantDetailsActivity.this,EditTenantActivity.class);
                intent.putExtra("phone",id);
                //request code edit the tenant is 2 and for adding it is 1
                startActivityForResult(intent,2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            if(resultCode==2){
                Bundle bundle=data.getExtras();
                String i=bundle.getString("phone","-1");
                if(i!="-1") id=i;
            }
        }
    }
}
