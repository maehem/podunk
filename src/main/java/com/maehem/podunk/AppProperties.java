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
package com.maehem.podunk;

import com.maehem.podunk.logging.Logging;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.HostServices;

/**
 * Manage properties for the application
 *
 * @author Mark J Koch ( @maehem on GitHub )
 */
public class AppProperties extends Properties {

    private static final Logger LOGGER = Logging.LOGGER;

//    public static final String DAT_DIR = "DAT";
//    public static final String CACHE_DIR = "cache";
    private static final String APP_VERSION = "0"; // Get from Viking.java ???

    private static AppProperties instance = null;
    private final File propFile;

    private HostServices hostServices;

    private AppProperties() {
        propFile = initPropFile();
        if (propFile.exists()) {
            load();
        } else {
            propFile.getParentFile().mkdirs();
            save(true);
        }
    }

    public static AppProperties getInstance() {
        if (instance == null) {
            instance = new AppProperties();
        }

        return instance;
    }

    public final void load() {
        try {
            load(new FileInputStream(propFile));
            LOGGER.log(Level.SEVERE, "Loaded Podunk settings file: {0}", propFile.getAbsolutePath());
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }

    public final void save() {
        save(false);
    }

    public final void save(boolean asNew) {
        try {
            String msg = "Saved Podunk Settings File.";
            if (asNew) {
                msg = "Created New Podunk Settings File.";
            }
            store(new FileOutputStream(propFile), msg);
            LOGGER.log(Level.SEVERE, "{0} :: {1}", new Object[]{msg, propFile.getAbsolutePath()});
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public File getPropFile() {
        return propFile;
    }

    private static File initPropFile() {
        final String os = System.getProperty("os.name");
        if (os != null && os.startsWith("Mac")) {
            return new File(System.getProperty("user.home")
                    + File.separator + "Library"
                    + File.separator + "Application Support"
                    + File.separator + "Podunk"
                    + File.separator + APP_VERSION
                    + File.separator + "settings.properties"
            );
        } else {
            // TODO: Sort this out per platform later.
            return new File(System.getProperty("user.home")
                    + File.separator + "podunk-settings.properies"
            );
        }
    }

    public HostServices getHostServices() {
        return hostServices;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public String getSaveFolder() {
        return getPropFile().getParent();
    }
}
