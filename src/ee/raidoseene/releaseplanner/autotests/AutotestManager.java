/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.autotests;

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
        
        try {
            project = DataGenerator.generateProject("name", settings);
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }
    }
}
