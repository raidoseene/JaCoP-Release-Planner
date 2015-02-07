/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Raido Seene
 */
public class Feature extends NamedObject implements Serializable {
    
    private final Map<Resource, Integer> consumptions;
    
    Feature() {
        this.consumptions = new HashMap<>();
    }

    public void setConsumption(Resource r, int consumption) {
        if (consumption > 0) {
            this.consumptions.put(r, consumption);
        } else {
            this.consumptions.remove(r);
        }
    }

    public int getConsumption(Resource r) {
        Integer value = this.consumptions.get(r);
        if (value != null) {
            return value;
        }
        
        return 0;
    }
    
    boolean removeConsumption(Resource r) {
        return (this.consumptions.remove(r) != null);
    }
    
    boolean hasConsumption(Resource r) {
        return this.consumptions.containsKey(r);
    }
}
