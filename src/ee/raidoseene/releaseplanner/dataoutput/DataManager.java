/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataoutput;

import ee.raidoseene.releaseplanner.backend.ResourceManager;
import ee.raidoseene.releaseplanner.datamodel.Dependency;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.FixedDependency;
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

    public static void saveDataFile(Project project) throws Exception {
        File dir = ResourceManager.createDirectoryFromFile(new File(project.getStorage()));
        File file = new File(dir, "data.dzn");

        try (PrintWriter pw = new PrintWriter(file)) { // try with resource: printwriter closes automatically!
            DataManager dm = new DataManager(project, pw);

            Project ModDep = new Project("ModifyingDependencies");

            dm.printFailHeader();
            dm.printProjectParameters(ModDep.getFeatures().getFeatureCount());
            dm.printParamImportance();
            dm.printDependencies(ModDep);
            dm.printResources();
            dm.printFeatures(ModDep);
            //dm.printModifyingInterdependencies();
            dm.printStakeholders(ModDep);
        }
    }

    private DataManager(Project project, PrintWriter pw) {
        this.project = project;
        this.printWriter = pw;
    }

    /*
    private boolean ModifyingDependencyConversion(Project ModDep) {

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
        return true;
    }
    */

    private void printFailHeader() {
        printWriter.println("% Release planner data file\n% =========================\n\n");
    }

    private void printProjectParameters(int featureCount) {
        printWriter.println("% Number of features/releases/resources/stakeholders");
        printWriter.println("F = " + (project.getFeatures().getFeatureCount() + featureCount) + ";");
        printWriter.println("Rel = " + project.getReleases().getReleaseCount() + ";");
        printWriter.println("Res = " + project.getResources().getResourceCount() + ";");
        printWriter.println("S = " + project.getStakeholders().getStakeholderCount() + ";");
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

    private void printDependencies(Project proj) {

        FixedDependency[] FixDS = project.getDependencies().getTypedDependencies(FixedDependency.class, Dependency.FIXED);
        Interdependency[] AndDS = project.getDependencies().getTypedDependencies(Interdependency.class, Dependency.AND);
        Interdependency[] ReqDS = project.getDependencies().getTypedDependencies(Interdependency.class, Dependency.REQ);
        Interdependency[] PreDS = project.getDependencies().getTypedDependencies(Interdependency.class, Dependency.PRE);
        Interdependency[] XorDS = project.getDependencies().getTypedDependencies(Interdependency.class, Dependency.XOR);

        printWriter.println("% FIXED features / AND features / REQUIRED features / PRECEDING features / XOR features");

        // Fixed release
        printWriter.println("FIX = " + FixDS.length + ";");
        printWriter.print("fx = [| ");
        if (FixDS.length > 0) {
            for (int i = 0; i < FixDS.length; i++) {
                printWriter.print((project.getFeatures().getFeatureIndex(FixDS[i].getFeature()) + 1)
                        + ", " + (project.getReleases().getReleaseIndex(FixDS[i].getRelease()) + 1) + ", ");
            }
            printWriter.println("|];");
        } else {
            printWriter.println("1, 1, |];");
        }

        // AND dependency
        printWriter.println("AND = " + AndDS.length + ";");
        printWriter.print("and = [| ");
        if (AndDS.length > 0) {
            for (int i = 0; i < AndDS.length; i++) {
                printWriter.print((project.getFeatures().getFeatureIndex(AndDS[i].getPrimary()) + 1)
                        + ", " + (project.getFeatures().getFeatureIndex(AndDS[i].getSecondary()) + 1) + ", ");
            }
            printWriter.println("|];");
        } else {
            printWriter.println("1, 1, |];");
        }

        // REQUIRES dependency
        printWriter.println("REQ = " + ReqDS.length + ";");
        printWriter.print("req = [| ");
        if (ReqDS.length > 0) {
            for (int i = 0; i < ReqDS.length; i++) {
                printWriter.print((project.getFeatures().getFeatureIndex(ReqDS[i].getPrimary()) + 1)
                        + ", " + (project.getFeatures().getFeatureIndex(ReqDS[i].getSecondary()) + 1) + ", ");
            }
        } else if (proj.getDependencies().getDependencyCount() > 0) {
            Interdependency[] newReqDS = proj.getDependencies().getTypedDependencies(Interdependency.class, Dependency.REQ);
            for (int i = 0; i < newReqDS.length; i++) {
                printWriter.print((proj.getFeatures().getFeatureIndex(newReqDS[i].getPrimary()) + 1
                        + project.getFeatures().getFeatureCount())
                        + ", " + (project.getFeatures().getFeatureIndex(newReqDS[i].getSecondary()) + 1) + ", ");
            }
        } else {
            printWriter.print("1, 1, ");
        }
        printWriter.println("|];");

        // PRECEDES dependency
        printWriter.println("PRE = " + PreDS.length + ";");
        printWriter.print("pre = [| ");
        if (PreDS.length > 0) {
            for (int i = 0; i < PreDS.length; i++) {
                printWriter.print((project.getFeatures().getFeatureIndex(PreDS[i].getPrimary()) + 1)
                        + ", " + (project.getFeatures().getFeatureIndex(PreDS[i].getSecondary()) + 1) + ", ");
            }
        } else if (proj.getDependencies().getDependencyCount() > 0) {
            Interdependency[] newPreDS = proj.getDependencies().getTypedDependencies(Interdependency.class, Dependency.PRE);
            for (int i = 0; i < newPreDS.length; i++) {
                printWriter.print((project.getFeatures().getFeatureIndex(newPreDS[i].getPrimary()) + 1)
                        + ", " + (project.getFeatures().getFeatureIndex(newPreDS[i].getSecondary()) + 1) + ", ");
            }
        } else {
            printWriter.print("1, 1, ");
        }
        printWriter.println("|];");

        // XOR dependency
        printWriter.println("XOR = " + XorDS.length + ";");
        printWriter.print("xr = [| ");
        if (XorDS.length > 0) {
            for (int i = 0; i < XorDS.length; i++) {
                printWriter.print((project.getFeatures().getFeatureIndex(XorDS[i].getPrimary()) + 1)
                        + ", " + (project.getFeatures().getFeatureIndex(XorDS[i].getSecondary()) + 1) + ", ");
            }
        } else if (proj.getDependencies().getDependencyCount() > 0) {
            Interdependency[] newXorDS = proj.getDependencies().getTypedDependencies(Interdependency.class, Dependency.XOR);
            for (int i = 0; i < newXorDS.length; i++) {
                printWriter.print((project.getFeatures().getFeatureIndex(newXorDS[i].getPrimary()) + 1)
                        + ", " + ((proj.getFeatures().getFeatureIndex(newXorDS[i].getSecondary()) + 1)
                        + project.getFeatures().getFeatureCount()) + ", ");
            }
        } else {
            printWriter.println("1, 1, ");
        }
        printWriter.println("|];");

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

    /*
     private void printModifyingInterdependencies() {
     ModifyingInterdependency[] CcDS = project.getInterdependencies().getTypedDependencies(ModifyingInterdependency.class, Dependency.CC);
     ModifyingInterdependency[] CvDS = project.getInterdependencies().getTypedDependencies(ModifyingInterdependency.class, Dependency.CV);
     ModifyingInterdependency[] CuDS = project.getInterdependencies().getTypedDependencies(ModifyingInterdependency.class, Dependency.CU);

     printWriter.println("% Change in Cost  (feature, feature, costs 1, ..., cost n)");

     // CHANGE IN COST dependency
     printWriter.println("CC = " + CcDS.length + ";");
     printWriter.print("cc = [| ");
     if (CcDS.length > 0) {
     for (int i = 0; i < CcDS.length; i++) {
     printWriter.print((project.getFeatures().getFeatureIndex(CcDS[i].getPrimary()) + 1)
     + ", " + (project.getFeatures().getFeatureIndex(CcDS[i].getSecondary()) + 1)
     + "(missing costs)" + ", ");
     }
     printWriter.println("|];");
     } else {
     printWriter.println("1, 1, |];");
     }

     // CHANGE IN VALUE dependency
     printWriter.println("CV = " + CvDS.length + ";");
     printWriter.print("cv = [| ");
     if (CvDS.length > 0) {
     for (int i = 0; i < CvDS.length; i++) {
     printWriter.print((project.getFeatures().getFeatureIndex(CvDS[i].getPrimary()) + 1)
     + ", " + (project.getFeatures().getFeatureIndex(CvDS[i].getSecondary()) + 1)
     + "{missing values}" + ", ");
     }
     printWriter.println("|];");
     } else {
     printWriter.println("1, 1, |];");
     }

     // CHANGE IN URGENCY dependency
     printWriter.println("CU = " + CuDS.length + ";");
     printWriter.print("cu = [| ");
     if (CuDS.length > 0) {
     for (int i = 0; i < CuDS.length; i++) {
     printWriter.print((project.getFeatures().getFeatureIndex(CuDS[i].getPrimary()) + 1)
     + ", " + (project.getFeatures().getFeatureIndex(CuDS[i].getSecondary()) + 1)
     + "{missing urgencies}" + ", ");
     }
     printWriter.println("|];");
     } else {
     printWriter.println("1, 1, |];");
     }

     printWriter.println("% =========================");
     }
     */
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
                    printWriter.print("\n");
                }
            }
        }
        printWriter.println("]);");
    }
}
