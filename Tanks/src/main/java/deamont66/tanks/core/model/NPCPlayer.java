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
package deamont66.tanks.core.model;

import deamont66.tanks.utils.UserData;
import deamont66.util.Box2DWorld;
import deamont66.util.FPSCounter;
import deamont66.util.InputHandler;

/**
 *
 * @author JiriSimecek
 */
public class NPCPlayer extends Player {

    private final int respawnTime = 5000;
    private long destroyTime = 0L;

    public NPCPlayer(String name, Box2DWorld world) {
        super(3, world);
        tank = new AutomaticTank(world, this);
        tank.setStop(false);
        tank.setPlayerNumber(3);
        tank.setRotation(45);
        setName(name);
    }

    @Override
    protected void updateEntity(double tfp) {
        InputHandler in = InputHandler.getInputHandler();
        if (exploded && FPSCounter.getTime() - destroyTime > respawnTime) {
            if (map != null) {
                tank.setPosition(map.getRandomSpawnPlace());
            }
            resetHealth();
        }

        if (exploded) {
            ((UserData) tank.body.getUserData()).number2 = 0;
            ((UserData) tank.body.getUserData()).number3 = 0;
        }

        tank.setStop(health == 0);
        tank.update(tfp);

        smoke.setPosition(tank.getX(), tank.getY());
    }

    @Override
    protected void destroyed(int shooterID) {
        super.destroyed(shooterID);
        destroyTime = FPSCounter.getTime();
    }
}
