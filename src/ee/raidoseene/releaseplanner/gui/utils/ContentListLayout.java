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
 * @author risto
 */
public class ContentListLayout implements LayoutManager {
    
    private static final int VERTICAL_GAP = 10;
    private final Class<? extends Component> type;
    
    public ContentListLayout(Class<? extends Component> fullWidthComponent) {
        this.type = fullWidthComponent;
    }

    @Override
    public void addLayoutComponent(String string, Component cmpnt) {
    }

    @Override
    public void removeLayoutComponent(Component cmpnt) {
    }

    @Override
    public Dimension preferredLayoutSize(Container cntnr) {
        int y = Math.max(0, cntnr.getComponentCount() - 1) * VERTICAL_GAP;
        
        for (Component c : cntnr.getComponents()) {
            y += (c.getPreferredSize().height);
        }

        // TODO: width = cntnr.getWidth()
        Insets insets = cntnr.getInsets();
        return new Dimension(insets.left + 10 + insets.right, insets.top + y + insets.bottom);
    }

    @Override
    public Dimension minimumLayoutSize(Container cntnr) {
        int y = Math.max(0, cntnr.getComponentCount() - 1) * VERTICAL_GAP;
        
        for (Component c : cntnr.getComponents()) {
            y += (c.getMinimumSize().height);
        }

        // TODO: width = cntnr.getWidth()
        Insets insets = cntnr.getInsets();
        return new Dimension(insets.left + 10 + insets.right, insets.top + y + insets.bottom);
    }

    @Override
    public void layoutContainer(Container cntnr) {
        Insets insets = cntnr.getInsets();
        int w = cntnr.getWidth() - insets.left - insets.right;
        int y = insets.top;
        
        for (Component c : cntnr.getComponents()) {
            int h = c.getPreferredSize().height;
            
            if (this.type.isInstance(c)) {
                c.setBounds(insets.left, y, w, h);
            } else {
                int lw = Math.min(c.getPreferredSize().width, w);
                c.setBounds(insets.left, y, lw, h);
            }
            
            y += (h + VERTICAL_GAP);
        }
    }
    
}
