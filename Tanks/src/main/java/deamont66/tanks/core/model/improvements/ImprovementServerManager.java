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
package deamont66.tanks.core.model.improvements;

import deamont66.tanks.core.App;
import deamont66.tanks.network.ServerTest;
import deamont66.tanks.network.packets.PacketImprovementSpawn;
import java.util.Random;

/**
 *
 * @author JiriSimecek
 */
public class ImprovementServerManager {

    private final int numberOfImprovments = 3;
    private final int timeToSpawn = 15000; // 15000
    private long lastSpawn = 0L;

    private final ServerTest server;
    private final Random random = new Random();

    private int currentImprovement = - 1;
    private float x, y;
    private int timeToDespawn;

    public ImprovementServerManager(ServerTest server) {
        this.server = server;
    }

    public void update() {
        if (System.currentTimeMillis() - lastSpawn > timeToSpawn) {                                                                                     // 8000 + 5000 * rand
            spawn(random.nextInt(numberOfImprovments), App.ORTHO_SIZE.getWidth() * random.nextFloat(), App.ORTHO_SIZE.getHeight() * random.nextFloat(), 8000 + (int) (5000 * random.nextFloat()));
        }
    }

    public void spawn(int improvement, float x, float y, int time) {
        PacketImprovementSpawn packet = new PacketImprovementSpawn();
        this.currentImprovement = improvement;
        this.x = x;
        this.y = x;
        this.timeToDespawn = time;
        packet.improvement = improvement;
        packet.x = x;
        packet.y = y;
        packet.timeToDespawn = time;
        server.sentToAll(packet, true);
        lastSpawn = System.currentTimeMillis();
    }

    public PacketImprovementSpawn getActualPacket() {
        PacketImprovementSpawn packet = new PacketImprovementSpawn();
        packet.improvement = currentImprovement;
        packet.x = x;
        packet.y = y;
        packet.timeToDespawn = (int) (lastSpawn - System.currentTimeMillis() + timeToDespawn);
        return packet;
    }
}
