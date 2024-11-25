// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.
package com.maehem.podunk.gui;

import com.maehem.podunk.engine.Micropolis;
import com.maehem.podunk.engine.MicropolisMessage;
import com.maehem.podunk.engine.ZoneStatus;
import java.util.*;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class NotificationAlert extends Alert {

    Label headerLbl;
    //JViewport mapViewport;
    MicropolisDrawingArea mapView;
    //Pane mainPane;
    Node infoPane;

    static final Dimension2D VIEWPORT_SIZE = new Dimension2D(160, 160);
    static final Color QUERY_COLOR = Color.web("#FF5500");
    private final ResourceBundle mstrings;
    private final ResourceBundle s_strings;
    private final ResourceBundle MSG;

    public NotificationAlert(Micropolis engine, MicropolisMessage message, int xPos, int yPos) {
        super(AlertType.INFORMATION);
        // TODO: Map Zoom on target thing.
        MSG = ResourceBundle.getBundle("i18n/GuiStrings"); // Must be done after super() called.
        mstrings = ResourceBundle.getBundle("i18n/CityMessages");
        s_strings = ResourceBundle.getBundle("i18n/StatusMessages");

        // TODO setPicture(engine, xpos, ypos);
//        if (infoPane != null) {
//            mainPane.remove(infoPane);
//            infoPane = null;
//        }
        //headerLbl.setText(mstrings.getString(msg.name()+".title"));
        setHeaderText(mstrings.getString(message.name() + ".title"));
//		headerLbl.setBackground(parseColor(mstrings.getString(msg.name()+".color")));

        Label myLabel = new Label(mstrings.getString(message.name() + ".detail"));
        //myLabel.setPreferredSize(new Dimension2D(1, 1));

        infoPane = myLabel;
        //mainPane.add(myLabel, BorderLayout.CENTER);
        getDialogPane().setContent(infoPane);

        //headerLbl = new Label();
        //headerLbl.setOpaque(true);
        //headerLbl.setHorizontalAlignment(SwingConstants.CENTER);
        //headerLbl.setBorder(BorderFactory.createRaisedBevelBorder());
        //add(headerLbl, BorderLayout.NORTH);
        //JButton dismissBtn = new JButton(MSG.getString("notification.dismiss"));
//		dismissBtn.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				onDismissClicked();
//			}});
//		add(dismissBtn, BorderLayout.SOUTH);
//		mainPane = new JPanel(new BorderLayout());
//		add(mainPane, BorderLayout.CENTER);
//		JPanel viewportContainer = new JPanel(new BorderLayout());
//		viewportContainer.setBorder(
//			BorderFactory.createCompoundBorder(
//				BorderFactory.createEmptyBorder(8,4,8,4),
//				BorderFactory.createLineBorder(Color.BLACK)
//				));
//		mainPane.add(viewportContainer, BorderLayout.WEST);
//		mapViewport = new JViewport();
//		mapViewport.setPreferredSize(VIEWPORT_SIZE);
//		mapViewport.setMaximumSize(VIEWPORT_SIZE);
//		mapViewport.setMinimumSize(VIEWPORT_SIZE);
//		viewportContainer.add(mapViewport, BorderLayout.CENTER);
//
//		mapView = new MicropolisDrawingArea(engine);
//		mapViewport.setView(mapView);
    }

    public NotificationAlert(Micropolis engine, ZoneStatus zone, int xPos, int yPos) {
        super(AlertType.INFORMATION);

        // TODO: Map Zoom on target thing.
        MSG = ResourceBundle.getBundle("i18n/GuiStrings"); // Must be done after super() called.
        mstrings = ResourceBundle.getBundle("i18n/CityMessages");
        s_strings = ResourceBundle.getBundle("i18n/StatusMessages");
        getDialogPane().setBackground(new Background(new BackgroundFill(QUERY_COLOR, CornerRadii.EMPTY, Insets.EMPTY)));
        setHeaderText(mstrings.getString("notification.query_hdr"));

        String buildingStr = zone.building != -1 ? s_strings.getString("zone." + zone.building) : "";
        String popDensityStr = s_strings.getString("status." + zone.popDensity);
        String landValueStr = s_strings.getString("status." + zone.landValue);
        String crimeLevelStr = s_strings.getString("status." + zone.crimeLevel);
        String pollutionStr = s_strings.getString("status." + zone.pollution);
        String growthRateStr = s_strings.getString("status." + zone.growthRate);

        GridPane p = new GridPane();
        p.add(new Label(MSG.getString("notification.zone_lbl")), 0, 0);
        p.add(new Label(buildingStr), 1, 0);

        p.add(new Label(MSG.getString("notification.density_lbl")), 0, 1);
        p.add(new Label(popDensityStr), 1, 1);

        p.add(new Label(MSG.getString("notification.value_lbl")), 0, 2);
        p.add(new Label(landValueStr), 1, 2);

        p.add(new Label(MSG.getString("notification.crime_lbl")), 0, 3);
        p.add(new Label(crimeLevelStr), 1, 3);

        p.add(new Label(MSG.getString("notification.pollution_lbl")), 0, 4);
        p.add(new Label(pollutionStr), 1, 4);

        p.add(new Label(MSG.getString("notification.growth_lbl")), 0, 5);
        p.add(new Label(growthRateStr), 1, 5);

        p.add(new Label(), 0, 6, 2, 1); // Blank Line

        infoPane = p;
    }

//    void setPicture(Micropolis engine, int xpos, int ypos) {
//		Dimension sz = VIEWPORT_SIZE;
//
//		mapView.setEngine(engine);
//		Rectangle r = mapView.getTileBounds(xpos,ypos);
//
//		mapViewport.setViewPosition(new Point(
//			r.x + r.width/2 - sz.width/2,
//			r.y + r.height/2 - sz.height/2
//			));
//    }

//    public void showMessage(Micropolis engine, MicropolisMessage msg, int xpos, int ypos) {
//        setPicture(engine, xpos, ypos);
//
//        if (infoPane != null) {
//            mainPane.remove(infoPane);
//            infoPane = null;
//        }
//
//        //headerLbl.setText(mstrings.getString(msg.name()+".title"));
//        setHeaderText(mstrings.getString(msg.name() + ".title"));
////		headerLbl.setBackground(parseColor(mstrings.getString(msg.name()+".color")));
//
//        JLabel myLabel = new JLabel("<html><p>"
//                + mstrings.getString(msg.name() + ".detail") + "</p></html>");
//        myLabel.setPreferredSize(new Dimension(1, 1));
//
//        infoPane = myLabel;
//        mainPane.add(myLabel, BorderLayout.CENTER);
//
//        setVisible(true);
//    }
//    public void showZoneStatus(Micropolis engine, int xpos, int ypos, ZoneStatus zone) {
//        headerLbl.setText(MSG.getString("notification.query_hdr"));
//        headerLbl.setBackground(QUERY_COLOR);
//
//        String buildingStr = zone.building != -1 ? s_strings.getString("zone." + zone.building) : "";
//        String popDensityStr = s_strings.getString("status." + zone.popDensity);
//        String landValueStr = s_strings.getString("status." + zone.landValue);
//        String crimeLevelStr = s_strings.getString("status." + zone.crimeLevel);
//        String pollutionStr = s_strings.getString("status." + zone.pollution);
//        String growthRateStr = s_strings.getString("status." + zone.growthRate);
//
//        setPicture(engine, xpos, ypos);
//
//        if (infoPane != null) {
//            mainPane.remove(infoPane);
//            infoPane = null;
//        }
//
//        JPanel p = new JPanel(new GridBagLayout());
//        mainPane.add(p, BorderLayout.CENTER);
//        infoPane = p;
//
//        GridBagConstraints c1 = new GridBagConstraints();
//        GridBagConstraints c2 = new GridBagConstraints();
//
//        c1.gridx = 0;
//        c2.gridx = 1;
//        c1.gridy = c2.gridy = 0;
//        c1.anchor = GridBagConstraints.WEST;
//        c2.anchor = GridBagConstraints.WEST;
//        c1.insets = new Insets(0, 0, 0, 8);
//        c2.weightx = 1.0;
//
//        p.add(new JLabel(MSG.getString("notification.zone_lbl")), c1);
//        p.add(new JLabel(buildingStr), c2);
//
//        c1.gridy = ++c2.gridy;
//        p.add(new JLabel(MSG.getString("notification.density_lbl")), c1);
//        p.add(new JLabel(popDensityStr), c2);
//
//        c1.gridy = ++c2.gridy;
//        p.add(new JLabel(MSG.getString("notification.value_lbl")), c1);
//        p.add(new JLabel(landValueStr), c2);
//
//        c1.gridy = ++c2.gridy;
//        p.add(new JLabel(MSG.getString("notification.crime_lbl")), c1);
//        p.add(new JLabel(crimeLevelStr), c2);
//
//        c1.gridy = ++c2.gridy;
//        p.add(new JLabel(MSG.getString("notification.pollution_lbl")), c1);
//        p.add(new JLabel(pollutionStr), c2);
//
//        c1.gridy = ++c2.gridy;
//        p.add(new JLabel(MSG.getString("notification.growth_lbl")), c1);
//        p.add(new JLabel(growthRateStr), c2);
//
//        c1.gridy++;
//        c1.gridwidth = 2;
//        c1.weighty = 1.0;
//        p.add(new JLabel(), c1);
//
//        setVisible(true);
//    }
}
