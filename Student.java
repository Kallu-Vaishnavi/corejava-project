package com.java.quizapp;



public class Student extends User {

    public Student(int id, String name) {
        super(id, name);
    }

    @Override
    public void showProfile() {
        System.out.println("Student ID: " + getId());
        System.out.println("Student Name: " + getName());
    }
}