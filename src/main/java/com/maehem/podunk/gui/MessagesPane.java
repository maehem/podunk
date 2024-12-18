// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.
package com.maehem.podunk.gui;

import com.maehem.podunk.engine.*;
import java.util.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class MessagesPane extends ScrollPane {

    private final ResourceBundle MSG;
    private static final TextFlow messageFlow = new TextFlow();

    public MessagesPane() {
        super(messageFlow);

        MSG = ResourceBundle.getBundle("i18n/CityMessages"); // Must be done after super() called.
        setPrefSize(360, 70);
        setVbarPolicy(ScrollBarPolicy.ALWAYS);
    }

    public void appendCityMessage(MicropolisMessage message) {
        appendMessageText(MSG.getString(message.name()));
        // TODO adjust message color based on severity "_NEED", "_REPORT", etc.
    }

    void appendMessageText(String messageText) {
        Text t = new Text(messageText + "\n");
        messageFlow.getChildren().add(t);
        setVvalue(1.0);

        // TODO. Fade previous children 0.1 until 0.5;
//        try {
//            StyledDocument doc = getStyledDocument();
//            if (doc.getLength() != 0) {
//                doc.insertString(doc.getLength(), "\n", null);
//            }
//            doc.insertString(doc.getLength(), messageText, null);
//        } catch (BadLocationException e) {
//            throw new Error("unexpected", e);
//        }
    }
}
