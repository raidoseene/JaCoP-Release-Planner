/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;

/**
 *
 * @author Raido Seene
 */
public class Urgency extends ModifiableObject implements Serializable {
    
    private static final long serialVersionUID = 1;
    
    public static final int DEADLINE_MASK = 0xf0;
    public static final int CURVE_MASK = 0x0f;
    
    public static final int EXACT = 0x10;
    public static final int EARLIEST = 0x20;
    public static final int LATEST = 0x30;
    
    public static final int HARD = 0x01;
    public static final int SOFT = 0x02;
    
    private Release release;
    private int urgency;
    private int deadlineCurve;
    private Stakeholder stakeholder;
    
    Urgency () {}
    
    public void setUrgency(int urgency) {
        this.urgency = urgency;
        this.modify();
    }
    
    public void setRelease(Release release) {
        this.release = release;
        this.modify();
    }
    
    public void setDeadlineCurve(int deadlineCurve) {
        this.deadlineCurve = deadlineCurve;
        this.modify();
    }
    
    public void setStakeholder(Stakeholder stakeholder) {
        this.stakeholder = stakeholder;
        this.modify();
    }
    
    public int getUrgency() {
        return this.urgency;
    }
    
    public Release getRelease() {
        return this.release;
    }
    
    public int getDeadlineCurve() {
        return this.deadlineCurve;
    }
    
    public Stakeholder getStakeholder() {
        return this.stakeholder;
    }
}
