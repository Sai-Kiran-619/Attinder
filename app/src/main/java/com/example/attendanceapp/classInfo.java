package com.example.attendanceapp;

public class classInfo {
    String name;
    String strength;

    public classInfo(String name, String  strength) {
        this.name = name;
        this.strength = strength;
    }

    public classInfo()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }
}
