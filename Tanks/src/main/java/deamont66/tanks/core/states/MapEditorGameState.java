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
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
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
package deamont66.tanks.core.states;

import deamont66.tanks.core.AbstractGameState;
import deamont66.tanks.core.App;
import deamont66.tanks.core.StateManager;
import deamont66.tanks.core.gui.Button;
import deamont66.tanks.core.gui.DebugStats;
import deamont66.tanks.core.gui.Panel;
import deamont66.tanks.core.model.Map;
import deamont66.tanks.core.model.map.Grass;
import deamont66.tanks.core.model.map.MapEntity;
import deamont66.tanks.utils.GameStateData;
import deamont66.tanks.utils.Settings;
import deamont66.util.ColorUtils;
import deamont66.util.FPSCounter;
import deamont66.util.InputHandler;
import deamont66.util.LWJGLUtils;
import deamont66.util.ResourceLoader;
import deamont66.util.Utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author JiriSimecek
 */
public class MapEditorGameState extends AbstractGameState {

    private final Map map = new Map();

    private DebugStats fps;
    private ToolBar toolBar;
    private Panel exit;
    private int texture;

    private int selectedX, selectedY;
    private int selectedTileID = 2;
    private boolean selectedSpawnMarker = false;
    private int brushSize = 2;
    private final SpawnPlace tank = new SpawnPlace();

    @Override
    protected void initDisplay() {
        try {
            StateManager.setDisplayMode();
            Display.setVSyncEnabled(Settings.getInstance().getVSync());
            Display.create();
            Mouse.setGrabbed(false);
        } catch (LWJGLException ex) {
            Utils.error("Cannot create display", ex);
            Display.destroy();
            System.exit(1);
        }
        setFpsLimit(120);
        FPSCounter.init(60);
    }

    @Override
    protected void initOpenGL() {
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_BLEND);
        glPointSize(6);
        glEnable(GL_POINT_SMOOTH);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        LWJGLUtils.setModelOrtho(App.ORTHO_SIZE.getWidth(), App.ORTHO_SIZE.getHeight());
    }

    @Override
    protected void initTextures() {
        this.texture = ResourceLoader.glLoadPNG("/tiles_new.png");
    }

    @Override
    protected void initFonts() {
    }

    @Override
    protected void initGUI() {
        fps = new DebugStats();
        add(fps);

        toolBar = new ToolBar();
        add(toolBar);

        // <editor-fold defaultstate="collapsed" desc="exit panel">
        exit = new Panel();
        exit.setBackground(ColorUtils.HALF_TRASPARENT_BLACK);
        exit.setSize(mainComponent.getSize());
        exit.setVisible(false);
        add(exit);

        Button but = new Button("Return to editor");
        but.setSize(200, 50);
        but.setPosition(new Vector2f(App.ORTHO_SIZE.getWidth() / 2 - but.getWidth() / 2, App.ORTHO_SIZE.getHeight() / 2 - 100));
        but.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exit.setVisible(false);
            }
        });
        exit.add(but);
        but = new Button("Save map");
        but.setSize(200, 50);
        but.setPosition(new Vector2f(App.ORTHO_SIZE.getWidth() / 2 - but.getWidth() / 2, App.ORTHO_SIZE.getHeight() / 2 - 50));
        but.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveMapDialog();
                exit.setVisible(false);
            }
        });
        exit.add(but);
        but = new Button("Open map");
        but.setSize(200, 50);
        but.setPosition(new Vector2f(App.ORTHO_SIZE.getWidth() / 2 - but.getWidth() / 2, App.ORTHO_SIZE.getHeight() / 2));
        but.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openMapDialog();
                exit.setVisible(false);
            }
        });
        exit.add(but);
        but = new Button("Test map");
        but.setSize(200, 50);
        but.setDisabled(true);
        but.setPosition(new Vector2f(App.ORTHO_SIZE.getWidth() / 2 - but.getWidth() / 2, App.ORTHO_SIZE.getHeight() / 2 + 50));
        but.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 
            }
        });
        exit.add(but);
        but = new Button("Back to menu");
        but.setSize(200, 50);
        but.setPosition(new Vector2f(App.ORTHO_SIZE.getWidth() / 2 - but.getWidth() / 2, App.ORTHO_SIZE.getHeight() / 2 + 100));
        but.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StateManager.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        StateManager.createState("MenuGameState");
                    }
                });
            }
        }
        );
        exit.add(but);
        // </editor-fold>        
    }

    @Override
    protected void initModels() {
        tank.setSize((int) MapEntity.SIZE.x, (int) MapEntity.SIZE.y);
        tank.setGrassVisible(false);
    }

    @Override
    protected void preLoadFonts() {
    }

    @Override
    protected void preLoadTextures() {
        ResourceLoader.glLoadPNG("/tiles_new.png");
        ResourceLoader.glLoadPNG("/selection_mode.png");
    }

    @Override
    protected void preLoadMap() {
        map.setFileName("/default/empty.tmap");
        map.load();
    }

    @Override
    protected void render() {
        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, texture);

        // tiles
        glBegin(GL_QUADS);
        {
            for (MapEntity[] tileY : map.getTiles()) {
                for (MapEntity tile : tileY) {
                    if (!tile.isOnTop()) {
                        tile.render();
                    }
                }
            }

            for (MapEntity[] tileY : map.getTiles()) {
                for (MapEntity tile : tileY) {
                    if (tile.isOnTop()) {
                        tile.render();
                        if (tile.isSpawnPlace()) {

                        }
                    }
                }
            }

        }
        glEnd();

        glDisable(GL_TEXTURE_2D);
        for (MapEntity[] tileY : map.getTiles()) {
            for (MapEntity tile : tileY) {
                if (tile.isSpawnPlace()) {
                    glPushMatrix();
                    glTranslatef(tile.getX() - tile.getWidth() / 2, tile.getY() - tile.getHeight() / 2, 0);
                    tank.paint();
                    glPopMatrix();
                }
            }
        }

        glColor4f(0f, 0f, 0f, 0.4f);
        LWJGLUtils.renderQuad(selectedX * MapEntity.SIZE.getX(), selectedY * MapEntity.SIZE.getY(), MapEntity.SIZE.getX() * brushSize, MapEntity.SIZE.getY() * brushSize);
        if (selectedSpawnMarker) {
            glColor4f(1f, 1f, 1f, 0.8f);
            glPushMatrix();
            glTranslatef(selectedX * MapEntity.SIZE.getX(), selectedY * MapEntity.SIZE.getY(), 0);
            tank.paint();
            glPopMatrix();
        }
        glColor4f(1f, 1f, 1f, 1f);
    }

    @Override
    protected void update(double tfp) {
        InputHandler in = InputHandler.getInputHandler();

        for (MapEntity[] tileY : map.getTiles()) {
            for (MapEntity tile : tileY) {
                tile.update(tfp);
            }
        }

        // pause
        if (in.isKeyPressed(InputHandler.KEY_ESCAPE)) {
            exit.setVisible(!exit.isVisible());
        }
        // fullscreen
        if (in.isKeyPressed(InputHandler.KEY_F11)) {
            StateManager.invokeLater(new Runnable() {
                @Override
                public void run() {
                    StateManager.setFullscreen(!StateManager.isFullscreen());
                    StateManager.resetState();
                }
            });
        }
        // screenshot
        if (in.isKeyPressed(InputHandler.KEY_F2)) {
            LWJGLUtils.saveScreenshot(new SimpleDateFormat("yyyy-MM-dd hh-mm-ss").format(new Date()) + " " + (int) (Math.random() * 99));
        }
        if (exit.isVisible()) {
            return;
        }

        // selected tile
        selectedX = (int) Math.floor(in.getMouseX() / MapEntity.SIZE.getX());
        selectedY = (int) Math.floor(in.getMouseY() / MapEntity.SIZE.getY());

        // toolbar hiding
        if (!in.isMouseButtonDown(0) && in.getMouseX() > toolBar.getPosition().x && in.getMouseX() < toolBar.getPosition().x + toolBar.getWidth() && in.getMouseY() < toolBar.getHeight()) {
            if (toolBar.getHeight() < (int) (MapEntity.SIZE.y * 2 + 10)) {
                toolBar.setHeight((int) (toolBar.getHeight() + 1));
            }
        } else {
            if (toolBar.getHeight() > 10) {
                toolBar.setHeight((int) (toolBar.getHeight() - 1));
            }
        }
        // left click
        if (in.isMouseButtonDown(0) && ((in.getMouseX() < toolBar.getPosition().x || in.getMouseX() > toolBar.getPosition().x + toolBar.getWidth() || !(in.getMouseY() < toolBar.getHeight())))) {
            for (int i = 0; i < brushSize; i++) {
                for (int j = 0; j < brushSize; j++) {
                    if (map.isInBounds(selectedX + i, selectedY + j) && !map.getMapEntity(selectedX + i, selectedY + j).isTile(Map.getEntityNameByID(selectedTileID))) {
                        if (selectedSpawnMarker) {
                            map.getMapEntity(selectedX + i, selectedY + j).setSpawnPlace(true);
                        } else {
                            map.updateMap(selectedX + i, selectedY + j, map.createEntityByID(selectedTileID, selectedX + i, selectedY + j));
                        }
                    }
                }
            }
        }
        // right click
        if (in.isMouseButtonDown(1) && ((in.getMouseX() < toolBar.getPosition().x || in.getMouseX() > toolBar.getPosition().x + toolBar.getWidth() || !(in.getMouseY() < toolBar.getHeight())))) {
            for (int i = 0; i < brushSize; i++) {
                for (int j = 0; j < brushSize; j++) {
                    if (map.isInBounds(selectedX + i, selectedY + j) && !map.getMapEntity(selectedX + i, selectedY + j).isTile(Map.getEntityNameByID(Grass.ID))) {
                        map.updateMap(selectedX + i, selectedY + j, map.createEntityByID(Grass.ID, selectedX + i, selectedY + j));
                        map.getMapEntity(selectedX + i, selectedY + j).setSpawnPlace(false);
                    } else if (map.isInBounds(selectedX + i, selectedY + j)) {
                        map.getMapEntity(selectedX + i, selectedY + j).setSpawnPlace(false);
                    }
                }
            }
        }
        // middle click
        if (in.isMouseButtonPressed(2)) {
            selectedSpawnMarker = false;
            if (brushSize == 1) {
                brushSize = 2;
            } else if (brushSize == 2) {
                brushSize = 3;
            } else if (brushSize > 2) {
                brushSize = 1;
            }
        }
        // tiles by number key
        if (in.isKeyPressed(InputHandler.KEY_1)) {
            setSelectedTileID(1);
            selectedSpawnMarker = false;
        } else if (in.isKeyPressed(InputHandler.KEY_2)) {
            setSelectedTileID(2);
            selectedSpawnMarker = false;
        } else if (in.isKeyPressed(InputHandler.KEY_3)) {
            setSelectedTileID(3);
            selectedSpawnMarker = false;
        } else if (in.isKeyPressed(InputHandler.KEY_4)) {
            setSelectedTileID(4);
            selectedSpawnMarker = false;
        } else if (in.isKeyPressed(InputHandler.KEY_5)) {
            setSelectedTileID(5);
            selectedSpawnMarker = false;
        } else if (in.isKeyPressed(InputHandler.KEY_6)) {
            setSelectedTileID(6);
            selectedSpawnMarker = false;
        } else if (in.isKeyPressed(InputHandler.KEY_7)) {
            setSelectedTileID(7);
            selectedSpawnMarker = false;
        } else if (in.isKeyPressed(InputHandler.KEY_8)) {
            setSelectedTileID(8);
            selectedSpawnMarker = false;
        } else if (in.isKeyPressed(InputHandler.KEY_9)) {
            selectedSpawnMarker = true;
            brushSize = 1;
        }
        // save and open hotkeys
        if (in.isKeyPressed(InputHandler.KEY_S) && in.isKeyDown(InputHandler.KEY_LCONTROL)) {
            saveMapDialog();
        }
        if (in.isKeyPressed(InputHandler.KEY_O) && in.isKeyDown(InputHandler.KEY_LCONTROL)) {
            openMapDialog();
        }
    }

    public void setSelectedTileID(int selectedTileID) {
        this.selectedTileID = selectedTileID;
    }

    private void saveMapDialog() {
        // <editor-fold defaultstate="collapsed" desc="ctrl + s -> save option">
        new Thread(new Runnable() {
            @Override
            public void run() {
                JFileChooser fileChooser = Map.getFileChooser();
                Utils.closeWindowWithComponent(fileChooser);
                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (file.exists()) {
                        if (JOptionPane.showConfirmDialog(fileChooser, "File already exist, do you want replace it?") != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    if (file.getName().endsWith(".tmap")) {
                        map.save(file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(".tanks" + File.separator + "maps") + ".tanks/maps".length(), file.getAbsolutePath().length()));
                        return;
                    }
                    JOptionPane.showConfirmDialog(fileChooser, "Wrong extension/suffix.", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
        // </editor-fold>
    }

    private void openMapDialog() {
        // <editor-fold defaultstate="collapsed" desc="ctrl + o -> open option">
        new Thread(new Runnable() {
            @Override
            public void run() {
                JFileChooser fileChooser = Map.getFileChooser();
                Utils.closeWindowWithComponent(fileChooser);
                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    Utils.windowToFront(Utils.getWindoByComponenet(fileChooser));
                    File file = fileChooser.getSelectedFile();
                    if (!file.exists()) {
                        JOptionPane.showConfirmDialog(fileChooser, "File doesn't exist", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (file.getName().endsWith(".tmap")) {
                        map.load(file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(".tanks" + File.separator + "maps") + ".tanks/maps".length(), file.getAbsolutePath().length()).replace(File.separator, "/"));
                        return;
                    }
                    JOptionPane.showConfirmDialog(fileChooser, "Wrong extension/suffix.", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
                }
            }
        }).start();
        // </editor-fold>
    }

    @Override
    protected GameStateData saveData() {
        return null;
    }

    @Override
    protected void restoreData(GameStateData data) {
    }

    // private classes for toolbar (GUI)
    private class ToolBar extends Panel {

        public ToolBar() {
            setSize((int) (MapEntity.SIZE.x * 2 * 10 + 5 * 10 + 15 + 10), (int) (MapEntity.SIZE.y * 2 + 10));
            setPosition(new Vector2f(App.ORTHO_SIZE.getWidth() / 2 - (int) (MapEntity.SIZE.x * 2 * 10 + 5 * 10 + 10) / 2, 0));
            setBackground(ColorUtils.HALF_TRASPARENT_BLACK);

            Panel p;
            for (int i = 1; i <= 8; i++) {
                p = new TilePanel(i);
                p.setSize((int) MapEntity.SIZE.x * 2, (int) MapEntity.SIZE.y * 2);
                p.setPosition(new Vector2f(10 + (int) (MapEntity.SIZE.x * 2 + 5) * (i - 1), 0));
                add(p);
            }

            p = new SpawnPlace();
            p.setSize((int) MapEntity.SIZE.x * 2, (int) MapEntity.SIZE.y * 2);
            p.setPosition(new Vector2f(20 + (int) (MapEntity.SIZE.x * 2 + 5) * (9 - 1), 0));
            add(p);

            p = new SelectionTool();
            p.setSize((int) MapEntity.SIZE.x * 2, (int) MapEntity.SIZE.y * 2);
            p.setPosition(new Vector2f(20 + (int) (MapEntity.SIZE.x * 2 + 5) * (10 - 1), 0));
            add(p);
        }

        public void setHeight(int height) {
            setSize(getWidth(), height);
        }
    }

    private class SelectionTool extends Panel {

        private final int texture;

        public SelectionTool() {
            this.texture = ResourceLoader.glLoadPNG("/selection_mode.png");
            setListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (brushSize == 1) {
                        brushSize = 2;
                    } else if (brushSize == 2) {
                        brushSize = 3;
                    } else if (brushSize == 3) {
                        brushSize = 1;
                    }
                    selectedSpawnMarker = false;
                }
            });
            System.out.println(texture);
        }

        @Override
        public void paint() {
            super.paint();

            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, texture);
            glColor4f(1f, 1f, 1f, 1f);
            int y = (int) (MapEntity.SIZE.y * 2 + 10) - getParent().getHeight();
            glBegin(GL_QUADS);
            {
                glTexCoord2f((brushSize == 1 || brushSize == 4) ? 0.5f : 0, (brushSize == 3 || brushSize == 4) ? 0.5f : 0);
                glVertex2f(0, -y);
                glTexCoord2f((brushSize == 1 || brushSize == 4) ? 1f : 0.5f, (brushSize == 3 || brushSize == 4) ? 0.5f : 0);
                glVertex2f(getWidth(), -y);
                glTexCoord2f((brushSize == 1 || brushSize == 4) ? 1f : 0.5f, (brushSize == 3 || brushSize == 4) ? 1 : 0.5f);
                glVertex2f(getWidth(), -y + getHeight());
                glTexCoord2f((brushSize == 1 || brushSize == 4) ? 0.5f : 0, (brushSize == 3 || brushSize == 4) ? 1 : 0.5f);
                glVertex2f(0, -y + getHeight());
            }
            glEnd();
            glDisable(GL_TEXTURE_2D);
        }

    }

    private class TilePanel extends Panel {

        private final MapEntity entity;

        public TilePanel(final int tileID) {
            entity = map.createEntityByID(tileID, -2, -2);
            entity.update(1);
            setListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setSelectedTileID(tileID);
                    selectedSpawnMarker = false;
                }
            });
        }

        @Override
        public void paint() {
            super.paint();
            int y = (int) (MapEntity.SIZE.y * 2 + 10) - getParent().getHeight();
            entity.setPosition(new Vector2f(getWidth() / 2, getHeight() / 2 - y));
            entity.setSize(getWidth(), getHeight());
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, texture);
            glBegin(GL_QUADS);
            entity.render();
            glEnd();
            glDisable(GL_TEXTURE_2D);
        }
    }

    private class SpawnPlace extends Panel {

        private final int tileX;
        private final int tileY;
        private final int rotation;
        private final float tileSize;
        private final Grass grass;
        private boolean grassVisible = true;

        public SpawnPlace() {
            tileX = 0;
            tileY = 1;
            rotation = 0;
            tileSize = 1 / 16f;
            grass = new Grass(null, map, -1, -1);
            grass.update(1);

            setListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedSpawnMarker = true;
                    brushSize = 1;
                }
            });
        }

        @Override
        public void paint() {
            super.paint();

            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, texture);
            glColor4f(1f, 1f, 1f, 1f);
            int y = 0;
            if (grassVisible) {
                y = (int) (MapEntity.SIZE.y * 2 + 10) - getParent().getHeight();
                grass.setPosition(new Vector2f(getWidth() / 2, getHeight() / 2 - y));
                grass.setSize(getWidth(), getHeight());
            }
            glBegin(GL_QUADS);
            {
                if (grassVisible) {
                    grass.render();
                }
                glTexCoord2f(getTileTexCoord(0, tileX, tileY)[0], getTileTexCoord(0, tileX, tileY)[1]);
                glVertex2f(0, -y);
                glTexCoord2f(getTileTexCoord(1, tileX, tileY)[0], getTileTexCoord(1, tileX, tileY)[1]);
                glVertex2f(getWidth(), -y);
                glTexCoord2f(getTileTexCoord(2, tileX, tileY)[0], getTileTexCoord(2, tileX, tileY)[1]);
                glVertex2f(getWidth(), -y + getHeight());
                glTexCoord2f(getTileTexCoord(3, tileX, tileY)[0], getTileTexCoord(3, tileX, tileY)[1]);
                glVertex2f(0, -y + getHeight());
            }
            glEnd();
            glDisable(GL_TEXTURE_2D);
        }

        public void setGrassVisible(boolean grass) {
            grassVisible = grass;
        }

        /**
         * Gets texCoord for tiles (enables rotation of tiles)
         *
         * @param i - number of texcoord
         * @param x - x position in tile texture
         * @param y - y position in tile texture
         * @return 2D texCoord for tile
         */
        protected float[] getTileTexCoord(int i, int x, int y) {
            if (i == 0 && rotation == 0 || i == 1 && rotation == 1 || i == 2 && rotation == 2 || i == 3 && rotation == 3) {
                return new float[]{tileSize * x, tileSize * y};
            } else if (i == 1 && rotation == 0 || i == 2 && rotation == 1 || i == 3 && rotation == 2 || i == 0 && rotation == 3) {
                return new float[]{tileSize * (x + 1), tileSize * y};
            } else if (i == 2 && rotation == 0 || i == 3 && rotation == 1 || i == 0 && rotation == 2 || i == 1 && rotation == 3) {
                return new float[]{tileSize * (x + 1), tileSize * (y + 1)};
            } else if (i == 3 && rotation == 0 || i == 0 && rotation == 1 || i == 1 && rotation == 2 || i == 2 && rotation == 3) {
                return new float[]{tileSize * x, tileSize * (y + 1)};
            }
            return null;
        }

    }
}
