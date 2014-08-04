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
package deamont66.tanks.core.states;

import deamont66.tanks.utils.Settings;
import deamont66.tanks.utils.UserData;
import deamont66.tanks.core.AbstractGameState;
import deamont66.tanks.core.App;
import deamont66.tanks.core.StateManager;
import deamont66.tanks.core.gui.DebugStats;
import deamont66.tanks.core.gui.GamePause;
import deamont66.tanks.core.model.*;
import deamont66.tanks.core.model.effects.Animator;
import deamont66.tanks.utils.GameStateData;
import deamont66.util.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import static deamont66.util.PhysicsUtils.*;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

/**
 * Old one state no more used but can be usefull in future
 * @author Jirka
 */
public class SingleGameState extends AbstractGameState {

    private DebugStats fps;
    private GamePause pause;
    private WorldEntity mapa;

    private final Box2DWorld world = new Box2DWorld(new Vec2(0, 0));

    private boolean mouseGrabed = true;
    private int cursorIcon = -1;

    @Override
    protected void initDisplay() {
        try {
            StateManager.setDisplayMode();
            Display.setVSyncEnabled(Settings.getInstance().getVSync());
            Display.create();
            Mouse.setGrabbed(mouseGrabed);
        } catch (LWJGLException ex) {
            Utils.error("Cannot create display", ex);
            Display.destroy();
            System.exit(1);
        }
        FPSCounter.init(60);
    }

    @Override
    protected void initOpenGL() {
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glPointSize(6);
        glEnable(GL_POINT_SMOOTH);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        LWJGLUtils.setModelOrtho(App.ORTHO_SIZE.getWidth(), App.ORTHO_SIZE.getHeight());
    }

    @Override
    protected void initTextures() {
        cursorIcon = ResourceLoader.glLoadPNG("/target_point.png");
    }

    @Override
    protected void initFonts() {
    }

    @Override
    protected void initGUI() {
        fps = new DebugStats();
        fps.setVisible(false);
        add(fps);

        pause = new GamePause(this);
        pause.setSize(mainComponent.getSize());
        pause.setVisible(false);
        add(pause);
    }

    @Override
    protected void initModels() {

        // <editor-fold defaultstate="collapsed" desc="collision detection">
        world.setContactListener(new ContactAdapter() {
            @Override
            public void beginContact(Contact contact) {
                Body b;
                if ((b = findBody(contact, "bullet", "block")) != null || (b = findBody(contact, "bullet", "edge")) != null || (b = findBody(contact, "bullet", "map-rock")) != null || (b = findBody(contact, "bullet", "map-brick")) != null || (b = findBody(contact, "bullet", "map-tree")) != null) {
                    world.removeBody(b);

                } else if ((b = findBody(contact, "tank", "map-shallowwater")) != null || (b = findBody(contact, "tank", "map-destroyedtree")) != null) {
                    ((UserData) b.getUserData()).number++;
                }
                if ((b = findBody(contact, "map-brick", "bullet")) != null || (b = findBody(contact, "map-tree", "bullet")) != null || (b = findBody(contact, "map-tree", "tank")) != null) {
                    ((UserData) b.getUserData()).bool = true;
                }
                //System.out.println("Contact: " + ((UserData) contact.getFixtureA().getBody().getUserData()).name + " - " + ((UserData) contact.getFixtureB().getBody().getUserData()).name);
            }

            @Override
            public void endContact(Contact contact) {
                Body b;
                if ((b = findBody(contact, "tank", "map-shallowwater")) != null || (b = findBody(contact, "tank", "map-destroyedtree")) != null) {
                    ((UserData) b.getUserData()).number--;
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                Body b;
                if ((b = findBody(contact, "bullet", "map-deepwater")) != null) {
                    contact.setEnabled(false);
                }
            }

            /**
             * Return body with name toFind if is colliding with body named
             * collidingWith or null if isn't find.
             *
             * @param contact
             * @param toFind
             * @param collidingWith
             * @return
             */
            private Body findBody(Contact contact, String toFind, String collidingWith) {
                if (((UserData) contact.getFixtureA().getBody().getUserData()).name.equals(toFind) && ((UserData) contact.getFixtureB().getBody().getUserData()).name.equals(collidingWith)) {
                    return contact.getFixtureA().getBody();
                }
                if (((UserData) contact.getFixtureA().getBody().getUserData()).name.equals(collidingWith) && ((UserData) contact.getFixtureB().getBody().getUserData()).name.equals(toFind)) {
                    return contact.getFixtureB().getBody();
                }
                return null;
            }

        });
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="world edge declaration">
        // ground
        // bottom
        BodyDef groundDef = new BodyDef();
        groundDef.position.set(pxToWorld(App.ORTHO_SIZE.getWidth()) / 2, pxToWorld(App.ORTHO_SIZE.getHeight()));
        groundDef.type = BodyType.STATIC;
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(pxToWorld(App.ORTHO_SIZE.getWidth()) / 2, 0);
        Body ground = world.getWorld().createBody(groundDef);
        ground.setUserData(new UserData("edge"));
        ground.createFixture(groundShape, 0f);

        // top
        groundDef = new BodyDef();
        groundDef.position.set(pxToWorld(App.ORTHO_SIZE.getWidth()) / 2, 0);
        groundDef.type = BodyType.STATIC;
        groundShape = new PolygonShape();
        groundShape.setAsBox(pxToWorld(App.ORTHO_SIZE.getWidth()) / 2, 0);
        ground = world.getWorld().createBody(groundDef);
        ground.setUserData(new UserData("edge"));
        ground.createFixture(groundShape, 0f);

        // left
        groundDef = new BodyDef();
        groundDef.position.set(0, pxToWorld(App.ORTHO_SIZE.getHeight()) / 2);
        groundDef.type = BodyType.STATIC;
        groundShape = new PolygonShape();
        groundShape.setAsBox(0, pxToWorld(App.ORTHO_SIZE.getHeight()) / 2);
        ground = world.getWorld().createBody(groundDef);
        ground.setUserData(new UserData("edge"));
        ground.createFixture(groundShape, 0f);

        // right
        groundDef = new BodyDef();
        groundDef.position.set(pxToWorld(App.ORTHO_SIZE.getWidth()), pxToWorld(App.ORTHO_SIZE.getHeight()) / 2);
        groundDef.type = BodyType.STATIC;
        groundShape = new PolygonShape();
        groundShape.setAsBox(0, pxToWorld(App.ORTHO_SIZE.getHeight()) / 2);
        ground = world.getWorld().createBody(groundDef);
        ground.setUserData(new UserData("edge"));
        ground.createFixture(groundShape, 0f);
        // </editor-fold>

        mapa = new WorldEntity(world);

        for (Player player : players) {
            mapa.add(player);
        }
        players[0].setPosition(200, 200);
    }

    @Override
    protected void preLoadTextures() {
        ResourceLoader.glLoadPNG("/effects/explosion.png");
        ResourceLoader.glLoadPNG("/effects/fireball.png");
        ResourceLoader.glLoadPNG("/tiles_new.png");
        ResourceLoader.glLoadPNG("/effects/smoke.png");
        ResourceLoader.glLoadPNG("/tanks/Tiger/Tiger_full_s.png");
    }

    @Override
    protected void preLoadFonts() {
    }

    @Override
    protected void preLoadMap() {
    }

    @Override
    protected void render() {
        mapa.render();

        if (mouseGrabed) {
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, cursorIcon);
            LWJGLUtils.renderQuad(-16 + InputHandler.getInputHandler().getMouseX(), -16 + InputHandler.getInputHandler().getMouseY(), 32, 32);
            glDisable(GL_TEXTURE_2D);
        }
    }

    @Override
    protected void update(double tfp) {
        InputHandler in = InputHandler.getInputHandler();
        if (in.isKeyReleased(InputHandler.KEY_F)) {
            StateManager.setFullscreen(!StateManager.isFullscreen());
            try {
                StateManager.setDisplayMode();
            } catch (LWJGLException ex) {
                Utils.error(ex);
            }
        }
        if (in.isKeyReleased(InputHandler.KEY_ESCAPE)) {
            mouseGrabed = !mouseGrabed;
            if (mouseGrabed == false) {
                Mouse.setCursorPosition(Mouse.getX(), Mouse.getY());
            }
            Mouse.setGrabbed(mouseGrabed);
            pause.setVisible(!mouseGrabed);
        }

        // if paused we stop here
        if (pause.isVisible()) {
            return;
        }

        mapa.update(tfp);
        Animator.update(tfp);

        // PHYSICS
        world.update();
    }

    @Override
    protected GameStateData saveData() {
        return null;
    }

    @Override
    protected void restoreData(GameStateData data) {
        String[] names = (String[]) data.object;
        players = new Player[names.length];
        
        for (int i = 0; i < names.length; i++) {
            players[i] = new Player((names.length == 1) ? 0 : (i + 1), world);
            players[i].setName(names[i]);
        }
    }
}
