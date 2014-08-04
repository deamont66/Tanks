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
package deamont66.tanks.core;

import deamont66.util.NativesLoader;
import deamont66.tanks.utils.Settings;
import deamont66.util.Utils;
import java.io.File;
import java.io.IOException;
import org.lwjgl.util.Dimension;

public class App {

    public static final Dimension ORTHO_SIZE = new Dimension(1600, 1200);
    public static String VERSION = "0.1";
    
    private static final String nameOfApp = "tanks"; 

    public static void main(String[] args) {
        // create app folders instalations
        createAppFolders();
        // loads matives like OpenGL, OpenAL
        NativesLoader.loadNatives();
        // loads settings
        Settings s = Settings.getInstance();
        s.setResolution("800x600");
        // SingleGameState
        s.setMainState("MenuGameState");
        s.setVSync(false);
        // Starts game state with setings
        String[] size = s.getResolution().split("x");
        StateManager.setSize(Integer.valueOf(size[0]), Integer.valueOf(size[1]));
        StateManager.createState(s.getMainState(), true);
    }

    /**
     * Create folders for setting and maps.
     * Loads default maps from class path.
     */
    private static void createAppFolders() {
        File f = getSettingsFolderFile();
        try {
            if (!f.exists()) {
                if (!f.mkdir()) {
                    throw new IOException("Cannot create folder: " + f.getAbsolutePath());
                }
            }
            f = getMapFolderFile();
            if (!f.exists()) {
                if (!f.mkdir()) {
                    throw new IOException("Cannot create folder: " + f.getAbsolutePath());
                }
            }
            f = getScreenFolderFile();
            if (!f.exists()) {
                if (!f.mkdir()) {
                    throw new IOException("Cannot create folder: " + f.getAbsolutePath());
                }
            }
            f = new File(getMapFolderFile().getAbsolutePath() + File.separator + "default");
            if (!f.exists()) {
                if (!f.mkdir()) {
                    throw new IOException("Cannot create folder: " + f.getAbsolutePath());
                }
            }
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
        
        // copy maps from jar
        try {
            Utils.copyFile(App.class.getResourceAsStream("/maps/default/" + "empty.tmap"), new File(f.getAbsoluteFile() + File.separator + "empty.tmap"));
            Utils.copyFile(App.class.getResourceAsStream("/maps/default/" + "default.tmap"), new File(f.getAbsoluteFile() + File.separator + "default.tmap"));
        } catch (Exception ex) {
            // files already exist, can be ignored
            Utils.error("Default maps already copied to installation path.", ex);
            System.out.println("Default maps already copied to installation path.");
        }
    }
    
    /**
     * Retunrs settings folder, .tanks folder in user's home.
     * @return settings file
     */
    public static File getSettingsFolderFile() {
        return new File(System.getProperty("user.home") + File.separator + "." + nameOfApp);
    }
    
    /**
     * Retunrs maps folder, .tanks/maps folder in user's home.
     * @return maps file
     */
    public static File getMapFolderFile() {
        return new File(System.getProperty("user.home") + File.separator + "." + nameOfApp + File.separator + "maps");
    }
    
    /**
     * Retunrs screenshots folder, .tanks/screenshots folder in user's home.
     * @return screenshots file
     */
    public static File getScreenFolderFile() {
        return new File(System.getProperty("user.home") + File.separator + "." + nameOfApp + File.separator + "screenshots");
    }
}
