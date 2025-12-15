package com.example.warehouse.warehouseclient.model;

public class OperationCreateRequest {
    public Integer productId;
    public Integer employeeId;
    public Integer contractorId;     // можно null
    public Integer operationTypeId;
    public Integer quantity;
    public String date;
}
