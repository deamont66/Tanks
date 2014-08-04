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

import deamont66.tanks.core.model.WorldEntity;
import deamont66.tanks.core.model.Player;
import deamont66.util.Box2DWorld;
import static deamont66.util.PhysicsUtils.*;
import deamont66.tanks.utils.UserData;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Jirka
 */
public class Bullet extends Animation {

    private Vector2f startPos;
    private Vector2f destPos;
    private FireBall fireball;
    private boolean explode = false;
    private final Player player;

    /**
     * 
     * @param world Box2D world instance
     * @param speed speed of projectile
     * @param pl player (owner of bullet)
     */
    public Bullet(Box2DWorld world, float speed, Player pl) {
        super(world, speed);
        player = pl;
    }

    /**
     * 
     * @param world
     * @param startPos start position of bullet
     * @param destPos dest position of bullet
     * @param speed
     * @param pl 
     */
    public Bullet(Box2DWorld world, Vector2f startPos, Vector2f destPos, float speed, Player pl) {
        super(world, speed);
        this.startPos = startPos;
        setPosition(startPos.x, startPos.y);
        this.destPos = destPos;
        setSize(20, 20);
        player = pl;
    }

    /**
     * 
     * @param world
     * @param x1 source x
     * @param y1 source y
     * @param x2 desr x
     * @param y2 dest y
     * @param speed
     * @param pl
     */
    public Bullet(Box2DWorld world, float x1, float y1, float x2, float y2, float speed, Player pl) {
        this(world, new Vector2f(x1, y1), new Vector2f(x2, y2), speed, pl);
    }

    @Override
    protected void initEntity() {
        BodyDef boxDef = new BodyDef();
        boxDef.position.set(pxToWorld(position.getX()), pxToWorld(position.getY()));
        boxDef.type = BodyType.DYNAMIC;
        PolygonShape boxShape = new PolygonShape();
        boxShape.setAsBox(pxToWorld(getWidth() / 2), pxToWorld(getHeight() / 2));
        body = world.getWorld().createBody(boxDef);
        body.setFixedRotation(true);
        body.setUserData(new UserData("bullet"));
        body.createFixture(boxShape, 0.1f);
        body.setBullet(true);
        fireball = new FireBall(world);
        Animator.addOnTop(fireball);
    }

    @Override
    protected void renderEntity(boolean useTexture) {
    }

    @Override
    protected void updateEntity(double tfp) {
        if (isEnded()) {
            return;
        }
        ((UserData) body.getUserData()).number = player.id;
        position = new Vector2f(worldToPx(body.getPosition()).x, worldToPx(body.getPosition()).y);
        ((UserData) body.getUserData()).numberFloat = rotation;
        if (!body.isActive()) {
            setEnded(true);
            /*if(((UserData) body.getUserData()).bool) {
                player.setScore(player.getScore() + 1);
            }*/
        }
        if (startPos.x <= destPos.x && destPos.x <= position.x) {
            if (startPos.y <= destPos.y && destPos.y <= position.y) {
//                System.out.print("fine-1 ");
                setEnded(true);
            } else if (startPos.y > destPos.y && destPos.y > position.y) {
//                System.out.print("fine-2 ");
                setEnded(true);
            }

//            System.out.println("ok1");
        } else if (startPos.x > destPos.x && destPos.x > position.x) {
            if (startPos.y <= destPos.y && destPos.y <= position.y) {
//                System.out.print("fine-1 ");
                setEnded(true);
            } else if (startPos.y > destPos.y && destPos.y > position.y) {
//                System.out.print("fine-2 ");
                setEnded(true);
            }
//            System.out.println("ok2");
        }
        if (isEnded() || explode) {
            setEnded(true);
            Explosion ex = new Explosion(world);
            ex.setPosition(position.x, position.y);
            Animator.addOnTop(ex);
            fireball.setEnded(true);
            world.removeBody(body);
        }
        body.setLinearVelocity(new Vec2((float) (Math.cos(Math.toRadians(rotation + 90)) * getSpeed() * tfp), (float) (Math.sin(Math.toRadians(rotation + 90)) * getSpeed() * tfp)));
        fireball.setShadowOffset((float) ((WorldEntity.distanceBetweenPoints(startPos, destPos) / 50 * Math.sin(((WorldEntity.distanceBetweenPoints(startPos, destPos) - WorldEntity.distanceBetweenPoints(position, destPos)) / WorldEntity.distanceBetweenPoints(startPos, destPos) * Math.PI)))));
        fireball.setPosition(position.x, position.y);
    }

    /**
     * Makes bullet explode.
     */
    public void explode() {
        if (explode == false) {
            explode = true;
        }
    }
    
    public int getOwnerId() {
        return player.id;
    }
}
