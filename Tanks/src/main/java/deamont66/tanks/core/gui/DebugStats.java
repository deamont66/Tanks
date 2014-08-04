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

import deamont66.tanks.network.NetworkClient;
import deamont66.util.FPSCounter;
import org.lwjgl.util.vector.Vector2f;

/**
 * Component for showing current fps and ping values.
 * @author Jirka
 */
public class DebugStats extends Panel {

    private final Label fps;
    private final Label ping;
    private NetworkClient client;

    public DebugStats() {
        this(null);
    }
    
    public DebugStats(NetworkClient client) {
        this.client = client;
        
        fps = new Label();
        fps.setFontSize(24);
        add(fps);
        
        ping = new Label();
        ping.setFontSize(18);
        ping.setPosition(new Vector2f(0, fps.getHeight()));
        add(ping);
    }

    @Override
    public void paint() {
        super.paint();
        
        fps.setText("FPS: " + FPSCounter.getFPS());
        ping.setText("Ping: " + ((client != null) ? client.getPing() : 0));
    }

    /**
     * Sets NetworkClient object for getting ping value.
     * @param client 
     */
    public void setClient(NetworkClient client) {
        this.client = client;
    } 
    
}
