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
public class Interdependency extends Dependency implements Serializable {
    
    /*
    public static final int TYPE_AND = 1;
    public static final int TYPE_REQUIRES = 2;
    public static final int TYPE_PRECEDES = 3;
    public static final int TYPE_XOR = 4;
    public static final int TYPE_CHANGE_IN_COST = 5;
    public static final int TYPE_CHANGE_IN_VALUE = 6;
    public static final int TYPE_CHANGE_IN_URGENCY = 7;
    */
    
    private static final long serialVersionUID = 1;
    protected final Feature primary, secondary;
    //protected final int type;
    
    Interdependency(Feature f1, Feature f2, int type) {
        super(type);
        this.primary = f1;
        this.secondary = f2;
    }
    
    public int getType() {
        return this.type;
    }

    /*
    @Override
    public DependencyType getType() {
        return DependencyType.INTERDEPENDENCY;
    }
    */

    /*
    public int getDependencySubType() {
        return this.type;
    }
    */
    
    public Feature getPrimary() {
        return this.primary;
    }
    
    public Feature getSecondary() {
        return this.secondary;
    }
    
}
