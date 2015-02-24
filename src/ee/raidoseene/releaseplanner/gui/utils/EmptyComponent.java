/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui.utils;

import java.awt.Component;
import java.awt.Dimension;

/**
 *
 * @author Raido Seene
 */
public class EmptyComponent extends Component {

    public EmptyComponent(Dimension preferredSize) {
        this.setPreferredSize(preferredSize);
    }
    
    public EmptyComponent(int preferredWidth, int preferredHeight) {
        this.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
    }
    
}
