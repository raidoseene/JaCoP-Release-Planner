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

    private static final long serialVersionUID = 1;
    private final Map<Resource, Integer> consumptions;
    private String comment;

    Feature() {
        this.consumptions = new HashMap<>();
    }

    public void setConsumption(Resource r, int consumption) {
        if (consumption > 0) {
            this.consumptions.put(r, consumption);
        } else {
            this.consumptions.remove(r);
        }
        this.modify();
    }

    public int getConsumption(Resource r) {
        Integer value = this.consumptions.get(r);
        if (value != null) {
            return value;
        }

        return 0;
    }

    public boolean removeConsumption(Resource r) {
        if (this.consumptions.remove(r) != null) {
            this.modify();
            return true;
        }
        return false;
    }

    public boolean hasConsumption(Resource r) {
        return this.consumptions.containsKey(r);
    }
    
    public void setComment(String comment) {
        this.comment = comment;
        this.modify();
    }
    
    public String getComment() {
        return this.comment;
    }
    
}
