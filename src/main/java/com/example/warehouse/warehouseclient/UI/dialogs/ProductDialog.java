package com.example.warehouse.warehouseclient.UI.dialogs;

import com.example.warehouse.warehouseclient.model.Category;
import com.example.warehouse.warehouseclient.model.Product;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.util.List;

public class ProductDialog extends Dialog<Product> {

    /** Диалог создания и редактирования товара. */
    public ProductDialog(Product existing, List<Category> categories) {
        setTitle(existing == null ? "Добавить товар" : "Изменить товар");

        TextField name = new TextField(existing == null ? "" : safe(existing.name));

        int initQty = (existing == null || existing.quantity == null) ? 0 : existing.quantity;
        Spinner<Integer> qty = new Spinner<>(0, 1_000_000, initQty);
        qty.setEditable(true);

        ComboBox<Category> cat = new ComboBox<>(FXCollections.observableArrayList(categories));
        cat.setPrefWidth(280);

        /* Выбор категории при редактировании. */
        if (existing != null && existing.category != null && existing.category.id != null) {
            for (Category c : categories) {
                if (c != null && c.id != null && c.id.equals(existing.category.id)) {
                    cat.getSelectionModel().select(c);
                    break;
                }
            }
        }

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        gp.addRow(0, new Label("Название:"), name);
        gp.addRow(1, new Label("Категория:"), cat);
        gp.addRow(2, new Label("Количество:"), qty);

        getDialogPane().setContent(gp);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        /* Валидация ввода и формирование объекта товара. */
        setResultConverter(bt -> {
            if (bt != ButtonType.OK) return null;

            String n = name.getText() == null ? "" : name.getText().trim();
            if (n.isBlank()) throw new IllegalArgumentException("Название не может быть пустым");
            if (cat.getValue() == null) throw new IllegalArgumentException("Выбери категорию");

            Product p = new Product();
            p.id = existing == null ? null : existing.id;
            p.name = n;
            p.category = cat.getValue();
            p.quantity = qty.getValue();
            return p;
        });
    }

    /** Преобразование null в пустую строку для корректного отображения в форме. */
    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
