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

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import deamont66.tanks.network.packets.PacketAddPlayer;
import deamont66.tanks.network.packets.PacketLoginPlayer;
import deamont66.tanks.network.packets.PacketRemovePlayer;
import deamont66.tanks.network.packets.PacketServerInfoRequest;
import deamont66.tanks.network.packets.PacketServerInfoResponse;
import deamont66.tanks.network.packets.PacketUpdatePlayer;
import deamont66.util.Utils;
import java.io.IOException;
import java.net.InetAddress;

/**
 *
 * @author JiriSimecek
 */
public class ClientTest extends Listener {

    public static String name = "Player " + (int) (Math.random() * 10);

    public ClientTest() {
    }

    public static void main(String[] args) throws IOException {
        final Client cl = new Client();
        cl.getKryo().register(PacketAddPlayer.class);
        cl.getKryo().register(PacketLoginPlayer.class);
        cl.getKryo().register(PacketRemovePlayer.class);
        cl.getKryo().register(PacketServerInfoRequest.class);
        cl.getKryo().register(PacketServerInfoResponse.class);
        cl.getKryo().register(PacketUpdatePlayer.class);
        
        cl.addListener(new Listener() {

            @Override
            public void received(Connection c, Object o) {
                if(o instanceof PacketServerInfoResponse) {
                    PacketServerInfoResponse packet = (PacketServerInfoResponse) o;
                    System.out.println(packet.name + " - " + packet.onlinePlayers + "/" + packet.maxPlayers);
                    c.close();
                }
            }
        });
        

        InetAddress discoverHost = cl.discoverHost(7777, 2000);
        System.out.println("Host found: " + discoverHost.toString());
        cl.start();
        cl.connect(2000, discoverHost, 7777, 7777);

        PacketServerInfoRequest packet = new PacketServerInfoRequest();
        cl.sendUDP(packet);

        // only 'cause deamon thread in client
        while(cl.isConnected()) {
            Utils.sleep(200);
        }
    }
}
