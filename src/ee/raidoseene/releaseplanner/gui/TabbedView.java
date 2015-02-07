/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 *
 * @author risto
 */
public class TabbedView extends JPanel {
    
    public TabbedView() {
        this.setLayout(new GridLayout(1, 1));

        JTabbedPane tb = new JTabbedPane();
        this.add(tb);

        FeaturesPanel featp = new FeaturesPanel();
        tb.add(FeaturesPanel.TITLE_STRING, featp);

        ResourcesPanel resp = new ResourcesPanel();
        tb.add(ResourcesPanel.TITLE_STRING, resp);

        ReleasesPanel relp = new ReleasesPanel();
        tb.add(ReleasesPanel.TITLE_STRING, relp);

        StakeholdersPanel stakp = new StakeholdersPanel();
        tb.add(StakeholdersPanel.TITLE_STRING, stakp);

        UrgValPanel urgvp = new UrgValPanel();
        tb.add(UrgValPanel.TITLE_STRING, urgvp);

        SettingsPanel setp = new SettingsPanel();
        tb.add(SettingsPanel.TITLE_STRING, setp);
    }
    
}
