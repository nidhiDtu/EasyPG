package com.example.easypg;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText email,password;
    private Button signup;
    private TextView login_ref;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseUser firebaseUser;
    private DatabaseReference pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        init();
    }

    private void init() {
        pg=Databases.getPgDB();
        mAuth=FirebaseAuth.getInstance();
        firebaseUser=mAuth.getCurrentUser();
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
                    intent.putExtra("aboutIntent","create");
                    startActivityForResult(intent,1);
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

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null && firebaseUser.isEmailVerified()){
            Intent intent=new Intent(SignUpActivity.this,ManagerPortalActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){
            if(resultCode==1){
                //nothing to do now
            }
        }
    }
}
