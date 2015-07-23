/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.solverutils;

import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.ProjectElement;
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
    
    public Simulation addSimulation() {
        Simulation s = new Simulation(this.parent.getFeatures());
        this.simulationContainer.add(s);
        return s;
    }

    public void removeSimulation(Simulation s) {
        this.simulationContainer.remove(s);
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
}
