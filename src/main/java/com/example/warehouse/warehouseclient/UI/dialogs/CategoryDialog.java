package com.example.warehouse.warehouseclient.UI.dialogs;

import com.example.warehouse.warehouseclient.model.Category;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class CategoryDialog extends Dialog<Category> {

    /** Диалог создания/редактирования категории. */
    public CategoryDialog(Category existing) {
        setTitle(existing == null ? "Добавить категорию" : "Изменить категорию");

        TextField name = new TextField(existing == null ? "" : existing.name);

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.addRow(0, new Label("Название:"), name);

        getDialogPane().setContent(gp);
        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        setResultConverter(bt -> {
            if (bt != ButtonType.OK) return null;

            String n = name.getText() == null ? "" : name.getText().trim();
            if (n.isBlank()) throw new IllegalArgumentException("Название не может быть пустым");

            Category c = new Category();
            c.id = existing == null ? null : existing.id;
            c.name = n;
            return c;
        });
    }
}
