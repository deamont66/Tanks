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

import deamont66.tanks.core.AbstractGameState;
import deamont66.tanks.core.App;
import deamont66.tanks.core.StateManager;
import deamont66.tanks.core.gui.Button;
import deamont66.tanks.core.gui.Component;
import deamont66.tanks.core.gui.DebugStats;
import deamont66.tanks.core.gui.GameOver;
import deamont66.tanks.core.gui.GamePauseMultiplayer;
import deamont66.tanks.core.gui.Label;
import deamont66.tanks.core.model.Player;
import deamont66.tanks.core.model.WorldEntity;
import deamont66.tanks.core.model.effects.Animator;
import deamont66.tanks.core.model.effects.Bullet;
import deamont66.tanks.core.model.improvements.ImprovementClientManager;
import deamont66.tanks.network.ClientListener;
import deamont66.tanks.network.ClientPlayer;
import deamont66.tanks.network.NetworkClient;
import deamont66.tanks.network.packets.PacketImprovementSpawn;
import deamont66.tanks.network.packets.PacketPlayerDestroyed;
import deamont66.tanks.network.packets.PacketShotFired;
import deamont66.tanks.network.packets.PacketUpdatePlayer;
import deamont66.tanks.utils.GameStateData;
import deamont66.tanks.utils.PlayerListener;
import deamont66.tanks.utils.Settings;
import deamont66.tanks.utils.UserData;
import deamont66.util.Box2DWorld;
import deamont66.util.ContactAdapter;
import deamont66.util.FPSCounter;
import deamont66.util.InputHandler;
import deamont66.util.LWJGLUtils;
import static deamont66.util.PhysicsUtils.pxToWorld;
import deamont66.util.ResourceLoader;
import deamont66.util.TrueTypeFont;
import deamont66.util.Utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
public class NetworkGameState extends AbstractGameState implements ClientListener, PlayerListener {

    private DebugStats fps;
    private NetworkClient client;
    private GameStateData data;
    private GamePauseMultiplayer pause;
    private GameOver over;
    private WorldEntity map;
    private ImprovementClientManager improvements;

    private final Box2DWorld world = new Box2DWorld(new Vec2(0, 0));

    private int cursorIcon = -1;

    private Player localPlayer;

    private final HashMap<Integer, Player> remotePlayers = new HashMap<>();
    private final Map<Integer, ClientPlayer> remotePlayersData = Collections.synchronizedMap(new HashMap<Integer, ClientPlayer>());

    private StatePanel statePanel;

    private boolean mouseGrabed = false;

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

        pause = new GamePauseMultiplayer(this);
        pause.setSize(mainComponent.getSize());
        pause.setVisible(false);
        add(pause);

        fps = new DebugStats(client);
        fps.setVisible(false);
        add(fps);

        statePanel = new StatePanel();
        add(statePanel);
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
                    /*if (impulse.normalImpulses[0] < 0.23f) {
                     tankData.number2 = 25;
                     } else {
                     tankData.number2 = 34;
                     }*/
                    tankData.number2 = 25;
                    tankData.number3 = bulletData.number;
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

        map = new WorldEntity(world);
        localPlayer = new Player(0, world);
        localPlayer.setPlayerListener(this);
        localPlayer.setPosition(-50, -50);
        localPlayer.takeDamage(100, -1);
        map.add(localPlayer);

        improvements = new ImprovementClientManager(world);
        improvements.update(0);
        map.add(improvements);

        client = new NetworkClient(data.text2, data.text1, data.integer);
        client.addListener(this);
        client.connect();

        pause.setNetworkClient(client);
        fps.setClient(client);

        localPlayer.setName(client.getUserName());
    }

    @Override
    protected void preLoadFonts() {
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
    protected void preLoadMap() {
    }

    @Override
    protected void preLoadEnd() {
        fps.setVisible(true);
    }

    @Override
    protected void render() {
        if (!statePanel.isVisible()) {
            map.render();
        }

        if (mouseGrabed) {
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, cursorIcon);
            LWJGLUtils.renderQuad(-16 + InputHandler.getInputHandler().getMouseX(), -16 + InputHandler.getInputHandler().getMouseY(), 32, 32);
            glDisable(GL_TEXTURE_2D);
        }
    }

    @Override
    protected void update(double tfp) {
        client.update(tfp);
        if (statePanel.isText("Connected")) {
            statePanel.setVisible(false);
        } else if (statePanel.isVisible()) {
            Mouse.setGrabbed(false);
            return;
        }

        for (Map.Entry<Integer, Player> entry : remotePlayers.entrySet()) {
            int id = entry.getKey();
            Player player = entry.getValue();
            ClientPlayer playerData = remotePlayersData.get(id);
            player.setRotation(playerData.rotation_h);
            player.setTurretRotation(playerData.rotation_t);
            player.setPosition(playerData.x, playerData.y);
            if (player.getHealth() != playerData.health) {
                player.setHealth(playerData.health);
            }
            player.setSheild(playerData.shield);
            player.setScore(playerData.score);
            player.setPing(playerData.ping);
            playerData.lastUpdate = FPSCounter.getTime();
        }

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

        // if paused we skip this
        //if (!pause.isVisible() && !over.isVisible()) {
        map.update(tfp);
        //}
        Animator.update(tfp);
        localPlayer.setMap(map);
        localPlayer.id = client.getID();
        // PHYSICS
        world.update();
    }

    @Override
    protected GameStateData saveData() {
        System.out.println("Trying to disconnect...");
        client.stop();
        return null;
    }

    @Override
    public Player[] getPlayers() {
        return (Player[]) remotePlayers.values().toArray(new Player[remotePlayers.values().size()]);
    }

    @Override
    protected void restoreData(GameStateData data) {
        this.data = data;
    }

    @Override
    public void connected() {
        statePanel.setText("Connected");
        statePanel.setVisible(true);
        mouseGrabed = true;
        Mouse.setGrabbed(mouseGrabed);
    }

    @Override
    public void downloadingMap(String mapName) {
        statePanel.setText("Downloading map: " + mapName);
        statePanel.setVisible(true);
    }

    @Override
    public void disconnected() {
        statePanel.setText("Disconnected");
        statePanel.setVisible(true);
    }

    @Override
    public void playerConnected(ClientPlayer player) {
        Player gamePlayer = new Player(3, world) {
            @Override
            public void takeDamage(int damage, int shoterID) {
                // do nothing...
            }
        };
        gamePlayer.id = player.id;
        gamePlayer.setName(player.name);
        map.add(gamePlayer);

        remotePlayersData.put(player.id, player);
        remotePlayers.put(player.id, gamePlayer);
    }

    @Override
    public void playerDisconnected(int id) {
        remotePlayersData.remove(id);
        Player gamePlayer = remotePlayers.remove(id);
        map.remove(gamePlayer);
    }

    @Override
    public void error(String error) {
        statePanel.setText(error);
        statePanel.setVisible(true);
    }

    @Override
    public void serverDataUpdated() {

    }

    @Override
    public void mapFileDownloaded() {
        if (!map.getMapFile().equals(client.getMapName())) {
            map.setMapFile(client.getMapName());
            map.prepareForLoad();
            statePanel.setVisible(false);
        }
    }

    @Override
    public void updateData() {
        ClientPlayer player = new ClientPlayer();
        player.rotation_h = localPlayer.getRotation();
        player.rotation_t = localPlayer.getTurretRotation();
        player.x = localPlayer.getX();
        player.y = localPlayer.getY();
        player.health = localPlayer.getHealth();
        player.score = localPlayer.getScore();
        player.shield = localPlayer.isShielded();
        client.sendPlayerUpdate(player);
    }

    @Override
    public void updatePlayer(PacketUpdatePlayer packet) {
        ClientPlayer player = remotePlayersData.get(packet.id);
        if (player == null) {
            System.err.println("Problem !!! Hráč s ID: " + packet.id + " nenalezen.");
            return;
        }
        player.rotation_h = packet.rotation_h;
        player.rotation_t = packet.rotation_t;
        player.x = packet.x;
        player.y = packet.y;
        player.health = packet.health;
        player.score = packet.score;
        player.ping = packet.ping;
        player.shield = packet.shield;
        player.lastUpdate = FPSCounter.getTime();
    }

    @Override
    public void fired(Vector2f source, Vector2f destination, float direction, Player owner) {
        PacketShotFired packetShotFired = new PacketShotFired();
        packetShotFired.destX = destination.x;
        packetShotFired.destY = destination.y;
        packetShotFired.sourceX = source.x;
        packetShotFired.sourceY = source.y;
        packetShotFired.direction = direction;
        client.fired(packetShotFired);
    }

    @Override
    public void destroyed(int by, Player who) {
        PacketPlayerDestroyed packet = new PacketPlayerDestroyed();
        packet.killer = by;
        if (client != null) {
            client.playerDestroyed(packet);
        }
    }

    @Override
    public void improvementTaken(int improvementID, Player bywho) {
        PacketImprovementSpawn packet = new PacketImprovementSpawn();
        packet.improvement = -1;
        client.sendImprovementTakenPacket(packet);
    }

    @Override
    public void improvementSpawned(int improvement, float x, float y, int timeToDespawn) {
        improvements.spawn(improvement, x, y, timeToDespawn);
    }

    @Override
    public void playerDestroyed(int id) {
        localPlayer.addScore(1);
        System.out.println(localPlayer.getScore());
    }

    @Override
    public void shotFired(PacketShotFired packet) {
        Bullet bullet = new Bullet(world, packet.sourceX, packet.sourceY, packet.destX, packet.destY, 12f, remotePlayers.get(packet.id));
        bullet.setRotation(packet.direction);
        Animator.addOnTop(bullet);
    }

    public Player getLocalPlayer() {
        return localPlayer;
    }

    private class StatePanel extends Component {

        private final Label stateMess;
        private final Button cancelBut;

        public StatePanel() {
            cancelBut = new Button("Cancel") {
                private boolean t = false;
                @Override
                public void update() {
                    super.update();
                    if (isHover() && InputHandler.getInputHandler().isMouseButtonDown(0) && !t) {
                        t = true;
                        client.stop();
                        StateManager.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                StateManager.createState("NetworkDiscoverGameState");
                            }
                        });
                    }
                }

            };
            cancelBut.setFontSize(28);
            cancelBut.setPosition(new Vector2f(App.ORTHO_SIZE.getWidth() / 2 - cancelBut.getWidth() / 2, App.ORTHO_SIZE.getHeight() / 2 + 50));
            add(cancelBut);

            stateMess = new Label("Connecting to " + data.text1 + ":" + data.integer);
            stateMess.setFormat(TrueTypeFont.ALIGN_CENTER);
            stateMess.setFontSize(32);
            stateMess.setPosition(new Vector2f(App.ORTHO_SIZE.getWidth() / 2, App.ORTHO_SIZE.getHeight() / 2));
            add(stateMess);
        }

        public void setText(String text) {
            stateMess.setText(text);
        }

        public boolean isText(String text) {
            return stateMess.getText().equals(text);
        }
    }
}
