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

import deamont66.util.InputHandler;
import deamont66.util.TrueTypeFont;
import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author JiriSimecek
 */
public class TextField extends Panel {

    private boolean focus = false;
    private Label textLabel;
    private boolean init = false;
    private int style = TrueTypeFont.ALIGN_LEFT;
    private int limit = 100;
    protected int fontSize = 24;

    private boolean numberOnly = false;

    public TextField() {
        this("");
    }

    public TextField(String value) {
        textLabel = new Label();
        textLabel.setFontSize(fontSize);
        textLabel.setForeground(new Color(255, 255, 255, 255));
        textLabel.setText(value);
    }

    private void init() {
        textLabel.setFormat(style);
        if (style == TrueTypeFont.ALIGN_CENTER) {
            textLabel.setPosition(new Vector2f(getWidth() / 2, 10));
        } else if (style == TrueTypeFont.ALIGN_RIGHT) {
            textLabel.setPosition(new Vector2f(getWidth(), 10));
        } else {
            textLabel.setPosition(new Vector2f(0, 10));
        }
        add(textLabel);
        init = true;
    }

    @Override
    public void paint() {
        super.paint();
    }

    @Override
    public void update() {
        if (!init) {
            init();
        }

        super.update();
        InputHandler in = InputHandler.getInputHandler();

        if (in.isMouseButtonPressed(0)) {
            if (isHover() && !isDisabled()) {
                focus = true;
            } else {
                focus = false;
            }
        }

        if (focus) {
            if (in.isKeyPressed(InputHandler.KEY_BACK) && getValue().length() > 0) {
                setValue(getValue().substring(0, getValue().length() - 1));
            }
            if (getValue().length() < limit) {
                String string = in.getWrittenText();
                append(filterText(string));
            }
            setBackground(new Color(0, 0, 0, 255));
        } else {
            setBackground(new Color(0, 0, 0, 127));
        }

    }

    protected String filterText(String text) {
        if (numberOnly) {
            char[] chars = text.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                if (!Character.isDigit(chars[i])) {
                    text = text.replace(chars[i] + "", "");
                }
            }
        }
        return text;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public String getValue() {
        return textLabel.getText();
    }

    public void setValue(String value) {
        textLabel.setText(value);
    }

    public void setCharacterLimit(int limit) {
        this.limit = limit;
    }

    public void append(String text) {
        setValue(getValue() + text);
    }

    public void setNumberOnly(boolean numberOnly) {
        this.numberOnly = numberOnly;
    }

    public boolean isNumberOnly() {
        return numberOnly;
    }
}
