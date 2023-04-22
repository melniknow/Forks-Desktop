package com.melniknow.fd.ui.panels.impl;

import com.melniknow.fd.context.Context;
import com.melniknow.fd.ui.Controller;
import com.melniknow.fd.ui.panels.IPanel;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Window;

public class BookmakersPanel implements IPanel {
    @Override
    public GridPane getGrid() {
        var grid = new GridPane();

        grid.setAlignment(Pos.BASELINE_CENTER);
        grid.setPadding(new Insets(20, 20, 20, 20));
        grid.setHgap(10);
        grid.setVgap(10);

        ColumnConstraints columnOneConstraints = new ColumnConstraints(400, 400, Double.MAX_VALUE);
        columnOneConstraints.setHalignment(HPos.RIGHT);

        ColumnConstraints columnTwoConstrains = new ColumnConstraints(400, 400, Double.MAX_VALUE);
        columnTwoConstrains.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(columnOneConstraints, columnTwoConstrains);

        var saveButton = new Button("Сохранить");
        saveButton.setPrefHeight(40);
        saveButton.setDefaultButton(true);
        saveButton.setPrefWidth(150);
        grid.add(saveButton, 0, 1, 2, 1);
        GridPane.setHalignment(saveButton, HPos.CENTER);

        saveButton.setOnAction(event -> {

            Controller.session.setDisable(false);
            Controller.runButton.setDisable(false);

            showSuccessAlert(grid.getScene().getWindow());
            Context.betsParams = null;
        });

        return grid;
    }

    private void showErrorAlert(Window owner) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка!");
        alert.setHeaderText(null);
        alert.setContentText("Корректно заполните все необходимые поля!");
        alert.initOwner(owner);
        alert.show();
    }

    private void showSuccessAlert(Window owner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setHeaderText(null);
        alert.setContentText("Все настройки сохранены!");
        alert.initOwner(owner);
        alert.show();
    }
}
