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
import org.jbox2d.dynamics.World;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Jirka
 */
public class AfterExplosion extends Animation {

    private boolean ending = false;
    private int coll = 0;

    public AfterExplosion(Box2DWorld world, float timeToEnd) {
        super(world, timeToEnd);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        setTexture(ResourceLoader.glLoadPNG("/effects/explosion.png"));
    }

    @Override
    protected void renderEntity(boolean useTexture) {
        if (isEnded()) {
            return;
        }

        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texture);
        glColor4f(0f, 0f, 0f, 0.5f);
        glBegin(GL_QUADS);
        {
            float tileSize = 1f / TextureAnimation.numberOfTiles;
            glTexCoord2f(tileSize * coll, tileSize * 7);
            glVertex2f(position.x - getWidth() / 2, position.y - getHeight() / 2);
            glTexCoord2f(tileSize * (coll + 1), tileSize * 7);
            glVertex2f(position.x + getWidth() - getWidth() / 2, position.y - getHeight() / 2);
            glTexCoord2f(tileSize * (coll + 1), tileSize * (7 + 1));
            glVertex2f(position.x + getWidth() - getWidth() / 2, position.y + getHeight() - getHeight() / 2);
            glTexCoord2f(tileSize * coll, tileSize * (7 + 1));
            glVertex2f(position.x - getWidth() / 2, position.y + getHeight() - getHeight() / 2);
        }
        glEnd();
        glColor4f(1f, 1f, 1f, 1f);
    }

    @Override
    protected void updateEntity(double tfp) {
        if (System.currentTimeMillis() - lastAnimated > getSpeed()) {
            ending = true;
            lastAnimated = System.currentTimeMillis();
        }
        if (ending == true) {
            if (System.currentTimeMillis() - lastAnimated > 50) {
                lastAnimated = System.currentTimeMillis();
                coll++;
                if (coll + 1 > TextureAnimation.numberOfTiles) {
                    setEnded(true);
                }
            }
        }
    }
}
