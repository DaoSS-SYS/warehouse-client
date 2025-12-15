package com.example.warehouse.warehouseclient.UI.dialogs;

import com.example.warehouse.warehouseclient.model.Contractor;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class ContractorDialog extends Dialog<Contractor> {

    /** Диалог создания и редактирования контрагента. */
    public ContractorDialog(Contractor existing) {
        setTitle(existing == null ? "Добавить контрагента" : "Изменить контрагента");

        ButtonType ok = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        TextField tfName = new TextField(existing == null ? "" : safe(existing.name));
        TextField tfPhone = new TextField(existing == null ? "" : safe(existing.phone));
        TextField tfAddress = new TextField(existing == null ? "" : safe(existing.address));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.add(new Label("Название*:"), 0, 0);
        grid.add(tfName, 1, 0);
        grid.add(new Label("Телефон:"), 0, 1);
        grid.add(tfPhone, 1, 1);
        grid.add(new Label("Адрес:"), 0, 2);
        grid.add(tfAddress, 1, 2);

        getDialogPane().setContent(grid);

        Node okBtn = getDialogPane().lookupButton(ok);
        okBtn.disableProperty().bind(tfName.textProperty().isEmpty());

        setResultConverter(btn -> {
            if (btn != ok) return null;

            Contractor c = new Contractor();
            c.id = existing == null ? null : existing.id;
            c.name = tfName.getText().trim();
            c.phone = emptyToNull(tfPhone.getText());
            c.address = emptyToNull(tfAddress.getText());
            return c;
        });
    }

    /** Защита от null при инициализации полей. */
    private static String safe(String s) {
        return s == null ? "" : s;
    }

    /** Преобразование пустой строки в null. */
    private static String emptyToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
