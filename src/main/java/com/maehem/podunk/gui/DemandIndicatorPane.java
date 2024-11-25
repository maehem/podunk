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

import com.maehem.podunk.engine.CityLocation;
import com.maehem.podunk.engine.Micropolis;
import com.maehem.podunk.engine.MicropolisMessage;
import com.maehem.podunk.engine.Sound;
import com.maehem.podunk.logging.Logging;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Dimension2D;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class DemandIndicatorPane extends Group
        implements Micropolis.Listener {

    public static final Logger LOGGER = Logging.LOGGER;

    static final int UPPER_EDGE = 19;
    static final int LOWER_EDGE = 28;
    static final int MAX_LENGTH = 16;
    static final int RES_LEFT = 8;
    static final int COM_LEFT = 17;
    static final int IND_LEFT = 26;
    static final int BAR_WIDTH = 6;

    static final Color STROKE_COLOR = Color.BLACK;
    static final double STROKE_W = 1.0;

    static final Image backgroundImage = loadImage("/images/demandg.png");
    static final Dimension2D MY_SIZE = new Dimension2D(
            backgroundImage.getWidth(),
            backgroundImage.getHeight()
    );

    private final Rectangle resRect = new Rectangle(BAR_WIDTH, MY_SIZE.getHeight(), Color.GREEN);
    private final Rectangle comRect = new Rectangle(BAR_WIDTH, MY_SIZE.getHeight(), Color.BLUE);
    private final Rectangle indRect = new Rectangle(BAR_WIDTH, MY_SIZE.getHeight(), Color.YELLOW);

    Micropolis engine;

    public DemandIndicatorPane() {
        resRect.setStroke(STROKE_COLOR);
        resRect.setStrokeWidth(STROKE_W);
        resRect.setLayoutX(RES_LEFT);
        comRect.setStroke(STROKE_COLOR);
        comRect.setStrokeWidth(STROKE_W);
        comRect.setLayoutX(COM_LEFT);
        indRect.setStroke(STROKE_COLOR);
        indRect.setStrokeWidth(STROKE_W);
        indRect.setLayoutX(IND_LEFT);

        Pane p = new Pane(
                new ImageView(backgroundImage),
                resRect,
                comRect,
                indRect
        );
        p.setMinSize(MY_SIZE.getWidth(), MY_SIZE.getHeight());
        p.setPrefSize(MY_SIZE.getWidth(), MY_SIZE.getHeight());
        p.setMaxSize(MY_SIZE.getWidth(), MY_SIZE.getHeight());

        p.setScaleX(1.6);
        p.setScaleY(1.6);
        getChildren().add(p);
    }

    public void setEngine(Micropolis newEngine) {
        if (engine != null) { //old engine
            engine.removeListener(this);
        }

        engine = newEngine;

        if (engine != null) { //new engine
            engine.addListener(this);
        }
        update();
    }

    static Image loadImage(String resourceName) {
        //URL iconUrl = MicropolisDrawingArea.class.getResource(resourceName);
        Image refImage = new Image(DemandIndicatorPane.class.getResourceAsStream(resourceName));

//        BufferedImage bi = new BufferedImage(refImage.getWidth(null), refImage.getHeight(null),
//                BufferedImage.TYPE_INT_RGB);
//        Graphics2D gr = bi.createGraphics();
//        gr.drawImage(refImage, 0, 0, null);
        return refImage;
    }

//    @Override
//    public Dimension2D getMinimumSize() {
//        return MY_SIZE;
//    }
//
//    @Override
//    public Dimension2D getPreferredSize() {
//        return MY_SIZE;
//    }
//
//    @Override
//    public Dimension2D getMaximumSize() {
//        return MY_SIZE;
//    }
//
//    public void paintComponent(Graphics gr1) {
//        Graphics2D gr = (Graphics2D) gr1;
//        gr.drawImage(backgroundImage, 0, 0, null);
//
//        if (engine == null) {
//            return;
//        }
//
//        int resValve = engine.getResValve();
//        int ry0 = resValve <= 0 ? LOWER_EDGE : UPPER_EDGE;
//        int ry1 = ry0 - resValve / 100;
//
//        if (ry1 - ry0 > MAX_LENGTH) {
//            ry1 = ry0 + MAX_LENGTH;
//        }
//        if (ry1 - ry0 < -MAX_LENGTH) {
//            ry1 = ry0 - MAX_LENGTH;
//        }
//
//        int comValve = engine.getComValve();
//        int cy0 = comValve <= 0 ? LOWER_EDGE : UPPER_EDGE;
//        int cy1 = cy0 - comValve / 100;
//
//        int indValve = engine.getIndValve();
//        int iy0 = indValve <= 0 ? LOWER_EDGE : UPPER_EDGE;
//        int iy1 = iy0 - indValve / 100;
//
//        if (ry0 != ry1) {
//            Rectangle resRect = new Rectangle(RES_LEFT, Math.min(ry0, ry1), BAR_WIDTH, Math.abs(ry1 - ry0));
//            gr.setColor(Color.GREEN);
//            gr.fill(resRect);
//            gr.setColor(Color.BLACK);
//            gr.draw(resRect);
//        }
//
//        if (cy0 != cy1) {
//            Rectangle comRect = new Rectangle(COM_LEFT, Math.min(cy0, cy1), BAR_WIDTH, Math.abs(cy1 - cy0));
//            gr.setColor(Color.BLUE);
//            gr.fill(comRect);
//            gr.setColor(Color.BLACK);
//            gr.draw(comRect);
//        }
//
//        if (iy0 != iy1) {
//            Rectangle indRect = new Rectangle(IND_LEFT, Math.min(iy0, iy1), BAR_WIDTH, Math.abs(iy1 - iy0));
//            gr.setColor(Color.YELLOW);
//            gr.fill(indRect);
//            gr.setColor(Color.BLACK);
//            gr.draw(indRect);
//        }
//    }
    private void update() {
        if (engine == null) {
            return;
        }

        int resValve = engine.getResValve();
        int ry0 = resValve <= 0 ? LOWER_EDGE : UPPER_EDGE;
        int ry1 = ry0 - resValve / 100;

        if (ry1 - ry0 > MAX_LENGTH) {
            ry1 = ry0 + MAX_LENGTH;
        }
        if (ry1 - ry0 < -MAX_LENGTH) {
            ry1 = ry0 - MAX_LENGTH;
        }

        int comValve = engine.getComValve();
        int cy0 = comValve <= 0 ? LOWER_EDGE : UPPER_EDGE;
        int cy1 = cy0 - comValve / 100;

        int indValve = engine.getIndValve();
        int iy0 = indValve <= 0 ? LOWER_EDGE : UPPER_EDGE;
        int iy1 = iy0 - indValve / 100;

        if (ry0 != ry1) {
            //Rectangle resRect = new Rectangle(RES_LEFT, Math.min(ry0, ry1), BAR_WIDTH, Math.abs(ry1 - ry0));
            resRect.setLayoutY(Math.min(ry0, ry1));
            resRect.setHeight(Math.abs(ry1 - ry0));
        }

        if (cy0 != cy1) {
            //Rectangle comRect = new Rectangle(COM_LEFT, Math.min(cy0, cy1), BAR_WIDTH, Math.abs(cy1 - cy0));
            comRect.setLayoutY(Math.min(cy0, cy1));
            comRect.setHeight(Math.abs(cy1 - cy0));
        }

        if (iy0 != iy1) {
            //Rectangle indRect = new Rectangle(IND_LEFT, Math.min(iy0, iy1), BAR_WIDTH, Math.abs(iy1 - iy0));
            indRect.setLayoutY(Math.min(iy0, iy1));
            indRect.setHeight(Math.abs(iy1 - iy0));
        }

    }

    //implements Micropolis.Listener
    @Override
    public void demandChanged() {
        LOGGER.log(Level.SEVERE, "Demand Indicator: Demand Changed...");
        update();
    }

    //implements Micropolis.Listener
    @Override
    public void cityMessage(MicropolisMessage m, CityLocation p) {
    }

    @Override
    public void citySound(Sound sound, CityLocation p) {
    }

    @Override
    public void censusChanged() {
    }

    @Override
    public void evaluationChanged() {
    }

    @Override
    public void fundsChanged() {
    }

    @Override
    public void optionsChanged() {
    }
}
