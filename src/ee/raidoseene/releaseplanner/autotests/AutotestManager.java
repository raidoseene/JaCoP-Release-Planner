/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.autotests;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.backend.ResourceManager;
import ee.raidoseene.releaseplanner.backend.Settings;
import ee.raidoseene.releaseplanner.backend.SettingsManager;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.dataoutput.DataManager;
import ee.raidoseene.releaseplanner.gui.Messenger;
import ee.raidoseene.releaseplanner.solverutils.Solver;
import ee.raidoseene.releaseplanner.solverutils.SolverResult;
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
        StringBuilder sbTimes = new StringBuilder();
        int projNo = this.settings.getProjectNo();
        SolverResult sr = null;

        for (int i = 0; i < projNo; i++) {
            try {
                String projName = "Project " + DataGenerator.numberGenerator(i, projNo);
                project = DataGenerator.generateProject(projName, this.settings, i);
                StringBuilder sbProject = new StringBuilder();
                StringBuilder sbResult = new StringBuilder();
                StringBuilder sbTime = new StringBuilder();

                saveProject(project);
                sr = runSolver();
                
                sbProject.append(projName + " simulation result:\n");
                sbTime.append("Solver Time: " + sr.getTime() + "ms");
                
                sbResult.append(sbProject);
                sbResult.append(sr.getResult() + "\n\n");
                sbResult.append("\n\n********************************************\n\n");
                
                sbTimes.append(sbProject);
                sbTimes.append(sbTime);
                sbTimes.append("\n********************************************\n\n");
                
                testingResultsFile(project, sbTimes.toString());
                projectResultFile(project, sbResult.toString());
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
    
    private SolverResult runSolver() {
        SolverResult sr = null;
        
        try {
            if (ProjectManager.getCurrentProject().getStorage() == null) {
                String msg = "Project is not saved!\nUnable to determine dump location!";
                throw new Exception(msg);
            }

            Settings s = SettingsManager.getCurrentSettings();
            sr = Solver.executeSimulation(ProjectManager.getCurrentProject(), s.getCodeOutput(), s.getPostponedUrgency(), true);
            //DataManager.fileOutput(project, output);
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }
        
        return sr;
    }
    
    private void testingResultsFile(Project project, String output) throws Exception {
        String dir = ResourceManager.getDirectory().toString();
        DataManager.fileOutput(project, output, dir);
    }
    
    private void projectResultFile(Project project, String output) throws Exception {
        String dir = ResourceManager.createDirectoryFromFile(new File(project.getStorage())).getAbsolutePath();
        DataManager.fileOutput(project, output, dir);
    }
}