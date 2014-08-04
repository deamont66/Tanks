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
package deamont66.tanks.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import deamont66.tanks.core.model.Map;
import deamont66.tanks.core.model.improvements.ImprovementServerManager;
import deamont66.tanks.network.packets.PacketAddPlayer;
import deamont66.tanks.network.packets.PacketImprovementSpawn;
import deamont66.tanks.network.packets.PacketLoginPlayer;
import deamont66.tanks.network.packets.PacketMapData;
import deamont66.tanks.network.packets.PacketRemovePlayer;
import deamont66.tanks.network.packets.PacketLoginResponse;
import deamont66.tanks.network.packets.PacketPlayerDestroyed;
import deamont66.tanks.network.packets.PacketServerInfoRequest;
import deamont66.tanks.network.packets.PacketServerInfoResponse;
import deamont66.tanks.network.packets.PacketShotFired;
import deamont66.tanks.network.packets.PacketUpdatePlayer;
import deamont66.tanks.utils.NetworkUtils;
import deamont66.util.Utils;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author JiriSimecek
 */
public class ServerTest extends Listener {

    // instance of server test class
    private static ServerTest serverInst = null;
    // instance of server class
    private final Server server;
    // ports
    private static int tcp, udp;
    private static String name = "Game Server";
    private static String mapName = "/default/default.tmap";
    private static int maxPlayers = 4;

    private final ImprovementServerManager improvements = new ImprovementServerManager(this);

    // kryonet server instance 
    private boolean running;

    // players
    private final java.util.Map<Integer, ServerPlayer> players;

    private ServerTest() {
        // players map
        players = new HashMap<>();

        // creating new kryonet server 
        server = new Server();
        // settings for serialization
        NetworkUtils.setUpSerialization(server.getKryo());
    }

    // updates players (ready for physics, collision and other things)
    private void updatePlayer(ServerPlayer player) {

    }

    @Override
    public void connected(Connection c) {
        ServerPlayer player = new ServerPlayer();
        player.c = c;
        players.put(c.getID(), player);
        System.out.println(c.getRemoteAddressTCP() + " (ID: " + c.getID() + ") connection received.");

        // sending server data
        PacketServerInfoResponse packet2 = new PacketServerInfoResponse();
        packet2.maxPlayers = maxPlayers;
        packet2.mapName = mapName;
        packet2.onlinePlayers = getNumberOfPlayers(true);
        packet2.name = name;
        c.sendUDP(packet2);
    }

    @Override
    public void received(Connection c, Object o) {
        if (o instanceof PacketLoginPlayer) {
            PacketLoginPlayer packet = (PacketLoginPlayer) o;
            ServerPlayer player = players.get(c.getID());
            if (getNumberOfPlayers(true) >= maxPlayers) {
                PacketLoginResponse packet2 = new PacketLoginResponse(false);
                packet2.mess = "Server is full.";
                c.sendTCP(packet2);
            } else {
                player.name = packet.name;
                player.logged_id = true;
                c.sendTCP(new PacketLoginResponse(true));
                PacketAddPlayer packet2 = new PacketAddPlayer();
                packet2.id = c.getID();
                packet2.name = player.name;
                server.sendToAllExceptTCP(c.getID(), packet2);

                for (ServerPlayer pl : players.values()) {
                    if (pl.c.getID() != c.getID()) {
                        packet2 = new PacketAddPlayer();
                        packet2.id = pl.c.getID();
                        packet2.name = pl.name;
                        c.sendTCP(packet2);
                    }
                }
                PacketImprovementSpawn actualPacket = improvements.getActualPacket();
                if (actualPacket.improvement != -1) {
                    c.sendTCP(actualPacket);
                }
            }
        } else if (o instanceof PacketServerInfoRequest) {
            PacketServerInfoRequest packet = (PacketServerInfoRequest) o;
            if (packet.specific.equals("all")) {
                PacketServerInfoResponse packet2 = new PacketServerInfoResponse();
                packet2.maxPlayers = maxPlayers;
                packet2.mapName = mapName;
                packet2.onlinePlayers = getNumberOfPlayers(true);
                packet2.name = name;
                c.sendUDP(packet2);
            }
        } else if (o instanceof PacketMapData) {
            PacketMapData packet = (PacketMapData) o;
            packet.mapData = Map.loadFromFile(packet.name);
            c.sendTCP(packet);
        } else if (o instanceof PacketUpdatePlayer) {
            PacketUpdatePlayer packet = (PacketUpdatePlayer) o;
            ServerPlayer player = players.get(c.getID());
            player.rotation_h = packet.rotation_h;
            player.rotation_t = packet.rotation_t;
            player.x = packet.x;
            player.y = packet.y;
            player.health = packet.health;
            player.score = packet.score;
            player.ping = packet.ping;
            player.shield = packet.shield;
            // set id of player and send it to others
            packet.id = c.getID();
            server.sendToAllExceptUDP(c.getID(), packet);
        } else if (o instanceof PacketShotFired) {
            PacketShotFired packet = (PacketShotFired) o;
            packet.id = c.getID();
            server.sendToAllExceptTCP(c.getID(), packet);
        } else if (o instanceof PacketPlayerDestroyed) {
            PacketPlayerDestroyed packet = (PacketPlayerDestroyed) o;
            packet.id = c.getID();
            server.sendToTCP(packet.killer, packet);
        } else if (o instanceof PacketImprovementSpawn) {
            PacketImprovementSpawn packet = (PacketImprovementSpawn) o;
            if (packet.improvement == -1) {
                server.sendToAllExceptTCP(c.getID(), packet);
            }
        }
    }

    @Override
    public void disconnected(Connection c) {
        ServerPlayer pl = players.get(c.getID());
        players.remove(c.getID());
        if (pl != null && pl.logged_id == true) {
            PacketRemovePlayer packet = new PacketRemovePlayer();
            packet.id = c.getID();
            server.sendToAllExceptTCP(c.getID(), packet);
        }
        System.out.println("Connection (ID: " + c.getID() + ") dropped.");
    }

    /**
     * Sents packet (object) to all connected clients. Object class have to be
     * specified in Kryo definition.
     *
     * @param o object to send
     * @param tcp boolean (if its true tcp is used else udp)
     */
    public void sentToAll(Object o, boolean tcp) {
        if (tcp) {
            server.sendToAllTCP(o);
        } else {
            server.sendToAllUDP(o);
        }
    }

    /**
     * Will start the server.
     */
    public void start() {
        // starts server thread
        server.start();
        server.addListener(this);
        try {
            // bind ports
            server.bind(tcp, udp);
        } catch (IOException ex) {
            // if is unsuccessfull we abort stating
            Utils.error("Cannot create server", ex);
            stop();
            return;
        }

        System.out.println("Server started on port: " + tcp);
        System.out.println("-------------------------------\nFor exit press Control+C\n");

        // server is now running
        running = true;
        updateLoop();
    }

    /**
     * Will staph the server.
     */
    public void stop() {
        server.stop();
        running = false;
        try {
            if (t != null && t.isAlive()) {
                t.join();
            }
        } catch (InterruptedException ex) {
            Utils.error("Can't join update thread", ex);
        }
        System.out.println("Server stoped.");
    }

    private Thread t = null;

    /**
     * Loop with thread for update clients (sends messages to clients)
     */
    private void updateLoop() {
        boolean tempRunnning = running;
        if (t != null && t.isAlive()) {
            running = false;
            try {
                t.join();
            } catch (InterruptedException ex) {
                Utils.error("Can't join update thread", ex);
            }
        }
        running = tempRunnning;

        t = new Thread(new Runnable() {
            @Override
            public void run() {
                long lastUpdate;
                while (isRunning()) {
                    lastUpdate = System.currentTimeMillis();
                    for (ServerPlayer player : players.values()) {
                        updatePlayer(player);
                    }
                    Utils.sleep((int) (30 - (System.currentTimeMillis() - lastUpdate)));
                    improvements.update();
                }
            }
        });
        t.start();
    }

    /**
     * Returns true if is server runnning or false if isn't.
     *
     * @return running
     */
    public boolean isRunning() {
        return running;
    }

    private int getNumberOfPlayers(boolean onlyLoggedIn) {
        int n = 0;
        for (ServerPlayer player : players.values()) {
            if (!onlyLoggedIn || player.logged_id) {
                n++;
            }
        }
        return n;
    }

    /**
     * Returns server instance.
     *
     * @return server
     */
    public static ServerTest getServer() {
        if (serverInst == null) {
            serverInst = new ServerTest();
        }
        return serverInst;
    }

    /**
     * Sets server ports. Should be calld before start.
     *
     * @param tcp
     * @param udp
     */
    public static void setPorts(int tcp, int udp) {
        ServerTest.tcp = tcp;
        ServerTest.udp = udp;
    }

    public static void setName(String name) {
        ServerTest.name = name;
    }

    public static void setMaxPlayers(int maxPlayers) {
        ServerTest.maxPlayers = maxPlayers;
    }

    public static void setMapName(String mapName) {
        ServerTest.mapName = mapName;
    }

    public static void main(String[] args) {
        // args
        int maxPl = maxPlayers;
        String map = mapName;
        String serverName = name;
        if (args.length > 0) {
            maxPl = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            map = args[1];
        }
        if (args.length > 2) {
            serverName = args[2];
        }
        System.out.println("__ Tanks server by deamont66 __\n-------------------------------");
        System.out.println("Maxplayers: " + maxPl);
        System.out.println("Map: " + map);

        // System.exit(0);
        // settings
        ServerTest.setPorts(7777, 7777);
        ServerTest.setMaxPlayers(maxPl);
        ServerTest.setMapName(map);
        ServerTest.setName(serverName);

        ServerTest server = ServerTest.getServer();
        server.start();
    }

}
