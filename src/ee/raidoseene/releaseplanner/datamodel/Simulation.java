/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Simulation extends ModifiableObject implements Serializable {
    
    private static final long serialVersionUID = 1;
    private final List<CandidatePlan> candidatePlanContainer;
    private long simulationDuraton;
    private String simulationDate;
    private String comment;
    
    private final Features independentFeatures;
    private final Features features;
    private final Releases releases;
    
    Simulation(Features features, Releases releases, Project modDep) {
        this.features = features;
        this.independentFeatures = modDep != null ? modDep.getFeatures() : null;
        this.releases = releases;
        candidatePlanContainer = new ArrayList<>();
        
        Date date = new Date();
        this.simulationDate = date.toString();
    }
    
    public CandidatePlan addCandidatePlan() {
        CandidatePlan cp = new CandidatePlan(this.features, this.releases, this.independentFeatures);
        this.candidatePlanContainer.add(cp);
        this.modify();
        return cp;
    }
    
    public void removeCandidatePlan(CandidatePlan cp) {
        if(this.candidatePlanContainer.remove(cp)) {
            this.modify();
        }
    }

    public CandidatePlan getCandidatePlan(int index) {
        return this.candidatePlanContainer.get(index);
    }

    public int getCandidatePlanCount() {
        return this.candidatePlanContainer.size();
    }
    
    public int getCandidatePlanIndex(CandidatePlan cp) {
        return this.candidatePlanContainer.indexOf(cp);
    }
    
    public String getSimulationDate() {
        return this.simulationDate;
    }
    
    public void setSimulationDuration(long duration) {
        this.simulationDuraton = duration;
        this.modify();
    }
    
    public long getSimulationDuration() {
        return this.simulationDuraton;
    }
    
    public void setComment(String comment) {
        this.comment = comment;
        this.modify();
    }
    
    public String getComment() {
        return this.comment;
    }
}
