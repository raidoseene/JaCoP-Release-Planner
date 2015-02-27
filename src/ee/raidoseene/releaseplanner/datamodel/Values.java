/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Values implements Serializable {
    
    private final List<Value> valuesContainer;
    
    Values() {
        this.valuesContainer = new ArrayList<>();
    }
    
    public Value addValue(Feature feature, Stakeholder stakeholder) {
        Value value = new Value(feature, stakeholder);
        this.valuesContainer.add(value);
        return value;
    }
    
    public void removeValue(Value value) {
        this.valuesContainer.remove(value);
    }
    
    public Value getValue(int index) {
        return this.valuesContainer.get(index);
    }

    public int getValueCount() {
        return this.valuesContainer.size();
    }
    
    public List<Value> getValuesByStakeholder(Stakeholder stakeholder) {
        ArrayList<Value> list = new ArrayList<>();
        for (Value v: this.valuesContainer) {
            if(v.getStakeholder() == stakeholder) {
                list.add(v);
            }
        }
        return list;
    }
    
    public List<Value> getValuesByFeature(Feature feature) {
        ArrayList<Value> list = new ArrayList<>();
        for (Value v : this.valuesContainer) {
            if (v.getFeature() == feature) {
                list.add(v);
            }
        }
        return list;
    }
    
}
