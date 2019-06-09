package com.example.easypg;

//Tenant POJO
public class Tenant {

    private String id;
    private TenantDetails details;

    public Tenant() {
    }

    public Tenant(String id, TenantDetails details) {
        this.id = id;
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
        private String name;
        private String phone;
        private String room;
        private String rentAmount;
        private String pgId;

        public TenantDetails() {
        }
        public TenantDetails(String name, String phone, String room, String rentAmount) {
            this.name = name;
            this.phone =phone;
            this.room = room;
            this.rentAmount = rentAmount;
        }
        public TenantDetails(String name, String phone, String room, String rentAmount, String pgId) {
            this.name = name;
            this.phone = phone;
            this.room = room;
            this.rentAmount = rentAmount;
            this.pgId = pgId;
        }
        public String getPgId() {
            return pgId;
        }
        public void setPgId(String pgId) {
            this.pgId = pgId;
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
