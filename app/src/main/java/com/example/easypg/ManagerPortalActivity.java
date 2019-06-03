package com.example.easypg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ManagerPortalActivity extends AppCompatActivity {

    Toolbar toolbar;
    PG pg;
    ArrayList<Tenant> onBoardTenants=new ArrayList<>();
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_portal);

        init();
    }

    private void init() {
        database= FirebaseDatabase.getInstance();
        toolbar=findViewById(R.id.toolbar);
        toolbar.setTitle("Test Title");
        toolbar.inflateMenu(R.menu.manager_portal_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.add_new_tenant:
                        startActivity(new Intent(getApplicationContext(),AddTenantActivity.class));
                        break;
                    case R.id.editinfo:
                        break;
                }
                return true;
            }
        });
    }
}
