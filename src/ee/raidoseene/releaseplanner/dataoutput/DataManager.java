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
import ee.raidoseene.releaseplanner.datamodel.Urgencies;
import ee.raidoseene.releaseplanner.datamodel.Urgency;
import ee.raidoseene.releaseplanner.datamodel.Value;
import ee.raidoseene.releaseplanner.datamodel.Values;
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
    private static boolean modifier;

    public static void saveDataFile(Project project) throws Exception {
        File dir = ResourceManager.createDirectoryFromFile(new File(project.getStorage()));
        File file = new File(dir, "data.dzn");

        try (PrintWriter pw = new PrintWriter(file)) { // try with resource: printwriter closes automatically!
            DataManager dm = new DataManager(project, pw);

            Project ModDep = new Project("ModifyingDependencies");
            modifier = dm.ModifyingDependencyConversion(ModDep);

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

    private boolean ModifyingDependencyConversion(Project ModDep) {

        if (project.getInterdependencies().getTypedDependancyCount(ModifyingInterdependency.class, Dependency.CC) > 0) {
            ModifyingInterdependency[] CcDS = project.getInterdependencies().getTypedDependencies(ModifyingInterdependency.class, Dependency.CC);

            for (int dep = 0; dep < CcDS.length; dep++) {
                Feature f = ModDep.getFeatures().addFeature();
                f.setName(CcDS[dep].getSecondary().getName() + "' (Cost Changed)");
                for (int r = 0; r < project.getResources().getResourceCount(); r++) {
                    if (CcDS[dep].getSecondary().hasConsumption(project.getResources().getResource(r))) {
                        f.setConsumption(project.getResources().getResource(r),
                                CcDS[dep].getChange(Feature.class).getConsumption(project.getResources().getResource(r)));
                    }
                }

                List<Value> values = project.getValues().getValuesByFeature(CcDS[dep].getSecondary());
                for (Value v : values) {
                    Value newV = ModDep.getValues().addValue(f, v.getStakeholder());
                    newV.setValue(v.getValue());
                }

                List<Urgency> urgencies = project.getUrgencies().getUrgenciesByFeature(CcDS[dep].getSecondary());
                for (Urgency u : urgencies) {
                    Urgency newU = ModDep.getUrgencies().addUrgency(f, u.getStakeholder());
                    for (int r = 1; r <= project.getReleases().getReleaseCount(); r++) {
                        if (u.getUrgency(project.getReleases().getRelease(r)) != 0) {
                            newU.setUrgency(project.getReleases().getRelease(r), u.getUrgency(project.getReleases().getRelease(r)));
                        }
                    }
                }


                ModDep.getInterdependencies().addInterdependency(f, CcDS[dep].getPrimary(), Dependency.REQ);
                ModDep.getInterdependencies().addInterdependency(CcDS[dep].getPrimary(), CcDS[dep].getSecondary(), Dependency.PRE);
                ModDep.getInterdependencies().addInterdependency(CcDS[dep].getPrimary(), f, Dependency.XOR);
            }
        }

        if (project.getInterdependencies().getTypedDependancyCount(ModifyingInterdependency.class, Dependency.CV) > 0) {
            ModifyingInterdependency[] CvDS = project.getInterdependencies().getTypedDependencies(ModifyingInterdependency.class, Dependency.CV);

            for (int dep = 0; dep < CvDS.length; dep++) {
                Feature f = ModDep.getFeatures().addFeature();
                f.setName(CvDS[dep].getSecondary().getName() + "' (Value Changed)");
                for (int r = 0; r < project.getResources().getResourceCount(); r++) {
                    if (CvDS[dep].getSecondary().hasConsumption(project.getResources().getResource(r))) {
                        f.setConsumption(project.getResources().getResource(r),
                                CvDS[dep].getSecondary().getConsumption(project.getResources().getResource(r)));
                    }
                }

                List<Value> values = CvDS[dep].getChange(Values.class).getValuesByFeature(CvDS[dep].getSecondary());
                for (Value v : values) {
                    Value newV = ModDep.getValues().addValue(f, v.getStakeholder());
                    newV.setValue(v.getValue());
                }

                List<Urgency> urgencies = project.getUrgencies().getUrgenciesByFeature(CvDS[dep].getSecondary());
                for (Urgency u : urgencies) {
                    Urgency newU = ModDep.getUrgencies().addUrgency(f, u.getStakeholder());
                    for (int r = 1; r <= project.getReleases().getReleaseCount(); r++) {
                        if (u.getUrgency(project.getReleases().getRelease(r)) != 0) {
                            newU.setUrgency(project.getReleases().getRelease(r), u.getUrgency(project.getReleases().getRelease(r)));
                        }
                    }
                }


                ModDep.getInterdependencies().addInterdependency(f, CvDS[dep].getPrimary(), Dependency.REQ);
                ModDep.getInterdependencies().addInterdependency(CvDS[dep].getPrimary(), CvDS[dep].getSecondary(), Dependency.PRE);
                ModDep.getInterdependencies().addInterdependency(CvDS[dep].getPrimary(), f, Dependency.XOR);
            }
        }

        if (project.getInterdependencies().getTypedDependancyCount(ModifyingInterdependency.class, Dependency.CU) > 0) {
            ModifyingInterdependency[] CuDS = project.getInterdependencies().getTypedDependencies(ModifyingInterdependency.class, Dependency.CU);

            for (int dep = 0; dep < CuDS.length; dep++) {
                Feature f = ModDep.getFeatures().addFeature();
                f.setName(CuDS[dep].getSecondary().getName() + "' (Urgency Changed)");
                for (int r = 0; r < project.getResources().getResourceCount(); r++) {
                    if (CuDS[dep].getSecondary().hasConsumption(project.getResources().getResource(r))) {
                        f.setConsumption(project.getResources().getResource(r),
                                CuDS[dep].getSecondary().getConsumption(project.getResources().getResource(r)));
                    }
                }

                List<Value> values = CuDS[dep].getChange(Values.class).getValuesByFeature(CuDS[dep].getSecondary());
                for (Value v : values) {
                    Value newV = ModDep.getValues().addValue(f, v.getStakeholder());
                    newV.setValue(v.getValue());
                }

                List<Urgency> urgencies = CuDS[dep].getChange(Urgencies.class).getUrgenciesByFeature(CuDS[dep].getSecondary());
                for (Urgency u : urgencies) {
                    Urgency newU = ModDep.getUrgencies().addUrgency(f, u.getStakeholder());
                    for (int r = 1; r <= project.getReleases().getReleaseCount(); r++) {
                        if (u.getUrgency(project.getReleases().getRelease(r)) != 0) {
                            newU.setUrgency(project.getReleases().getRelease(r), u.getUrgency(project.getReleases().getRelease(r)));
                        }
                    }
                }


                ModDep.getInterdependencies().addInterdependency(f, CuDS[dep].getPrimary(), Dependency.REQ);
                ModDep.getInterdependencies().addInterdependency(CuDS[dep].getPrimary(), CuDS[dep].getSecondary(), Dependency.PRE);
                ModDep.getInterdependencies().addInterdependency(CuDS[dep].getPrimary(), f, Dependency.XOR);
            }
        }
        return true;
    }

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

        FixedDependency[] FixDS = project.getInterdependencies().getTypedDependencies(FixedDependency.class, Dependency.FIXED);
        Interdependency[] AndDS = project.getInterdependencies().getTypedDependencies(Interdependency.class, Dependency.AND);
        Interdependency[] ReqDS = project.getInterdependencies().getTypedDependencies(Interdependency.class, Dependency.REQ);
        Interdependency[] PreDS = project.getInterdependencies().getTypedDependencies(Interdependency.class, Dependency.PRE);
        Interdependency[] XorDS = project.getInterdependencies().getTypedDependencies(Interdependency.class, Dependency.XOR);

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
        } else if (proj.getInterdependencies().getDependencyCount() > 0) {
            Interdependency[] newReqDS = proj.getInterdependencies().getTypedDependencies(Interdependency.class, Dependency.REQ);
            for (int i = 0; i < newReqDS.length; i++) {
                printWriter.print((proj.getFeatures().getFeatureIndex(newReqDS[i].getPrimary()) + 1
                        + project.getFeatures().getFeatureCount())
                        + ", " + (project.getFeatures().getFeatureIndex(newReqDS[i].getSecondary()) + 1) + ", ");
            }
        } else {
            printWriter.println("1, 1, ");
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
        } else if (proj.getInterdependencies().getDependencyCount() > 0) {
            Interdependency[] newPreDS = proj.getInterdependencies().getTypedDependencies(Interdependency.class, Dependency.PRE);
            for (int i = 0; i < newPreDS.length; i++) {
                printWriter.print((project.getFeatures().getFeatureIndex(newPreDS[i].getPrimary()) + 1)
                        + ", " + (project.getFeatures().getFeatureIndex(newPreDS[i].getSecondary()) + 1) + ", ");
            }
        } else {
            printWriter.println("1, 1, ");
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
        } else if (proj.getInterdependencies().getDependencyCount() > 0) {
            Interdependency[] newXorDS = proj.getInterdependencies().getTypedDependencies(Interdependency.class, Dependency.XOR);
            for (int i = 0; i < newXorDS.length; i++) {
                printWriter.print((project.getFeatures().getFeatureIndex(newXorDS[i].getPrimary()) + 1)
                        + ", " + ((proj.getFeatures().getFeatureIndex(newXorDS[i].getSecondary()) + 1)
                        + project.getFeatures().getFeatureCount()) + ", ");
            }
        } else {
            printWriter.println("1, 1, ");
        }
        printWriter.println("|];");

        printWriter.println("% =========================");
    }

    private void printResources() {
        printWriter.println("% Resources (buffer/id/capacity)");
        printWriter.println("B = " + "{missing buffer}" + ";");

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
        printWriter.println("% =========================");
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
            if (f < project.getReleases().getReleaseCount() - 1) {
                printWriter.print("\n");
            }
        }
        if (proj.getFeatures().getFeatureCount() > 0) {
            for (int f = 0; f < proj.getFeatures().getFeatureCount(); f++) {
                printWriter.print("|");
                for (int res = 0; res < project.getResources().getResourceCount(); res++) {
                    printWriter.print(" " + proj.getFeatures().getFeature(f).getConsumption(project.getResources().getResource(res)) + ",");
                }
                if (f < project.getReleases().getReleaseCount() - 1) {
                    printWriter.print("\n");
                }
            }
        }
        printWriter.println(" |];");
        printWriter.println("% =========================");
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
        printWriter.print("value = [| ");
        for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
            List<Value> values = project.getValues().getValuesByStakeholder(project.getStakeholders().getStakeholder(s));
            boolean valueNotFound = true;
            for (int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
                for (Value v : values) {
                    if (v.getFeature() == project.getFeatures().getFeature(f)) {
                        printWriter.print(v.getValue() + ", ");
                        valueNotFound = false;
                        break;
                    } else {
                        valueNotFound = true;
                    }
                    if (valueNotFound) {
                        printWriter.print("0, ");
                    }
                }
            }

            if (ModDep.getValues().getValueCount() > 0) {
                List<Value> newValues = ModDep.getValues().getValuesByStakeholder(project.getStakeholders().getStakeholder(s));
                boolean newValueNotFound = true;
                for (int f = 0; f < ModDep.getFeatures().getFeatureCount(); f++) {
                    for (Value v : newValues) {
                        if (v.getFeature() == ModDep.getFeatures().getFeature(f)) {
                            printWriter.print(v.getValue() + ", ");
                            newValueNotFound = false;
                            break;
                        } else {
                            newValueNotFound = true;
                        }
                        if (newValueNotFound) {
                            printWriter.print("0, ");
                        }
                    }
                }
            }
            printWriter.print("| ");
            if (s < project.getStakeholders().getStakeholderCount() - 1) {
                printWriter.print("\n");
            }
        }
        printWriter.println(" |];");

        printWriter.println("urgency = array3d(1..S, 1..F, 1.." + (project.getReleases().getReleaseCount() + 1) + ", [");
        for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
            List<Urgency> urgencies = project.getUrgencies().getUrgenciesByStakeholder(project.getStakeholders().getStakeholder(s));
            printWriter.println("% stakeholder " + (s + 1));
            for (int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
                for (Urgency u : urgencies) {
                    if (u.getFeature() == project.getFeatures().getFeature(f)) {
                        for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                            printWriter.print(u.getUrgency(project.getReleases().getRelease(r)) + ", ");
                        }
                    } else {
                        for (int i = 0; i < project.getFeatures().getFeatureCount(); i++) {
                            printWriter.print("0, ");
                        }
                    }
                    printWriter.print("\n");
                }
            }
            if (ModDep.getUrgencies().getUrgecnyCount() > 0) {
                List<Urgency> newUrgencies = ModDep.getUrgencies().getUrgenciesByStakeholder(project.getStakeholders().getStakeholder(s));
                for (int f = 0; f < ModDep.getFeatures().getFeatureCount(); f++) {
                    for (Urgency u : newUrgencies) {
                        if (u.getFeature() == ModDep.getFeatures().getFeature(f)) {
                            for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                                printWriter.print(u.getUrgency(project.getReleases().getRelease(r)) + ", ");
                            }
                        } else {
                            for (int i = 0; i < project.getFeatures().getFeatureCount(); i++) {
                                printWriter.print("0, ");
                            }
                        }
                        printWriter.print("\n");
                    }
                }
            }
        }
        printWriter.println("]);");
    }

}
