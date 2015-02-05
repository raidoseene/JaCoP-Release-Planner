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
public class Release {

    private String name;
    private int importance;
    private Map<Resource, Integer> capacity;

    Release(Resources resources) {
        capacity = new HashMap<>();
        for (Resource r : resources.getResourceList()) {
            this.addResource(r);
        }
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setImportance(int importance) {
        this.importance = importance;
    }
    
    public int getImportance() {
        return this.importance;
    }
    
    public void setCapacity(Resource r, int capacity) {
        this.capacity.put(r, capacity);
    }
    
    public int getCapacity(Resource r) {
        return capacity.get(r);
    }
    
    public void addResource(Resource r) {
        capacity.put(r, 0);
    }
    
    public void removeResource(Resource r) {
        capacity.remove(r);
    }
}
