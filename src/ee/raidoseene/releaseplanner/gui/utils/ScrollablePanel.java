/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui.utils;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

/**
 *
 * @author Raido Seene
 */
public class ScrollablePanel extends JPanel {
    
    @Override
    public void setEnabled(boolean enable) {
        super.setEnabled(enable);
        
        for (Component c : this.getComponents()) {
            c.setEnabled(enable);
        }
    }

    public void contentUpdated() {
        Container parent = this.getParent();
        if (parent != null && parent instanceof JViewport) {
            ((JViewport) parent).updateUI();
        }
    }

    public void scrollDown() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    Container parent = ScrollablePanel.this.getParent();
                    if (parent != null && parent instanceof JViewport) {
                        parent = ((JViewport) parent).getParent();

                        if (parent instanceof JScrollPane) {
                            JScrollBar sb = ((JScrollPane) parent).getVerticalScrollBar();

                            if (sb != null) {
                                sb.setValue(sb.getMaximum());
                            }
                        }
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        });
    }

}
