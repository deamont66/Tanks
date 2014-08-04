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

/**
 *
 * @author JiriSimecek
 */
public class ImprovementClientManager extends Entity {

    private long lastSpawnTime = FPSCounter.getTime();
    private int timeToDespawn = 10000;

    private ImprovementEntity[] improvements;
    private boolean spawned = false;
    private int activeImprovement = -1;

    private final Object lock = new Object();

    public ImprovementClientManager(Box2DWorld world) {
        super(world);
    }

    @Override
    protected void initEntity() {
        improvements = new ImprovementEntity[]{
            new Wrench(world), new Boost(world), new Shield(world)};

        for (ImprovementEntity improvementEntity : improvements) {
            improvementEntity.update(0);
        }
    }

    @Override
    protected void renderEntity(boolean useTexture) {
        if (spawned) {
            improvements[activeImprovement].render();
        }
    }

    @Override
    protected void updateEntity(double tfp) {
        if (spawned && improvements[activeImprovement].isDestroyed()) {
            improvements[activeImprovement].deactivate();
            spawned = false;
        }
        if (spawned) {
            improvements[activeImprovement].update(tfp);
        }
        synchronized (lock) {
            if (FPSCounter.getTime() - lastSpawnTime > timeToDespawn && spawned) {
                despawn();
            }
        }
    }

    public void spawn(int numberOfImprovement, float x, float y, int timeToDespawn) {
        synchronized (lock) {
            despawn();
            if (numberOfImprovement != -1) {
                activeImprovement = numberOfImprovement;
                improvements[activeImprovement].setPosition(x, y);
                improvements[activeImprovement].setDestroyed(false);
                lastSpawnTime = FPSCounter.getTime();
                this.timeToDespawn = timeToDespawn;
                spawned = true;
            }
        }
    }

    public void despawn() {
        if (activeImprovement != -1) {
            improvements[activeImprovement].deactivate();
        }
        spawned = false;
    }
}
