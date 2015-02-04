/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Release {

    private String name;
    private int importance;
    private List<Pair> capacity; // resource, capacity

    Release(Resources resources) {
        capacity = new ArrayList<Pair>();
        for (Resource r : resources.getResourceList()) {
            addCapacity(r, 0);
        }
    }
    
    Release(String name, Resources resources) {
        this.name = name;
        capacity = new ArrayList<Pair>();
        for (Resource r : resources.getResourceList()) {
            addCapacity(r, 0);
        }
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
    
    public void setImportance(int importance) {
        this.importance = importance;
    }
    
    public int getImportance() {
        return this.importance;
    }
    
    public void addCapacity(Resource r, int capacity) {
        this.capacity.add(new Pair(r, capacity));
    }
    
    public void changeCapacity(Resource r, int capacity) {
        this.capacity.get(this.capacity.indexOf(r)).setRight(capacity);
    }
}
