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

import static com.maehem.podunk.logging.Logging.LOGGER;
import java.util.logging.Level;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * Visual Representation
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class CellView extends StackPane {

    // TODO:
    //  blink unpowered
    char data;
    public final int x;
    public final int y;
    private final TileImages tileImages;
    static Image animationsImg = new Image(CellView.class.getResourceAsStream("/graphics/misc_animation.png"));
    private final ImageView unpoweredOverlay;

    public CellView(TileImages ti, char data, int x, int y) {
        LOGGER.log(Level.FINEST, "create CellView @ {0},{1} for: {2}", new Object[]{x, y, String.format("%03d", (int) data)});
        this.tileImages = ti;
        this.data = data;
        this.x = x;
        this.y = y;

        //setBorder(new Border(new BorderStroke(new Color(0, 0, 0.7, 0.1), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0.5))));
        this.unpoweredOverlay = new ImageView(animationsImg);
        unpoweredOverlay.setViewport(new Rectangle2D(0, 0, 16, 16));

        setBlinkUnpowered(true);

        updateView();
    }

    public void updateData(char data) {
        this.data = data;
        updateView();
    }

    private void updateView() {
        ImageView iv = tileImages.getTileImage(data);
        getChildren().clear();
        getChildren().addAll(iv, unpoweredOverlay);
        setMinSize(iv.getFitWidth(), iv.getFitHeight());

        //getChildren().add(new Text(String.format("%03d", (int) data)));
        layout();
    }

    protected final void setBlinkUnpowered(boolean b) {
        //LOGGER.log(Level.SEVERE, "Toggle blink: {0}", String.valueOf(b));
        unpoweredOverlay.setOpacity(b ? 1.0 : 0.0);

    }


}
