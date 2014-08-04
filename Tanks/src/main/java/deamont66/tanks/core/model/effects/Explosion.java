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

import deamont66.util.Box2DWorld;
import deamont66.util.ResourceLoader;

/**
 *
 * @author Jirka
 */
public class Explosion extends TextureAnimation {

    private AfterExplosion ae;
    private boolean added = false;
    
    public Explosion(Box2DWorld world, int speed) {
        super(world, false, speed);
        setSize(100, 100);
    }

    public Explosion(Box2DWorld world) {
        this(world, 20);
        ae = new AfterExplosion(world, 1000 * 5);
        ae.setSize(100, 100);
        ae.setWidht(0);
        Animator.add(ae);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setTexture(ResourceLoader.glLoadPNG("/effects/explosion.png"));
    }   

    @Override
    protected void updateEntity(double tfp) {
        super.updateEntity(tfp);
        if(currentRow > 3 && !added) {
            added = true;
            ae.setWidht(100);
        }
        if(!isEnded()) {
            ae.lastAnimated = System.currentTimeMillis();
            ae.setPosition(position.x, position.y);
        }
    }
}
