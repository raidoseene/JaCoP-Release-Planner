/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.solverutils;

import ee.raidoseene.releaseplanner.datamodel.Features;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Releases;
import java.util.Scanner;

/**
 *
 * @author Raido Seene
 */
public class SolverResultConverter {

    public static void SolverResultConverter(Project project, SolverResult result) {
        SimulationArchive archive = project.getSimulationArchive();
        Features features = project.getFeatures();
        Releases releases = project.getReleases();
        
        String solverResult = result.getResult();
        long simulationDuration = result.getTime();
        
        try (Scanner scanner = new Scanner(solverResult)) {
            Simulation s = archive.addSimulation();
            s.setSimulationDuration(simulationDuration);
            
            while (scanner.hasNextLine()) {
                String value = null;
                String allocations[];
                String line = scanner.nextLine();
                
                if(line.startsWith("VALUE = ")) {
                    String tempValue = line.split(" = ")[1];
                    value = tempValue.substring(0, tempValue.length() - 1);
                    line = scanner.nextLine();
                    if(line.startsWith("x = array1d(1..")) {
                        String[] tempAllocations = line.split(", \\[");
                        String allocationsLine = tempAllocations[1].substring(0, tempAllocations[1].length() - 3);
                        allocations = allocationsLine.split(", ");
                        
                        CandidatePlan cp = s.addCandidatePlan();
                        cp.setPlanValue(Integer.parseInt(value));
                        for(int i = 0; i < allocations.length; i++) {
                            int index = Integer.parseInt(allocations[i]) - 1;
                            if(index < 0) {
                                cp.setAllocation(features.getFeature(i), null);
                            } else {
                                cp.setAllocation(features.getFeature(i), releases.getRelease(index));
                            }
                        }
                    }
                }
            }
            
            /*
            // for testing the data
            for(int i = 0; i < s.getCandidatePlanCount(); i++) {
                CandidatePlan cp = s.getCandidatePlan(i);
                System.out.println(cp.getPlanValue());
                for(int j = 0; j < 10; j++) {
                    System.out.println(releases.getReleaseIndex(cp.getAllocation(features.getFeature(j))));
                }
            }
            */
        }
    }
}
