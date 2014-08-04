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

import deamont66.tanks.core.App;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Color;

/**
 *
 * @author Jirka
 */
public class LWJGLUtils {

    public static void drawString(TrueTypeFont ttf, float x, float y, String text, float scaleX, float scaleY) {
        drawString(ttf, x, y, text, scaleX, scaleY, TrueTypeFont.ALIGN_LEFT);
    }

    public static void drawString(TrueTypeFont ttf, float x, float y, String text, float scaleX, float scaleY, int format) {
        if (ttf != null) {
            y = App.ORTHO_SIZE.getHeight() - y;
            setFontOrtho(App.ORTHO_SIZE.getWidth(), App.ORTHO_SIZE.getHeight());
            glEnable(GL_TEXTURE_2D);
            ttf.drawString(x, y, text, scaleX, scaleY, format);
            glDisable(GL_TEXTURE_2D);
            setModelOrtho(App.ORTHO_SIZE.getWidth(), App.ORTHO_SIZE.getHeight());
        }
    }

    public static void setFontOrtho(int width, int height) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, 0, height, 1, -1);
        glMatrixMode(GL_MODELVIEW);
    }

    public static void setModelOrtho(int width, int height) {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, height, 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
    }

    /**
     * Calls glClear, glColor and glLoadIndentity
     */
    public static void clean() {
        clean(new Color(255, 255, 255, 255));
    }

    /**
     * Calls clean with specified color
     *
     * @param c
     */
    public static void clean(Color c) {
        float r, g, b, a;
        r = g = b = a = 0f;
        if (c.getAlpha() > 0) {
            a = 255 / (float) c.getAlpha();
        }
        if (c.getRed() > 0) {
            r = 255 / (float) c.getRed();
        }
        if (c.getGreen() > 0) {
            g = 255 / (float) c.getGreen();
        }
        if (c.getBlue() > 0) {
            b = 255 / (float) c.getBlue();
        }
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(r, g, b, a);
        glLoadIdentity();
    }

    /**
     * Calls Display.update(), .sync() and InputHandler.update(). FPS counter is
     * updated too.
     *
     * @param fpsLimit fpsLimit for sync
     */
    public static void update(int fpsLimit) {
        InputHandler.getInputHandler().update();
        Display.update();
        if (fpsLimit > 0) {
            Display.sync(fpsLimit);
        }
        FPSCounter.update();
    }

    /**
     * Calls Display.update() and InputHandler.update(). FPS counter is updated
     * too.
     */
    public static void update() {
        update(-1);
    }

    /**
     * Render quad at position 0,0 with height and width.
     * @param width
     * @param height 
     */
    public static void renderQuad(int width, int height) {
        renderQuad(0, 0, width, height);
    }

    /**
     * Render quad at position x,y with height and width.
     * @param x
     * @param y
     * @param width
     * @param height 
     */
    public static void renderQuad(float x, float y, float width, float height) {
        renderQuad(x, y, width, height, true, false, 0, 0);
    }

    /**
     * Render quad at position 0,0 with height and width. If glBeginEnd is true will skip glBegin and glEnd calls.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param glBeginEnd 
     */
    public static void renderQuad(float x, float y, float width, float height, boolean glBeginEnd) {
        renderQuad(x, y, width, height, glBeginEnd, false, 0, 0);
    }

    /**
     * Render quad at position 0,0 with height and width. If glBeginEnd is true will skip glBegin and glEnd calls. If repeatTexture is true will reapeat texture using textureWidth and textureHeight.
     * @param x
     * @param y
     * @param width
     * @param height
     * @param glBeginEnd
     * @param repeatTexture
     * @param textureWidth
     * @param textureHeight 
     */
    public static void renderQuad(float x, float y, float width, float height, boolean glBeginEnd, boolean repeatTexture, float textureWidth, float textureHeight) {
        if (glBeginEnd) {
            glBegin(GL_QUADS);
        }

        glTexCoord2f(0, 0);
        glVertex2f(x, y);
        glTexCoord2f((repeatTexture) ? width / textureWidth : 1, 0);
        glVertex2f(x + width, y);
        glTexCoord2f((repeatTexture) ? width / textureWidth : 1, (repeatTexture) ? height / textureHeight : 1);
        glVertex2f(x + width, y + height);
        glTexCoord2f(0, (repeatTexture) ? height / textureHeight : 1);
        glVertex2f(x, y + height);

        if (glBeginEnd) {
            glEnd();
        }
    }

    /**
     * Set the display mode to be used
     *
     * @param width The width of the display required
     * @param height The height of the display required
     * @param fullscreen True if we want fullscreen mode
     * @throws org.lwjgl.LWJGLException
     */
    public static void setDisplayMode(int width, int height, boolean fullscreen) throws LWJGLException {

        // return if requested DisplayMode is already set
        if ((Display.getDisplayMode().getWidth() == width)
                && (Display.getDisplayMode().getHeight() == height)
                && (Display.isFullscreen() == fullscreen)) {
            return;
        }

        DisplayMode targetDisplayMode = null;

        if (fullscreen) {
            DisplayMode[] modes = Display.getAvailableDisplayModes();
            int freq = 0;
            for (DisplayMode current : modes) {
                if ((current.getWidth() == width) && (current.getHeight() == height)) {
                    if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
                        if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
                            targetDisplayMode = current;
                            freq = targetDisplayMode.getFrequency();
                        }
                    }

                    // if we've found a match for bpp and frequence against the 
                    // original display mode then it's probably best to go for this one
                    // since it's most likely compatible with the monitor
                    if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel())
                            && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
                        targetDisplayMode = current;
                        break;
                    }
                }
            }
        } else {
            targetDisplayMode = new DisplayMode(width, height);
        }

        if (targetDisplayMode == null) {
            throw new LWJGLException("Failed to find value mode: " + width + "x" + height + " fs=" + fullscreen);
        }

        Display.setDisplayMode(targetDisplayMode);
        Display.setFullscreen(fullscreen);
    }

    /**
     * Will save current screen to file in screenShot file 
     * @param name
     * @return 
     */
    public static File saveScreenshot(String name) {
        final String format = "png"; // Example: "PNG" or "JPG"
        final File file = new File(App.getScreenFolderFile(), name + "." + format); // The file to save to.

        glReadBuffer(GL_FRONT);
        final int width = Display.getDisplayMode().getWidth();
        final int height = Display.getDisplayMode().getHeight();
        final int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
        final ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
        glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int i = (x + (width * y)) * bpp;
                        int r = buffer.get(i) & 0xFF;
                        int g = buffer.get(i + 1) & 0xFF;
                        int b = buffer.get(i + 2) & 0xFF;
                        image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
                    }
                }

                try {
                    ImageIO.write(image, format, file);
                    System.out.println("Screenshot saved to: " + file.getAbsolutePath());
                } catch (IOException e) {
                    Utils.error("Cannot save screenshot", e);
                }
            }
        }, "screenshot");
        t.start();
        return file;
    }
}
