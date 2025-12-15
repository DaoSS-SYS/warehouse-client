package com.example.warehouse.warehouseclient.api;

import com.example.warehouse.warehouseclient.model.Category;
import com.example.warehouse.warehouseclient.model.Contractor;
import com.example.warehouse.warehouseclient.model.Employee;
import com.example.warehouse.warehouseclient.model.EmployeeRequest;
import com.example.warehouse.warehouseclient.model.Operation;
import com.example.warehouse.warehouseclient.model.OperationCreateRequest;
import com.example.warehouse.warehouseclient.model.OperationType;
import com.example.warehouse.warehouseclient.model.Position;
import com.example.warehouse.warehouseclient.model.Product;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

public class ApiClient {

    /** Базовый адрес серверного API. */
    private final String baseUrl = "http://localhost:8080/api";

    /** HTTP-клиент для выполнения запросов. */
    private final HttpClient http = HttpClient.newHttpClient();

    /** JSON-сериализация и поддержка типов даты/времени. */
    private final ObjectMapper om = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    /** Получение списка категорий. */
    public List<Category> getCategories() throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/categories"))
                .GET().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), new TypeReference<>() {});
        throw httpError(resp);
    }

    /** Создание категории. */
    public Category createCategory(Category c) throws Exception {
        String body = om.writeValueAsString(c);
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/categories"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), Category.class);
        throw httpError(resp);
    }

    /** Обновление категории по идентификатору. */
    public Category updateCategory(int id, Category c) throws Exception {
        String body = om.writeValueAsString(c);
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/categories/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), Category.class);
        throw httpError(resp);
    }

    /** Удаление категории по идентификатору. */
    public void deleteCategory(int id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/categories/" + id))
                .DELETE().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return;
        throw httpError(resp);
    }

    /** Получение списка товаров с фильтрацией и сортировкой. */
    public List<Product> getProducts(String nameFilter, Integer categoryId, String sort) throws Exception {
        StringBuilder url = new StringBuilder(baseUrl + "/products?");

        if (nameFilter != null && !nameFilter.isBlank()) {
            url.append("name=").append(enc(nameFilter.trim())).append("&");
        }
        if (categoryId != null) {
            url.append("categoryId=").append(categoryId).append("&");
        }
        if (sort != null && !sort.isBlank()) {
            url.append("sort=").append(enc(sort)).append("&");
        }

        HttpRequest req = HttpRequest.newBuilder(URI.create(url.toString()))
                .GET().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), new TypeReference<>() {});
        throw httpError(resp);
    }

    /** Создание товара. */
    public Product createProduct(Product p) throws Exception {
        String body = om.writeValueAsString(p);
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), Product.class);
        throw httpError(resp);
    }

    /** Обновление товара по идентификатору. */
    public Product updateProduct(int id, Product p) throws Exception {
        String body = om.writeValueAsString(p);
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/products/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), Product.class);
        throw httpError(resp);
    }

    /** Удаление товара по идентификатору. */
    public void deleteProduct(int id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/products/" + id))
                .DELETE().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return;
        throw httpError(resp);
    }

    /** Получение списка контрагентов. */
    public List<Contractor> getContractors() throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/contractors"))
                .GET().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), new TypeReference<>() {});
        throw httpError(resp);
    }

    /** Создание контрагента. */
    public Contractor createContractor(Contractor c) throws Exception {
        String body = om.writeValueAsString(c);
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/contractors"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), Contractor.class);
        throw httpError(resp);
    }

    /** Обновление контрагента по идентификатору. */
    public Contractor updateContractor(int id, Contractor c) throws Exception {
        String body = om.writeValueAsString(c);
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/contractors/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), Contractor.class);
        throw httpError(resp);
    }

    /** Удаление контрагента по идентификатору. */
    public void deleteContractor(int id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/contractors/" + id))
                .DELETE().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return;
        throw httpError(resp);
    }

    /** Получение списка должностей. */
    public List<Position> getPositions() throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/positions"))
                .GET().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), new TypeReference<>() {});
        throw httpError(resp);
    }

    /** Создание должности. */
    public Position createPosition(Position p) throws Exception {
        String body = om.writeValueAsString(p);
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/positions"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), Position.class);
        throw httpError(resp);
    }

    /** Обновление должности по идентификатору. */
    public Position updatePosition(int id, Position p) throws Exception {
        String body = om.writeValueAsString(p);
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/positions/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), Position.class);
        throw httpError(resp);
    }

    /** Удаление должности по идентификатору. */
    public void deletePosition(int id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/positions/" + id))
                .DELETE().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return;
        throw httpError(resp);
    }

    /** Получение списка сотрудников. */
    public List<Employee> getEmployees() throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/employees"))
                .GET().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), new TypeReference<>() {});
        throw httpError(resp);
    }

    /** Создание сотрудника на основе DTO запроса. */
    public Employee createEmployee(EmployeeRequest dto) throws Exception {
        String body = om.writeValueAsString(dto);
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/employees"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), Employee.class);
        throw httpError(resp);
    }

    /** Обновление сотрудника по идентификатору на основе DTO запроса. */
    public Employee updateEmployee(int id, EmployeeRequest dto) throws Exception {
        String body = om.writeValueAsString(dto);
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/employees/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), Employee.class);
        throw httpError(resp);
    }

    /** Удаление сотрудника по идентификатору. */
    public void deleteEmployee(int id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/employees/" + id))
                .DELETE().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return;
        throw httpError(resp);
    }

    /** Получение справочника типов операций. */
    public List<OperationType> getOperationTypes() throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/operation-types"))
                .GET().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), new TypeReference<>() {});
        throw httpError(resp);
    }

    /** Получение списка операций с фильтрацией по параметрам и диапазону дат. */
    public List<Operation> getOperations(
            Integer productId,
            Integer employeeId,
            Integer contractorId,
            Integer operationTypeId,
            LocalDateTime from,
            LocalDateTime to,
            String sort
    ) throws Exception {

        StringBuilder url = new StringBuilder(baseUrl + "/operations?");

        if (productId != null) url.append("productId=").append(productId).append("&");
        if (employeeId != null) url.append("employeeId=").append(employeeId).append("&");
        if (contractorId != null) url.append("contractorId=").append(contractorId).append("&");
        if (operationTypeId != null) url.append("operationTypeId=").append(operationTypeId).append("&");

        if (from != null) url.append("from=").append(enc(from.toString())).append("&");
        if (to != null) url.append("to=").append(enc(to.toString())).append("&");
        if (sort != null && !sort.isBlank()) url.append("sort=").append(enc(sort)).append("&");

        HttpRequest req = HttpRequest.newBuilder(URI.create(url.toString()))
                .GET().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return om.readValue(resp.body(), new TypeReference<>() {});
        throw httpError(resp);
    }

    /** Создание операции на основе DTO запроса. */
    public void createOperation(OperationCreateRequest reqDto) throws Exception {
        String body = om.writeValueAsString(reqDto);

        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/operations"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return;
        throw httpError(resp);
    }

    /** Удаление операции по идентификатору. */
    public void deleteOperation(int id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(baseUrl + "/operations/" + id))
                .DELETE().build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (isOk(resp)) return;
        throw httpError(resp);
    }

    /** Проверка успешности HTTP-ответа. */
    private boolean isOk(HttpResponse<String> resp) {
        return resp.statusCode() >= 200 && resp.statusCode() < 300;
    }

    /** Формирование исключения на основе кода ответа и тела ответа. */
    private RuntimeException httpError(HttpResponse<String> resp) {
        return new RuntimeException("HTTP " + resp.statusCode() + ": " + resp.body());
    }

    /** URL-кодирование параметров запроса. */
    private String enc(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}
