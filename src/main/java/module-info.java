module com.example.warehouse.warehouseclient {

    requires javafx.controls;
    requires javafx.fxml;

    requires java.net.http;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310;

    opens com.example.warehouse.warehouseclient.model to com.fasterxml.jackson.databind;

    exports com.example.warehouse.warehouseclient;
    opens com.example.warehouse.warehouseclient to javafx.fxml;

    exports com.example.warehouse.warehouseclient.Auth;
    opens com.example.warehouse.warehouseclient.Auth to javafx.fxml;
}
