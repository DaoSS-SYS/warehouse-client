package com.example.warehouse.warehouseclient.model;

public class Contractor {
    public Integer id;
    public String name;
    public String phone;
    public String address;

    @Override
    public String toString() {
        return name == null ? "" : name;
    }
}
