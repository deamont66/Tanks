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

import deamont66.tanks.core.model.Entity;
import deamont66.util.Box2DWorld;
import deamont66.util.FPSCounter;
import deamont66.util.InputHandler;


/**
 *
 * @author JiriSimecek
 */
public class ImprovementManager extends Entity {

    private long lastSpawnTime = FPSCounter.getTime();
    private final int timeToNextSpawn = 15000;
    private final int timeToDespawn = 10000;

    private ImprovementEntity[] improvements;
    private boolean spawned = false;
    private int activeImprovement = -1;

    public ImprovementManager(Box2DWorld world) {
        super(world);
    }

    @Override
    protected void initEntity() {
        improvements = new ImprovementEntity[]{
            new Wrench(world), new Boost(world), new Shield(world)};

        for (ImprovementEntity improvementEntity : improvements) {
            improvementEntity.update(0);
        }
        spawnNextRandom();
    }

    @Override
    protected void renderEntity(boolean useTexture) {
        if (spawned) {
            improvements[activeImprovement].render();
        }
    }

    @Override
    protected void updateEntity(double tfp) {
        if (improvements[activeImprovement].isDestroyed() && spawned) {
            improvements[activeImprovement].deactivate();
            spawned = false;
        }
        improvements[activeImprovement].update(tfp);
        
        /*if (InputHandler.getInputHandler().isKeyReleased(InputHandler.KEY_3)) {
            spawnNextRandom();
        }*/

        if (FPSCounter.getTime() - lastSpawnTime > timeToDespawn && spawned) {
            spawned = false;
            improvements[activeImprovement].deactivate();
        }

        if (FPSCounter.getTime() - lastSpawnTime > timeToNextSpawn) {
            spawnNextRandom();
        }
    }

    public void spawnNextRandom() {
        if (activeImprovement != -1) {
            improvements[activeImprovement].deactivate();
        }
        activeImprovement = (int) (Math.random() * improvements.length);
        improvements[activeImprovement].setRandomPosition();
        improvements[activeImprovement].setDestroyed(false);
        spawned = true;
        lastSpawnTime = FPSCounter.getTime();
    }
}
