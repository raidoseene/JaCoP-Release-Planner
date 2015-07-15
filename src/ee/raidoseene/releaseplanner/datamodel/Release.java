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
public class Release extends NamedObject implements Serializable {

    private static final long serialVersionUID = 1;
    private final Map<Resource, Integer> capacities;
    private int importance;
    private Type type;
    
    public enum Type {
        RELEASE,
        POSTPONED
    }

    Release() {
        this.type = Type.RELEASE;
        
        this.capacities = new HashMap<>();
        this.importance = 1;
    }
    
    Release(Type type) {
        this.type = type;
        
        this.capacities = new HashMap<>();
        this.importance = 1;
    }
    
    Release(Type type, int importance) {
        this.type = type;
        
        this.capacities = new HashMap<>();
        this.importance = importance;
    }
    
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
        if(this.type == Type.RELEASE) {
            if (capacity > 0) {
                this.capacities.put(r, capacity);
            } else {
                this.capacities.remove(r);
            }
        }
    }

    public int getCapacity(Resource r) {
        if(this.type == Type.RELEASE) {
            Integer value = this.capacities.get(r);
            if (value != null) {
                return value;
            }
            return 0;
        } else {
            return 0;
        }
    }

    public boolean removeCapacity(Resource r) {
        if(this.type == Type.RELEASE) {
            return (this.capacities.remove(r) != null);
        } else {
            return false;
        }
    }

    public boolean hasCapacity(Resource r) {
        if(this.type == Type.RELEASE) {
            return this.capacities.containsKey(r);
        } else {
            return false;
        }
    }
    
    public Type getType() {
        return this.type;
    }
}
