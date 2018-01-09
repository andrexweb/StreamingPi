/*
 * Copyright (C) 2014 Andres Velez Perez
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.avpsoft.streaming.player;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.JPanel;

/**
 *
 * @author andresvelezperez
 */
public class PlayerFrame extends JPanel {

    private Image lastFrame;

    public PlayerFrame() {
        super();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if (lastFrame != null) {
            g.drawImage(lastFrame, 0, 0, lastFrame.getWidth(this), lastFrame.getHeight(this), this);
        }
    }

    public void setImage(Image image) {
        this.lastFrame = image;
        invalidate();
        updateUI();
    }

}
