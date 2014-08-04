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

/**
 *
 * @author JiriSimecek
 */
public class TankSmokePack {
    
    private float x, y;
    private final int randX, randY;
    
    final TankSmoke[] smokes;
    
    public TankSmokePack(Box2DWorld world, int size, int randX, int randY) {
        smokes = new TankSmoke[size];
        for (int i = 0; i < smokes.length; i++) {
            smokes[i] = new TankSmoke(world);
            smokes[i].genRandomOffset(randX, randY);
            smokes[i].setEnded(true);
        }
        this.randX = randX;
        this.randY = randY;
    }

    public void setX(float x) {
        setPosition(x, y);
    }

    public void setY(float y) {
        setPosition(x, y);
    }
    
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;

        for (TankSmoke tankSmoke : smokes) {
            tankSmoke.setPosition(x + tankSmoke.randomOffset.x, y + tankSmoke.randomOffset.y);
        }
    }
    
    public void show() {
        for (TankSmoke tankSmoke : smokes) {
            tankSmoke.setEnded(false);
            Animator.addOnTop(tankSmoke);
        }
    }
    
    public void end() {
        for (TankSmoke tankSmoke : smokes) {
            tankSmoke.setEnded(true);
        }
    }
}
