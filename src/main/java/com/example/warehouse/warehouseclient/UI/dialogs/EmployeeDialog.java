package com.example.warehouse.warehouseclient.UI.dialogs;

import com.example.warehouse.warehouseclient.model.Employee;
import com.example.warehouse.warehouseclient.model.EmployeeRequest;
import com.example.warehouse.warehouseclient.model.Position;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

import java.util.List;

public class EmployeeDialog extends Dialog<EmployeeRequest> {

    /** Диалог создания и редактирования сотрудника. */
    public EmployeeDialog(Employee existing, List<Position> positions) {
        setTitle(existing == null ? "Добавить сотрудника" : "Редактировать сотрудника");

        ButtonType ok = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        ComboBox<Position> cbPos = new ComboBox<>(FXCollections.observableArrayList(positions));
        TextField tfLast = new TextField();
        TextField tfFirst = new TextField();
        TextField tfPatr = new TextField();

        cbPos.setPrefWidth(260);
        tfLast.setPrefWidth(260);

        /* Отображение должности в выпадающем списке. */
        cbPos.setConverter(new StringConverter<>() {
            @Override public String toString(Position p) {
                if (p == null) return "";
                return p.name != null ? p.name : ("ID=" + p.id);
            }
            @Override public Position fromString(String s) { return null; }
        });

        cbPos.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Position item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : (item.name != null ? item.name : ("ID=" + item.id)));
            }
        });

        /* Заполнение формы при редактировании. */
        if (existing != null) {
            if (existing.position != null && existing.position.id != null) {
                for (Position p : positions) {
                    if (p != null && p.id != null && p.id.equals(existing.position.id)) {
                        cbPos.getSelectionModel().select(p);
                        break;
                    }
                }
            }
            tfLast.setText(safe(existing.lastName));
            tfFirst.setText(safe(existing.firstName));
            tfPatr.setText(safe(existing.patronymic));
        } else {
            if (!positions.isEmpty()) cbPos.getSelectionModel().selectFirst();
        }

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Должность:"), 0, 0);
        grid.add(cbPos, 1, 0);

        grid.add(new Label("Фамилия:"), 0, 1);
        grid.add(tfLast, 1, 1);

        grid.add(new Label("Имя:"), 0, 2);
        grid.add(tfFirst, 1, 2);

        grid.add(new Label("Отчество:"), 0, 3);
        grid.add(tfPatr, 1, 3);

        getDialogPane().setContent(grid);

        /* Проверка обязательных полей перед сохранением. */
        Node okBtn = getDialogPane().lookupButton(ok);
        okBtn.disableProperty().bind(
                cbPos.valueProperty().isNull()
                        .or(tfLast.textProperty().isEmpty())
                        .or(tfFirst.textProperty().isEmpty())
        );

        /* Формирование DTO для отправки на сервер. */
        setResultConverter(btn -> {
            if (btn != ok) return null;

            Position p = cbPos.getValue();

            EmployeeRequest dto = new EmployeeRequest();

            // Передача должности в формате { position: { id: ... } }
            if (p != null && p.id != null) {
                dto.position = new Position();
                dto.position.id = p.id;
            } else {
                dto.position = null;
            }

            dto.lastName = tfLast.getText().trim();
            dto.firstName = tfFirst.getText().trim();

            String patr = tfPatr.getText() == null ? "" : tfPatr.getText().trim();
            dto.patronymic = patr.isBlank() ? null : patr;

            return dto;
        });
    }

    /** Преобразование null в пустую строку для отображения в полях ввода. */
    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
