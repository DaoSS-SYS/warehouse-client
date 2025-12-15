package com.example.warehouse.warehouseclient.UI.dialogs;

import com.example.warehouse.warehouseclient.model.Position;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class PositionDialog extends Dialog<Position> {

    /** Диалог создания и редактирования должности. */
    public PositionDialog(Position existing) {
        setTitle(existing == null ? "Добавить должность" : "Редактировать должность");

        ButtonType ok = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        TextField tfName = new TextField();
        TextField tfSalary = new TextField();

        tfName.setPrefWidth(260);
        tfSalary.setPrefWidth(260);
        tfSalary.setPromptText("Например 60000");

        /* Заполнение формы при редактировании. */
        if (existing != null) {
            tfName.setText(safe(existing.name));
            if (existing.salary != null) tfSalary.setText(existing.salary.toString());
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Название:"), 0, 0);
        grid.add(tfName, 1, 0);

        grid.add(new Label("Зарплата:"), 0, 1);
        grid.add(tfSalary, 1, 1);

        getDialogPane().setContent(grid);

        /* Проверка обязательных полей перед сохранением. */
        Node okBtn = getDialogPane().lookupButton(ok);
        okBtn.disableProperty().bind(
                tfName.textProperty().isEmpty()
                        .or(tfSalary.textProperty().isEmpty())
        );

        /* Формирование объекта должности и валидация зарплаты. */
        setResultConverter(btn -> {
            if (btn != ok) return null;

            Integer salary = parseSalary(tfSalary.getText());
            if (salary == null) return null;

            Position p = new Position();
            p.id = existing == null ? null : existing.id;
            p.name = tfName.getText().trim();
            p.salary = salary;
            return p;
        });
    }

    /** Преобразование null в пустую строку для отображения в полях ввода. */
    private static String safe(String s) {
        return s == null ? "" : s;
    }

    /** Проверка и преобразование значения зарплаты. */
    private Integer parseSalary(String raw) {
        int salary;
        try {
            salary = Integer.parseInt(raw.trim());
        } catch (Exception e) {
            showAlert("Зарплата должна быть числом.");
            return null;
        }

        if (salary < 0) {
            showAlert("Зарплата не может быть отрицательной.");
            return null;
        }

        return salary;
    }

    /** Вывод сообщения об ошибке ввода. */
    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle("Ошибка ввода");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
