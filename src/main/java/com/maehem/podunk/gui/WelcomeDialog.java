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
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class WelcomeDialog extends Stage {

    private final ResourceBundle MSG;
    private final MainPane mainPane;


    public WelcomeDialog(MainPane mainPane) {
        this.mainPane = mainPane;

        MSG = ResourceBundle.getBundle("i18n/GuiStrings"); // Must be done after super() called.
        setTitle(MSG.getString("welcome.caption"));
        initModality(Modality.APPLICATION_MODAL);

        Scene scene = new Scene(new BorderPane(content()), 260, 600);
        // Set scene
        setScene(scene);
        showAndWait();
    }

    private Node content() {
        Button newCityButton = new Button(MSG.getString("welcome.new_city"));
        Button loadCityButton = new Button(MSG.getString("welcome.load_city"));
        Button cancelButton = new Button(MSG.getString("welcome.cancel"));
        Button quitButton = new Button(MSG.getString("welcome.quit"));

        newCityButton.setOnAction((t) -> {
            mainPane.onNewCityClicked();
        });
        loadCityButton.setOnAction((t) -> {
            mainPane.onLoadGameClicked();
        });
        cancelButton.setOnAction((t) -> {
            onCancelClicked();
        });
        quitButton.setOnAction((t) -> {
            onQuitClicked();
        });

        VBox box = new VBox(
                newCityButton,
                loadCityButton,
                cancelButton,
                quitButton
        );

        return box;
    }

//    private void onLoadCityClicked() {
//        mainPane.onLoadGameClicked();
//
//        try {
//            JFileChooser fc = new JFileChooser();
//            FileNameExtensionFilter filter1 = new FileNameExtensionFilter(strings.getString("cty_file"), EXTENSION);
//            fc.setFileFilter(filter1);
//
//            int rv = fc.showOpenDialog(this);
//            if (rv == JFileChooser.APPROVE_OPTION) {
//                File file = fc.getSelectedFile();
//                Micropolis newEngine = new Micropolis();
//                newEngine.load(file);
//                startPlaying(newEngine, file);
//            }
//        } catch (Exception e) {
//            e.printStackTrace(System.err);
//            JOptionPane.showMessageDialog(this, e, strings.getString("main.error_caption"),
//                    JOptionPane.ERROR_MESSAGE);
//        }
//    }
    private void onCancelClicked() {

    }

    private void onQuitClicked() {

    }
}
