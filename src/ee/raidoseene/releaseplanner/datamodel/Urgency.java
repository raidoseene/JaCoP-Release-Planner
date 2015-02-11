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
public class Urgency {
    
    private final Feature feature;
    private final Stakeholder stakeholder;
    private final Map<Release, Integer> urgencies;
    
    Urgency(Feature feature, Stakeholder stakeholder) {
        this.feature = feature;
        this.stakeholder = stakeholder;
        this.urgencies = new HashMap<>();
    }

    public void setUrgency(Release release, int urgency) {
        if (urgency > 0) {
            this.urgencies.put(release, urgency);
        } else {
            this.urgencies.remove(release);
        }
    }

    public int getUrgency(Release release) {
        Integer value = this.urgencies.get(release);
        if (value != null) {
            return value;
        }

        return 0;
    }

    boolean removeUrgency(Release release) {
        return (this.urgencies.remove(release) != null);
    }

    boolean hasUrgency(Release r) {
        return this.urgencies.containsKey(r);
    }
    
}