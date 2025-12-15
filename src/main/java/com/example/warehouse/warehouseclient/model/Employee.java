package com.example.warehouse.warehouseclient.model;

public class Employee {
    public Integer id;
    public String firstName;
    public String lastName;
    public String patronymic;
    public Position position;


    @Override
    public String toString() {
        return lastName + " " + firstName;
    }
}
