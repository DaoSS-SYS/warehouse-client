package com.example.warehouse.warehouseclient.Auth;

public class AuthService {

    public static Role login(String username, String password) {
        if ("admin".equals(username) && "admin".equals(password)) return Role.ADMIN;
        if ("manager".equals(username) && "manager".equals(password)) return Role.MANAGER;
        if ("sklad".equals(username) && "sklad".equals(password)) return Role.SKLAD;
        if ("hire".equals(username) && "hire".equals(password)) return Role.HIRE;
        return null;
    }
}
