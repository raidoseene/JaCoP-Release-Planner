/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.autotests;

import ee.raidoseene.releaseplanner.backend.ProjectFileFilter;
import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.backend.ResourceManager;
import ee.raidoseene.releaseplanner.backend.Settings;
import ee.raidoseene.releaseplanner.backend.SettingsManager;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.dataoutput.DataManager;
import ee.raidoseene.releaseplanner.gui.Messenger;
import ee.raidoseene.releaseplanner.solverutils.Solver;
import java.awt.FileDialog;
import java.io.File;

/**
 *
 * @author Raido Seene
 */
public class AutotestManager {

    private AutotestSettings settings;

    public AutotestManager() {
        this.settings = new AutotestSettings(); // Settings need to be populated by autotest interface
    }

    public AutotestSettings getSettings() {
        return this.settings;
    }

    public void startTesting() {
        Project project;
        String output = "";
        StringBuilder sb = new StringBuilder();
        int projNo = this.settings.getProjectNo();

        for (int i = 0; i < projNo; i++) {
            try {
                String projName = "Project " + DataGenerator.numberGenerator(i, projNo);
                project = DataGenerator.generateProject(projName, this.settings, i);

                saveProject(project);
                sb.append(projName + " simulation result\n");
                sb.append(runSolver());
                sb.append("********************************************\n\n");
                testingResultsFile(project, sb.toString());
            } catch (Exception ex) {
                Messenger.showError(ex, null);
            }
        }
    }

    private void saveProject(Project project) {
        try {
            if (project.getStorage() != null) { // Overwrite
                ProjectManager.saveCurrentProject(null);
                return;
            }

            String dir = ResourceManager.getDirectory().toString();
            String fil = ProjectManager.getCurrentProject().getName() + ".proj";

            if (dir != null && fil != null) {
                File file = new File(dir, fil);
                ProjectManager.saveCurrentProject(file);
            }
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }
    }
    
    private String runSolver() {
        String output = "";
        
        try {
            if (ProjectManager.getCurrentProject().getStorage() == null) {
                String msg = "Project is not saved!\nUnable to determine dump location!";
                throw new Exception(msg);
            }

            Settings s = SettingsManager.getCurrentSettings();
            output = Solver.executeSimulation(ProjectManager.getCurrentProject(), s.getCodeOutput(), s.getPostponedUrgency(), true);
            //DataManager.fileOutput(project, output);
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }
        
        return output;
    }
    
    private void testingResultsFile(Project project, String output) throws Exception {
        DataManager.fileOutput(project, output);
    }
}