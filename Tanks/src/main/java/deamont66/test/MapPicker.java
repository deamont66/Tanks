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
package deamont66.test;

import deamont66.tanks.core.model.Map;
import deamont66.tanks.core.model.map.Brick;
import deamont66.tanks.core.model.map.DeepWater;
import deamont66.tanks.core.model.map.DestroyedBrick;
import deamont66.tanks.core.model.map.DestroyedTree;
import deamont66.tanks.core.model.map.Grass;
import deamont66.tanks.core.model.map.Rock;
import deamont66.tanks.core.model.map.ShallowWater;
import deamont66.tanks.core.model.map.Tree;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author JiriSimecek
 */
public class MapPicker extends JFrame {

    private static final File defaultFolder = new File(System.getProperty("user.home") + File.separator + ".tanks" + File.separator + "maps");
    // private static final File defaultFolder = new File("C:\\Users\\JiriSimecek\\Downloads");
    private File currentFolder = defaultFolder;
    public String selectedMapFile = null;
    private static Image folder = null;
    public volatile boolean done = false;

    private JPanel items;
    private final List<Item> itemsList = new ArrayList<>();

    public MapPicker() throws HeadlessException {

        try {
            folder = ImageIO.read(MapPicker.class.getResourceAsStream("/folder.png"));
        } catch (IOException ex) {
            Logger.getLogger(MapPicker.class.getName()).log(Level.SEVERE, null, ex);
        }

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setTitle("Select map file:");
        getContentPane().setPreferredSize(new Dimension(360, 400));
        pack();
        setLayout(null);
        setLocationRelativeTo(null);
        setResizable(false);

        initGUI();

        setVisible(true);
    }

    private void initGUI() {
        JPanel itemsPanel = new JPanel();
        itemsPanel.setPreferredSize(new Dimension(360, 400 - 34));
        itemsPanel.setSize(360, 400 - 34);

        items = new JPanel();
        items.setLayout(null);

        changeDir(currentFolder);

        JScrollPane scroll = new JScrollPane();
        scroll.setPreferredSize(new Dimension(360, 400 - 34));
        scroll.getViewport().add(items);
        itemsPanel.add(scroll);
        add(itemsPanel);

        JPanel controlPanel = new JPanel();
        controlPanel.setSize(200, 34);
        controlPanel.setLocation(180, 400 - 34);
        add(controlPanel);
        JButton select = new JButton("Select");
        select.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectDone();
            }
        });
        controlPanel.add(select);

        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                done = true;
                dispose();
            }
        });
        controlPanel.add(cancel);
    }
    
    private void selectDone() {
        if(selectedMapFile != null) {
            done = true;
            dispose();
        }
    }

    private void changeDir(File f) {
        if (f.isDirectory()) {
            currentFolder = f;
        }
        items.removeAll();
        itemsList.clear();
        File[] listFiles = currentFolder.listFiles();
        if (f.getParentFile().getName().equalsIgnoreCase("maps")) {
            File[] copyOfListFiles = listFiles.clone();
            listFiles = new File[copyOfListFiles.length + 1];
            listFiles[0] = f.getParentFile();
            for (int i = 1; i < listFiles.length; i++) {
                listFiles[i] = copyOfListFiles[i - 1];
            }
        }
        for (int i = 0; i < listFiles.length; i++) {
            try {
                Item item = new Item(listFiles[i], getImageByFile(listFiles[i]));
                item.setLocation(((i % 2 == 0) ? 0 : 175) + 10, i / 2 * 55 + 10);
                items.add(item);
                itemsList.add(item);
            } catch (IOException ex) {
            }
        }
        items.setPreferredSize(new Dimension(340, 10 + 55 * (int) Math.floor(listFiles.length / 2f + 0.5f)));
        repaint();
    }

    private void setFocus(Item i) {
        for (Item item : itemsList) {
            if (!i.equals(item)) {
                item.imageLabel.setBorder(null);
            }
        }
        i.imageLabel.setBorder(BorderFactory.createLineBorder(Color.black, 3));
        if (i.itemFile.getName().endsWith(".tmap")) {
            selectedMapFile = i.itemFile.getAbsolutePath().substring(i.itemFile.getAbsolutePath().indexOf(".tanks") + 6 + 5);
        }
    }

    private Image getImageByFile(File f) {
        BufferedImage bufferedImage = new BufferedImage(50, 38, BufferedImage.TYPE_INT_RGB);

        if (f.getName().endsWith(".tmap")) {
            String mapFileName = f.getAbsolutePath().substring(f.getAbsolutePath().indexOf(".tanks") + 6 + 5);
            int[][] loadFromFile = Map.loadFromFile(mapFileName);
            for (int y = 0; y < loadFromFile.length; y++) {
                int[] is = loadFromFile[y];
                for (int x = 0; x < is.length; x++) {
                    int type = is[x];
                    if (type < 0) {
                        type *= -1;
                    }

                    switch (type) {
                        case Brick.ID: {
                            bufferedImage.setRGB(x, y, new Color(115, 53, 53).getRGB());
                            break;
                        }
                        case DeepWater.ID: {
                            bufferedImage.setRGB(x, y, new Color(29, 88, 130).getRGB());
                            break;
                        }
                        case DestroyedBrick.ID: {
                            bufferedImage.setRGB(x, y, new Color(100, 69, 48).getRGB());
                            break;
                        }
                        case DestroyedTree.ID: {
                            bufferedImage.setRGB(x, y, new Color(88, 61, 36).getRGB());
                            break;
                        }
                        case Grass.ID: {
                            bufferedImage.setRGB(x, y, new Color(73, 101, 40).getRGB());
                            break;
                        }
                        case Rock.ID: {
                            bufferedImage.setRGB(x, y, new Color(122, 110, 100).getRGB());
                            break;
                        }
                        case ShallowWater.ID: {
                            bufferedImage.setRGB(x, y, new Color(67, 155, 217).getRGB());
                            break;
                        }
                        case Tree.ID: {
                            bufferedImage.setRGB(x, y, new Color(39, 54, 22).getRGB());
                            break;
                        }
                    }
                }
            }
        } else if (folder != null) {
            return folder;
        }

        return bufferedImage;
    }

    private class Item extends JPanel {

        private final JLabel nameOfItem = new JLabel();
        private final JLabel imageLabel;
        private final File itemFile;

        public Item(File file, Image image) throws IOException {
            this.itemFile = file;
            setLayout(null);
            imageLabel = new JLabel(new ImageIcon(image));
            imageLabel.setLocation(0, 0);
            imageLabel.setSize(50, 38);
            add(imageLabel);
            nameOfItem.setText(file.getName());
            nameOfItem.setLocation(55, 0);
            nameOfItem.setSize(125, 38);
            nameOfItem.setBackground(Color.red);
            add(imageLabel);
            add(nameOfItem);
            setSize(175, 50);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    setFocus(Item.this);
                }

                @Override
                public void mouseClicked(MouseEvent event) {
                    if (event.getClickCount() == 2) {
                        if (itemFile.isDirectory()) {
                            changeDir(itemFile);
                        } else {
                            selectDone();
                        }
                    }
                }
            });
        }
    }

    public static void main(String[] args) {
        MapPicker mapPicker = new MapPicker();
        while(!mapPicker.done) {}
        System.out.println(mapPicker.selectedMapFile);
    }

}
