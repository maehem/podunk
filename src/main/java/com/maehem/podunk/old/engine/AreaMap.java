/*
 * MIT License
 *
 * Copyright (c) 2024 Mark J. Koch ( @maehem on GitHub )
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

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class AreaMap extends HashMap<MapPoint, MapCell> {

    public Map asSortedMap() {
        TreeMap<MapPoint, MapCell> sortedMap = new TreeMap<>((MapPoint c1, MapPoint c2) -> {
            int d = c1.y - c2.y;
            if (d == 0) {
                d = c1.x - c2.y;
            }
            return d;
        });
        sortedMap.putAll(this); // copy values from other map
        return sortedMap;
    }

}
