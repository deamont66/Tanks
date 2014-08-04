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

import deamont66.tanks.core.App;
import deamont66.tanks.core.model.map.Brick;
import deamont66.tanks.core.model.map.DeepWater;
import deamont66.tanks.core.model.map.Grass;
import deamont66.tanks.core.model.map.MapEntity;
import deamont66.tanks.core.model.map.Rock;
import deamont66.tanks.core.model.map.ShallowWater;
import deamont66.tanks.core.model.map.Tree;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author JiriSimecek
 */
public class MapSaveTest {

    public static void main(String[] args) {
        new MapSaveTest();
    }

    public MapSaveTest() {
        run1();
        // run2();
    }

    private void run1() {
        int[][] data = new int[(int) (Math.floor(App.ORTHO_SIZE.getHeight() / MapEntity.SIZE.y + 0.5))][(int) (Math.floor(App.ORTHO_SIZE.getWidth() / MapEntity.SIZE.x))];
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[y].length; x++) {
                if (x > 15 && x < 20 && (y < 10)) {
                    data[y][x] = Tree.ID;
                } else if (x > 20 && x < 24 && (y < 10 || y > 20)) {
                    data[y][x] = Brick.ID;
                } else if (x > 40 && x < 42 && y < 17) {
                    data[y][x] = Rock.ID;
                } else if (x > 39 && x < 44 && y >= 20) {
                    data[y][x] = Rock.ID;
                } else if (x > 37 && x < 44 && y >= 25 && y < 28) {
                    data[y][x] = Rock.ID;
                } else if (x > 37 && x < 44 && y != 18 && y != 17) {
                    data[y][x] = DeepWater.ID;
                } else if (x > 35 && x < 48 && y != 18 && y != 17) {
                    data[y][x] = ShallowWater.ID;
                } else if (x > 32 && x < 48 && (y == 21 || y == 20)) {
                    data[y][x] = ShallowWater.ID;
                } else {
                    data[y][x] = Grass.ID;
                }
            }
        }
        
        saveToFile("test.map", data);
    }
    
    
    private void run2() {
        int[][] data = loadFromFile("empty.map");
        for (int[] data1 : data) {
            for (int j = 0; j < data1.length; j++) {
                System.out.println(data1[j]);
            }
        }
    }
    
    private void saveToFile(String file, int[][] data) {
        try {
            File f = new File(System.getProperty("user.home") + File.separator + ".tanks");
            if (!f.exists()) {
                if (!f.mkdir()) {
                    throw new IOException("Cannot create folder: " + f.getAbsolutePath());
                }
            }
            f = new File(System.getProperty("user.home") + File.separator + ".tanks" + File.separator + "maps");
            if (!f.exists()) {
                if (!f.mkdir()) {
                    throw new IOException("Cannot create folder: " + f.getAbsolutePath());
                }
            }
            File mapFile = new File(f.getAbsolutePath() + File.separator + file);
            if (!mapFile.exists()) {
                if (!mapFile.createNewFile()) {
                    throw new IOException("Cannot create file: " + mapFile.getAbsolutePath());
                }
            }

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(mapFile));
            oos.writeObject(data);
            oos.flush();
            oos.close();

        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    private int[][] loadFromFile(String file) {
        int[][] data = null;
        try {
            File f = new File(System.getProperty("user.home") + File.separator + ".tanks");
            if (!f.exists()) {
                if (!f.mkdir()) {
                    throw new IOException("Cannot create folder: " + f.getAbsolutePath());
                }
            }
            f = new File(System.getProperty("user.home") + File.separator + ".tanks" + File.separator + "maps");
            if (!f.exists()) {
                if (!f.mkdir()) {
                    throw new IOException("Cannot create folder: " + f.getAbsolutePath());
                }
            }
            
            File mapFile = new File(f.getAbsolutePath() + File.separator + file);
            if (!mapFile.exists()) {
                if (!mapFile.createNewFile()) {
                    throw new IOException("Cannot create file: " + mapFile.getAbsolutePath());
                }
            }

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(mapFile));
            data = (int[][]) ois.readObject();
            ois.close();

        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        } catch (ClassNotFoundException ex) {
            System.err.println("Problem with loading data from file: " + ex.getLocalizedMessage());
        }
        return data;
    }
}
