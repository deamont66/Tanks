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
import static deamont66.util.PhysicsUtils.*;
import deamont66.tanks.utils.UserData;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Jirka
 */
public class Block extends Entity {

    public Block(Box2DWorld world) {
        super(world);
    }

    @Override
    protected void initEntity() {
        BodyDef boxDef = new BodyDef();
        boxDef.position.set(pxToWorld(position.getX()), pxToWorld(position.getY()));
        boxDef.type = BodyType.STATIC;
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(pxToWorld(getWidth() / 2), pxToWorld(getHeight() / 2));
        body = world.getWorld().createBody(boxDef);
        body.setFixedRotation(true);
        body.setUserData(new UserData("block"));
        body.createFixture(boxShape, 0.1f);
    }

    @Override
    protected void renderEntity(boolean useTexture) {
        glDisable(GL_TEXTURE_2D);
        glColor3f(1, 1, 1);
        glBegin(GL_QUADS);
        {
            glVertex2f(worldToPx(body.getPosition().x) - getWidth() / 2, worldToPx(body.getPosition().y) - getHeight() / 2);
            glVertex2f(worldToPx(body.getPosition().x) + getWidth() - getWidth() / 2, worldToPx(body.getPosition().y) - getHeight() / 2);
            glVertex2f(worldToPx(body.getPosition().x) + getWidth() - getWidth() / 2, worldToPx(body.getPosition().y) + getHeight() - getHeight() / 2);
            glVertex2f(worldToPx(body.getPosition().x) - getWidth() / 2, worldToPx(body.getPosition().y) + getHeight() - getHeight() / 2);
        }
        glEnd();
    }

    @Override
    protected void updateEntity(double tfp) {
        
    }
    
}
