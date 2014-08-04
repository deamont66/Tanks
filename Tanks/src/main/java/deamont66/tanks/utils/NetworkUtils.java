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
package deamont66.tanks.utils;

import com.esotericsoftware.kryo.Kryo;
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

/**
 *
 * @author JiriSimecek
 */
public class NetworkUtils {

    public static void setUpSerialization(Kryo p) {
        p.register(int[].class);
        p.register(int[][].class);
        p.register(PacketAddPlayer.class);
        p.register(PacketLoginPlayer.class);
        p.register(PacketRemovePlayer.class);
        p.register(PacketServerInfoRequest.class);
        p.register(PacketServerInfoResponse.class);
        p.register(PacketUpdatePlayer.class);
        p.register(PacketMapData.class);
        p.register(PacketShotFired.class);
        p.register(PacketLoginResponse.class);
        p.register(PacketPlayerDestroyed.class);
        p.register(PacketImprovementSpawn.class);
    }
}
