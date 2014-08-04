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
 */
package deamont66.util;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class NativesLoader {

    public static void loadNatives() {
        try {
            addNativesDir(new File("target/natives").getAbsolutePath()); // this works only for IDE
            addNativesDir(new File(new File(NativesLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent() + "/natives").getAbsolutePath()); // this allow loading natives without IDE
        } catch (Exception ex) {
            Utils.error("Cannot load natives", ex);
            System.exit(1);
        }
    }

    /**
     * This allow only add our path to java.library.path (without calling
     * System.getProperty())
     *
     * @param s - file path of dir
     * @throws IOException
     */
    private static void addNativesDir(String s) throws IOException {
        try {
            // This enables the java.library.path to be modified at runtime
            // From a Sun engineer at http://forums.sun.com/thread.jspa?threadID=707176
            //
            Field field = ClassLoader.class.getDeclaredField("usr_paths"); // načte staré cesty
            field.setAccessible(true);
            String[] paths = (String[]) field.get(null); // uložíme do pole
            for (int i = 0; i < paths.length; i++) {
                if (s.equals(paths[i])) { // zkontroluje zda už novou cestu neobsahuje
                    return;
                }
            }
            String[] tmp = new String[paths.length + 1];
            System.arraycopy(paths, 0, tmp, 0, paths.length);
            tmp[paths.length] = s;
            field.set(null, tmp); // uložíme i s novou cestou
            System.setProperty("java.library.path", System.getProperty("java.library.path") + File.pathSeparator + s); // načteme nové cesty (jen aby getProperty() vracelo pravdivé údaje) 
        } catch (IllegalAccessException e) {
            throw new IOException("Failed to get permissions to set library path");
        } catch (NoSuchFieldException e) {
            throw new IOException("Failed to get field handle to set library path");
        }
    }
}
