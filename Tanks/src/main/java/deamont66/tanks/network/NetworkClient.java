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

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import deamont66.tanks.core.model.Map;
import deamont66.tanks.network.packets.PacketAddPlayer;
import deamont66.tanks.network.packets.PacketImprovementSpawn;
import deamont66.tanks.network.packets.PacketLoginPlayer;
import deamont66.tanks.network.packets.PacketLoginResponse;
import deamont66.tanks.network.packets.PacketMapData;
import deamont66.tanks.network.packets.PacketPlayerDestroyed;
import deamont66.tanks.network.packets.PacketRemovePlayer;
import deamont66.tanks.network.packets.PacketServerInfoRequest;
import deamont66.tanks.network.packets.PacketServerInfoResponse;
import deamont66.tanks.network.packets.PacketShotFired;
import deamont66.tanks.network.packets.PacketUpdatePlayer;
import deamont66.tanks.utils.NetworkUtils;
import deamont66.util.FPSCounter;
import deamont66.util.Utils;
import java.io.IOException;
import java.util.HashSet;

/**
 *
 * @author JiriSimecek
 */
public class NetworkClient extends Listener {

    // client synchrnization with server (time between updates sended to server)
    public final static int SYNC_TIME = 20;
    private long lastUpdate = 0L;

    // settings and conncetion
    private final String name;
    private final String ip;
    private final int port;
    private final Client client;

    // listeners
    private final HashSet<ClientListener> listeners;

    // server informations
    private int maxPlayers;
    private int onlinePlayers;
    private String mapName;
    private String serverName;

    // state varialbles
    private boolean logged_in, connected;

    private int ping;

    public NetworkClient(String name, String ip, int port) {
        this.listeners = new HashSet<>();
        this.name = name;
        this.ip = ip;
        this.port = port;

        client = new Client();
        NetworkUtils.setUpSerialization(client.getKryo());
        client.addListener(this);
    }

    public void connect() {
        client.start();
        try {
            client.connect(2000, ip, port, port);
        } catch (IOException ex) {
            error(ex.getLocalizedMessage());
            Utils.error("Can't connect to server", ex);
        }
    }

    /**
     * Disconnects client form server, you can call connect again to connect.
     */
    public void stop() {
        client.close();
        client.stop();
    }

    /**
     * Don't call externaly.
     *
     * @param c
     */
    @Override
    public void connected(Connection c) {
        connected = true;
        connected();
        client.updateReturnTripTime();
        // updateServerData(); - server will automaticaly send server data with type flag "all"
        login();
    }

    /**
     * Don't call externaly.
     *
     * @param c
     * @param o
     */
    @Override
    public void received(Connection c, Object o) {
        if (o instanceof FrameworkMessage.Ping) {
            FrameworkMessage.Ping packet = (FrameworkMessage.Ping) o;
            if (packet.isReply) {
                this.ping = c.getReturnTripTime();
            }
            client.updateReturnTripTime();
        } else if (o instanceof PacketServerInfoResponse) {
            PacketServerInfoResponse packet = (PacketServerInfoResponse) o;
            this.mapName = packet.mapName;
            this.serverName = packet.name;
            this.maxPlayers = packet.maxPlayers;
            this.onlinePlayers = packet.onlinePlayers;
            downloadMapFile();

            serverDataUpdated();
        } else if (o instanceof PacketMapData) {
            PacketMapData packet = (PacketMapData) o;
            if (mapName.equals(packet.name)) {
                Map.saveToFile(packet.name, packet.mapData);
                mapFileChanged();
            }
        } else if (o instanceof PacketAddPlayer && logged_in) {
            PacketAddPlayer packet = (PacketAddPlayer) o;
            ClientPlayer player = new ClientPlayer();
            player.id = packet.id;
            player.name = packet.name;
            playerConnected(player);
        } else if (o instanceof PacketRemovePlayer && logged_in) {
            PacketRemovePlayer packet = (PacketRemovePlayer) o;
            playerDisconnected(packet.id);
        } else if (o instanceof PacketUpdatePlayer && logged_in) {
            PacketUpdatePlayer packet = (PacketUpdatePlayer) o;
            if (packet.id != client.getID() && packet.id != -1) {
                updatePlayer(packet);
            }
        } else if (o instanceof PacketShotFired && logged_in) {
            PacketShotFired packet = (PacketShotFired) o;
            if (packet.id != client.getID() && packet.id != -1) {
                shotFired(packet);
            }
        } else if (o instanceof PacketLoginResponse) {
            PacketLoginResponse packet = (PacketLoginResponse) o;
            if (packet.successful) {
                logged_in = true;
            } else {
                logged_in = false;
                error(packet.mess);
            }
        } else if (o instanceof PacketPlayerDestroyed) {
            PacketPlayerDestroyed packet = (PacketPlayerDestroyed) o;
            playerDestroyed(packet.id);
        } else if (o instanceof PacketImprovementSpawn) {
            PacketImprovementSpawn packet = (PacketImprovementSpawn) o;
            improvementSpawned(packet.improvement, packet.x, packet.y, packet.timeToDespawn);
        }
    }

    /**
     * Don't call externaly.
     *
     * @param c
     */
    @Override
    public void disconnected(Connection c) {
        disconnected();
    }

    // <editor-fold defaultstate="collapsed" desc="getters and setters">
    public int getID() {
        return client.getID();
    }

    public boolean isLoggedIn() {
        return logged_in;
    }

    public boolean isConnected() {
        return connected;
    }

    public int getPing() {
        return ping;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return name;
    }

    public String getMapName() {
        return mapName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    public String getServerName() {
        return serverName;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="public methods">
    public void update(double tfp) {
        if (lastUpdate + SYNC_TIME < FPSCounter.getTime()) {
            updateData();
            lastUpdate = FPSCounter.getTime();
        }
        if (!client.getUpdateThread().isAlive()) {
            disconnected(client);
        }
    }

    public void sendPlayerUpdate(ClientPlayer player) {
        if (client.isConnected() && logged_in) {
            PacketUpdatePlayer packet = new PacketUpdatePlayer();
            packet.rotation_h = player.rotation_h;
            packet.rotation_t = player.rotation_t;
            packet.x = player.x;
            packet.y = player.y;
            packet.health = player.health;
            packet.score = player.score;
            packet.shield = player.shield;
            packet.ping = getPing();
            client.sendUDP(packet);
        }
    }
    
    public void sendImprovementTakenPacket(PacketImprovementSpawn p) {
        client.sendTCP(p);
    }

    public void downloadMapFile() {
        if (!Map.mapExist(mapName)) {
            PacketMapData packet = new PacketMapData();
            packet.name = mapName;
            client.sendTCP(packet);
            downloadingMap(mapName);
        } else {
            mapFileChanged();
        }
    }

    public void updateServerData() {
        PacketServerInfoRequest packet = new PacketServerInfoRequest();
        client.sendTCP(packet);
    }

    public void login() {
        PacketLoginPlayer packet2 = new PacketLoginPlayer();
        packet2.name = name;
        client.sendTCP(packet2);

    }

    public void addListener(ClientListener l) {
        listeners.add(l);
    }

    public void removeListener(ClientListener l) {
        listeners.remove(l);
    }

    public void removeAllListeners() {
        listeners.clear();
    }

    public void fired(PacketShotFired packet) {
        client.sendTCP(packet);
    }

    public void playerDestroyed(PacketPlayerDestroyed packet) {
        if (logged_in) {
            client.sendTCP(packet);
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="listeners / events stuff">
    private void updateData() {
        for (ClientListener clientListener : listeners) {
            clientListener.updateData();
        }
    }

    private void playerDestroyed(int id) {
        for (ClientListener clientListener : listeners) {
            clientListener.playerDestroyed(id);
        }
    }

    private void connected() {
        for (ClientListener clientListener : listeners) {
            clientListener.connected();
        }
    }

    private void downloadingMap(String mapName) {
        for (ClientListener clientListener : listeners) {
            clientListener.downloadingMap(mapName);
        }
    }

    private void disconnected() {
        for (ClientListener clientListener : listeners) {
            clientListener.disconnected();
        }
    }

    private void playerConnected(ClientPlayer player) {
        for (ClientListener clientListener : listeners) {
            clientListener.playerConnected(player);
        }
    }

    private void playerDisconnected(int player) {
        for (ClientListener clientListener : listeners) {
            clientListener.playerDisconnected(player);
        }
    }

    private void error(String error) {
        for (ClientListener clientListener : listeners) {
            clientListener.error(error);
        }
    }

    private void serverDataUpdated() {
        for (ClientListener clientListener : listeners) {
            clientListener.serverDataUpdated();
        }
    }

    private void mapFileChanged() {
        for (ClientListener clientListener : listeners) {
            clientListener.mapFileDownloaded();
        }
    }

    private void updatePlayer(PacketUpdatePlayer packet) {
        for (ClientListener clientListener : listeners) {
            clientListener.updatePlayer(packet);
        }
    }

    private void shotFired(PacketShotFired packet) {
        for (ClientListener clientListener : listeners) {
            clientListener.shotFired(packet);
        }
    }
    
    private void improvementSpawned(int improvement, float x, float y, int timeToDespawn) {
        for (ClientListener clientListener : listeners) {
            clientListener.improvementSpawned(improvement, x, y, timeToDespawn);
        }
    }
    // </editor-fold>

    
}
