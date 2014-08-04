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
import deamont66.util.NativesLoader;
import deamont66.util.Utils;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Color;

/**
 *
 * @author JiriSimecek
 */
public class PhysicsTest {

    private final int width = 1280;
    private final int height = 720;

    private final World world = new World(new Vec2(0, 0 /*9.8f*/));

    public static void main(String[] args) {
        NativesLoader.loadNatives();
        new PhysicsTest().start();
    }

    private void start() {
        try {
            LWJGLUtils.setDisplayMode(width, height, false);
            Display.create();
            Display.setVSyncEnabled(true);

            glEnable(GL_TEXTURE_2D);
            glEnable(GL_BLEND);
            glPointSize(6);
            glEnable(GL_POINT_SMOOTH);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            LWJGLUtils.setModelOrtho(width, height);

            FPSCounter.init(60);

            init();
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
        LWJGLUtils.update(60);
        InputHandler in = InputHandler.getInputHandler();
        world.step(1 / 60f, 8, 3);

        if (in.isKeyDown(InputHandler.KEY_W) && !in.isKeyDown(InputHandler.KEY_S)) {
            float rotation = tank.getAngle();
            Vec2 force = new Vec2((float) (Math.cos(rotation)), (float) (Math.sin(rotation)));
            tank.setLinearVelocity(force.mul(5f));
        } else if (in.isKeyDown(InputHandler.KEY_S) && !in.isKeyDown(InputHandler.KEY_W)) {
            float rotation = tank.getAngle();
            Vec2 force = new Vec2((float) (Math.cos(rotation)), (float) (Math.sin(rotation)));
            tank.setLinearVelocity(force.mul(5f).negate());
        } else {
            tank.setLinearVelocity(new Vec2());
        }

        if (in.isKeyDown(InputHandler.KEY_A) && !in.isKeyDown(InputHandler.KEY_D)) {
            tank.setAngularVelocity(-2f);
        } else if (in.isKeyDown(InputHandler.KEY_D) && !in.isKeyDown(InputHandler.KEY_A)) {
            tank.setAngularVelocity(2f);
        } else {
            tank.setAngularVelocity(0f);
        }
    }

    private void render() {
        LWJGLUtils.clean(new Color(0, 0, 0));

        if (tank.isActive()) {
            glColor3f(1f, 0, 0);
            glPushMatrix();
            Vec2 bodyPosition = worldToPx(tank.getPosition());
            glTranslatef(bodyPosition.x, bodyPosition.y, 0);
            glRotated(Math.toDegrees(tank.getAngle()), 0, 0, 1);
            glRectf(-0.75f * 30, -0.5f * 30, 0.75f * 30, 0.5f * 30);
            glPopMatrix();
        }
    }

    Body tank;

    private void init() {
        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                System.out.println("begin con");

                if (contact.getFixtureB().getBody().getUserData().equals("tank")) {
                    Body tank = contact.getFixtureB().getBody();
                    tank.setActive(false);
                }
            }

            @Override
            public void endContact(Contact contact) {
                System.out.println("end con");
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                //System.out.println("preSolve");
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
                //System.out.println("postSolve");
            }
        });

        // ground
        // bottom
        BodyDef groundDef = new BodyDef();
        groundDef.position.set(pxToWorld(width) / 2, pxToWorld(height));
        groundDef.type = BodyType.STATIC;
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(pxToWorld(width) / 2, 0);
        Body ground = world.createBody(groundDef);
        FixtureDef groundFixture = new FixtureDef();
        // groundFixture.density = 1;
        groundFixture.restitution = 0.1f;
        groundFixture.shape = groundShape;
        ground.createFixture(groundFixture);

        // top
        groundDef = new BodyDef();
        groundDef.position.set(pxToWorld(width) / 2, 0);
        groundDef.type = BodyType.STATIC;
        groundShape = new PolygonShape();
        groundShape.setAsBox(pxToWorld(width) / 2, 0);
        ground = world.createBody(groundDef);
        ground.createFixture(groundShape, 0f);

        // left
        groundDef = new BodyDef();
        groundDef.position.set(0, pxToWorld(height) / 2);
        groundDef.type = BodyType.STATIC;
        groundShape = new PolygonShape();
        groundShape.setAsBox(0, pxToWorld(height) / 2);
        ground = world.createBody(groundDef);
        groundFixture = new FixtureDef();
        // groundFixture.density = 1;
        groundFixture.restitution = 0.1f;
        groundFixture.shape = groundShape;
        ground.createFixture(groundFixture);

        // right
        groundDef = new BodyDef();
        groundDef.position.set(pxToWorld(width), pxToWorld(height) / 2);
        groundDef.type = BodyType.STATIC;
        groundShape = new PolygonShape();
        groundShape.setAsBox(0, pxToWorld(height) / 2);
        ground = world.createBody(groundDef);
        groundFixture = new FixtureDef();
        groundFixture.density = 0;
        groundFixture.restitution = 0f;
        groundFixture.friction = 0;
        groundFixture.shape = groundShape;
        ground.createFixture(groundFixture);

        // Dynamic Body
        BodyDef boxDef = new BodyDef();
        boxDef.position.set(pxToWorld(width) / 2, pxToWorld(height) / 2);
        boxDef.type = BodyType.DYNAMIC;
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(0.75f, 0.5f);
        tank = world.createBody(boxDef);
        FixtureDef boxFixture = new FixtureDef();
        boxFixture.density = 0.1f;
        boxFixture.shape = boxShape;
        tank.setFixedRotation(true);
        tank.setUserData("tank");
        tank.createFixture(boxFixture);
    }

    private float pxToWorld(float px) {
        return px / 30f;
    }

    private Vec2 pxToWorld(Vec2 px) {
        return px.mul(1 / 30f);
    }

    private float worldToPx(float px) {
        return px * 30f;
    }

    private Vec2 worldToPx(Vec2 px) {
        return px.mul(30f);
    }
}
