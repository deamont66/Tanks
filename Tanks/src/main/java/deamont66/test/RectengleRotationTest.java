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

import deamont66.util.FPSCounter;
import deamont66.util.InputHandler;
import deamont66.util.LWJGLUtils;
import deamont66.util.MyRectangle;
import deamont66.util.NativesLoader;
import deamont66.util.Utils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Color;

/**
 *
 * @author Jirka
 */
public class RectengleRotationTest {

    public static void main(String[] args) {
        NativesLoader.loadNatives();

        new RectengleRotationTest().start();
    }

    MyRectangle rect = new MyRectangle(400, 400, 250, 100, 0);
    float rotation = 0;
    
    private void start() {
        try {
            LWJGLUtils.setDisplayMode(1280, 720, false);
            Display.create();
            Display.setVSyncEnabled(true);

            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glPointSize(6);
            glEnable(GL_POINT_SMOOTH);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0, 1280, 720, 0, 1, -1);
            glMatrixMode(GL_MODELVIEW);

            FPSCounter.init(30);

            while (!Display.isCloseRequested()) {
                update();
                render();
            }

            Display.destroy();

        } catch (LWJGLException ex) {
            Utils.error(ex);
            Display.destroy();
            System.exit(1);
        }
    }

    private void update() {
        LWJGLUtils.update();
        InputHandler in = InputHandler.getInputHandler();
        if(in.isKeyDown(InputHandler.KEY_SPACE)) {
             rotation++;
             rect = new MyRectangle(400, 400, 250, 100, rotation);
        }
    }

    private void render() {
        LWJGLUtils.clean(new Color(0, 0, 0));        
       
        glDisable(GL_TEXTURE_2D);
        glColor3f(1, 1, 0);
        glBegin(GL_QUADS);
        {
            glVertex2f(rect.getMinX(), rect.getMinY());
            glVertex2f(rect.getMaxX(), rect.getMinY());
            glVertex2f(rect.getMaxX(), rect.getMaxY());
            glVertex2f(rect.getMinX(), rect.getMaxY());
        }
        glEnd();
        
        glPushMatrix();
        glTranslatef(400 + 250 / 2f, 400 + 100 / 2f, 0);
        glColor3f(1, 0, 1);
        glRotatef(rotation, 0, 0, 1);
        glBegin(GL_QUADS);
        {
            glVertex2f(-250 / 2f, -100 / 2f);
            glVertex2f(250 / 2f, -100 / 2f);
            glVertex2f(250 / 2f, 100 / 2f);
            glVertex2f(-250 / 2f, 100 / 2f);
        }
        glEnd();
        glPopMatrix();
    }
}
