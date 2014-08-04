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

import deamont66.tanks.core.model.Entity;
import deamont66.tanks.core.model.Map;
import deamont66.tanks.core.model.WorldEntity;
import deamont66.util.Box2DWorld;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glVertex2f;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author JiriSimecek
 */
public abstract class MapEntity extends Entity {

    protected int tileX, tileY;
    protected int x, y;
    protected final Map mapa;
    public static Vector2f SIZE = new Vector2f(32, 32);
    private boolean onTop = false;
    protected float tileSize = 1f / 16f;

    private int specialX = -1;
    private int specialY = -1;
    private int specialRotation;
    private boolean isSpawnPlace = false;

    /**
     * Abstract map entity
     *
     * @param world - physic world
     * @param mapa - tilemap entity
     * @param x - x position in tilemap
     * @param y - y position in tilemap
     */
    public MapEntity(Box2DWorld world, Map mapa, int x, int y) {
        super(world);
        this.mapa = mapa;
        this.x = x;
        this.y = y;
    }

    @Override
    protected void initEntity() {
        setSize(SIZE.x, SIZE.y);
        setPosition(x * SIZE.x + getWidth() / 2, y * SIZE.y + getHeight() / 2);
    }

    @Override
    protected void updateEntity(double tfp) {
    }

    /**
     * Makes entity rendered on the top so additional effects like shadows can
     * be rendered.
     *
     * @param onTop
     */
    public void setOnTop(boolean onTop) {
        this.onTop = onTop;
    }

    /**
     * @return true if is set to be rendered on the top
     */
    public boolean isOnTop() {
        return onTop;
    }

    /**
     * Finds if is on position in tilemap objec of given class.
     *
     * @param x - position in tilemap
     * @param y - position in tilemap
     * @param tileName - name of class in tilemep
     * @return true if class match with class in tilemap else false
     */
    protected boolean isTile(int x, int y, String tileName) {
        if (x >= 0 && y >= 0) {
            try {
                if (mapa.getMapEntity(x, y).getClass().getSimpleName().equals(tileName)) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Gets texCoord for tiles (enables rotation of tiles)
     *
     * @param i - number of texcoord
     * @param x - x position in tile texture
     * @param y - y position in tile texture
     * @return 2D texCoord for tile
     */
    protected float[] getTileTexCoord(int i, int x, int y) {
        if (i == 0 && rotation == 0 || i == 1 && rotation == 1 || i == 2 && rotation == 2 || i == 3 && rotation == 3) {
            return new float[]{tileSize * x, tileSize * y};
        } else if (i == 1 && rotation == 0 || i == 2 && rotation == 1 || i == 3 && rotation == 2 || i == 0 && rotation == 3) {
            return new float[]{tileSize * (x + 1), tileSize * y};
        } else if (i == 2 && rotation == 0 || i == 3 && rotation == 1 || i == 0 && rotation == 2 || i == 1 && rotation == 3) {
            return new float[]{tileSize * (x + 1), tileSize * (y + 1)};
        } else if (i == 3 && rotation == 0 || i == 0 && rotation == 1 || i == 1 && rotation == 2 || i == 2 && rotation == 3) {
            return new float[]{tileSize * x, tileSize * (y + 1)};
        }
        return null;
    }

    public void setSpawnPlace(boolean isSpawnPlace) {
        this.isSpawnPlace = isSpawnPlace;
    }

    public boolean isSpawnPlace() {
        return isSpawnPlace;
    }

    /**
     * Is calles every time when neighbour tile is changed.
     */
    public abstract void updateTile();

    /**
     * Same like isTile(className, x, y),
     *
     * @param className - name of class in tilemep
     * @return true if class match with class in tilemap else false
     */
    public boolean isTile(String className) {
        return isTile(x, y, className);
    }

    protected void renderSpecialTile() {
        if (specialX != -1 && specialY != -1) {
            float tempRotation = getRotation();
            setRotation(specialRotation);
            // shadow
            float offset = 2f;
            glColor4f(0f, 0f, 0f, 0.6f);
            glTexCoord2f(getTileTexCoord(0, specialX, specialY)[0], getTileTexCoord(0, specialX, specialY)[1]);
            glVertex2f(position.x - getWidth() / 2 + offset, position.y - getHeight() / 2 + offset);
            glTexCoord2f(getTileTexCoord(1, specialX, specialY)[0], getTileTexCoord(1, specialX, specialY)[1]);
            glVertex2f(position.x + getWidth() - getWidth() / 2 + offset, position.y - getHeight() / 2 + offset);
            glTexCoord2f(getTileTexCoord(2, specialX, specialY)[0], getTileTexCoord(2, specialX, specialY)[1]);
            glVertex2f(position.x + getWidth() - getWidth() / 2 + offset, position.y + getHeight() - getHeight() / 2 + offset);
            glTexCoord2f(getTileTexCoord(3, specialX, specialY)[0], getTileTexCoord(3, specialX, specialY)[1]);
            glVertex2f(position.x - getWidth() / 2 + offset, position.y + getHeight() - getHeight() / 2 + offset);

            // normal
            glColor4f(1f, 1f, 1f, 1f);
            glTexCoord2f(getTileTexCoord(0, specialX, specialY)[0], getTileTexCoord(0, specialX, specialY)[1]);
            glVertex2f(position.x - getWidth() / 2, position.y - getHeight() / 2);
            glTexCoord2f(getTileTexCoord(1, specialX, specialY)[0], getTileTexCoord(1, specialX, specialY)[1]);
            glVertex2f(position.x + getWidth() - getWidth() / 2, position.y - getHeight() / 2);
            glTexCoord2f(getTileTexCoord(2, specialX, specialY)[0], getTileTexCoord(2, specialX, specialY)[1]);
            glVertex2f(position.x + getWidth() - getWidth() / 2, position.y + getHeight() - getHeight() / 2);
            glTexCoord2f(getTileTexCoord(3, specialX, specialY)[0], getTileTexCoord(3, specialX, specialY)[1]);
            glVertex2f(position.x - getWidth() / 2, position.y + getHeight() - getHeight() / 2);
            setRotation(tempRotation);
        }
    }

    protected void updateSpecialTile() {
        boolean set = false;
        
        if (!isTile("Tree") && !set) {
            set = true;
            if (isTile(x, y + 1, "Tree") && isTile(x + 1, y, "Tree") && isTile(x - 1, y, "Tree") && isTile(x, y - 1, "Tree")) {
                specialX = 14;
                specialY = 0;
                specialRotation = 1;
            } else if (isTile(x, y + 1, "Tree") && isTile(x + 1, y, "Tree") && isTile(x - 1, y, "Tree")) {
                specialX = 13;
                specialY = 0;
                specialRotation = 1;
            } else if (isTile(x, y + 1, "Tree") && isTile(x + 1, y, "Tree") && isTile(x, y - 1, "Tree")) {
                specialX = 13;
                specialY = 0;
                specialRotation = 0;
            } else if (isTile(x, y - 1, "Tree") && isTile(x - 1, y, "Tree") && isTile(x + 1, y, "Tree")) {
                specialX = 13;
                specialY = 0;
                specialRotation = 3;
            } else if (isTile(x, y - 1, "Tree") && isTile(x, y + 1, "Tree") && isTile(x - 1, y, "Tree")) {
                specialX = 13;
                specialY = 0;
                specialRotation = 2;
            } else if (isTile(x, y + 1, "Tree") && isTile(x + 1, y, "Tree")) {
                specialX = 11;
                specialY = 0;
                specialRotation = 1;
            } else if (isTile(x, y - 1, "Tree") && isTile(x + 1, y, "Tree")) {
                specialX = 11;
                specialY = 0;
                specialRotation = 0;
                setRotation(0);
            } else if (isTile(x, y - 1, "Tree") && isTile(x - 1, y, "Tree")) {
                specialX = 11;
                specialY = 0;
                specialRotation = 3;
            } else if (isTile(x, y + 1, "Tree") && isTile(x - 1, y, "Tree")) {
                specialX = 11;
                specialY = 0;
                specialRotation = 2;
            } else {
                set = false;
                specialX = -1;
                specialY = -1;
                specialRotation = 0;
            }
        }
        if (!isTile("DestroyedTree") && !set) {
            set = true;
            if (isTile(x, y + 1, "DestroyedTree") && isTile(x + 1, y, "DestroyedTree") && isTile(x - 1, y, "DestroyedTree") && isTile(x, y - 1, "DestroyedTree")) {
                specialX = 14;
                specialY = 1;
                specialRotation = 1;
            } else if (isTile(x, y + 1, "DestroyedTree") && isTile(x + 1, y, "DestroyedTree") && isTile(x - 1, y, "DestroyedTree")) {
                specialX = 13;
                specialY = 1;
                specialRotation = 1;
            } else if (isTile(x, y + 1, "DestroyedTree") && isTile(x + 1, y, "DestroyedTree") && isTile(x, y - 1, "DestroyedTree")) {
                specialX = 13;
                specialY = 1;
                specialRotation = 0;
            } else if (isTile(x, y - 1, "DestroyedTree") && isTile(x - 1, y, "DestroyedTree") && isTile(x + 1, y, "DestroyedTree")) {
                specialX = 13;
                specialY = 1;
                specialRotation = 3;
            } else if (isTile(x, y - 1, "DestroyedTree") && isTile(x, y + 1, "DestroyedTree") && isTile(x - 1, y, "DestroyedTree")) {
                specialX = 13;
                specialY = 1;
                specialRotation = 2;
            } else if (isTile(x, y + 1, "DestroyedTree") && isTile(x + 1, y, "DestroyedTree")) {
                specialX = 11;
                specialY = 1;
                specialRotation = 1;
            } else if (isTile(x, y - 1, "DestroyedTree") && isTile(x + 1, y, "DestroyedTree")) {
                specialX = 11;
                specialY = 1;
                specialRotation = 0;
                setRotation(0);
            } else if (isTile(x, y - 1, "DestroyedTree") && isTile(x - 1, y, "DestroyedTree")) {
                specialX = 11;
                specialY = 1;
                specialRotation = 3;
            } else if (isTile(x, y + 1, "DestroyedTree") && isTile(x - 1, y, "DestroyedTree")) {
                specialX = 11;
                specialY = 1;
                specialRotation = 2;
            } else {
                set = false;
                specialX = -1;
                specialY = -1;
                specialRotation = 0;
            }
        }
        if(!set) {
            specialX = -1;
            specialY = -1;
            specialRotation = 0;
        }
    }
}
