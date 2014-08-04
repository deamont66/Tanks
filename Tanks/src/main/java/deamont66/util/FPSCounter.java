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
package deamont66.util;

import org.lwjgl.Sys;

/**
 *
 * @author Jirka
 */
public class FPSCounter {

    private static long lastFPS;
    private static int fps;
    private static long lastFrame;
    private static int FPS;
    private static int GAME_SPEED = 60;
    private static double tfp;

    /**
     * Initialize counter.
     * @param gameSpeed Game speed in fps for getTFP().
     */
    public static void init(int gameSpeed) {
        GAME_SPEED = gameSpeed;
        lastFPS = getTime();
        getDelta();
        getDelta();
    }

    /**
     * Should be called every frame once to count fps.
     */
    public static void update() {
        double currentFPS = 1000 / (double) getDelta();
        tfp = GAME_SPEED / currentFPS;
        updateFPS();
    }

    /**
     * @return Current fps, when counter isn't running at least one second
     * return number of current frame.
     */
    public static int getFPS() {
        return (FPS == 0) ? fps : FPS;
    }

    /**
     * @return Double value for smoooth runnig of game based on GAME SPEED. (0.0 to 1.0).
     */
    public static double getTFP() {
        return tfp;
    }

    /**
     * @return Time from start of application (first call) in milisec.
     */
    public static long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    private static int getDelta() {
        long time = getTime();
        int delta = (int) (time - lastFrame);
        lastFrame = time;
        // lastDelta = delta;
        return delta;
    }

    private static void updateFPS() {
        if (getTime() - lastFPS > 1000) {
            FPS = fps;
            fps = 0;
            lastFPS += 1000;
        }
        fps++;
    }

    public static int getGameSpeed() {
        return GAME_SPEED;
    }
    
    
}
