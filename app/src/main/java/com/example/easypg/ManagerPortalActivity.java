package com.example.easypg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManagerPortalActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private PG Pg;
    private ArrayList<Tenant> onBoardTenants=new ArrayList<>();
    private RecyclerView recyclerView;
    private TenantViewAdapter tenantViewAdapter;
    private ProgressBar progressBar;

    private DatabaseReference notOnBoardDB;
    private DatabaseReference onBoardDB;
    private DatabaseReference tenants;
    private DatabaseReference pg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_portal);

        init();

    }

    private void init() {
        onBoardDB=Databases.getOnBoardDB();
        notOnBoardDB=Databases.getNotOnBoardDB();
        tenants=Databases.getTenantsDB();
        pg=Databases.getPgDB();
        recyclerView=findViewById(R.id.recyclerview);
        progressBar=findViewById(R.id.progressbar);

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
                        Intent intent=new Intent(ManagerPortalActivity.this,RegistrationActivity.class);
                        //request code for update info is 2 and for registration is 1
                        intent.putExtra("aboutIntent",2);
                        startActivityForResult(intent,2);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressBar.setVisibility(View.VISIBLE);

        onBoardDB.addValueEventListener(new ValueEventListener() {
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
                        Tenant tenant=onBoardTenants.get(position);
                        Intent intent=new Intent(ManagerPortalActivity.this,ShowTenantProfileImage.class);
                        intent.putExtra("phone",tenant.getId());
                        startActivity(intent);
                    }

                    });

                recyclerView.setAdapter(tenantViewAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(ManagerPortalActivity.this));
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManagerPortalActivity.this,"The read failed: " + databaseError.getCode(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            if(resultCode==2){
                //manager info update call
            }
        }
    }
}
