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

import deamont66.tanks.core.model.effects.Animator;
import deamont66.tanks.core.model.effects.Bullet;
import deamont66.tanks.utils.UserData;
import deamont66.util.Box2DWorld;
import deamont66.util.FPSCounter;
import static deamont66.util.PhysicsUtils.worldToPx;
import org.jbox2d.common.Vec2;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author JiriSimecek
 */
public class AutomaticTank extends Tank {

    private long timeOfNextChangeSide = 0;
    private long timeOfNextChangeVertical = 0;
    private boolean left = false;
    private boolean straight = false;
    private boolean forward = false;

    private final Vector2f lastPosition = new Vector2f();

    public AutomaticTank(Box2DWorld world, NPCPlayer player) {
        super(world, player);
    }

    @Override
    protected void updateEntity(double tfp) {
        position = new Vector2f(worldToPx(body.getPosition()).x, worldToPx(body.getPosition()).y);
        rotation = (float) Math.toDegrees(body.getAngle());
        ((UserData) body.getUserData()).numberFloat = rotation;

        if (stop) {
            body.setAngularVelocity(0f);
            body.setLinearVelocity(new Vec2());
            return;
        }

        if (((UserData) body.getUserData()).number2 != 0) {
            if (((UserData) body.getUserData()).number2 < 0) {
                if (((UserData) body.getUserData()).number2 == -1) { // repair
                    player.resetHealth();
                } else if (((UserData) body.getUserData()).number2 == -2) {  // sheild
                    shield = true;
                } else if (((UserData) body.getUserData()).number2 == -3) {  // boost
                    boost = true;
                }
                lastImprovement = FPSCounter.getTime();
                if (player.isPlayer) {
                    player.improvementTaken((int) Math.abs(((UserData) body.getUserData()).number2));
                }
            } else {
                if (!shield) {
                    player.takeDamage(((UserData) body.getUserData()).number2, ((UserData) body.getUserData()).number3);
                }
            }
            ((UserData) body.getUserData()).number2 = 0;
            ((UserData) body.getUserData()).number3 = 0;
        }

        if (FPSCounter.getTime() - lastImprovement > IMPROVEMENT_TIME && (shield || boost)) {
            shield = boost = false;
        }

        boolean fire = (Math.random() > 0.5) && (player.health != 0);
        if (fire && FPSCounter.getTime() - lastBullet > 5000) {
            Vector2f bulletPos = new Vector2f(position);
            bulletPos.translate((float) (Math.cos(Math.toRadians(t_rotation + 90)) * 50), (float) (Math.sin(Math.toRadians(t_rotation + 90)) * 50));
            Vector2f destPos = new Vector2f((float) (Math.cos(Math.toRadians(t_rotation + 90)) * 2000), (float) (Math.sin(Math.toRadians(t_rotation + 90)) * 2000));
            Bullet bullet = new Bullet(world, bulletPos, destPos, 12f, player);
            bullet.setRotation(t_rotation);
            Animator.addOnTop(bullet);
            lastBullet = FPSCounter.getTime();
        }

        // random left/straight/right 1/16/1
        if (timeOfNextChangeSide < FPSCounter.getTime()) {
            straight = Math.random() > 0.8d;
            left = Math.random() > 0.5d;
            timeOfNextChangeSide = (long) (FPSCounter.getTime() + (Math.random() * 800 + 200));
        }
        // random forward/reverse 5/1
        if (timeOfNextChangeVertical < FPSCounter.getTime()) {
            forward = Math.random() > 0.2d;
            timeOfNextChangeVertical = (long) (FPSCounter.getTime() + (Math.random() * 800 + 200));
        }

        // set angular velocity
        if (straight) {
            body.setAngularVelocity(0f);
        } else if (left) {
            body.setAngularVelocity((float) (-2f * tfp));
        } else {
            body.setAngularVelocity((float) (2f * tfp));
        }

        // anti-stuck
        if (WorldEntity.distanceBetweenPoints(lastPosition, position) < 0.02 * tfp) {
            forward = !forward;
        }

        float forceSize = (((UserData) body.getUserData()).number > 0) ? 2f : 4f;

        // set linear velocity
        if (forward) {
            Vec2 force = new Vec2((float) (Math.cos(body.getAngle() + Math.PI / 2)), (float) (Math.sin(body.getAngle() + Math.PI / 2)));
            body.setLinearVelocity(force.mul(forceSize).mul((float) tfp));
        } else {
            Vec2 force = new Vec2((float) (Math.cos(body.getAngle() + Math.PI / 2)), (float) (Math.sin(body.getAngle() + Math.PI / 2)));
            body.setLinearVelocity(force.mul(forceSize).mul((float) tfp).negate());
        }

        t_rotation = getRotation();
        lastPosition.set(position);
    }

}
