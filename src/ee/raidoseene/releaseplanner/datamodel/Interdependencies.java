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
public class Interdependencies extends ProjectElement implements Serializable {

    //private final List<Interdependence> interdependenciesContainer;

    Interdependencies(Project project) {
        super(project);

        //this.interdependenciesContainer = new ArrayList<>();
    }
    
    /*public Interependence addInterdependence(Feature f1, Feature f2, Dependence d) {
        Interdependence dep = new Interdependence(f1, f2, d);
        this.interdependenciesContainer.add(dep);
        return dep;
    }

    public void removeInterdependence(Interdependence d) {
        if (this.interdependenciesContainer.remove(d) && super.parent != null) {
            super.parent.interdependenceRemoved(d);
        }
    }
    
    public Feature getInterdependence(int index) {
        return this.interdependenciesContainer.get(index);
    }

    public int getInterdependenceCount() {
        return this.interdependenciesContainer.size();
    }*/
}
