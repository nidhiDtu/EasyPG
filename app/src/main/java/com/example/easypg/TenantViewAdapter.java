package com.example.easypg;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class TenantViewAdapter extends RecyclerView.Adapter<TenantViewAdapter.TenantViewHolder> {

    public interface OnItemClickListner{
        void onItemClick(int position);
    }

    private ArrayList<Tenant> tenantList;
    private Context context;
    private OnItemClickListner mListner;

    @NonNull
    @Override
    public TenantViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.tenant_item_view,viewGroup,false);
        TenantViewHolder tenantViewHolder=new TenantViewHolder(view);
        return tenantViewHolder;
    }

    public TenantViewAdapter(ArrayList<Tenant> list, Context context, OnItemClickListner mListner) {
        this.tenantList = list;
        this.context = context;
        this.mListner = mListner;
    }

    @Override
    public void onBindViewHolder(@NonNull final TenantViewHolder tenantViewHolder, int i) {
        Tenant tenant=tenantList.get(i);
        Tenant.TenantDetails tenantDetails=tenant.getDetails();
        tenantViewHolder.name.setText(tenantDetails.getName());
        tenantViewHolder.phone.setText(tenantDetails.getPhone());
        tenantViewHolder.room.setText(tenantDetails.getRoom());
        tenantViewHolder.rent.setText(tenantDetails.getRentAmount());

        tenantViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListner.onItemClick(tenantViewHolder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return tenantList.size();
    }

    class TenantViewHolder extends RecyclerView.ViewHolder{

        TextView name,phone,room,rent;
        View itemView;

        public TenantViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView=itemView;
            name=itemView.findViewById(R.id.name);
            phone=itemView.findViewById(R.id.phone);
            room=itemView.findViewById(R.id.room);
            rent=itemView.findViewById(R.id.rentAmt);
        }
    }
}
