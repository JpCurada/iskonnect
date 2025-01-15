package com.iskonnect.models;

public class Admin extends User {
    public Admin(String strAdminId, String strFirstName, String strLastName, String strEmail) {
        super(strAdminId, strFirstName, strLastName, strEmail, "ADMIN");
    }
}
