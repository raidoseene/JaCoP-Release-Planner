/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.autotests;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.gui.Messenger;

/**
 *
 * @author Raido Seene
 */
public class AutotestManager {

    public AutotestManager() {
        AutotestSettings settings = new AutotestSettings(); // Settings need to be populated by autotest interface
        Project project;

        int projNo = settings.getProjectNo();
        
        for (int i = 0; i < projNo; i++) {
            try {
                String projName = "Project " + DataGenerator.numberGenerator(i, projNo);
                project = DataGenerator.generateProject(projName, settings, i);
                
                // run simulatore
                
                // save and close results
            } catch (Exception ex) {
                Messenger.showError(ex, null);
            }
        }
    }
}
