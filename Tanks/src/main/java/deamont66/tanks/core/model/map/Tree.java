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
import deamont66.tanks.core.model.WorldEntity;
import deamont66.tanks.core.model.effects.Animator;
import deamont66.tanks.core.model.effects.Smoke;
import deamont66.util.Box2DWorld;
import static deamont66.util.PhysicsUtils.pxToWorld;
import deamont66.tanks.utils.UserData;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author JiriSimecek
 */
public class Tree extends MapEntity {

    public static final int ID = 7;
    
    private final Grass grass = new Grass(null, mapa, x, y);

    public Tree(Box2DWorld world, Map mapa, int x, int y) {
        super(world, mapa, x, y);
        tileX = 6;
        tileY = 0;
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        grass.initEntity();
        // načtení tilu (textura, okraje atd.)
        updateTile();

        if (world != null) {
            // tělo pro kolize
            BodyDef boxDef = new BodyDef();
            boxDef.position.set(pxToWorld(position.getX()), pxToWorld(position.getY()));
            boxDef.type = BodyType.STATIC;
            PolygonShape boxShape = new PolygonShape();
            boxShape.setAsBox(pxToWorld(getWidth() / 2), pxToWorld(getHeight() / 2));
            body = world.getWorld().createBody(boxDef);
            FixtureDef boxFixture = new FixtureDef();
            boxFixture.density = 0.1f;
            boxFixture.shape = boxShape;
            boxFixture.isSensor = true;
            body.setFixedRotation(true);
            body.setUserData(new UserData("map-" + getClass().getSimpleName().toLowerCase()));
            body.setAwake(true);
            body.createFixture(boxFixture);
        }
    }

    @Override
    protected void updateEntity(double tfp) {
        super.updateEntity(tfp);
        grass.updateEntity(tfp);
        // pokud bylo nastaveno 
        if (body != null && ((UserData) body.getUserData()).bool) {
            Smoke smoke = new Smoke(world);
            smoke.setPosition(position.x, position.y);
            Animator.addOnTop(smoke);
            mapa.updateMap(x, y, new DestroyedTree(world, mapa, x, y));
        }
    }

    @Override
    public void updateTile() {
        grass.updateTile();
        // <editor-fold defaultstate="collapsed" desc="tile settings">
        int number = 0;
        boolean rUp, lUp, rDown, lDown;
        rUp = lUp = rDown = lDown = false;
        if (isTile(x - 1, y - 1, "Tree")) {
            number++;
            lUp = true;
        }
        if (isTile(x + 1, y + 1, "Tree")) {
            number++;
            rDown = true;
        }
        if (isTile(x + 1, y - 1, "Tree")) {
            number++;
            rUp = true;
        }
        if (isTile(x - 1, y + 1, "Tree")) {
            number++;
            lDown = true;
        }
        if (!lUp && isTile(x - 1, y, "Tree") && isTile(x, y - 1, "Tree")) {
            number++;
            lUp = true;
        }
        if (!rUp && isTile(x + 1, y, "Tree") && isTile(x, y - 1, "Tree")) {
            number++;
            rUp = true;
        }
        if (!rDown && isTile(x + 1, y, "Tree") && isTile(x, y + 1, "Tree")) {
            number++;
            rDown = true;
        }
        if (!lDown && isTile(x - 1, y, "Tree") && isTile(x, y + 1, "Tree")) {
            number++;
            lDown = true;
        }

        switch (number) {
            case 0: {
                tileX = 8;
                tileY = 0;
                setRotation(0);
                break;
            }
            case 1: {
                if (lUp) {
                    tileX = 10;
                    tileY = 0;
                    setRotation(0);
                } else if (rUp) {
                    tileX = 10;
                    tileY = 0;
                    setRotation(1);
                } else if (rDown) {
                    tileX = 10;
                    tileY = 0;
                    setRotation(2);
                } else if (lDown) {
                    tileX = 10;
                    tileY = 0;
                    setRotation(3);
                }
                break;
            }
            case 2: {
                if (lUp && rUp) {
                    tileX = 7;
                    tileY = 0;
                    setRotation(1);
                } else if (rUp && rDown) {
                    tileX = 7;
                    tileY = 0;
                    setRotation(2);
                } else if (rDown && lDown) {
                    tileX = 7;
                    tileY = 0;
                    setRotation(3);
                } else if (lDown && lUp) {
                    tileX = 7;
                    tileY = 0;
                    setRotation(0);
                } else if (lDown && rUp) {
                    tileX = 12;
                    tileY = 0;
                    setRotation(1);
                } else if (rDown && lUp) {
                    tileX = 12;
                    tileY = 0;
                    setRotation(0);
                }
                break;
            }
            case 3: {
                if (lUp && rUp && rDown) {
                    tileX = 9;
                    tileY = 0;
                    setRotation(1);
                } else if (rUp && rDown && lDown) {
                    tileX = 9;
                    tileY = 0;
                    setRotation(2);
                } else if (rDown && lDown && lUp) {
                    tileX = 9;
                    tileY = 0;
                    setRotation(3);
                } else if (lDown && lUp && rUp) {
                    tileX = 9;
                    tileY = 0;
                    setRotation(0);
                }
                break;
            }
            default:
                tileX = 6;
                tileY = 0;
                setRotation(0);
                break;
        }
        // </editor-fold>
    }

    @Override
    protected void renderEntity(boolean useTexture) {
        grass.renderEntity(useTexture);
        
        // shadow render
        float offset = 2f;
        glColor4f(0f, 0f, 0f, 0.6f);
        glTexCoord2f(getTileTexCoord(0, tileX, tileY)[0], getTileTexCoord(0, tileX, tileY)[1]);
        glVertex2f(position.x - getWidth() / 2 + offset, position.y - getHeight() / 2 + offset);
        glTexCoord2f(getTileTexCoord(1, tileX, tileY)[0], getTileTexCoord(1, tileX, tileY)[1]);
        glVertex2f(position.x + getWidth() - getWidth() / 2 + offset, position.y - getHeight() / 2 + offset);
        glTexCoord2f(getTileTexCoord(2, tileX, tileY)[0], getTileTexCoord(2, tileX, tileY)[1]);
        glVertex2f(position.x + getWidth() - getWidth() / 2 + offset, position.y + getHeight() - getHeight() / 2 + offset);
        glTexCoord2f(getTileTexCoord(3, tileX, tileY)[0], getTileTexCoord(3, tileX, tileY)[1]);
        glVertex2f(position.x - getWidth() / 2 + offset, position.y + getHeight() - getHeight() / 2 + offset);
        
        
        // tile
        glColor4f(1f, 1f, 1f, 1f);
        glTexCoord2f(getTileTexCoord(0, tileX, tileY)[0], getTileTexCoord(0, tileX, tileY)[1]);
        glVertex2f(position.x - getWidth() / 2, position.y - getHeight() / 2);
        glTexCoord2f(getTileTexCoord(1, tileX, tileY)[0], getTileTexCoord(1, tileX, tileY)[1]);
        glVertex2f(position.x + getWidth() - getWidth() / 2, position.y - getHeight() / 2);
        glTexCoord2f(getTileTexCoord(2, tileX, tileY)[0], getTileTexCoord(2, tileX, tileY)[1]);
        glVertex2f(position.x + getWidth() - getWidth() / 2, position.y + getHeight() - getHeight() / 2);
        glTexCoord2f(getTileTexCoord(3, tileX, tileY)[0], getTileTexCoord(3, tileX, tileY)[1]);
        glVertex2f(position.x - getWidth() / 2, position.y + getHeight() - getHeight() / 2);
    }

}
