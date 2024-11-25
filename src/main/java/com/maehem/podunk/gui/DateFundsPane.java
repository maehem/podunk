/*  GNU GPLv3 License, with additional terms

    Podunk Project -- https://github.com/maehem/podunk
    A city simulator in JavaFX
    Copyright (C) 2024 Mark J. Koch  ( @maehem on GitHub )

    This project is derived from MicropolisJ. https://github.com/SimHacker/micropolis
    Copyright (C) 2013 Jason Long
    Portions Copyright (C) 1989-2007 Electronic Arts Inc.

    Podunk (derived from MicropolisJ) is free software; you can redistribute it
    and/or modify it under the terms of the GNU GPLv3, with additional terms.
    See the README file, included in this distribution, for details.

 */
package com.maehem.podunk.gui;

import java.util.ResourceBundle;
import javafx.geometry.HPos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class DateFundsPane extends GridPane {
    private Label dateValLabel = new Label("0");
    private Label fundsValLabel = new Label("0");
    private Label popValLabel = new Label("0");
    private final ResourceBundle MSG;

    public DateFundsPane() {
        MSG = ResourceBundle.getBundle("i18n/GuiStrings"); // Must be done after super() called.

        setHgap(6);

        Label dateLabel = new Label(MSG.getString("main.date_label"));
        Label fundsLabel = new Label(MSG.getString("main.funds_label"));
        Label popLabel = new Label(MSG.getString("main.population_label"));

        GridPane.setHalignment(dateLabel, HPos.RIGHT);
        GridPane.setHalignment(fundsLabel, HPos.RIGHT);
        GridPane.setHalignment(popLabel, HPos.RIGHT);

        add(dateLabel, 0, 0);
        add(fundsLabel, 0, 1);
        add(popLabel, 0, 2);
        add(dateValLabel, 1, 0);
        add(fundsValLabel, 1, 1);
        add(popValLabel, 1, 2);

    }

    // Maybe this is a engine listener instead?
    public void setDate(String val) {
        dateValLabel.setText(val);
    }

    public void setFunds(String val) {
        fundsValLabel.setText(val);
    }

    public void setPop(String val) {
        popValLabel.setText(val);
    }

}
