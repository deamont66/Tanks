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

import deamont66.util.LWJGLUtils;
import deamont66.util.ResourceLoader;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Color;

/**
 *
 * @author JiriSimecek
 */
public class Checkbox extends Component {

    private boolean checked = false;
    private int texture = -1;
    private ActionListener changeListener;

    public Checkbox() {
        setTexture(ResourceLoader.glLoadPNG("/checkbox.png"));
        setBackground(new Color(255, 255, 255));
        setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checked = !checked;
                if(changeListener != null) changeListener.actionPerformed(new ActionEvent(this, 0, getCommand()));
            }
        });
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public void paint() {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texture);

        if(isHover()) {
            glColor4f(0f, 0f, 0f, 1);
        } else {
            glColor4f(0.15f, 0.15f, 0.15f, 1);
        }
        
        glBegin(GL_QUADS);
        {
            if(!checked) {
                glTexCoord2f(0, 0);
                glVertex2f(0, 0);
                glTexCoord2f(0.5f, 0);
                glVertex2f(getWidth(), 0);
                glTexCoord2f(0.5f, 1);
                glVertex2f(getWidth(), getHeight());
                glTexCoord2f(0, 1);
                glVertex2f(0, getHeight());
            } else {
                glTexCoord2f(0.5f, 0);
                glVertex2f(0.5f, 0);
                glTexCoord2f(1, 0);
                glVertex2f(getWidth(), 0);
                glTexCoord2f(1, 1);
                glVertex2f(getWidth(), getHeight());
                glTexCoord2f(0.5f, 1);
                glVertex2f(0, getHeight());
            }
        }
        glEnd();
        glColor4f(1, 1, 1, 1);

        glDisable(GL_TEXTURE_2D);
        super.paint();
    }  
    
    private void setTexture(int texture) {
        this.texture = texture;
    }
        
    public void setChangeListener(ActionListener changeListener) {
        this.changeListener = changeListener;
    }
}
