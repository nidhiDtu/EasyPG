package com.example.easypg;

import java.util.ArrayList;

/*
* PG Node POJO*/
public class PG {
    private String id;
    private PGDetails details;
    private ArrayList<Tenant> NotOnBoardTenants;
    private ArrayList<Tenant> OnBoardTenants;

    //getters and setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public PGDetails getDetails() {
        return details;
    }
    public void setDetails(PGDetails details) {
        this.details = details;
    }
    public ArrayList<Tenant> getNotOnBoardTenants() {
        return NotOnBoardTenants;
    }
    public void setNotOnBoardTenants(ArrayList<Tenant> notOnBoardTenants) {
        NotOnBoardTenants = notOnBoardTenants;
    }
    public ArrayList<Tenant> getOnBoardTenants() {
        return OnBoardTenants;
    }
    public void setOnBoardTenants(ArrayList<Tenant> onBoardTenants) {
        OnBoardTenants = onBoardTenants;
    }
    public PG() {
    }

    //constructor
    public PG(String id, PGDetails manager,
              ArrayList<Tenant> notOnBoardTenants, ArrayList<Tenant> onBoardTenants) {
        this.id = id;
        this.details = manager;
        this.NotOnBoardTenants = notOnBoardTenants;
        this.OnBoardTenants = onBoardTenants;
    }

    //details class structure
    static class PGDetails{
        private String name;
        private String phone;
        private String PGName;
        private String pincode;
        private String dateCreated;

        //PGdetails constructor
        public PGDetails(String name, String phone, String PGName,
                         String pincode, String dateCreated) {
            this.name = name;
            this.phone = phone;
            this.PGName = PGName;
            this.pincode = pincode;
            this.dateCreated = dateCreated;
        }

        //Getters and Setters
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
        public String getPGName() {
            return PGName;
        }
        public void setPGName(String PGName) {
            this.PGName = PGName;
        }
        public String getPincode() {
            return pincode;
        }
        public void setPincode(String pincode) {
            this.pincode = pincode;
        }
        public String getDateCreated() {
            return dateCreated;
        }
        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
        }
        public PGDetails() {
        }
    }


}
