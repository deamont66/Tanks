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
package deamont66.tanks.core.model.improvements;

import deamont66.tanks.core.App;
import deamont66.tanks.core.model.Entity;
import deamont66.tanks.utils.UserData;
import deamont66.util.Box2DWorld;
import deamont66.util.LWJGLUtils;
import static deamont66.util.PhysicsUtils.pxToWorld;
import deamont66.util.ResourceLoader;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author JiriSimecek
 */
public abstract class ImprovementEntity extends Entity {

    protected boolean destroyed = false;
    private int backgroundTexture = -1;

    public ImprovementEntity(Box2DWorld world) {
        super(world);
    }

    public void init() {
        initEntity();
    }

    @Override
    protected void initEntity() {
        backgroundTexture = ResourceLoader.glLoadPNG("/improvements/circle.png");
        setSize(32, 32);
        if (world != null) {
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
            body.setUserData(new UserData("sp-" + getClass().getSimpleName().toLowerCase()));
            body.setAwake(true);
            body.createFixture(boxFixture);
        }
    }

    @Override
    protected void renderEntity(boolean useTexture) {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, backgroundTexture);
        glColor4f(0.8f, 0.8f, 0.8f, 0.5f);
        LWJGLUtils.renderQuad(position.x - 40 / 2, position.y - 40 / 2, 40, 40);
        glBindTexture(GL_TEXTURE_2D, texture);
        glColor4f(1, 1, 1, 1f);
        LWJGLUtils.renderQuad(position.x - getWidth() / 2, position.y - getHeight() / 2, getWidth(), getHeight());
        glDisable(GL_TEXTURE_2D);
    }

    @Override
    protected void updateEntity(double tfp) {
        if (((UserData) body.getUserData()).bool == true) {
            ((UserData) body.getUserData()).bool = false;
            destroyed = true;
        }
    }

    protected void setRandomPosition() {
        position.set((float) Math.random() * App.ORTHO_SIZE.getWidth(), (float) Math.random() * App.ORTHO_SIZE.getHeight());
        body.setTransform(pxToWorld(new Vec2(position.x, position.y)), (float) Math.toRadians(rotation));
    }

    @Override
    public void setPosition(float x, float y) {
        position.set(x, y);
        body.setTransform(pxToWorld(new Vec2(position.x, position.y)), (float) Math.toRadians(rotation));
    }

    public void deactivate() {
        setPosition(-50, -50);
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
}
