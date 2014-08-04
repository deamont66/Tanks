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
package deamont66.tanks.core.gui;

import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author JiriSimecek
 */
public class ScorePanel extends Panel {

    private final Label name;
    private final Label score;
    private final Label ping;

    private final int index;

    public ScorePanel() {
        this("", "", "", 0);
    }

    public ScorePanel(String name, String score, String ping, int index) {
        this.name = new Label(name);
        this.score = new Label(score);
        this.ping = new Label(ping);
        this.index = index;
    }

    private boolean initialized = false;

    private void init() {
        setBackground(new Color(0, 0, 0));
        if(index == 0) {
            setBackground(new Color(55, 55, 55));
        }
        setSize(700, 50);
        setPosition(new Vector2f(75, 75 + (75 * index)));

        name.setFontSize(24);
        name.setPosition(new Vector2f(0, 10));
        add(name);
        
        score.setFontSize(24);
        score.setPosition(new Vector2f(700 / 3f, 10));
        add(score);

        ping.setFontSize(24);
        ping.setPosition(new Vector2f((700 / 3f) * 2, 10));
        add(ping);
    }

    @Override
    public void update() {
        super.update();
        if (!initialized) {
            init();
            initialized = true;
        }
    }

    public void setName(String name) {
        this.name.setText(name);
    }
    
    public String getName() {
        return this.name.getText();
    }
    
    public void setScore(String score) {
        this.score.setText(score);
    }
    
    public String getScore() {
        return this.score.getText();
    }
    
    public void setPing(String ping) {
        this.ping.setText(ping);
    }
    
    public String getPing() {
        return this.ping.getText();
    }
}
