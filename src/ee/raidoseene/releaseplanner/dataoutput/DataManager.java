/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataoutput;

import ee.raidoseene.releaseplanner.backend.ResourceManager;
import ee.raidoseene.releaseplanner.backend.Settings;
import ee.raidoseene.releaseplanner.backend.SettingsManager;
import ee.raidoseene.releaseplanner.datamodel.Dependency;
import ee.raidoseene.releaseplanner.datamodel.Features;
import ee.raidoseene.releaseplanner.datamodel.Group;
import ee.raidoseene.releaseplanner.datamodel.GroupDependency;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Stakeholders;
import ee.raidoseene.releaseplanner.datamodel.ValueAndUrgency;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public final class DataManager {

    private final Project project;
    private static Project modDep;
    private static Features features;
    private static Stakeholders stakeholders;
    private static ValueAndUrgency valueAndUrgency;
    private final PrintWriter printWriter;

    public static void errorLoging(List<String> input) throws Exception {
        // TO DO: create a directory for logs if it do not exist
        // TO DO: create a new log file if it do not exist
        // TO DO: append input into the log file
    }

    public static File[] initiateDataOutput(Project project, boolean codeOutput, boolean postponedUrgency, boolean normalizedImportances) throws Exception {
        DependencyManager dm = new DependencyManager(project);
        
        File[] files = new File[2];
        File data = saveDataFile(project, dm, codeOutput, postponedUrgency, normalizedImportances);
        files[0] = data;
        if (codeOutput) {
            // export solver code file with dependencies
            int group = project.getDependencies().getTypedDependancyCount(GroupDependency.class, Dependency.GROUP);
            Settings settings = SettingsManager.getCurrentSettings();
            File code = SolverCodeManager.saveSolverCodeFile(project, dm, settings.getResourceShifting(),
                    group > 0 ? true : false);
            files[1] = code;
        }
        return files;
    }
    
    public static void fileOutput(Project project, String content, String dir) throws Exception {
        //File dir = ResourceManager.createDirectoryFromFile(new File(project.getStorage()));
        //String dir = ResourceManager.getDirectory().toString();
        File file = new File(dir, "result.txt");
        
        try (PrintWriter pw = new PrintWriter(file)) {
            pw.append(content);
        }
    }

    private static File saveDataFile(Project project, DependencyManager depMan, boolean codeOutput, boolean postponedUrgency, boolean normalizedImportances) throws Exception {
        //Project modDep = depMan.getModDep();
        
        File dir = ResourceManager.createDirectoryFromFile(new File(project.getStorage()));
        File file = new File(dir, "data.dzn");

        try (PrintWriter pw = new PrintWriter(file)) { // try with resource: printwriter closes automatically!
            DataManager dm = new DataManager(project, depMan.getModDep(), pw);

            //Project modDep = dm.ModifyingDependencyConversion(project);

            if (codeOutput) {
                dm.printFailHeader();
                dm.printProjectParameters(modDep.getFeatures().getFeatureCount(), true);
                dm.printWriter.println(depMan.getGroupDependenciesData());
                dm.printResources();
                dm.printFeatures(modDep);
                dm.printGroups(true);
                dm.printWAS(modDep, postponedUrgency, normalizedImportances);
            } else {
                System.out.println("Existing code file solution deprecated!");
                /*
                dm.printFailHeader();
                dm.printProjectParameters(modDep.getFeatures().getFeatureCount(), false);
                dm.printParamImportance();
                //dm.printDependencies(modDep);
                dm.printWriter.println(DependencyManager.getDependenciesData(project, modDep, false));
                dm.printResources();
                dm.printFeatures(modDep);
                dm.printGroups(false);
                dm.printStakeholders(modDep);
                */
            }
        }
        return file;
    }

    private DataManager(Project project, Project modDep, PrintWriter pw) {
        this.project = project;
        this.modDep = modDep;
        this.features = project.getFeatures();
        this.stakeholders = project.getStakeholders();
        this.valueAndUrgency = project.getValueAndUrgency();
        this.printWriter = pw;
    }

    private void printFailHeader() {
        printWriter.println("% Release planner data file\n% =========================\n\n");
    }

    private void printProjectParameters(int modDepFeatureCount, boolean codeOutput) {
        printWriter.println("% Number of features/releases/resources/stakeholders");
        printWriter.println("F = " + (project.getFeatures().getFeatureCount() + modDepFeatureCount) + ";");
        printWriter.println("Rel = " + (project.getReleases().getReleaseCount() - 1) + ";");
        printWriter.println("Res = " + project.getResources().getResourceCount() + ";");
        if (!codeOutput) {
            printWriter.println("S = " + project.getStakeholders().getStakeholderCount() + ";");
        }
        printWriter.println("% =========================\n");
    }

    private void printResources() {
        printWriter.println("% Resources (buffer/id/capacity)");
        printWriter.println("B = " + "0" + ";" + " % Missing Buffer!");

        printWriter.print("resource_id = [");
        for (int i = 0; i < project.getResources().getResourceCount(); i++) {
            printWriter.print("\"" + project.getResources().getResource(i).getName() + "\"");
            if (i < project.getResources().getResourceCount() - 1) {
                printWriter.print(",\n");
            }
        }
        printWriter.println("];");

        printWriter.print("Cap = [");
        for (int rel = 0; rel < project.getReleases().getReleaseCount() - 1; rel++) {
            printWriter.print("|");
            for (int res = 0; res < project.getResources().getResourceCount(); res++) {
                printWriter.print(" " + project.getReleases().getRelease(rel).getCapacity(project.getResources().getResource(res)) + ",");
            }
            if (rel < project.getReleases().getReleaseCount() - 2) {
                printWriter.print("\n");
            }
        }
        printWriter.println(" |];");
        printWriter.println("% =========================\n");
    }

    private void printFeatures(Project proj) {
        printWriter.println("% Features (id/consumtion per resource)");
        printWriter.print("feature_id = [");
        for (int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
            printWriter.print("\"" + project.getFeatures().getFeature(f).getName() + "\"");
            if (f < project.getFeatures().getFeatureCount() - 1) {
                printWriter.print(",\n");
            }
        }
        if (proj.getFeatures().getFeatureCount() > 0) {
            for (int f = 0; f < proj.getFeatures().getFeatureCount(); f++) {
                printWriter.print("\"" + proj.getFeatures().getFeature(f).getName() + "\"");
                if (f < proj.getFeatures().getFeatureCount() - 1) {
                    printWriter.print(",\n");
                }
            }
        }
        printWriter.println("];");

        printWriter.print("r = [");
        for (int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
            printWriter.print("|");
            for (int res = 0; res < project.getResources().getResourceCount(); res++) {
                printWriter.print(" " + project.getFeatures().getFeature(f).getConsumption(project.getResources().getResource(res)) + ",");
            }
            if (f < project.getFeatures().getFeatureCount() - 1) {
                printWriter.print("\n");
            }
        }
        if (proj.getFeatures().getFeatureCount() > 0) {
            for (int f = 0; f < proj.getFeatures().getFeatureCount(); f++) {
                printWriter.print("|");
                for (int res = 0; res < project.getResources().getResourceCount(); res++) {
                    printWriter.print(" " + proj.getFeatures().getFeature(f).getConsumption(project.getResources().getResource(res)) + ",");
                }
                if (f < proj.getFeatures().getFeatureCount() - 1) {
                    printWriter.print("\n");
                }
            }
        }
        printWriter.println(" |];");
        printWriter.println("% =========================\n");
    }

    private void printGroups(boolean codeOutput) {
        if ((codeOutput && project.getGroups().getGroupCount() > 0) || !codeOutput) {
            printWriter.println("% Feature Groups (1..groups, 1..F)");
            printWriter.println("nrOfGroups = " + project.getGroups().getGroupCount() + ";");
            printWriter.print("groups = [");
            if (project.getGroups().getGroupCount() > 0) {
                for (int g = 0; g < project.getGroups().getGroupCount(); g++) {
                    printWriter.print("| ");
                    Group group = project.getGroups().getGroup(g);
                    for (int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
                        printWriter.print((group.contains(project.getFeatures().getFeature(f)))
                                ? 1 + ", " : 0 + ", ");
                    }
                    if (g < project.getGroups().getGroupCount() - 1) {
                        printWriter.print("\n");
                    }
                }
            } else {
                printWriter.print("| ");
                for (int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
                    printWriter.print("0, ");
                }
            }
            printWriter.println("|];");
            printWriter.println("% =========================\n");
        }
    }

    
    
    private static float getNormalizedStkWeight(int s, int f, int[][] urgencies, int rel) {
        int sumOfStkWeights = 0;
        int featureCount = features.getFeatureCount() + modDep.getFeatures().getFeatureCount();
        
        if(urgencies == null) {
            if(valueAndUrgency.getValue(stakeholders.getStakeholder(s), features.getFeature(f)) == 0) {
                return 0;
            } else {
                for(int i = 0; i < stakeholders.getStakeholderCount(); i++) {
                    if(valueAndUrgency.getValue(stakeholders.getStakeholder(i), features.getFeature(f)) > 0) {
                        sumOfStkWeights += stakeholders.getStakeholder(i).getImportance();
                    }
                }
            }
        } else {
            if(urgencies[(s * featureCount) + f][rel] == 0) {
                return 0;
            } else {
                for(int i = 0; i < stakeholders.getStakeholderCount(); i++) {
                    if(urgencies[(i * featureCount) + f][rel] > 0) {
                        sumOfStkWeights += stakeholders.getStakeholder(i).getImportance();
                    }
                }
            }
        }
        
        System.out.println("Stk importance: " + stakeholders.getStakeholder(s).getImportance() + ", sum of importances: " + sumOfStkWeights + ", Stk " + s + " weight: " + ((float)stakeholders.getStakeholder(s).getImportance() / (float)sumOfStkWeights));
        return (float)stakeholders.getStakeholder(s).getImportance() / (float)sumOfStkWeights;
    }

    private void printWAS(Project modDep, boolean postponedUrgency, boolean normalizedImportances) {
        int[][] urgencies = UrgencyManager.getUrgencies(project, modDep);
        
        int featureCount = project.getFeatures().getFeatureCount() + modDep.getFeatures().getFeatureCount();
        
        
        
        for(int s = 0; s < stakeholders.getStakeholderCount(); s++) {
            System.out.print("Stakeholder " + s + ":\n");
            for(int rel = 0; rel < project.getReleases().getReleaseCount(); rel ++) {
                System.out.print("Release " + rel + ": ");
                for(int f = 0; f < featureCount; f++) {
                    System.out.print(urgencies[(s * featureCount) + f][rel] + ", ");
                }
                System.out.print("\n");
            }
        }
        
        
        
        int stkNo = stakeholders.getStakeholderCount();
        int criteriaNo = 2;
        
        float[] stkNormImportance = new float[stkNo];
        float[] criteriaNormImportance = new float[criteriaNo];
        
        int stkImpSum = 0;
        int criteriaImpSum = 0;
        
        for(int s = 0; s < stkNo; s++) {
            stkImpSum += stakeholders.getStakeholder(s).getImportance();
        }
        //for(int c = 0; c < stkNo; c++) {
            criteriaImpSum += 10;
        //}
        
        for(int s = 0; s < stkNo; s++) {
            stkNormImportance[s] = (float)(stakeholders.getStakeholder(s).getImportance()) / (float)stkImpSum;
            System.out.println("Stk" + (s + 1) + " normalized weight: " + stkNormImportance[s]);
        }
        //for(int c = 0; c < stkNo; c++) {
            criteriaNormImportance[0] = 0.5f;
            criteriaNormImportance[1] = 0.5f;
        //}
        
        printWriter.print("WAS = [");
        
        for (int f = 0; f < featureCount; f++) {
            printWriter.print("| 0, ");
            for (int rel = 0; rel < project.getReleases().getReleaseCount(); rel++) {
                float temp = 0.0f;
                for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
                    if(!normalizedImportances) {
                        temp += project.getStakeholders().getStakeholder(s).getImportance()
                                * (f < project.getFeatures().getFeatureCount() ?
                                project.getValueAndUrgency().getValue(
                                project.getStakeholders().getStakeholder(s),
                                project.getFeatures().getFeature(f)) :
                                modDep.getValueAndUrgency().getValue(
                                project.getStakeholders().getStakeholder(s),
                                project.getFeatures().getFeature(f - modDep.getFeatures().getFeatureCount())) )
                                * urgencies[(s * featureCount) + f][rel];
                    } else {
                        temp += //stkNormImportance[s] *
                                getNormalizedStkWeight(s, f, null, rel) *
                                criteriaNormImportance[0] *
                                (float)valueAndUrgency.getValue(
                                stakeholders.getStakeholder(s),
                                features.getFeature(f));
                        temp += //stkNormImportance[s] *
                                getNormalizedStkWeight(s, f, urgencies, rel) *
                                criteriaNormImportance[1] *
                                (float)urgencies[(s * featureCount) + f][rel];
                    }
                }
                /*
                if (rel == project.getReleases().getReleaseCount()) {
                    if(postponedUrgency) {
                        printWriter.print(Math.round(project.getReleases().getRelease(rel - 1).getImportance() * temp) + ", \n");
                    }
                } else {
                */
                    printWriter.print(Math.round(project.getReleases().getRelease(rel).getImportance() * temp) + ", ");
                //}
            }
        }

        

        printWriter.println("|];");
    }
}
