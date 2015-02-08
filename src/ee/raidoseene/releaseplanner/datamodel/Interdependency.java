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
public class Interdependency implements Dependency, Serializable {
    
    public static final int TYPE_AND = 1;
    public static final int TYPE_REQUIRES = 2;
    public static final int TYPE_XOR = 3;
    public static final int TYPE_CONDITIONAL = 4;
    protected final Feature primary, second;
    protected final int type;
    
    Interdependency(Feature f1, Feature f2, int type) {
        this.primary = f1;
        this.second = f2;
        this.type = type;
    }

}
