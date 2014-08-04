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
import deamont66.util.ResourceLoader;
import deamont66.util.NativesLoader;
import deamont66.util.TrueTypeFont;
import deamont66.util.Utils;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Dimension;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Jirka
 */
public class MenuTest implements ActionListener {

    public static int WIDTH = 1280;
    public static int HEIGHT = 720;
    
    private Dimension orthoSize = new Dimension(1980, 1080);

    public static void main(String[] args) {
        NativesLoader.loadNatives();
        new MenuTest().start();
    }
    private TrueTypeFont ttf = null;
    private boolean running;

    public void start() {
        try {
            LWJGLUtils.setDisplayMode(WIDTH, HEIGHT, false);
            Display.create();
            Display.setVSyncEnabled(true);

            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glPointSize(6);
            glEnable(GL_POINT_SMOOTH);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            setModelOrtho();

            try {
                InputStream inputStream = MenuTest.class.getResourceAsStream("/fonts/starcraft.ttf");

                Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
                awtFont2 = awtFont2.deriveFont(32f); // set font size
                ttf = new TrueTypeFont(awtFont2, true);

            } catch (Exception e) {
                Utils.error("Cannot load font", e);
            }

            running = true;
            while (!Display.isCloseRequested() && running) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glClearColor(0, 0, 0, 1);

                init();
                update();
                render();
                Display.update();
                // Display.sync(60);
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
    Button single, multiplayer, lan, options, exit;

    private void init() {
        single = new Button(100, 750, 300, 50, "SINGLEPLAYER").setListener(this);
        multiplayer = new Button(100, 800, 300, 50, "MULTIPLAYER").setListener(this).setDisable(true);
        lan = new Button(100, 850, 300, 50, "LAN").setListener(this).setDisable(true);
        options = new Button(100, 900, 300, 50, "OPTIONS").setListener(this).setDisable(true);
        exit = new Button(100, 950, 300, 50, "EXIT").setListener(this);
    }

    private void update() {
        single.update();
        multiplayer.update();
        lan.update();
        options.update();
        exit.update();

        InputHandler.getInputHandler().update();
    }

    private void render() {
        glColor3f(1, 1, 1);
        new Quad(0, 0, orthoSize.getWidth(), orthoSize.getHeight()).setTexture("/menu/menu_image.png").render();
        new Quad(0, 0, orthoSize.getWidth(), orthoSize.getHeight()).setTexture("/menu/menu_shadow.png").render();

        single.render();
        multiplayer.render();
        lan.render();
        options.render();
        exit.render();
    }

    private void drawString(float x, float y, String text, float scaleX, float scaleY) {
        drawString(x, y, text, scaleX, scaleY, TrueTypeFont.ALIGN_LEFT);
    }

    private void drawString(float x, float y, String text, float scaleX, float scaleY, int format) {
        y = orthoSize.getHeight() - y;
        if (ttf != null) {
            glPushMatrix();
            setFontOrtho();
            glEnable(GL_TEXTURE_2D);
            ttf.drawString(x, y, text, scaleX, scaleY, TrueTypeFont.ALIGN_CENTER);
            glDisable(GL_TEXTURE_2D);
            setModelOrtho();
            glPopMatrix();
        }
    }

    private void setFontOrtho() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, orthoSize.getWidth(), 0, orthoSize.getHeight(), 1, -1);
        glMatrixMode(GL_MODELVIEW);
    }

    private void setModelOrtho() {
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, orthoSize.getWidth(), orthoSize.getHeight(), 0, 1, -1);
        glMatrixMode(GL_MODELVIEW);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("Action performed: " + e.getActionCommand());
        if(e.getActionCommand().equalsIgnoreCase("exit")) {
            running = false;
        } else if(e.getActionCommand().equalsIgnoreCase("singleplayer")) {
            System.out.println("Not implemented yet!");
        }
    }

    private class Button extends Quad {

        private String text;
        private boolean hover;
        private boolean disable;
        private ActionListener listener = null;

        public Button(float x, float y, float width, float height, String text) {
            super(x, y, width, height);
            this.text = text;
        }

        public Button setText(String text) {
            this.text = text;
            return this;
        }

        public Button setListener(ActionListener listener) {
            this.listener = listener;
            return this;
        }
        
        public Button removeListener() {
            this.listener = null;
            return this;
        }
        
        public Button setDisable(boolean disable) {
            this.disable = disable;
            return this;
        }

        public boolean isDisable() {
            return disable;
        }

        public String getText() {
            return text;
        }

        @Override
        public void render() {
            if (super.texture != -1) {
                super.render();
            }
            if (isDisable()) {
                glColor3f(0.75f, 0.75f, 0.75f);
            } else {
                glColor3f(1f, 1f, 1f);
            }
            drawString(super.position.x + getWidth() / 2 + ((hover) ? 2 : 0), super.position.y + getHeight() / 5 * 4 + ((hover) ? 2 : 0), text, 1f, 1f, TrueTypeFont.ALIGN_CENTER);
        }

        public void update() {
            if (isDisable()) {
                return;
            }
            InputHandler in = InputHandler.getInputHandler();
            float mouseX = Mouse.getX() / (float) WIDTH * orthoSize.getWidth();
            float mouseY = orthoSize.getHeight() - Mouse.getY() / (float) HEIGHT * orthoSize.getHeight();
            System.out.println(mouseX + " . " + mouseY);
            if (mouseX > super.position.x && mouseX < super.position.x + getWidth() && mouseY > super.position.y && mouseY < super.position.y + getHeight()) {
                hover = true;
                if (in.isMouseButtonReleased(0) && listener != null) {
                    listener.actionPerformed(new ActionEvent(this, 0, text));
                }
            } else {
                hover = false;
            }
        }
    }

    private class Quad {

        private Vector2f position;
        private Vector2f size;
        private int texture = -1;

        public Quad(float x, float y, float width, float height) {
            this(new Vector2f(x, y), new Vector2f(width, height));
        }

        public Quad(Vector2f position, Vector2f size) {
            this.position = position;
            this.size = size;
        }

        public Quad setPosition(Vector2f position) {
            this.position = position;
            return this;
        }

        public Vector2f getPosition() {
            return new Vector2f(position);
        }

        public Quad setWidth(float width) {
            this.size.x = width;
            return this;
        }

        public Quad setHeight(float height) {
            this.size.y = height;
            return this;
        }

        public float getWidth() {
            return this.size.x;
        }

        public float getHeight() {
            return this.size.y;
        }

        public Quad setTexture(String loc) {
            texture = ResourceLoader.glLoadPNG(loc);
            return this;
        }

        public void render() {
            if (texture != -1) {
                glEnable(GL_TEXTURE_2D);
                glBindTexture(GL_TEXTURE_2D, texture);
            }
            glPushMatrix();
            glTranslatef(position.x, position.y, 0);
            glBegin(GL_QUADS);
            {
                glTexCoord2f(0, 0);
                glVertex2f(0, 0);
                glTexCoord2f(1, 0);
                glVertex2f(size.x, 0);
                glTexCoord2f(1, 1);
                glVertex2f(size.x, size.y);
                glTexCoord2f(0, 1);
                glVertex2f(0, size.y);
            }
            glEnd();
            glPopMatrix();
            if (texture != -1) {
                glDisable(GL_TEXTURE_2D);
            }
        }
    }
}
