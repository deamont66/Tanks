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

import deamont66.util.Box2DWorld;
import org.jbox2d.dynamics.Body;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Jirka
 */
public abstract class Entity {

    protected final Box2DWorld world;

    protected Vector2f position = new Vector2f();
    protected Vector2f size = new Vector2f();
    protected float rotation = 0;
    protected int texture = -1;
    private boolean initialized = false;
    protected final Vector2f direction = new Vector2f(0, 0);
    protected Body body = null;

    /**
     * Abstract entity.
     * @param world physic world
     */
    public Entity(Box2DWorld world) {
        this.world = world;
    }

    /**
     * Sets rotation.
     * @param rotation 
     */
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    /**
     * Returns rotation.
     * @return rotation
     */
    public float getRotation() {
        return rotation;
    }

    /**
     * Adds angle to rotation and normalized angle.
     * @param angle 
     */
    public void rotate(float angle) {
        rotation += angle;
        normalizeRotation();
    }

    /**
     * Sets opengl number of texture.
     * @param texture 
     */
    public void setTexture(int texture) {
        this.texture = texture;
    }

    /**
     * Returns opengl number of texture.
     * @return 
     */
    public int getTexture() {
        return texture;
    }

    /**
     * Returns position.
     * @return position
     */
    public Vector2f getPosition() {
        return position;
    }

    /**
     * Sets position.
     *
     * @param x
     * @param y
     */
    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    /**
     * Sets position
     *
     * @param position
     */
    public void setPosition(Vector2f position) {
        this.position.set(position);
    }

    /**
     * Returns size of entity.
     *
     * @return
     */
    public Vector2f getSize() {
        return size;
    }

    /**
     * Sets size of entity.
     *
     * @param width
     * @param height
     */
    public void setSize(float width, float height) {
        this.size.set(width, height);
    }

    /**
     * Sets size of entity.
     *
     * @param size
     */
    public void setSize(Vector2f size) {
        this.size = size;
    }

    /**
     * Returns width.
     *
     * @return width
     */
    public float getWidth() {
        return size.x;
    }

    /**
     * Returns height.
     *
     * @return height
     */
    public float getHeight() {
        return size.y;
    }

    /**
     * Sets width of entity
     *
     * @param width new width
     */
    public void setWidht(float width) {
        size.x = width;
    }

    /**
     * Sets height of entity
     *
     * @param height new height
     */
    public void setHeight(float height) {
        size.y = height;
    }

    /**
     * Sets x position
     *
     * @param x new x
     */
    public void setX(float x) {
        position.x = x;
    }

    /**
     * Sets y position.
     *
     * @param y new y
     */
    public void setY(float y) {
        position.y = y;
    }

    /**
     * Returns x position.
     *
     * @return x
     */
    public float getX() {
        return position.x;
    }

    /**
     * Returns y position.
     *
     * @return y
     */
    public float getY() {
        return position.y;
    }

    /**
     * Calls renderEntity
     */
    public void render() {
        renderEntity((texture != -1));
    }

    /**
     * Calls updateEntity everyFrame and at the begin call initEntity()
     *
     * @param tfp game_speed/fps
     */
    public void update(double tfp) {
        if (!initialized) {
            initEntity();
            initialized = true;
        }
        updateEntity(tfp);
    }

    /**
     * Normalizate rotation of entity (Example: 480° -> 120°)
     */
    protected void normalizeRotation() {
        if (rotation >= 360) {
            rotation -= 360;
        } else if (rotation < 0) {
            rotation += 360;
        }
    }

    /**
     * Sets new physic body to entity.
     *
     * @param body new body
     */
    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * Return physic body.
     *
     * @return body
     */
    public Body getBody() {
        return this.body;
    }
    
    /**
     * Prepare entity to destroy, remove body from world if exist
     */
    public void destroy() {
        if(body != null) {
            world.removeBody(body);
        }
    }

    /**
     * Called in first frame to intializate entity.
     */
    protected abstract void initEntity();

    /**
     * Called every frame to render the entity.
     *
     * @param useTexture true if is set texture else false
     */
    protected abstract void renderEntity(boolean useTexture);

    /**
     * Called every frame to update entity.
     *
     * @param tfp game_speed/fps
     */
    protected abstract void updateEntity(double tfp);
}
