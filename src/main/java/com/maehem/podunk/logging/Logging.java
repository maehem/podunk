/*
    Licensed to the Apache Software Foundation (ASF) under one or more
    contributor license agreements.  See the NOTICE file distributed with this
    work for additional information regarding copyright ownership.  The ASF
    licenses this file to you under the Apache License, Version 2.0
    (the "License"); you may not use this file except in compliance with the
    License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
    License for the specific language governing permissions and limitations
    under the License.
 */
package com.maehem.podunk.logging;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class Logging {
    public static final Logger LOGGER = Logger.getLogger("com.maehem.podunk");

    public static void configureLogging() {
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(new LoggingFormatter());

        // handler level is the level we see printed.
        handler.setLevel(Level.FINE);

        // Get the top most logger and add our handler.
        LOGGER.setUseParentHandlers(false);  // Prevent INFO and HIGHER from going to stderr.
        LOGGER.addHandler(handler);

        // Log everything and use the DebugTab to filter later.
        // Use handler level to adjust what you see.
        LOGGER.setLevel(Level.FINEST);

    }

    public static Handler getHandler() {
        return LOGGER.getHandlers()[0];
    }
}
