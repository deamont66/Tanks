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
package deamont66.util;

import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Jirka
 */
public class MyRectangle {

    private float x, y, w, h;
    private Vector2f point1, point2, point3, point4;

    public MyRectangle(float x, float y, float w, float h) {
        this(x, y, w, h, 0);
    }

    public MyRectangle(float x, float y, float w, float h, float rotation) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;

        point1 = new Vector2f(x, y);
        point2 = new Vector2f(x, y + h);
        point3 = new Vector2f(x + w, y);
        point4 = new Vector2f(x + w, y + h);

        rotate(rotation);
    }

    public float getMinX() {
        return Math.min(point4.x, Math.min(point3.x, Math.min(point1.x, point2.x)));
    }

    public float getMaxX() {
        return Math.max(point4.x, Math.max(point3.x, Math.max(point1.x, point2.x)));
    }

    public float getMinY() {
        return Math.min(point4.y, Math.min(point3.y, Math.min(point1.y, point2.y)));
    }

    public float getMaxY() {
        return Math.max(point4.y, Math.max(point3.y, Math.max(point1.y, point2.y)));
    }

    public float getCenterX() {
        return x + (w / 2);
    }

    public float getCenterY() {
        return y + (h / 2);
    }
    
    public Vector2f getCenter() {
        return new Vector2f(getCenterX(), getCenterY());
    }

    public boolean intersects(MyRectangle r1) {
        if (r1.getMinX() > getMinX() && r1.getMinX() < getMaxX() || r1.getMaxX() < getMaxX() && r1.getMaxX() > getMinX() || r1.getMinY() > getMinY() && r1.getMinY() < getMaxY() || r1.getMaxY() < getMaxY() && r1.getMaxY() > getMinY()) {
            return true;
        }
        return false;
    }

    private void rotate(float rotation) {
        point1 = rotatePoint(point1, getCenter(), rotation);
        point2 = rotatePoint(point2, getCenter(), rotation);
        point3 = rotatePoint(point3, getCenter(), rotation);
        point4 = rotatePoint(point4, getCenter(), rotation);
    }

    private Vector2f rotatePoint(Vector2f point, Vector2f center, float rotation) {
        double r = Math.toRadians(rotation);
        double cosTheta = Math.cos(r);
        double sinTheta = Math.sin(r);

        float newX = (float) (cosTheta * (point.x - center.x)
                - sinTheta * (point.y - center.y) + center.x);
        float newY = (float) (sinTheta * (point.x - center.x)
                + cosTheta * (point.y - center.y) + center.y);
        return new Vector2f(newX, newY);
    }
}
