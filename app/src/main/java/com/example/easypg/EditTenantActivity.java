package com.example.easypg;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import static android.provider.MediaStore.Images.Media.getBitmap;

public class EditTenantActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 5;
    private static final int CAMERA_ACCESS_REQUEST=6;
    private static final String IMAGE_DIRECTORY = "/Pictures";
    private EditText nameEdit;
    private TextView roomText,rentText,phoneText;
    private ImageView profile;
    private Button save,cancel,upload,camera;

    private String phone;
    private Tenant tenant;
    private StorageReference storageReference;
    private DatabaseReference notOnBoardDB;
    private DatabaseReference onBoardDB;
    private DatabaseReference tenants;
    private DatabaseReference pg;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tenant);

        Intent intent=getIntent();
        phone = intent.getStringExtra("phone");

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        notOnBoardDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(phone).exists()){
                    tenant=dataSnapshot.child(phone).getValue(Tenant.class);
                    if(tenant.getDetails()!=null){
                        nameEdit.setText(tenant.getDetails().getName());
                        phoneText.setText(tenant.getDetails().getPhone());
                        roomText.setText(tenant.getDetails().getRoom());
                        rentText.setText(tenant.getDetails().getRentAmount());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditTenantActivity.this,databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        nameEdit=findViewById(R.id.name_edittext);
        phoneText=findViewById(R.id.phone_edittext);
        roomText=findViewById(R.id.room);
        rentText=findViewById(R.id.rentAmt);
        profile=findViewById(R.id.profilepic);
        save=findViewById(R.id.save);
        cancel=findViewById(R.id.cancel);
        camera=findViewById(R.id.camera);
        upload=findViewById(R.id.upload);

        storageReference= Databases.getStorageReference();
        onBoardDB=Databases.getOnBoardDB();
        notOnBoardDB=Databases.getNotOnBoardDB();
        tenants=Databases.getTenantsDB();
        pg=Databases.getPgDB();

        upload.setOnClickListener(this);
        camera.setOnClickListener(this);
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
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

            StorageReference Ref = storageReference.child("images/"+ phone +"-profilejpg");

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
                Bitmap bitmap= getBitmap(getContentResolver(),uri);
                profile.setImageBitmap(bitmap);
                uploadFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode==CAMERA_ACCESS_REQUEST && resultCode==RESULT_OK
                && data!=null && data.getData()!=null){
            uri=data.getData();
            Bitmap bitmap = null;
            try {
                bitmap= getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(bitmap!=null){
                saveImage(bitmap);
                profile.setImageBitmap(bitmap);
                uploadFile();
                Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
            }else{
                Log.d("BITMAP","Bitmap is null");
            }
        }
    }

    private void captureCameraImage() {
        Intent chooserIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        chooserIntent.setType("image/+");
        chooserIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(chooserIntent, CAMERA_ACCESS_REQUEST);
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {  // have the object build the directory structure, if needed.
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.save:
                saveDetails();
                break;

            case R.id.camera:
               captureCameraImage();
                break;

            case R.id.cancel:
                save.setEnabled(false);
                camera.setEnabled(false);
                upload.setEnabled(false);
                finish();
                break;

            case R.id.upload:
                showFileChooser();
                break;
        }
    }

    private void saveDetails() {
        String newName=nameEdit.getText().toString();
        tenant.getDetails().setName(newName);

        //updating tenant in noOnBoardtenants
        Tenant.TenantDetails tenantDetails=tenant.getDetails();

        DatabaseReference childTenant=Databases.getNotOnBoardDB().child(phone).child("details");
        childTenant.child("name").setValue(tenantDetails.getName());
        childTenant.child("phone").setValue(tenantDetails.getPhone());
        childTenant.child("room").setValue(tenantDetails.getRoom());
        childTenant.child("rentAmount").setValue(tenantDetails.getRentAmount());
        ShiftOfTenant.copyTenantFromOnBoardToTenant(tenant);
        finish();
    }
}
