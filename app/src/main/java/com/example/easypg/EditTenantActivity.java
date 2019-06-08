package com.example.easypg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class EditTenantActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 5;
    private static final int CAMERA_ACCESS_REQUEST=6;
    EditText name,phone;
    TextView room,rent;
    ImageView profile;
    Button save,cancel,upload,camera;

    String id;
    Tenant tenant;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    private Uri uri;

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
        storageReference= FirebaseStorage.getInstance().getReference();
        name=findViewById(R.id.name_edittext);
        phone=findViewById(R.id.phone_edittext);
        room=findViewById(R.id.room);
        rent=findViewById(R.id.rentAmt);
        profile=findViewById(R.id.profilepic);
        save=findViewById(R.id.save);
        cancel=findViewById(R.id.cancel);
        camera=findViewById(R.id.camera);
        upload=findViewById(R.id.upload);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //upload profile
//                Intent intent=new Intent(EditTenantActivity.this,UploadImage.class);
//                intent.putExtra("phone",id);
                showFileChooser();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,CAMERA_ACCESS_REQUEST);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save.setEnabled(false);
                camera.setEnabled(false);
                upload.setEnabled(false);
                finish();
            }
        });

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
                                DatabaseReference newRef=tenants.child(ref.getId()).child("details");
                                newRef.removeValue();
                                newRef.setValue(ref.getDetails());
                                break;
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });

                //updating tenant in noOnBoardtenants
                DatabaseReference notOnBoard=FirebaseDatabase.getInstance().getReference("PG").child("0").child("NotOnBoardTenants");
                notOnBoard.child(id).removeValue();
                notOnBoard.child(newPhone).setValue(newTenant);

                //updating tenant in onBoardtenants
                DatabaseReference onboard=FirebaseDatabase.getInstance()
                        .getReference("PG").child("0").child("OnBoardTenants");

                onboard.child(id).removeValue();
                id=onboard.child(newPhone).setValue(newTenant).toString();
                Toast.makeText(EditTenantActivity.this,id,Toast.LENGTH_LONG).show();
//                Intent intent=new Intent();
//                intent.putExtra("phone",id);
//                setResult(2,intent);
                finish();
            }
        });
    }

    private void showFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/+");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select an image."),PICK_IMAGE_REQUEST);
    }

    private void uploadFile(){
        if(uri!=null){

            final ProgressDialog progressDialog=new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference Ref = storageReference.child("images/"+id+"-profilejpg");

            Ref.putFile(uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Upload successful!",Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int progress=(int)((100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage(progress+"Uploaded..");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
        }else{
            //Error toast
            Toast.makeText(getApplicationContext(),"Select an image first.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK
                && data!=null && data.getData()!=null){
//image selected successfully
            uri=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                profile.setImageBitmap(bitmap);
                uploadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode==CAMERA_ACCESS_REQUEST && resultCode==RESULT_OK
                && data!=null && data.getData()!=null){
            uri=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                profile.setImageBitmap(bitmap);
                uploadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }
}
