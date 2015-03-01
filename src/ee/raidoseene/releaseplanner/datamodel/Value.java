/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

/**
 * Needs to be deprecated
 * @author Raido Seene
 */
public class Value {
    
    private final Feature feature;
    private final Stakeholder stakeholder;
    private int value;
    
    Value(Feature feature, Stakeholder stakeholder) {
        this.feature = feature;
        this.stakeholder = stakeholder;
        this.value = 0;
    }

    /*
    public void setValue(int value) {
        if (value >= 0 && value < 10) {
            this.value = value;
        } else {
            throw new ArrayIndexOutOfBoundsException(value);
        }
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
    
    public Feature getFeature() {
        return this.feature;
    }
    
    public Stakeholder getStakeholder() {
        return this.stakeholder;
    }
    */
}
