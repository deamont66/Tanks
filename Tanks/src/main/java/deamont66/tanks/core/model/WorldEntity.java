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
package deamont66.tanks.core.model;

import deamont66.tanks.core.model.map.MapEntity;
import deamont66.tanks.core.App;
import deamont66.tanks.core.model.effects.Animator;
import deamont66.util.Box2DWorld;
import deamont66.util.ResourceLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Jirka
 */
public class WorldEntity extends Entity {

    private final List<Entity> entities = Collections.synchronizedList(new ArrayList<Entity>());

    private final Map map = new Map();
    private String mapFile = "";

    public WorldEntity(Box2DWorld world) {
        super(world);
        map.setWorld(world);
    }

    @Override
    protected void initEntity() {
        setTexture(ResourceLoader.glLoadPNG("/tiles_new.png"));
        setSize(App.ORTHO_SIZE.getWidth(), App.ORTHO_SIZE.getHeight());
    }

    public void preloadMap() {
        map.setFileName(mapFile);
        map.load();
    }

    public void setMapFile(String name) {
        this.mapFile = name;
    }

    public String getMapFile() {
        return mapFile;
    }

    public Vector2f getRandomSpawnPlace() {
        return map.getRandomSpawnPlace();
    }
    
    public void reload() {
        map.setFileName(mapFile);
        map.load();
        synchronized (entities) {
            for (Entity entity : entities) {
                entity.destroy();
            }
        }
        entities.clear();
        Animator.clear();
    }

    @Override
    protected void renderEntity(boolean useTexture) {
        // background grass

        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texture);
        // tiles
        glBegin(GL_QUADS);
        {
            for (MapEntity[] tileY : map.getTiles()) {
                for (MapEntity tile : tileY) {
                    if (!tile.isOnTop()) {
                        tile.render();
                    }
                }
            }

            for (MapEntity[] tileY : map.getTiles()) {
                for (MapEntity tile : tileY) {
                    if (tile.isOnTop()) {
                        tile.render();
                    }
                }
            }
        }
        glEnd();
        glDisable(GL_TEXTURE_2D);

        // entity and animations (effects)
        Animator.render();
        synchronized (entities) {
            for (Entity entity : entities) {
                entity.render();
            }
        }
        Animator.renderOnTop();
    }

    @Override
    protected void updateEntity(double tfp) {
        if(reloadMap) {
            System.out.println("reloadMap->MAP");
            preloadMap();
            reloadMap = false;
        }
        
        for (Entity entity : entities) {
            entity.update(tfp);
        }

        for (MapEntity[] tileY : map.getTiles()) {
            for (MapEntity tile : tileY) {
                tile.update(tfp);
            }
        }
    }

    private boolean reloadMap = false;
    
    public void prepareForLoad() {
        reloadMap = true;
    }
    
    /**
     * Adds entity to list for update and render
     *
     * @param en entity
     */
    public void add(Entity en) {
        entities.add(en);
    }

    /**
     * Removes entity from list.
     *
     * @param en entity
     * @return true if successful
     */
    public boolean remove(Entity en) {
        return entities.remove(en);
    }

    /**
     * Removes entity by index.
     *
     * @param index
     * @return Removed entity.
     */
    public Entity remove(int index) {
        return entities.remove(index);
    }

    /**
     * Compute distance between two points on the map.
     *
     * @param start point
     * @param end point
     * @return distance
     */
    public static float distanceBetweenPoints(Vector2f start, Vector2f end) {
        return (float) Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2));
    }
}
