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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
    
    private void resetSettings() {
        this.settings = new AutotestSettings();
    }

    public void generateProjects(boolean simulate) {
        Project project;
        int projNo = this.settings.getProjectNo();
        int repNo = this.settings.getRepetitionNo();

        for (int pro = 0; pro < projNo; pro++) {
            for (int rep = 0; rep < repNo; rep++) {
                try {
                    //String projName = "Project " + DataGenerator.numberGenerator(i, projNo);
                    String projName = "Project " + DataGenerator.numberGenerator((pro * repNo) + rep, projNo * repNo);
                    project = DataGenerator.generateProject(projName, this.settings, pro);
                    saveProject(project);
                    if (simulate) {
                        this.startSimulation(project);
                    }
                } catch (Exception ex) {
                    Messenger.showError(ex, null);
                }
            }
        }
    }

    public void generateProjects(File file, boolean simulate) throws IOException {
        Project project;
        String line;
        int projNo, repNo;
        String ID;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                ID = InstructionReader.readSettings(this.settings, line);
                System.out.println("No. of repetitions from settings after coming out of reading: " + settings.getRepetitionNo());
                
                if (ID != null) {
                    projNo = this.settings.getProjectNo();
                    repNo = this.settings.getRepetitionNo();
                    
                    System.out.println("No. of projects: " + projNo + ", No. of repetitions: " + repNo);
                    
                    for (int pro = 0; pro < projNo; pro++) {
                        for (int rep = 0; rep < repNo; rep++) {
                            try {
                                String projName = ID + "_Project " + DataGenerator.numberGenerator((pro * repNo) + rep, projNo * repNo);
                                project = DataGenerator.generateProject(projName, this.settings, pro);
                                saveProject(project);
                                if (simulate) {
                                    this.startSimulation(project);
                                }
                            } catch (Exception ex) {
                                Messenger.showError(ex, null);
                            }
                        }
                    }
                    
                    this.resetSettings();
                }
            }
        }
    }

    private String startSimulation(Project project) {
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
        return sbTimes.toString();
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