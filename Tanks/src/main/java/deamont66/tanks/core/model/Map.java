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
package deamont66.tanks.core.model;

import deamont66.tanks.core.App;
import deamont66.tanks.core.model.map.Brick;
import deamont66.tanks.core.model.map.DeepWater;
import deamont66.tanks.core.model.map.DestroyedBrick;
import deamont66.tanks.core.model.map.DestroyedTree;
import deamont66.tanks.core.model.map.Grass;
import deamont66.tanks.core.model.map.MapEntity;
import deamont66.tanks.core.model.map.Rock;
import deamont66.tanks.core.model.map.ShallowWater;
import deamont66.tanks.core.model.map.Tree;
import deamont66.util.Box2DWorld;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author JiriSimecek
 */
public class Map {

    private Box2DWorld world = null;
    private String name = "";

    private final MapEntity[][] tiles = new MapEntity[(int) (Math.floor(App.ORTHO_SIZE.getHeight() / MapEntity.SIZE.y + 0.5))][(int) (Math.floor(App.ORTHO_SIZE.getWidth() / MapEntity.SIZE.x))];

    /**
     * Loads map from file, set map's name by {@link #setFileName(String)}.
     */
    public void load() {
        if (!mapExist(name)) {
            name = "/default/default.tmap";
        }
        int[][] data = loadFromFile(name);
        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[y].length; x++) {
                if (tiles[y][x] != null) {
                    tiles[y][x].destroy();
                }
                if (data[y][x] < 0) {
                    data[y][x] *= -1;
                    tiles[y][x] = createEntityByID(data[y][x], x, y);
                    tiles[y][x].setSpawnPlace(true);
                } else {
                    tiles[y][x] = createEntityByID(data[y][x], x, y);
                }
            }
        }
    }

    /**
     * Takes entity id and return {@link MapEntity# MapEntity} object.
     *
     * @param id entity id (if not valid return null)
     * @param x position x in map grid
     * @param y position y in map grid
     * @return map entity object
     */
    public MapEntity createEntityByID(int id, int x, int y) {
        switch (id) {
            case Brick.ID: {
                return new Brick(world, this, x, y);
            }
            case DeepWater.ID: {
                return new DeepWater(world, this, x, y);
            }
            case DestroyedBrick.ID: {
                return new DestroyedBrick(world, this, x, y);
            }
            case DestroyedTree.ID: {
                return new DestroyedTree(world, this, x, y);
            }
            case Grass.ID: {
                return new Grass(world, this, x, y);
            }
            case Rock.ID: {
                return new Rock(world, this, x, y);
            }
            case ShallowWater.ID: {
                return new ShallowWater(world, this, x, y);
            }
            case Tree.ID: {
                return new Tree(world, this, x, y);
            }
        }
        return null;
    }

    public Vector2f getRandomSpawnPlace() {
        List<Vector2f> vecs = new ArrayList<>();
        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[y].length; x++) {
                if (tiles[y][x] != null && tiles[y][x].isSpawnPlace()) {
                    vecs.add(new Vector2f(x * MapEntity.SIZE.x, y * MapEntity.SIZE.y));
                }
            }
        }
        if (vecs.isEmpty()) {
            System.out.println("empty");
            // default place is in te middle of display/map
            vecs.add(new Vector2f(App.ORTHO_SIZE.getHeight() / 2, App.ORTHO_SIZE.getWidth() / 2));
        }
        return vecs.get((int) (Math.random() * vecs.size()));
    }

    /**
     * Returns map entity name by id.
     *
     * @param id
     * @return name (description)
     */
    public static String getEntityNameByID(int id) {
        switch (id) {
            case Brick.ID: {
                return "Brick";
            }
            case DeepWater.ID: {
                return "DeepWater";
            }
            case DestroyedBrick.ID: {
                return "DestroyedBrick";
            }
            case DestroyedTree.ID: {
                return "DestroyedTree";
            }
            case Grass.ID: {
                return "Grass";
            }
            case Rock.ID: {
                return "Rock";
            }
            case ShallowWater.ID: {
                return "ShallowWater";
            }
            case Tree.ID: {
                return "Tree";
            }
        }
        return null;
    }

    /**
     * Revers funtion to {@link #getEntityNameByID(int)}
     *
     * @param name
     * @return id
     */
    public static int getEntityIDbyName(String name) {
        switch (name) {
            case "Brick": {
                return Brick.ID;
            }
            case "DeepWater": {
                return DeepWater.ID;
            }
            case "DestroyedBrick": {
                return DestroyedBrick.ID;
            }
            case "DestroyedTree": {
                return DestroyedTree.ID;
            }
            case "Grass": {
                return Grass.ID;
            }
            case "Rock": {
                return Rock.ID;
            }
            case "ShallowWater": {
                return ShallowWater.ID;
            }
            case "Tree": {
                return Tree.ID;
            }
        }
        return 0;
    }

    /**
     * Sets map filename for load.
     *
     * @param name
     */
    public void setFileName(String name) {
        this.name = name;
    }

    /**
     * Return last saved filename.
     *
     * @return
     */
    public String getFileName() {
        return name;
    }

    /**
     * Sets world engine {@link Box2DWorld}.
     *
     * @param world
     */
    public void setWorld(Box2DWorld world) {
        this.world = world;
    }

    /**
     * Returns mapEntity from tilemap by given position
     *
     * @param x
     * @param y
     * @return tile
     */
    public MapEntity getMapEntity(int x, int y) {
        if (!isInBounds(x, y)) {
            throw new IllegalArgumentException("Out of bounds");
        }
        return tiles[y][x];
    }

    /**
     * Set newEntity to the tilemap to given position. Calls update for all
     * neigbours of tile. Calls destroy for old entity if set.
     *
     * @param x position in tilemap
     * @param y position in tilemap
     * @param newEntity new map entity
     */
    public void updateMap(int x, int y, MapEntity newEntity) {
        if (newEntity != null) {
            tiles[y][x].destroy();
            tiles[y][x] = newEntity;
        }
        if (y > 0) {
            tiles[y - 1][x].updateTile();
        }
        if (y < tiles.length - 1) {
            tiles[y + 1][x].updateTile();
        }
        if (x > 0) {
            tiles[y][x - 1].updateTile();
        }
        if (x < tiles[y].length - 1) {
            tiles[y][x + 1].updateTile();
        }
        if (y < tiles.length - 1 && x < tiles[y].length - 1) {
            tiles[y + 1][x + 1].updateTile();
        }
        if (y > 0 && x < tiles[y].length - 1) {
            tiles[y - 1][x + 1].updateTile();
        }
        if (y < tiles.length - 1 && x > 0) {
            tiles[y + 1][x - 1].updateTile();
        }
        if (y > 0 && x > 0) {
            tiles[y - 1][x - 1].updateTile();
        }
    }

    /**
     * Returns true if is position in bounds of tilemap size.
     *
     * @param x position
     * @param y position
     * @return true if is in bounds
     */
    public boolean isInBounds(int x, int y) {
        if (y < 0 || y >= tiles.length) {
            return false;
        } else if (x < 0 || x >= tiles[y].length) {
            return false;
        }
        return true;
    }

    /**
     * Returns array of mapEntities.
     *
     * @return tiles
     */
    public MapEntity[][] getTiles() {
        return tiles;
    }

    /**
     * Saves data array of map ids to file.
     *
     * @param file
     * @param data
     */
    public static void saveToFile(String file, int[][] data) {
        try {
            File mapFile = new File(App.getMapFolderFile().getAbsolutePath() + file);
            if (!mapFile.exists()) {
                if (!mapFile.createNewFile()) {
                    throw new IOException("Cannot create file: " + mapFile.getAbsolutePath());
                }
            }

            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(mapFile));
            oos.writeObject(data);
            oos.flush();
            oos.close();
            System.out.println(file + " saved successfully.");
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        }
    }

    /**
     * Loads data array of map ids from file.
     *
     * @param file
     * @return array of ids
     */
    public static int[][] loadFromFile(String file) {
        int[][] data = null;
        try {
            File mapFile = new File(App.getMapFolderFile().getAbsolutePath() + file);
            if (!mapFile.exists()) {
                throw new IOException("File not found: " + mapFile.getAbsolutePath());
            }

            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(mapFile));
            data = (int[][]) ois.readObject();
            ois.close();
            System.out.println(file + " loaded successfully.");
        } catch (IOException ex) {
            System.err.println(ex.getLocalizedMessage());
        } catch (ClassNotFoundException ex) {
            System.err.println("Problem with loading data from file: " + ex.getLocalizedMessage());
        }
        return data;
    }

    /**
     * Saves current map to file named name.
     *
     * @param name filename
     */
    public void save(String name) {
        int[][] data = new int[tiles.length][tiles[0].length];
        for (int y = 0; y < data.length; y++) {
            for (int x = 0; x < data[y].length; x++) {
                data[y][x] = getEntityIDbyName(tiles[y][x].getClass().getSimpleName()) * (tiles[y][x].isSpawnPlace() ? -1 : 1);
            }
        }
        saveToFile(name, data);
    }

    /**
     * Loads map from file called name and initialize it.
     *
     * @param name
     */
    public void load(String name) {
        int[][] data = loadFromFile(name);
        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[y].length; x++) {
                if (tiles[y][x] != null) {
                    tiles[y][x].destroy();
                }
                if (data[y][x] < 0) {
                    data[y][x] *= -1;
                    tiles[y][x] = createEntityByID(data[y][x], x, y);
                    tiles[y][x].setSpawnPlace(true);
                } else {
                    tiles[y][x] = createEntityByID(data[y][x], x, y);
                }
            }
        }
    }

    private static JFileChooser fileChooser = null;

    /**
     * Return file chooser for map loading.
     *
     * @return
     */
    public static JFileChooser getFileChooser() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(System.getProperty("user.home") + File.separator + ".tanks" + File.separator + "maps" + File.separator + "new.tmap"));
            fileChooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.getName().endsWith(".tmap");
                }

                @Override
                public String getDescription() {
                    return "Game maps";
                }
            });
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        return fileChooser;
    }

    /**
     * Checks if map name exist in map folder.
     *
     * @param name
     * @return
     */
    public static boolean mapExist(String name) {
        File mapFile = new File(App.getMapFolderFile().getAbsolutePath() + name);
        return mapFile.exists();
    }
}
