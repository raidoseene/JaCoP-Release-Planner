/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataoutput;

import ee.raidoseene.releaseplanner.datamodel.Dependencies;
import ee.raidoseene.releaseplanner.datamodel.Dependency;
import ee.raidoseene.releaseplanner.datamodel.ExistanceDependency;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.Group;
import ee.raidoseene.releaseplanner.datamodel.ReleaseDependency;
import ee.raidoseene.releaseplanner.datamodel.GroupDependency;
import ee.raidoseene.releaseplanner.datamodel.Groups;
import ee.raidoseene.releaseplanner.datamodel.ModifyingParameterDependency;
import ee.raidoseene.releaseplanner.datamodel.OrderDependency;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Urgency;
import ee.raidoseene.releaseplanner.datamodel.Value;
import ee.raidoseene.releaseplanner.datamodel.ValueAndUrgency;

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
    
    private Project project;
    private Project modDep;
    
    private Dependencies deps;
    private Dependencies modDeps;
    private Groups groups;
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

    public DependencyManager(Project project) {
        this.project = project;
        this.modDep = modifyingDependencyConversion();
        initializeData();
    }

    private void initializeData() {
        this.deps = this.project.getDependencies();
        this.modDeps = this.modDep.getDependencies();

        this.groups = this.project.getGroups();

        this.FixedDS = this.deps.getTypedDependencies(ReleaseDependency.class, Dependency.FIXED);
        this.ExcludedDS = this.deps.getTypedDependencies(ReleaseDependency.class, Dependency.EXCLUDED);
        this.EarlierDS = this.deps.getTypedDependencies(ReleaseDependency.class, Dependency.EARLIER);
        this.LaterDS = this.deps.getTypedDependencies(ReleaseDependency.class, Dependency.LATER);

        this.SoftPrecedenceDS = this.deps.getTypedDependencies(OrderDependency.class, Dependency.SOFTPRECEDENCE);
        this.MSoftPrecedenceDS = this.modDeps.getTypedDependencies(OrderDependency.class, Dependency.SOFTPRECEDENCE);
        this.HardPrecedenceDS = this.deps.getTypedDependencies(OrderDependency.class, Dependency.HARDPRECEDENCE);
        this.MHardPrecedenceDS = this.modDeps.getTypedDependencies(OrderDependency.class, Dependency.HARDPRECEDENCE);
        this.CouplingDS = this.deps.getTypedDependencies(OrderDependency.class, Dependency.COUPLING);
        this.SeparationDS = this.deps.getTypedDependencies(OrderDependency.class, Dependency.SEPARATION);

        this.AndDS = this.deps.getTypedDependencies(ExistanceDependency.class, Dependency.AND);
        this.XorDS = this.deps.getTypedDependencies(ExistanceDependency.class, Dependency.XOR);
        this.MXorDS = this.modDeps.getTypedDependencies(ExistanceDependency.class, Dependency.XOR);

        this.AtLeastDS = this.deps.getTypedDependencies(GroupDependency.class, Dependency.ATLEAST);
        this.ExactlyDS = this.deps.getTypedDependencies(GroupDependency.class, Dependency.EXACTLY);
        this.AtMostDS = this.deps.getTypedDependencies(GroupDependency.class, Dependency.ATMOST);
    }
    //}
    
    public Project getModDep() {
        return this.modDep;
    }
    
    public ReleaseDependency[] getReleaseDS(int type) {
        if(type == Dependency.FIXED) {
            return this.FixedDS;
        } else if(type == Dependency.EXCLUDED) {
            return this.ExcludedDS;
        } else if(type == Dependency.EARLIER) {
            return this.EarlierDS;
        } else if(type == Dependency.LATER) {
            return this.LaterDS;
        } else {
            return null;
        }
    }
    
    public OrderDependency[] getOrderDS(int type) {
        if(type == Dependency.SOFTPRECEDENCE) {
            /*
            int len = this.SoftPrecedenceDS.length;
            int mLen = this.MSoftPrecedenceDS.length;
            OrderDependency[] temp = new OrderDependency[len + mLen];
            System.arraycopy(this.SoftPrecedenceDS, 0, temp, 0, len);
            System.arraycopy(this.MSoftPrecedenceDS, 0, temp, len, mLen);
            return temp;
            */
            return this.SoftPrecedenceDS;
        } else if(type == Dependency.HARDPRECEDENCE) {
            /*
            int len = this.HardPrecedenceDS.length;
            int mLen = this.MHardPrecedenceDS.length;
            OrderDependency[] temp = new OrderDependency[len + mLen];
            System.arraycopy(this.HardPrecedenceDS, 0, temp, 0, len);
            System.arraycopy(this.MHardPrecedenceDS, 0, temp, len, mLen);
            return temp;
            */
            return this.HardPrecedenceDS;
        } else if(type == Dependency.COUPLING) {
            return this.CouplingDS;
        } else if(type == Dependency.SEPARATION) {
            return this.SeparationDS;
        } else {
            return null;
        }
    }
    
    public ExistanceDependency[] getExistanceDS(int type) {
        if(type == Dependency.AND) {
            return this.AndDS;
        } else if(type == Dependency.XOR) {
            /*
            int len = this.XorDS.length;
            int mLen = this.MXorDS.length;
            ExistanceDependency[] temp = new ExistanceDependency[len + mLen];
            System.arraycopy(this.XorDS, 0, temp, 0, len);
            System.arraycopy(this.MXorDS, 0, temp, len, mLen);
            return temp;
            */
            return this.XorDS;
        } else {
            return null;
        }
    }
    
    public GroupDependency[] getGroupDS(int type) {
        if(type == Dependency.ATLEAST) {
            return this.AtLeastDS;
        } else if(type == Dependency.EXACTLY) {
            return this.ExactlyDS;
        } else if(type == Dependency.ATMOST) {
            return this.AtLeastDS;
        } else {
            return null;
        }
    }

    public String getDependenciesData(boolean codeOutput) {
        //DataPackage dp = new DataPackage(project, modDep);

        if (codeOutput) {
            return getDependencyCode();
        } else {
            return "Existing code file solution deprecated!";
            //return getDependencyRawData(dp);
        }
    }

    public String getGroupDependenciesData() {
        if (this.project.getDependencies().getTypedDependancyCount(GroupDependency.class, Dependency.GROUP) > 0) {
            //DataPackage dp = new DataPackage(this.project, modDep);
            return getGroupDepData();
        }
        return "";
    }

    private String getDependencyCode() {
        StringBuilder sb = new StringBuilder();

        // Non-XOR features
        sb.append("% Non-XOR dependencies").append("\n");
        if (this.XorDS.length > 0 || this.MXorDS.length > 0) {
            for (int i = 0; i < this.project.getFeatures().getFeatureCount(); i++) {
                boolean xorAnd = false;
                xorAnd = xorAnd || isXOR(this.project.getFeatures().getFeature(i), this.XorDS);
                xorAnd = xorAnd || isXOR(this.project.getFeatures().getFeature(i), this.MXorDS);
                xorAnd = xorAnd || isAND(this.project.getFeatures().getFeature(i), this.AndDS);
                if (!xorAnd) {
                    sb.append("constraint x[");
                    sb.append(i + 1).append("] > 0;");
                    sb.append("\n");
                }
            }
            for (int i = 0; i < this.modDep.getFeatures().getFeatureCount(); i++) {
                boolean xor = false;
                xor = xor || isXOR(this.modDep.getFeatures().getFeature(i), this.XorDS);
                xor = xor || isXOR(this.modDep.getFeatures().getFeature(i), this.MXorDS);
                if (!xor) {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureCount() + i + 1).append("] > 0;");
                    sb.append("\n");
                }
            }
        } else {
            int featureCount = this.project.getFeatures().getFeatureCount()
                    + this.modDep.getFeatures().getFeatureCount();
            for (int i = 0; i < featureCount; i++) {
                sb.append("constraint x[");
                sb.append(i + 1).append("] > 0;");
                sb.append("\n");
            }
        }

        sb.append("% Release related dependencies:" + "\n");

        // Fixed release
        sb.append("% FIXED dependencies" + "\n");
        if (this.FixedDS.length > 0) {
            for (int i = 0; i < this.FixedDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(this.FixedDS[i], this.XorDS);
                xor = xor || isXOR(this.FixedDS[i], this.MXorDS);
                if (!xor) {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.FixedDS[i].getFeature()) + 1).append("] = ");
                    sb.append(this.project.getReleases().getReleaseIndex(this.FixedDS[i].getRelease()) + 1).append(";");
                    sb.append("\n");
                } else {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.FixedDS[i].getFeature()) + 1).append("] = ");
                    sb.append(this.project.getReleases().getReleaseIndex(this.FixedDS[i].getRelease()) + 1).append(" \\/ x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.FixedDS[i].getFeature()) + 1).append(" = 0;");
                    sb.append("\n");
                }
            }
        }

        // Excluded release
        sb.append("% EXCLUDED dependencies" + "\n");
        if (this.ExcludedDS.length > 0) {
            for (int i = 0; i < this.ExcludedDS.length; i++) {
                sb.append("constraint x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.ExcludedDS[i].getFeature()) + 1).append("] != ");
                sb.append(this.project.getReleases().getReleaseIndex(this.ExcludedDS[i].getRelease()) + 1).append(";");
                sb.append("\n");
            }
        }

        // Earlier release
        sb.append("% EARLIER dependencies" + "\n");
        if (this.EarlierDS.length > 0) {
            for (int i = 0; i < this.EarlierDS.length; i++) {
                sb.append("constraint x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.EarlierDS[i].getFeature()) + 1).append("] < ");
                sb.append(this.project.getReleases().getReleaseIndex(this.EarlierDS[i].getRelease()) + 1).append(";");
                sb.append("\n");
            }
        }

        // Later release
        sb.append("% LATER dependencies" + "\n");
        if (this.LaterDS.length > 0) {
            for (int i = 0; i < this.LaterDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(this.LaterDS[i], this.XorDS);
                xor = xor || isXOR(this.LaterDS[i], this.MXorDS);
                if (!xor) {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.LaterDS[i].getFeature()) + 1).append("] > ");
                    sb.append(this.project.getReleases().getReleaseIndex(this.LaterDS[i].getRelease()) + 1).append(";");
                    sb.append("\n");
                } else {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.LaterDS[i].getFeature()) + 1).append("] > ");
                    sb.append(this.project.getReleases().getReleaseIndex(this.LaterDS[i].getRelease()) + 1).append(" \\/ x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.LaterDS[i].getFeature()) + 1).append(" = 0;");
                    sb.append("\n");
                }
            }
        }

        sb.append("% Order related dependencies:" + "\n");
        // SOFTPRECEDENCE dependency
        sb.append("% SOFTPRECEDENCE dependencies" + "\n");
        getSoftPrecedenceCode(sb);

        // HARDPRECEDENCE dependency
        sb.append("% HARDPRECEDENCE dependencies" + "\n");
        getHardPrecedenceCode(sb);

        // COUPLING dependency
        sb.append("% COUPLING dependencies" + "\n");
        if (this.CouplingDS.length > 0) {
            for (int i = 0; i < this.CouplingDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(this.CouplingDS[i], this.XorDS);
                xor = xor || isXOR(this.CouplingDS[i], this.MXorDS);
                if (!xor) {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.CouplingDS[i].getPrimary()) + 1).append("] = x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.CouplingDS[i].getSecondary()) + 1).append("];");
                    sb.append("\n");
                } else {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.CouplingDS[i].getPrimary()) + 1).append("] = x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.CouplingDS[i].getSecondary()) + 1).append("] \\/ x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.CouplingDS[i].getPrimary()) + 1).append("] = 0 \\/ x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.CouplingDS[i].getSecondary()) + 1).append("] = 0;");
                    sb.append("\n");
                }
            }
        }

        // SEPARATION dependency
        sb.append("% SEPARATION dependencies" + "\n");
        if (this.SeparationDS.length > 0) {
            for (int i = 0; i < this.SeparationDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(this.SeparationDS[i], this.XorDS);
                xor = xor || isXOR(this.SeparationDS[i], this.MXorDS);
                if (!xor) {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.SeparationDS[i].getPrimary()) + 1).append("] != x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.SeparationDS[i].getSecondary()) + 1).append("];");
                    sb.append("\n");
                } else {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.SeparationDS[i].getPrimary()) + 1).append("] != x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.SeparationDS[i].getSecondary()) + 1).append("] \\/ x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.SeparationDS[i].getPrimary()) + 1).append("] = 0 \\/ x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.SeparationDS[i].getSecondary()) + 1).append("] = 0;");
                    sb.append("\n");
                }
            }
        }

        sb.append("% Existance related dependencies:" + "\n");
        // AND dependency
        sb.append("% AND dependencies" + "\n");
        if (this.AndDS.length > 0) {
            for (int i = 0; i < this.AndDS.length; i++) {
                sb.append("constraint (x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.AndDS[i].getPrimary()) + 1).append("] != 0 /\\ x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.AndDS[i].getSecondary()) + 1).append("] != 0) xor (x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.AndDS[i].getPrimary()) + 1).append("] = 0 /\\ x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.AndDS[i].getSecondary()) + 1).append("] = 0);");
                sb.append("\n");
            }
        }

        // XOR dependency
        sb.append("% XOR dependencies" + "\n");
        if (this.XorDS.length > 0) {
            for (int i = 0; i < this.XorDS.length; i++) {
                /*
                sb.append("constraint (x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.XorDS[i].getPrimary()) + 1).append("] = 0) xor (x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.XorDS[i].getSecondary()) + 1).append("] = 0);");
                sb.append("\n");
                */
                /*
                sb.append("constraint (x["
                        + (this.project.getFeatures().getFeatureIndex(this.XorDS[i].getPrimary()) + 1) + "] = 0) != (x["
                        + (this.project.getFeatures().getFeatureIndex(this.XorDS[i].getSecondary()) + 1) + "] = 0);"
                        + "\n");
                */
                
                sb.append("constraint (x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.XorDS[i].getPrimary()) + 1).append("] = 0 /\\ x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.XorDS[i].getSecondary()) + 1).append("] != 0) \\/ (x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.XorDS[i].getPrimary()) + 1).append("] != 0 /\\ x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.XorDS[i].getSecondary()) + 1).append("] = 0);");
                sb.append("\n");
                
            }
        }
        if (this.MXorDS.length > 0) {
            for (int i = 0; i < this.MXorDS.length; i++) {
                /*
                sb.append("constraint (x["
                        + (this.project.getFeatures().getFeatureIndex(this.MXorDS[i].getPrimary()) + 1) + "] = 0) xor (x["
                        + (this.modDep.getFeatures().getFeatureIndex(this.MXorDS[i].getSecondary())
                        + this.project.getFeatures().getFeatureCount() + 1) + "] = 0);"
                        + "\n");
                */
                /*
                sb.append("constraint (x["
                        + (this.project.getFeatures().getFeatureIndex(this.MXorDS[i].getPrimary()) + 1) + "] = 0) != (x["
                        + (this.modDep.getFeatures().getFeatureIndex(this.MXorDS[i].getSecondary())
                        + this.project.getFeatures().getFeatureCount() + 1) + "] = 0);"
                        + "\n");
                */
                
                sb.append("constraint (x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.MXorDS[i].getPrimary()) + 1).append("] = 0 /\\ x[");
                sb.append(this.modDep.getFeatures().getFeatureIndex(this.MXorDS[i].getSecondary()) + this.project.getFeatures().getFeatureCount() + 1).append("] != 0) \\/ (x["); 
                sb.append(this.project.getFeatures().getFeatureIndex(this.MXorDS[i].getPrimary()) + 1).append("] != 0 /\\ x[");
                sb.append(this.modDep.getFeatures().getFeatureIndex(this.MXorDS[i].getSecondary()) + this.project.getFeatures().getFeatureCount() + 1).append("] = 0);");
                sb.append("\n");
                
            }
        }

        return sb.toString();
    }
    
    private String getGroupDepData() {
        StringBuilder sb = new StringBuilder();
        sb.append("% Group dependencies: AtLeast / Exactly / AtMost").append("\n");

        sb.append("ATLEAST = ").append(0).append(";").append("\n");
        sb.append("atLeast = [|");
        if (this.AtLeastDS.length > 0) {
            for (int i = 0; i < this.AtLeastDS.length; i++) {
                sb.append(" ").append(this.project.getGroups().getGroupIndex(this.AtLeastDS[i].getGroup()) + 1);
                sb.append(", ").append(this.AtLeastDS[i].getFeatureCount()).append(", |");
            }
        } else {
            sb.append(" 0, 0, |");
        }
        sb.append("];").append("\n");

        sb.append("EXACTLY = ").append(0).append(";").append("\n");
        sb.append("exactly = [|");
        if (this.ExactlyDS.length > 0) {
            for (int i = 0; i < this.ExactlyDS.length; i++) {
                sb.append(" ").append(this.project.getGroups().getGroupIndex(this.ExactlyDS[i].getGroup()) + 1);
                sb.append(", ").append(this.ExactlyDS[i].getFeatureCount()).append(", |");
            }
        } else {
            sb.append(" 0, 0, |");
        }
        sb.append("];").append("\n");

        sb.append("ATMOST = ").append(0).append(";").append("\n");
        sb.append("atMost = [|");
        if (this.AtMostDS.length > 0) {
            for (int i = 0; i < this.AtMostDS.length; i++) {
                sb.append(" ").append(this.project.getGroups().getGroupIndex(this.AtMostDS[i].getGroup()) + 1);
                sb.append(", ").append(this.AtMostDS[i].getFeatureCount()).append(", |");
            }
        } else {
            sb.append(" 0, 0, |");
        }
        sb.append("];").append("\n");

        sb.append("% =========================\n").append("\n");

        return sb.toString();
    }

    private boolean isXOR(Feature f, ExistanceDependency[] xor) {
        boolean exists = false;
        for (int i = 0; i < xor.length; i++) {
            if (f == xor[i].getPrimary()
                    || f == xor[i].getSecondary()) {
                exists = true;
            }
        }
        return exists;
    }

    private boolean isAND(Feature f, ExistanceDependency[] and) {
        boolean exists = false;
        for (int i = 0; i < and.length; i++) {
            if (f == and[i].getPrimary()
                    || f == and[i].getSecondary()) {
                exists = true;
            }
        }
        return exists;
    }

    private boolean isXOR(ReleaseDependency dep, ExistanceDependency[] xor) {
        boolean exists = false;
        for (int i = 0; i < xor.length; i++) {
            if (dep.getFeature() == xor[i].getPrimary()
                    || dep.getFeature() == xor[i].getSecondary()) {
                exists = true;
            }
        }
        return exists;
    }

    private boolean isXOR(OrderDependency dep, ExistanceDependency[] xor) {
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

    private void getSoftPrecedenceCode(StringBuilder sb) {
        if (this.SoftPrecedenceDS.length > 0) {
            for (int i = 0; i < this.SoftPrecedenceDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(this.SoftPrecedenceDS[i], this.XorDS);
                xor = xor || isXOR(this.SoftPrecedenceDS[i], this.MXorDS);
                if (!xor) {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.SoftPrecedenceDS[i].getPrimary()) + 1).append("] <= x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.SoftPrecedenceDS[i].getSecondary()) + 1).append("];");
                    sb.append("\n");
                } else {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.SoftPrecedenceDS[i].getPrimary()) + 1).append("] <= x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.SoftPrecedenceDS[i].getSecondary()) + 1).append("] \\/ x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.SoftPrecedenceDS[i].getPrimary()) + 1).append("] = 0 \\/ x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.SoftPrecedenceDS[i].getSecondary()) + 1).append("] = 0;");
                    sb.append("\n");
                }
            }
        }
        if (this.MSoftPrecedenceDS.length > 0) {
            for (int i = 0; i < this.MSoftPrecedenceDS.length; i++) {
                boolean xor = isXOR(this.MSoftPrecedenceDS[i], this.MXorDS);
                if (!xor) {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.MSoftPrecedenceDS[i].getPrimary()) + 1).append("] <= x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.MSoftPrecedenceDS[i].getSecondary()) + 1).append("];");
                    sb.append("\n");
                } else {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.MSoftPrecedenceDS[i].getPrimary()) + 1).append("] <= x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.MSoftPrecedenceDS[i].getSecondary()) + (this.project.getFeatures().getFeatureCount()) + 2).append("] \\/ x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.MSoftPrecedenceDS[i].getPrimary()) + 1).append("] = 0 \\/ x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.MSoftPrecedenceDS[i].getSecondary()) + (this.project.getFeatures().getFeatureCount()) + 2).append("] = 0;");
                    sb.append("\n");
                }
            }
        }
    }

    private void getHardPrecedenceCode(StringBuilder sb) {
        if (this.HardPrecedenceDS.length > 0) {
            for (int i = 0; i < this.HardPrecedenceDS.length; i++) {
                boolean xor = false;
                xor = xor || isXOR(this.HardPrecedenceDS[i], this.XorDS);
                xor = xor || isXOR(this.HardPrecedenceDS[i], this.MXorDS);
                if (!xor) {
                    sb.append("constraint x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.HardPrecedenceDS[i].getSecondary()) + 1).append("] < ");
                    sb.append(this.project.getReleases().getReleaseCount()).append(" -> x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.HardPrecedenceDS[i].getPrimary()) + 1).append("] < x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.HardPrecedenceDS[i].getSecondary()) + 1).append("];");
                    sb.append("\n");
                } else {
                    sb.append("constraint (x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.HardPrecedenceDS[i].getSecondary()) + 1).append("] < ");
                    sb.append(this.project.getReleases().getReleaseCount()).append(" -> x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.HardPrecedenceDS[i].getPrimary()) + 1).append("] < x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.HardPrecedenceDS[i].getSecondary()) + 1).append("]) \\/ x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.HardPrecedenceDS[i].getPrimary()) + 1).append("] = 0 \\/ x[");
                    sb.append(this.project.getFeatures().getFeatureIndex(this.HardPrecedenceDS[i].getSecondary()) + 1).append("] = 0;");
                    sb.append("\n");
                }
            }
        }
        if (this.MHardPrecedenceDS.length > 0) {
            for (int i = 0; i < this.MHardPrecedenceDS.length; i++) {
                sb.append("constraint (x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.MHardPrecedenceDS[i].getSecondary()) + 1).append("] < ");
                sb.append(this.project.getReleases().getReleaseCount()).append(" -> x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.MHardPrecedenceDS[i].getPrimary()) + 1).append("] < x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.MHardPrecedenceDS[i].getSecondary()) + 1).append("]) \\/ x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.MHardPrecedenceDS[i].getPrimary()) + 1).append("] = 0 \\/ x[");
                sb.append(this.project.getFeatures().getFeatureIndex(this.MHardPrecedenceDS[i].getSecondary()) + 1).append("] = 0;");
                sb.append("\n");
            }
        }
    }

    private Project modifyingDependencyConversion() {
        Project ModDep = new Project("ModifyingDependencies");
        if (this.project.getDependencies().getTypedDependancyCount(ModifyingParameterDependency.class, Dependency.CC) > 0) {
            ModifyingParameterDependency[] CcDS = this.project.getDependencies().getTypedDependencies(ModifyingParameterDependency.class, Dependency.CC);

            for (int dep = 0; dep < CcDS.length; dep++) {
                Feature f = ModDep.getFeatures().addFeature();
                f.setName(CcDS[dep].getSecondary().getName() + "' (Cost Changed)");
                for (int r = 0; r < this.project.getResources().getResourceCount(); r++) {
                    if (CcDS[dep].getSecondary().hasConsumption(this.project.getResources().getResource(r))) {
                        f.setConsumption(this.project.getResources().getResource(r),
                                CcDS[dep].getChange(Feature.class).getConsumption(this.project.getResources().getResource(r)));
                    }
                }

                for (int s = 0; s < this.project.getStakeholders().getStakeholderCount(); s++) {
                    ValueAndUrgency.ValUrg valUrg = this.project.getValueAndUrgency().getValUrgObject(this.project.getStakeholders().getStakeholder(s), CcDS[dep].getSecondary());
                    ModDep.getValueAndUrgency().setValUrgObject(this.project.getStakeholders().getStakeholder(s), f, valUrg);
                }

                ModDep.getDependencies().addOrderDependency(CcDS[dep].getPrimary(), f, Dependency.SOFTPRECEDENCE);
                ModDep.getDependencies().addOrderDependency(CcDS[dep].getSecondary(), CcDS[dep].getPrimary(), Dependency.HARDPRECEDENCE);
                ModDep.getDependencies().addExistanceDependency(CcDS[dep].getSecondary(), f, Dependency.XOR);
                duplicateDependencies(f, CcDS[dep].getSecondary());
            }
        }

        if (this.project.getDependencies().getTypedDependancyCount(ModifyingParameterDependency.class, Dependency.CV) > 0) {
            ModifyingParameterDependency[] CvDS = this.project.getDependencies().getTypedDependencies(ModifyingParameterDependency.class, Dependency.CV);

            for (int dep = 0; dep < CvDS.length; dep++) {
                Feature f = ModDep.getFeatures().addFeature();
                f.setName(CvDS[dep].getSecondary().getName() + "' (Value Changed)");
                for (int r = 0; r < this.project.getResources().getResourceCount(); r++) {
                    if (CvDS[dep].getSecondary().hasConsumption(this.project.getResources().getResource(r))) {
                        f.setConsumption(this.project.getResources().getResource(r),
                                CvDS[dep].getSecondary().getConsumption(this.project.getResources().getResource(r)));
                    }
                }
                
                ValueAndUrgency oldValAndUrg = this.project.getValueAndUrgency();
                ValueAndUrgency valAndUrg = ModDep.getValueAndUrgency();
                
                for (int s = 0; s < this.project.getStakeholders().getStakeholderCount(); s++) {
                    Urgency urgency = oldValAndUrg.getUrgencyObject(this.project.getStakeholders().getStakeholder(s), CvDS[dep].getSecondary());
                    int value = oldValAndUrg.getValue(this.project.getStakeholders().getStakeholder(s), CvDS[dep].getSecondary());
                    if (value > 0) {
                        valAndUrg.setValue(this.project.getStakeholders().getStakeholder(s), f, value);
                    }
                    if (urgency != null) {
                        valAndUrg.setUrgency(this.project.getStakeholders().getStakeholder(s), f, urgency.getUrgency());
                        valAndUrg.setDeadlineCurve(this.project.getStakeholders().getStakeholder(s), f, urgency.getDeadlineCurve());
                        valAndUrg.setRelease(this.project.getStakeholders().getStakeholder(s), f, urgency.getRelease());
                    }
                }
                
                Value value = CvDS[dep].getChange(Value.class);
                valAndUrg.setValue(value.getStakeholder(), f, value.getValue());

                ModDep.getDependencies().addOrderDependency(CvDS[dep].getPrimary(), f, Dependency.SOFTPRECEDENCE);
                ModDep.getDependencies().addOrderDependency(CvDS[dep].getSecondary(), CvDS[dep].getPrimary(), Dependency.HARDPRECEDENCE);
                ModDep.getDependencies().addExistanceDependency(CvDS[dep].getSecondary(), f, Dependency.XOR);
                duplicateDependencies(f, CvDS[dep].getSecondary());
            }
        }

        if (this.project.getDependencies().getTypedDependancyCount(ModifyingParameterDependency.class, Dependency.CU) > 0) {
            ModifyingParameterDependency[] CuDS = this.project.getDependencies().getTypedDependencies(ModifyingParameterDependency.class, Dependency.CU);

            for (int dep = 0; dep < CuDS.length; dep++) {
                Feature f = ModDep.getFeatures().addFeature();
                f.setName(CuDS[dep].getSecondary().getName() + "' (Urgency Changed)");
                for (int r = 0; r < this.project.getResources().getResourceCount(); r++) {
                    if (CuDS[dep].getSecondary().hasConsumption(this.project.getResources().getResource(r))) {
                        f.setConsumption(this.project.getResources().getResource(r),
                                CuDS[dep].getSecondary().getConsumption(this.project.getResources().getResource(r)));
                    }
                }

                ValueAndUrgency oldValAndUrg = this.project.getValueAndUrgency();
                ValueAndUrgency valAndUrg = ModDep.getValueAndUrgency();
                
                for (int s = 0; s < this.project.getStakeholders().getStakeholderCount(); s++) {
                    Urgency urgency = this.project.getValueAndUrgency().getUrgencyObject(this.project.getStakeholders().getStakeholder(s), CuDS[dep].getSecondary());
                    int value = oldValAndUrg.getValue(this.project.getStakeholders().getStakeholder(s), CuDS[dep].getSecondary());
                    if (value > 0) {
                        valAndUrg.setValue(this.project.getStakeholders().getStakeholder(s), f, value);
                    }
                    if (urgency != null) {
                        valAndUrg.setUrgency(this.project.getStakeholders().getStakeholder(s), f, urgency.getUrgency());
                        valAndUrg.setDeadlineCurve(this.project.getStakeholders().getStakeholder(s), f, urgency.getDeadlineCurve());
                        valAndUrg.setRelease(this.project.getStakeholders().getStakeholder(s), f, urgency.getRelease());
                    }
                }
                
                Urgency urgency = CuDS[dep].getChange(Urgency.class);
                valAndUrg.setUrgency(urgency.getStakeholder(), f, urgency.getUrgency());
                valAndUrg.setDeadlineCurve(urgency.getStakeholder(), f, urgency.getDeadlineCurve());
                valAndUrg.setRelease(urgency.getStakeholder(), f, urgency.getRelease());
                
                ModDep.getDependencies().addOrderDependency(CuDS[dep].getPrimary(), f, Dependency.SOFTPRECEDENCE);
                ModDep.getDependencies().addOrderDependency(CuDS[dep].getSecondary(), CuDS[dep].getPrimary(), Dependency.HARDPRECEDENCE);
                ModDep.getDependencies().addExistanceDependency(CuDS[dep].getSecondary(), f, Dependency.XOR);
                duplicateDependencies(f, CuDS[dep].getSecondary());
            }
        }
        return ModDep;
    }

    private void duplicateDependencies(Feature receiver, Feature donor) {
        duplicateReleaseDep(this.FixedDS, receiver, donor);
        duplicateReleaseDep(this.ExcludedDS, receiver, donor);
        duplicateReleaseDep(this.EarlierDS, receiver, donor);
        duplicateReleaseDep(this.LaterDS, receiver, donor);

        duplicateOrderDep(this.SoftPrecedenceDS, receiver, donor);
        duplicateOrderDep(this.HardPrecedenceDS, receiver, donor);
        duplicateOrderDep(this.CouplingDS, receiver, donor);
        duplicateOrderDep(this.SeparationDS, receiver, donor);

        duplicateExistanceDep(this.AndDS, receiver, donor);
        duplicateExistanceDep(this.XorDS, receiver, donor);

        duplicateGroupDep(this.AtLeastDS, receiver, donor);
        duplicateGroupDep(this.AtMostDS, receiver, donor);
        duplicateGroupDep(this.ExactlyDS, receiver, donor);
    }

    private void duplicateReleaseDep(ReleaseDependency[] dependencies, Feature receiver, Feature donor) {
        int type;
        int length = (dependencies != null) ? dependencies.length : 0;

        for (int i = 0; i < length; i++) {
            if (dependencies[i].getFeature() == donor) {
                type = dependencies[i].getType();
                this.deps.addReleaseDependency(receiver, dependencies[i].getRelease(), type, false, true);
            }
        }
    }

    private void duplicateOrderDep(OrderDependency[] dependencies, Feature receiver, Feature donor) {
        int type;
        int length = (dependencies != null) ? dependencies.length : 0;

        for (int i = 0; i < length; i++) {
            if (dependencies[i].getPrimary() == donor) {
                type = dependencies[i].getType();
                this.deps.addOrderDependency(receiver, dependencies[i].getSecondary(), type);
            } else if (dependencies[i].getSecondary() == donor) {
                type = dependencies[i].getType();
                this.deps.addOrderDependency(dependencies[i].getPrimary(), receiver, type);
            }
        }
    }

    private void duplicateExistanceDep(ExistanceDependency[] dependencies, Feature receiver, Feature donor) {
        int type;
        int length = (dependencies != null) ? dependencies.length : 0;

        for (int i = 0; i < length; i++) {
            if (dependencies[i].getPrimary() == donor) {
                type = dependencies[i].getType();
                this.deps.addExistanceDependency(receiver, dependencies[i].getSecondary(), type);
            } else if (dependencies[i].getSecondary() == donor) {
                type = dependencies[i].getType();
                this.deps.addExistanceDependency(dependencies[i].getPrimary(), receiver, type);
            }
        }
    }

    private void duplicateGroupDep(GroupDependency[] dependencies, Feature receiver, Feature donor) {
        int length = (dependencies != null) ? dependencies.length : 0;

        for (int i = 0; i < length; i++) {
            Group g = dependencies[i].getGroup();
            if (g.contains(donor)) {
                this.groups.addFeature(g, receiver);
            }
        }
    }
}
