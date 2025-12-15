package com.example.warehouse.warehouseclient.model;
import com.example.warehouse.warehouseclient.model.Position;

public class Position {
    public Integer id;
    public String name;
    public Integer salary; // ← ДОБАВИЛИ

    @Override
    public String toString() {
        if (salary != null) {
            return name + " (" + salary + " ₽)";
        }
        return name == null ? "" : name;
    }
}
