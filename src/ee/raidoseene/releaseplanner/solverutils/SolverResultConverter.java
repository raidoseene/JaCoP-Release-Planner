/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.solverutils;

import ee.raidoseene.releaseplanner.datamodel.SimulationArchive;
import ee.raidoseene.releaseplanner.datamodel.Simulation;
import ee.raidoseene.releaseplanner.datamodel.CandidatePlan;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.Features;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Releases;
import ee.raidoseene.releaseplanner.dataoutput.DependencyManager;
import java.util.Scanner;

/**
 *
 * @author Raido Seene
 */
public class SolverResultConverter {

    public static void SolverResultConverter(Project project, DependencyManager dm, SolverResult result) {
        SimulationArchive archive = project.getSimulationArchive();
        Features features = project.getFeatures();
        Features independentFeatures = dm.getModDep().getFeatures();
        Project modDep = dm.getModDep();
        int originalFeatCount = features.getFeatureCount();
        Releases releases = project.getReleases();
        
        String solverResult = result.getResult();
        long simulationDuration = result.getTime();
        
        try (Scanner scanner = new Scanner(solverResult)) {
            Simulation s = archive.addSimulation(modDep);
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
                            Feature feat;
                            if(i < originalFeatCount) {
                                feat = features.getFeature(i);
                            } else {
                                feat = independentFeatures.getFeature(i - originalFeatCount);
                            }
                            if(index < 0) {
                                cp.setAllocation(feat, null);
                            } else {
                                cp.setAllocation(feat, releases.getRelease(index));
                            }
                        }
                    }
                } else if(line.startsWith("=====UNSATISFIABLE=====")) {
                    break;
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
