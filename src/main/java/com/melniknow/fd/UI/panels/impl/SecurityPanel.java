package com.melniknow.fd.UI.panels.impl;

import com.melniknow.fd.UI.panels.IPanel;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class SecurityPanel implements IPanel {
    @Override
    public GridPane getGrid() {
        var grid = new GridPane();
        grid.add(new Button("SecurityPanel"), 1, 0);

        return grid;
    }
}