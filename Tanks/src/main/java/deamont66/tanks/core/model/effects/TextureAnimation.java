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
import static org.lwjgl.opengl.GL11.*;

/**
 * Special type of animation from texture.
 * @author Jirka
 */
public abstract class TextureAnimation extends Animation {

    public static int sizeOfTexture = 512;
    public static int sizeOfTile = 64;
    public static int numberOfTiles = 8;
    private boolean loop;
    protected int currentCol;
    protected int currentRow;

    public TextureAnimation(Box2DWorld world) {
        this(world, true, 20);
    }

    public TextureAnimation(Box2DWorld world, boolean loop, int speed) {
        super(world, speed);
        this.loop = loop;

    }

    /**
     * Returns if is animation running in loop.
     * @return 
     */
    public boolean isLoop() {
        return loop;
    }

    /**
     * Sets if is aniamtion loop.
     * @param loop 
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    @Override
    protected void updateEntity(double tfp) {
        if (isEnded()) {
            return;
        }
        if (System.currentTimeMillis() - lastAnimated > getSpeed()) {
            currentCol++;
            if (currentCol < TextureAnimation.numberOfTiles) {
                lastAnimated = System.currentTimeMillis();
            } else {
                currentCol = 0;
                currentRow++;
                if (currentRow < TextureAnimation.numberOfTiles) {
                    lastAnimated = System.currentTimeMillis();
                } else {
                    if (isLoop()) {
                        currentRow = 0;
                    } else {
                        end();
                    }
                }
            }
        }
    }

    /**
     * Calls setEnded(true)
     */
    protected void end() {
        setEnded(true);
    }

    @Override
    protected void renderEntity(boolean useTexture) {
        if (isEnded()) {
            return;
        }
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texture);
        glBegin(GL_QUADS);
        {
            float tileSize = 1f / TextureAnimation.numberOfTiles;
            glTexCoord2f(tileSize * currentCol, tileSize * currentRow);
            glVertex2f(position.x - getWidth() / 2, position.y - getHeight() / 2);
            glTexCoord2f(tileSize * (currentCol + 1), tileSize * currentRow);
            glVertex2f(position.x + getWidth() - getWidth() / 2, position.y - getHeight() / 2);
            glTexCoord2f(tileSize * (currentCol + 1), tileSize * (currentRow + 1));
            glVertex2f(position.x + getWidth() - getWidth() / 2, position.y + getHeight() - getHeight() / 2);
            glTexCoord2f(tileSize * currentCol, tileSize * (currentRow + 1));
            glVertex2f(position.x - getWidth() / 2, position.y + getHeight() - getHeight() / 2);
        }
        glEnd();
    }
}
