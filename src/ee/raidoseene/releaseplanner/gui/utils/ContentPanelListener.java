/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui.utils;

import java.util.EventListener;

/**
 *
 * @author risto
 */
public interface ContentPanelListener extends EventListener {
    
    public void contentPanelClosed(ContentPanel source);
    public void contentPanelExpanded(ContentPanel source);
    public void contentPanelCompressed(ContentPanel source);
    
}
