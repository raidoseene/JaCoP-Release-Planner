/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataoutput;

import ee.raidoseene.releaseplanner.datamodel.Dependency;
import ee.raidoseene.releaseplanner.datamodel.ExistanceDependency;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.ReleaseDependency;
import ee.raidoseene.releaseplanner.datamodel.GroupDependency;
import ee.raidoseene.releaseplanner.datamodel.OrderDependency;
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
        private ReleaseDependency[] FixedDS;
        private ReleaseDependency[] ExcludedDS;
        private ReleaseDependency[] EarlierDS;
        private ReleaseDependency[] LaterDS;
        
        private OrderDependency[] SoftPrecedenceDS;
        private OrderDependency[] MSoftPrecedenceDS;
        private OrderDependency[] HardPrecedenceDS;
        private OrderDependency[] MHardPrecedenceDS;
        private OrderDependency[] CouplingDS;
        private OrderDependency[] SeparationDS;
        
        private ExistanceDependency[] AndDS;
        private ExistanceDependency[] XorDS;
        private ExistanceDependency[] MXorDS;
        
        private GroupDependency[] AtLeastDS;
        private GroupDependency[] ExactlyDS;
        private GroupDependency[] AtMostDS;

        private DataPackage(Project project, Project modDep) {
            this.project = project;
            this.modDep = modDep;
            this.FixedDS = project.getDependencies().getTypedDependencies(ReleaseDependency.class, Dependency.FIXED);
            this.ExcludedDS = project.getDependencies().getTypedDependencies(ReleaseDependency.class, Dependency.EXCLUDED);
            this.EarlierDS = project.getDependencies().getTypedDependencies(ReleaseDependency.class, Dependency.EARLIER);
            this.LaterDS = project.getDependencies().getTypedDependencies(ReleaseDependency.class, Dependency.LATER);
            
            this.SoftPrecedenceDS = project.getDependencies().getTypedDependencies(OrderDependency.class, Dependency.SOFTPRECEDENCE);
            this.MSoftPrecedenceDS = modDep.getDependencies().getTypedDependencies(OrderDependency.class, Dependency.SOFTPRECEDENCE);
            this.HardPrecedenceDS = project.getDependencies().getTypedDependencies(OrderDependency.class, Dependency.HARDPRECEDENCE);
            this.MHardPrecedenceDS = modDep.getDependencies().getTypedDependencies(OrderDependency.class, Dependency.HARDPRECEDENCE);
            this.CouplingDS = project.getDependencies().getTypedDependencies(OrderDependency.class, Dependency.COUPLING);
            this.SeparationDS = project.getDependencies().getTypedDependencies(OrderDependency.class, Dependency.SEPARATION);
            
            this.AndDS = project.getDependencies().getTypedDependencies(ExistanceDependency.class, Dependency.AND);
            this.XorDS = project.getDependencies().getTypedDependencies(ExistanceDependency.class, Dependency.XOR);
            this.MXorDS = modDep.getDependencies().getTypedDependencies(ExistanceDependency.class, Dependency.XOR);
            
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
            return "Existing code file solution deprecated!";
            //return getDependencyRawData(dp);
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
                boolean xorAnd = false;
                xorAnd = xorAnd || isXOR(dp.project.getFeatures().getFeature(i), dp.XorDS);
                xorAnd = xorAnd || isXOR(dp.project.getFeatures().getFeature(i), dp.MXorDS);
                xorAnd = xorAnd || isAND(dp.project.getFeatures().getFeature(i), dp.AndDS);
                if (!xorAnd) {
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

        sb.append("% Release related dependencies:" + "\n");
        
        // Fixed release
        sb.append("% FIXED dependencies" + "\n");
        if (dp.FixedDS.length > 0) {
            for (int i = 0; i < dp.FixedDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(dp.FixedDS[i], dp.XorDS);
                xor = xor || isXOR(dp.FixedDS[i], dp.MXorDS);
                if (!xor) {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.FixedDS[i].getFeature()) + 1) + "] = "
                            + (dp.project.getReleases().getReleaseIndex(dp.FixedDS[i].getRelease()) + 1) + ";"
                            + "\n");
                } else {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.FixedDS[i].getFeature()) + 1) + "] = "
                            + (dp.project.getReleases().getReleaseIndex(dp.FixedDS[i].getRelease()) + 1) + " \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.FixedDS[i].getFeature()) + 1) + " = 0;"
                            + "\n");
                }
            }
        }
        
        // Excluded release
        sb.append("% EXCLUDED dependencies" + "\n");
        if (dp.ExcludedDS.length > 0) {
            for (int i = 0; i < dp.ExcludedDS.length; i++) {
                sb.append("constraint x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.ExcludedDS[i].getFeature()) + 1) + "] != "
                        + (dp.project.getReleases().getReleaseIndex(dp.ExcludedDS[i].getRelease()) + 1) + ";"
                        + "\n");
            }
        }
        
        // Earlier release
        sb.append("% EARLIER dependencies" + "\n");
        if (dp.EarlierDS.length > 0) {
            for (int i = 0; i < dp.EarlierDS.length; i++) {
                sb.append("constraint x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.EarlierDS[i].getFeature()) + 1) + "] < "
                        + (dp.project.getReleases().getReleaseIndex(dp.EarlierDS[i].getRelease()) + 1) + ";"
                        + "\n");
            }
        }
        
        // Later release
        sb.append("% LATER dependencies" + "\n");
        if (dp.LaterDS.length > 0) {
            for (int i = 0; i < dp.LaterDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(dp.LaterDS[i], dp.XorDS);
                xor = xor || isXOR(dp.LaterDS[i], dp.MXorDS);
                if (!xor) {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.LaterDS[i].getFeature()) + 1) + "] > "
                            + (dp.project.getReleases().getReleaseIndex(dp.LaterDS[i].getRelease()) + 1) + ";"
                            + "\n");
                } else {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.LaterDS[i].getFeature()) + 1) + "] > "
                            + (dp.project.getReleases().getReleaseIndex(dp.LaterDS[i].getRelease()) + 1) + " \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.LaterDS[i].getFeature()) + 1) + " = 0;"
                            + "\n");
                }
            }
        }
        
        sb.append("% Order related dependencies:" + "\n");
        // SOFTPRECEDENCE dependency
        sb.append("% SOFTPRECEDENCE dependencies" + "\n");
        getSoftPrecedenceCode(sb, dp);

        // HARDPRECEDENCE dependency
        sb.append("% HARDPRECEDENCE dependencies" + "\n");
        getHardPrecedenceCode(sb, dp);
        
        // COUPLING dependency
        sb.append("% COUPLING dependencies" + "\n");
        if (dp.CouplingDS.length > 0) {
            for (int i = 0; i < dp.CouplingDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(dp.CouplingDS[i], dp.XorDS);
                xor = xor || isXOR(dp.CouplingDS[i], dp.MXorDS);
                if (!xor) {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.CouplingDS[i].getPrimary()) + 1) + "] = x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.CouplingDS[i].getSecondary()) + 1) + "];"
                            + "\n");
                } else {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.CouplingDS[i].getPrimary()) + 1) + "] = x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.CouplingDS[i].getSecondary()) + 1) + "] \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.CouplingDS[i].getPrimary()) + 1) + "] = 0 \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.CouplingDS[i].getSecondary()) + 1) + "] = 0;"
                            + "\n");
                }
            }
        }
        
        // SEPARATION dependency
        sb.append("% SEPARATION dependencies" + "\n");
        if (dp.SeparationDS.length > 0) {
            for (int i = 0; i < dp.SeparationDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(dp.SeparationDS[i], dp.XorDS);
                xor = xor || isXOR(dp.SeparationDS[i], dp.MXorDS);
                if (!xor) {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.SeparationDS[i].getPrimary()) + 1) + "] != x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.SeparationDS[i].getSecondary()) + 1) + "];"
                            + "\n");
                } else {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.SeparationDS[i].getPrimary()) + 1) + "] != x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.SeparationDS[i].getSecondary()) + 1) + "] \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.SeparationDS[i].getPrimary()) + 1) + "] = 0 \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.SeparationDS[i].getSecondary()) + 1) + "] = 0;"
                            + "\n");
                }
            }
        }

        sb.append("% Existance related dependencies:" + "\n");
        // AND dependency
        sb.append("% AND dependencies" + "\n");
        if (dp.AndDS.length > 0) {
            for (int i = 0; i < dp.AndDS.length; i++) {
                sb.append("constraint (x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.AndDS[i].getPrimary()) + 1) + "] != 0 /\\ x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.AndDS[i].getSecondary()) + 1) + "] != 0) xor (x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.AndDS[i].getPrimary()) + 1) + "] = 0 /\\ x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.AndDS[i].getSecondary()) + 1) + "] = 0);"
                        + "\n");
            }
        }
        
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

    /*
    private static String getDependencyRawData(DataPackage dp) {
        StringBuilder sb = new StringBuilder();
        sb.append("% FIXED features / AND features / REQUIRED features / PRECEDING features / XOR features" + "\n");

        // Fixed release
        sb.append("FIX = " + dp.FixedDS.length + ";" + "\n");
        sb.append("fx = [|");
        if (dp.FixedDS.length > 0) {
            for (int i = 0; i < dp.FixedDS.length; i++) {
                sb.append(" " + (dp.project.getFeatures().getFeatureIndex(dp.FixedDS[i].getFeature()) + 1)
                        + ", " + (dp.project.getReleases().getReleaseIndex(dp.FixedDS[i].getRelease()) + 1) + ", |");
            }
            sb.append("];" + "\n");
        } else {
            sb.append(" 0, 0, |];" + "\n");
        }

        // AND dependency
        sb.append("AND = " + dp.CouplingDS.length + ";" + "\n");
        sb.append("and = [|");
        if (dp.CouplingDS.length > 0) {
            for (int i = 0; i < dp.CouplingDS.length; i++) {
                sb.append(" " + (dp.project.getFeatures().getFeatureIndex(dp.CouplingDS[i].getPrimary()) + 1)
                        + ", " + (dp.project.getFeatures().getFeatureIndex(dp.CouplingDS[i].getSecondary()) + 1) + ", |");
            }
            sb.append("];" + "\n");
        } else {
            sb.append(" 0, 0, |];" + "\n");
        }

        // REQUIRES dependency
        sb.append("REQ = " + dp.SoftPrecedenceDS.length + ";" + "\n");
        sb.append("req = [|");
        if (dp.SoftPrecedenceDS.length > 0) {
            for (int i = 0; i < dp.SoftPrecedenceDS.length; i++) {
                sb.append(" " + (dp.project.getFeatures().getFeatureIndex(dp.SoftPrecedenceDS[i].getPrimary()) + 1)
                        + ", " + (dp.project.getFeatures().getFeatureIndex(dp.SoftPrecedenceDS[i].getSecondary()) + 1) + ", |");
            }
        } else if (dp.MSoftPrecedenceDS.length > 0) {
            for (int i = 0; i < dp.MSoftPrecedenceDS.length; i++) {
                sb.append(" " + (dp.modDep.getFeatures().getFeatureIndex(dp.MSoftPrecedenceDS[i].getPrimary()) + 1
                        + dp.project.getFeatures().getFeatureCount())
                        + ", " + (dp.project.getFeatures().getFeatureIndex(dp.MSoftPrecedenceDS[i].getSecondary()) + 1) + ", |");
            }
        } else {
            sb.append(" 0, 0, |");
        }
        sb.append("];" + "\n");

        // PRECEDES dependency
        sb.append("PRE = " + dp.HardPrecedenceDS.length + ";" + "\n");
        sb.append("pre = [|");
        if (dp.HardPrecedenceDS.length > 0) {
            for (int i = 0; i < dp.HardPrecedenceDS.length; i++) {
                sb.append(" " + (dp.project.getFeatures().getFeatureIndex(dp.HardPrecedenceDS[i].getPrimary()) + 1)
                        + ", " + (dp.project.getFeatures().getFeatureIndex(dp.HardPrecedenceDS[i].getSecondary()) + 1) + ", |");
            }
        } else if (dp.MHardPrecedenceDS.length > 0) {
            for (int i = 0; i < dp.MHardPrecedenceDS.length; i++) {
                sb.append(" " + (dp.project.getFeatures().getFeatureIndex(dp.MHardPrecedenceDS[i].getPrimary()) + 1)
                        + ", " + (dp.project.getFeatures().getFeatureIndex(dp.MHardPrecedenceDS[i].getSecondary()) + 1) + ", |");
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
    */

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

    private static boolean isXOR(Feature f, ExistanceDependency[] xor) {
        boolean exists = false;
        for (int i = 0; i < xor.length; i++) {
            if (f == xor[i].getPrimary()
                    || f == xor[i].getSecondary()) {
                exists = true;
            }
        }
        return exists;
    }
    
    private static boolean isAND(Feature f, ExistanceDependency[] and) {
        boolean exists = false;
        for (int i = 0; i < and.length; i++) {
            if (f == and[i].getPrimary()
                    || f == and[i].getSecondary()) {
                exists = true;
            }
        }
        return exists;
    }
    
    private static boolean isXOR(ReleaseDependency dep, ExistanceDependency[] xor) {
        boolean exists = false;
        for (int i = 0; i < xor.length; i++) {
            if (dep.getFeature() == xor[i].getPrimary()
                    || dep.getFeature() == xor[i].getSecondary()) {
                exists = true;
            }
        }
        return exists;
    }

    private static boolean isXOR(OrderDependency dep, ExistanceDependency[] xor) {
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
    private static int isXOR(OrderDependency dep, OrderDependency[] xor) {
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

    private static void getSoftPrecedenceCode(StringBuilder sb, DataPackage dp) {
        if (dp.SoftPrecedenceDS.length > 0) {
            for (int i = 0; i < dp.SoftPrecedenceDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(dp.SoftPrecedenceDS[i], dp.XorDS);
                xor = xor || isXOR(dp.SoftPrecedenceDS[i], dp.MXorDS);
                if (!xor) {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.SoftPrecedenceDS[i].getPrimary()) + 1) + "] <= x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.SoftPrecedenceDS[i].getSecondary()) + 1) + "];"
                            + "\n");
                } else {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.SoftPrecedenceDS[i].getPrimary()) + 1) + "] <= x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.SoftPrecedenceDS[i].getSecondary()) + 1) + "] \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.SoftPrecedenceDS[i].getPrimary()) + 1) + " = 0 \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.SoftPrecedenceDS[i].getSecondary()) + 1) + " = 0;"
                            + "\n");
                }
            }
        }
        if (dp.MSoftPrecedenceDS.length > 0) {
            for (int i = 0; i < dp.MSoftPrecedenceDS.length; i++) {
                boolean xor = isXOR(dp.MSoftPrecedenceDS[i], dp.MXorDS);
                if (!xor) {
                    // Do we need even this part? As from MSoftPrecedence only features features with xor can come
                    sb.append("constraint x["
                            + (dp.modDep.getFeatures().getFeatureIndex(dp.MSoftPrecedenceDS[i].getPrimary())
                            + dp.project.getFeatures().getFeatureCount() + 1) + "] <= x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.MSoftPrecedenceDS[i].getSecondary()) + 1) + "];"
                            + "\n");
                } else {
                    sb.append("constraint x["
                            + (dp.modDep.getFeatures().getFeatureIndex(dp.MSoftPrecedenceDS[i].getPrimary())
                            + dp.project.getFeatures().getFeatureCount() + 1) + "] <= x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.MSoftPrecedenceDS[i].getSecondary()) + 1) + "] \\/ x["
                            + (dp.modDep.getFeatures().getFeatureIndex(dp.MSoftPrecedenceDS[i].getPrimary())
                            + dp.project.getFeatures().getFeatureCount() + 1) + " = 0 \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.MSoftPrecedenceDS[i].getSecondary()) + 1) + " = 0;"
                            + "\n");
                }
            }
        }
    }

    private static void getHardPrecedenceCode(StringBuilder sb, DataPackage dp) {
        if (dp.HardPrecedenceDS.length > 0) {
            for (int i = 0; i < dp.HardPrecedenceDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(dp.HardPrecedenceDS[i], dp.XorDS);
                xor = xor || isXOR(dp.HardPrecedenceDS[i], dp.MXorDS);
                if (!xor) {
                    sb.append("constraint x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.HardPrecedenceDS[i].getSecondary()) + 1) + "] <= "
                            + (dp.project.getReleases().getReleaseCount()) + " -> x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.HardPrecedenceDS[i].getPrimary()) + 1) + "] < x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.HardPrecedenceDS[i].getSecondary()) + 1) + "];"
                            + "\n");
                } else {
                    sb.append("constraint (x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.HardPrecedenceDS[i].getSecondary()) + 1) + "] <= "
                            + (dp.project.getReleases().getReleaseCount()) + " -> x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.HardPrecedenceDS[i].getPrimary()) + 1) + "] < x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.HardPrecedenceDS[i].getSecondary()) + 1) + "]) \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.HardPrecedenceDS[i].getPrimary()) + 1) + " = 0 \\/ x["
                            + (dp.project.getFeatures().getFeatureIndex(dp.HardPrecedenceDS[i].getSecondary()) + 1) + " = 0;"
                            + "\n");
                }
            }
        }
        if (dp.MHardPrecedenceDS.length > 0) {
            for (int i = 0; i < dp.MHardPrecedenceDS.length; i++) {
                sb.append("constraint (x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.MHardPrecedenceDS[i].getSecondary()) + 1) + "] <= "
                        + (dp.project.getReleases().getReleaseCount()) + " -> x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.MHardPrecedenceDS[i].getPrimary()) + 1) + "] < x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.MHardPrecedenceDS[i].getSecondary()) + 1) + "]) \\/ x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.MHardPrecedenceDS[i].getPrimary()) + 1) + " = 0 \\/ x["
                        + (dp.project.getFeatures().getFeatureIndex(dp.MHardPrecedenceDS[i].getSecondary()) + 1) + " = 0;"
                        + "\n");
            }
        }
    }
}
