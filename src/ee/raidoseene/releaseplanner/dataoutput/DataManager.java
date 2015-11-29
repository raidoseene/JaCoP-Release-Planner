/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataoutput;

import ee.raidoseene.releaseplanner.backend.ProjectManager;
import ee.raidoseene.releaseplanner.backend.ResourceManager;
import ee.raidoseene.releaseplanner.backend.Settings;
import ee.raidoseene.releaseplanner.backend.SettingsManager;
import ee.raidoseene.releaseplanner.datamodel.Criteria;
import ee.raidoseene.releaseplanner.datamodel.Criterium;
import ee.raidoseene.releaseplanner.datamodel.Dependency;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.Features;
import ee.raidoseene.releaseplanner.datamodel.Group;
import ee.raidoseene.releaseplanner.datamodel.GroupDependency;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Stakeholder;
import ee.raidoseene.releaseplanner.datamodel.Stakeholders;
import ee.raidoseene.releaseplanner.datamodel.ValueAndUrgency;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private static Features independentFeatures;
    private static Stakeholders stakeholders;
    private static ValueAndUrgency valueAndUrgency;
    private final PrintWriter printWriter;

    public static void errorLoging(List<String> input) throws Exception {
        // TO DO: create a directory for logs if it do not exist
        // TO DO: create a new log file if it do not exist
        // TO DO: append input into the log file
    }

    public static File[] initiateDataOutput(Project project, DependencyManager dm, boolean codeOutput, boolean postponedUrgency, boolean normalizedImportances) throws Exception {
        //DependencyManager dm = new DependencyManager(project);

        File[] files = new File[3];
        
        File dir = ResourceManager.createDirectoryFromFile(new File(project.getStorage()));
        File solverCode = new File(dir, "CompiledSolverInput.dzn");
        files[0] = solverCode;
        
        File data = saveDataFile(project, dm, codeOutput, postponedUrgency, normalizedImportances);
        files[1] = data;
        if (codeOutput) {
            // export solver code file with dependencies
            int group = project.getDependencies().getTypedDependancyCount(GroupDependency.class, Dependency.GROUP);
            Settings settings = SettingsManager.getCurrentSettings();
            File code = SolverCodeManager.saveSolverCodeFile(project, dm, settings.getResourceShifting(),
                    group > 0 ? true : false);
            files[2] = code;
        }
        return files;
    }

    public static void fileOutput(String name, String content, String dir) throws Exception {
        /*
        File file = new File(dir, name + ".txt");

        try (PrintWriter pw = new PrintWriter(file)) {
            pw.append(content);
        }
        */
        String file = dir + "\\" + name + ".txt";
         
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file, true)))) {
            out.println(content);
        } catch (IOException e) {
            //exception handling left as an exercise for the reader
        }
    }

    private static File saveDataFile(Project project, DependencyManager depMan, boolean codeOutput, boolean postponedUrgency, boolean normalizedImportances) throws Exception {
        //Project modDep = depMan.getModDep();

        File dir = ResourceManager.createDirectoryFromFile(new File(project.getStorage()));
        File file = new File(dir, "Data.dzn");

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
        this.independentFeatures = modDep.getFeatures();
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
                printWriter.print(",\n\"" + proj.getFeatures().getFeature(f).getName() + "\"");
                //if (f < proj.getFeatures().getFeatureCount() - 1) {
                //    printWriter.print("\n");
                //}
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
                printWriter.print("\n|");
                for (int res = 0; res < project.getResources().getResourceCount(); res++) {
                    printWriter.print(" " + proj.getFeatures().getFeature(f).getConsumption(project.getResources().getResource(res)) + ",");
                }
                //if (f < proj.getFeatures().getFeatureCount() - 1) {
                //    printWriter.print("\n");
                //}
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
        int originalFeatCount = features.getFeatureCount();
        int featureCount = features.getFeatureCount() + modDep.getFeatures().getFeatureCount();
        Feature feat;

        if (urgencies == null) {
            if (f < originalFeatCount) {
                feat = features.getFeature(f);
            } else {
                feat = independentFeatures.getFeature(f - originalFeatCount);
            }

            if (valueAndUrgency.getValue(stakeholders.getStakeholder(s), feat) == 0) {
                return 0;
            } else {
                for (int i = 0; i < stakeholders.getStakeholderCount(); i++) {
                    if (valueAndUrgency.getValue(stakeholders.getStakeholder(i), feat) > 0) {
                        sumOfStkWeights += stakeholders.getStakeholder(i).getImportance();
                    }
                }
            }
        } else {
            if (urgencies[(s * featureCount) + f][rel] == 0) {
                return 0;
            } else {
                for (int i = 0; i < stakeholders.getStakeholderCount(); i++) {
                    if (urgencies[(i * featureCount) + f][rel] > 0) {
                        sumOfStkWeights += stakeholders.getStakeholder(i).getImportance();
                    }
                }
            }
        }

        return (float) stakeholders.getStakeholder(s).getImportance() / (float) sumOfStkWeights;
    }

    private static float getNormalizedCriteriaWeight(Criteria criteria, int index, int s, int f, int[][] urgencies, int rel) {
        // At the moment looks only Value and Urgency - needs to look input criterium - Value and Urgency are special cases

        Project proj = ProjectManager.getCurrentProject();
        Stakeholder stk = proj.getStakeholders().getStakeholder(s);
        Feature feature;
        int criteriumValue;
        int sumOfCriteriaWeights = 0;
        int originalFeatCount = features.getFeatureCount();
        int featureCount = features.getFeatureCount() + modDep.getFeatures().getFeatureCount();

        if (f < originalFeatCount) {
            feature = features.getFeature(f);
        } else {
            feature = independentFeatures.getFeature(f - originalFeatCount);
        }

        if (index == 0) {
            criteriumValue = valueAndUrgency.getValue(stakeholders.getStakeholder(s),
                    (f < originalFeatCount ? features.getFeature(f)
                    : independentFeatures.getFeature(f - originalFeatCount)));
        } else if (index == 1) {
            criteriumValue = urgencies[(s * featureCount) + f][rel];
        } else {
            criteriumValue = criteria.getCriteriumValue(criteria.getCriterium(index), stk, feature);
        }

        if (criteriumValue > 0) {
            int valueValue = valueAndUrgency.getValue(stakeholders.getStakeholder(s),
                    (f < originalFeatCount ? features.getFeature(f)
                    : independentFeatures.getFeature(f - originalFeatCount)));
            int urgencyValue = urgencies[(s * featureCount) + f][rel];

            if (valueValue > 0) {
                sumOfCriteriaWeights += criteria.getCriterium(0).getWeight();
            }
            if (urgencyValue > 0) {
                sumOfCriteriaWeights += criteria.getCriterium(1).getWeight();
            }

            for (int c = 2; c < criteria.getCriteriumCount(); c++) {
                Criterium crit = criteria.getCriterium(c);
                if (criteria.getCriteriumValue(crit, stk, feature) > 0) {
                    sumOfCriteriaWeights += crit.getWeight();
                }
            }

            // Think, if urgecny is 0 (by the curve) then do we have only 1 criterium (Value) or still 2 criteria

            return (float) criteria.getCriterium(index).getWeight() / (float) sumOfCriteriaWeights;
        }
        return 0;
    }

    private void printWAS(Project modDep, boolean postponedUrgency, boolean normalizedImportances) {
        int[][] urgencies = UrgencyManager.getUrgencies(project, modDep);
        int featureCount = project.getFeatures().getFeatureCount() + modDep.getFeatures().getFeatureCount();
        int originalFeatCount = project.getFeatures().getFeatureCount();

        int stkNo = stakeholders.getStakeholderCount();
        Criteria criteria = ProjectManager.getCurrentProject().getCriteria();
        //int criteriaNo = criteria.getCriteriumCount();

        //float[] stkNormImportance = new float[stkNo];
        //float[] criteriaNormImportance = new float[criteriaNo];

        int criteriaImpSum = 0;

        /*
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
         */
        //for(int c = 0; c < stkNo; c++) {
        //criteriaNormImportance[0] = 0.5f;
        //criteriaNormImportance[1] = 0.5f;
        //}

        printWriter.print("WAS = [");

        for (int f = 0; f < featureCount; f++) {
            printWriter.print("| 0, ");
            for (int rel = 0; rel < project.getReleases().getReleaseCount(); rel++) {
                float temp = 0.0f;
                for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
                    if (!normalizedImportances) {
                        temp += project.getStakeholders().getStakeholder(s).getImportance() *
                                criteria.getCriterium(0).getWeight() *
                                valueAndUrgency.getValue(
                                stakeholders.getStakeholder(s),
                                (f < originalFeatCount ? features.getFeature(f)
                                : independentFeatures.getFeature(f - originalFeatCount)));
                        temp += project.getStakeholders().getStakeholder(s).getImportance() *
                                criteria.getCriterium(1).getWeight() *
                                urgencies[(s * featureCount) + f][rel];
                        for (int c = 2; c < criteria.getCriteriumCount(); c++) {
                            temp += project.getStakeholders().getStakeholder(s).getImportance() *
                                    criteria.getCriterium(c).getWeight() *
                                    criteria.getCriteriumValue(criteria.getCriterium(c), stakeholders.getStakeholder(s), features.getFeature(f)); // (c) value
                        }
                    } else {
                        temp += getNormalizedStkWeight(s, f, null, rel) * // Normalizes stk weight
                                getNormalizedCriteriaWeight(criteria, 0, s, f, urgencies, rel) * // Normalized criteria (Value) weight
                                (float) valueAndUrgency.getValue( // (Value) value
                                stakeholders.getStakeholder(s),
                                (f < originalFeatCount ? features.getFeature(f)
                                : independentFeatures.getFeature(f - originalFeatCount)));
                        temp += getNormalizedStkWeight(s, f, urgencies, rel) * // Normalizes stk weight
                                getNormalizedCriteriaWeight(criteria, 1, s, f, urgencies, rel) * // Normalized criteria (Value) weight
                                (float) urgencies[(s * featureCount) + f][rel]; // (Urgecy) value


                        System.out.println("Stakeholder: " + stakeholders.getStakeholder(s).getName());
                        System.out.println("Feature: " + features.getFeature(f).getName());
                        System.out.println("Value normalized weight: " + getNormalizedCriteriaWeight(criteria, 0, s, f, urgencies, rel));
                        System.out.println("Urgency normalized weight: " + getNormalizedCriteriaWeight(criteria, 1, s, f, urgencies, rel));
                        System.out.println("*** *** ***");

                        for (int c = 2; c < criteria.getCriteriumCount(); c++) {
                            temp += getNormalizedStkWeight(s, f, null, rel) * // Normalizes stk weight
                                    getNormalizedCriteriaWeight(criteria, c, s, f, urgencies, rel) * // Normalized criteria (c) weight
                                    criteria.getCriteriumValue(criteria.getCriterium(c), stakeholders.getStakeholder(s), features.getFeature(f)); // (c) value
                        }
                    }
                }
                printWriter.print(Math.round(project.getReleases().getRelease(rel).getImportance() * temp) + ", ");
                //printWriter.print(Math.round(project.getReleases().getRelease(rel).getImportance() * (temp * 1000)) + ", ");
            }
        }



        printWriter.println("|];");
    }
}
