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
package deamont66.util;

import java.net.URL;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecWav;
import paulscode.sound.libraries.LibraryLWJGLOpenAL;

/**
 *
 * @author JiriSimecek
 */
public class SoundHandler {

    private static SoundHandler instance = null;
    
    public static final String soundPackage = "/sound/"; 

    private final SoundSystem sound;

    private SoundHandler() {
        initializate();
        sound = new SoundSystem();
    }

    public static SoundHandler getInstance() {
        if (instance == null) {
            instance = new SoundHandler();
        }
        return instance;
    }

    private void initializate() {
        try {
            SoundSystemConfig.addLibrary(LibraryLWJGLOpenAL.class);
        } catch (SoundSystemException e) {
            System.err.println("Error linking with the LibraryLWJGLOpenAL plug-in: " + e.getLocalizedMessage());
        }
        try {
            SoundSystemConfig.setCodec("wav", CodecWav.class);
        } catch (SoundSystemException e) {
            System.err.println("Error linking with the CodecWav plug-in: " + e.getLocalizedMessage());
        }
    }

    public void createSource(String filename, String identifier) {
        URL url = getClass().getResource("/sounds/" + filename);
        sound.setListenerPosition(1280 / 2, 720 / 2, 0);
        sound.newSource(false, identifier, url, filename, false, 0, 0, 0,
                SoundSystemConfig.ATTENUATION_NONE, SoundSystemConfig.getDefaultRolloff());
    }
    

    public void createSteamSource(String filename, String identifier) {
        URL url = getClass().getResource("/sounds/" + filename);
        sound.setListenerPosition(1280 / 2, 720 / 2, 0);
        sound.newStreamingSource(false, identifier, url, filename, false, 0, 0, 0,
                SoundSystemConfig.ATTENUATION_NONE, SoundSystemConfig.getDefaultRolloff());
    }

    public void removeSource(String sourcename) {
        sound.removeSource(sourcename);
    }

    public void setPosition(String sourcename, float x, float y, float z) {
        sound.setPosition(sourcename, x, y, z);
    }

    public void play(String sourcename) {
        sound.play(sourcename);
    }

    public void pause(String sourcename) {
        sound.pause(sourcename);
    }

    public void stop(String sourcename) {
        sound.stop(sourcename);
    }
    
    public boolean isPlaying(String sourcename) {
        if(sourcename == null) {
            return sound.playing();
        }
        return sound.playing(sourcename);
    } 

    public SoundSystem getSoundSystem() {
        return sound;
    }
    
    public static void cleanUp() {
        getInstance().sound.cleanup();
    }
}
