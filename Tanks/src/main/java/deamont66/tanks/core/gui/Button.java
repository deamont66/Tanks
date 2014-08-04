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

import deamont66.tanks.core.App;
import deamont66.util.LWJGLUtils;
import deamont66.util.ResourceLoader;
import deamont66.util.TrueTypeFont;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Color;

/**
 *
 * @author Jirka
 */
public class Button extends Component {

    private Color focusForeground = new Color(255, 255, 255);
    private Color focusBackground = new Color(255, 0, 0, 0);
    private final Color disabledForeground = new Color(200, 200, 200);
    private TrueTypeFont font = null;
    private final float FONT_SIZE = 32f;
    private float scale = 1f;
    private String text;
    private int hoverShift = 2;

    public Button() {
        this("Button");
    }

    public Button(String text) {
        this.text = text;
        setCommand(text);
        font = ResourceLoader.loadFontFromFile("/fonts/CollegiateInsideFLF.ttf", FONT_SIZE);
    }

    /**
     * Sets font for label.
     * @param font 
     */
    public void setFont(TrueTypeFont font) {
        this.font = font;
    }

    /**
     * Sets text of Label.
     * @param text 
     */
    public void setText(String text) {
        this.text = text;
        setCommand(text);
    }

    public String getText() {
        return text;
    }

    /**
     * Sets size of font.
     * @param size 
     */
    public void setFontSize(int size) {
        scale = size / FONT_SIZE;
    }

    public int getFontSize() {
        return (int) (FONT_SIZE * scale);
    }

    public void setFocusBackground(Color focusBackground) {
        this.focusBackground = focusBackground;
    }

    public void setFocusForeground(Color focusForeground) {
        this.focusForeground = focusForeground;
    }

    public Color getFocusBackground() {
        return focusBackground;
    }

    public Color getFocusForeground() {
        return focusForeground;
    }

    /**
     * Sets Label shift when it's under focus. 
     * @param hoverShift 
     */
    public void setHoverShift(int hoverShift) {
        this.hoverShift = hoverShift;
    }

    public int getHoverShift() {
        return hoverShift;
    }

    @Override
    public int getWidth() {
        return (int) (font.getWidth((text == null) ? "null" : text) * scale) + 10;
    }

    @Override
    public int getHeight() {
        return font.getHeight((text == null) ? "null" : text);
    }

    @Override
    public void paint() {
        super.paint();
        int shift = (isHover() && !isDisabled()) ? hoverShift : 0;
        if (isHover() && !isDisabled()) {
            glColor4ub(focusBackground.getRedByte(), focusBackground.getGreenByte(), focusBackground.getBlueByte(), focusBackground.getAlphaByte());
        } else {
            glColor4ub(getBackground().getRedByte(), getBackground().getGreenByte(), getBackground().getBlueByte(), getBackground().getAlphaByte());
        }
        glTranslatef(shift, shift - 5, 0);
        LWJGLUtils.renderQuad(getWidth(), getHeight());
        glTranslatef(-shift, -shift + 5, 0);

        if (isHover() && !isDisabled()) {
            glColor4ub(focusForeground.getRedByte(), focusForeground.getGreenByte(), focusForeground.getBlueByte(), focusForeground.getAlphaByte());
        } else if(isDisabled()){
            glColor4ub(disabledForeground.getRedByte(), disabledForeground.getGreenByte(), disabledForeground.getBlueByte(), disabledForeground.getAlphaByte());
        } else {
            glColor4ub(getForeground().getRedByte(), getForeground().getGreenByte(), getForeground().getBlueByte(), getForeground().getAlphaByte());
        }
        LWJGLUtils.setFontOrtho(App.ORTHO_SIZE.getWidth(), App.ORTHO_SIZE.getHeight());
        glTranslatef(-realPosition.x, -realPosition.y, 0);
        LWJGLUtils.drawString(font, realPosition.x + (getWidth() / 2) + shift, realPosition.y + (font.getHeight((text == null) ? "null" : text) * scale) + shift, (text == null) ? "null" : text, scale, scale, TrueTypeFont.ALIGN_CENTER);
        glTranslatef(realPosition.x, realPosition.y, 0);
        LWJGLUtils.setModelOrtho(App.ORTHO_SIZE.getWidth(), App.ORTHO_SIZE.getHeight());

        glColor4f(1, 1, 1, 1);
    }
}
