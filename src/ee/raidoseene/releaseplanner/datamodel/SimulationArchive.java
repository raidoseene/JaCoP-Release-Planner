/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class SimulationArchive extends ProjectElement implements Serializable {
    
    private static final long serialVersionUID = 1;
    private final List<Simulation> simulationContainer;
    
    public SimulationArchive(Project project) {
        super(project);
        
        this.simulationContainer = new ArrayList<>();
    }
    
    public Simulation addSimulation(Project modDep) {
        Simulation s = new Simulation(this.parent.getFeatures(), this.parent.getReleases(), modDep);
        this.simulationContainer.add(s);
        this.modify();
        return s;
    }

    public void removeSimulation(Simulation s) {
        if(this.simulationContainer.remove(s)) {
            this.modify();
        }
    }

    public Simulation getSimulation(int index) {
        return this.simulationContainer.get(index);
    }

    public int getSimulationCount() {
        return this.simulationContainer.size();
    }
    
    public int getSimulationIndex(Simulation simulation) {
        return this.simulationContainer.indexOf(simulation);
    }
    
    @Override
    public boolean isModified() {
        if(super.isModified()) {
            return true;
        } else {
            for(Simulation s: simulationContainer) {
                if (s.isModified()) {
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
    public void resetModification() {
        super.resetModification();
        for(Simulation s: simulationContainer) {
            s.resetModification();
        }
    }
}
