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

import deamont66.tanks.utils.GameStateData;
import deamont66.util.LWJGLUtils;
import deamont66.util.Utils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.util.Dimension;

/**
 *
 * @author Jirka
 */
public class StateManager {

    private static final Dimension sizeOfWindow = new Dimension();
    private static boolean fullscreen;
    private static AbstractGameState activeState;
    private static Thread stateThread;
    // saved data
    private static GameStateData stateData;

    private StateManager() {
    }

    /**
     * Set width of window, changes changes will take effect after next call of
     * createState or resetState.
     *
     * @param width
     */
    public static void setWidth(int width) {
        sizeOfWindow.setWidth(width);
    }

    /**
     * Set height of window, changes changes will take effect after next call of
     * createState or resetState.
     *
     * @param height
     */
    public static void setHeight(int height) {
        sizeOfWindow.setHeight(height);
    }

    /**
     * Set size of window, changes will take effect after next call of
     * createState or resetState.
     *
     * @param width
     * @param height
     */
    public static void setSize(int width, int height) {
        sizeOfWindow.setSize(width, height);
    }

    /**
     * Set fullscreen of window, changes changes will take effect after next
     * call of createState or resetState.
     *
     * @param fullscreen
     */
    public static void setFullscreen(boolean fullscreen) {
        StateManager.fullscreen = fullscreen;
    }

    public static int getWidth() {
        return sizeOfWindow.getWidth();
    }

    public static int getHeight() {
        return sizeOfWindow.getHeight();
    }

    public static boolean isFullscreen() {
        return fullscreen;
    }

    public static void createState(String name) {
        createState(name, false);
    }

    /**
     * Finds class in package deamont66.tanks.core and tries to start it like
     * new state.
     *
     * @param name - name of class (child of AbstractGameState)
     * @param init - boolean if true will not try save and restore data from old
     * state
     */
    public static void createState(String name, boolean init) {
        try {
            if (!init) {
                saveData();
            }
            stopState();
            Class<?> act = Class.forName("deamont66.tanks.core.states." + name);
            AbstractGameState instanceOfMyClass = (AbstractGameState) act.newInstance();
            activeState = instanceOfMyClass;
            startState();
            if (!init) {
                restoreData();
            }
        } catch (Exception ex) {
            Utils.error("State " + name + " wasn't found", ex);
        }
    }

    /**
     * Start new state in new loop.
     */
    private static void startState() {
        stateThread = new Thread(activeState, activeState.getClass().getSimpleName());
        stateThread.start();
        System.out.println(activeState.getClass().getName() + " started.");
    }

    /**
     * Call destroy of state and join to his thread and wait to his end.
     */
    private static void stopState() {
        if (stateThread != null && stateThread.isAlive()) {
            try {
                activeState.destroy();
                stateThread.join();
            } catch (InterruptedException ex) {
                Utils.error("Cannot destroy old state", ex);
            }
            System.out.println(activeState.getClass().getSimpleName() + " stoped.");
        }
    }

    /**
     * Will destroy current loop and start it again.
     */
    public static void resetState() {
        createState(activeState.getClass().getSimpleName(), false);
    }

    public static void saveData() {
        if (activeState != null) {
            stateData = activeState.saveData();
        }
    }

    public static void restoreData() {
        if (activeState != null) {
            activeState.restoreData(stateData);
        }
    }

    /**
     * Tries to set up display mode for lwjgl display by StateManager settings.
     *
     * @throws LWJGLException when is something wrong with Display setting.
     */
    public static void setDisplayMode() throws LWJGLException {
        if (isFullscreen()) {
            for (DisplayMode mode : Display.getAvailableDisplayModes()) {
                if (mode.getHeight() == getHeight() && mode.getWidth() == getWidth() && mode.isFullscreenCapable()) {
                    LWJGLUtils.setDisplayMode(getWidth(), getHeight(), fullscreen);
                    return;
                }
            }
            throw new LWJGLException("This resolution cannot be set up for fullscreen, try other one.");
        } else {
            if (getWidth() > 0 && getHeight() > 0) {
                LWJGLUtils.setDisplayMode(getWidth(), getHeight(), fullscreen);
                return;
            }
            throw new LWJGLException("This resolution cannot be set up. It has to be bigger then zero.");
        }
    }

    /**
     * Runs code in different thread - current state can reset himself, set
     * properities of window or start new state.
     *
     * @param run - Code to run
     */
    public static void invokeLater(Runnable run) {
        Thread t = new Thread(run, "StateManager - invokeLater");
        t.start();
    }
}
