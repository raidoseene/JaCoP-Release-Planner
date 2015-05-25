/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataoutput;

import ee.raidoseene.releaseplanner.datamodel.Dependency;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.FixedDependency;
import ee.raidoseene.releaseplanner.datamodel.GroupDependency;
import ee.raidoseene.releaseplanner.datamodel.Interdependency;
import ee.raidoseene.releaseplanner.datamodel.Project;

/**
 *
 * @author Raido Seene
 */
public class DependencyManager {
    public static final int EXIST_MASK = 0xf0;
    
    public static final int TRUE = 0x10;
    public static final int FALSE = 0x00;
    
    public static final int BOTH = 0x10;
    public static final int PRIMARY = 0x11;
    public static final int SECONDARY = 0x12;

    private static class DataPackage {

        private Project project;
        private Project modDep;
        private FixedDependency[] FixDS;
        private Interdependency[] AndDS;
        private Interdependency[] ReqDS;
        private Interdependency[] MReqDS;
        private Interdependency[] PreDS;
        private Interdependency[] MPreDS;
        private Interdependency[] XorDS;
        private Interdependency[] MXorDS;
        private GroupDependency[] AtLeastDS;
        private GroupDependency[] ExactlyDS;
        private GroupDependency[] AtMostDS;

        private DataPackage(Project project, Project modDep) {
            this.project = project;
            this.modDep = modDep;
            this.FixDS = project.getDependencies().getTypedDependencies(FixedDependency.class, Dependency.FIXED);
            this.AndDS = project.getDependencies().getTypedDependencies(Interdependency.class, Dependency.AND);
            this.ReqDS = project.getDependencies().getTypedDependencies(Interdependency.class, Dependency.REQ);
            this.MReqDS = modDep.getDependencies().getTypedDependencies(Interdependency.class, Dependency.REQ);
            this.PreDS = project.getDependencies().getTypedDependencies(Interdependency.class, Dependency.PRE);
            this.MPreDS = modDep.getDependencies().getTypedDependencies(Interdependency.class, Dependency.PRE);
            this.XorDS = project.getDependencies().getTypedDependencies(Interdependency.class, Dependency.XOR);
            this.MXorDS = modDep.getDependencies().getTypedDependencies(Interdependency.class, Dependency.XOR);
            this.AtLeastDS = project.getDependencies().getTypedDependencies(GroupDependency.class, Dependency.ATLEAST);
            this.ExactlyDS = project.getDependencies().getTypedDependencies(GroupDependency.class, Dependency.EXACTLY);
            this.AtMostDS = project.getDependencies().getTypedDependencies(GroupDependency.class, Dependency.ATMOST);
        }
    }

    public static String getDependenciesData(Project project, Project modDep, boolean codeOutput) {
        DataPackage dp = new DataPackage(project, modDep);

        if (codeOutput) {
            return getDependencyCode(dp);
        } else {
            return getDependencyRawData(dp);
        }
    }

    public static String getGroupDependenciesData(Project project, Project modDep) {
        if (project.getDependencies().getTypedDependancyCount(GroupDependency.class, Dependency.GROUP) > 0) {
            DataPackage dp = new DataPackage(project, modDep);
            return getGroupDepData(dp);
        }
        return "";
    }

    private static String getDependencyCode(DataPackage dp) {
        StringBuilder sb = new StringBuilder();

        // Non-XOR features
        sb.append("% Non-XOR dependencies" + "\n");
        if (dp.XorDS.length > 0 || dp.MXorDS.length > 0) {
            for (int i = 0; i < dp.project.getFeatures().getFeatureCount(); i++) {
                boolean xor = false;
                xor = xor || isXOR(dp.project.getFeatures().getFeature(i), dp.XorDS);
                xor = xor || isXOR(dp.project.getFeatures().getFeature(i), dp.MXorDS);
                if (!xor) {
                    sb.append("constraint x["
                            + (i + 1) + "] > 0;"
                            + "\n");
                }
            }
            for (int i = 0; i < dp.modDep.getFeatures().getFeatureCount(); i++) {
                boolean xor = false;
                xor = xor || isXOR(dp.modDep.getFeatures().getFeature(i), dp.XorDS);
                xor = xor || isXOR(dp.modDep.getFeatures().getFeature(i), dp.MXorDS);
                if (!xor) {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureCount() + i + 1) + "] > 0;"
                            + "\n");
                }
            }
        } else {
            int featureCount = dp.project.getFeatures().getFeatureCount() +
                    dp.modDep.getFeatures().getFeatureCount();
            for (int i = 0; i < featureCount; i++) {
                sb.append("constraint x["
                        + (i + 1) + "] > 0;"
                        + "\n");
            }
        }


        // Fixed release
        sb.append("% FIXED dependencies" + "\n");
        if (dp.FixDS.length > 0) {
            for (int i = 0; i < dp.FixDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(dp.FixDS[i], dp.XorDS);
                xor = xor || isXOR(dp.FixDS[i], dp.MXorDS);
                if (!xor) {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.FixDS[i].getFeature()) + 1) + "] = "
                            + (dp.project.getReleases().getReleaseIndex(dp.FixDS[i].getRelease()) + 1) + ";"
                            + "\n");
                } else {
                    // Is the following part needed?
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.FixDS[i].getFeature()) + 1) + "] = "
                            + (dp.project.getReleases().getReleaseIndex(dp.FixDS[i].getRelease()) + 1) + " \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.FixDS[i].getFeature()) + 1) + " = 0;"
                            + "\n");
                }
            }
        }

        // AND dependency
        sb.append("% AND dependencies" + "\n");
        if (dp.AndDS.length > 0) {
            for (int i = 0; i < dp.AndDS.length; i++) {
                /* AND seems to be close enough dependency to leave out both features if one is in XOR dependency
                boolean xor = false;
                xor = xor || isXOR(dp.AndDS[i], dp.XorDS);
                xor = xor || isXOR(dp.AndDS[i], dp.MXorDS);
                if (!xor) {
                */
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.AndDS[i].getPrimary()) + 1) + "] = x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.AndDS[i].getSecondary()) + 1) + "];"
                            + "\n");
                /* AND seems to be close enough dependency to leave out both features if one is in XOR dependency
                } else {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.AndDS[i].getPrimary()) + 1) + "] = x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.AndDS[i].getSecondary()) + 1) + "] \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.AndDS[i].getPrimary()) + 1) + "] = 0 \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.AndDS[i].getSecondary()) + 1) + "] = 0;"
                            + "\n");
                }
                */
                // Add if one of features from AND is in ChangeIn... xor then it has to work with the other XOR feature also
            }
        }

        // REQUIRES dependency
        sb.append("% REQ dependencies" + "\n");
        getREQCode(sb, dp);

        // PRECEDES dependency
        sb.append("% PRE dependencies" + "\n");
        getPRECode(sb, dp);

        // XOR dependency
        sb.append("% XOR dependencies" + "\n");
        if (dp.XorDS.length > 0) {
            for (int i = 0; i < dp.XorDS.length; i++) {
                sb.append("constraint x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.XorDS[i].getPrimary()) + 1) + "] = 0 xor x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.XorDS[i].getSecondary()) + 1) + "] = 0;"
                        + "\n");
            }
        }
        if (dp.MXorDS.length > 0) {
            for (int i = 0; i < dp.MXorDS.length; i++) {
                sb.append("constraint x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.MXorDS[i].getPrimary()) + 1) + "] = 0 xor x["
                        + (dp.modDep.getFeatures().getFeatureIndex(dp.MXorDS[i].getSecondary())
                        + dp.project.getFeatures().getFeatureCount() + 1) + "] = 0;"
                        + "\n");
            }
        }

        return sb.toString();
    }

    private static String getDependencyRawData(DataPackage dp) {
        StringBuilder sb = new StringBuilder();
        sb.append("% FIXED features / AND features / REQUIRED features / PRECEDING features / XOR features" + "\n");

        // Fixed release
        sb.append("FIX = " + dp.FixDS.length + ";" + "\n");
        sb.append("fx = [|");
        if (dp.FixDS.length > 0) {
            for (int i = 0; i < dp.FixDS.length; i++) {
                sb.append(" " + (dp.project.getFeatures().getFeatureIndex(dp.FixDS[i].getFeature()) + 1)
                        + ", " + (dp.project.getReleases().getReleaseIndex(dp.FixDS[i].getRelease()) + 1) + ", |");
            }
            sb.append("];" + "\n");
        } else {
            sb.append(" 0, 0, |];" + "\n");
        }

        // AND dependency
        sb.append("AND = " + dp.AndDS.length + ";" + "\n");
        sb.append("and = [|");
        if (dp.AndDS.length > 0) {
            for (int i = 0; i < dp.AndDS.length; i++) {
                sb.append(" " + (dp.project.getFeatures().getFeatureIndex(dp.AndDS[i].getPrimary()) + 1)
                        + ", " + (dp.project.getFeatures().getFeatureIndex(dp.AndDS[i].getSecondary()) + 1) + ", |");
            }
            sb.append("];" + "\n");
        } else {
            sb.append(" 0, 0, |];" + "\n");
        }

        // REQUIRES dependency
        sb.append("REQ = " + dp.ReqDS.length + ";" + "\n");
        sb.append("req = [|");
        if (dp.ReqDS.length > 0) {
            for (int i = 0; i < dp.ReqDS.length; i++) {
                sb.append(" " + (dp.project.getFeatures().getFeatureIndex(dp.ReqDS[i].getPrimary()) + 1)
                        + ", " + (dp.project.getFeatures().getFeatureIndex(dp.ReqDS[i].getSecondary()) + 1) + ", |");
            }
        } else if (dp.MReqDS.length > 0) {
            for (int i = 0; i < dp.MReqDS.length; i++) {
                sb.append(" " + (dp.modDep.getFeatures().getFeatureIndex(dp.MReqDS[i].getPrimary()) + 1
                        + dp.project.getFeatures().getFeatureCount())
                        + ", " + (dp.project.getFeatures().getFeatureIndex(dp.MReqDS[i].getSecondary()) + 1) + ", |");
            }
        } else {
            sb.append(" 0, 0, |");
        }
        sb.append("];" + "\n");

        // PRECEDES dependency
        sb.append("PRE = " + dp.PreDS.length + ";" + "\n");
        sb.append("pre = [|");
        if (dp.PreDS.length > 0) {
            for (int i = 0; i < dp.PreDS.length; i++) {
                sb.append(" " + (dp.project.getFeatures().getFeatureIndex(dp.PreDS[i].getPrimary()) + 1)
                        + ", " + (dp.project.getFeatures().getFeatureIndex(dp.PreDS[i].getSecondary()) + 1) + ", |");
            }
        } else if (dp.MPreDS.length > 0) {
            for (int i = 0; i < dp.MPreDS.length; i++) {
                sb.append(" " + (dp.project.getFeatures().getFeatureIndex(dp.MPreDS[i].getPrimary()) + 1)
                        + ", " + (dp.project.getFeatures().getFeatureIndex(dp.MPreDS[i].getSecondary()) + 1) + ", |");
            }
        } else {
            sb.append(" 0, 0, |");
        }
        sb.append("];" + "\n");

        // XOR dependency
        sb.append("XOR = " + dp.XorDS.length + ";" + "\n");
        sb.append("xr = [|");
        if (dp.XorDS.length > 0) {
            for (int i = 0; i < dp.XorDS.length; i++) {
                sb.append(" " + (dp.project.getFeatures().getFeatureIndex(dp.XorDS[i].getPrimary()) + 1)
                        + ", " + (dp.project.getFeatures().getFeatureIndex(dp.XorDS[i].getSecondary()) + 1) + ", |");
            }
        } else if (dp.MXorDS.length > 0) {
            for (int i = 0; i < dp.MXorDS.length; i++) {
                sb.append(" " + (dp.project.getFeatures().getFeatureIndex(dp.MXorDS[i].getPrimary()) + 1)
                        + ", " + ((dp.modDep.getFeatures().getFeatureIndex(dp.MXorDS[i].getSecondary()) + 1)
                        + dp.project.getFeatures().getFeatureCount()) + ", |");
            }
        } else {
            sb.append(" 0, 0, |");
        }
        sb.append("];\n" + "\n");

        // Group dependencies
        sb.append(getGroupDepData(dp));

        return sb.toString();
    }

    private static String getGroupDepData(DataPackage dp) {
        StringBuilder sb = new StringBuilder();
        sb.append("% Group dependencies: AtLeast / Exactly / AtMost" + "\n");

        sb.append("ATLEAST = " + 0 + ";" + "\n");
        sb.append("atLeast = [|");
        if (dp.AtLeastDS.length > 0) {
            for (int i = 0; i < dp.AtLeastDS.length; i++) {
                sb.append(" " + (dp.project.getGroups().getGroupIndex(dp.AtLeastDS[i].getGroup()) + 1)
                        + ", " + (dp.AtLeastDS[i].getFeatureCount()) + ", |");
            }
        } else {
            sb.append(" 0, 0, |");
        }
        sb.append("];" + "\n");

        sb.append("EXACTLY = " + 0 + ";" + "\n");
        sb.append("exactly = [|");
        if (dp.ExactlyDS.length > 0) {
            for (int i = 0; i < dp.ExactlyDS.length; i++) {
                sb.append(" " + (dp.project.getGroups().getGroupIndex(dp.ExactlyDS[i].getGroup()) + 1)
                        + ", " + (dp.ExactlyDS[i].getFeatureCount()) + ", |");
            }
        } else {
            sb.append(" 0, 0, |");
        }
        sb.append("];" + "\n");

        sb.append("ATMOST = " + 0 + ";" + "\n");
        sb.append("atMost = [|");
        if (dp.AtMostDS.length > 0) {
            for (int i = 0; i < dp.AtMostDS.length; i++) {
                sb.append(" " + (dp.project.getGroups().getGroupIndex(dp.AtMostDS[i].getGroup()) + 1)
                        + ", " + (dp.AtMostDS[i].getFeatureCount()) + ", |");
            }
        } else {
            sb.append(" 0, 0, |");
        }
        sb.append("];" + "\n");

        sb.append("% =========================\n" + "\n");

        return sb.toString();
    }

    private static boolean isXOR(Feature f, Interdependency[] xor) {
        boolean exists = false;
        for (int i = 0; i < xor.length; i++) {
            if (f == xor[i].getPrimary()
                    || f == xor[i].getSecondary()) {
                exists = true;
            }
        }
        return exists;
    }
    
    private static boolean isXOR(FixedDependency dep, Interdependency[] xor) {
        boolean exists = false;
        for (int i = 0; i < xor.length; i++) {
            if (dep.getFeature() == xor[i].getPrimary()
                    || dep.getFeature() == xor[i].getSecondary()) {
                exists = true;
            }
        }
        return exists;
    }

    private static boolean isXOR(Interdependency dep, Interdependency[] xor) {
        boolean exists = false;
        for (int i = 0; i < xor.length; i++) {
            if (dep.getPrimary() == xor[i].getPrimary()
                    || dep.getPrimary() == xor[i].getSecondary()
                    || dep.getSecondary() == xor[i].getPrimary()
                    || dep.getSecondary() == xor[i].getSecondary()) {
                exists = true;
            }
        }
        return exists;
    }
    
    /*
    private static int isXOR(Interdependency dep, Interdependency[] xor) {
        int exists = 0;
        int primary = 0;
        int secondary = 0;
        for (int i = 0; i < xor.length; i++) {
            if (dep.getPrimary() == xor[i].getPrimary()
                    || dep.getPrimary() == xor[i].getSecondary()) {
                exists++;
                primary++;
            }
            if (dep.getSecondary() == xor[i].getPrimary()
                    || dep.getSecondary() == xor[i].getSecondary()) {
                exists++;
                secondary++;
            }
        }
        if (exists == 0) {
            return DependencyManager.FALSE;
        } else {
            if (primary > 0 && secondary == 0) {
                return DependencyManager.PRIMARY;
            } else if (primary == 0 && secondary > 0) {
                return DependencyManager.SECONDARY;
            } else {
                return DependencyManager.BOTH;
            }
        }
    }
    */

    private static void getREQCode(StringBuilder sb, DataPackage dp) {
        if (dp.ReqDS.length > 0) {
            for (int i = 0; i < dp.ReqDS.length; i++) {
                //int xorExists = isXOR(dp.ReqDS[i], dp.XorDS);
                //int mXorExists = isXOR(dp.ReqDS[i], dp.MXorDS);
                boolean xor = false;
                //xor = xor || ((xorExists & DependencyManager.EXIST_MASK) > 0);
                //xor = xor || ((mXorExists & DependencyManager.EXIST_MASK) > 0);
                xor = xor || isXOR(dp.ReqDS[i], dp.XorDS);
                xor = xor || isXOR(dp.ReqDS[i], dp.MXorDS);
                if (!xor) {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.ReqDS[i].getSecondary()) + 1) + "] <= x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.ReqDS[i].getPrimary()) + 1) + "];"
                            + "\n");
                } else {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.ReqDS[i].getSecondary()) + 1) + "] <= x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.ReqDS[i].getPrimary()) + 1) + "] \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.ReqDS[i].getSecondary()) + 1) + " = 0 \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.ReqDS[i].getPrimary()) + 1) + " = 0;"
                            + "\n");
                    /*
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.ReqDS[i].getSecondary()) + 1) + "] <= x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.ReqDS[i].getPrimary()) + 1) + "]");
                    if ((((xorExists & DependencyManager.EXIST_MASK) > 0 && (xorExists == DependencyManager.SECONDARY
                            || xorExists == DependencyManager.BOTH)) || (xorExists & DependencyManager.EXIST_MASK) == 0)
                            &&
                            (((mXorExists & DependencyManager.EXIST_MASK) > 0 && (mXorExists == DependencyManager.SECONDARY
                            || mXorExists == DependencyManager.BOTH)) || (mXorExists & DependencyManager.EXIST_MASK) == 0)) {
                        sb.append(" \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.ReqDS[i].getSecondary()) + 1) + " = 0 ");
                    } else if ((((xorExists & DependencyManager.EXIST_MASK) > 0 && (xorExists == DependencyManager.PRIMARY
                            || xorExists == DependencyManager.BOTH)) || (xorExists & DependencyManager.EXIST_MASK) == 0)
                            &&
                            (((mXorExists & DependencyManager.EXIST_MASK) > 0 && (mXorExists == DependencyManager.PRIMARY
                            || mXorExists == DependencyManager.BOTH)) || (mXorExists & DependencyManager.EXIST_MASK) == 0)) {
                        sb.append(" \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.ReqDS[i].getPrimary()) + 1) + " = 0");
                    }
                    sb.append(";" + "\n");
                    */
                }
            }
        }
        if (dp.MReqDS.length > 0) {
            for (int i = 0; i < dp.MReqDS.length; i++) {
                boolean xor = isXOR(dp.MReqDS[i], dp.MXorDS);
                if (!xor) {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.MReqDS[i].getSecondary()) + 1) + "] <= x["
                            + (dp.modDep.getFeatures().getFeatureIndex(dp.MReqDS[i].getPrimary())
                            + dp.project.getFeatures().getFeatureCount() + 1) + "];"
                            + "\n");
                } else {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.MReqDS[i].getSecondary()) + 1) + "] <= x["
                            + (dp.modDep.getFeatures().getFeatureIndex(dp.MReqDS[i].getPrimary())
                            + dp.project.getFeatures().getFeatureCount() + 1) + "] \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.MReqDS[i].getSecondary()) + 1) + " = 0 \\/ x["
                            + (dp.modDep.getFeatures().getFeatureIndex(dp.MReqDS[i].getPrimary())
                            + dp.project.getFeatures().getFeatureCount() + 1) + " = 0;"
                            + "\n");
                }
            }
        }
    }

    private static void getPRECode(StringBuilder sb, DataPackage dp) {
        if (dp.PreDS.length > 0) {
            for (int i = 0; i < dp.PreDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(dp.PreDS[i], dp.XorDS);
                xor = xor || isXOR(dp.PreDS[i], dp.MXorDS);
                if (!xor) {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.PreDS[i].getSecondary()) + 1) + "] <= "
                            + (dp.project.getReleases().getReleaseCount()) + " -> x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.PreDS[i].getPrimary()) + 1) + "] < x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.PreDS[i].getSecondary()) + 1) + "];"
                            + "\n");
                } else {
                    sb.append("constraint (x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.PreDS[i].getSecondary()) + 1) + "] <= "
                            + (dp.project.getReleases().getReleaseCount()) + " -> x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.PreDS[i].getPrimary()) + 1) + "] < x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.PreDS[i].getSecondary()) + 1) + "]) \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.PreDS[i].getPrimary()) + 1) + " = 0 \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.PreDS[i].getSecondary()) + 1) + " = 0;"
                            + "\n");
                }
            }
        }
        if (dp.MPreDS.length > 0) {
            for (int i = 0; i < dp.MPreDS.length; i++) {
                sb.append("constraint (x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.MPreDS[i].getSecondary()) + 1) + "] <= "
                        + (dp.project.getReleases().getReleaseCount()) + " -> x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.MPreDS[i].getPrimary()) + 1) + "] < x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.MPreDS[i].getSecondary()) + 1) + "]) \\/ x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.MPreDS[i].getPrimary()) + 1) + " = 0 \\/ x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.MPreDS[i].getSecondary()) + 1) + " = 0;"
                        + "\n");
            }
        }
    }
}
