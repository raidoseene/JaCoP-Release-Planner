/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.solverutils;

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

        Process process = Runtime.getRuntime().exec(minizincInput.toArray((String[]) solverInput));
        String minizincErrors = process.getOutputStream().toString();
        System.out.println("MiniZinc Errors:\n" + minizincErrors + "==========\n\n");
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(Solver.class.getName()).log(Level.SEVERE, null, ex);
        }
        String jacopOutput = null;
        InterceptionManager icMan = new InterceptionManager( jacopOutput, false );
        Fz2jacop.main(solverInput);
        System.out.println("==========\nJacop Output:\n" + jacopOutput + "==========\n\n");
    }

    /*
     private class Interceptor extends PrintStream {

     public Interceptor(OutputStream out) {
     super(out, true);
     }

     @Override
     public void print(String s) {//do what ever you like
     super.print(s);
     }
     }
     */
    private static class Interceptor extends PrintStream {

        private String str;
        PrintStream orig;

        public Interceptor(OutputStream out, String str) {
            super(out, true);
            this.str = str;
        }

        @Override
        public void print(String s) {
            //do what ever you like
            orig.print(s);
            str = s;
        }

        @Override
        public void println(String s) {
            print(s);
        }

        public void attachOut() {
            orig = System.out;
            System.setOut(this);
        }

        public void attachErr() {
            orig = System.err;
            System.setErr(this);
        }

        public void detachOut() {
            if (null != orig) {
                System.setOut(orig);
            }
        }

        public void detachErr() {
            if (null != orig) {
                System.setErr(orig);
            }
        }
    }

    private static class InterceptionManager {

        private Interceptor out;
        private Interceptor err;

        public InterceptionManager(String str, boolean append) {
            this.err = new Interceptor(System.err, str);
            this.err.attachErr();
        }
    }
    
}
