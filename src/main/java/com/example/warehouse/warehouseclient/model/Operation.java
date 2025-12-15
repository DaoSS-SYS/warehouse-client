package com.example.warehouse.warehouseclient.model;

import java.time.LocalDateTime;

public class Operation {
    public Integer id;

    public Product product;
    public Employee employee;
    public Contractor contractor;   // может быть null
    public OperationType operationType;

    public Integer quantity;
    public LocalDateTime date;
}
