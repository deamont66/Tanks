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

import deamont66.util.LWJGLUtils;
import deamont66.util.NativesLoader;
import deamont66.util.TrueTypeFont;
import deamont66.util.Utils;
import java.awt.Font;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Jirka
 */
public class GraphPhysic {

    public static void main(String[] args) {
        try {

            // amplituda v centimetrech
            float amplitude1 = 100;
            float amplitude2 = 200;

            // frekvence v herzích
            float frecvency1 = 1;
            float frecvency2 = 0.f;

            // posunutí ve stupních
            float shift1 = 0;
            float shift2 = 180 / 4;

            NativesLoader.loadNatives();
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

            TrueTypeFont tf = new TrueTypeFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12), true);

            while (!Display.isCloseRequested()) {
                LWJGLUtils.clean();

                glDisable(GL_TEXTURE_2D);
                // první
                glBegin(GL_LINE_STRIP);
                {
                    glColor3f(0, 1, 0);
                    for (float i = 0; i < 1280; i++) {
                        glVertex2f(i, (float) (amplitude1 * -Math.sin(frecvency1 * Math.PI * 2 * (i / 1280 * 5) + Math.toRadians(shift1))) + 720 / 2);
                    }
                }
                glEnd();
                // druhá
                glBegin(GL_LINE_STRIP);
                {
                    glColor3f(1, 0, 0);
                    for (float i = 0; i < 1280; i++) {
                        glVertex2f(i, (float) (amplitude2 * -Math.sin(frecvency2 * Math.PI * 2 * (i / 1280 * 5) + Math.toRadians(shift2))) + 720 / 2);
                    }
                }
                glEnd();
                // celková
                glBegin(GL_LINE_STRIP);
                {
                    glColor3f(0, 0, 1);
                    for (float i = 0; i < 1280; i++) {
                        glVertex2f(i, (float) (amplitude1 * -Math.sin(frecvency1 * Math.PI * 2 * (i / 1280 * 5) + Math.toRadians(shift1)) + amplitude2 * -Math.sin(frecvency2 * Math.PI * 2 * (i / 1280 * 5) + Math.toRadians(shift2))) + 720 / 2);
                    }
                }
                glEnd();
                // graf (čáry)
                glBegin(GL_LINES);
                {
                    glColor3f(0, 0, 0);
                    glVertex2i(1, 0);
                    glVertex2i(1, 720);
                    glVertex2i(0, 720 / 2);
                    glVertex2i(1280, 720 / 2);
                    for (int i = 0; i < 5; i++) {
                        glVertex2i(i * 1280 / 5, 720 / 2 + 2);
                        glVertex2i(i * 1280 / 5, 720 / 2 - 2);
                    }
                    glVertex2i(1, 720 / 2 - 100);
                    glVertex2i(4, 720 / 2 - 100);
                    
                    glVertex2i(1, 720 / 2 + 100);
                    glVertex2i(4, 720 / 2 + 100);
                }
                glEnd();

                glBegin(GL_QUADS);
                {
                    glColor3f(0, 1, 0);
                    glVertex2i(10, 10);
                    glVertex2i(10 + 10, 10);
                    glVertex2i(10 + 10, 10 + 10);
                    glVertex2i(10, 10 + 10);

                    glColor3f(1, 0, 0);
                    glVertex2i(10, 30);
                    glVertex2i(10 + 10, 30);
                    glVertex2i(10 + 10, 30 + 10);
                    glVertex2i(10, 30 + 10);

                    glColor3f(0, 0, 1);
                    glVertex2i(10, 50);
                    glVertex2i(10 + 10, 50);
                    glVertex2i(10 + 10, 50 + 10);
                    glVertex2i(10, 50 + 10);
                }
                glEnd();

                glEnable(GL_TEXTURE_2D);
                LWJGLUtils.setFontOrtho(1280, 720);
                glColor3f(0, 0, 0);
                tf.drawString(25, 697, "y1 = " + (int)amplitude1 + " cm.sin(2.PI." + (int)frecvency1 + " + " + (int)shift1 + ")" , 1, 1);
                tf.drawString(25, 677, "y2 = " + (int)amplitude2 + " cm.sin(2.PI." + (int)frecvency2 + " + " + (int)shift2 + ")" , 1, 1);
                tf.drawString(25, 657, "y3 = y1 + y2" , 1, 1);
                
                tf.drawString(0, 720 / 2 + 5, "0" , 1, 1);
                tf.drawString(1280 / 5 - 10, 720 / 2 + 5, "1s" , 1, 1);
                tf.drawString(5, 720 / 2 + 95, "100cm" , 1, 1);
                LWJGLUtils.setModelOrtho(1280, 720);

                LWJGLUtils.update(15);
            }

            Display.destroy();

        } catch (LWJGLException ex) {
            Utils.error(ex);
            Display.destroy();
            System.exit(1);
        }


    }
}
