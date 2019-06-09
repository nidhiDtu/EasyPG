package com.example.easypg;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class TenantDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView nameText, phoneText, roomText, rentText;
    private Button editButton;
    private ImageView profile;

    private String phone;//phone
    private Tenant tenant;

    private StorageReference storageReference;
    private Uri uri;
    private DatabaseReference notOnBoardDB;
    private DatabaseReference onBoardDB;
    private DatabaseReference tenants;
    private DatabaseReference pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_details);

        Intent intent=getIntent();
        phone=intent.getStringExtra("phone");
//        if(phone==null) phone=firebaseUser.getPhoneNumber();
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference child=notOnBoardDB.child(phone);

        child.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tenant=dataSnapshot.getValue(Tenant.class);
                if(tenant!=null && tenant.getDetails()!=null){
                    nameText.setText(tenant.getDetails().getName());
                    phoneText.setText(tenant.getDetails().getPhone());
                    roomText.setText(tenant.getDetails().getRoom());
                    rentText.setText(tenant.getDetails().getRentAmount());
//                    try {
////                        downloadFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
////                    }
//                    if (firebaseUser!=null && !firebaseUser.isEmailVerified())
                    ShiftOfTenant.copyTenantFromOnBoardToTenant(tenant);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TenantDetailsActivity.this,"The read failed: " + databaseError.getCode(),Toast.LENGTH_LONG).show();
            }
        });

    }

//    private void downloadFile() throws IOException {
//        File localFile = File.createTempFile("images", "jpg");
//        storageReference.getFile(localFile)
//                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                        // Successfully downloaded data to local file
//                        // ...
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                // Handle failed download
//                // ...
//            }
//        });
//    }

    private void init() {
        profile=findViewById(R.id.profilepic);
        nameText =findViewById(R.id.name);
        phoneText =findViewById(R.id.phone);
        roomText =findViewById(R.id.room);
        rentText =findViewById(R.id.rentAmt);
        editButton =findViewById(R.id.edit_button);
        editButton.setOnClickListener(this);

        storageReference= Databases.getStorageReference();
        onBoardDB=Databases.getOnBoardDB();
        notOnBoardDB=Databases.getNotOnBoardDB();
        tenants=Databases.getTenantsDB();
        pg=Databases.getPgDB();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.edit_button:
                Intent intent=new Intent(TenantDetailsActivity.this,EditTenantActivity.class);
                intent.putExtra("phone",phone);
//                TenantDetailsActivity.this.finish();
                startActivity(intent);
                break;
        }
    }

}
