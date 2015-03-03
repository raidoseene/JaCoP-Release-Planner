/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.solverutils;

import java.io.IOException;
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
        String[] solverInput = new String[] {"-v", "D:/University/UT/Magistritöö/Code/InputFiles/ruhe_problem.fzn"};
        
        Process process = Runtime.getRuntime().exec(minizincInput.toArray((String[]) solverInput));
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            Logger.getLogger(Solver.class.getName()).log(Level.SEVERE, null, ex);
        }
        Fz2jacop.main(solverInput);
     }
}
