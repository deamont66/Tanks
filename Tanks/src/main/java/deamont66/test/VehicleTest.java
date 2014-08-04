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

import deamont66.tanks.core.App;
import deamont66.tanks.core.StateManager;
import deamont66.util.InputHandler;
import deamont66.util.LWJGLUtils;
import deamont66.util.ResourceLoader;
import deamont66.util.NativesLoader;
import deamont66.util.Utils;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Jirka
 */
public class VehicleTest {

    Tank tank = new Tank(PlayerType.PLAYER_1, false);
    // Tank tank0 = new Tank(PlayerType.PLAYER_2, true);
    List<Bullet> bullets;

    public static void main(String[] args) {
        NativesLoader.loadNatives();

        new VehicleTest().start();
    }

    public VehicleTest() {
        this.bullets = new ArrayList<Bullet>();
    }

    public void start() {
        try {
            LWJGLUtils.setDisplayMode(1280, 720, false);
            Display.create();
            Display.setVSyncEnabled(true);

            tank.init();
            // tank0.init();

            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glPointSize(6);
            glEnable(GL_POINT_SMOOTH);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0, 1280, 720, 0, 1, -1);
            glMatrixMode(GL_MODELVIEW);

            while (!Display.isCloseRequested()) {
                update();
                render();

                Display.update();
            }

            Display.destroy();

        } catch (LWJGLException ex) {
            Utils.error(ex);
            Display.destroy();
            System.exit(1);
        }
    }

    private void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0, 1, 0, 1);
        glDisable(GL_TEXTURE_2D);

        glPushMatrix();
        glBegin(GL_POINTS);
        {
            for (int i = 0; i < bullets.size(); i++) {
                Bullet but = bullets.get(i);
                glColor4f(0, 0, 0, 1);
                glPointSize(6);
                glVertex2f(but.getPosition().x, but.getPosition().y);
                glColor4f(1, 1, 1, 1);
                glPointSize(5);
                glVertex2f(but.getPosition().x, but.getPosition().y);
            }
        }
        glEnd();
        glPopMatrix();

        glEnable(GL_TEXTURE_2D);
        tank.render();
        // tank0.render();
    }

    private void update() {
        tank.update();
        // tank0.update();

        for (int i = 0; i < bullets.size(); i++) {
            Bullet but = bullets.get(i);
            if (System.currentTimeMillis() - but.getShotTime() > but.BULLET_LIFETIME) {
                bullets.remove(i);
                i--;
                continue;
            }
            but.update();
        }
        InputHandler.getInputHandler().update();
    }

    public enum PlayerType {

        NO_UPDATE, PLAYER_1, PLAYER_2
    }

    private class Tank {

        private final PlayerType type;
        private int tank_h = -1;
        private int tank_t = -1;
        private Vector2f position = new Vector2f(640, 360);
        private float rotation_h;
        private float rotation_t;
        private float speed;
        private final boolean freezeMouseAim;
        private long lastShot;

        public Tank(PlayerType type) {
            this(type, false);
        }

        public Tank(PlayerType type, boolean freezeMouseAim) {
            this.type = type;
            this.freezeMouseAim = freezeMouseAim;
        }

        public Tank() {
            this(PlayerType.PLAYER_1, false);
        }

        public float getX() {
            return position.x;
        }

        public float getY() {
            return position.y;
        }

        public void setX(float x) {
            position.x = x;
        }

        public void setY(float y) {
            position.y = y;
        }

        public void setPosition(Vector2f position) {
            this.position = position;
        }

        public void setRotation_h(float rotation_h) {
            this.rotation_h = rotation_h;
        }

        public void setRotation_t(float rotation_t) {
            this.rotation_t = rotation_t;
        }

        public Vector2f getPosition() {
            return position;
        }

        public float getRotation_h() {
            return rotation_h;
        }

        public float getRotation_t() {
            return rotation_t;
        }

        public void init() {
            tank_h = ResourceLoader.glLoadPNG("/tanks/Tiger/Tiger_hs.png");
            tank_t = ResourceLoader.glLoadPNG("/tanks/Tiger/Tiger_ts.png");
        }

        public void render() {
            // RENDER SHADOW
            glColor4f(0, 0, 0, 0.8f);
            renderTank(4, -4);
            // RENDER TANK
            glColor4f(1, 1, 1, 1);
            renderTank();
        }

        public void update() {
            int KEY_UP, KEY_DOWN, KEY_LEFT, KEY_RIGHT, KEY_FIRE;
            if (type == PlayerType.PLAYER_1) {
                KEY_UP = InputHandler.KEY_W;
                KEY_DOWN = InputHandler.KEY_S;
                KEY_LEFT = InputHandler.KEY_A;
                KEY_RIGHT = InputHandler.KEY_D;
                KEY_FIRE = InputHandler.KEY_SPACE;
            } else if (type == PlayerType.PLAYER_2) {
                KEY_UP = InputHandler.KEY_UP;
                KEY_DOWN = InputHandler.KEY_DOWN;
                KEY_LEFT = InputHandler.KEY_LEFT;
                KEY_RIGHT = InputHandler.KEY_RIGHT;
                KEY_FIRE = InputHandler.KEY_RCONTROL;
            } else {
                return;
            }

            InputHandler in = InputHandler.getInputHandler();
            if (in.isKeyDown(KEY_UP)) {
                speed += 3;
            }
            if (in.isKeyDown(KEY_DOWN)) {
                speed -= 2;
            }
            if (in.isKeyDown(KEY_LEFT)) {
                rotation_h -= 2;
                if (rotation_h <= 0) {
                    rotation_h = 360;
                }
            }
            if (in.isKeyDown(KEY_RIGHT)) {
                rotation_h += 2;
                if (rotation_h >= 360) {
                    rotation_h = 0;
                }
            }
            if (in.isKeyDown(KEY_FIRE)) {
                if (System.currentTimeMillis() - lastShot > 250) {
                    Vector2f bullet_pos = new Vector2f(position);
                    bullet_pos.translate((float) (Math.cos(Math.toRadians(rotation_h + 90)) * 32), (float) (Math.sin(Math.toRadians(rotation_h + 90)) * 32));
                    bullets.add(new Bullet(bullet_pos, rotation_t, 0));
                    lastShot = System.currentTimeMillis();
                }
            }

            if (speed > 100) {
                speed = 100;
            } else if (speed < -10) {
                speed = -10;
            }

            position.translate((float) (Math.cos(Math.toRadians(rotation_h + 90)) * speed), (float) (Math.sin(Math.toRadians(rotation_h + 90)) * speed));

            if (!freezeMouseAim) {
                /*float mouseY = App.ORTHO_SIZE.getHeight() - Mouse.getY() / 720f * App.ORTHO_SIZE.getHeight();
                float mouseX = Mouse.getX() / 1280f * App.ORTHO_SIZE.getWidth();*/
                
                float mouseY = 720f - Mouse.getY();
                float mouseX = Mouse.getX();
                //System.out.println(mouseX + "x" + mouseY );
                System.out.println(position.x + "x" + position.y);
                rotation_t = (float) Math.toDegrees(Math.atan2(mouseY - position.y, mouseX - position.x)) - 90;
            } else {
                rotation_t = rotation_h;
            }
            speed *= 0.01;

        }

        private void renderTank() {
            renderTank(0, 0);
        }

        private void renderTank(int offsetX, int offsetY) {
            // RENDER HULL OF TANK
            glPushMatrix();
            glBindTexture(GL_TEXTURE_2D, tank_h);
            glTranslatef(position.x + offsetX, position.y + offsetY, 0);
            glRotatef(rotation_h, 0f, 0f, 1f);
            glBegin(GL_QUADS);
            {
                glTexCoord2f(0, 0);
                glVertex2i(-13, -22);
                glTexCoord2f(1, 0);
                glVertex2i(13, -22);
                glTexCoord2f(1, 1);
                glVertex2i(13, 22);
                glTexCoord2f(0, 1);
                glVertex2i(-13, 22);
            }
            glEnd();
            glPopMatrix();

            // RENDER TURRET
            glPushMatrix();
            glTranslatef(position.x + offsetX, position.y + offsetY, 0);
            glRotatef(rotation_t, 0f, 0f, 1f);
            glBindTexture(GL_TEXTURE_2D, tank_t);
            glBegin(GL_QUADS);
            {
                glTexCoord2f(0, 1);
                glVertex2i(-12, -33);
                glTexCoord2f(1, 1);
                glVertex2i(12, -33);
                glTexCoord2f(1, 0);
                glVertex2i(12, 33);
                glTexCoord2f(0, 0);
                glVertex2i(-12, 33);
            }
            glEnd();
            glPopMatrix();
        }
    }

    private class Bullet {

        float BULLET_SPEED = 8f;
        public long BULLET_LIFETIME = 3 * 1000;
        Vector2f position;
        Vector2f velocity;
        long shotTime;
        int player_id;

        public Bullet(Vector2f position, float angle, int player_id) {
            this.player_id = player_id;
            this.position = position;
            this.velocity = new Vector2f((float) (Math.cos(Math.toRadians(angle + 90)) * BULLET_SPEED), (float) (Math.sin(Math.toRadians(angle + 90)) * BULLET_SPEED));
            shotTime = System.currentTimeMillis();
        }

        public void update() {
            position.translate(velocity.x, velocity.y);
        }

        public Vector2f getPosition() {
            return position;
        }

        public int getPlayerID() {
            return player_id;
        }

        public long getShotTime() {
            return shotTime;
        }
    }
}
