/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataoutput;

import ee.raidoseene.releaseplanner.datamodel.Project;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;

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
            
            // TODO: print all elements to file
            printWriter.println("Hello world");
            printFailHeader();
            printProjectParameters();
            printParamImportance();
            printDependencies();
            printResources();
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
        printWriter.println("% Designated features/coupled features/preceeding tasks/ OR tasks");
        /*printWriter.println("A = " + project.getInterdependencies().getFixedDependencies().getCount() + ";");
        for(int i = 0; i < project.getInterdependencies().getFixedDependencies().getCount(); i++) {
            printWriter.print(project.getInterdependencies().getFixedDependency(i) + ", ");
            if(i < project.getInterdependencies().getFixedDependencies().getCount() - 1) {
                printWriter.print(", ");
            } else {
                printWriter.print(" ");
            }
        }*/
        // TODO: get dependencies working
        printWriter.println("% =========================");
    }
    
    private void printResources(){
        printWriter.println("% Resources (buffer/id/capacity)");
        //printWriter.println("B = " + TODO: Buffer + ";");
        
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
        printWriter.println("% Change in Cost  (feature, feature, costs 1, ..., cost n)");
        // TODO
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
