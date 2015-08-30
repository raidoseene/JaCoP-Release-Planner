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

    public void generateProjects(boolean simulate) {
        Project project;
        int projNo = this.settings.getProjectNo();

        for (int i = 0; i < projNo; i++) {
            try {
                String projName = "Project " + DataGenerator.numberGenerator(i, projNo);
                project = DataGenerator.generateProject(projName, this.settings, i);
                saveProject(project);
                if (simulate) {
                    this.startSimulation(project);
                }
            } catch (Exception ex) {
                Messenger.showError(ex, null);
            }
        }
    }

    private void startSimulation(Project project) {
        StringBuilder sbTimes = new StringBuilder();
        StringBuilder sbProject = new StringBuilder();
        StringBuilder sbResult = new StringBuilder();
        SolverResult sr = null;

        sr = runSolver();

        sbProject.append(project.getName() + " simulation result:\n");

        sbResult.append(sbProject);
        sbResult.append(sr.getResult() + "\n\n");
        sbResult.append("\n\n********************************************\n\n");

        sbTimes.append(sbProject);
        sbTimes.append("Solver Time: " + sr.getTime() + "ms");
        sbTimes.append("\n********************************************\n\n");

        try {
            String dir = ResourceManager.createDirectoryFromFile(new File(ProjectManager.getCurrentProject().getStorage())).getAbsolutePath();
            DataManager.fileOutput("result", sbResult.toString(), dir);
            
            dir = ResourceManager.getDirectory().toString();
            DataManager.fileOutput("results", sbTimes.toString(), dir);
        } catch (Exception ex) {
            Messenger.showError(ex, null);
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
            sr = Solver.executeSimulation(ProjectManager.getCurrentProject(), s.getCodeOutput(), s.getPostponedUrgency(), s.getNormalizedImportances(), true);
        } catch (Exception ex) {
            Messenger.showError(ex, null);
        }

        return sr;
    }
}