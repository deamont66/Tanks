/*
 *  Copyright (c) 2012, Jiří Protego Šimeček
 *  All rights reserved.
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package deamont66.util;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.lwjgl.util.vector.Vector3f;

/**
 *
 * @author Jiří Šimeček
 */
public class OBJLoader {

    private static String state = "Ready";

    public static String getState() {
        return state;
    }
    private static final Map<String, Model> models = new HashMap<String, Model>();

    /**
     * Create displayList for OpenGL from model instance, return handle for
     * display List
     *
     * @param model
     * @return displayListHandle
     */
    public static int createDisplayList(Model m) {
        if (m.displayList == 0) {
            m.displayList = glGenLists(1);
            glNewList(m.displayList, GL_COMPILE);
            {
                //glColor3f(0.4f, 0.27f, 0.17f);
                //glMaterialf(GL_FRONT, GL_SHININESS, 128.0f);
                glBegin(GL_TRIANGLES);
                for (Face face : m.faces) {
                    Vector3f n1 = m.normals.get((int) face.normal.x - 1);
                    glNormal3f(n1.x, n1.y, n1.z);
                    Vector3f v1 = m.vertices.get((int) face.vertex.x - 1);
                    glVertex3f(v1.x, v1.y, v1.z);
                    Vector3f n2 = m.normals.get((int) face.normal.y - 1);
                    glNormal3f(n2.x, n2.y, n2.z);
                    Vector3f v2 = m.vertices.get((int) face.vertex.y - 1);
                    glVertex3f(v2.x, v2.y, v2.z);
                    Vector3f n3 = m.normals.get((int) face.normal.z - 1);
                    glNormal3f(n3.x, n3.y, n3.z);
                    Vector3f v3 = m.vertices.get((int) face.vertex.z - 1);
                    glVertex3f(v3.x, v3.y, v3.z);
                }
                glEnd();
            }
            glEndList();
        }
        return m.displayList;
    }

    private static FloatBuffer reserveData(int size) {
        FloatBuffer data = BufferUtils.createFloatBuffer(size);
        return data;
    }

    private static float[] asFloats(Vector3f v) {
        return new float[]{v.x, v.y, v.z};
    }

    /**
     * Create VBO buffers from Model instance, return integer array with
     * vboHadles (first index is VertexHandle, second NormalHandle)
     *
     * @param model
     * @return Array of vboVertexHandle and vboNrmalHandle
     */
    public static int[] createVBO(Model model) {
        if (model.vboVertexHandle == 0) {
            model.vboVertexHandle = glGenBuffers();
            model.vboNormalHandle = glGenBuffers();
            FloatBuffer vertices = reserveData(model.faces.size() * 9);
            FloatBuffer normals = reserveData(model.faces.size() * 9);
            for (Face face : model.faces) {
                vertices.put(asFloats(model.vertices.get((int) face.vertex.x - 1)));
                vertices.put(asFloats(model.vertices.get((int) face.vertex.y - 1)));
                vertices.put(asFloats(model.vertices.get((int) face.vertex.z - 1)));
                normals.put(asFloats(model.normals.get((int) face.normal.x - 1)));
                normals.put(asFloats(model.normals.get((int) face.normal.y - 1)));
                normals.put(asFloats(model.normals.get((int) face.normal.z - 1)));
            }
            vertices.flip();
            normals.flip();
            glBindBuffer(GL_ARRAY_BUFFER, model.vboVertexHandle);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
            glVertexPointer(3, GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, model.vboNormalHandle);
            glBufferData(GL_ARRAY_BUFFER, normals, GL_STATIC_DRAW);
            glNormalPointer(GL_FLOAT, 0, 0L);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
        }

        return new int[]{model.vboVertexHandle, model.vboNormalHandle};
    }

    public synchronized static Model loadModel(File f) throws IOException {
        if (!models.containsKey(f.getName())) {
            state = "Loading model: " + f.getName();
            Model m = null;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(f));
                m = new Model();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("v ")) {
                        float x = Float.valueOf(line.split(" ")[1]);
                        float y = Float.valueOf(line.split(" ")[2]);
                        float z = Float.valueOf(line.split(" ")[3]);
                        m.vertices.add(new Vector3f(x, y, z));
                    } else if (line.startsWith("vn ")) {
                        float x = Float.valueOf(line.split(" ")[1]);
                        float y = Float.valueOf(line.split(" ")[2]);
                        float z = Float.valueOf(line.split(" ")[3]);
                        m.normals.add(new Vector3f(x, y, z));
                    } else if (line.startsWith("vt ")) {
                    } else if (line.startsWith("f ")) {
                        Vector3f vertexIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[0]),
                                Float.valueOf(line.split(" ")[2].split("/")[0]),
                                Float.valueOf(line.split(" ")[3].split("/")[0]));
                        Vector3f normalIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("/")[2]),
                                Float.valueOf(line.split(" ")[2].split("/")[2]),
                                Float.valueOf(line.split(" ")[3].split("/")[2]));
                        m.faces.add(new Face(vertexIndices, normalIndices));
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                }
                state = "Ready";
            } catch (FileNotFoundException e) {
                state = "Model " + f.getName() + " not found.";
                throw e;
            } catch (IOException e) {
                state = "Model " + f.getName() + " cannot be loaded. " + e.getLocalizedMessage();
                throw e;
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
            models.put(f.getName(), m);
        }

        return models.get(f.getName());
    }
}
