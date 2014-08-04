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

import deamont66.tanks.core.model.effects.Animator;
import deamont66.tanks.core.model.effects.Bullet;
import deamont66.tanks.utils.UserData;
import deamont66.util.Box2DWorld;
import deamont66.util.FPSCounter;
import deamont66.util.InputHandler;
import static deamont66.util.PhysicsUtils.*;
import deamont66.util.ResourceLoader;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.lwjgl.input.Mouse;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Jirka
 */
public class Tank extends Entity {

    protected static final int BULLET_RELOAD_TIME = 500;
    protected int playerNum = 0;
    protected float t_rotation = 0;
    protected long lastBullet = 0L;
    protected final Player player;
    protected boolean stop = false;

    protected static final int IMPROVEMENT_TIME = 4000;
    protected long lastImprovement = 0;
    protected boolean boost, shield;

    public Tank(Box2DWorld world, Player player) {
        super(world);
        // setPosition(200, 250);
        setSize(36, 54);
        this.player = player;
    }

    @Override
    protected void initEntity() {
        setTexture(ResourceLoader.glLoadPNG("/tanks/Tiger/Tiger_full_s.png"));

        BodyDef boxDef = new BodyDef();
        boxDef.position.set(pxToWorld(position.x), pxToWorld(position.y));
        boxDef.type = BodyType.DYNAMIC;
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(pxToWorld(18f), pxToWorld(27f));
        body = world.getWorld().createBody(boxDef);
        FixtureDef boxFixture = new FixtureDef();
        boxFixture.density = 0.1f;
        boxFixture.shape = boxShape;
        body.setFixedRotation(false);
        body.setUserData(new UserData("tank"));
        body.createFixture(boxFixture);
    }

    @Override
    protected void renderEntity(boolean useTexture) {
        int shadowOffsetX = 4, shadowOffsetY = 4;

        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, getTexture());
        // SHADOW OF TANK
        if (((UserData) body.getUserData()).number > 1 && ((UserData) body.getUserData()).bool) {
            shadowOffsetX = shadowOffsetY = 0;
        } else if (((UserData) body.getUserData()).number > 0) {
            shadowOffsetX = shadowOffsetY = 2;
        }
        glColor4f(0f, 0f, 0f, 0.6f);
        renderTank(shadowOffsetX, shadowOffsetY, true);

        // TANK ITSELF
        glColor4f(1f, 1f, 1f, 1f);
        renderTank(0, 0);
        glDisable(GL_TEXTURE_2D);
    }

    @Override
    protected void updateEntity(double tfp) {
        position = new Vector2f(worldToPx(body.getPosition()).x, worldToPx(body.getPosition()).y);
        rotation = (float) Math.toDegrees(body.getAngle());
        ((UserData) body.getUserData()).numberFloat = rotation;

        if (stop) {
            body.setAngularVelocity(0f);
            body.setLinearVelocity(new Vec2());
            return;
        }

        if (((UserData) body.getUserData()).number2 != 0) {
            if (((UserData) body.getUserData()).number2 < 0) {
                if (((UserData) body.getUserData()).number2 == -1) { // repair
                    player.resetHealth();
                } else if (((UserData) body.getUserData()).number2 == -2) {  // sheild
                    shield = true;
                } else if (((UserData) body.getUserData()).number2 == -3) {  // boost
                    boost = true;
                }
                lastImprovement = FPSCounter.getTime();
                if (player.isPlayer) {
                    player.improvementTaken((int) Math.abs(((UserData) body.getUserData()).number2));
                }
            } else {
                if (!shield) {
                    player.takeDamage(((UserData) body.getUserData()).number2, ((UserData) body.getUserData()).number3);
                }
            }
            ((UserData) body.getUserData()).number2 = 0;
            ((UserData) body.getUserData()).number3 = 0;
        }

        if (FPSCounter.getTime() - lastImprovement > IMPROVEMENT_TIME && (shield || boost)) {
            shield = boost = false;
        }

        InputHandler in = InputHandler.getInputHandler();
        boolean up, down, left, right, fire;
        if (playerNum == 1) {
            up = (in.isKeyDown(InputHandler.KEY_W) && !in.isKeyDown(InputHandler.KEY_S));
            down = (in.isKeyDown(InputHandler.KEY_S) && !in.isKeyDown(InputHandler.KEY_W));
            left = (in.isKeyDown(InputHandler.KEY_A) && !in.isKeyDown(InputHandler.KEY_D));
            right = (in.isKeyDown(InputHandler.KEY_D) && !in.isKeyDown(InputHandler.KEY_A));
            fire = (in.isKeyDown(InputHandler.KEY_SPACE));
        } else if (playerNum == 2) {
            up = (in.isKeyDown(InputHandler.KEY_UP) && !in.isKeyDown(InputHandler.KEY_DOWN));
            down = (in.isKeyDown(InputHandler.KEY_DOWN) && !in.isKeyDown(InputHandler.KEY_UP));
            left = (in.isKeyDown(InputHandler.KEY_LEFT) && !in.isKeyDown(InputHandler.KEY_RIGHT));
            right = (in.isKeyDown(InputHandler.KEY_RIGHT) && !in.isKeyDown(InputHandler.KEY_LEFT));
            fire = (in.isKeyDown(InputHandler.KEY_RCONTROL));
        } else if (playerNum == 0) {
            up = (in.isKeyDown(InputHandler.KEY_W) && !in.isKeyDown(InputHandler.KEY_S)) || (in.isKeyDown(InputHandler.KEY_UP) && !in.isKeyDown(InputHandler.KEY_DOWN));
            down = (in.isKeyDown(InputHandler.KEY_S) && !in.isKeyDown(InputHandler.KEY_W)) || (in.isKeyDown(InputHandler.KEY_DOWN) && !in.isKeyDown(InputHandler.KEY_UP));
            left = (in.isKeyDown(InputHandler.KEY_A) && !in.isKeyDown(InputHandler.KEY_D)) || (in.isKeyDown(InputHandler.KEY_LEFT) && !in.isKeyDown(InputHandler.KEY_RIGHT));
            right = (in.isKeyDown(InputHandler.KEY_D) && !in.isKeyDown(InputHandler.KEY_A)) || (in.isKeyDown(InputHandler.KEY_RIGHT) && !in.isKeyDown(InputHandler.KEY_LEFT));
            fire = (in.isMouseButtonDown(0));
        } else {
            up = false;
            down = false;
            left = false;
            right = false;
            fire = false;
        }
        if (!Mouse.isGrabbed()) {
            fire = false;
        }

        // FIRE
        if (fire && FPSCounter.getTime() - lastBullet > BULLET_RELOAD_TIME) {
            Vector2f bulletPos = new Vector2f(position);
            bulletPos.translate((float) (Math.cos(Math.toRadians(t_rotation + 90)) * 50), (float) (Math.sin(Math.toRadians(t_rotation + 90)) * 50));
            Vector2f destPos = new Vector2f(in.getMouseX(), in.getMouseY());
            if (WorldEntity.distanceBetweenPoints(position, bulletPos) > WorldEntity.distanceBetweenPoints(position, destPos) || playerNum > 0) {
                destPos = new Vector2f((float) (Math.cos(Math.toRadians(t_rotation + 90)) * 2000), (float) (Math.sin(Math.toRadians(t_rotation + 90)) * 2000));
            }
            Bullet bullet = new Bullet(world, bulletPos, destPos, 12f, player);
            bullet.setRotation(t_rotation);
            Animator.addOnTop(bullet);
            lastBullet = FPSCounter.getTime();
            player.fire(bulletPos, destPos, t_rotation);
        }
        if (left) {
            body.setAngularVelocity((float) (-2f * tfp));
        } else if (right) {
            body.setAngularVelocity((float) (2f * tfp));
        } else {
            body.setAngularVelocity(0f);
        }

        float forceSize = (((UserData) body.getUserData()).number > 0) ? 2f : 4f;
        if (boost) {
            forceSize += 2;
        }
        if (up) {
            Vec2 force = new Vec2((float) (Math.cos(body.getAngle() + Math.PI / 2)), (float) (Math.sin(body.getAngle() + Math.PI / 2)));
            body.setLinearVelocity(force.mul(forceSize).mul((float) tfp));
        } else if (down) {
            Vec2 force = new Vec2((float) (Math.cos(body.getAngle() + Math.PI / 2)), (float) (Math.sin(body.getAngle() + Math.PI / 2)));
            body.setLinearVelocity(force.mul(forceSize).mul((float) tfp).negate());
        } else {
            body.setLinearVelocity(new Vec2());
        }

        if (playerNum == 0) {
            t_rotation = (float) Math.toDegrees(Math.atan2(in.getMouseY() - position.y, in.getMouseX() - position.x)) - 90;
        } else if (playerNum != 3) {
            t_rotation = getRotation();
        }
    }

    private void renderTank(int offsetX, int offsetY) {
        renderTank(offsetX, offsetY, false);
    }

    private void renderTank(int offsetX, int offsetY, boolean shadow) {
        // RENDER HULL OF TANK
        if (!shadow) {
            if (((UserData) body.getUserData()).bool && ((UserData) body.getUserData()).number > 1) {
                glColor4f(0.7f, 0.85f, 1f, 1f);
            }
        }
        glPushMatrix();
        glTranslatef(position.x + offsetX, position.y + offsetY, 0);
        glRotatef(rotation, 0f, 0f, 1f);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 0);
            glVertex2i(-18, -27);
            glTexCoord2f(0.52f, 0);
            glVertex2i(18, -27);
            glTexCoord2f(0.52f, 2 / 3f);
            glVertex2i(18, 27);
            glTexCoord2f(0, 2 / 3f);
            glVertex2i(-18, 27);
        }
        glEnd();
        glPopMatrix();

        // RENDER TURRET
        if (!shadow) {
            if (((UserData) body.getUserData()).bool && ((UserData) body.getUserData()).number > 1) {
                glColor4f(1f, 1f, 1f, 1f);
            }
        }
        glPushMatrix();
        glTranslatef(position.x + offsetX, position.y + offsetY, 0);
        glRotatef(t_rotation, 0f, 0f, 1f);
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0.52f, 1);
            glVertex2i(-17, -38);
            glTexCoord2f(1, 1);
            glVertex2i(17, -38);
            glTexCoord2f(1, 0);
            glVertex2i(17, 38);
            glTexCoord2f(0.52f, 0);
            glVertex2i(-17, 38);
        }
        glEnd();
        glPopMatrix();
    }

    /**
     * Sets player number for control and stuff around.
     *
     * <pre>{@code
     * 0 ... only one player on local computer (WASD and arrow keys)
     * 1 ... two players on local computer (WASD)
     * 2 ... two players on local computer (Arrow keys)
     * 3 ... more then two players, player cannot be controled by local machine
     * }</pre>
     *
     * @param n player number
     */
    public void setPlayerNumber(int n) {
        if (n >= 0 && n < 3) {
            playerNum = n;
        } else {
            playerNum = 3;
            // throw new IllegalArgumentException("Illegal argument " + n);
        }
    }

    public void setTurretRotation(float t_rotation) {
        this.t_rotation = t_rotation;
    }

    public float getTurretRotation() {
        return this.t_rotation;
    }

    @Override
    public Vector2f getPosition() {
        return new Vector2f(worldToPx(body.getPosition()).x, worldToPx(body.getPosition()).y);
    }

    @Override
    public void setPosition(Vector2f position) {
        setPosition(position.x, position.y);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        transforBody();
    }

    private synchronized void transforBody() {
        if (body != null) {
            ((UserData) body.getUserData()).number = 0;
            ((UserData) body.getUserData()).bool = false;
            body.setTransform(pxToWorld(new Vec2(position.x, position.y)), (float) Math.toRadians(rotation));
        }
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public boolean isBoost() {
        return boost;
    }

    public boolean isShield() {
        return shield;
    }
}
