// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.
package com.maehem.podunk.gui;

import com.maehem.podunk.engine.GameLevel;
import java.util.*;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class NewCityDialog extends Alert {

    private final ResourceBundle MSG;
    //Micropolis engine;

    //Button previousMapBtn;
    //Stack<Micropolis> previousMaps = new Stack<Micropolis>();
    //Stack<Micropolis> nextMaps = new Stack<Micropolis>();
    //OverlayMapView mapPane;
    HashMap<Integer, RadioButton> levelBtns = new HashMap<>();
    TextField cityNameTextField = new TextField();

    ToggleGroup levelButtonsGroup = new ToggleGroup();
    RadioButton easyButton;
    RadioButton mediumButton;
    RadioButton hardButton;

    //static final ResourceBundle strings = MainWindow.strings;
    public NewCityDialog(MainPane owner, boolean showCancelOption) {
        super(AlertType.INFORMATION);
        MSG = ResourceBundle.getBundle("i18n/GuiStrings"); // Must be done after super() called.

        setTitle(MSG.getString("welcome.caption"));

        assert owner != null; // Not needed ?

        easyButton = new RadioButton(MSG.getString("menu.difficulty.0"));
        easyButton.setToggleGroup(levelButtonsGroup);
        mediumButton = new RadioButton(MSG.getString("menu.difficulty.1"));
        mediumButton.setToggleGroup(levelButtonsGroup);
        hardButton = new RadioButton(MSG.getString("menu.difficulty.2"));
        hardButton.setToggleGroup(levelButtonsGroup);

//        engine = new Micropolis();
//
//        new MapGenerator(engine).generateNewCity();

        getDialogPane().setContent(contentPane());
    }

    private Node contentPane() {
//        JPanel p1 = new JPanel(new BorderLayout());
//        p1.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
//        getContentPane().add(p1, BorderLayout.CENTER);

        Label cityNameLabel = new Label(MSG.getString("new-city.name_lbl"));
        Label levelLabel = new Label(MSG.getString("new-city.level_lbl"));
        levelLabel.setPadding(new Insets(6));

        VBox box = new VBox(cityNameLabel,
                cityNameTextField,
                levelLabel,
                easyButton,
                mediumButton,
                hardButton
        );
        BorderPane pane = new BorderPane(box);

        return pane;

//        mapPane = new OverlayMapView(engine);
//        mapPane.setBorder(BorderFactory.createLoweredBevelBorder());
//        p1.add(mapPane, BorderLayout.WEST);
//
//        JPanel p2 = new JPanel(new BorderLayout());
//        p1.add(p2, BorderLayout.CENTER);
//
//        Box levelBox = new Box(BoxLayout.Y_AXIS);
//        levelBox.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
//        p2.add(levelBox, BorderLayout.CENTER);
//
//        levelBox.add(Box.createVerticalGlue());
//        JRadioButton radioBtn;
//        for (int lev = GameLevel.MIN_LEVEL; lev <= GameLevel.MAX_LEVEL; lev++) {
//            final int x = lev;
//            radioBtn = new JRadioButton(strings.getString("menu.difficulty." + lev));
//            radioBtn.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent evt) {
//                    setGameLevel(x);
//                }
//            });
//            levelBox.add(radioBtn);
//            levelBtns.put(lev, radioBtn);
//        }
//        levelBox.add(Box.createVerticalGlue());
//        setGameLevel(GameLevel.MIN_LEVEL);
//
//        JPanel buttonPane = new JPanel();
//        getContentPane().add(buttonPane, BorderLayout.SOUTH);
//
//        JButton btn;
//        btn = new JButton(strings.getString("welcome.previous_map"));
//        btn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                onPreviousMapClicked();
//            }
//        });
//        btn.setEnabled(false);
//        buttonPane.add(btn);
//        previousMapBtn = btn;
//
//        btn = new JButton(strings.getString("welcome.play_this_map"));
//        btn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                onPlayClicked();
//            }
//        });
//        buttonPane.add(btn);
//        getRootPane().setDefaultButton(btn);
//
//        btn = new JButton(strings.getString("welcome.next_map"));
//        btn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                onNextMapClicked();
//            }
//        });
//        buttonPane.add(btn);
//
//        btn = new JButton(strings.getString("welcome.load_city"));
//        btn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                onLoadCityClicked();
//            }
//        });
//        buttonPane.add(btn);
//
//        if (showCancelOption) {
//            btn = new JButton(strings.getString("welcome.cancel"));
//            btn.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent evt) {
//                    onCancelClicked();
//                }
//            });
//            buttonPane.add(btn);
//        } else {
//            btn = new JButton(strings.getString("welcome.quit"));
//            btn.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent evt) {
//                    onQuitClicked();
//                }
//            });
//            buttonPane.add(btn);
//        }
//
//        pack();
//        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//        setLocationRelativeTo(owner);
//        getRootPane().registerKeyboardAction(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                dispose();
//            }
//        },
//                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
//                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

//    private void onPreviousMapClicked() {
//        if (previousMaps.isEmpty()) {
//            return;
//        }
//
//        nextMaps.push(engine);
//        engine = previousMaps.pop();
//        mapPane.setEngine(engine);
//
//        previousMapBtn.setEnabled(!previousMaps.isEmpty());
//    }
//
//    private void onNextMapClicked() {
//        if (nextMaps.isEmpty()) {
//            Micropolis m = new Micropolis();
//            new MapGenerator(m).generateNewCity();
//            nextMaps.add(m);
//        }
//
//        previousMaps.push(engine);
//        engine = nextMaps.pop();
//        mapPane.setEngine(engine);
//
//        previousMapBtn.setEnabled(true);
//    }

//    private void onLoadCityClicked() {
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

//    void startPlaying(Micropolis newEngine, File file) {
//        MainPane win = (MainPane) getOwner();
//        win.setEngine(newEngine);
//        win.currentFile = file;
//        win.makeClean();
//        dispose();
//    }

//    private void onPlayClicked() {
//        engine.setGameLevel(getSelectedGameLevel());
//        engine.setFunds(GameLevel.getStartingFunds(engine.gameLevel));
//        startPlaying(engine, null);
//    }

//    private void onCancelClicked() {
//        dispose();
//    }
//
//    private void onQuitClicked() {
//        System.exit(0);
//    }
//
    protected int getSelectedGameLevel() {
        for (int lev : levelBtns.keySet()) {
            if (levelBtns.get(lev).isSelected()) {
                return lev;
            }
        }
        return GameLevel.MIN_LEVEL;
    }

    private void setGameLevel(int level) {
        for (int lev : levelBtns.keySet()) {
            levelBtns.get(lev).setSelected(lev == level);
        }
    }
}
