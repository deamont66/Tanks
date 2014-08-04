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
package deamont66.tanks.core.model.map;

import deamont66.tanks.core.model.Map;
import deamont66.util.Box2DWorld;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;

/**
 *
 * @author JiriSimecek
 */
public class DestroyedBrick extends MapEntity {
    
    public static final int ID = 3;
    
    public DestroyedBrick(Box2DWorld world, Map mapa, int x, int y) {
        super(world, mapa, x, y);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        updateTile();
        tileX = 5;
        tileY = 0;
    }
    
    

    @Override
    public void updateTile() {
        updateSpecialTile();
    }

    @Override
    protected void renderEntity(boolean useTexture) {
        glColor3f(1f, 1f, 1f);
        glTexCoord2f(getTileTexCoord(0, tileX, tileY)[0], getTileTexCoord(0, tileX, tileY)[1]);
        glVertex2f(position.x - getWidth() / 2, position.y - getHeight() / 2);
        glTexCoord2f(getTileTexCoord(1, tileX, tileY)[0], getTileTexCoord(1, tileX, tileY)[1]);
        glVertex2f(position.x + getWidth() - getWidth() / 2, position.y - getHeight() / 2);
        glTexCoord2f(getTileTexCoord(2, tileX, tileY)[0], getTileTexCoord(2, tileX, tileY)[1]);
        glVertex2f(position.x + getWidth() - getWidth() / 2, position.y + getHeight() - getHeight() / 2);
        glTexCoord2f(getTileTexCoord(3, tileX, tileY)[0], getTileTexCoord(3, tileX, tileY)[1]);
        glVertex2f(position.x - getWidth() / 2, position.y + getHeight() - getHeight() / 2);
        renderSpecialTile();
    }
    
}
