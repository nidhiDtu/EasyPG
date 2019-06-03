package com.example.easypg;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView signup_ref,tenantormanager;
    Button login;
    EditText username,password;
    ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();

        init();
    }

    private void init() {
        //to set the UI
        username =findViewById(R.id.username);
        password=findViewById(R.id.signin_password);
        signup_ref=findViewById(R.id.signup_ref);
        login=findViewById(R.id.login_button);
        progressBar=findViewById(R.id.progress);
        tenantormanager=findViewById(R.id.tenant_ref);

        tenantormanager.setOnClickListener(this);
        signup_ref.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.tenant_ref:
                intent=new Intent(MainActivity.this,PhoneAuthActivity.class);
                startActivity(intent);
                break;
            case R.id.signup_ref:
                intent=new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
                break;
            case R.id.login_button:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        //for logging in
        String username= this.username.getText().toString();
        String pass=password.getText().toString();

        if(username.isEmpty()){
            this.username.setError("Email is required!");
            this.username.requestFocus();
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

        if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            this.username.setError("Please enter a valid email address!");
            this.username.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        //email address login of managers
        mAuth.signInWithEmailAndPassword(username,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);

                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Sign Up successfully!"
                            ,Toast.LENGTH_LONG).show();
                }else{
                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        Toast.makeText(MainActivity.this,"Email or Password is incorrect!"
                                ,Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(MainActivity.this,task.getException().getMessage()
                                ,Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }
}
