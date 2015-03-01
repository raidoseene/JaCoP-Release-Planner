/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui.utils;

import java.util.EventListener;

/**
 *
 * @author Raido Seene
 */
public interface ContentPanelListener extends EventListener {
    
    public void contentPanelClosed(ContentPanel source);
    public void contentPanelExpansionChanged(ContentPanel source, boolean expanded);
    public void contentPanelSelectionChanged(ContentPanel source, boolean selected);
    
}
