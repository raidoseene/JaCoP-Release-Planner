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
 * @author Raido Seene
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
        
        CriteriaPanel critp = new CriteriaPanel();
        tb.add(CriteriaPanel.TITLE_STRING, critp);


        ValUrgPanel urgvp = new ValUrgPanel();
        tb.add(ValUrgPanel.TITLE_STRING, urgvp);
        
        SimulationPanel simp = new SimulationPanel();
        tb.add(SimulationPanel.TITLE_STRING, simp);

        SettingsPanel setp = new SettingsPanel();
        tb.add(SettingsPanel.TITLE_STRING, setp);
    }
    
}
