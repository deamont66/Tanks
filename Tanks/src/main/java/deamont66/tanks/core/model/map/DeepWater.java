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
import static deamont66.util.PhysicsUtils.*;
import deamont66.tanks.utils.UserData;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author JiriSimecek
 */
public class DeepWater extends MapEntity {

    public static final int ID = 5;
    
    private int shadowX, shadowY;

    public DeepWater(Box2DWorld world, Map mapa, int x, int y) {
        super(world, mapa, x, y);
    }

    @Override
    protected void initEntity() {
        super.initEntity();

        tileX = 2;
        tileY = 0;

        updateTile();
        if (world != null) {
            BodyDef boxDef = new BodyDef();
            boxDef.position.set(pxToWorld(position.getX()), pxToWorld(position.getY()));
            boxDef.type = BodyType.STATIC;
            PolygonShape boxShape = new PolygonShape();
            boxShape.setAsBox(pxToWorld(getWidth() / 2), pxToWorld(getHeight() / 2));
            body = world.getWorld().createBody(boxDef);
            body.setFixedRotation(true);
            body.setUserData(new UserData("map-" + getClass().getSimpleName().toLowerCase()));
            body.setAwake(true);
            body.createFixture(boxShape, 0f);
        }
    }

    @Override
    public void renderEntity(boolean useTexture) {
        float tempRot = rotation;
        setRotation(0);
        glColor4f(1f, 1f, 1f, 1f);
        glTexCoord2f(getTileTexCoord(0, tileX, tileY)[0], getTileTexCoord(0, tileX, tileY)[1]);
        glVertex2f(position.x - getWidth() / 2, position.y - getHeight() / 2);
        glTexCoord2f(getTileTexCoord(1, tileX, tileY)[0], getTileTexCoord(1, tileX, tileY)[1]);
        glVertex2f(position.x + getWidth() - getWidth() / 2, position.y - getHeight() / 2);
        glTexCoord2f(getTileTexCoord(2, tileX, tileY)[0], getTileTexCoord(2, tileX, tileY)[1]);
        glVertex2f(position.x + getWidth() - getWidth() / 2, position.y + getHeight() - getHeight() / 2);
        glTexCoord2f(getTileTexCoord(3, tileX, tileY)[0], getTileTexCoord(3, tileX, tileY)[1]);
        glVertex2f(position.x - getWidth() / 2, position.y + getHeight() - getHeight() / 2);

        setRotation(tempRot);
        if (shadowX != -1 && shadowY != -1) {
            glColor4f(1f, 1f, 1f, 1f);
            glTexCoord2f(getTileTexCoord(0, shadowX, shadowY)[0], getTileTexCoord(0, shadowX, shadowY)[1]);
            glVertex2f(position.x - getWidth() / 2, position.y - getHeight() / 2);
            glTexCoord2f(getTileTexCoord(1, shadowX, shadowY)[0], getTileTexCoord(1, shadowX, shadowY)[1]);
            glVertex2f(position.x + getWidth() - getWidth() / 2, position.y - getHeight() / 2);
            glTexCoord2f(getTileTexCoord(2, shadowX, shadowY)[0], getTileTexCoord(2, shadowX, shadowY)[1]);
            glVertex2f(position.x + getWidth() - getWidth() / 2, position.y + getHeight() - getHeight() / 2);
            glTexCoord2f(getTileTexCoord(3, shadowX, shadowY)[0], getTileTexCoord(3, shadowX, shadowY)[1]);
            glVertex2f(position.x - getWidth() / 2, position.y + getHeight() - getHeight() / 2);
        }
    }

    @Override
    public void updateTile() {
        // <editor-fold defaultstate="collapsed" desc="shadows settings">
        shadowX = shadowY = -1;

        if (!isTile(x - 1, y, "DeepWater") && !isTile(x + 1, y, "DeepWater") && !isTile(x, y - 1, "DeepWater") && !isTile(x, y + 1, "DeepWater")) {
            // * * *
            // *   *
            // * * *
            shadowX = 4;
            shadowY = 15;
            setRotation(0);
        } else if (!isTile(x - 1, y, "DeepWater") && !isTile(x, y - 1, "DeepWater") && !isTile(x + 1, y, "DeepWater")) {
            // * * *
            // *   *
            // *   *
            shadowX = 3;
            shadowY = 15;
            setRotation(1);
        } else if (!isTile(x, y + 1, "DeepWater") && !isTile(x + 1, y, "DeepWater") && !isTile(x, y - 1, "DeepWater")) {
            // * * *
            //     *
            // * * *
            shadowX = 3;
            shadowY = 15;
            setRotation(2);
        } else if (!isTile(x, y - 1, "DeepWater") && !isTile(x - 1, y, "DeepWater") && !isTile(x, y + 1, "DeepWater")) {
            // * * *
            // *    
            // * * *
            shadowX = 3;
            shadowY = 15;
            setRotation(0);
        } else if (!isTile(x + 1, y, "DeepWater") && !isTile(x, y + 1, "DeepWater") && !isTile(x - 1, y, "DeepWater")) {
            // *   *
            // *   *
            // * * *
            shadowX = 3;
            shadowY = 15;
            setRotation(3);
        } else if (!isTile(x, y + 1, "DeepWater") && !isTile(x, y - 1, "DeepWater")) {
            // *   *
            // *   *
            // *   *
            shadowX = 2;
            shadowY = 15;
            setRotation(0);
        } else if (!isTile(x + 1, y, "DeepWater") && !isTile(x - 1, y, "DeepWater")) {
            // * * *
            //      
            // * * *
            shadowX = 2;
            shadowY = 15;
            setRotation(1);
        } else if (!isTile(x - 1, y, "DeepWater") && !isTile(x, y - 1, "DeepWater")) {
            // *
            // *    
            // * * *
            shadowX = 0;
            shadowY = 15;
            setRotation(3);
        } else if (!isTile(x + 1, y, "DeepWater") && !isTile(x, y - 1, "DeepWater")) {
            // * * *
            //     *
            //     *
            shadowX = 0;
            shadowY = 15;
            setRotation(0);
        } else if (!isTile(x - 1, y, "DeepWater") && !isTile(x, y + 1, "DeepWater")) {
            // *   
            // *    
            // * * *
            shadowX = 0;
            shadowY = 15;
            setRotation(2);
        } else if (!isTile(x + 1, y, "DeepWater") && !isTile(x, y + 1, "DeepWater")) {
            //     *
            //     *
            // * * *
            shadowX = 0;
            shadowY = 15;
            setRotation(1);
        } else if (!isTile(x - 1, y, "DeepWater")) {
            shadowX = 1;
            shadowY = 15;
            setRotation(3);
        } else if (!isTile(x + 1, y, "DeepWater")) {
            shadowX = 1;
            shadowY = 15;
            setRotation(1);
        } else if (!isTile(x, y + 1, "DeepWater")) {
            shadowX = 1;
            shadowY = 15;
            setRotation(2);
        } else if (!isTile(x, y - 1, "DeepWater")) {
            shadowX = 1;
            shadowY = 15;
            setRotation(0);
        }
        // </editor-fold>
    }

}
