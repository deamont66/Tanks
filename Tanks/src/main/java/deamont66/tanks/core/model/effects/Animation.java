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
package deamont66.tanks.core.model.effects;

import deamont66.tanks.core.model.Entity;
import deamont66.util.Box2DWorld;

/**
 *
 * @author Jirka
 */
public abstract class Animation extends Entity {

    private float speed;
    private boolean ended;
    protected long lastAnimated;

    /**
     * Abstract animation.
     * @param world physic world
     * @param speed speed of animation
     */
    public Animation(Box2DWorld world, float speed) {
        super(world);
        this.speed = speed;
    }

    /**
     * Sets speed of animation.
     * @param speed 
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * Returns speed of animation.
     * @return 
     */
    public float getSpeed() {
        return speed;
    }
    
    /**
     * Returns if is animation already ended.
     * @return true if ended
     */
    public boolean isEnded() {
        return ended;
    }

    /**
     * Sets if is animation already ended.
     * @param ended 
     */
    public void setEnded(boolean ended) {
        this.ended = ended;
    }

    @Override
    protected void initEntity() {
        lastAnimated = System.currentTimeMillis();
    }
}
