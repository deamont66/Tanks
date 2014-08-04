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

import deamont66.tanks.core.AbstractGameState;
import deamont66.tanks.core.StateManager;
import deamont66.tanks.core.model.Player;
import deamont66.tanks.core.states.NetworkGameState;
import deamont66.tanks.network.NetworkClient;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector2f;

/**
 *
 * @author JiriSimecek
 */
public class GamePauseMultiplayer extends Panel {

    private boolean initialized = false;
    private final NetworkGameState game;
    private final List<ScorePanel> playerPanel = new ArrayList<ScorePanel>();
    private NetworkClient client;

    public GamePauseMultiplayer(NetworkGameState game) {
        setBackground(new Color(0, 0, 0, 175));
        this.game = game;
    }

    private void init() {
        Button but = new Button("Continue");
        but.setSize(100, 50);
        but.setPosition(new Vector2f(getWidth() - 200 - but.getWidth() / 2, getHeight() - 175));
        but.setListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        add(but);

        but = new Button("Back to menu");
        but.setSize(100, 50);
        but.setPosition(new Vector2f(getWidth() - 200 - but.getWidth() / 2, getHeight() - 100));
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
        });
        add(but);

        playerPanel.add(new ScorePanel("name", "score", "ping", 0));
        for (int i = 0; i < client.getMaxPlayers(); i++) {
            //Player pl = game.getPlayers()[i];
            ScorePanel scorePanel = new ScorePanel("", "", "0 ms", i + 1);
            scorePanel.setVisible(false);
            playerPanel.add(scorePanel);

            for (Panel component : playerPanel) {
                add(component);
            }
        }
    }

    @Override
    public void update() {
        super.update();
        if (!initialized) {
            init();
            initialized = true;
        }

        Player[] players = game.getPlayers();
        for (int i = 1; i < playerPanel.size(); i++) {
            ScorePanel sp = playerPanel.get(i);
            if ((i - 1) < players.length) {
                Player pl = game.getPlayers()[i - 1];
                sp.setName(pl.getName());
                sp.setScore(pl.getScore() + "");
                sp.setPing(pl.getPing() + " ms");
                sp.setVisible(true);
            } else if((i - 1) == players.length) {
                Player pl = game.getLocalPlayer();
                sp.setName(pl.getName());
                sp.setScore(pl.getScore() + "");
                sp.setPing(client.getPing() + " ms");
                sp.setVisible(true);
            } else {
                sp.setVisible(false);
            }
        }
    }

    public void setNetworkClient(NetworkClient client) {
        this.client = client;
    }
}
