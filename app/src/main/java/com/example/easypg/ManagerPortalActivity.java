package com.example.easypg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManagerPortalActivity extends AppCompatActivity {

    Toolbar toolbar;
    PG pg;
    ArrayList<Tenant> onBoardTenants=new ArrayList<>();
    DatabaseReference database;
    RecyclerView recyclerView;
    TenantViewAdapter tenantViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_portal);

        init();

        fetchTenants();
    }

    private void fetchTenants() {

    }

    private void init() {
        database= FirebaseDatabase.getInstance().getReference("NotOnBoardTenants");
        recyclerView=findViewById(R.id.recyclerview);

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

    @Override
    protected void onResume() {
        super.onResume();
        fetchTenants();
    }

    @Override
    protected void onStart() {
        super.onStart();

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                onBoardTenants.clear();
                for(DataSnapshot pg_snapshot:dataSnapshot.getChildren()){
                    Tenant tenant=pg_snapshot.getValue(Tenant.class);

                    onBoardTenants.add(tenant);
                }
                tenantViewAdapter=new TenantViewAdapter(onBoardTenants, ManagerPortalActivity.this,
                        new TenantViewAdapter.OnItemClickListner() {
                    @Override
                    public void onItemClick(int position) {
                        //when a tenant is selected to view or edit
                        Toast.makeText(getApplicationContext(),"Item touched!",Toast.LENGTH_LONG).show();
                    }
                });

                recyclerView.setAdapter(tenantViewAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
