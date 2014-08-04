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

/**
 *
 * @author Jirka
 */
public class Label extends Component {

    private TrueTypeFont font = null;
    private final float FONT_SIZE = 32f;
    private float scale = 1f;
    private String text = "";
    private int format = TrueTypeFont.ALIGN_LEFT;

    public Label() {
        font = ResourceLoader.loadFontFromFile("/fonts/CollegiateInsideFLF.ttf", FONT_SIZE);
    }

    public Label(String t) {
        this();
        this.text = t;
    }

    public void setFont(TrueTypeFont font) {
        this.font = font;
    }

    /**
     * Sets format of text aligment.
     *
     * @param format
     * {@link TrueTypeFont#ALIGN_CENTER}, {@link TrueTypeFont#ALIGN_LEFT}, {@link TrueTypeFont#ALIGN_RIGHT}
     */
    public void setFormat(int format) {
        this.format = format;
    }

    /**
     * @return format
     * {@link TrueTypeFont#ALIGN_CENTER}, {@link TrueTypeFont#ALIGN_LEFT}, {@link TrueTypeFont#ALIGN_RIGHT}
     */
    public int getFormat() {
        return format;
    }

    /**
     * Sets size of used font. (default is 32)
     * @param size 
     */
    public void setFontSize(int size) {
        scale = size / FONT_SIZE;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public int getFontSize() {
        return (int) (FONT_SIZE * scale);
    }

    @Override
    public int getWidth() {
        if (super.getWidth() != 0) {
            return super.getWidth();
        }
        return font.getWidth((text == null) ? "null" : text);
    }

    @Override
    public int getHeight() {
        if (super.getHeight() != 0) {
            return super.getHeight();
        }
        return font.getHeight((text == null) ? "null" : text);
    }

    @Override
    public void paint() {
        super.paint();
        if (getBackground().getAlpha() != 0) {
            glColor4ub(getBackground().getRedByte(), getBackground().getGreenByte(), getBackground().getBlueByte(), getBackground().getAlphaByte());
            if (format == TrueTypeFont.ALIGN_LEFT) {
                LWJGLUtils.renderQuad(getWidth(), getHeight());
            } else if (format == TrueTypeFont.ALIGN_CENTER) {
                LWJGLUtils.renderQuad(-getWidth() / 2, 0, getWidth(), getHeight());
            } else if (format == TrueTypeFont.ALIGN_RIGHT) {
                LWJGLUtils.renderQuad(-getWidth(), 0, getWidth(), getHeight());
            }
            glColor4f(1, 1, 1, 1);
        }
        glColor4ub(getForeground().getRedByte(), getForeground().getGreenByte(), getForeground().getBlueByte(), getForeground().getAlphaByte());
        LWJGLUtils.setFontOrtho(App.ORTHO_SIZE.getWidth(), App.ORTHO_SIZE.getHeight());
        glTranslatef(-realPosition.x, -realPosition.y, 0);
        LWJGLUtils.drawString(font, realPosition.x, realPosition.y + (font.getHeight((text == null) ? "null" : text) * scale), (text == null) ? "null" : text, scale, scale, format);
        glTranslatef(realPosition.x, realPosition.y, 0);
        LWJGLUtils.setModelOrtho(App.ORTHO_SIZE.getWidth(), App.ORTHO_SIZE.getHeight());
        glColor4f(1, 1, 1, 1);
    }
}
