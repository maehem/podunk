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

import com.maehem.podunk.engine.MapListener;
import com.maehem.podunk.engine.MapState;
import com.maehem.podunk.engine.Micropolis;
import com.maehem.podunk.engine.Sprite;
import javafx.scene.image.ImageView;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class SpriteView extends ImageView implements MapListener {

    private final Sprite sprite;
    private final Micropolis m;

    public SpriteView(Micropolis m, Sprite sprite) {
        this.m = m;
        this.sprite = sprite;

        m.addMapListener(this);
    }

    @Override
    public void mapOverlayDataChanged(MapState overlayDataType) {
    }

    @Override
    public void spriteMoved(Sprite sprite) {
        if (sprite.equals(this.sprite)) {
            setLayoutX(sprite.x);
            setLayoutY(sprite.y);
        }
    }

    @Override
    public void tileChanged(int xpos, int ypos) {
    }

    @Override
    public void wholeMapChanged() {
    }

}
