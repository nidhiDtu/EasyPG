package com.example.easypg;

import android.arch.core.executor.TaskExecutor;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity implements View.OnClickListener {

    TextView manager_ref;
    EditText phoneEditText;
    Button signupButton;
    ProgressBar progressBar;

    String phone;
    String otp;
    private String verificationId;
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        init();
    }

    private void init() {
        mAuth=FirebaseAuth.getInstance();

        progressBar=findViewById(R.id.progressbar);
        phoneEditText=findViewById(R.id.phone);
        manager_ref=findViewById(R.id.manager_ref);
        signupButton=findViewById(R.id.signup_button);

        manager_ref.setOnClickListener(this);
        signupButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        phone="+91"+phoneEditText.getText().toString();

        if(phone.isEmpty()||phone.length()<10){
            phoneEditText.setError("Please add valid phone number!");
            phoneEditText.requestFocus();
            return;
        }
        switch (view.getId()){
            case R.id.manager_ref:
                finish();
                break;

            case R.id.signup_button:
                findThePhoneNoInTheTenant();
                break;
        }
    }

    private void findThePhoneNoInTheTenant() {
        // TODO: 6/5/2019 find this number in the firebase tenant node
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference database=FirebaseDatabase.getInstance().getReference("PG").child("0")
                .child("NotOnBoardTenants");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(phone)){
                    tenantSignup();
                }else{
                    Toast.makeText(PhoneAuthActivity.this,"You are not registered as a tenant in the database!",
                            Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void tenantSignup() {
        //search in Tenant node
        sendVerificationCode(phone);
    }

    private void sendVerificationCode(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(phone,60, TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,mCallback);
    }

    private void verifyCode(String code){
        PhoneAuthCredential phoneAuthCredential=PhoneAuthProvider.getCredential(verificationId,code);
        signInWithCredential(phoneAuthCredential);
    }

    private void signInWithCredential(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    // TODO: 6/5/2019 when signup is successfull
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(PhoneAuthActivity.this,"verification successful!",Toast.LENGTH_LONG).show();
                    //set flag and call user activity
                    Intent intent=new Intent(PhoneAuthActivity.this,TenantDetailsActivity.class);
                    intent.putExtra("phone",phone);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    Toast.makeText(PhoneAuthActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId=s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code=phoneAuthCredential.getSmsCode();
            if(code==null){

                AlertDialog.Builder builder=new AlertDialog.Builder(PhoneAuthActivity.this);
                builder.setTitle("Enter code below");
                builder.setMessage("expires in 60 second.");

                final EditText input = new EditText(PhoneAuthActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                builder.setView(input);

                builder.setPositiveButton("Submit",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                otp=input.getText().toString();
                                verifyCode(otp);
                            }
                        });

                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                builder.show();
            }else{
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(PhoneAuthActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };

//    @Override
//    protected void onStart() {
//        super.onStart();
//        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
//        if(firebaseUser!=null){
//            Intent intent=new Intent(PhoneAuthActivity.this,TenantDetailsActivity.class);
//            intent.putExtra("phone",firebaseUser.getPhoneNumber());
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent);
//        }
//    }
}
