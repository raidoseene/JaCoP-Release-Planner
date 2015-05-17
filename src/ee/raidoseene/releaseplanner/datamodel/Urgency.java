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
public class Urgency implements Serializable {
    
    private static final long serialVersionUID = 1;
    private final Map<Release, Integer> urgencies;
    
    public static final int DEADLYNE_MASK = 0xf0;
    public static final int CURVE_MASK = 0x0f;
    
    public static final int EXACT = 0x10;
    public static final int EARLIEST = 0x20;
    public static final int LATEST = 0x30;
    
    public static final int HARD = 0x01;
    public static final int SOFT = 0x02;
    
    private Release release;
    private int urgency;
    private int deadlineCurve;
    
    Urgency () {
        this.urgencies = new HashMap<>();
    }

    public void setUrgency(Release release, int urgency) {
        if (urgency > 0) {
            this.urgencies.put(release, urgency);
        } else {
            this.urgencies.remove(release);
        }
    }
    
    /*
    public void setUrgency(int urgency) {
        this.urgency = urgency;
    }
    
    public void setRelease(Release release) {
        this.release = release;
        
    }
    
    public void setDeadlineCurve(int deadlineCurve) {
        this.deadlineCurve = deadlineCurve;
    }
    */

    public int getUrgency(Release release) {
        Integer value = this.urgencies.get(release);
        if (value != null) {
            return value;
        }

        return 0;
    }
    
    /*
    public int getUrgency() {
        return this.urgency;
    }
    
    public Release getRelease() {
        return this.release;
    }
    
    public int getDeadlineCurve() {
        return this.deadlineCurve;
    }
    */

    boolean removeUrgency(Release release) {
        return (this.urgencies.remove(release) != null);
    }

    boolean hasUrgency(Release r) {
        return this.urgencies.containsKey(r);
    }
    
}
