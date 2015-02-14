/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataoutput;

import ee.raidoseene.releaseplanner.datamodel.Dependency;
import ee.raidoseene.releaseplanner.datamodel.DependencyType;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.FixedDependency;
import ee.raidoseene.releaseplanner.datamodel.Interdependency;
import ee.raidoseene.releaseplanner.datamodel.Project;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public final class DataManager {

    private File file;
    private PrintWriter printWriter;
    private Project project;

    public void saveDataFile(Project project) {
        try {
            this.project = project;
            file = new File(DataManager.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            printWriter = new PrintWriter(file.getParent() + "\\data.dzn");
            
            printFailHeader();
            printProjectParameters();
            printParamImportance();
            printDependencies();
            printResources();
            printFeatures();
            printModifyingInterdependencies();
            printStakeholders();
            printWriter.close();
        } catch (URISyntaxException | IOException e) {
        }
    }
    
    private void printFailHeader() {
        printWriter.println("% Release planner data file\n% =========================\n\n");
    }
    
    private void printProjectParameters() {
        printWriter.println("% Number of features/releases/resources/stakeholders");
        printWriter.println("F = " + project.getFeatures().getFeatureCount() + ";");
        printWriter.println("Rel = " + project.getReleases().getReleaseCount() + ";");
        printWriter.println("Res = " + project.getResources().getResourceCount() + ";");
        printWriter.println("S = " + project.getStakeholders().getStakeholderCount() + ";");
        printWriter.println("% =========================\n");
    }
    
    private void printParamImportance() {
        printWriter.println("% Stakeholder/release importance");
        printWriter.print("lambda = [ ");
        for(int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
            printWriter.print(project.getStakeholders().getStakeholder(s).getImportance() + ", ");
            if(s < project.getStakeholders().getStakeholderCount() - 1) {
                printWriter.print(", ");
            } else {
                printWriter.print(" ");
            }
        }
        printWriter.println("];");
        
        printWriter.print("ksi = [ ");
        for(int rel = 0; rel < project.getReleases().getReleaseCount(); rel++) {
            printWriter.print(project.getReleases().getRelease(rel).getImportance() + ", ");
            if(rel < project.getReleases().getReleaseCount() - 1) {
                printWriter.print(", ");
            } else {
                printWriter.print(" ");
            }
        }
        printWriter.println("];");
        printWriter.println("% =========================\n");
    }
    
    private void printDependencies() {
        List<Dependency> fixedDependencies;
        List<Dependency> andDependencies;
        List<Dependency> reqDependencies;
        List<Dependency> xorDependencies;
        
        fixedDependencies = project.getInterdependencies().getDependencyList(DependencyType.FIXED_DEPENDENCY);
        andDependencies = project.getInterdependencies().getInterdependencyList(DependencyType.INTERDEPENDENCY, 1);
        reqDependencies = project.getInterdependencies().getInterdependencyList(DependencyType.INTERDEPENDENCY, 2);
        xorDependencies = project.getInterdependencies().getInterdependencyList(DependencyType.INTERDEPENDENCY, 3);
        
        int fixedSize = fixedDependencies.size();
        int andSize = andDependencies.size();
        int reqSize = reqDependencies.size();
        int xorSize = xorDependencies.size();
        
        printWriter.println("% Designated features/coupled features/preceeding tasks/ OR tasks");
        
        // Fixed release
        printWriter.println("A = " + fixedSize + ";");
        printWriter.print("a = [| ");
        if(fixedSize > 0) {
            for(int i = 0; i < fixedSize; i++) {
                FixedDependency fd = ((FixedDependency)fixedDependencies.get(i));
                printWriter.print(project.getFeatures().getFeatureIndex(fd.getFeature()) +
                        ", " + project.getReleases().getReleaseIndex(fd.getRelease()) + ", ");
            }
            printWriter.println("|];");
        } else {
            printWriter.println("1, 1, |];");
        }
        
        // AND dependency
        printWriter.println("C = " + andSize + ";");
        printWriter.print("c = [| ");
        if(andSize > 0) {
            for(int i = 0; i < andSize; i++) {
                Interdependency id = ((Interdependency)andDependencies.get(i));
                printWriter.print(project.getFeatures().getFeatureIndex(id.getPrimaryFeature()) + 
                        ", " + project.getFeatures().getFeatureIndex(id.getSecondaryRelease()) + ", ");
            }
            printWriter.println("|];");
        } else {
            printWriter.println("1, 1, |];");
        }
        
        // REQUIRES dependency
        printWriter.println("P = " + reqSize + ";");
        printWriter.print("p = [| ");
        if(reqSize > 0) {
            for(int i = 0; i < reqSize; i++) {
                Interdependency id = ((Interdependency)reqDependencies.get(i));
                printWriter.print(project.getFeatures().getFeatureIndex(id.getPrimaryFeature()) + 
                        ", " + project.getFeatures().getFeatureIndex(id.getSecondaryRelease()) + ", ");
            }
            printWriter.println("|];");
        } else {
            printWriter.println("1, 1, |];");
        }
        
        // XOR dependency
        printWriter.println("O = " + xorSize + ";");
        printWriter.print("o = [| ");
        if(xorSize > 0) {
            for(int i = 0; i < xorSize; i++) {
                Interdependency id = ((Interdependency)xorDependencies.get(i));
                printWriter.print(project.getFeatures().getFeatureIndex(id.getPrimaryFeature()) + 
                        ", " + project.getFeatures().getFeatureIndex(id.getSecondaryRelease()) + ", ");
            }
            printWriter.println("|];");
        } else {
            printWriter.println("1, 1, |];");
        }
        
        printWriter.println("% =========================");
    }
    
    private void printResources(){
        printWriter.println("% Resources (buffer/id/capacity)");
        printWriter.println("B = " + "{missing buffer}" + ";");
        
        printWriter.print("resource_id = [");
        for(int i = 0; i < project.getResources().getResourceCount(); i++) {
            printWriter.print("\"" + project.getResources().getResource(i) + "\",");
            if(i < project.getResources().getResourceCount() - 1) {
                printWriter.print("\n");
            }
        }
        printWriter.println("];");
        
        printWriter.print("Cap = [");
        for(int rel = 0; rel < project.getReleases().getReleaseCount(); rel++) {
            printWriter.print("|");
            for(int res = 0; res < project.getResources().getResourceCount(); res++) {
                printWriter.print(" " + project.getReleases().getRelease(rel).getCapacity(project.getResources().getResource(res)) + ",");
            }
            if(rel < project.getReleases().getReleaseCount() - 1) {
                printWriter.print("\n");
            }
        }
        printWriter.println(" |];");
        printWriter.println("% =========================");
    }
    
    private void printFeatures() {
        printWriter.println("% Features (id/consumtion per resource)");
        printWriter.print("feature_id = [");
        for(int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
            printWriter.print("\"" + project.getFeatures().getFeature(f) + "\",");
            if(f < project.getFeatures().getFeatureCount() - 1) {
                printWriter.print("\n");
            }
        }
        printWriter.println("];");
        
        printWriter.print("r = [");
        for(int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
            printWriter.print("|");
            for(int res = 0; res < project.getResources().getResourceCount(); res++) {
                printWriter.print(" " + project.getFeatures().getFeature(f).getConsumption(project.getResources().getResource(res)) + ",");
            }
            if(f < project.getReleases().getReleaseCount() - 1) {
                printWriter.print("\n");
            }
        }
        printWriter.println(" |];");
        printWriter.println("% =========================");    
    }
    
    private void printModifyingInterdependencies() {
        List<Dependency> changeInCostDependencies;
        List<Dependency> changeInValueDependencies;
        List<Dependency> changeInUrgencyDependencies;
        
        changeInCostDependencies = project.getInterdependencies().getInterdependencyList(DependencyType.INTERDEPENDENCY, 4);
        changeInValueDependencies = project.getInterdependencies().getInterdependencyList(DependencyType.INTERDEPENDENCY, 5);
        changeInUrgencyDependencies = project.getInterdependencies().getInterdependencyList(DependencyType.INTERDEPENDENCY, 6);
        
        int ccSize = changeInCostDependencies.size();
        int cvSize = changeInValueDependencies.size();
        int cuSize = changeInUrgencyDependencies.size();
        
        printWriter.println("% Change in Cost  (feature, feature, costs 1, ..., cost n)");
        
        // CHANGE IN COST dependency
        printWriter.println("CC = " + ccSize + ";");
        printWriter.print("cc = [| ");
        if(ccSize > 0) {
            for(int i = 0; i < ccSize; i++) {
                Interdependency id = ((Interdependency)changeInCostDependencies.get(i));
                printWriter.print(project.getFeatures().getFeatureIndex(id.getPrimaryFeature()) + 
                        ", " + project.getFeatures().getFeatureIndex(id.getSecondaryRelease()) + 
                        "{missing costs}" + ", ");
            }
            printWriter.println("|];");
        } else {
            printWriter.println("1, 1, |];");
        }
        
        // CHANGE IN VALUE dependency
        printWriter.println("CV = " + cvSize + ";");
        printWriter.print("cv = [| ");
        if(cvSize > 0) {
            for(int i = 0; i < cvSize; i++) {
                Interdependency id = ((Interdependency)changeInValueDependencies.get(i));
                printWriter.print(project.getFeatures().getFeatureIndex(id.getPrimaryFeature()) + 
                        ", " + project.getFeatures().getFeatureIndex(id.getSecondaryRelease()) + 
                        "{missing values}" + ", ");
            }
            printWriter.println("|];");
        } else {
            printWriter.println("1, 1, |];");
        }
        
        // CHANGE IN URGENCY dependency
        printWriter.println("CU = " + cuSize + ";");
        printWriter.print("cu = [| ");
        if(cuSize > 0) {
            for(int i = 0; i < cuSize; i++) {
                Interdependency id = ((Interdependency)changeInUrgencyDependencies.get(i));
                printWriter.print(project.getFeatures().getFeatureIndex(id.getPrimaryFeature()) + 
                        ", " + project.getFeatures().getFeatureIndex(id.getSecondaryRelease()) + 
                        "{missing urgencies}" + ", ");
            }
            printWriter.println("|];");
        } else {
            printWriter.println("1, 1, |];");
        }
        
        printWriter.println("% =========================");
    }

    private void printStakeholders() {
        printWriter.println("% Stakeholders value(1..9), urgency (x + y + z = 9)");
        printWriter.print("value = [");
        for(int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
            printWriter.print("|");
            // TODO get value per stakeholder and per feature
            if(s < project.getStakeholders().getStakeholderCount() - 1) {
                printWriter.print("\n");
            }
        }
        printWriter.println(" |];");
        
        printWriter.println("urgency = array" + (project.getReleases().getReleaseCount() + 1) + "d(1..S, 1..F, 1.." + (project.getReleases().getReleaseCount() + 1) + ", [");
        // TODO
        printWriter.println("]);");
        printWriter.println("% =========================");
    }
}
