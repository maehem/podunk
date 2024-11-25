// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.
package com.maehem.podunk.gui;

import com.maehem.podunk.engine.*;
import static com.maehem.podunk.gui.MainWindow.formatFunds;
import java.util.*;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class BudgetDialog extends Alert {

    Micropolis engine;

    private final static double SLIDER_W = 100;

    private final Spinner<Integer> taxRateEntry = new Spinner<>(0, 20, 7);
    int origTaxRate;
    double origRoadPct;
    double origFirePct;
    double origPolicePct;

    private final Label roadFundRequest = new Label();
    private final Label roadFundAlloc = new Label();
    private final Slider roadFundEntry = new Slider();

    private final Label policeFundRequest = new Label();
    private final Label policeFundAlloc = new Label();
    private final Slider policeFundEntry = new Slider();

    private final Label fireFundRequest = new Label();
    private final Label fireFundAlloc = new Label();
    private final Slider fireFundEntry = new Slider();

    private final Label taxRevenueLbl = new Label();

    private final Label cashFlowAmount = new Label();
    private final Label prevFundAmount = new Label();
    private final Label curFundsAmount = new Label();

    //CheckBox autoBudgetBtn;
    //CheckBox pauseBtn;

    private ResourceBundle MSG;

    public BudgetDialog(Micropolis engine) {
        super(AlertType.INFORMATION);
        this.engine = engine;
        this.origTaxRate = engine.cityTax;
        this.origRoadPct = engine.roadPercent;
        this.origFirePct = engine.firePercent;
        this.origPolicePct = engine.policePercent;

        MSG = ResourceBundle.getBundle("i18n/GuiStrings"); // Must be done after super() called.

        ((Button) getDialogPane().lookupButton(ButtonType.OK)).setText(
                MSG.getString("budgetdlg.continue")
        );

        //autoBudgetBtn = new CheckBox(MSG.getString("budgetdlg.auto_budget"));

        //pauseBtn = new CheckBox(MSG.getString("budgetdlg.pause_game"));
        loadBudgetNumbers(true);

        getDialogPane().setContent(content());

        taxRateEntry.valueProperty().addListener((o) -> {
            applyChange();
        });
        roadFundEntry.valueProperty().addListener((o) -> {
            applyChange();
        });
        policeFundEntry.valueProperty().addListener((o) -> {
            applyChange();
        });
        fireFundEntry.valueProperty().addListener((o) -> {
            applyChange();
        });
    }

    private Node content() {
        Label headingLabel = new Label(
                String.valueOf(engine.cityTime / 48)
                + " "
                + MSG.getString("budgetdlg.title")
        );
        Separator s1 = new Separator(Orientation.HORIZONTAL);

        Label taxRateLabel = new Label(MSG.getString("budgetdlg.tax_rate_hdr"));
        HBox taxRateBox = new HBox(taxRateLabel, taxRateEntry);

        Label taxCollectedLabel = new Label(MSG.getString("budgetdlg.taxes_collected"));
        HBox taxesCollectedBox = new HBox(taxCollectedLabel, taxRateEntry);

        Label cashFlowLabel = new Label(MSG.getString("budgetdlg.cash_flow"));
        HBox cashFlowBox = new HBox(cashFlowLabel, cashFlowAmount);

        Label prevFundLabel = new Label(MSG.getString("budgetdlg.previous_fund"));
        HBox prevFundBox = new HBox(prevFundLabel, prevFundAmount);

        Label curFundsLabel = new Label(MSG.getString("budgetdlg.current_funds"));
        HBox curFundsBox = new HBox(curFundsLabel, curFundsAmount);

        Separator s2 = new Separator(Orientation.HORIZONTAL);

        VBox box = new VBox(
                headingLabel,
                s1,
                taxRateBox,
                taxesCollectedBox,
                // Road/Police/Fire Spinners
                cashFlowBox,
                prevFundBox,
                s2,
                curFundsBox
        );
        return box;
    }

    private void applyChange() {
        int newTaxRate = ((Number) taxRateEntry.getValue()).intValue();
        int newRoadPct = ((Number) roadFundEntry.getValue()).intValue();
        int newPolicePct = ((Number) policeFundEntry.getValue()).intValue();
        int newFirePct = ((Number) fireFundEntry.getValue()).intValue();

        engine.cityTax = newTaxRate;
        engine.roadPercent = (double) newRoadPct / 100.0;
        engine.policePercent = (double) newPolicePct / 100.0;
        engine.firePercent = (double) newFirePct / 100.0;

        loadBudgetNumbers(false);
    }

    private void loadBudgetNumbers(boolean updateEntries) {
        BudgetNumbers b = engine.generateBudget();
        if (updateEntries) {
            taxRateEntry.getValueFactory().setValue(b.taxRate);
            roadFundEntry.setValue((int) Math.round(b.roadPercent * 100.0));
            policeFundEntry.setValue((int) Math.round(b.policePercent * 100.0));
            fireFundEntry.setValue((int) Math.round(b.firePercent * 100.0));
        }

        taxRevenueLbl.setText(formatFunds(b.taxIncome));

        roadFundRequest.setText(formatFunds(b.roadRequest));
        roadFundAlloc.setText(formatFunds(b.roadFunded));

        policeFundRequest.setText(formatFunds(b.policeRequest));
        policeFundAlloc.setText(formatFunds(b.policeFunded));

        fireFundRequest.setText(formatFunds(b.fireRequest));
        fireFundAlloc.setText(formatFunds(b.fireFunded));
    }

//    private static void adjustSliderSize(Slider slider) {
//        slider.setPrefWidth(80);
//    }

//    public BudgetDialog(Window owner, Micropolis engine) {
//        super(owner);
//        setTitle(MSG.getString("budgetdlg.title"));
//
//        this.engine = engine;
//        this.origTaxRate = engine.cityTax;
//        this.origRoadPct = engine.roadPercent;
//        this.origFirePct = engine.firePercent;
//        this.origPolicePct = engine.policePercent;
//
//        // give text fields of the fund-level spinners a minimum size
//        taxRateEntry = new JSpinner(new SpinnerNumberModel(7, 0, 20, 1));
//
//        // widgets to set funding levels
//        roadFundEntry = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
//        adjustSliderSize(roadFundEntry);
//        fireFundEntry = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
//        adjustSliderSize(fireFundEntry);
//        policeFundEntry = new JSlider(JSlider.HORIZONTAL, 0, 100, 100);
//        adjustSliderSize(policeFundEntry);
//
//        ChangeListener change = new ChangeListener() {
//            public void stateChanged(ChangeEvent ev) {
//                applyChange();
//            }
//        };
//        taxRateEntry.addChangeListener(change);
//        roadFundEntry.addChangeListener(change);
//        fireFundEntry.addChangeListener(change);
//        policeFundEntry.addChangeListener(change);
//
//        Box mainBox = new Box(BoxLayout.Y_AXIS);
//        mainBox.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
//        add(mainBox, BorderLayout.CENTER);
//
//        mainBox.add(makeTaxPane());
//
//        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
//        mainBox.add(sep);
//
//        mainBox.add(makeFundingRatesPane());
//
//        JSeparator sep1 = new JSeparator(SwingConstants.HORIZONTAL);
//        mainBox.add(sep1);
//
//        mainBox.add(makeBalancePane());
//
//        JSeparator sep2 = new JSeparator(SwingConstants.HORIZONTAL);
//        mainBox.add(sep2);
//
//        mainBox.add(makeOptionsPane());
//
//        JPanel buttonPane = new JPanel();
//        add(buttonPane, BorderLayout.SOUTH);
//
//        JButton continueBtn = new JButton(MSG.getString("budgetdlg.continue"));
//        continueBtn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onContinueClicked();
//            }
//        });
//        buttonPane.add(continueBtn);
//
//        JButton resetBtn = new JButton(MSG.getString("budgetdlg.reset"));
//        resetBtn.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent ev) {
//                onResetClicked();
//            }
//        });
//        buttonPane.add(resetBtn);
//
//        loadBudgetNumbers(true);
//        setAutoRequestFocus_compat(false);
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
//    }

//    private void setAutoRequestFocus_compat(boolean v) {
//        try {
//            if (super.getClass().getMethod("setAutoRequestFocus", boolean.class) != null) {
//                super.setAutoRequestFocus(v);
//            }
//        } catch (NoSuchMethodException e) {
//            // ok to ignore
//        }
//    }

//    private JComponent makeFundingRatesPane() {
//        JPanel fundingRatesPane = new JPanel(new GridBagLayout());
//        fundingRatesPane.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
//
//        GridBagConstraints c0 = new GridBagConstraints();
//        c0.gridx = 0;
//        c0.weightx = 0.25;
//        c0.anchor = GridBagConstraints.WEST;
//        GridBagConstraints c1 = new GridBagConstraints();
//        c1.gridx = 1;
//        c1.weightx = 0.25;
//        c1.anchor = GridBagConstraints.EAST;
//        GridBagConstraints c2 = new GridBagConstraints();
//        c2.gridx = 2;
//        c2.weightx = 0.5;
//        c2.anchor = GridBagConstraints.EAST;
//        GridBagConstraints c3 = new GridBagConstraints();
//        c3.gridx = 3;
//        c3.weightx = 0.5;
//        c3.anchor = GridBagConstraints.EAST;
//
//        c1.gridy = c2.gridy = c3.gridy = 0;
//        fundingRatesPane.add(new JLabel(MSG.getString("budgetdlg.funding_level_hdr")), c1);
//        fundingRatesPane.add(new JLabel(MSG.getString("budgetdlg.requested_hdr")), c2);
//        fundingRatesPane.add(new JLabel(MSG.getString("budgetdlg.allocation_hdr")), c3);
//
//        c0.gridy = c1.gridy = c2.gridy = c3.gridy = 1;
//        fundingRatesPane.add(new JLabel(MSG.getString("budgetdlg.road_fund")), c0);
//        fundingRatesPane.add(roadFundEntry, c1);
//        fundingRatesPane.add(roadFundRequest, c2);
//        fundingRatesPane.add(roadFundAlloc, c3);
//
//        c0.gridy = c1.gridy = c2.gridy = c3.gridy = 2;
//        fundingRatesPane.add(new JLabel(MSG.getString("budgetdlg.police_fund")), c0);
//        fundingRatesPane.add(policeFundEntry, c1);
//        fundingRatesPane.add(policeFundRequest, c2);
//        fundingRatesPane.add(policeFundAlloc, c3);
//
//        c0.gridy = c1.gridy = c2.gridy = c3.gridy = 3;
//        fundingRatesPane.add(new JLabel(MSG.getString("budgetdlg.fire_fund")), c0);
//        fundingRatesPane.add(fireFundEntry, c1);
//        fundingRatesPane.add(fireFundRequest, c2);
//        fundingRatesPane.add(fireFundAlloc, c3);
//
//        return fundingRatesPane;
//    }

//    private JComponent makeOptionsPane() {
//        JPanel optionsPane = new JPanel(new GridBagLayout());
//        optionsPane.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
//
//        GridBagConstraints c0 = new GridBagConstraints();
//        GridBagConstraints c1 = new GridBagConstraints();
//
//        c0.gridx = 0;
//        c1.gridx = 1;
//        c0.anchor = c1.anchor = GridBagConstraints.WEST;
//        c0.gridy = c1.gridy = 0;
//        c0.weightx = c1.weightx = 0.5;
//        optionsPane.add(autoBudgetBtn, c0);
//        optionsPane.add(pauseBtn, c1);
//
//        autoBudgetBtn.setSelected(engine.autoBudget);
//        pauseBtn.setSelected(engine.simSpeed == Speed.PAUSED);
//
//        return optionsPane;
//    }

//    private JComponent makeTaxPane() {
//        JPanel pane = new JPanel(new GridBagLayout());
//        pane.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
//
//        GridBagConstraints c0 = new GridBagConstraints();
//        GridBagConstraints c1 = new GridBagConstraints();
//        GridBagConstraints c2 = new GridBagConstraints();
//
//        c0.gridx = 0;
//        c0.anchor = GridBagConstraints.WEST;
//        c0.weightx = 0.25;
//        c1.gridx = 1;
//        c1.anchor = GridBagConstraints.EAST;
//        c1.weightx = 0.25;
//        c2.gridx = 2;
//        c2.anchor = GridBagConstraints.EAST;
//        c2.weightx = 0.5;
//
//        c0.gridy = c1.gridy = c2.gridy = 0;
//        pane.add(new JLabel(MSG.getString("budgetdlg.tax_rate_hdr")), c1);
//        pane.add(new JLabel(MSG.getString("budgetdlg.annual_receipts_hdr")), c2);
//
//        c0.gridy = c1.gridy = c2.gridy = 1;
//        pane.add(new JLabel(MSG.getString("budgetdlg.tax_revenue")), c0);
//        pane.add(taxRateEntry, c1);
//        pane.add(taxRevenueLbl, c2);
//
//        return pane;
//    }

//    private void onContinueClicked() {
//        if (autoBudgetBtn.isSelected() != engine.autoBudget) {
//            engine.toggleAutoBudget();
//        }
//        if (pauseBtn.isSelected() && engine.simSpeed != Speed.PAUSED) {
//            engine.setSpeed(Speed.PAUSED);
//        } else if (!pauseBtn.isSelected() && engine.simSpeed == Speed.PAUSED) {
//            engine.setSpeed(Speed.NORMAL);
//        }
//
//        dispose();
//    }
//
//    private void onResetClicked() {
//        engine.cityTax = this.origTaxRate;
//        engine.roadPercent = this.origRoadPct;
//        engine.firePercent = this.origFirePct;
//        engine.policePercent = this.origPolicePct;
//        loadBudgetNumbers(true);
//    }
//
//  }
}
