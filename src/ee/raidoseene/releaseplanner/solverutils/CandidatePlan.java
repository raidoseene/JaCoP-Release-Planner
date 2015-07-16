/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.solverutils;

import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.Release;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Raido Seene
 */
public class CandidatePlan implements Serializable {
    
    private static final long serialVersionUID = 1;
    private final Map<Feature, Release> featureAllocations;
    private int planValue;
    private String comment;
    
    CandidatePlan(){
        this.featureAllocations = new HashMap<>();
    }
    
    public void setPlanValue(int value) {
        this.planValue = value;
    }
    
    public int getPlanValue() {
        return this.planValue;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String getComment() {
        return this.comment;
    }
    
    public void setAllocation(Feature f, Release r) {
        this.featureAllocations.put(f, r);
    }
    
    public Release getAllocation(Feature f) {
        return this.featureAllocations.get(f);
    }
}
