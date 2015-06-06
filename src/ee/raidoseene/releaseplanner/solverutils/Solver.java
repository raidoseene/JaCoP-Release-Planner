/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.solverutils;

import ee.raidoseene.releaseplanner.backend.InputListener;
import ee.raidoseene.releaseplanner.backend.InputReader;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.dataoutput.DataManager;
import ee.raidoseene.releaseplanner.gui.Messenger;
import ee.raidoseene.releaseplanner.gui.SolverOutputFrame;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jacop.fz.Fz2jacop;

/**
 *
 * @author Raido Seene
 */
public class Solver {

    public static SolverResult executeSimulation(Project project, boolean codeOutput, boolean postponedUrgency, boolean saveOutput) throws IOException {
        SolverResult sr = null;
        // TO DO: check if all needed elements are filled in the project
        if (project.getFeatures().getFeatureCount() > 0 & project.getReleases().getReleaseCount() > 0 &
                project.getResources().getResourceCount() > 0 & project.getStakeholders().getStakeholderCount() > 0) {
            File[] files;
            try {
                //file = DataManager.saveDataFile(ProjectManager.getCurrentProject());
                files = DataManager.initiateDataOutput(project, codeOutput, postponedUrgency);
                sr = runSolver(files, codeOutput, saveOutput);
            } catch (Exception ex) {
                Messenger.showError(ex, null);
            }
        } else {
            Messenger.showError(null, "For simulation, features, releases, resources and stakeholder need to be defined!");
        }
        return sr;
    }

    public static SolverResult runSolver(File[] files, boolean codeOutput, boolean saveOutput) throws IOException {
        String minizincLocation = "C:/Program Files (x86)/MiniZinc 1.6/bin/mzn2fzn.bat";
        String outputFile = "D:/University/UT/Magistritöö/UI/Test/SolverCode.fzn";
        String solverCode;
        String dataFile;
        if(codeOutput) {
            solverCode = files[1].getAbsolutePath();
            dataFile = files[0].getAbsolutePath();
        } else {
            solverCode = "D:/University/UT/Magistritöö/UI/Test/SolverCode.mzn";
            dataFile = files[0].getAbsolutePath();
        }
        //String outputFile = "D:/University/UT/Magistritöö/UI/Test/SolverCode.fzn";
        //String outputFile = "D:/University/UT/Magistritöö/UI/Test/Kuchcinski.fzn";
        //String outputFile = "D:/University/UT/Magistritöö/UI/Test/Mark.fzn";
        //String outputFile = "D:/University/UT/Magistritöö/UI/TestInitialTestAddons.fzn";
        //String solverCode = "D:/University/UT/Magistritöö/UI/Test/SolverCode.mzn";
        //String solverCode = "D:/University/UT/Magistritöö/UI/Test/Kuchcinski.mzn";
        //String solverCode = "D:/University/UT/Magistritöö/UI/Test/Mark.mzn";
        //String solverCode = "D:/University/UT/Magistritöö/Code/InputFiles/InitialTestAddons.mzn";
        //String dataFile = file.getAbsolutePath();
        //String dataFile = "D:/University/UT/Magistritöö/Code/InputFiles/InitialTestAddons.dzn";
        //String dataFile = "D:/University/UT/Magistritöö/UI/Test/Kuchcinski_data.dzn";
        //String dataFile = "D:/University/UT/Magistritöö/UI/Test/Mark.dzn";


        List<String> minizincInput = new ArrayList<>();
        minizincInput.add(minizincLocation);

        minizincInput.add("-G");
        minizincInput.add("jacop");
        minizincInput.add("-O-");
        minizincInput.add("-o");

        minizincInput.add(outputFile);
        minizincInput.add(solverCode);

        minizincInput.add("-d");

        minizincInput.add(dataFile);
        //String[] solverInput = new String[]{"-v", "D:/University/UT/Magistritöö/UI/Test/SolverCode.fzn"};
       //String[] solverInput = new String[]{"D:/University/UT/Magistritöö/UI/Test/SolverCode.fzn"};
        //String[] solverInput = new String[]{"D:/University/UT/Magistritöö/UI/Test/Kuchcinski.fzn"};
        String[] solverInput = new String[]{outputFile};
        //String[] solverInput = new String[]{"D:/University/UT/Magistritöö/UI/TestInitialTestAddons.fzn"};
        //String[] solverInput = new String[]{"-t", "15", "D:/University/UT/Magistritöö/UI/Test/SolverCode.fzn"};
        //String[] solverInput = new String[]{"-h"};
        
        System.out.println("\n*** *** *** MiniZinc Started *** *** ***\n");
        Process process = Runtime.getRuntime().exec(minizincInput.toArray(new String[minizincInput.size()]));
        InputListener listener = new InputListener() {
            @Override
            public void lineRead(String line) {
                // These methods are called from another thread so be careful
                // especially don't update UI directly from here
                System.out.println(line);
            }

            @Override
            public void errorThrown(Throwable error) {
                error.printStackTrace();
            }

            @Override
            public void finishedReading() {
                //System.out.println("Done!");
            }
        };

        new InputReader(process.getInputStream(), listener);
        new InputReader(process.getErrorStream(), listener);

        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(Solver.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("\n*** *** *** MiniZinc Stopped *** *** ***\n");

        // Running JaCoP
        StringBuilder sb = new StringBuilder();

        PrintStream origOut = System.out;
        PrintStream interceptor = new Interceptor(origOut, sb);
        
        System.out.println("\n*** *** *** 1. JaCoP Process Started *** *** ***\n");
        System.setOut(interceptor);
        
        Long start = System.currentTimeMillis();
        Fz2jacop.main(solverInput);
        Long end = System.currentTimeMillis();
        
       
        System.setOut(origOut);
        System.out.println(sb.toString());
        System.out.println("\n*** *** *** 1. JaCoP Process Ended *** *** ***\n");
        System.out.println("Solver Time: " + (end - start) + "ms");
        
        sb.append("\nSolver Time: ");
        long time = end - start;
        sb.append(time);
        sb.append("ms");
        
        String result = sb.toString();
        SolverResult sr = new SolverResult(result, time);
        if(!saveOutput) {
            SolverOutputFrame.showSolverOutputFrame(result);
        }
        //return output;
        return sr;
    }

    private static class Interceptor extends PrintStream {

        private StringBuilder sb;

        public Interceptor(OutputStream out, StringBuilder sb) {
            super(out, true);
            this.sb = sb;
        }

        @Override
        public void print(String s) {
            sb.append(s);
            sb.append("\n");
        }

        @Override
        public void println(String s) {
            sb.append(s);
            sb.append("\n");
        }
    }
}
