package com.example.easypg;

//Tenant POJO
public class Tenant {

    private String id;

    private TenantDetails details;

    public Tenant() {
    }
    public Tenant(TenantDetails details) {
        this.details = details;
    }
    public TenantDetails getDetails() {
        return details;
    }
    public void setDetails(TenantDetails details) {
        this.details = details;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    static class TenantDetails{
        String name;
        String phone;
        String room;
        String rentAmount;

        public TenantDetails(String name, String phone, String room, String rentAmount) {
            this.name = name;
            this.phone = phone;
            this.room = room;
            this.rentAmount = rentAmount;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getPhone() {
            return phone;
        }
        public void setPhone(String phone) {
            this.phone = phone;
        }
        public String getRoom() {
            return room;
        }
        public void setRoom(String room) {
            this.room = room;
        }
        public String getRentAmount() {
            return rentAmount;
        }
        public void setRentAmount(String rentAmount) {
            this.rentAmount = rentAmount;
        }
    }
}
