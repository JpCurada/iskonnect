package com.iskonnect.models;

public class User {
    private String studentNumber;
    private String name;
    private String email;
    private String course;
    
    public User(String studentNumber, String name, String email, String course) {
        this.studentNumber = studentNumber;
        this.name = name;
        this.email = email;
        this.course = course;
    }
    
    // Getters
    public String getStudentNumber() { return studentNumber; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getCourse() { return course; }

    // Setters
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setCourse(String course) { this.course = course; }
}