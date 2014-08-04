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
import org.lwjgl.opengl.GL11;

/**
 *
 * @author Jirka
 */
public class FireBall extends TextureAnimation {

    float offset = 0f;

    public FireBall(Box2DWorld world, int speed) {
        super(world, true, speed);
        setSize(20, 20);
    }

    public FireBall(Box2DWorld world) {
        this(world, 20);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setTexture(ResourceLoader.glLoadPNG("/effects/fireball.png"));
    }

    @Override
    protected void renderEntity(boolean useTexture) {
        // SHADOW
        position.translate(offset, offset);
        GL11.glColor4f(0f, 0f, 0f, 0.6f);
        super.renderEntity(useTexture);
        position.translate(-offset, -offset);
        GL11.glColor4f(1f, 1f, 1f, 1f);
        // normal
        super.renderEntity(useTexture);
    }

    /**
     * Sets offset of shadow.
     * @param offset 
     */
    protected void setShadowOffset(float offset) {
        this.offset = offset;
    }

}
