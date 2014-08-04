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
import deamont66.tanks.core.gui.*;
import deamont66.tanks.core.model.NPCPlayer;
import deamont66.tanks.core.model.WorldEntity;
import deamont66.tanks.core.model.Player;
import deamont66.tanks.core.model.effects.Animator;
import deamont66.tanks.core.model.improvements.ImprovementManager;
import deamont66.tanks.utils.PlayerListener;
import deamont66.tanks.utils.GameStateData;
import static deamont66.util.PhysicsUtils.*;
import deamont66.util.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.contacts.Contact;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author JiriSimecek
 */
public class LocalGameState extends AbstractGameState {

    private DebugStats fps;
    private GamePause pause;
    private GameOver over;
    private WorldEntity mapa;
    private String mapFile = "/default/default.tmap";

    private final Box2DWorld world = new Box2DWorld(new Vec2(0, 0));

    private boolean mouseGrabed = true;
    private int cursorIcon = -1;

    private int limitOfFrags = 0;

    private final PlayerListener playerListener = new PlayerListener() {

        @Override
        public void fired(Vector2f source, Vector2f destination, float direction, Player owner) {
        }

        @Override
        public void destroyed(int by, Player who) {
            players[by].addScore(1);
        }

        @Override
        public void improvementTaken(int improvementID, Player bywho) {
        }
    };

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
        over = new GameOver(this);
        over.setSize(mainComponent.getSize());
        over.setVisible(false);
        add(over);

        pause = new GamePause(this);
        pause.setSize(mainComponent.getSize());
        pause.setVisible(false);
        add(pause);

        fps = new DebugStats();
        fps.setVisible(false);
        add(fps);
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
                    if ((b = findBody(contact, "tank", "map-shallowwater")) != null) {
                        ((UserData) b.getUserData()).bool = true;
                    }
                }
                if ((b = findBody(contact, "map-brick", "bullet")) != null || (b = findBody(contact, "map-tree", "bullet")) != null || (b = findBody(contact, "map-tree", "tank")) != null) {
                    ((UserData) b.getUserData()).bool = true;
                }

                if (findBody(contact, "bullet", "bullet") != null) {
                    world.removeBody(contact.m_fixtureB.getBody());
                    world.removeBody(contact.m_fixtureA.getBody());
                }

                if ((b = findBody(contact, "tank", "sp-wrench")) != null) {
                    UserData wrench = ((UserData) findBody(contact, "sp-wrench", "tank").getUserData());
                    ((UserData) b.getUserData()).number2 = -1;
                    wrench.bool = true;
                    System.out.println("wrench");
                }

                if ((b = findBody(contact, "tank", "sp-shield")) != null) {
                    UserData shield = ((UserData) findBody(contact, "sp-shield", "tank").getUserData());
                    ((UserData) b.getUserData()).number2 = -2;
                    shield.bool = true;
                }

                if ((b = findBody(contact, "tank", "sp-boost")) != null) {
                    UserData boost = ((UserData) findBody(contact, "sp-boost", "tank").getUserData());
                    ((UserData) b.getUserData()).number2 = -3;
                    boost.bool = true;
                }
                //System.out.println("Contact: " + ((UserData) contact.getFixtureA().getBody().getUserData()).name + " - " + ((UserData) contact.getFixtureB().getBody().getUserData()).name);
            }

            @Override
            public void endContact(Contact contact) {
                Body b;
                if ((b = findBody(contact, "tank", "map-shallowwater")) != null || (b = findBody(contact, "tank", "map-destroyedtree")) != null) {
                    ((UserData) b.getUserData()).number--;
                    if (((UserData) b.getUserData()).number == 0 && (b = findBody(contact, "tank", "map-shallowwater")) != null) {
                        ((UserData) b.getUserData()).bool = false;
                    }
                }
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
                Body b;
                if ((b = findBody(contact, "bullet", "map-deepwater")) != null) {
                    contact.setEnabled(false);
                }
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
                // tanks vs bullet
                Body b;
                if ((b = findBody(contact, "bullet", "tank")) != null) {
                    UserData bulletData = (UserData) b.getUserData();
                    UserData tankData = (UserData) findBody(contact, "tank", "bullet").getUserData();
                    System.out.println(impulse.normalImpulses[0]);
                    if (impulse.normalImpulses[0] < 0.23f) {
                        tankData.number2 = 25;
                    } else {
                        tankData.number2 = 34;
                    }
                    tankData.number = bulletData.number;
                    world.removeBody(b);
                    // ((UserData) b.getUserData()).bool = true;
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
        groundDef.position.set(pxToWorld(App.ORTHO_SIZE.getWidth()) / 2, pxToWorld(App.ORTHO_SIZE.getHeight()) + pxToWorld(10));
        groundDef.type = BodyType.STATIC;
        PolygonShape groundShape = new PolygonShape();
        groundShape.setAsBox(pxToWorld(App.ORTHO_SIZE.getWidth()) / 2, pxToWorld(10));
        Body ground = world.getWorld().createBody(groundDef);
        ground.setUserData(new UserData("edge"));
        ground.createFixture(groundShape, 0f);

        // top
        groundDef = new BodyDef();
        groundDef.position.set(pxToWorld(App.ORTHO_SIZE.getWidth()) / 2, -pxToWorld(10));
        groundDef.type = BodyType.STATIC;
        groundShape = new PolygonShape();
        groundShape.setAsBox(pxToWorld(App.ORTHO_SIZE.getWidth()) / 2, pxToWorld(10));
        ground = world.getWorld().createBody(groundDef);
        ground.setUserData(new UserData("edge"));
        ground.createFixture(groundShape, 0f);

        // left
        groundDef = new BodyDef();
        groundDef.position.set(-pxToWorld(10), pxToWorld(App.ORTHO_SIZE.getHeight()) / 2);
        groundDef.type = BodyType.STATIC;
        groundShape = new PolygonShape();
        groundShape.setAsBox(pxToWorld(10), pxToWorld(App.ORTHO_SIZE.getHeight()) / 2);
        ground = world.getWorld().createBody(groundDef);
        ground.setUserData(new UserData("edge"));
        ground.createFixture(groundShape, 0f);

        // right
        groundDef = new BodyDef();
        groundDef.position.set(pxToWorld(App.ORTHO_SIZE.getWidth()) + pxToWorld(10), pxToWorld(App.ORTHO_SIZE.getHeight()) / 2);
        groundDef.type = BodyType.STATIC;
        groundShape = new PolygonShape();
        groundShape.setAsBox(pxToWorld(10), pxToWorld(App.ORTHO_SIZE.getHeight()) / 2);
        ground = world.getWorld().createBody(groundDef);
        ground.setUserData(new UserData("edge"));
        ground.createFixture(groundShape, 0f);
        // </editor-fold>
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
        mapa = new WorldEntity(world);

        mapa.setMapFile(mapFile);
        mapa.preloadMap();

        ImprovementManager imp = new ImprovementManager(world);
        mapa.add(imp);

        for (int i = 0; i < players.length; i++) {
            players[i].setMap(mapa);
            players[i].id = i;
            players[i].setPlayerListener(playerListener);
            mapa.add(players[i]);
        }

        players[0].setPosition(mapa.getRandomSpawnPlace());
        for (int i = 1; i < players.length; i++) {
            players[i].setPosition(mapa.getRandomSpawnPlace());

        }
    }

    @Override
    protected void preLoadEnd() {
        fps.setVisible(true);
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

        // screenshot
        if (in.isKeyPressed(InputHandler.KEY_F2)) {
            LWJGLUtils.saveScreenshot(new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date()) + " " + (int) (Math.random() * 99));
        }
        // fullscreen
        if (in.isKeyReleased(InputHandler.KEY_F11)) {
            StateManager.setFullscreen(!StateManager.isFullscreen());
            try {
                StateManager.setDisplayMode();
            } catch (LWJGLException ex) {
                Utils.error(ex);
            }
        }
        // pause
        if (in.isKeyReleased(InputHandler.KEY_ESCAPE) && !over.isVisible()) {
            mouseGrabed = !mouseGrabed;
            if (mouseGrabed == false) {
                Mouse.setCursorPosition(Mouse.getX(), Mouse.getY());
            }
            Mouse.setGrabbed(mouseGrabed);
            pause.setVisible(!mouseGrabed);
        }
        // if paused we stop here
        if (pause.isVisible() || over.isVisible()) {
            return;
        }
        // score check
        if (limitOfFrags > 0) {
            for (Player player : players) {
                if (player.getScore() >= limitOfFrags) {
                    win(player);
                }
            }
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
        limitOfFrags = data.integer;
        if (!data.text2.isEmpty()) {
            mapFile = data.text2;
        }
        String[] names = (String[]) data.object;

        players = new Player[names.length];
        if (names.length == 1) {
            int npcCount = 1;
            players = new Player[names.length + npcCount];
            for (int i = 1; i < players.length; i++) {
                players[i] = new NPCPlayer("NPC Player", world);
            }
        }
        for (int i = 0; i < names.length; i++) {
            players[i] = new Player((names.length == 1) ? 0 : (i + 1), world);
            players[i].setName(names[i]);
        }
    }

    private void win(Player player) {
        over.setWinner(player);
        over.setVisible(true);
        mouseGrabed = false;
        Mouse.setCursorPosition(Mouse.getX(), Mouse.getY());
        Mouse.setGrabbed(mouseGrabed);
    }

}
