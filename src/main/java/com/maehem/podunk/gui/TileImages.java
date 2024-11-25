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

import com.maehem.podunk.engine.*;
import com.maehem.podunk.logging.Logging;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class TileImages {

    public static final Logger LOGGER = Logging.LOGGER;

    public final int TILE_WIDTH;
    public final int TILE_HEIGHT;
    public final ImageView[] images;
    public Map<SpriteKind, Map<Integer, ImageView>> spriteImages;

    private TileImages(int size) {
        this.TILE_WIDTH = size;
        this.TILE_HEIGHT = size;

        //this.images = loadTileImages(size + "x" + size + "/tiles.png", size);
        this.images = loadTileImages2("tiles.png", size);
        loadSpriteImages();
    }

    static Map<Integer, TileImages> savedInstances = new HashMap<>();

    public static TileImages getInstance(int size) {
        if (!savedInstances.containsKey(size)) {
            savedInstances.put(size, new TileImages(size));
        }
        return savedInstances.get(size);
    }

    public ImageView getTileImage(int cell) {
        //TileSpec ts = Tiles.get(cell & LOMASK);

        //int tile = (cell & LOMASK) % images.length;
        //return images[tile];
        return clone(images[cell]);
    }

    private ImageView clone(ImageView iv) {
        ImageView civ = new ImageView(iv.getImage());
        Rectangle2D viewport = iv.getViewport();
        civ.setViewport(iv.getViewport());
        civ.setFitHeight(TILE_HEIGHT);
        civ.setPreserveRatio(iv.isPreserveRatio());
        return civ;
    }

    private ImageView[] loadTileImages(String resourceName, int srcSize) {
        LOGGER.log(Level.SEVERE, "Load Tile Images. SRC size: " + srcSize);
        //URL iconUrl = TileImages.class.getResource(resourceName);
        Image refImage = new Image(TileImages.class.getResourceAsStream("/images/" + resourceName));

//        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice dev = env.getDefaultScreenDevice();
//        GraphicsConfiguration conf = dev.getDefaultConfiguration();

        ImageView[] imgs = new ImageView[(int) refImage.getHeight() / srcSize];
        for (int i = 0; i < imgs.length; i++) {
//            BufferedImage bi = conf.createCompatibleImage(TILE_WIDTH, TILE_HEIGHT, Transparency.OPAQUE);
//            Graphics2D gr = bi.createGraphics();
//            gr.drawImage(refImage, 0, 0, TILE_WIDTH, TILE_HEIGHT,
//                    0, i * srcSize,
//                    0 + srcSize, i * srcSize + srcSize,
//                    null);

            Rectangle2D vp = new Rectangle2D(0, i * srcSize, srcSize, srcSize);
            ImageView iv = new ImageView(refImage);
            iv.setViewport(vp);
            iv.setFitHeight(TILE_HEIGHT);
            iv.setPreserveRatio(true);
            imgs[i] = iv;
        }
        return imgs;
    }

    private ImageView[] loadTileImages2(String resourceName, int srcSize) {
        LOGGER.log(Level.SEVERE, "Load Tile Images2. SRC size: " + srcSize);

        ImageView[] imgs = new ImageView[Tiles.tiles.length];
        for (int i = 0; i < imgs.length; i++) {
            TileSpec ts = Tiles.get(i);
            if (ts.getImages().length == 0) {
                continue;
            }
            LOGGER.log(Level.FINEST, "TileSpec {0}  Image0: {1}", new Object[]{ts.name, ts.getImages()[0]});
            String[] split = ts.getImages()[0].split("@");
            String[] cords = split[1].split(",");
            LOGGER.log(Level.FINEST, "Load Image: /graphics/{0}.png", split[0]);
            Image srcImage = new Image(TileImages.class.getResourceAsStream("/graphics/" + split[0] + ".png"));
            Rectangle2D vp = new Rectangle2D(0, Integer.parseInt(cords[1]), srcSize, srcSize);
            ImageView iv = new ImageView(srcImage);
            iv.setViewport(vp);
            iv.setFitHeight(TILE_HEIGHT);
            iv.setPreserveRatio(true);

            imgs[i] = iv;
        }

        return imgs;

//        //URL iconUrl = TileImages.class.getResource(resourceName);
//        Image refImage = new Image(TileImages.class.getResourceAsStream("/images/" + resourceName));
//
////        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
////        GraphicsDevice dev = env.getDefaultScreenDevice();
////        GraphicsConfiguration conf = dev.getDefaultConfiguration();
//        //ImageView[] imgs = new ImageView[(int) refImage.getHeight() / srcSize];
//        for (int i = 0; i < imgs.length; i++) {
////            BufferedImage bi = conf.createCompatibleImage(TILE_WIDTH, TILE_HEIGHT, Transparency.OPAQUE);
////            Graphics2D gr = bi.createGraphics();
////            gr.drawImage(refImage, 0, 0, TILE_WIDTH, TILE_HEIGHT,
////                    0, i * srcSize,
////                    0 + srcSize, i * srcSize + srcSize,
////                    null);
//
//            Rectangle2D vp = new Rectangle2D(0, i * srcSize, srcSize, srcSize);
//            ImageView iv = new ImageView(refImage);
//            iv.setViewport(vp);
//            iv.setFitHeight(TILE_HEIGHT);
//            iv.setPreserveRatio(true);
//            imgs[i] = iv;
//        }
//        return imgs;
    }

    public ImageView getSpriteImage(SpriteKind kind, int frameNumber) {
        return spriteImages.get(kind).get(frameNumber);
    }

    private void loadSpriteImages() {
        spriteImages = new EnumMap<>(SpriteKind.class);
        for (SpriteKind kind : SpriteKind.values()) {
            HashMap<Integer, ImageView> imgs = new HashMap<Integer, ImageView>();
            for (int i = 0; i < kind.numFrames; i++) {
                ImageView img = loadSpriteImage(kind, i);
                if (img != null) {
                    imgs.put(i, img);
                }
            }
            spriteImages.put(kind, imgs);
        }
    }

    ImageView loadSpriteImage(SpriteKind kind, int frameNo) {
        String resourceName = "/obj" + kind.objectId + "-" + frameNo;

        // first, try to load specific size image
        //URL iconUrl = TileImages.class.getResource(resourceName + "_" + TILE_WIDTH + "x" + TILE_HEIGHT + ".png");String
        String resourcePath = "/images" + resourceName + ".png";
        LOGGER.log(Level.FINEST, "Load Sprite: " + resourceName + "  full path: " + resourcePath);
        try {
            Image img = new Image(getClass().getResourceAsStream(resourcePath));
            ImageView iv = new ImageView(img);

            if (TILE_WIDTH == 16 && TILE_HEIGHT == 16) {
                return iv;
            }

            iv.setFitWidth(img.getWidth() * TILE_WIDTH / 16);
            iv.setPreserveRatio(true);

            return new ImageView(img);
        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, "Could not find image: " + resourcePath, ex);
            return null;
        }
//        if (iconUrl != null) {
//            return new ImageIcon(iconUrl).getImage();
//        }
//
//        iconUrl = TileImages.class.getResource(resourceName + ".png");
//        if (iconUrl == null) {
//            return null;
//        }
//
//        if (TILE_WIDTH == 16 && TILE_HEIGHT == 16) {
//            return new ImageIcon(iconUrl).getImage();
//        }
//
//        // scale the image ourselves
//        ImageIcon ii = new ImageIcon(iconUrl);
//        int destWidth = ii.getIconWidth() * TILE_WIDTH / 16;
//        int destHeight = ii.getIconHeight() * TILE_HEIGHT / 16;
//
//        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        GraphicsDevice dev = env.getDefaultScreenDevice();
//        GraphicsConfiguration conf = dev.getDefaultConfiguration();
//        BufferedImage bi = conf.createCompatibleImage(destWidth, destHeight, Transparency.TRANSLUCENT);
//        Graphics2D gr = bi.createGraphics();
//
//        gr.drawImage(ii.getImage(),
//                0, 0, destWidth, destHeight,
//                0, 0,
//                ii.getIconWidth(), ii.getIconHeight(),
//                null);
//        return bi;
    }

}
