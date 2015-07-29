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
public class ExistanceDependency extends Dependency implements Serializable {

    private static final long serialVersionUID = 1;
    
    protected final Feature primary, secondary;

    ExistanceDependency(Feature f1, Feature f2, int type) {
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

    public static String getToken(ExistanceDependency dep) {
        if (dep.type == Dependency.AND) {
            return "&";
        } else if (dep.type == Dependency.XOR) {
            return "^";
        }
        return "?";
    }
}
