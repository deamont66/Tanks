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
package deamont66.tanks.core.model;

import deamont66.tanks.core.App;
import deamont66.tanks.core.gui.Label;
import deamont66.tanks.core.model.effects.Animator;
import deamont66.tanks.core.model.effects.TankExplosion;
import deamont66.tanks.core.model.effects.TankSmokePack;
import deamont66.tanks.utils.PlayerListener;
import deamont66.tanks.utils.UserData;
import deamont66.util.Box2DWorld;
import deamont66.util.FPSCounter;
import deamont66.util.InputHandler;
import deamont66.util.LWJGLUtils;
import deamont66.util.ResourceLoader;
import deamont66.util.TrueTypeFont;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author JiriSimecek
 */
public class Player extends Entity {

    private int score;
    protected int health = 100;
    private String name = "";
    protected Tank tank;
    private TrueTypeFont ttf = null;
    private PlayerListener playerListener = null;
    protected boolean exploded = false;
    protected boolean isPlayer = false;
    protected final TankSmokePack smoke = new TankSmokePack(world, 5, 10, 10);
    private Label respawn = null;
    protected WorldEntity map;
    public int id;

    // pouze pro multiplayer and remotePlayers
    private int ping = 0;

    /**
     *
     * @param playerNumber {@link Tank#setPlayerNumber}
     * @param world
     */
    public Player(int playerNumber, Box2DWorld world) {
        super(world);
        tank = new Tank(world, this);
        if (playerNumber == 0) {
            isPlayer = true;
        }
        tank.setPlayerNumber(playerNumber);
    }

    @Override
    protected void initEntity() {
        ttf = ResourceLoader.loadFontFromFile("/fonts/CollegiateInsideFLF.ttf", 32f);
        respawn = new Label("YOU ARE DEAD\nPress ENTER to respawn.");
        respawn.setPosition(new Vector2f(0, App.ORTHO_SIZE.getHeight() / 4));
        respawn.setFormat(TrueTypeFont.ALIGN_CENTER);
        //respawn.setCenter(null);
    }

    @Override
    protected void renderEntity(boolean useTexture) {
        // nick
        glColor4f(0f, 0f, 0f, 0.6f);
        LWJGLUtils.drawString(ttf, tank.getX() + 2, tank.getY() - 40 + 2, name, 0.5f, 0.5f, TrueTypeFont.ALIGN_CENTER);
        glColor4f(1f, 1f, 1f, 1f);
        LWJGLUtils.drawString(ttf, tank.getX(), tank.getY() - 40, name, 0.5f, 0.5f, TrueTypeFont.ALIGN_CENTER);
        // health
        glColor4f(0, 0, 0, 0.6f);
        LWJGLUtils.renderQuad(tank.getX() - 50, tank.getY() - 44, 102, 7);
        // green
        glColor4f(0, 1, 0, 1);
        LWJGLUtils.renderQuad(tank.getX() - 50, tank.getY() - 44, health, 5);
        // red
        glColor4f(1, 0, 0, 1);
        LWJGLUtils.renderQuad(tank.getX() - 50 + health, tank.getY() - 44, 100 - health, 5);
        // blue
        if (tank.isShield()) {
            glColor4f(0, 0, 1, 0.5f);
            LWJGLUtils.renderQuad(tank.getX() - 50, tank.getY() - 44, 100, 5);
        }

        tank.render();
        if (exploded && isPlayer) {
            glTranslated(App.ORTHO_SIZE.getWidth() / 2, 0, 0);
            respawn.paint();
            glTranslated(App.ORTHO_SIZE.getWidth() / -2, 0, 0);
        }
    }

    @Override
    protected void updateEntity(double tfp) {
        InputHandler in = InputHandler.getInputHandler();
        // ------------------- CHEATS ----------------------
        /*if (in.isKeyReleased(InputHandler.KEY_1)) {
         resetHealth();
         }
         if (isPlayer && in.isKeyReleased(InputHandler.KEY_2)) {
         takeDamage(10, -1);
         }*/
        // ------------------- CHEATS-----------------------
        if (exploded && in.isKeyPressed(InputHandler.KEY_RETURN)) {
            if (map != null) {
                tank.setPosition(map.getRandomSpawnPlace());
            }
            resetHealth();
        } else if (exploded && tank.body != null) {
            ((UserData) tank.body.getUserData()).number2 = 0;
            ((UserData) tank.body.getUserData()).number3 = 0;
        }

        tank.setStop(health == 0);
        tank.update(tfp);

        smoke.setPosition(tank.getX(), tank.getY());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMap(WorldEntity map) {
        this.map = map;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public void setPing(int ping) {
        this.ping = ping;
    }

    public int getPing() {
        return ping;
    }

    @Override
    public void setPosition(Vector2f position) {
        tank.setPosition(position);
    }

    @Override
    public float getX() {
        return tank.getX();
    }

    @Override
    public float getY() {
        return tank.getY();
    }

    @Override
    public void setX(float x) {
        tank.setX(x);
    }

    @Override
    public void setY(float y) {
        tank.setY(y);
    }

    @Override
    public void setRotation(float rotation) {
        tank.setRotation(rotation);
    }

    @Override
    public float getRotation() {
        return tank.getRotation();
    }

    @Override
    public void setPosition(float x, float y) {
        tank.setPosition(x, y);
    }

    public boolean isShielded() {
        return tank.isShield();
    }

    public void setSheild(boolean shield) {
        tank.lastImprovement = FPSCounter.getTime();
        tank.shield = shield;
    }

    public void takeDamage(int damage, int shooterID) {
        health -= damage;
        if (health < 0) {
            health = 0;
        }
        if (health == 0 && exploded == false) {
            destroyed(shooterID);
        }
    }

    protected void destroyed(int shooterID) {
        TankExplosion ex = new TankExplosion(world);
        ex.setPosition(tank.getX(), tank.getY());
        smoke.show();
        Animator.addOnTop(ex);
        exploded = true;
        if (playerListener != null) {
            playerListener.destroyed(shooterID, this);
        }
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (health > 0) {
            exploded = false;
            smoke.end();
        } else if (health == 0) {
            TankExplosion ex = new TankExplosion(world);
            ex.setPosition(tank.getX(), tank.getY());
            smoke.show();
            Animator.addOnTop(ex);
            exploded = true;
        }
        this.health = health;
    }

    public void resetHealth() {
        health = 100;
        exploded = false;
        smoke.end();
    }

    @Override
    public Vector2f getPosition() {
        return tank.getPosition();
    }

    public void setTurretRotation(float rotation_t) {
        tank.setTurretRotation(rotation_t);
    }

    public float getTurretRotation() {
        return tank.getTurretRotation();
    }

    public void setPlayerListener(PlayerListener playerListener) {
        this.playerListener = playerListener;
    }

    public void fire(Vector2f source, Vector2f dest, float rotation) {
        if (playerListener != null) {
            playerListener.fired(source, dest, rotation, this);
        }
    }

    public void improvementTaken(int improvementID) {
        if (playerListener != null) {
            playerListener.improvementTaken(improvementID, this);
        }
    }
}
