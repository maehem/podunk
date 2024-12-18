// This file is part of MicropolisJ.
// Copyright (C) 2013 Jason Long
// Portions Copyright (C) 1989-2007 Electronic Arts Inc.
//
// MicropolisJ is free software; you can redistribute it and/or modify
// it under the terms of the GNU GPLv3, with additional terms.
// See the README file, included in this distribution, for details.
package com.maehem.podunk.gui;

import com.maehem.podunk.engine.MicropolisMessage;
import java.util.*;
import javafx.scene.control.TextArea;

public class MessagesTextArea extends TextArea {

    static ResourceBundle cityMessageStrings = ResourceBundle.getBundle("micropolisj.CityMessages");

    public MessagesTextArea() {
        setEditable(false);
    }

    public void appendCityMessage(MicropolisMessage message) {
        appendMessageText(cityMessageStrings.getString(message.name()));
    }

    void appendMessageText(String messageText) {
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
