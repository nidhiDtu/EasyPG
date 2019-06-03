package com.example.easypg;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class PhoneAuthActivity extends AppCompatActivity implements View.OnClickListener {

    TextView manager_ref;
    EditText phoneEditText;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);

        init();
    }

    private void init() {
        phoneEditText=findViewById(R.id.phone);
        loginButton.findViewById(R.id.login_button);
        manager_ref=findViewById(R.id.manager_ref);

        loginButton.setOnClickListener(this);
        manager_ref.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.manager_ref:
                Intent intent=new Intent(PhoneAuthActivity.this,MainActivity.class);
                startActivity(intent);
                break;
            case R.id.login_button:
                String phone=phoneEditText.getText().toString();
                break;
        }
    }
}
