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
package deamont66.test;

import deamont66.util.InputHandler;
import deamont66.util.LWJGLUtils;
import deamont66.util.NativesLoader;
import deamont66.util.TrueTypeFont;
import deamont66.util.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Dimension;

/**
 *
 * @author Jirka
 */
public class PixelTest {

    public static int WIDTH = 1280;
    public static int HEIGHT = 720;

    public static void main(String[] args) {
        NativesLoader.loadNatives();
        new PixelTest().start();
    }
    private TrueTypeFont ttf = null;
    private boolean running;

    public void start() {
        try {
            LWJGLUtils.setDisplayMode(WIDTH, HEIGHT, false);
            Display.create();
            Display.setVSyncEnabled(true);

            //glEnable(GL_TEXTURE_2D);
            //glEnable(GL_BLEND);
            //glPointSize(6);
            //glEnable(GL_POINT_SMOOTH);
            //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            LWJGLUtils.setModelOrtho(WIDTH, HEIGHT);

            running = true;
            while (!Display.isCloseRequested() && running) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glClearColor(0, 0, 0, 1);

                init();
                update();
                render();
                Display.update();
                Display.sync(30);
            }

            Display.destroy();

        } catch (LWJGLException ex) {
            Utils.error(ex);
            Display.destroy();
            System.exit(1);
        } catch (Exception ex) {
            Logger.getLogger(MenuTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void init() {

    }

    private void update() {

        InputHandler.getInputHandler().update();
    }

    private void render() {
        glColor3f(1, 1, 1);
        glTranslatef(1, 0, 0);
        glBegin(GL_POINTS);
        {
            glVertex2i(0, 720 / 2);
        }
        glEnd();
        /*
        glBegin(GL_QUADS);
        {
            glVertex2i(0, 0 + 720 / 2);
            glVertex2i(200, 0 + 720 / 2);
            glVertex2i(150, 200 + 720 / 2);
            glVertex2i(20, 200 + 720 / 2);
        }
        glEnd();
        */
    }
}
