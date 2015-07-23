/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.solverutils;

import ee.raidoseene.releaseplanner.datamodel.Features;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Simulation implements Serializable {
    
    private static final long serialVersionUID = 1;
    private final List<CandidatePlan> candidatePlanContainer;
    private String simulationDate;
    private long simulationDuraton;
    private Features features;
    
    Simulation(Features features) {
        this.features = features;
        candidatePlanContainer = new ArrayList<>();
        
        Date date = new Date();
        this.simulationDate = date.toString();
    }
    
    public CandidatePlan addCandidatePlan() {
        CandidatePlan cp = new CandidatePlan(this.features);
        this.candidatePlanContainer.add(cp);
        return cp;
    }
    
    public void removeCandidatePlan(CandidatePlan cp) {
        this.candidatePlanContainer.remove(cp);
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
    }
    
    public long getSimulationDuration() {
        return this.simulationDuraton;
    }
}
