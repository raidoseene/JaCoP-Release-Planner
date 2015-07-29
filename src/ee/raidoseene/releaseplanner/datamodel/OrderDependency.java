/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;

/**
 *
 * @author Raido Seene
 */
public class OrderDependency extends Dependency implements Serializable {

    private static final long serialVersionUID = 1;
    
    protected final Feature primary, secondary;

    OrderDependency(Feature f1, Feature f2, int type) {
        super(type);
        this.primary = f1;
        this.secondary = f2;
    }

    public int getType() {
        return this.type;
    }

    public Feature getPrimary() {
        return this.primary;
    }

    public Feature getSecondary() {
        return this.secondary;
    }
    
    public static String getToken(OrderDependency dep, Project project) {
        Features f = project.getFeatures();
        if (dep.type == Dependency.SOFTPRECEDENCE) {
            if(f.getFeatureIndex(dep.primary) < f.getFeatureIndex(dep.secondary)) {
                return "<=";
            }
            return ">=";
        } else if (dep.type == Dependency.HARDPRECEDENCE) {
            if(f.getFeatureIndex(dep.primary) < f.getFeatureIndex(dep.secondary)) {
                return "<";
            }
            return ">";
        } else if (dep.type == Dependency.COUPLING) {
            return "><";
        } else if (dep.type == Dependency.SEPARATION) {
            return "<>";
        }
        return "?";
    }
}
