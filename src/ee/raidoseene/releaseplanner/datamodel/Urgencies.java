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
public class Urgencies {
    
    private final List<Urgency> urgenciesContainer;
    
    Urgencies() {
        this.urgenciesContainer = new ArrayList<>();
    }
    
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
    
}
