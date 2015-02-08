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

    private final List<Dependency> interdependenciesContainer;

    Interdependencies(Project project) {
        super(project);

        this.interdependenciesContainer = new ArrayList<>();
    }
    
    public Dependency addFixedDependency(Feature feature, Release release) {
        Dependency dependency = new FixedDependency(feature, release);
        this.interdependenciesContainer.add(dependency);
        return dependency;
    }
    
    public Dependency addInterdependency(Feature feature1, Feature feature2, int type) {
        Dependency dependency = new Interdependency(feature1, feature2, type);
        this.interdependenciesContainer.add(dependency);
        return dependency;
    }
    
    public Dependency addModifyingInterdependency(Feature feature1, Feature feature2, Feature feature) {
        Dependency dependency = new ModifyingInterdependency(feature1, feature2, feature);
        this.interdependenciesContainer.add(dependency);
        return dependency;
    }
    
    public Dependency addModifyingInterdependency(Feature feature1, Feature feature2, Value value) {
        Dependency dependency = new ModifyingInterdependency(feature1, feature2, value);
        this.interdependenciesContainer.add(dependency);
        return dependency;
    }
    
    public Dependency addModifyingInterdependency(Feature feature1, Feature feature2, Urgency urgency) {
        Dependency dependency = new ModifyingInterdependency(feature1, feature2, urgency);
        this.interdependenciesContainer.add(dependency);
        return dependency;
    }

    public void removeInterdependency(Dependency dependency) {
        if (this.interdependenciesContainer.remove(dependency) && super.parent != null) {
            super.parent.dependencyRemoved(dependency);
        }
    }

    public Dependency getInterdependency(int index) {
        return this.interdependenciesContainer.get(index);
    }

    public int getInterdependencyCount() {
        return this.interdependenciesContainer.size();
    }
}
