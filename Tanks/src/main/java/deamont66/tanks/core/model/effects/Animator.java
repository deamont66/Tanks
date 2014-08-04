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

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Jirka
 */
public class Animator {

    private static Animator instance;

    private Animator() {
    }

    /**
     * Returns animator.
     * @return 
     */
    private static Animator getAnimator() {
        if (instance == null) {
            instance = new Animator();
        }
        return instance;
    }
    
    private final List<Animation> animations = new ArrayList<Animation>();
    private final List<Animation> animationsOnTop = new ArrayList<Animation>();

    /**
     * Add animation to list of animations.
     * @param a animation
     */
    public static void add(Animation a) {
        getAnimator().animations.add(a);
    }
    /**
     * Add animation to list of animations with flag to be rendered on top after other entities.
     * @param a animation
     */
    public static void addOnTop(Animation a) {
        getAnimator().animationsOnTop.add(a);
    }

    /**
     * Updates animations.
     * @param tfp 
     */
    public static void update(double tfp) {
        List<Animation> list = getAnimator().animations;
        for (int i = 0; i < list.size(); i++) {
            Animation a = list.get(i);
            if (a.isEnded()) {
                list.remove(i);
                i--;
                continue;
            }
            a.update(tfp);
        }
        
        list = getAnimator().animationsOnTop;
        for (int i = 0; i < list.size(); i++) {
            Animation a = list.get(i);
            if (a.isEnded()) {
                list.remove(i);
                i--;
                continue;
            }
            a.update(tfp);
        }
    }

    /**
     * Render animations.
     */
    public static void render() {
        List<Animation> list = getAnimator().animations;
        for (int i = 0; i < list.size(); i++) {
            Animation a = list.get(i);
            a.render();
        }
    }
    
    /**
     * Render animations with flag to be rendered on top.
     */
    public static void renderOnTop() {
        List<Animation> list = getAnimator().animationsOnTop;
        for (int i = 0; i < list.size(); i++) {
            Animation a = list.get(i);
            a.render();
        }
    }
    
    /**
     * Clears all animations.
     */
    public static void clear() {
        getAnimator().animations.clear();
        getAnimator().animationsOnTop.clear();
    }
}
