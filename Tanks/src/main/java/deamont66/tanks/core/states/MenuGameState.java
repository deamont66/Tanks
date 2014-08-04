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
package deamont66.tanks.core.states;

import deamont66.tanks.core.AbstractGameState;
import deamont66.tanks.core.App;
import deamont66.tanks.core.StateManager;
import deamont66.tanks.core.gui.*;
import deamont66.tanks.core.model.Map;
import deamont66.tanks.utils.GameStateData;
import deamont66.tanks.utils.Settings;
import deamont66.test.MapPicker;
import deamont66.util.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author Jirka
 */
public class MenuGameState extends AbstractGameState {

    private Label fps;
    private int background1;

    // information for next GameState
    GameStateData gameData;
    private String map = "/default/default.tmap";
    // sub panels variabless
    private final List<Panel> subPanels = new ArrayList<>();

    @Override
    protected void initDisplay() {
        try {
            StateManager.setDisplayMode();
            Display.setVSyncEnabled(Settings.getInstance().getVSync());
            Display.create();
        } catch (LWJGLException ex) {
            Utils.error("Cannot create display", ex);
            Display.destroy();
            System.exit(1);
        }
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
        background1 = ResourceLoader.glLoadPNG("/menu/background.png");
    }

    @Override
    protected void initGUI() {
        mainComponent.setBackground(new Color(255, 255, 255, 255));
        mainComponent.setTexture(background1);

        initSubPanels();

        // <editor-fold defaultstate="collapsed" desc="main menu buttons inicialization">
        Button butt = new Button("SINGLEPLAYER");
        butt.setPosition(new Vector2f(250 - butt.getWidth() / 2, 650));
        //butt.setFont(ResourceLoader.loadFontFromFile("/fonts/starcraft.ttf", 32f));
        butt.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] names = new String[1];
                names[0] = "Player";
                gameData = new GameStateData("LocalGameState", map, 0, false, names);
                showSubmenu("selectmap");
            }
        });
        add(butt);

        butt = new Button("HOT SEAT");
        butt.setPosition(new Vector2f(250 - butt.getWidth() / 2, 700));
        //butt.setFont(ResourceLoader.loadFontFromFile("/fonts/starcraft.ttf", 32f));
        butt.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSubmenu("localgame");
            }
        });
        add(butt);

        butt = new Button("MULTIPLAYER");
        butt.setPosition(new Vector2f(250 - butt.getWidth() / 2, 750));
        butt.setDisabled(false);
        //butt.setFont(ResourceLoader.loadFontFromFile("/fonts/starcraft.ttf", 32f));
        butt.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameData = new GameStateData("127.0.0.1", "Player", 7777, false, null);
                StateManager.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        StateManager.createState("NetworkDiscoverGameState");
                    }
                });
            }
        });
        add(butt);

        butt = new Button("MAP EDITOR");
        butt.setPosition(new Vector2f(250 - butt.getWidth() / 2, 800));
        butt.setDisabled(false);
        //butt.setFont(ResourceLoader.loadFontFromFile("/fonts/starcraft.ttf", 32f));
        butt.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StateManager.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        StateManager.createState("MapEditorGameState");
                    }
                });
            }
        });
        add(butt);

        butt = new Button("SETTINGS");
        butt.setPosition(new Vector2f(250 - butt.getWidth() / 2, 850));
        butt.setDisabled(true);
        //butt.setFont(ResourceLoader.loadFontFromFile("/fonts/starcraft.ttf", 32f));
        butt.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getActionCommand());
            }
        });
        add(butt);

        butt = new Button("EXIT");
        butt.setPosition(new Vector2f(250 - butt.getWidth() / 2, 900));
        //butt.setFont(ResourceLoader.loadFontFromFile("/fonts/starcraft.ttf", 32f));
        butt.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSubmenu("exit");
            }
        });
        add(butt);
        // </editor-fold>

        fps = new Label();
        fps.setFontSize(24);
        fps.setText("Loading...");
        add(fps);
    }

    @Override
    protected void initModels() {
    }

    @Override
    protected void initFonts() {
        ResourceLoader.loadFontFromFile("/fonts/starcraft.ttf", 32f);
    }

    @Override
    protected void render() {
    }

    @Override
    protected void update(double tfp) {
        InputHandler in = InputHandler.getInputHandler();

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
        fps.setText("FPS: " + FPSCounter.getFPS());
    }

    private void initSubPanels() {
        // <editor-fold defaultstate="collapsed" desc="exitPanel declaration">
        Panel exitPanel = new Panel();
        exitPanel.setCommand("exit");
        exitPanel.setSize(300, 150);
        exitPanel.setCenter(mainComponent);
        exitPanel.setBackground(new Color(0, 0, 0, 175));
        exitPanel.setVisible(false);

        Panel titlePanel = new Panel();
        titlePanel.setBackground(new Color(0, 0, 0));
        titlePanel.setSize(300, 75);

        Label l = new Label();
        l.setText("Do you want exit\nto desktop?");
        l.setFormat(TrueTypeFont.ALIGN_CENTER);
        l.setFontSize(24);
        l.setPosition(new Vector2f(titlePanel.getWidth() / 2, 10));
        l.setForeground(new Color(255, 255, 255));

        Button but = new Button("YES");
        but.setFontSize(26);
        but.setPosition(new Vector2f(40, 100));
        but.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                destroy();
            }
        });

        Button no = new Button("NO");
        no.setFontSize(26);
        no.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideSubmenu("exit");
            }
        });
        no.setPosition(new Vector2f(exitPanel.getWidth() - 40 - no.getWidth(), 100));

        exitPanel.add(no);
        exitPanel.add(but);
        exitPanel.add(titlePanel);
        exitPanel.add(l);
        add(exitPanel);
        subPanels.add(exitPanel);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="localGamePanel declaration">
        Panel localGamePanel = new Panel();
        localGamePanel.setCommand("localgame");
        localGamePanel.setSize(600, 500);
        localGamePanel.setCenter(mainComponent);
        localGamePanel.setBackground(new Color(0, 0, 0, 175));
        localGamePanel.setVisible(false);

        titlePanel = new Panel();
        titlePanel.setBackground(new Color(0, 0, 0));
        titlePanel.setSize(600, 75);
        localGamePanel.add(titlePanel);

        l = new Label();
        l.setText("CREATE NEW LOCAL GAME");
        l.setFormat(TrueTypeFont.ALIGN_CENTER);
        l.setFontSize(24);
        l.setPosition(new Vector2f(titlePanel.getWidth() / 2, 22));
        l.setForeground(new Color(255, 255, 255));
        localGamePanel.add(l);

        l = new Label();
        l.setText("Choose your names:");
        l.setFormat(TrueTypeFont.ALIGN_CENTER);
        l.setFontSize(32);
        l.setPosition(new Vector2f(titlePanel.getWidth() / 2, 100));
        l.setForeground(new Color(255, 255, 255));
        localGamePanel.add(l);

        final TextField textField1 = new TextField("player-1");
        textField1.setCharacterLimit(19);
        textField1.setSize(350, 50);
        textField1.setStyle(TrueTypeFont.ALIGN_CENTER);
        textField1.setPosition(new Vector2f(125, 200));
        localGamePanel.add(textField1);

        final TextField textField2 = new TextField("player-2");
        textField2.setCharacterLimit(19);
        textField2.setSize(350, 50);
        textField2.setStyle(TrueTypeFont.ALIGN_CENTER);
        textField2.setPosition(new Vector2f(125, 275));
        localGamePanel.add(textField2);

        final TextField numberOfFrags = new TextField("10");
        numberOfFrags.setCharacterLimit(3);
        numberOfFrags.setNumberOnly(true);
        numberOfFrags.setDisabled(true);
        numberOfFrags.setSize(64, 50);
        numberOfFrags.setStyle(TrueTypeFont.ALIGN_CENTER);
        numberOfFrags.setPosition(new Vector2f(165 + 185, 341));
        localGamePanel.add(numberOfFrags);

        final Checkbox checkbox = new Checkbox();
        checkbox.setSize(32, 32);
        checkbox.setPosition(new Vector2f(110, 350));
        checkbox.setChangeListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                numberOfFrags.setDisabled(!checkbox.isChecked());
            }
        });
        localGamePanel.add(checkbox);

        l = new Label("End after");
        l.setFontSize(28);
        l.setFormat(TrueTypeFont.ALIGN_LEFT);
        l.setPosition(new Vector2f(150, 350));
        localGamePanel.add(l);

        l = new Label("hits.");
        l.setFontSize(28);
        l.setFormat(TrueTypeFont.ALIGN_LEFT);
        l.setPosition(new Vector2f(420, 350));
        localGamePanel.add(l);

        but = new Button("CREATE");
        but.setFocusBackground(new Color(0, 0, 0, 175));
        but.setFontSize(26);
        but.setPosition(new Vector2f(550 - but.getWidth(), 415));
        but.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] names = new String[2];
                names[0] = textField1.getValue();
                names[1] = textField2.getValue();
                int limit = Integer.parseInt(((numberOfFrags.getValue().length() != 0 && !numberOfFrags.isDisabled()) ? numberOfFrags.getValue() : "0"));
                gameData = new GameStateData("LocalGameState", map, limit, false, names);
                showSubmenu("selectmap");
            }
        });
        localGamePanel.add(but);

        but = new Button("CANCEL");
        but.setFocusBackground(new Color(0, 0, 0, 175));
        but.setFontSize(26);
        but.setPosition(new Vector2f(40, 415));
        but.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hideSubmenu("localgame");
            }
        });
        localGamePanel.add(but);
        add(localGamePanel);
        subPanels.add(localGamePanel);
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="selectMap declaration">
        Panel selectMap = new Panel();
        selectMap.setCommand("selectmap");
        selectMap.setSize(450, 250);
        selectMap.setCenter(mainComponent);
        selectMap.setBackground(new Color(0, 0, 0, 175));
        selectMap.setVisible(false);

        titlePanel = new Panel();
        titlePanel.setBackground(new Color(0, 0, 0));
        titlePanel.setSize(450, 45);
        selectMap.add(titlePanel);

        l = new Label();
        l.setText("Change map:");
        l.setFormat(TrueTypeFont.ALIGN_CENTER);
        l.setFontSize(24);
        l.setPosition(new Vector2f(titlePanel.getWidth() / 2, 10));
        l.setForeground(new Color(255, 255, 255));
        selectMap.add(l);

        Panel panel = new Panel();
        panel.setBackground(new Color(0, 0, 0));
        panel.setPosition(new Vector2f(0, 88));
        panel.setSize(450, 50);
        selectMap.add(panel);

        final Label mapLabel = new Label();
        mapLabel.setText(map);
        mapLabel.setFormat(TrueTypeFont.ALIGN_CENTER);
        mapLabel.setFontSize(24);
        mapLabel.setPosition(new Vector2f(titlePanel.getWidth() / 2, 100));
        mapLabel.setForeground(new Color(255, 255, 255));
        selectMap.add(mapLabel);

        but = new Button("Change");
        but.setFontSize(26);
        but.setPosition(new Vector2f(30, 180));
        but.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String newMap = openMapDialog();
                        if (newMap != null) {
                            map = newMap;
                            gameData.text2 = map;
                            mapLabel.setText(map);
                        }
                    }
                }).start();
            }
        });
        selectMap.add(but);

        but = new Button("START");
        but.setFontSize(26);
        but.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StateManager.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        StateManager.createState(gameData.text1);
                    }
                });
            }
        });
        but.setPosition(new Vector2f(selectMap.getWidth() - 30 - but.getWidth(), 180));
        selectMap.add(but);

        add(selectMap);
        subPanels.add(selectMap);
        // </editor-fold>
// Panel settings = new Panel();

    }

    private void showSubmenu(String name) {
        for (Panel panel : subPanels) {
            if (panel.getCommand().equalsIgnoreCase(name)) {
                panel.setVisible(true);
            } else {
                panel.setVisible(false);
            }
        }
    }

    private void hideSubmenu(String name) {
        for (Panel panel : subPanels) {
            if (panel.getCommand().equalsIgnoreCase(name)) {
                panel.setVisible(false);
            }
        }
    }

    @Override
    protected GameStateData saveData() {
        return gameData;
    }

    @Override
    protected void restoreData(GameStateData data) {
    }

    @Override
    protected void preLoadFonts() {
    }

    @Override
    protected void preLoadTextures() {
    }

    @Override
    protected void preLoadMap() {
    }

    /**
     * Open map dialog and return selected map name.
     *
     * @return mapName
     */
    private String openMapDialog2() {
        // <editor-fold defaultstate="collapsed" desc="map name loading">
        String selectedMap = null;
        JFileChooser fileChooser = Map.getFileChooser();
        Utils.closeWindowWithComponent(fileChooser);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.exists()) {
                JOptionPane.showConfirmDialog(fileChooser, "File doesn't exist", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            } else if (file.getName().endsWith(".tmap")) {
                selectedMap = file.getAbsolutePath().substring(file.getAbsolutePath().indexOf(".tanks" + File.separator + "maps") + ".tanks/maps".length(), file.getAbsolutePath().length()).replace(File.separator, "/");
            } else {
                JOptionPane.showConfirmDialog(fileChooser, "Wrong extension/suffix.", "Error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
            }
        }
        return selectedMap;
        // </editor-fold>
    }
    
    private String openMapDialog() {
        MapPicker mapPicker = new MapPicker();
        while(!mapPicker.done) {}
        return mapPicker.selectedMapFile;
    }
}
