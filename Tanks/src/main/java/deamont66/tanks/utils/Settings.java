/*
 * Copyright (c) 2012 - 2013, Jiří Šimeček
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are those
 * of the authors and should not be interpreted as representing official policies,
 * either expressed or implied, of the FreeBSD Project.
 * 
 */
package deamont66.tanks.utils;

import deamont66.tanks.core.App;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Jirka
 */
public class Settings {

    private static Settings instance = null;
    private Properties properities;

    private Settings() {
        properities = new Properties();
        loadFromFile("settings.cfg");
    }

    public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
        }
        return instance;
    }
    
    public String getResolution() {
        return properities.getProperty("resolution", "1280x720");
    }
    
    public void setResolution(String resolution) {
        properities.setProperty("resolution", resolution);
    }
    
    public String getMainState() {
        return properities.getProperty("mainState", "MenuGameState");
    }
    
    public void setMainState(String resolution) {
        properities.setProperty("mainState", resolution);
    }
    
    public String getPlayerName() {
        return properities.getProperty("player", "Player");
    }
    
    public void setPlayerName(String name) {
        properities.setProperty("player", name);
    }
    
    public boolean getVSync() {
        return Boolean.parseBoolean(properities.getProperty("vsync", "true"));
    }
    
    public void setVSync(boolean vsync) {
        properities.setProperty("vsync", vsync+"");
    }

    public void save() {
        saveToFile("settings.cfg");
    }

    private void loadFromFile(String file) {
        try {
            File cfgFile = new File(App.getSettingsFolderFile().getAbsolutePath() + File.separator + file);
            if (!cfgFile.exists()) {
                if (!cfgFile.createNewFile()) {
                    throw new IOException("Cannot create file: " + cfgFile.getAbsolutePath());
                }
            }
            properities.load(new FileInputStream(cfgFile));
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
    
    private void saveToFile(String file) {
        try {
            File cfgFile = new File(App.getSettingsFolderFile().getAbsolutePath() + File.separator + file);
            if (!cfgFile.exists()) {
                if (!cfgFile.createNewFile()) {
                    throw new IOException("Cannot create file: " + cfgFile.getAbsolutePath());
                }
            }
            properities.store(new FileOutputStream(cfgFile), "Settings for game Tanks");
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }
}
