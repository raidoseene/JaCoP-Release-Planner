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
public final class ModifyingInterdependency extends Interdependency implements Serializable {
    
    public ModifyingInterdependency(Feature feature1, Feature feature2, Feature feature) {
        super(feature1, feature2, TYPE_CHANGE_IN_COST);
    }
    
    public ModifyingInterdependency(Feature feature1, Feature feature2, Value value) {
        super(feature1, feature2, TYPE_CHANGE_IN_VALUE);
    }
    
    public ModifyingInterdependency(Feature feature1, Feature feature2, Urgency urgency) {
        super(feature1, feature2, TYPE_CHANGE_IN_URGENCY);
    }
    
    @Override
    public DependencyType getType() {
        return DependencyType.MODIFYINGDEPENDENCY;
    }
    
}
