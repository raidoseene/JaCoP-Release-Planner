/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 *
 * @author Raido Seene
 */
public final class ExtendableLayout implements LayoutManager {

    public static final boolean VERTICAL = true;
    public static final boolean HORIZONTAL = false;
    private final boolean orientation;
    private final int gap;

    public ExtendableLayout(boolean orientation, int gap) {
        this.orientation = orientation;
        this.gap = Math.max(0, gap);
    }

    @Override
    public void addLayoutComponent(String string, Component cmpnt) {
    }

    @Override
    public void removeLayoutComponent(Component cmpnt) {
    }

    @Override
    public Dimension preferredLayoutSize(Container cntnr) {
        Insets is = cntnr.getInsets();
        int width = is.left + is.right;
        int height = is.top + is.bottom;

        if (this.orientation) { // vertical
            height += Math.max(0, (cntnr.getComponentCount() - 1) * this.gap);
            for (Component c : cntnr.getComponents()) {
                Dimension d = c.getPreferredSize();
                width = Math.max(width, d.width);
                height += d.height;
            }
        } else { // horizontal
            width += Math.max(0, (cntnr.getComponentCount() - 1) * this.gap);
            for (Component c : cntnr.getComponents()) {
                Dimension d = c.getPreferredSize();
                height = Math.max(height, d.height);
                width += d.width;
            }
        }

        return new Dimension(width, height);
    }

    @Override
    public Dimension minimumLayoutSize(Container cntnr) {
        Insets is = cntnr.getInsets();
        int width = is.left + is.right;
        int height = is.top + is.bottom;

        if (this.orientation) { // vertical
            height += Math.max(0, (cntnr.getComponentCount() - 1) * this.gap);
            for (Component c : cntnr.getComponents()) {
                Dimension d = c.getMinimumSize();
                width = Math.max(width, d.width);
                height += d.height;
            }
        } else { // horizontal
            width += Math.max(0, (cntnr.getComponentCount() - 1) * this.gap);
            for (Component c : cntnr.getComponents()) {
                Dimension d = c.getMinimumSize();
                height = Math.max(height, d.height);
                width += d.width;
            }
        }

        return new Dimension(width, height);
    }

    @Override
    public void layoutContainer(Container cntnr) {
        Insets is = cntnr.getInsets();
        
        if (this.orientation) { // vertical
            int w = cntnr.getWidth() - is.left - is.right;
            int y = is.top;
            
            for (Component c : cntnr.getComponents()) {
                int h = c.getPreferredSize().height;
                c.setBounds(is.left, y, w, h);
                y += (h + this.gap);
            }
        } else { // horizontal
            int h = cntnr.getHeight() - is.top - is.bottom;
            int x = is.left;
            
            for (Component c : cntnr.getComponents()) {
                int w = c.getPreferredSize().width;
                c.setBounds(x, is.top, w, h);
                x += (w + this.gap);
            }
        }
    }

}
