/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Deprecated
 * @author Raido Seene
 */
public class Urgencies implements Serializable {
    
    private static final long serialVersionUID = 1;
    private final List<Urgency> urgenciesContainer;
    
    Urgencies() {
        this.urgenciesContainer = new ArrayList<>();
    }
    
    /*
    public Urgency addUrgency(Feature feature, Stakeholder stakeholder) {
        Urgency urgency = new Urgency(feature, stakeholder);
        this.urgenciesContainer.add(urgency);
        return urgency;
    }
    
    public void removeUrgency(Urgency urgency) {
        this.urgenciesContainer.remove(urgency);
    }
    
    public Urgency getUrgency(int index) {
        return this.urgenciesContainer.get(index);
    }

    public int getUrgecnyCount() {
        return this.urgenciesContainer.size();
    }
    
    public List<Urgency> getUrgenciesByStakeholder(Stakeholder stakeholder) {
        ArrayList<Urgency> list = new ArrayList<>();
        for (Urgency u: this.urgenciesContainer) {
            if(u.getStakeholder() == stakeholder) {
                list.add(u);
            }
        }
        return list;
    }
    
    public List<Urgency> getUrgenciesByFeature(Feature feature) {
        ArrayList<Urgency> list = new ArrayList<>();
        for (Urgency u : this.urgenciesContainer) {
            if (u.getFeature() == feature) {
                list.add(u);
            }
        }
        return list;
    }
    */
}
