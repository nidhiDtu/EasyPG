package com.example.easypg;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    EditText email,password;
    Button signup;
    TextView login_ref;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;

//    DatabaseReference database;
//    PG pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        init();
    }

    private void init() {
//        database=FirebaseDatabase.getInstance().getReference();
        email=findViewById(R.id.email);
        password=findViewById(R.id.signup_password);
        signup=findViewById(R.id.signup_button);
        login_ref=findViewById(R.id.login_ref);
        progressBar=findViewById(R.id.progressbar);

        signup.setOnClickListener(this);
        login_ref.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signup_button:
                registerUser();
                break;
            case R.id.login_ref:
                finish();
                break;
        }
    }

    private void registerUser() {
        String mailid=email.getText().toString();
        String pass=password.getText().toString();

        if(mailid.isEmpty()){
            email.setError("Email is required!");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(mailid).matches()){
            email.setError("Please enter a valid username address!");
            email.requestFocus();
            return;
        }

        if(pass.isEmpty()){
            password.setError("Password is required!");
            password.requestFocus();
            return;
        }

        if(pass.length()<8){
            password.setError("Minimum length of password is atleast characters.");
            password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(mailid,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful()){

                    Toast.makeText(SignUpActivity.this,"Sign Up successfully!"
                            ,Toast.LENGTH_LONG).show();

                    Intent intent=new Intent(SignUpActivity.this,RegistrationActivity.class);
                    startActivity(intent);
                }else{
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(SignUpActivity.this,"You are already registered!"
                                ,Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(SignUpActivity.this,task.getException().getMessage()
                                ,Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }
}
