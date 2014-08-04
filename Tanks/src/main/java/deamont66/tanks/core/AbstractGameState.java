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

import deamont66.tanks.core.gui.Component;
import deamont66.tanks.core.gui.Label;
import deamont66.tanks.core.gui.Panel;
import deamont66.tanks.core.model.Map;
import deamont66.tanks.core.model.Player;
import deamont66.tanks.core.model.effects.Animator;
import deamont66.tanks.utils.GameStateData;
import deamont66.util.ColorUtils;
import deamont66.util.FPSCounter;
import deamont66.util.LWJGLUtils;
import deamont66.util.ResourceLoader;
import deamont66.util.Utils;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.Color;

/**
 *
 * @author Jirka
 */
public abstract class AbstractGameState implements Runnable {

    private boolean running;
    private int fpsLimit = 60;

    protected Panel mainComponent;
    protected Label loaderInfo;
    protected Player[] players = new Player[0];

    private int preloadStep = 0;
    private Color mainComponentColor;

    @Override
    public void run() {
        init();
        loop();
        Display.destroy();
    }

    /**
     * Main game loop
     */
    private void loop() {
        while (!Display.isCloseRequested() && running) {
            LWJGLUtils.update((!Display.isActive()) ? 30 : fpsLimit);
            LWJGLUtils.clean(ColorUtils.BLACK);
            if (preloadStep < 7) { 
                preload();
            } else {
                update(FPSCounter.getTFP());
                render();
            }
            mainComponent.update();
        }
    }

    /**
     * Initilizates state.
     */
    protected void init() {
        initDisplay();
        initOpenGL();
        initFonts();
        initTextures();

        mainComponent = new Panel();
        mainComponent.setSize(App.ORTHO_SIZE.getWidth(), App.ORTHO_SIZE.getHeight());
        mainComponent.setBackground(new Color(255, 0, 0, 0));
        loaderInfo = new Label();
        loaderInfo.setText("Tanks " + App.VERSION);
        loaderInfo.setFontSize(24);
        mainComponent.add(loaderInfo);
        initGUI();
        initModels();
        mainComponentColor = mainComponent.getBackground();
        mainComponent.setBackground(new Color(255, 0, 0, 0));
        running = true;
    }

    /**
     * Initializing method for display (Example: Display.create(),
     * setDiplayMode(),...)
     */
    protected abstract void initDisplay();

    /**
     * Initializing method for OpenGL states, enabling blending, alpha, lights,
     * viewport, ortho or prespective...
     */
    protected abstract void initOpenGL();

    /**
     * Initializing method for texture loading
     */
    protected abstract void initTextures();

    /**
     * Initializing method for fonts loading (TrueTypeFonts, bitmap fonts...)
     */
    protected abstract void initFonts();

    /**
     * Initializing method for GUI - Labels, Buttons, Panels...
     */
    protected abstract void initGUI();

    /**
     * Initializing method for fonts loading (TrueTypeFonts, bitmap fonts...)
     */
    protected abstract void initModels();

    protected void preload() {
        switch (preloadStep) {
            case 0: {
                preLoadStart();
                loaderInfo.setText("Loading fonts...");
                break;
            }
            case 1: {
                preLoadFonts();
                break;
            }
            case 2: {
                loaderInfo.setText("Loading textures...");
                break;
            }
            case 3: {
                preLoadTextures();
                break;
            }
            case 4: {
                loaderInfo.setText("Loading map...");
                break;
            }
            case 5: {
                preLoadMap();
                break;
            }
            case 6: {
                loaderInfo.setText("Starting...");
                loaderInfo.setVisible(false);
                mainComponent.setBackground(mainComponentColor);
                preLoadEnd();
                break;
            }
        }
        preloadStep++;
    }

    /**
     * Method is called when preload state start.
     */
    protected void preLoadStart() {
    }

    /**
     * Method for preload fonts.
     */
    protected abstract void preLoadFonts();

    /**
     * Method for preload textures.
     */
    protected abstract void preLoadTextures();

    /**
     * Method for preload map.
     */
    protected abstract void preLoadMap();

    /**
     * Method is called when preload state end.
     */
    protected void preLoadEnd() {
    }

    /**
     * Method for rendering every frame
     */
    protected abstract void render();

    /**
     * Method for updating sceen every frame
     *
     * @param tfp - number for smooth animations from FPSCounter class usualy
     */
    protected abstract void update(double tfp);

    /**
     * Stop loop, destroy Display and run cleanup before thread death.
     */
    public void destroy() {
        ResourceLoader.destroy();
        running = false;
        mainComponent.destroy();
        Animator.clear();
        Utils.closeWindowWithComponent(Map.getFileChooser());
    }

    /**
     * Adds component to mainComponent.
     *
     * @param component
     */
    protected void add(Component component) {
        mainComponent.add(component);
    }

    /**
     * Called before destroying state for saving data.
     *
     * @return data to save
     */
    protected abstract GameStateData saveData();

    /**
     * Called after start if there was some saved data to restore.
     *
     * @param data saved data
     */
    protected abstract void restoreData(GameStateData data);

    public Player[] getPlayers() {
        return players;
    }

    /**
     * Sets fps limit for state to given fps amount.
     *
     * @param fps
     */
    public void setFpsLimit(int fps) {
        this.fpsLimit = fps;
    }
}
