/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.solverutils;

import ee.raidoseene.releaseplanner.backend.InputListener;
import ee.raidoseene.releaseplanner.backend.InputReader;
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

    public static void runSolver() throws IOException {
        String minizincLocation = "C:/Program Files (x86)/MiniZinc 1.6/bin/mzn2fzn.bat";
        String outputFile = "D:/University/UT/Magistritöö/Code/InputFiles/ruhe_problem.fzn";
        String solverCode = "D:/University/UT/Magistritöö/Code/InputFiles/ruhe_problem_0.8.mzn";
        String dataFile = "D:/University/UT/Magistritöö/Code/InputFiles/ruhe_input_0.6.dzn";


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
        String[] solverInput = new String[]{"-v", "D:/University/UT/Magistritöö/Code/InputFiles/ruhe_problem.fzn"};

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
                System.out.println("Done!");
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
        System.setOut(interceptor);
        
        Fz2jacop.main(solverInput);

        System.setOut(origOut);
        System.out.println("\n*** *** *** JaCoP Output Start *** *** ***\n" + sb.toString() + "\n*** *** *** JaCoP End *** *** ***\n");
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
        }

        @Override
        public void println(String s) {
            sb.append(s);
            sb.append("\n");
        }
    }
}