/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 *
 * @author Raido Seene
 */
public final class ModifyingInterdependency extends Interdependency implements Serializable {
    
    private final Object change;
    
    public ModifyingInterdependency(Feature feature1, Feature feature2, Feature feature) {
        super(feature1, feature2, CC);
        this.change = feature;
    }
    
    public ModifyingInterdependency(Feature feature1, Feature feature2, Value value) {
        super(feature1, feature2, CV);
        this.change = value;
    }
    
    public ModifyingInterdependency(Feature feature1, Feature feature2, Urgency urgency) {
        super(feature1, feature2, CU);
        this.change = urgency;
    }
    
    public <T> T getChange(Class<T> cls) {
        if(cls.isInstance(change)) {
            return (T) change;
        } else {
            throw new RuntimeException();
        }
    }
    
    /*
    @Override
    public DependencyType getType() {
        return DependencyType.MODIFYINGDEPENDENCY;
    }
    */
    
}
