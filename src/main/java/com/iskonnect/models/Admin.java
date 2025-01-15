package com.iskonnect.models;

public class Admin extends User {
    public Admin(String adminId, String firstName, String lastName, String email) {
        super(adminId, firstName, lastName, email, "ADMIN");
    }
}