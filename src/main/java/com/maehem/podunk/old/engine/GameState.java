/*
 * MIT License
 *
 * Copyright (c) 2024 Mark J. Koch ( @maehem on GitHub )
 *
 * Portions of this software are Copyright (c) 2018 Henadzi Matuts and are
 * derived from their project: https://github.com/HenadziMatuts/Reuromancer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.maehem.podunk.old.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Properties;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class GameState extends Properties {
    private final ArrayList<GameStateListener> listeners = new ArrayList<>();
    private boolean showDebug = true;
    private String shortName;
    private String longName;
    private String version;
    private File gameSaveFile = null;

    public void setShowDebug(boolean show) {
        showDebug = show;
        for (GameStateListener l : listeners) {
            l.gameStateShowDebug(this, showDebug);
        }
    }

    public void toggleDebugShowing() {
        setShowDebug(!showDebug);
    }

    public void addListener(GameStateListener l) {
        listeners.add(l);
    }

    public void removeListener(GameStateListener l) {
        listeners.remove(l);
    }

    public String getShortGameName() {
        return shortName;
    }

    public String getLongGameName() {
        return longName;
    }

    public void setGameName(String shortName, String longName) {
        this.shortName = shortName;
        this.longName = longName;

        gameSaveFile = new File(
                System.getProperty("user.home")
                + File.separator + "Documents"
                + File.separator + shortName
                + File.separator + "save-0.properties"
        );
    }

    public String getGameVersion() {
        return version;
    }

    public void setGameVersion(String versionString) {
        this.version = versionString;
    }
}
