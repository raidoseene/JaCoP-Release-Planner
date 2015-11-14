/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.backend.ResourceManager;
import ee.raidoseene.releaseplanner.backend.Settings;
import ee.raidoseene.releaseplanner.backend.SettingsManager;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.dataoutput.DataManager;
import ee.raidoseene.releaseplanner.solverutils.Solver;
import ee.raidoseene.releaseplanner.solverutils.SolverResult;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author Raido Seene
 */
public class SimulationDialog extends JDialog {

    private final JProgressBar pbar;
    private boolean state = false;

    private SimulationDialog() {
        //this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setTitle("Simulating...");
        this.setResizable(false);
        this.setModal(true);

        Dimension size;
        JPanel cont = new JPanel(new BorderLayout());
        cont.setBorder(new EmptyBorder(10, 10, 10, 10));
        this.setContentPane(cont);

        this.pbar = new JProgressBar();
        this.pbar.setIndeterminate(true);
        this.pbar.setStringPainted(true);
        this.pbar.setString("");
        cont.add(this.pbar);

        size = this.pbar.getPreferredSize();
        size.width = Math.max(250, size.width);
        size.height = Math.max(10, size.height);
        this.pbar.setPreferredSize(size);
        this.pack();

        size = this.getSize();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screen.width - size.width) >> 1, (screen.height - size.height) >> 1);
    }

    private class SimulationFinalizer implements Runnable {

        private final SolverResult result;
        private final Throwable error;

        private SimulationFinalizer(SolverResult r, Throwable t) {
            this.result = r;
            this.error = t;
        }

        @Override
        public void run() {
            SimulationDialog.this.state = (this.result != null);
            SimulationDialog.this.dispose();

            if (this.error != null) {
                Messenger.showError(this.error, null);
            }
        }
    }

    private void finalizeSimulation(SolverResult r, Throwable t) {
        SimulationFinalizer finalizer = new SimulationFinalizer(r, t);
        SwingUtilities.invokeLater(finalizer);
    }

    private static class SimulationProcessor implements Runnable {

        private final SimulationDialog dialog;

        private SimulationProcessor(SimulationDialog sd) {
            this.dialog = sd;
        }

        @Override
        public void run() {
            Project project = ProjectManager.getCurrentProject();
            
            StringBuilder sbTimes = new StringBuilder();
            StringBuilder sbProject = new StringBuilder();
            StringBuilder sbResult = new StringBuilder();

            SolverResult sr = null;

            //this.saveCurrentProject(false);
            try {
                if (ProjectManager.getCurrentProject().getStorage() == null) {
                    String msg = "Project is not saved!\nUnable to determine dump location!";
                    throw new Exception(msg);
                }

                //Solver.runSolver();
                Settings settings = SettingsManager.getCurrentSettings();
                sr = Solver.executeSimulation(ProjectManager.getCurrentProject(), settings.getCodeOutput(), settings.getPostponedUrgency(), settings.getNormalizedImportances(), false);
                this.dialog.finalizeSimulation(sr, null);
            } catch (Throwable t) {
                this.dialog.finalizeSimulation(null, t);
            }

            sbProject.append(project.getName() + " simulation result:\n");

            sbResult.append(sbProject);
            sbResult.append(sr.getResult() + "\n\n");
            sbResult.append("\n\n********************************************\n\n");

            sbTimes.append(sbProject);
            sbTimes.append("Solver Time: " + sr.getTime() + "ms");
            sbTimes.append("\n********************************************\n\n");

            try {
                String dir = ResourceManager.createDirectoryFromFile(new File(project.getStorage())).getAbsolutePath();
                DataManager.fileOutput("result", sbResult.toString(), dir);

                dir = ResourceManager.getDirectory().toString();
                DataManager.fileOutput("results", sbTimes.toString(), dir);
            } catch (Exception ex) {
                Messenger.showError(ex, null);
            }
        }
    }

    public static boolean showSimulationDialog() {
        SimulationDialog sd = new SimulationDialog();
        Thread t = new Thread(new SimulationProcessor(sd), "Simulator Thread");
        t.start();

        sd.setVisible(true);
        return sd.state;
    }
}
