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
import ee.raidoseene.releaseplanner.datamodel.FixedDependency;
import ee.raidoseene.releaseplanner.datamodel.Group;
import ee.raidoseene.releaseplanner.datamodel.GroupDependency;
import ee.raidoseene.releaseplanner.datamodel.Interdependency;
import ee.raidoseene.releaseplanner.datamodel.ModifyingInterdependency;
import ee.raidoseene.releaseplanner.datamodel.Project;
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
    private final PrintWriter printWriter;

    public static void errorLoging(List<String> input) throws Exception {
        // TO DO: create a directory for logs if it do not exist
        // TO DO: create a new log file if it do not exist
        // TO DO: append input into the log file
    }

    public static void jacopOutput(String input) {
        // TO DO: create or write over project named jacop output file
    }

    public static File[] initiateDataOutput(Project project, boolean codeOutput) throws Exception {
        File[] files = new File[2];
        File data = saveDataFile(project, codeOutput);
        files[0] = data;
        if (codeOutput) {
            // export solver code file with dependencies
            int group = project.getDependencies().getTypedDependancyCount(GroupDependency.class, Dependency.GROUP);
            Settings settings = SettingsManager.getCurrentSettings();
            File code = SolverCodeManager.saveSolverCodeFile(project, settings.getResourceShifting(),
                    group > 0 ? true : false);
            files[1] = code;
        }
        return files;
    }

    private static File saveDataFile(Project project, boolean codeOutput) throws Exception {
        File dir = ResourceManager.createDirectoryFromFile(new File(project.getStorage()));
        File file = new File(dir, "data.dzn");

        try (PrintWriter pw = new PrintWriter(file)) { // try with resource: printwriter closes automatically!
            DataManager dm = new DataManager(project, pw);

            //Project modDep = new Project("ModifyingDependencies");
            Project modDep = dm.ModifyingDependencyConversion(project);
            //dm.ModifyingDependencyConversion(modDep);

            if (codeOutput) {
                dm.printFailHeader();
                dm.printProjectParameters(modDep.getFeatures().getFeatureCount(), true);
                dm.printWriter.println(DependencyManager.getGroupDependenciesData(project, modDep));
                dm.printResources();
                dm.printFeatures(modDep);
                dm.printGroups(true);
                dm.printWAS();
            } else {
                dm.printFailHeader();
                dm.printProjectParameters(modDep.getFeatures().getFeatureCount(), false);
                dm.printParamImportance();
                //dm.printDependencies(modDep);
                dm.printWriter.println(DependencyManager.getDependenciesData(project, modDep, false));
                dm.printResources();
                dm.printFeatures(modDep);
                dm.printGroups(false);
                dm.printStakeholders(modDep);
            }
        }
        return file;
    }

    private DataManager(Project project, PrintWriter pw) {
        this.project = project;
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
        if(!codeOutput) {
            printWriter.println("S = " + project.getStakeholders().getStakeholderCount() + ";");
        }
        printWriter.println("% =========================\n");
    }

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

    /*
     private void printDependencies(Project proj) {

     FixedDependency[] FixDS = project.getDependencies().getTypedDependencies(FixedDependency.class, Dependency.FIXED);
     Interdependency[] AndDS = project.getDependencies().getTypedDependencies(Interdependency.class, Dependency.AND);
     Interdependency[] ReqDS = project.getDependencies().getTypedDependencies(Interdependency.class, Dependency.REQ);
     Interdependency[] PreDS = project.getDependencies().getTypedDependencies(Interdependency.class, Dependency.PRE);
     Interdependency[] XorDS = project.getDependencies().getTypedDependencies(Interdependency.class, Dependency.XOR);
     GroupDependency[] AtLeastDS = project.getDependencies().getTypedDependencies(GroupDependency.class, Dependency.ATLEAST);
     GroupDependency[] ExactlyDS = project.getDependencies().getTypedDependencies(GroupDependency.class, Dependency.EXACTLY);
     GroupDependency[] AtMostDS = project.getDependencies().getTypedDependencies(GroupDependency.class, Dependency.ATMOST);

     printWriter.println("% FIXED features / AND features / REQUIRED features / PRECEDING features / XOR features");
     printWriter.println("% Group: AtLeast / Exactly / AtMost");

     // Fixed release
     printWriter.println("FIX = " + FixDS.length + ";");
     printWriter.print("fx = [|");
     if (FixDS.length > 0) {
     for (int i = 0; i < FixDS.length; i++) {
     printWriter.print(" " + (project.getFeatures().getFeatureIndex(FixDS[i].getFeature()) + 1)
     + ", " + (project.getReleases().getReleaseIndex(FixDS[i].getRelease()) + 1) + ", |");
     }
     printWriter.println("];");
     } else {
     printWriter.println(" 0, 0, |];");
     }

     // AND dependency
     printWriter.println("AND = " + AndDS.length + ";");
     printWriter.print("and = [|");
     if (AndDS.length > 0) {
     for (int i = 0; i < AndDS.length; i++) {
     printWriter.print(" " + (project.getFeatures().getFeatureIndex(AndDS[i].getPrimary()) + 1)
     + ", " + (project.getFeatures().getFeatureIndex(AndDS[i].getSecondary()) + 1) + ", |");
     }
     printWriter.println("];");
     } else {
     printWriter.println(" 0, 0, |];");
     }

     // REQUIRES dependency
     printWriter.println("REQ = " + ReqDS.length + ";");
     printWriter.print("req = [|");
     if (ReqDS.length > 0) {
     for (int i = 0; i < ReqDS.length; i++) {
     printWriter.print(" " + (project.getFeatures().getFeatureIndex(ReqDS[i].getPrimary()) + 1)
     + ", " + (project.getFeatures().getFeatureIndex(ReqDS[i].getSecondary()) + 1) + ", |");
     }
     } else if (proj.getDependencies().getDependencyCount() > 0) {
     Interdependency[] newReqDS = proj.getDependencies().getTypedDependencies(Interdependency.class, Dependency.REQ);
     for (int i = 0; i < newReqDS.length; i++) {
     printWriter.print(" " + (proj.getFeatures().getFeatureIndex(newReqDS[i].getPrimary()) + 1
     + project.getFeatures().getFeatureCount())
     + ", " + (project.getFeatures().getFeatureIndex(newReqDS[i].getSecondary()) + 1) + ", |");
     }
     } else {
     printWriter.print(" 0, 0, |");
     }
     printWriter.println("];");

     // PRECEDES dependency
     printWriter.println("PRE = " + PreDS.length + ";");
     printWriter.print("pre = [|");
     if (PreDS.length > 0) {
     for (int i = 0; i < PreDS.length; i++) {
     printWriter.print(" " + (project.getFeatures().getFeatureIndex(PreDS[i].getPrimary()) + 1)
     + ", " + (project.getFeatures().getFeatureIndex(PreDS[i].getSecondary()) + 1) + ", |");
     }
     } else if (proj.getDependencies().getDependencyCount() > 0) {
     Interdependency[] newPreDS = proj.getDependencies().getTypedDependencies(Interdependency.class, Dependency.PRE);
     for (int i = 0; i < newPreDS.length; i++) {
     printWriter.print(" " + (project.getFeatures().getFeatureIndex(newPreDS[i].getPrimary()) + 1)
     + ", " + (project.getFeatures().getFeatureIndex(newPreDS[i].getSecondary()) + 1) + ", |");
     }
     } else {
     printWriter.print(" 0, 0, |");
     }
     printWriter.println("];");

     // XOR dependency
     printWriter.println("XOR = " + XorDS.length + ";");
     printWriter.print("xr = [|");
     if (XorDS.length > 0) {
     for (int i = 0; i < XorDS.length; i++) {
     printWriter.print(" " + (project.getFeatures().getFeatureIndex(XorDS[i].getPrimary()) + 1)
     + ", " + (project.getFeatures().getFeatureIndex(XorDS[i].getSecondary()) + 1) + ", |");
     }
     } else if (proj.getDependencies().getDependencyCount() > 0) {
     Interdependency[] newXorDS = proj.getDependencies().getTypedDependencies(Interdependency.class, Dependency.XOR);
     for (int i = 0; i < newXorDS.length; i++) {
     printWriter.print(" " + (project.getFeatures().getFeatureIndex(newXorDS[i].getPrimary()) + 1)
     + ", " + ((proj.getFeatures().getFeatureIndex(newXorDS[i].getSecondary()) + 1)
     + project.getFeatures().getFeatureCount()) + ", |");
     }
     } else {
     printWriter.print(" 0, 0, |");
     }
     printWriter.println("];\n");

     printWriter.println("ATLEAST = " + 0 + ";");
     printWriter.print("atLeast = [|");
     if (AtLeastDS.length > 0) {
     for (int i = 0; i < AtLeastDS.length; i++) {
     printWriter.print(" " + (project.getGroups().getGroupIndex(AtLeastDS[i].getGroup()) + 1)
     + ", " + (AtLeastDS[i].getFeatureCount()) + ", |");
     }
     } else {
     printWriter.print(" 0, 0, |");
     }
     printWriter.println("];");

     printWriter.println("EXACTLY = " + 0 + ";");
     printWriter.print("exactly = [|");
     if (ExactlyDS.length > 0) {
     for (int i = 0; i < ExactlyDS.length; i++) {
     printWriter.print(" " + (project.getGroups().getGroupIndex(ExactlyDS[i].getGroup()) + 1)
     + ", " + (ExactlyDS[i].getFeatureCount()) + ", |");
     }
     } else {
     printWriter.print(" 0, 0, |");
     }
     printWriter.println("];");

     printWriter.println("ATMOST = " + 0 + ";");
     printWriter.print("atMost = [|");
     if (AtMostDS.length > 0) {
     for (int i = 0; i < AtMostDS.length; i++) {
     printWriter.print(" " + (project.getGroups().getGroupIndex(AtMostDS[i].getGroup()) + 1)
     + ", " + (AtMostDS[i].getFeatureCount()) + ", |");
     }
     } else {
     printWriter.print(" 0, 0, |");
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
        for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
            ValueAndUrgency valueAndUrgency = project.getValueAndUrgency();
            printWriter.println("% stakeholder " + (s + 1));
            for (int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
                for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                    printWriter.print(valueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                            project.getFeatures().getFeature(f),
                            project.getReleases().getRelease(r)) + ", ");
                }
                printWriter.print(valueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                        project.getFeatures().getFeature(f),
                        null) + ", ");

                printWriter.print("\n");
            }
            if (ModDep.getValueAndUrgency().getValueAndUrgencyCount() > 0) {
                ValueAndUrgency newValueAndUrgency = ModDep.getValueAndUrgency();
                for (int f = 0; f < ModDep.getFeatures().getFeatureCount(); f++) {
                    for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                        printWriter.print(newValueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                                ModDep.getFeatures().getFeature(f),
                                project.getReleases().getRelease(r)) + ", ");
                    }
                    printWriter.print(valueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                            project.getFeatures().getFeature(f),
                            null) + ", ");

                    printWriter.print("\n");
                }
            }
        }
        printWriter.println("]);");
    }

    private void printWAS() { // Missing change in extra features WAS
        printWriter.print("WAS = [");
        for (int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
            printWriter.print("| 0, ");
            for (int rel = 0; rel < project.getReleases().getReleaseCount(); rel++) {
                /*printWriter.print(project.getReleases().getRelease(rel).getImportance() *
                 project.getStakeholders().getStakeholder(0).getImportance() *
                 project.getValueAndUrgency().getValue(project.getStakeholders().getStakeholder(0),
                 project.getFeatures().getFeature(f)) * project.getValueAndUrgency().getUrgency(
                 project.getStakeholders().getStakeholder(0),
                 project.getFeatures().getFeature(f), project.getReleases().getRelease(rel)) + ", ");*/
                int temp = 0;
                for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
                    temp += project.getStakeholders().getStakeholder(s).getImportance()
                            * project.getValueAndUrgency().getValue(
                            project.getStakeholders().getStakeholder(s),
                            project.getFeatures().getFeature(f))
                            * project.getValueAndUrgency().getUrgency(
                            project.getStakeholders().getStakeholder(s),
                            project.getFeatures().getFeature(f),
                            project.getReleases().getRelease(rel));
                }
                printWriter.print((project.getReleases().getRelease(rel).getImportance() * temp) + ", ");
                if (rel == project.getReleases().getReleaseCount() - 1) {
                    printWriter.print("1, \n");
                }
            }
        }

        /*
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
         int release = valueAndUrgency.getUrgencyRelease(project.getStakeholders().getStakeholder(s),
         project.getFeatures().getFeature(f));
         int deadlineCurve = valueAndUrgency.getDeadlineCurve(project.getStakeholders().getStakeholder(s),
         project.getFeatures().getFeature(f));
         if (deadlineCurve == (Urgency.DEADLYNE_MASK & Urgency.EARLIEST)) {
         if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
         for (int r = 0; r < release - 2; r++) {
         printWriter.print("0, ");
         }
         for (int r = release - 1; r < project.getReleases().getReleaseCount(); r++) {
         printWriter.print(urgency + ", ");
         }
         } else {
         int[] urgencies = new int[project.getReleases().getReleaseCount() + 1];
         int tempUrgency = urgency;
         if ((project.getReleases().getReleaseCount() + 1) - release >= ((urgency > 3)
         ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1))) {
         for (int i = (release - 1) + (int) Math.round(urgency / 2.5f); i >= release - 1; i--) {
         urgencies[i] = tempUrgency;
         tempUrgency = tempUrgency / 2;
         }
         for (int i = 0; i < release - 1; i++) {
         urgencies[i] = 0;
         }
         if (release + ((urgency > 3)
         ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)) < project.getReleases().getReleaseCount() + 1) {
         for (int i = (release) + ((urgency > 3)
         ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)); i <= project.getReleases().getReleaseCount(); i++) {
         urgencies[i] = urgency;
         }
         }
         for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
         printWriter.print(urgencies[r] + ", ");
         }
         } else {
         for (int i = project.getReleases().getReleaseCount(); i >= release - 1; i--) {
         urgencies[i] = tempUrgency;
         tempUrgency = tempUrgency / 2;
         }
         for (int i = 0; i < release - 1; i++) {
         urgencies[i] = 0;
         }
         }
         }
         } else if (deadlineCurve == (Urgency.DEADLYNE_MASK & Urgency.LATEST)) {
         if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
         for (int r = 0; r < release - 1; r++) {
         printWriter.print(urgency + ", ");
         }
         for (int r = release; r < project.getReleases().getReleaseCount(); r++) {
         printWriter.print("0, ");
         }
         } else {
         int[] urgencies = new int[project.getReleases().getReleaseCount() + 1];
         int tempUrgency = urgency;
         if (release - ((urgency > 3)
         ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)) > 0) {
         for (int i = (release - 1) - ((urgency > 3)
         ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)); i < release; i++) {
         urgencies[i] = tempUrgency;
         tempUrgency = tempUrgency / 2;
         }
         for (int i = release; i < project.getReleases().getReleaseCount(); i++) {
         urgencies[i] = 0;
         }
         if (release - (int) ((urgency > 3)
         ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)) > 0) {
         for (int i = 0; i < (release - 1) - ((urgency > 3)
         ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)); i++) {
         urgencies[i] = urgency;
         }
         }
         for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
         printWriter.print(urgencies[r] + ", ");
         }
         } else {
         for (int i = project.getReleases().getReleaseCount(); i >= release - 1; i--) {
         urgencies[i] = tempUrgency;
         tempUrgency = tempUrgency / 2;
         }
         for (int i = 0; i < release - 1; i++) {
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
         urgencies[release - 1] = urgency;
         int tempUrgency = urgency;
         for (int r = release; r <= project.getReleases().getReleaseCount(); r++) {
         tempUrgency = tempUrgency / 2;
         urgencies[r] = tempUrgency;
         }
         tempUrgency = urgency;
         for (int r = 0; r < release - 1; r++) {
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
         project.getFeatures().getFeature(f),
         null) + ", ");
         printWriter.print("\n");
         }
            
         if (ModDep.getValueAndUrgency().getValueAndUrgencyCount() > 0) {
         ValueAndUrgency newValueAndUrgency = ModDep.getValueAndUrgency();
         for (int f = 0; f < ModDep.getFeatures().getFeatureCount(); f++) {
         int newUrgency = newValueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
         project.getFeatures().getFeature(f));
         if (newUrgency == 0) {
         for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
         printWriter.print("0, ");
         }
         } else {
         int release = valueAndUrgency.getUrgencyRelease(project.getStakeholders().getStakeholder(s),
         project.getFeatures().getFeature(f));
         int deadlineCurve = valueAndUrgency.getDeadlineCurve(project.getStakeholders().getStakeholder(s),
         project.getFeatures().getFeature(f));
         if (deadlineCurve == (Urgency.DEADLYNE_MASK & Urgency.EARLIEST)) {
         if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
         for (int r = 0; r < release - 2; r++) {
         printWriter.print("0, ");
         }
         for (int r = release - 1; r < project.getReleases().getReleaseCount(); r++) {
         printWriter.print(newUrgency + ", ");
         }
         } else {
         int[] urgencies = new int[project.getReleases().getReleaseCount() + 1];
         int tempUrgency = newUrgency;
         if ((project.getReleases().getReleaseCount() + 1) - release >= ((newUrgency > 3)
         ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1))) {
         for (int i = (release - 1) + (int) Math.round(newUrgency / 2.5f); i >= release - 1; i--) {
         urgencies[i] = tempUrgency;
         tempUrgency = tempUrgency / 2;
         }
         for (int i = 0; i < release - 1; i++) {
         urgencies[i] = 0;
         }
         if (release + ((newUrgency > 3)
         ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)) < project.getReleases().getReleaseCount() + 1) {
         for (int i = (release) + ((newUrgency > 3)
         ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)); i <= project.getReleases().getReleaseCount(); i++) {
         urgencies[i] = newUrgency;
         }
         }
         for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
         printWriter.print(urgencies[r] + ", ");
         }
         } else {
         for (int i = project.getReleases().getReleaseCount(); i >= release - 1; i--) {
         urgencies[i] = tempUrgency;
         tempUrgency = tempUrgency / 2;
         }
         for (int i = 0; i < release - 1; i++) {
         urgencies[i] = 0;
         }
         }
         }
         } else if (deadlineCurve == (Urgency.DEADLYNE_MASK & Urgency.LATEST)) {
         if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
         for (int r = 0; r < release - 1; r++) {
         printWriter.print(newUrgency + ", ");
         }
         for (int r = release; r < project.getReleases().getReleaseCount(); r++) {
         printWriter.print("0, ");
         }
         } else {
         int[] urgencies = new int[project.getReleases().getReleaseCount() + 1];
         int tempUrgency = newUrgency;
         if (release - ((newUrgency > 3)
         ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)) > 0) {
         for (int i = (release - 1) - ((newUrgency > 3)
         ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)); i < release; i++) {
         urgencies[i] = tempUrgency;
         tempUrgency = tempUrgency / 2;
         }
         for (int i = release; i < project.getReleases().getReleaseCount(); i++) {
         urgencies[i] = 0;
         }
         if (release - (int) ((newUrgency > 3)
         ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)) > 0) {
         for (int i = 0; i < (release - 1) - ((newUrgency > 3)
         ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)); i++) {
         urgencies[i] = newUrgency;
         }
         }
         for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
         printWriter.print(urgencies[r] + ", ");
         }
         } else {
         for (int i = project.getReleases().getReleaseCount(); i >= release - 1; i--) {
         urgencies[i] = tempUrgency;
         tempUrgency = tempUrgency / 2;
         }
         for (int i = 0; i < release - 1; i++) {
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
         urgencies[release - 1] = newUrgency;
         int tempUrgency = newUrgency;
         for (int r = release; r <= project.getReleases().getReleaseCount(); r++) {
         tempUrgency = tempUrgency / 2;
         urgencies[r] = tempUrgency;
         }
         tempUrgency = newUrgency;
         for (int r = 0; r < release - 1; r++) {
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
         project.getFeatures().getFeature(f),
         null) + ", ");
         printWriter.print("\n");
         }
         }
         }*/

        printWriter.println("|];");
    }

    public static Project ModifyingDependencyConversion(Project project) {
        Project ModDep = new Project("ModifyingDependencies");
        if (project.getDependencies().getTypedDependancyCount(ModifyingInterdependency.class, Dependency.CC) > 0) {
            ModifyingInterdependency[] CcDS = project.getDependencies().getTypedDependencies(ModifyingInterdependency.class, Dependency.CC);

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
                        for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                            ModDep.getValueAndUrgency().setUrgency(project.getStakeholders().getStakeholder(s), f,
                                    project.getReleases().getRelease(r),
                                    project.getValueAndUrgency().getUrgency(project.getStakeholders().getStakeholder(s), f,
                                    project.getReleases().getRelease(r)));
                        }
                    }
                }

                ModDep.getDependencies().addInterdependency(f, CcDS[dep].getPrimary(), Dependency.REQ);
                ModDep.getDependencies().addInterdependency(CcDS[dep].getPrimary(), CcDS[dep].getSecondary(), Dependency.PRE);
                ModDep.getDependencies().addInterdependency(CcDS[dep].getPrimary(), f, Dependency.XOR);
            }
        }

        if (project.getDependencies().getTypedDependancyCount(ModifyingInterdependency.class, Dependency.CV) > 0) {
            ModifyingInterdependency[] CvDS = project.getDependencies().getTypedDependencies(ModifyingInterdependency.class, Dependency.CV);

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
                        for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                            ModDep.getValueAndUrgency().setUrgency(project.getStakeholders().getStakeholder(s), f,
                                    project.getReleases().getRelease(r),
                                    project.getValueAndUrgency().getUrgency(project.getStakeholders().getStakeholder(s), f,
                                    project.getReleases().getRelease(r)));
                        }
                    }
                }

                ModDep.getDependencies().addInterdependency(f, CvDS[dep].getPrimary(), Dependency.REQ);
                ModDep.getDependencies().addInterdependency(CvDS[dep].getPrimary(), CvDS[dep].getSecondary(), Dependency.PRE);
                ModDep.getDependencies().addInterdependency(CvDS[dep].getPrimary(), f, Dependency.XOR);
            }
        }

        if (project.getDependencies().getTypedDependancyCount(ModifyingInterdependency.class, Dependency.CU) > 0) {
            ModifyingInterdependency[] CuDS = project.getDependencies().getTypedDependencies(ModifyingInterdependency.class, Dependency.CU);

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
                        for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                            ModDep.getValueAndUrgency().setUrgency(project.getStakeholders().getStakeholder(s), f,
                                    project.getReleases().getRelease(r),
                                    urgencies.getUrgency(project.getStakeholders().getStakeholder(s), f,
                                    project.getReleases().getRelease(r)));
                        }
                    }
                }

                ModDep.getDependencies().addInterdependency(f, CuDS[dep].getPrimary(), Dependency.REQ);
                ModDep.getDependencies().addInterdependency(CuDS[dep].getPrimary(), CuDS[dep].getSecondary(), Dependency.PRE);
                ModDep.getDependencies().addInterdependency(CuDS[dep].getPrimary(), f, Dependency.XOR);
            }
        }
        return ModDep;
    }
}
