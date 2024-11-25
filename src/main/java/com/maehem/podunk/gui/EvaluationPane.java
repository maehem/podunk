// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.
package com.maehem.podunk.gui;

import com.maehem.podunk.engine.CityLocation;
import com.maehem.podunk.engine.CityProblem;
import com.maehem.podunk.engine.Micropolis;
import com.maehem.podunk.engine.MicropolisMessage;
import com.maehem.podunk.engine.Sound;
import static com.maehem.podunk.gui.MainWindow.formatFunds;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EvaluationPane extends Alert
        implements Micropolis.Listener {

    Micropolis engine;

    private final ResourceBundle MSG;

    Label yesLbl = new Label();
    Label noLbl = new Label();
    Label[] voterProblemLbl;
    Label[] voterCountLbl;
    Label popLbl = new Label();
    Label deltaLbl = new Label();
    Label assessLbl = new Label();
    Label cityClassLbl = new Label();
    Label gameLevelLbl = new Label();
    Label scoreLbl = new Label();
    Label scoreDeltaLbl = new Label();

    static ResourceBundle cstrings = ResourceBundle.getBundle("i18n.CityStrings");
    static ResourceBundle gstrings = MainWindow.strings;


    public EvaluationPane(Micropolis _engine) {
        super(AlertType.INFORMATION);

        MSG = ResourceBundle.getBundle("i18n.GuiStrings"); // Must be done after super() called.

        assert _engine != null;
        HBox contentBox = new HBox(makePublicOpinionPane(), makeStatisticsPane());
        getDialogPane().setContent(new BorderPane(contentBox));
        setEngine(_engine);

//        JButton dismissBtn = new JButton(gstrings.getString("dismiss-evaluation"));
//        dismissBtn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent evt) {
//                onDismissClicked();
//            }
//        });
//        add(dismissBtn, BorderLayout.SOUTH);

//        Box b1 = new Box(BoxLayout.X_AXIS);
//        add(b1, BorderLayout.CENTER);
//
//        b1.add(makePublicOpinionPane());
//        b1.add(new JSeparator(SwingConstants.VERTICAL));
//        b1.add(makeStatisticsPane());

    }

    public final void setEngine(Micropolis newEngine) {
        if (engine != null) { //old engine
            engine.removeListener(this);
        }
        engine = newEngine;
        if (engine != null) { //new engine
            engine.addListener(this);
            loadEvaluation();
        }
    }

    private Node makePublicOpinionPane() {
        Label heading = new Label(MSG.getString("public-opinion"));

        Label b1Head = new Label(MSG.getString("public-opinion-1"));
        Label b1YesLabel = new Label(MSG.getString("public-opinion-yes"));
        HBox yesLine = new HBox(yesLbl, b1YesLabel);
        Label b1NoLabel = new Label(MSG.getString("public-opinion-no"));
        HBox noLine = new HBox(noLbl, b1NoLabel);
        VBox box1 = new VBox(b1Head, yesLine, noLine);


        Label b2Head = new Label(MSG.getString("public-opinion-2"));
        VBox box2 = new VBox(b2Head);
        final int NUM_PROBS = 4;
        voterProblemLbl = new Label[NUM_PROBS];
        voterCountLbl = new Label[NUM_PROBS];
        for (int i = 0; i < NUM_PROBS; i++) {
            voterProblemLbl[i] = new Label();
            voterCountLbl[i] = new Label();
            HBox line = new HBox(voterProblemLbl[i], voterCountLbl[i]);
            box2.getChildren().add(line);
        }

        VBox box = new VBox(box1, box2);


//        JPanel me = new JPanel(new GridBagLayout());
//        GridBagConstraints c1 = new GridBagConstraints();
//        GridBagConstraints c2 = new GridBagConstraints();
//        GridBagConstraints c3 = new GridBagConstraints();
//
//        // c1 is for the full-width headers
//        c1.gridx = c1.gridy = 0;
//        c1.gridwidth = 2;
//        c1.gridheight = 1;
//        c1.weightx = 1.0;
//        c1.fill = GridBagConstraints.NONE;
//        c1.anchor = GridBagConstraints.NORTH;

//        Label headerLbl = new Label(gstrings.getString("public-opinion"));
//        Font curFont = headerLbl.getFont();
//        headerLbl.setFont(
//                curFont.deriveFont(curFont.getStyle() | Font.BOLD, (float) (curFont.getSize() * 1.2))
//        );
//        me.add(headerLbl, c1);

//        c1.gridy = 1;
//        c1.insets = new Insets(3, 0, 3, 0);
//        me.add(new Label(gstrings.getString("public-opinion-1")), c1);
//
//        c1.gridy = 4;
//        me.add(new Label(gstrings.getString("public-opinion-2")), c1);
//
//        c2.gridx = 0;
//        c2.gridy = 2;
//        c2.gridwidth = c2.gridheight = 1;
//        c2.weightx = 1.0;
//        c2.anchor = GridBagConstraints.EAST;
//        c2.insets = new Insets(0, 0, 0, 4);
//
//        me.add(new Label(gstrings.getString("public-opinion-yes")), c2);
//
//        c2.gridy = 3;
//        me.add(new Label(gstrings.getString("public-opinion-no")), c2);
//
//        c3.gridx = 1;
//        c3.gridwidth = c3.gridheight = 1;
//        c3.weightx = 1.0;
//        c3.anchor = GridBagConstraints.WEST;
//        c3.insets = new Insets(0, 4, 0, 0);
//
//        c3.gridy = 2;
//        yesLbl = new Label();
//        me.add(yesLbl, c3);
//
//        c3.gridy = 3;
//        noLbl = new Label();
//        me.add(noLbl, c3);
//
//        c2.gridy = c3.gridy = 5;

//        final int NUM_PROBS = 4;
//        voterProblemLbl = new Label[NUM_PROBS];
//        voterCountLbl = new Label[NUM_PROBS];
//        for (int i = 0; i < NUM_PROBS; i++) {
//            voterProblemLbl[i] = new Label();
//            me.add(voterProblemLbl[i], c2);
//
//            voterCountLbl[i] = new Label();
//            me.add(voterCountLbl[i], c3);
//
//            c2.gridy = ++c3.gridy;
//        }
//
//        // add glue so that everything will align towards the top
//        c1.gridy = 999;
//        c1.weighty = 1.0;
//        me.add(new Label(), c1);
//
//        return me;
        VBox popPane = new VBox(heading, box);
        return popPane;
    }

    private Node makeStatisticsPane() {
        Label heading = new Label(MSG.getString("statistics-head"));

        Label popLabel = new Label(gstrings.getString("stats-population"));
        Label migLabel = new Label(gstrings.getString("stats-net-migration"));
        Label lyLabel = new Label(gstrings.getString("stats-last-year"));
        Label assValLabel = new Label(gstrings.getString("stats-assessed-value"));
        Label catLabel = new Label(gstrings.getString("stats-category"));
        Label glLabel = new Label(gstrings.getString("stats-game-level"));
        Label scoreCurLabel = new Label(gstrings.getString("city-score-current"));
        Label scoreChgLabel = new Label(gstrings.getString("city-score-change"));

        HBox popBox = new HBox(popLabel, popLbl);
        HBox migBox = new HBox(migLabel, deltaLbl);
        HBox assBox = new HBox(assValLabel, assessLbl);
        HBox catBox = new HBox(catLabel, cityClassLbl);
        HBox glBox = new HBox(glLabel, gameLevelLbl);
        HBox scoreChgLabelBox = new HBox(scoreCurLabel, new Label(":"), scoreChgLabel);
        HBox scoreChgValueBox = new HBox(scoreLbl, new Label("     "), scoreDeltaLbl);

        VBox statsBox = new VBox(
                popBox,
                migBox,
                lyLabel,
                assBox,
                catBox,
                glBox,
                scoreChgLabelBox,
                scoreChgValueBox
        );

        VBox box = new VBox(heading, statsBox);

        return box;

//        JPanel me = new JPanel(new GridBagLayout());
//        GridBagConstraints c1 = new GridBagConstraints();
//        GridBagConstraints c2 = new GridBagConstraints();
//        GridBagConstraints c3 = new GridBagConstraints();
//
//        c1.gridx = c1.gridy = 0;
//        c1.gridwidth = 2;
//        c1.gridheight = 1;
//        c1.weightx = 1.0;
//        c1.fill = GridBagConstraints.NONE;
//        c1.anchor = GridBagConstraints.NORTH;
//        c1.insets = new Insets(0, 0, 3, 0);
//
//        Label headerLbl = new Label(gstrings.getString("statistics-head"));
//        Font curFont = headerLbl.getFont();
//        headerLbl.setFont(
//                curFont.deriveFont(curFont.getStyle() | Font.BOLD, (float) (curFont.getSize() * 1.2))
//        );
//        me.add(headerLbl, c1);
//
//        c1.gridy = 20;
//        c1.insets = new Insets(9, 0, 3, 0);
//        c1.fill = GridBagConstraints.VERTICAL;
//        Label header2Lbl = new Label(gstrings.getString("city-score-head"));
//        me.add(header2Lbl, c1);
//
//        c2.gridx = 0;
//        c2.gridwidth = c2.gridheight = 1;
//        c2.weightx = 0.5;
//        c2.anchor = GridBagConstraints.EAST;
//        c2.insets = new Insets(0, 0, 0, 4);
//
//        c3.gridx = 1;
//        c3.gridwidth = c3.gridheight = 1;
//        c3.weightx = 0.5;
//        c3.anchor = GridBagConstraints.WEST;
//        c3.insets = new Insets(0, 4, 0, 0);
//
//        c2.gridy = c3.gridy = 1;
//        me.add(new Label(gstrings.getString("stats-population")), c2);
//        popLbl = new Label();
//        me.add(popLbl, c3);
//
//        c2.gridy = ++c3.gridy;
//        me.add(new Label(gstrings.getString("stats-net-migration")), c2);
//        deltaLbl = new Label();
//        me.add(deltaLbl, c3);
//
//        c2.gridy = ++c3.gridy;
//        me.add(new Label(gstrings.getString("stats-last-year")), c2);
//
//        c2.gridy = ++c3.gridy;
//        me.add(new Label(gstrings.getString("stats-assessed-value")), c2);
//        assessLbl = new Label();
//        me.add(assessLbl, c3);
//
//        c2.gridy = ++c3.gridy;
//        me.add(new Label(gstrings.getString("stats-category")), c2);
//        cityClassLbl = new Label();
//        me.add(cityClassLbl, c3);
//
//        c2.gridy = ++c3.gridy;
//        me.add(new Label(gstrings.getString("stats-game-level")), c2);
//        gameLevelLbl = new Label();
//        me.add(gameLevelLbl, c3);
//
//        c2.gridy = c3.gridy = 21;
//        me.add(new Label(gstrings.getString("city-score-current")), c2);
//        scoreLbl = new Label();
//        me.add(scoreLbl, c3);
//
//        c2.gridy = ++c3.gridy;
//        me.add(new Label(gstrings.getString("city-score-change")), c2);
//        scoreDeltaLbl = new Label();
//        me.add(scoreDeltaLbl, c3);
//
//        // add glue so that everything will align towards the top
//        c1.gridy = 999;
//        c1.weighty = 1.0;
//        c1.insets = new Insets(0, 0, 0, 0);
//        me.add(new Label(), c1);
//
//        return me;
    }

    //implements Micropolis.Listener
    @Override
    public void cityMessage(MicropolisMessage message, CityLocation loc) {
    }

    @Override
    public void citySound(Sound sound, CityLocation loc) {
    }

    @Override
    public void censusChanged() {
    }

    @Override
    public void demandChanged() {
    }

    @Override
    public void fundsChanged() {
    }

    @Override
    public void optionsChanged() {
    }

    //implements Micropolis.Listener
    @Override
    public void evaluationChanged() {
        loadEvaluation();
    }

    private void loadEvaluation() {
        NumberFormat pctFmt = NumberFormat.getPercentInstance();
        yesLbl.setText(pctFmt.format(0.01 * engine.evaluation.cityYes));
        noLbl.setText(pctFmt.format(0.01 * engine.evaluation.cityNo));

        for (int i = 0; i < voterProblemLbl.length; i++) {
            CityProblem p = i < engine.evaluation.problemOrder.length ? engine.evaluation.problemOrder[i] : null;
            int numVotes = p != null ? engine.evaluation.problemVotes.get(p) : 0;

            if (numVotes != 0) {
                voterProblemLbl[i].setText(cstrings.getString("problem." + p.name()));
                voterCountLbl[i].setText(pctFmt.format(0.01 * numVotes));
                voterProblemLbl[i].setVisible(true);
                voterCountLbl[i].setVisible(true);
            } else {
                voterProblemLbl[i].setVisible(false);
                voterCountLbl[i].setVisible(false);
            }
        }

        NumberFormat nf = NumberFormat.getInstance();
        popLbl.setText(nf.format(engine.evaluation.cityPop));
        deltaLbl.setText(nf.format(engine.evaluation.deltaCityPop));
        assessLbl.setText(formatFunds(engine.evaluation.cityAssValue));
        cityClassLbl.setText(getCityClassName(engine.evaluation.cityClass));
        gameLevelLbl.setText(getGameLevelName(engine.gameLevel));
        scoreLbl.setText(nf.format(engine.evaluation.cityScore));
        scoreDeltaLbl.setText(nf.format(engine.evaluation.deltaCityScore));
    }

    static String getCityClassName(int cityClass) {
        return cstrings.getString("class." + cityClass);
    }

    static String getGameLevelName(int gameLevel) {
        return cstrings.getString("level." + gameLevel);
    }
}
