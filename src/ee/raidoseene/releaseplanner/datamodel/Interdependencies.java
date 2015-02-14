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

    public int getDependencyCount() {
        return this.interdependenciesContainer.size();
    }
    
    public int getDependencyCount(DependencyType type) {
        int counter = 0;
        for(int i = 0; i < getDependencyCount(); i++) {
            if(getInterdependency(i).getType() == type) {
                counter ++;
            }
        }
        return counter;
    }
    
    public List<Dependency> getDependencyList(DependencyType type) {
        List<Dependency> list = new ArrayList<>();
        
        for(int i = 0; i < getDependencyCount(); i++) {
            if(getInterdependency(i).getType() == type) {
                list.add(getInterdependency(i));
            }
        }
        return list;
    }
    
    public List<Dependency> getInterdependencyList(DependencyType type, int subType) {
        List<Dependency> dList = getDependencyList(type);
        List<Dependency> list = new ArrayList<>();
        
        for(int i = 0; i < dList.size(); i++) {
            Interdependency id = ((Interdependency)dList.get(i));
            if(id.getDependencySubType() == subType) {
                list.add(id);
            }
        }
        return list;
    }
    
}
