package com.example.warehouse.warehouseclient;

import javafx.stage.Modality;
import com.example.warehouse.warehouseclient.model.StatsRow;
import javafx.beans.property.SimpleObjectProperty;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

import com.example.warehouse.warehouseclient.api.ApiClient;
import com.example.warehouse.warehouseclient.model.Category;
import com.example.warehouse.warehouseclient.model.Product;
import com.example.warehouse.warehouseclient.model.Employee;
import com.example.warehouse.warehouseclient.model.Contractor;
import com.example.warehouse.warehouseclient.model.Operation;
import com.example.warehouse.warehouseclient.model.OperationType;
import com.example.warehouse.warehouseclient.model.OperationCreateRequest;
import com.example.warehouse.warehouseclient.model.Position;
import com.example.warehouse.warehouseclient.UI.dialogs.PositionDialog;
import com.example.warehouse.warehouseclient.Auth.Role;
import javafx.scene.control.Tab;
import com.example.warehouse.warehouseclient.UI.dialogs.CategoryDialog;
import com.example.warehouse.warehouseclient.UI.dialogs.ProductDialog;
import com.example.warehouse.warehouseclient.UI.dialogs.ContractorDialog;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;

import com.example.warehouse.warehouseclient.model.EmployeeRequest;
import com.example.warehouse.warehouseclient.UI.dialogs.EmployeeDialog;

public class MainController {

    /** Клиент для взаимодействия с серверным API. */
    private final ApiClient api = new ApiClient();

    /** Таблица товаров и связанные колонки. */
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Number> colProdId;
    @FXML private TableColumn<Product, String> colProdName;
    @FXML private TableColumn<Product, String> colProdCategory;
    @FXML private TableColumn<Product, Number> colProdQty;

    /** Таблица должностей и связанные колонки. */
    @FXML private TableView<Position> positionTable;
    @FXML private TableColumn<Position, Number> colPosId;
    @FXML private TableColumn<Position, String> colPosName;
    @FXML private TableColumn<Position, Number> colPosSalary;

    /** Фильтры и параметры сортировки товаров. */
    @FXML private TextField tfProductSearch;
    @FXML private ComboBox<Category> cbProductCategory;
    @FXML private ComboBox<String> cbProductSort;

    /** Сопоставление отображаемых вариантов сортировки с параметрами запроса. */
    private final Map<String, String> productSortMap = new LinkedHashMap<>();

    /** Таблица категорий и связанные колонки. */
    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, Number> colCatId;
    @FXML private TableColumn<Category, String> colCatName;

    /** Таблица операций и связанные колонки. */
    @FXML private TableView<Operation> operationTable;
    @FXML private TableColumn<Operation, Number> colOpId;
    @FXML private TableColumn<Operation, String> colOpDate;
    @FXML private TableColumn<Operation, String> colOpProduct;
    @FXML private TableColumn<Operation, String> colOpType;
    @FXML private TableColumn<Operation, Number> colOpQty;
    @FXML private TableColumn<Operation, String> colOpEmployee;
    @FXML private TableColumn<Operation, String> colOpContractor;

    /** Параметры фильтрации операций по периоду и сортировке. */
    @FXML private DatePicker dpOpFrom;
    @FXML private DatePicker dpOpTo;
    @FXML private ComboBox<String> cbOpSort;

    /** Сопоставление отображаемых вариантов сортировки операций с параметрами запроса. */
    private final Map<String, String> opSortMap = new LinkedHashMap<>();

    /** Элементы фильтрации в журнале операций. */
    @FXML private ComboBox<Product> cbOpFilterProduct;
    @FXML private ComboBox<Employee> cbOpFilterEmployee;
    @FXML private ComboBox<OperationType> cbOpFilterType;
    @FXML private ComboBox<Contractor> cbOpFilterContractor;

    /** Элементы формы создания новой операции. */
    @FXML private ComboBox<Product> cbOpProduct;
    @FXML private ComboBox<Employee> cbOpEmployee;
    @FXML private ComboBox<OperationType> cbOpType;
    @FXML private ComboBox<Contractor> cbOpContractor;
    @FXML private TextField tfOpQty;
    @FXML private DatePicker dpOpDate;
    @FXML private TextField tfOpTime;
    @FXML private Label lblOpStatus;

    /** Таблица контрагентов и связанные колонки. */
    @FXML private TableView<Contractor> contractorTable;
    @FXML private TableColumn<Contractor, Number> colContId;
    @FXML private TableColumn<Contractor, String> colContName;
    @FXML private TableColumn<Contractor, String> colContPhone;
    @FXML private TableColumn<Contractor, String> colContAddress;

    /** Таблица сотрудников и связанные колонки. */
    @FXML private TableView<Employee> employeeTable;
    @FXML private TableColumn<Employee, Number> colEmpId;
    @FXML private TableColumn<Employee, String> colEmpPosition;
    @FXML private TableColumn<Employee, String> colEmpLastName;
    @FXML private TableColumn<Employee, String> colEmpFirstName;
    @FXML private TableColumn<Employee, String> colEmpPatronymic;

    /** Элементы вкладки отчетов. */
    @FXML private ComboBox<String> cbStatsReport;
    @FXML private DatePicker dpStatsFrom;
    @FXML private DatePicker dpStatsTo;
    @FXML private Label lblStatsInfo;

    /** Таблица результатов отчета. */
    @FXML private TableView<StatsRow> statsTable;
    @FXML private TableColumn<StatsRow, String> colStatsA;
    @FXML private TableColumn<StatsRow, String> colStatsB;

    /** Вкладки интерфейса для управления доступом по ролям. */
    @FXML private Tab tabCategories;
    @FXML private Tab tabProducts;
    @FXML private Tab tabOperations;
    @FXML private Tab tabNewOperation;
    @FXML private Tab tabContractors;
    @FXML private Tab tabEmployees;
    @FXML private Tab tabPositions;
    @FXML private Tab tabStats;

    /** Кэш должностей для диалогов добавления/редактирования сотрудников. */
    private List<Position> cachedPositions = java.util.Collections.emptyList();

    /** Текущая роль пользователя. */
    private Role role;

    /** Установка роли пользователя и применение ограничений интерфейса. */
    public void setRole(Role role) {
        this.role = role;
        applyRoleVisibility();
    }

    /** Настройка доступности вкладки без удаления из TabPane. */
    private void setTabAllowed(Tab tab, boolean allowed) {
        if (tab == null) return;
        tab.setDisable(!allowed);
        tab.setStyle(allowed ? "" : "-fx-opacity: 0; -fx-pref-width: 0; -fx-max-width: 0; -fx-padding: 0;");
    }

    /** Применение матрицы прав доступа к вкладкам в зависимости от роли. */
    private void applyRoleVisibility() {
        boolean admin = role == Role.ADMIN;

        setTabAllowed(tabCategories, admin || role == Role.MANAGER);
        setTabAllowed(tabProducts, admin || role == Role.MANAGER || role == Role.SKLAD);
        setTabAllowed(tabOperations, admin || role == Role.MANAGER);
        setTabAllowed(tabNewOperation, admin || role == Role.SKLAD);
        setTabAllowed(tabContractors, admin || role == Role.MANAGER);
        setTabAllowed(tabEmployees, admin || role == Role.HIRE);
        setTabAllowed(tabPositions, admin || role == Role.HIRE);
        setTabAllowed(tabStats, admin || role == Role.MANAGER);

        var tp = tabCategories != null ? tabCategories.getTabPane() : null;
        if (tp != null) {
            for (Tab t : tp.getTabs()) {
                if (!t.isDisable()) { tp.getSelectionModel().select(t); break; }
            }
        }
    }

    /** Инициализация таблиц, комбобоксов, параметров сортировки и первичная загрузка данных. */
    @FXML
    public void initialize() {
        colCatId.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().id == null ? 0 : c.getValue().id));
        colCatName.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().name));

        colProdId.setCellValueFactory(p ->
                new SimpleIntegerProperty(p.getValue().id == null ? 0 : p.getValue().id));
        colProdName.setCellValueFactory(p ->
                new SimpleStringProperty(p.getValue().name));
        colProdCategory.setCellValueFactory(p ->
                new SimpleStringProperty(p.getValue().category == null ? "" : p.getValue().category.name));
        colProdQty.setCellValueFactory(p ->
                new SimpleIntegerProperty(p.getValue().quantity == null ? 0 : p.getValue().quantity));

        productSortMap.put("Количество ↓", "quantity,desc");
        productSortMap.put("Количество ↑", "quantity,asc");
        productSortMap.put("Название А → Я", "name,asc");
        productSortMap.put("Название Я → А", "name,desc");
        if (cbProductSort != null) {
            cbProductSort.setItems(FXCollections.observableArrayList(productSortMap.keySet()));
            cbProductSort.getSelectionModel().selectFirst();
        }

        if (colOpId != null) {
            colOpId.setCellValueFactory(o -> new SimpleIntegerProperty(o.getValue().id == null ? 0 : o.getValue().id));
            colOpDate.setCellValueFactory(o -> new SimpleStringProperty(
                    o.getValue().date == null ? "" : o.getValue().date.toString()
            ));
            colOpProduct.setCellValueFactory(o -> new SimpleStringProperty(
                    o.getValue().product == null ? "" : o.getValue().product.name
            ));
            colOpType.setCellValueFactory(o -> new SimpleStringProperty(
                    o.getValue().operationType == null ? "" : o.getValue().operationType.name
            ));
            colOpQty.setCellValueFactory(o -> new SimpleIntegerProperty(
                    o.getValue().quantity == null ? 0 : o.getValue().quantity
            ));
            colOpEmployee.setCellValueFactory(o -> new SimpleStringProperty(
                    o.getValue().employee == null ? "" : formatEmployee(o.getValue().employee)
            ));
            colOpContractor.setCellValueFactory(o -> new SimpleStringProperty(
                    o.getValue().contractor == null ? "" : o.getValue().contractor.name
            ));
        }

        opSortMap.put("Дата ↓", "date,desc");
        opSortMap.put("Дата ↑", "date,asc");
        opSortMap.put("Количество ↓", "quantity,desc");
        opSortMap.put("Количество ↑", "quantity,asc");
        if (cbOpSort != null) {
            cbOpSort.setItems(FXCollections.observableArrayList(opSortMap.keySet()));
            cbOpSort.getSelectionModel().selectFirst();
        }

        if (cbOpProduct != null) setupCombo(cbOpProduct, p -> p == null ? "" : p.name);
        if (cbOpEmployee != null) setupCombo(cbOpEmployee, e -> e == null ? "" : formatEmployee(e));
        if (cbOpType != null) setupCombo(cbOpType, t -> t == null ? "" : t.name);
        if (cbOpContractor != null) setupCombo(cbOpContractor, c -> c == null ? "" : c.name);

        if (cbOpFilterProduct != null) setupCombo(cbOpFilterProduct, p -> p == null ? "" : p.name);
        if (cbOpFilterEmployee != null) setupCombo(cbOpFilterEmployee, e -> e == null ? "" : formatEmployee(e));
        if (cbOpFilterType != null) setupCombo(cbOpFilterType, t -> t == null ? "" : t.name);
        if (cbOpFilterContractor != null) setupCombo(cbOpFilterContractor, c -> c == null ? "" : c.name);

        if (dpOpDate != null) dpOpDate.setValue(LocalDate.now());
        if (tfOpTime != null) tfOpTime.setText(LocalTime.now().withSecond(0).withNano(0).toString());

        reloadCategories();
        reloadOpRefs();

        if (colContId != null) {
            colContId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().id == null ? 0 : c.getValue().id));
            colContName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().name == null ? "" : c.getValue().name));
            colContPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().phone == null ? "" : c.getValue().phone));
            colContAddress.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().address == null ? "" : c.getValue().address));
        }
        reloadContractors();

        if (employeeTable != null) {
            colEmpId.setCellValueFactory(e -> new SimpleIntegerProperty(e.getValue().id == null ? 0 : e.getValue().id));
            colEmpPosition.setCellValueFactory(e -> new SimpleStringProperty(
                    e.getValue().position == null ? "" : e.getValue().position.name
            ));
            colEmpLastName.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().lastName == null ? "" : e.getValue().lastName));
            colEmpFirstName.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().firstName == null ? "" : e.getValue().firstName));
            colEmpPatronymic.setCellValueFactory(e -> new SimpleStringProperty(e.getValue().patronymic == null ? "" : e.getValue().patronymic));

            reloadEmployees();
        }

        if (colPosId != null) {
            colPosId.setCellValueFactory(p ->
                    new SimpleIntegerProperty(p.getValue().id == null ? 0 : p.getValue().id));

            colPosName.setCellValueFactory(p ->
                    new SimpleStringProperty(p.getValue().name == null ? "" : p.getValue().name));

            colPosSalary.setCellValueFactory(p ->
                    new SimpleIntegerProperty(p.getValue().salary == null ? 0 : p.getValue().salary));

            reloadPositions();
        }

        initStatsTab();
    }

    /** Загрузка справочников для формы создания операции и фильтров журнала. */
    @FXML
    public void reloadOpRefs() {
        try {
            var products = api.getProducts(null, null, "name,asc");
            var employees = api.getEmployees();
            var types = api.getOperationTypes();
            var contractors = api.getContractors();

            if (cbOpProduct != null) {
                cbOpProduct.setItems(FXCollections.observableArrayList(products));
                cbOpProduct.getSelectionModel().clearSelection();
            }
            if (cbOpEmployee != null) {
                cbOpEmployee.setItems(FXCollections.observableArrayList(employees));
                cbOpEmployee.getSelectionModel().clearSelection();
            }
            if (cbOpType != null) {
                cbOpType.setItems(FXCollections.observableArrayList(types));
                cbOpType.getSelectionModel().clearSelection();
            }
            if (cbOpContractor != null) {
                cbOpContractor.setItems(FXCollections.observableArrayList(contractors));
                cbOpContractor.getItems().add(0, null);
                cbOpContractor.getSelectionModel().selectFirst();
            }

            if (cbOpFilterProduct != null) {
                cbOpFilterProduct.setItems(FXCollections.observableArrayList(products));
                cbOpFilterProduct.getItems().add(0, null);
                cbOpFilterProduct.getSelectionModel().selectFirst();
            }
            if (cbOpFilterEmployee != null) {
                cbOpFilterEmployee.setItems(FXCollections.observableArrayList(employees));
                cbOpFilterEmployee.getItems().add(0, null);
                cbOpFilterEmployee.getSelectionModel().selectFirst();
            }
            if (cbOpFilterType != null) {
                cbOpFilterType.setItems(FXCollections.observableArrayList(types));
                cbOpFilterType.getItems().add(0, null);
                cbOpFilterType.getSelectionModel().selectFirst();
            }
            if (cbOpFilterContractor != null) {
                cbOpFilterContractor.setItems(FXCollections.observableArrayList(contractors));
                cbOpFilterContractor.getItems().add(0, null);
                cbOpFilterContractor.getSelectionModel().selectFirst();
            }

            if (lblOpStatus != null) lblOpStatus.setText("Справочники загружены.");

        } catch (Exception e) {
            showError("Не удалось загрузить справочники для операций", e);
        }
    }

    /** Сброс формы создания операции к значениям по умолчанию. */
    @FXML
    public void clearOperationForm() {
        if (cbOpProduct != null) cbOpProduct.getSelectionModel().clearSelection();
        if (cbOpEmployee != null) cbOpEmployee.getSelectionModel().clearSelection();
        if (cbOpType != null) cbOpType.getSelectionModel().clearSelection();
        if (cbOpContractor != null) cbOpContractor.getSelectionModel().selectFirst();
        if (tfOpQty != null) tfOpQty.clear();
        if (dpOpDate != null) dpOpDate.setValue(LocalDate.now());
        if (tfOpTime != null) tfOpTime.setText("14:30");
        if (lblOpStatus != null) lblOpStatus.setText("");
    }

    /** Загрузка списка сотрудников и справочника должностей. */
    @FXML
    public void reloadEmployees() {
        try {
            cachedPositions = api.getPositions();
            var emps = api.getEmployees();
            employeeTable.setItems(FXCollections.observableArrayList(emps));
        } catch (Exception e) {
            showError("Не удалось загрузить сотрудников/должности", e);
        }
    }

    /** Добавление сотрудника через диалог ввода. */
    @FXML
    public void addEmployee() {
        try {
            if (cachedPositions == null || cachedPositions.isEmpty()) cachedPositions = api.getPositions();

            EmployeeDialog dlg = new EmployeeDialog(null, cachedPositions);
            dlg.showAndWait().ifPresent(dto -> {
                try {
                    api.createEmployee(dto);
                    reloadEmployees();
                    showSuccess("Сохранено", "Сотрудник добавлен.");
                } catch (Exception e) {
                    showError("Не удалось добавить сотрудника", e);
                }
            });
        } catch (Exception e) {
            showError("Ошибка при добавлении сотрудника", e);
        }
    }

    /** Редактирование выбранного сотрудника. */
    @FXML
    public void editEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) { info("Выбери сотрудника в таблице"); return; }

        try {
            if (cachedPositions == null || cachedPositions.isEmpty()) cachedPositions = api.getPositions();

            EmployeeDialog dlg = new EmployeeDialog(selected, cachedPositions);
            dlg.showAndWait().ifPresent(dto -> {
                try {
                    api.updateEmployee(selected.id, dto);
                    reloadEmployees();
                    showSuccess("Сохранено", "Данные сотрудника обновлены.");
                } catch (Exception e) {
                    showError("Не удалось обновить сотрудника", e);
                }
            });
        } catch (Exception e) {
            showError("Ошибка при редактировании сотрудника", e);
        }
    }

    /** Удаление выбранного сотрудника. */
    @FXML
    public void deleteEmployee() {
        Employee selected = employeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) { info("Выбери сотрудника в таблице"); return; }
        if (!confirm("Удалить сотрудника: " + formatEmployee(selected) + "?")) return;

        try {
            api.deleteEmployee(selected.id);
            reloadEmployees();
            showSuccess("Удалено", "Сотрудник удалён.");
        } catch (Exception e) {
            showError("Не удалось удалить сотрудника", e);
        }
    }

    /** Создание операции через DTO, соответствующий контракту серверного API. */
    @FXML
    public void createOperation() {
        try {
            Product product = cbOpProduct.getValue();
            Employee employee = cbOpEmployee.getValue();
            OperationType type = cbOpType.getValue();
            Contractor contractor = cbOpContractor.getValue();

            if (product == null || product.id == null) { info("Выбери товар"); return; }
            if (employee == null || employee.id == null) { info("Выбери сотрудника"); return; }
            if (type == null || type.id == null) { info("Выбери тип операции"); return; }

            int qty;
            try {
                qty = Integer.parseInt(tfOpQty.getText().trim());
            } catch (Exception ex) {
                info("Количество должно быть целым числом");
                return;
            }
            if (qty <= 0) { info("Количество должно быть > 0"); return; }

            LocalDate date = dpOpDate.getValue();
            if (date == null) { info("Выбери дату"); return; }

            LocalTime time;
            try {
                time = LocalTime.parse(tfOpTime.getText().trim());
            } catch (DateTimeParseException ex) {
                info("Время должно быть в формате HH:mm (например 14:30)");
                return;
            }

            LocalDateTime dt = LocalDateTime.of(date, time).withNano(0);

            OperationCreateRequest dto = new OperationCreateRequest();
            dto.productId = product.id;
            dto.employeeId = employee.id;
            dto.contractorId = (contractor == null || contractor.id == null) ? null : contractor.id;
            dto.operationTypeId = type.id;
            dto.quantity = qty;
            dto.date = dt.toString();

            api.createOperation(dto);

            showSuccess("Операция выполнена", "Операция успешно сохранена в журнале.");

            applyOperationFilter();
            applyProductFilter();
            clearOperationForm();

        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().startsWith("HTTP 400")) {
                String serverMsg = extractServerMessage(e);
                showWarningModal("Операция не выполнена", prettifyBadRequest(serverMsg));
                return;
            }
            showError("Не удалось сохранить операцию", e);
        }
    }

    /** Загрузка категорий и инициализация фильтра по категориям в товарах. */
    @FXML
    public void reloadCategories() {
        try {
            var list = api.getCategories();
            categoryTable.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            showError("Не удалось загрузить категории", e);
        }

        if (cbProductCategory != null) {
            cbProductCategory.setItems(FXCollections.observableArrayList(categoryTable.getItems()));
            cbProductCategory.getItems().add(0, null);
            cbProductCategory.getSelectionModel().selectFirst();
        }

        applyProductFilter();
        applyOperationFilter();
    }

    /** Добавление категории через диалог ввода. */
    @FXML
    public void addCategory() {
        try {
            CategoryDialog dialog = new CategoryDialog(null);
            dialog.showAndWait().ifPresent(c -> {
                try {
                    api.createCategory(c);
                    reloadCategories();
                } catch (Exception e) {
                    showError("Не удалось создать категорию", e);
                }
            });
        } catch (Exception e) {
            showError("Ошибка при создании категории", e);
        }
    }

    /** Редактирование выбранной категории. */
    @FXML
    public void editCategory() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) { info("Выбери категорию в таблице"); return; }

        try {
            CategoryDialog dialog = new CategoryDialog(selected);
            dialog.showAndWait().ifPresent(c -> {
                try {
                    api.updateCategory(selected.id, c);
                    reloadCategories();
                } catch (Exception e) {
                    showError("Не удалось обновить категорию", e);
                }
            });
        } catch (Exception e) {
            showError("Ошибка при редактировании категории", e);
        }
    }

    /** Удаление выбранной категории. */
    @FXML
    public void deleteCategory() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) { info("Выбери категорию в таблице"); return; }
        if (!confirm("Удалить категорию: " + selected.name + "?")) return;

        try {
            api.deleteCategory(selected.id);
            reloadCategories();
        } catch (Exception e) {
            showError("Не удалось удалить категорию (возможно есть товары с этой категорией)", e);
        }
    }

    /** Применение фильтров и сортировки товаров с запросом к серверу. */
    @FXML
    public void applyProductFilter() {
        try {
            String name = tfProductSearch.getText();
            Category c = cbProductCategory.getValue();
            String sortKey = cbProductSort.getValue();
            String sort = productSortMap.get(sortKey);

            var list = api.getProducts(name, c == null ? null : c.id, sort);
            productTable.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            showError("Не удалось загрузить товары", e);
        }
    }

    /** Добавление товара через диалог ввода. */
    @FXML
    public void addProduct() {
        try {
            var cats = categoryTable.getItems();
            ProductDialog dialog = new ProductDialog(null, cats);
            dialog.showAndWait().ifPresent(p -> {
                try {
                    api.createProduct(p);
                    applyProductFilter();
                } catch (Exception e) {
                    showError("Не удалось создать товар", e);
                }
            });
        } catch (Exception e) {
            showError("Ошибка при создании товара", e);
        }
    }

    /** Загрузка списка должностей. */
    @FXML
    public void reloadPositions() {
        try {
            var list = api.getPositions();
            positionTable.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            showError("Не удалось загрузить должности", e);
        }
    }

    /** Добавление должности через диалог ввода. */
    @FXML
    public void addPosition() {
        try {
            PositionDialog dialog = new PositionDialog(null);
            dialog.showAndWait().ifPresent(p -> {
                try {
                    api.createPosition(p);
                    reloadPositions();
                } catch (Exception e) {
                    showError("Не удалось создать должность", e);
                }
            });
        } catch (Exception e) {
            showError("Ошибка при добавлении должности", e);
        }
    }

    /** Построение выбранного отчета на основе операций за период. */
    @FXML
    public void buildStats() {
        try {
            String rep = cbStatsReport.getValue();
            LocalDateTime from = (dpStatsFrom.getValue() == null) ? null : dpStatsFrom.getValue().atStartOfDay();
            LocalDateTime to = (dpStatsTo.getValue() == null) ? null : dpStatsTo.getValue().atTime(23,59,59);

            List<Operation> ops = api.getOperations(null, null, null, null, from, to, "date,desc");

            List<StatsRow> rows;
            if ("Сотрудники: количество операций".equals(rep)) {
                rows = reportOpsByEmployee(ops);
            } else if ("Товары: топ-10 по движению".equals(rep)) {
                rows = reportTopProductsByMovement(ops, 10);
            } else if ("Товары: сколько отгружено".equals(rep)) {
                rows = reportShippedByProduct(ops, 50);
            } else if ("Контрагенты: количество операций".equals(rep)) {
                rows = reportOpsByContractor(ops);
            } else {
                rows = List.of(new StatsRow("Неизвестный отчёт", ""));
            }

            statsTable.setItems(FXCollections.observableArrayList(rows));
            lblStatsInfo.setText("Операций в периоде: " + ops.size());

        } catch (Exception e) {
            showError("Не удалось построить статистику", e);
        }
    }

    /** Сброс параметров отчета к значениям по умолчанию. */
    @FXML
    public void resetStats() {
        if (cbStatsReport == null) return;
        cbStatsReport.getSelectionModel().selectFirst();
        dpStatsFrom.setValue(LocalDate.now().minusDays(30));
        dpStatsTo.setValue(LocalDate.now());
        statsTable.setItems(FXCollections.observableArrayList());
        lblStatsInfo.setText("Сброшено.");
    }

    /** Формирование отчета по количеству операций на сотрудника. */
    private List<StatsRow> reportOpsByEmployee(List<Operation> ops) {
        HashMap<String, Integer> cnt = new HashMap<>();
        for (Operation o : ops) {
            String name = (o.employee == null) ? "(нет сотрудника)" : formatEmployee(o.employee);
            cnt.put(name, cnt.getOrDefault(name, 0) + 1);
        }

        return cnt.entrySet().stream()
                .sorted((a,b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(e -> new StatsRow(e.getKey(), String.valueOf(e.getValue())))
                .collect(Collectors.toList());
    }

    /** Формирование отчета по количеству операций на контрагента. */
    private List<StatsRow> reportOpsByContractor(List<Operation> ops) {
        HashMap<String, Integer> cnt = new HashMap<>();
        for (Operation o : ops) {
            String name = (o.contractor == null || o.contractor.name == null || o.contractor.name.isBlank())
                    ? "(без контрагента)"
                    : o.contractor.name;
            cnt.put(name, cnt.getOrDefault(name, 0) + 1);
        }

        return cnt.entrySet().stream()
                .sorted((a,b) -> Integer.compare(b.getValue(), a.getValue()))
                .map(e -> new StatsRow(e.getKey(), String.valueOf(e.getValue())))
                .collect(Collectors.toList());
    }

    /** Формирование отчета по суммарному движению товара (приход и расход). */
    private List<StatsRow> reportTopProductsByMovement(List<Operation> ops, int topN) {
        HashMap<String, Integer> sum = new HashMap<>();
        for (Operation o : ops) {
            String prod = (o.product == null || o.product.name == null) ? "(нет товара)" : o.product.name;
            int q = (o.quantity == null) ? 0 : Math.abs(o.quantity);
            sum.put(prod, sum.getOrDefault(prod, 0) + q);
        }

        return sum.entrySet().stream()
                .sorted((a,b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(topN)
                .map(e -> new StatsRow(e.getKey(), String.valueOf(e.getValue())))
                .collect(Collectors.toList());
    }

    /** Подготовка отчета по отгруженному количеству товара. */
    private List<StatsRow> reportShippedByProduct(List<Operation> ops, int topN) {
        Map<String, Integer> shipped = new HashMap<>();

        for (Operation o : ops) {
            if (o == null || o.operationType == null || o.operationType.name == null) continue;
            if (!o.operationType.name.equalsIgnoreCase("Отгрузка")) continue;

            String productName = (o.product == null || o.product.name == null)
                    ? "(нет товара)"
                    : o.product.name;

            int qty = o.quantity == null ? 0 : o.quantity;
            shipped.put(productName, shipped.getOrDefault(productName, 0) + qty);
        }

        return shipped.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(topN)
                .map(e -> new StatsRow(e.getKey(), String.valueOf(e.getValue())))
                .toList();
    }

    /** Редактирование выбранной должности. */
    @FXML
    public void editPosition() {
        Position selected = positionTable.getSelectionModel().getSelectedItem();
        if (selected == null) { info("Выбери должность в таблице"); return; }

        try {
            PositionDialog dialog = new PositionDialog(selected);
            dialog.showAndWait().ifPresent(p -> {
                try {
                    api.updatePosition(selected.id, p);
                    reloadPositions();
                } catch (Exception e) {
                    showError("Не удалось обновить должность", e);
                }
            });
        } catch (Exception e) {
            showError("Ошибка при редактировании должности", e);
        }
    }

    /** Удаление выбранной должности. */
    @FXML
    public void deletePosition() {
        Position selected = positionTable.getSelectionModel().getSelectedItem();
        if (selected == null) { info("Выбери должность в таблице"); return; }
        if (!confirm("Удалить должность: " + selected.name + "?")) return;

        try {
            api.deletePosition(selected.id);
            reloadPositions();
        } catch (Exception e) {
            showError("Не удалось удалить должность (возможно она используется сотрудниками)", e);
        }
    }

    /** Редактирование выбранного товара. */
    @FXML
    public void editProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) { info("Выбери товар в таблице"); return; }

        try {
            var cats = categoryTable.getItems();
            ProductDialog dialog = new ProductDialog(selected, cats);
            dialog.showAndWait().ifPresent(p -> {
                try {
                    api.updateProduct(selected.id, p);
                    applyProductFilter();
                } catch (Exception e) {
                    showError("Не удалось обновить товар", e);
                }
            });
        } catch (Exception e) {
            showError("Ошибка при редактировании товара", e);
        }
    }

    /** Удаление выбранного товара. */
    @FXML
    public void deleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) { info("Выбери товар в таблице"); return; }
        if (!confirm("Удалить товар: " + selected.name + "?")) return;

        try {
            api.deleteProduct(selected.id);
            applyProductFilter();
        } catch (Exception e) {
            showError("Не удалось удалить товар", e);
        }
    }

    /** Обновление журнала операций с учетом заданных фильтров. */
    @FXML
    public void reloadOperations() {
        applyOperationFilter();
    }

    /** Запрос списка операций по выбранным условиям фильтрации и сортировки. */
    @FXML
    public void applyOperationFilter() {
        if (operationTable == null) return;

        try {
            Integer productId = (cbOpFilterProduct == null || cbOpFilterProduct.getValue() == null) ? null : cbOpFilterProduct.getValue().id;
            Integer employeeId = (cbOpFilterEmployee == null || cbOpFilterEmployee.getValue() == null) ? null : cbOpFilterEmployee.getValue().id;
            Integer operationTypeId = (cbOpFilterType == null || cbOpFilterType.getValue() == null) ? null : cbOpFilterType.getValue().id;
            Integer contractorId = (cbOpFilterContractor == null || cbOpFilterContractor.getValue() == null) ? null : cbOpFilterContractor.getValue().id;

            LocalDateTime from = null;
            LocalDateTime to = null;

            LocalDate dFrom = dpOpFrom == null ? null : dpOpFrom.getValue();
            LocalDate dTo = dpOpTo == null ? null : dpOpTo.getValue();
            if (dFrom != null) from = dFrom.atStartOfDay();
            if (dTo != null) to = dTo.atTime(23, 59, 59);

            String sortKey = cbOpSort == null ? null : cbOpSort.getValue();
            String sort = (sortKey == null) ? null : opSortMap.get(sortKey);

            var list = api.getOperations(productId, employeeId, contractorId, operationTypeId, from, to, sort);
            operationTable.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            showError("Не удалось загрузить операции", e);
        }
    }

    /** Сброс фильтров журнала операций. */
    @FXML
    public void resetOperationFilter() {
        if (operationTable == null) return;

        if (cbOpFilterProduct != null) cbOpFilterProduct.getSelectionModel().selectFirst();
        if (cbOpFilterEmployee != null) cbOpFilterEmployee.getSelectionModel().selectFirst();
        if (cbOpFilterType != null) cbOpFilterType.getSelectionModel().selectFirst();
        if (cbOpFilterContractor != null) cbOpFilterContractor.getSelectionModel().selectFirst();

        if (dpOpFrom != null) dpOpFrom.setValue(null);
        if (dpOpTo != null) dpOpTo.setValue(null);
        if (cbOpSort != null) cbOpSort.getSelectionModel().selectFirst();

        applyOperationFilter();
    }

    /** Вывод подсказки по добавлению операций. */
    @FXML
    public void addOperation() {
        info("Добавление операции выполняется на вкладке 'Новая операция'.");
    }

    /** Удаление выбранной операции. */
    @FXML
    public void deleteOperation() {
        if (operationTable == null) return;

        Operation selected = operationTable.getSelectionModel().getSelectedItem();
        if (selected == null) { info("Выбери операцию в таблице"); return; }
        if (!confirm("Удалить операцию ID=" + selected.id + "?")) return;

        try {
            api.deleteOperation(selected.id);
            applyOperationFilter();
        } catch (Exception e) {
            showError("Не удалось удалить операцию", e);
        }
    }

    /** Настройка отображения значений в комбобоксах. */
    private <T> void setupCombo(ComboBox<T> cb, java.util.function.Function<T, String> textFn) {
        if (cb == null) return;

        cb.setConverter(new StringConverter<>() {
            @Override public String toString(T obj) { return obj == null ? "" : textFn.apply(obj); }
            @Override public T fromString(String s) { return null; }
        });

        cb.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : textFn.apply(item));
            }
        });

        cb.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : textFn.apply(item));
            }
        });
    }

    /** Формирование отображаемого ФИО сотрудника. */
    private String formatEmployee(Employee e) {
        if (e == null) return "";
        String ln = e.lastName == null ? "" : e.lastName;
        String fn = e.firstName == null ? "" : e.firstName;
        String pt = e.patronymic == null ? "" : e.patronymic;
        return (ln + " " + fn + " " + pt).trim();
    }

    /** Инициализация параметров вкладки отчетов. */
    private void initStatsTab() {
        if (cbStatsReport == null) return;

        colStatsA.setCellValueFactory(r -> new SimpleObjectProperty<>(r.getValue().a));
        colStatsB.setCellValueFactory(r -> new SimpleObjectProperty<>(r.getValue().b));

        cbStatsReport.setItems(FXCollections.observableArrayList(
                "Сотрудники: количество операций",
                "Товары: топ-10 по движению",
                "Товары: сколько отгружено",
                "Контрагенты: количество операций"
        ));
        cbStatsReport.getSelectionModel().selectFirst();

        dpStatsFrom.setValue(LocalDate.now().minusDays(30));
        dpStatsTo.setValue(LocalDate.now());

        lblStatsInfo.setText("Выбери отчёт и период, нажми «Построить».");
    }

    /** Окно справочной информации. */
    @FXML
    public void onAbout() {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Об авторе");
        a.setHeaderText("ИСС оптового склада");
        a.setContentText("Автор: Журавлев Олег Игоревич\nГруппа: ДПИ23-1с\n2025");
        a.showAndWait();
    }

    /** Вывод сообщения об ошибке. */
    private void showError(String title, Exception e) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Ошибка");
        a.setHeaderText(title);
        a.setContentText(e.getMessage());
        a.showAndWait();
    }

    /** Вывод информационного сообщения об успешном действии. */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    /** Вывод предупреждения пользователю. */
    private void showWarningModal(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    /** Извлечение сообщения об ошибке из ответа сервера. */
    private String extractServerMessage(Exception e) {
        String msg = (e == null || e.getMessage() == null) ? "" : e.getMessage();
        int idx = msg.indexOf(":");
        String body = (idx >= 0 && idx + 1 < msg.length()) ? msg.substring(idx + 1).trim() : msg;

        String extracted = extractJsonField(body, "message");
        if (extracted == null || extracted.isBlank()) extracted = extractJsonField(body, "detail");
        if (extracted == null || extracted.isBlank()) extracted = extractJsonField(body, "reason");

        return (extracted != null && !extracted.isBlank()) ? extracted : body;
    }

    /** Извлечение текстового поля из JSON-строки простым поиском. */
    private String extractJsonField(String json, String field) {
        if (json == null) return null;
        String key = "\"" + field + "\"";
        int k = json.indexOf(key);
        if (k < 0) return null;
        int colon = json.indexOf(':', k);
        if (colon < 0) return null;
        int q1 = json.indexOf('"', colon + 1);
        if (q1 < 0) return null;
        int q2 = json.indexOf('"', q1 + 1);
        if (q2 <= q1) return null;
        return json.substring(q1 + 1, q2);
    }

    /** Приведение сообщения об ошибке к пользовательскому формату. */
    private String prettifyBadRequest(String serverMsg) {
        if (serverMsg == null) serverMsg = "";
        String lower = serverMsg.toLowerCase();

        if (lower.contains("сейчас") && lower.contains("треб")) {
            Integer has = extractFirstIntAfter(serverMsg, "Сейчас");
            Integer need = extractFirstIntAfter(serverMsg, "треб");
            if (has != null || need != null) {
                StringBuilder sb = new StringBuilder();
                if (has != null) sb.append("Есть на складе: ").append(has).append("\n");
                if (need != null) sb.append("Требуется: ").append(need);
                return sb.toString().trim();
            }
        }

        if (serverMsg.contains("\"timestamp\"") && serverMsg.contains("\"status\"")) {
            return "Некорректная операция (400). Проверь введённые данные.";
        }

        return serverMsg;
    }

    /** Поиск первого целого числа после указанного маркера. */
    private Integer extractFirstIntAfter(String text, String marker) {
        int i = text.toLowerCase().indexOf(marker.toLowerCase());
        if (i < 0) return null;
        String sub = text.substring(i);
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("(\\d+)").matcher(sub);
        return m.find() ? Integer.parseInt(m.group(1)) : null;
    }

    /** Вывод информационного сообщения. */
    private void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Информация");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    /** Диалог подтверждения действия. */
    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Подтверждение");
        a.setHeaderText(msg);
        return a.showAndWait().filter(b -> b == ButtonType.OK).isPresent();
    }

    /** Кодирование параметров для использования в URL. */
    private String urlEncode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    /** Загрузка списка контрагентов. */
    @FXML
    public void reloadContractors() {
        try {
            var list = api.getContractors();
            contractorTable.setItems(FXCollections.observableArrayList(list));
        } catch (Exception e) {
            showError("Не удалось загрузить контрагентов", e);
        }
    }

    /** Добавление контрагента через диалог ввода. */
    @FXML
    public void addContractor() {
        try {
            ContractorDialog dialog = new ContractorDialog(null);
            dialog.showAndWait().ifPresent(c -> {
                try {
                    api.createContractor(c);
                    reloadContractors();
                    reloadOpRefs();
                } catch (Exception e) {
                    showError("Не удалось создать контрагента", e);
                }
            });
        } catch (Exception e) {
            showError("Ошибка при создании контрагента", e);
        }
    }

    /** Редактирование выбранного контрагента. */
    @FXML
    public void editContractor() {
        Contractor selected = contractorTable.getSelectionModel().getSelectedItem();
        if (selected == null) { info("Выбери контрагента в таблице"); return; }

        try {
            ContractorDialog dialog = new ContractorDialog(selected);
            dialog.showAndWait().ifPresent(c -> {
                try {
                    api.updateContractor(selected.id, c);
                    reloadContractors();
                    reloadOpRefs();
                } catch (Exception e) {
                    showError("Не удалось обновить контрагента", e);
                }
            });
        } catch (Exception e) {
            showError("Ошибка при редактировании контрагента", e);
        }
    }

    /** Удаление выбранного контрагента. */
    @FXML
    public void deleteContractor() {
        Contractor selected = contractorTable.getSelectionModel().getSelectedItem();
        if (selected == null) { info("Выбери контрагента в таблице"); return; }
        if (!confirm("Удалить контрагента: " + selected.name + "?")) return;

        try {
            api.deleteContractor(selected.id);
            reloadContractors();
            reloadOpRefs();
        } catch (Exception e) {
            showError("Не удалось удалить контрагента (возможно он используется в операциях)", e);
        }
    }
}
