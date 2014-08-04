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
import deamont66.tanks.core.StateManager;
import deamont66.util.InputHandler;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.input.Mouse;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Color;
import org.lwjgl.util.Dimension;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Jirka
 */
public abstract class Component {

    protected List<Component> container;
    private Dimension size;
    private Vector2f position;
    protected Vector2f realPosition;
    private Component parent;
    private boolean visible = true;
    private ActionListener listener = null;
    private boolean hover = false;
    private boolean disabled = false;
    private String command = "";
    private int id = 0;
    private Color foreground = new Color(255, 255, 255);
    private Color background = new Color(255, 0, 0, 0);
    private boolean overflowVisible = true;

    public Component() {
        container = new ArrayList<>();
        position = new Vector2f(0, 0);
        size = new Dimension(0, 0);
        realPosition = new Vector2f(0, 0);
    }

    /**
     * Sets position to new vector2f.
     *
     * @param position new position
     */
    public void setPosition(Vector2f position) {
        this.realPosition.translate(-this.position.x, -this.position.y);
        this.position = position;
        this.realPosition.translate(this.position.x, this.position.y);
    }
    
    /**
     * Sets position to new x, y coordinates.
     * @param x
     * @param y 
     */
    public void setPosition(float x, float y) {
        setPosition(new Vector2f(x, y));
    }

    /**
     * Sets position to center of other Component
     *
     * @param parent component to use
     */
    public void setCenter(Component parent) {
        setPosition(new Vector2f(parent.getWidth() / 2 - getWidth() / 2, parent.getHeight() / 2 - getHeight() / 2));
    }

    /**
     * Gets position.
     *
     * @return Vector2f of position.
     */
    public Vector2f getPosition() {
        return position;
    }

    /**
     * Sets component visible.
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Gets if is component visible.
     *
     * @return
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Gets size of component.
     *
     * @param size
     */
    public void setSize(Dimension size) {
        this.size = size;
    }

    /**
     * Sets size of component.
     *
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        setSize(new Dimension(width, height));
    }

    /**
     * Gets size of component.
     *
     * @return
     */
    public Dimension getSize() {
        return size;
    }

    /**
     * Gets height.
     *
     * @return
     */
    public int getHeight() {
        return size.getHeight();
    }

    /**
     * Gets width.
     *
     * @return
     */
    public int getWidth() {
        return size.getWidth();
    }

    /**
     * Sets listener for click action, in this time can have one component only
     * one listener.
     *
     * @param listener
     */
    public void setListener(ActionListener listener) {
        this.listener = listener;
    }

    /**
     * Returns if is component hovered.
     *
     * @return
     */
    public boolean isHover() {
        return hover;
    }

    /**
     * Returns if is component disabled.
     *
     * @return
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * Sets component to disable or not. If is disabled click action is
     * disabled.
     *
     * @param disabled
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * Sets text command of component.
     *
     * @param command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Gets text command of component.
     *
     * @return
     */
    public String getCommand() {
        return command;
    }

    /**
     * Sets id of component.
     *
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets id of components.
     *
     * @return
     */
    public int getId() {
        return id;
    }

    /**
     * Sets color of background.
     *
     * @param background
     */
    public void setBackground(Color background) {
        this.background = background;
    }

    /**
     * Sets color of foreground.
     *
     * @param foreground
     */
    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    /**
     * Gets actual color of background.
     *
     * @return
     */
    public Color getBackground() {
        return background;
    }

    /**
     * Gets actual color of foreground.
     *
     * @return
     */
    public Color getForeground() {
        return foreground;
    }

    /**
     * Sets overflow visibility (only for testing). Default is false.
     *
     * @param bool
     */
    public void setOverflowVisibility(boolean bool) {
        overflowVisible = bool;
    }

    /**
     * Gets if is overflow visible.
     *
     * @return
     */
    public boolean isOverflowVisible() {
        return overflowVisible;
    }

    /**
     * Paints and updates component and all his childs.
     */
    public void update() {
        if (!isVisible()) {
            return;
        }
        glTranslatef(getPosition().x, getPosition().y, 0);
        paint();
        glTranslatef(-getPosition().x, -getPosition().y, 0);

        InputHandler in = InputHandler.getInputHandler();
        float mouseX = Mouse.getX() / (float) StateManager.getWidth() * App.ORTHO_SIZE.getWidth();
        float mouseY = App.ORTHO_SIZE.getHeight() - Mouse.getY() / (float) StateManager.getHeight() * App.ORTHO_SIZE.getHeight();
        hover = false;
        if (mouseX > realPosition.x && mouseX < realPosition.x + getWidth()) {
            if (mouseY > realPosition.y && mouseY < realPosition.y + getHeight()) {
                hover = true;
                if (in.isMouseButtonReleased(0) && !isDisabled()) {
                    if (listener != null) {
                        listener.actionPerformed(new ActionEvent(this, id, command));
                    }
                }
            }
        }
    }

    /**
     * Paints component and childs components. Don't call this method externally
     * use {@link update()} instead.
     */
    public void paint() {
        if (!overflowVisible) {
            glScissor((int) (realPosition.x / App.ORTHO_SIZE.getWidth() * StateManager.getWidth()), (int) ((App.ORTHO_SIZE.getHeight() - realPosition.y - getHeight()) / App.ORTHO_SIZE.getHeight() * StateManager.getHeight()), (int) ((float) getWidth() / App.ORTHO_SIZE.getWidth() * StateManager.getWidth()), (int) ((float) getHeight() / App.ORTHO_SIZE.getHeight() * StateManager.getHeight()));
            glEnable(GL_SCISSOR_TEST);
        }
        for (Component component : container) {
            component.update();
        }
        if (!overflowVisible) {
            glDisable(GL_SCISSOR_TEST);
        }
    }

    /**
     * Sets parent component.
     *
     * @param parent
     */
    private void setParent(Component parent) {
        this.parent = parent;
    }

    /**
     * Gets parent component.
     *
     * @return
     */
    public Component getParent() {
        return parent;
    }

    /**
     * Adds compoent to child list.
     *
     * @param c component
     */
    public void add(Component c) {
        c.setParent(this);
        c.realPosition.translate(this.realPosition.x, this.realPosition.y);
        for (Component component : c.container) {
            component.realPosition.translate(this.realPosition.x, this.realPosition.y);
        }
        container.add(c);
    }
    
    public void remove(Component c) {
        container.remove(c);
    }

    /**
     * Fills compoment to parent one.
     */
    public void copyAtrFromParent() {
        size.setSize(new Dimension(parent.getSize()));
        position.set(0, 0);
    }

    /**
     * Destroy component and all his child components.
     */
    public void destroy() {
        for (Component component : container) {
            component.destroy();
        }
    }

    /**
     * Clears component container.
     */
    public void clear() {
        container.clear();
    }

    /**
     * Reset realative position. Usefull for repeatative adding of component.
     */
    public void resetRealPosition() {
        if (parent != null) {
            realPosition = new Vector2f(parent.realPosition);
        }
    }
}   
