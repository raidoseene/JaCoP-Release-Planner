/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataoutput;

import ee.raidoseene.releaseplanner.backend.ResourceManager;
import ee.raidoseene.releaseplanner.backend.Settings;
import ee.raidoseene.releaseplanner.backend.SettingsManager;
import ee.raidoseene.releaseplanner.datamodel.Dependency;
import ee.raidoseene.releaseplanner.datamodel.Feature;
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
        printWriter.println("Rel = " + project.getReleases().getReleaseCount() + ";");
        printWriter.println("Res = " + project.getResources().getResourceCount() + ";");
        if (!codeOutput) {
            printWriter.println("S = " + project.getStakeholders().getStakeholderCount() + ";");
        }
        printWriter.println("% =========================\n");
    }

    /*
    private void printParamImportance() {
        printWriter.println("% Stakeholder/release importance");
        printWriter.print("lambda = [ ");
        for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
            printWriter.print(project.getStakeholders().getStakeholder(s).getImportance());
            if (s < project.getStakeholders().getStakeholderCount() - 1) {
                printWriter.print(", ");
            } else {
                printWriter.print(" ");
            }
        }
        printWriter.println("];");

        printWriter.print("ksi = [ ");
        for (int rel = 0; rel < project.getReleases().getReleaseCount(); rel++) {
            printWriter.print(project.getReleases().getRelease(rel).getImportance());
            if (rel < project.getReleases().getReleaseCount() - 1) {
                printWriter.print(", ");
            } else {
                printWriter.print(" ");
            }
        }
        printWriter.println("];");
        printWriter.println("% =========================\n");
    }
    */

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
        for (int rel = 0; rel < project.getReleases().getReleaseCount(); rel++) {
            printWriter.print("|");
            for (int res = 0; res < project.getResources().getResourceCount(); res++) {
                printWriter.print(" " + project.getReleases().getRelease(rel).getCapacity(project.getResources().getResource(res)) + ",");
            }
            if (rel < project.getReleases().getReleaseCount() - 1) {
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

    /*
    private void printStakeholders(Project ModDep) {
        printWriter.println("% Stakeholders value(1..9), urgency");
        printWriter.print("value = [");
        for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
            printWriter.print("| ");
            ValueAndUrgency valueAndUrgency = project.getValueAndUrgency();
            for (int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
                printWriter.print(valueAndUrgency.getValue(project.getStakeholders().getStakeholder(s),
                        project.getFeatures().getFeature(f)) + ", ");
            }

            if (ModDep.getValueAndUrgency().getValueAndUrgencyCount() > 0) {
                ValueAndUrgency newValueAndUrgency = ModDep.getValueAndUrgency();
                for (int f = 0; f < ModDep.getFeatures().getFeatureCount(); f++) {
                    printWriter.print(newValueAndUrgency.getValue(project.getStakeholders().getStakeholder(s),
                            ModDep.getFeatures().getFeature(f)) + ", ");
                }
            }
            if (s < project.getStakeholders().getStakeholderCount() - 1) {
                printWriter.print("\n");
            }
        }
        printWriter.println("|];");
        
        printWriter.println("urgency = array3d(1..S, 1..F, 1.." + (project.getReleases().getReleaseCount() + 1) + ", [");
        ValueAndUrgency valueAndUrgency = project.getValueAndUrgency();
        for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
            printWriter.println("% stakeholder " + (s + 1));
            for (int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
                int urgency = valueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                        project.getFeatures().getFeature(f));
                if (urgency == 0) {
                    for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                        printWriter.print("0, ");
                    }
                } else {
                    Release release = valueAndUrgency.getUrgencyRelease(project.getStakeholders().getStakeholder(s),
                            project.getFeatures().getFeature(f));
                    int releaseNr = project.getReleases().getReleaseIndex(release);
                    int deadlineCurve = valueAndUrgency.getDeadlineCurve(project.getStakeholders().getStakeholder(s),
                            project.getFeatures().getFeature(f));
                    if (deadlineCurve == (Urgency.DEADLINE_MASK & Urgency.EARLIEST)) {
                        if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
                            for (int r = 0; r < releaseNr - 2; r++) {
                                printWriter.print("0, ");
                            }
                            for (int r = releaseNr - 1; r < project.getReleases().getReleaseCount(); r++) {
                                printWriter.print(urgency + ", ");
                            }
                        } else {
                            int[] urgencies = new int[project.getReleases().getReleaseCount() + 1];
                            int tempUrgency = urgency;
                            if ((project.getReleases().getReleaseCount() + 1) - releaseNr >= ((urgency > 3)
                                    ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1))) {
                                for (int i = (releaseNr - 1) + (int) Math.round(urgency / 2.5f); i >= releaseNr - 1; i--) {
                                    urgencies[i] = tempUrgency;
                                    tempUrgency = tempUrgency / 2;
                                }
                                for (int i = 0; i < releaseNr - 1; i++) {
                                    urgencies[i] = 0;
                                }
                                if (releaseNr + ((urgency > 3)
                                        ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)) < project.getReleases().getReleaseCount() + 1) {
                                    for (int i = (releaseNr) + ((urgency > 3)
                                            ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)); i <= project.getReleases().getReleaseCount(); i++) {
                                        urgencies[i] = urgency;
                                    }
                                }
                                for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
                                    printWriter.print(urgencies[r] + ", ");
                                }
                            } else {
                                for (int i = project.getReleases().getReleaseCount(); i >= releaseNr - 1; i--) {
                                    urgencies[i] = tempUrgency;
                                    tempUrgency = tempUrgency / 2;
                                }
                                for (int i = 0; i < releaseNr - 1; i++) {
                                    urgencies[i] = 0;
                                }
                            }
                        }
                    } else if (deadlineCurve == (Urgency.DEADLINE_MASK & Urgency.LATEST)) {
                        if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
                            for (int r = 0; r < releaseNr - 1; r++) {
                                printWriter.print(urgency + ", ");
                            }
                            for (int r = releaseNr; r < project.getReleases().getReleaseCount(); r++) {
                                printWriter.print("0, ");
                            }
                        } else {
                            int[] urgencies = new int[project.getReleases().getReleaseCount() + 1];
                            int tempUrgency = urgency;
                            if (releaseNr - ((urgency > 3)
                                    ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)) > 0) {
                                for (int i = (releaseNr - 1) - ((urgency > 3)
                                        ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)); i < releaseNr; i++) {
                                    urgencies[i] = tempUrgency;
                                    tempUrgency = tempUrgency / 2;
                                }
                                for (int i = releaseNr; i < project.getReleases().getReleaseCount(); i++) {
                                    urgencies[i] = 0;
                                }
                                if (releaseNr - (int) ((urgency > 3)
                                        ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)) > 0) {
                                    for (int i = 0; i < (releaseNr - 1) - ((urgency > 3)
                                            ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)); i++) {
                                        urgencies[i] = urgency;
                                    }
                                }
                                for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
                                    printWriter.print(urgencies[r] + ", ");
                                }
                            } else {
                                for (int i = project.getReleases().getReleaseCount(); i >= releaseNr - 1; i--) {
                                    urgencies[i] = tempUrgency;
                                    tempUrgency = tempUrgency / 2;
                                }
                                for (int i = 0; i < releaseNr - 1; i++) {
                                    urgencies[i] = 0;
                                }
                            }
                        }
                    } else {
                        if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
                            for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                                if (r == urgency - 1) {
                                    printWriter.print(urgency + ", ");
                                } else {
                                    printWriter.print("0, ");
                                }
                            }
                        } else {
                            int[] urgencies = new int[project.getReleases().getReleaseCount() + 1];
                            urgencies[releaseNr - 1] = urgency;
                            int tempUrgency = urgency;
                            for (int r = releaseNr; r <= project.getReleases().getReleaseCount(); r++) {
                                tempUrgency = tempUrgency / 2;
                                urgencies[r] = tempUrgency;
                            }
                            tempUrgency = urgency;
                            for (int r = 0; r < releaseNr - 1; r++) {
                                tempUrgency = tempUrgency / 2;
                                urgencies[r] = tempUrgency;
                            }
                            for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
                                printWriter.print(urgencies[r] + ", ");
                            }
                        }
                    }
                }
                printWriter.print(valueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                        project.getFeatures().getFeature(f)) + ", ");
                printWriter.print("\n");
            }

            if (ModDep.getValueAndUrgency().getValueAndUrgencyCount() > 0) {
                ValueAndUrgency newValueAndUrgency = ModDep.getValueAndUrgency();
                for (int f = 0; f < ModDep.getFeatures().getFeatureCount(); f++) {
                    int newUrgency = newValueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                            ModDep.getFeatures().getFeature(f));
                    if (newUrgency == 0) {
                        for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                            printWriter.print("0, ");
                        }
                    } else {
                        Release release = newValueAndUrgency.getUrgencyRelease(project.getStakeholders().getStakeholder(s),
                                ModDep.getFeatures().getFeature(f));
                        int releaseNr = project.getReleases().getReleaseCount();
                        int deadlineCurve = newValueAndUrgency.getDeadlineCurve(project.getStakeholders().getStakeholder(s),
                                ModDep.getFeatures().getFeature(f));
                        if (deadlineCurve == (Urgency.DEADLINE_MASK & Urgency.EARLIEST)) {
                            if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
                                for (int r = 0; r < releaseNr - 2; r++) {
                                    printWriter.print("0, ");
                                }
                                for (int r = releaseNr - 1; r < project.getReleases().getReleaseCount(); r++) {
                                    printWriter.print(newUrgency + ", ");
                                }
                            } else {
                                int[] urgencies = new int[project.getReleases().getReleaseCount() + 1];
                                int tempUrgency = newUrgency;
                                if ((project.getReleases().getReleaseCount() + 1) - releaseNr >= ((newUrgency > 3)
                                        ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1))) {
                                    for (int i = (releaseNr - 1) + (int) Math.round(newUrgency / 2.5f); i >= releaseNr - 1; i--) {
                                        urgencies[i] = tempUrgency;
                                        tempUrgency = tempUrgency / 2;
                                    }
                                    for (int i = 0; i < releaseNr - 1; i++) {
                                        urgencies[i] = 0;
                                    }
                                    if (releaseNr + ((newUrgency > 3)
                                            ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)) < project.getReleases().getReleaseCount() + 1) {
                                        for (int i = (releaseNr) + ((newUrgency > 3)
                                                ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)); i <= project.getReleases().getReleaseCount(); i++) {
                                            urgencies[i] = newUrgency;
                                        }
                                    }
                                    for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
                                        printWriter.print(urgencies[r] + ", ");
                                    }
                                } else {
                                    for (int i = project.getReleases().getReleaseCount(); i >= releaseNr - 1; i--) {
                                        urgencies[i] = tempUrgency;
                                        tempUrgency = tempUrgency / 2;
                                    }
                                    for (int i = 0; i < releaseNr - 1; i++) {
                                        urgencies[i] = 0;
                                    }
                                }
                            }
                        } else if (deadlineCurve == (Urgency.DEADLINE_MASK & Urgency.LATEST)) {
                            if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
                                for (int r = 0; r < releaseNr - 1; r++) {
                                    printWriter.print(newUrgency + ", ");
                                }
                                for (int r = releaseNr; r < project.getReleases().getReleaseCount(); r++) {
                                    printWriter.print("0, ");
                                }
                            } else {
                                int[] urgencies = new int[project.getReleases().getReleaseCount() + 1];
                                int tempUrgency = newUrgency;
                                if (releaseNr - ((newUrgency > 3)
                                        ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)) > 0) {
                                    for (int i = (releaseNr - 1) - ((newUrgency > 3)
                                            ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)); i < releaseNr; i++) {
                                        urgencies[i] = tempUrgency;
                                        tempUrgency = tempUrgency / 2;
                                    }
                                    for (int i = releaseNr; i < project.getReleases().getReleaseCount(); i++) {
                                        urgencies[i] = 0;
                                    }
                                    if (releaseNr - (int) ((newUrgency > 3)
                                            ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)) > 0) {
                                        for (int i = 0; i < (releaseNr - 1) - ((newUrgency > 3)
                                                ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)); i++) {
                                            urgencies[i] = newUrgency;
                                        }
                                    }
                                    for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
                                        printWriter.print(urgencies[r] + ", ");
                                    }
                                } else {
                                    for (int i = project.getReleases().getReleaseCount(); i >= releaseNr - 1; i--) {
                                        urgencies[i] = tempUrgency;
                                        tempUrgency = tempUrgency / 2;
                                    }
                                    for (int i = 0; i < releaseNr - 1; i++) {
                                        urgencies[i] = 0;
                                    }
                                }
                            }
                        } else {
                            if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
                                for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                                    if (r == newUrgency - 1) {
                                        printWriter.print(newUrgency + ", ");
                                    } else {
                                        printWriter.print("0, ");
                                    }
                                }
                            } else {
                                int[] urgencies = new int[project.getReleases().getReleaseCount() + 1];
                                urgencies[releaseNr - 1] = newUrgency;
                                int tempUrgency = newUrgency;
                                for (int r = releaseNr; r <= project.getReleases().getReleaseCount(); r++) {
                                    tempUrgency = tempUrgency / 2;
                                    urgencies[r] = tempUrgency;
                                }
                                tempUrgency = newUrgency;
                                for (int r = 0; r < releaseNr - 1; r++) {
                                    tempUrgency = tempUrgency / 2;
                                    urgencies[r] = tempUrgency;
                                }
                                for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
                                    printWriter.print(urgencies[r] + ", ");
                                }
                            }
                        }
                    }
                    printWriter.print(valueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                            project.getFeatures().getFeature(f)) + ", ");
                    printWriter.print("\n");
                }
            }
        }
        
        printWriter.println("]);");
    }
    */
    
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
            for (int rel = 0; rel < project.getReleases().getReleaseCount() + 1; rel++) {
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
                if (rel == project.getReleases().getReleaseCount()) {
                    if(postponedUrgency) {
                        printWriter.print(Math.round(project.getReleases().getRelease(rel - 1).getImportance() * temp) + ", \n");
                    }
                } else {
                    printWriter.print(Math.round(project.getReleases().getRelease(rel).getImportance() * temp) + ", ");
                }
            }
        }

        

        printWriter.println("|];");
    }

    /*
    public static Project ModifyingDependencyConversion(Project project) {
        Project ModDep = new Project("ModifyingDependencies");
        if (project.getDependencies().getTypedDependancyCount(ModifyingParameterDependency.class, Dependency.CC) > 0) {
            ModifyingParameterDependency[] CcDS = project.getDependencies().getTypedDependencies(ModifyingParameterDependency.class, Dependency.CC);

            for (int dep = 0; dep < CcDS.length; dep++) {
                Feature f = ModDep.getFeatures().addFeature();
                f.setName(CcDS[dep].getSecondary().getName() + "' (Cost Changed)");
                for (int r = 0; r < project.getResources().getResourceCount(); r++) {
                    if (CcDS[dep].getSecondary().hasConsumption(project.getResources().getResource(r))) {
                        f.setConsumption(project.getResources().getResource(r),
                                CcDS[dep].getChange(Feature.class).getConsumption(project.getResources().getResource(r)));
                    }
                }

                for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
                    int value = project.getValueAndUrgency().getValue(project.getStakeholders().getStakeholder(s), f);
                    if (value > 0) {
                        ModDep.getValueAndUrgency().setValue(project.getStakeholders().getStakeholder(s), f, value);
                        //for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                            ModDep.getValueAndUrgency().setUrgency(project.getStakeholders().getStakeholder(s), f,
                                    //project.getReleases().getRelease(r),
                                    project.getValueAndUrgency().getUrgency(project.getStakeholders().getStakeholder(s), f));
                        //}
                    }
                }

                ModDep.getDependencies().addOrderDependency(f, CcDS[dep].getPrimary(), Dependency.SOFTPRECEDENCE);
                ModDep.getDependencies().addOrderDependency(CcDS[dep].getPrimary(), CcDS[dep].getSecondary(), Dependency.HARDPRECEDENCE);
                ModDep.getDependencies().addOrderDependency(CcDS[dep].getPrimary(), f, Dependency.XOR);
                // Add all primary dependencies and group dependencies to f
                DependencyManager.duplicateDependencies(project, f, CcDS[dep].getPrimary());
            }
        }

        if (project.getDependencies().getTypedDependancyCount(ModifyingParameterDependency.class, Dependency.CV) > 0) {
            ModifyingParameterDependency[] CvDS = project.getDependencies().getTypedDependencies(ModifyingParameterDependency.class, Dependency.CV);

            for (int dep = 0; dep < CvDS.length; dep++) {
                Feature f = ModDep.getFeatures().addFeature();
                f.setName(CvDS[dep].getSecondary().getName() + "' (Value Changed)");
                for (int r = 0; r < project.getResources().getResourceCount(); r++) {
                    if (CvDS[dep].getSecondary().hasConsumption(project.getResources().getResource(r))) {
                        f.setConsumption(project.getResources().getResource(r),
                                CvDS[dep].getSecondary().getConsumption(project.getResources().getResource(r)));
                    }
                }

                ValueAndUrgency values = CvDS[dep].getChange(ValueAndUrgency.class);
                for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
                    if (values.getValue(project.getStakeholders().getStakeholder(s), f) > 0) {
                        ModDep.getValueAndUrgency().setValue(project.getStakeholders().getStakeholder(s), f,
                                values.getValue(project.getStakeholders().getStakeholder(s), f));
                        //for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                            ModDep.getValueAndUrgency().setUrgency(project.getStakeholders().getStakeholder(s), f,
                                    //project.getReleases().getRelease(r),
                                    project.getValueAndUrgency().getUrgency(project.getStakeholders().getStakeholder(s), f));
                        //}
                    }
                }

                ModDep.getDependencies().addOrderDependency(f, CvDS[dep].getPrimary(), Dependency.SOFTPRECEDENCE);
                ModDep.getDependencies().addOrderDependency(CvDS[dep].getPrimary(), CvDS[dep].getSecondary(), Dependency.HARDPRECEDENCE);
                ModDep.getDependencies().addOrderDependency(CvDS[dep].getPrimary(), f, Dependency.XOR);
                // Add all primary dependencies and group dependencies to f
            }
        }

        if (project.getDependencies().getTypedDependancyCount(ModifyingParameterDependency.class, Dependency.CU) > 0) {
            ModifyingParameterDependency[] CuDS = project.getDependencies().getTypedDependencies(ModifyingParameterDependency.class, Dependency.CU);

            for (int dep = 0; dep < CuDS.length; dep++) {
                Feature f = ModDep.getFeatures().addFeature();
                f.setName(CuDS[dep].getSecondary().getName() + "' (Urgency Changed)");
                for (int r = 0; r < project.getResources().getResourceCount(); r++) {
                    if (CuDS[dep].getSecondary().hasConsumption(project.getResources().getResource(r))) {
                        f.setConsumption(project.getResources().getResource(r),
                                CuDS[dep].getSecondary().getConsumption(project.getResources().getResource(r)));
                    }
                }

                ValueAndUrgency urgencies = CuDS[dep].getChange(ValueAndUrgency.class);
                for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
                    int value = project.getValueAndUrgency().getValue(project.getStakeholders().getStakeholder(s), f);
                    if (value > 0) {
                        ModDep.getValueAndUrgency().setValue(project.getStakeholders().getStakeholder(s), f, value);
                        //for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                            ModDep.getValueAndUrgency().setUrgency(project.getStakeholders().getStakeholder(s), f,
                                    //project.getReleases().getRelease(r),
                                    urgencies.getUrgency(project.getStakeholders().getStakeholder(s), f));
                        //}
                    }
                }

                ModDep.getDependencies().addOrderDependency(f, CuDS[dep].getPrimary(), Dependency.SOFTPRECEDENCE);
                ModDep.getDependencies().addOrderDependency(CuDS[dep].getPrimary(), CuDS[dep].getSecondary(), Dependency.HARDPRECEDENCE);
                ModDep.getDependencies().addOrderDependency(CuDS[dep].getPrimary(), f, Dependency.XOR);
                // Add all primary dependencies and group dependencies to f
            }
        }
        return ModDep;
    }
    */
}
