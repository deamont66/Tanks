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

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import deamont66.tanks.core.AbstractGameState;
import deamont66.tanks.core.App;
import deamont66.tanks.core.StateManager;
import deamont66.tanks.core.gui.*;
import deamont66.tanks.network.packets.*;
import deamont66.tanks.utils.*;
import deamont66.test.MapPicker;
import deamont66.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author JiriSimecek
 */
public class NetworkDiscoverGameState extends AbstractGameState {

    private GameStateData gameData;
    private String selectedIP;
    private String playerName;
    private Panel setPlayerNamePanel;

    @Override
    protected void initDisplay() {
        try {
            StateManager.setDisplayMode();
            Display.setVSyncEnabled(Settings.getInstance().getVSync());
            Display.create();
            Mouse.setGrabbed(false);
        } catch (LWJGLException ex) {
            Utils.error("Cannot create display", ex);
            Display.destroy();
            System.exit(1);
        }
        FPSCounter.init(60);

        Settings s = Settings.getInstance();
        playerName = s.getPlayerName();
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
    }

    @Override
    protected void initFonts() {
    }

    @Override
    protected void initGUI() {
        Label title = new Label("Server list:");
        title.setPosition(new Vector2f(50, 25));
        title.setFontSize(40);
        add(title);

        final ServerList sl = new ServerList();
        sl.setPosition(new Vector2f(0, 100));
        sl.reload();
        add(sl);

        final Button connect = new Button("Connect");
        connect.setPosition(new Vector2f(App.ORTHO_SIZE.getWidth() - 300, App.ORTHO_SIZE.getHeight() - 75));
        connect.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedIP != null) {
                    setPlayerNamePanel.setVisible(true);
                }
            }
        });
        add(connect);
        final Button cancel = new Button("Cancel");
        cancel.setPosition(new Vector2f(50, App.ORTHO_SIZE.getHeight() - 75));
        cancel.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StateManager.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        StateManager.createState("MenuGameState");
                    }
                });
            }
        });
        add(cancel);

        Button reload = new Button("Reload");
        reload.setPosition(new Vector2f(350, App.ORTHO_SIZE.getHeight() - 75));
        reload.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sl.reload();
            }
        });
        add(reload);

        Label ip = new Label("Direct IP:");
        ip.setPosition(new Vector2f(App.ORTHO_SIZE.getWidth() - 750, App.ORTHO_SIZE.getHeight() - 75));
        add(ip);

        final TextField directIP = new TextField("127.0.0.1") {
            @Override
            public void paint() {
                super.paint();
                fontSize = 32;
            }

            @Override
            protected String filterText(String text) {
                char[] chars = text.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if (!Character.isDigit(chars[i]) && chars[i] != '.') {
                        text = text.replace(chars[i] + "", "");
                        System.out.println(chars[i]);
                    }
                }
                selectedIP = getValue();
                return text;
            }
        };
        directIP.setCharacterLimit(15);
        directIP.setSize(150, 50);
        directIP.setPosition(new Vector2f(App.ORTHO_SIZE.getWidth() - 500, App.ORTHO_SIZE.getHeight() - 85));
        directIP.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedIP = ((TextField) e.getSource()).getValue();
            }
        });
        add(directIP);

        Panel ipPanel = new Panel() {
            @Override
            public void update() {
                super.update();
                setVisible((directIP.getValue().equals(selectedIP)));
            }
        };
        ipPanel.setBackground(ColorUtils.RED);
        ipPanel.setPosition(new Vector2f(App.ORTHO_SIZE.getWidth() - 500, App.ORTHO_SIZE.getHeight() - 45));
        ipPanel.setSize(150, 5);
        add(ipPanel);

        // <editor-fold defaultstate="collapsed" desc="create server panel">
        final Panel createPanel = new Panel();
        createPanel.setSize(App.ORTHO_SIZE.getWidth(), App.ORTHO_SIZE.getHeight());
        createPanel.setBackground(ColorUtils.BLACK);
        createPanel.setVisible(false);

        title = new Label("Create server:");
        title.setFontSize(40);
        title.setPosition(50, 25);
        createPanel.add(title);

        //
        Label l = new Label("Server name: ");
        l.setPosition(100, 200);
        createPanel.add(l);

        final TextField serverName = new TextField("Game server");
        serverName.setPosition(400, 200);
        serverName.setSize(250, 50);
        createPanel.add(serverName);

        //
        l = new Label("Max players: ");
        l.setPosition(100, 300);
        createPanel.add(l);

        final TextField maxPlayers = new TextField("8");
        maxPlayers.setPosition(400, 300);
        maxPlayers.setSize(100, 50);
        maxPlayers.setCharacterLimit(4);
        maxPlayers.setNumberOnly(true);
        createPanel.add(maxPlayers);

        //
        l = new Label("Map: ");
        l.setPosition(100, 400);
        createPanel.add(l);

        final TextField mapFile = new TextField("/default/default.tmap");
        mapFile.setPosition(400, 400);
        mapFile.setSize(500, 50);
        mapFile.setDisabled(true);
        createPanel.add(mapFile);

        Button changeMap = new Button("Change");
        changeMap.setPosition(900, 400);
        changeMap.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MapPicker mapPicker = new MapPicker();
                        while (!mapPicker.done) {
                        }
                        if (mapPicker.selectedMapFile != null) {
                            mapFile.setValue(mapPicker.selectedMapFile);
                        }
                    }
                }).start();
            }
        });
        createPanel.add(changeMap);

        Button close = new Button("Cancel");
        close.setPosition(createPanel.getWidth() - 550, createPanel.getHeight() - 75);
        close.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPanel.setVisible(false);
                connect.setDisabled(false);
                cancel.setDisabled(false);
            }
        });
        createPanel.add(close);

        Button create = new Button("Create");
        create.setPosition(createPanel.getWidth() - 300, createPanel.getHeight() - 75);
        create.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Runtime rt = Runtime.getRuntime();
                try {
                    String params = maxPlayers.getValue() + " " + mapFile.getValue() + " " + serverName.getValue();
                    String jarLocation = URLDecoder.decode(getClass().getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
                    rt.exec("cmd.exe /c cd \"" + jarLocation + "\" & start cmd.exe /k \"java -cp tanks-1.0-SNAPSHOT.jar deamont66.tanks.network.ServerTest " + params + "\"");
                } catch (IOException ex) {
                    Logger.getLogger(NetworkDiscoverGameState.class.getName()).log(Level.SEVERE, null, ex);
                }
                Utils.sleep(250);
                selectedIP = "127.0.0.1";
                if (selectedIP != null) {
                    setPlayerNamePanel.setVisible(true);
                }
                createPanel.setVisible(false);
                connect.setDisabled(false);
                cancel.setDisabled(false);
            }
        });
        createPanel.add(create);

        Button createToogle = new Button("Create server");
        createToogle.setPosition(new Vector2f(App.ORTHO_SIZE.getWidth() - 400, 50));
        createToogle.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createPanel.setVisible(!createPanel.isVisible());
                connect.setDisabled(true);
                cancel.setDisabled(true);
            }
        });
        add(createToogle);
        add(createPanel);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="selectMap declaration">
        setPlayerNamePanel = new Panel();
        setPlayerNamePanel.setSize(450, 210);
        setPlayerNamePanel.setCenter(mainComponent);
        setPlayerNamePanel.setBackground(ColorUtils.HALF_TRASPARENT_GREY);
        setPlayerNamePanel.setVisible(false);

        Panel titlePanel = new Panel();
        titlePanel.setBackground(ColorUtils.HALF_TRASPARENT_BLACK);
        titlePanel.setSize(450, 45);
        setPlayerNamePanel.add(titlePanel);

        l = new Label();
        l.setText("Play as:");
        l.setFormat(TrueTypeFont.ALIGN_CENTER);
        l.setFontSize(24);
        l.setPosition(new Vector2f(titlePanel.getWidth() / 2, 10));
        l.setForeground(new Color(255, 255, 255));
        setPlayerNamePanel.add(l);

        Panel panel = new Panel();
        panel.setBackground(ColorUtils.HALF_TRASPARENT_BLACK);
        panel.setPosition(new Vector2f(0, 68));
        panel.setSize(450, 50);
        setPlayerNamePanel.add(panel);

        final TextField usernameField = new TextField(playerName);
        usernameField.setStyle(TrueTypeFont.ALIGN_CENTER);
        usernameField.setCharacterLimit(32);
        usernameField.setPosition(new Vector2f(0, 70));
        usernameField.setSize(450, 45);
        usernameField.setForeground(new Color(255, 255, 255));
        setPlayerNamePanel.add(usernameField);

        Button but = new Button("Cancel");
        but.setFontSize(26);
        but.setPosition(new Vector2f(30, 150));
        but.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        usernameField.setValue(playerName);
                        setPlayerNamePanel.setVisible(false);
                    }
                }).start();
            }
        });
        setPlayerNamePanel.add(but);

        but = new Button("Connect");
        but.setFontSize(26);
        but.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StateManager.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        playerName = usernameField.getValue();
                        Settings s = Settings.getInstance();
                        s.setPlayerName(playerName);
                        s.save();
                        connect();
                    }
                });
            }
        });
        but.setPosition(new Vector2f(setPlayerNamePanel.getWidth() - 30 - but.getWidth(), 150));
        setPlayerNamePanel.add(but);

        add(setPlayerNamePanel);
        // </editor-fold>
    }

    @Override
    protected void initModels() {
    }

    @Override
    protected void preLoadFonts() {
    }

    @Override
    protected void preLoadTextures() {
    }

    @Override
    protected void preLoadMap() {
    }

    @Override
    protected void render() {
    }

    @Override
    protected void update(double tfp) {
    }

    @Override
    protected GameStateData saveData() {
        return gameData;
    }

    @Override
    protected void restoreData(GameStateData data) {
    }

    private void connect() {
        gameData = new GameStateData(selectedIP, playerName, 7777, false, null);
        StateManager.invokeLater(new Runnable() {
            @Override
            public void run() {
                StateManager.createState("NetworkGameState");
            }
        });
    }

    private class ServerList extends Component {

        private final Client client;
        private List<InetAddress> list;
        private Panel panel = null;
        private final Label search;

        public ServerList() {
            client = new Client();
            setSize(1300, 400);

            search = new Label() {
                int n = 0;
                long time = FPSCounter.getTime();

                @Override
                public void update() {
                    super.update();
                    setVisible(reloading);
                    switch (n) {
                        case 0: {
                            setText("Searching _..");
                            break;
                        }
                        case 1: {
                            setText("Searching ._.");
                            break;
                        }
                        case 2: {
                            setText("Searching .._");
                            break;
                        }
                    }
                    if (time + 500 < FPSCounter.getTime()) {
                        n++;
                        n %= 3;
                        time = FPSCounter.getTime();
                    }
                }
            };
            search.setText("Searching ...");
            search.setPosition(new Vector2f(150, 200));
            search.setSize(300, 50);
            add(search);
        }

        private boolean reloading = false, reloadingDone = false;

        public void reload() {
            if (reloading) {
                return;
            }
            reloading = true;
            reloadingDone = false;
            container.clear();
            search.resetRealPosition();
            search.setPosition(new Vector2f(150, 200));
            add(search);

            panel = new Panel();
            add(panel);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    list = client.discoverHosts(7777, 2000);
                    // SMAZAT!!!!!!!!!! hamachi test
                    {
                        try {
                            //list.add(InetAddress.getByName("25.113.229.95"));
                        } catch (Exception ex) {
                            System.err.println(ex);
                        }
                    }
                    // ---------------------------------
                    reloadingDone = true;
                }
            }).start();
        }

        @Override
        public void update() {
            super.update();
            if (reloading && reloadingDone) {
                realoadDone();
            }
        }

        private void realoadDone() {
            for (int i = 0; i < list.size(); i++) {
                final String address = list.get(i).getHostAddress();
                if (address.equals("127.0.0.1")) {
                    list.remove(i);
                    i--;
                    continue;
                }

                Panel p = new HostPanel(new Client(), address, new Vector2f(0, i * (35 + 15) + 15));
                p.setSize(App.ORTHO_SIZE.getWidth(), 35);
                p.setListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (selectedIP != null && selectedIP.equals(address)) {
                            connect();
                        } else {
                            selectedIP = address;
                        }
                    }
                });
                panel.add(p);
            }
            if (list.isEmpty()) {
                Panel p = new Panel();
                p.setPosition(new Vector2f(150, 200));
                p.setSize(300, 50);
                Label l = new Label();
                l.setText("No server found.");
                p.add(l);
                panel.add(p);
            }
            reloading = false;
        }

        private class HostPanel extends Panel {

            private final String address;
            private final int timeout = 1000;

            private int ping;

            public HostPanel(final Client client, String address, Vector2f position) {
                this.address = address;
                setPosition(position);

                Label ip = new Label();
                ip.setText(address);
                ip.setSize(350, 35);
                ip.setPosition(new Vector2f(0, 0));
                this.add(ip);

                final Label name = new Label();
                name.setText("Offline");
                name.setSize(400, 35);
                name.setPosition(new Vector2f(350, 0));
                this.add(name);

                final Label mapName = new Label();
                mapName.setText("-");
                mapName.setSize(550, 35);
                mapName.setPosition(new Vector2f(750, 0));
                this.add(mapName);

                final Label players = new Label();
                players.setText("0/0");
                players.setSize(200, 35);
                players.setPosition(new Vector2f(1300, 0));
                this.add(players);

                final Label pingL = new Label();
                pingL.setSize(100, 35);
                pingL.setPosition(new Vector2f(1500, 0));
                pingL.setText("--");
                this.add(pingL);

                NetworkUtils.setUpSerialization(client.getKryo());
                client.addListener(new Listener() {

                    @Override
                    public void connected(Connection c) {
                        client.updateReturnTripTime();
                    }

                    boolean pingFlag = false, dataFlag = false;
                    private long startTime = -1L;

                    @Override
                    public void received(Connection c, Object o) {
                        if (startTime == -1L) {
                            startTime = System.currentTimeMillis();
                        }

                        if (o instanceof FrameworkMessage.Ping) {
                            FrameworkMessage.Ping packet = (FrameworkMessage.Ping) o;
                            if (packet.isReply) {
                                ping = c.getReturnTripTime();
                                pingL.setText(ping + "ms");
                                pingFlag = true;
                            }
                            client.updateReturnTripTime();
                        } else if (o instanceof PacketServerInfoResponse) {
                            PacketServerInfoResponse packet = (PacketServerInfoResponse) o;
                            mapName.setText(packet.mapName.substring(packet.mapName.lastIndexOf("/") + 1).substring(0, packet.mapName.substring(packet.mapName.lastIndexOf("/") + 1).lastIndexOf(".tmap")));
                            name.setText(packet.name);
                            players.setText(packet.onlinePlayers + "/" + packet.maxPlayers);
                            dataFlag = true;
                        }
                        if ((pingFlag && dataFlag) || System.currentTimeMillis() - startTime > timeout) {
                            client.stop();
                        }
                    }

                });
                client.start();
                try {
                    client.connect(timeout, address, 7777, 7777);
                } catch (IOException ex) {
                    client.stop();
                    Utils.error(ex);
                }
            }

            @Override
            public void update() {
                super.update();
                if (isHover() || selectedIP != null && selectedIP.equals(address)) { //
                    this.setBackground(ColorUtils.RED);
                } else {
                    this.setBackground(ColorUtils.TRASPARET);
                }
            }

        }
    }
}
