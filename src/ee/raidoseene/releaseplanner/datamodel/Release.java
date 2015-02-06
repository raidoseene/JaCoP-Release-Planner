/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Raido Seene
 */
public class Release extends NamedObject {

    private final Map<Resource, Integer> capacities;
    private int importance;

    Release() {
        this.capacities = new HashMap<>();
        this.importance = 1;
    }

    /*Release(Resources resources) {
     capacity = new HashMap<>();
     for (Resource r : resources.getResourceList()) {
     this.addResource(r);
     }
     }*/

    public void setImportance(int importance) {
        if (importance > 0 && importance < 10) {
            this.importance = importance;
        } else {
            throw new ArrayIndexOutOfBoundsException(importance);
        }
    }

    public int getImportance() {
        return this.importance;
    }

    public void setCapacity(Resource r, int capacity) {
        if (capacity > 0) {
            this.capacities.put(r, capacity);
        } else {
            this.capacities.remove(r);
        }
    }

    public int getCapacity(Resource r) {
        Integer value = this.capacities.get(r);
        if (value != null) {
            return value;
        }
        
        return 0;
    }
    
    boolean removeCapacity(Resource r) {
        return (this.capacities.remove(r) != null);
    }
    
    boolean hasCapacity(Resource r) {
        return this.capacities.containsKey(r);
    }

    /*public void addResource(Resource r) {
        capacity.put(r, 0);
    }

    public void removeResource(Resource r) {
        capacity.remove(r);
    }*/
    
}
