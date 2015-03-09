/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Dependencies extends ProjectElement implements Serializable {

    private static final long serialVersionUID = 1;
    private final List<Dependency> interdependenciesContainer;

    Dependencies(Project project) {
        super(project);

        this.interdependenciesContainer = new ArrayList<>();
    }
    
    public FixedDependency addFixedDependency(Feature feature, Release release) {
        FixedDependency dependency = new FixedDependency(feature, release);
        this.interdependenciesContainer.add(dependency);
        return dependency;
    }
    
    public Interdependency addInterdependency(Feature feature1, Feature feature2, int type) {
        Interdependency dependency = new Interdependency(feature1, feature2, type);
        this.interdependenciesContainer.add(dependency);
        return dependency;
    }
    
    public ModifyingInterdependency addModifyingInterdependency(Feature feature1, Feature feature2, Feature feature) {
        ModifyingInterdependency dependency = new ModifyingInterdependency(feature1, feature2, feature);
        this.interdependenciesContainer.add(dependency);
        return dependency;
    }
    
    public ModifyingInterdependency addModifyingInterdependency(Feature feature1, Feature feature2, Value value) {
        ModifyingInterdependency dependency = new ModifyingInterdependency(feature1, feature2, value);
        this.interdependenciesContainer.add(dependency);
        return dependency;
    }
    
    public ModifyingInterdependency addModifyingInterdependency(Feature feature1, Feature feature2, Urgency urgency) {
        ModifyingInterdependency dependency = new ModifyingInterdependency(feature1, feature2, urgency);
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
    
    /*
    public int getDependencyCount(DependencyType type) {
        int counter = 0;
        for(int i = 0; i < getDependencyCount(); i++) {
            if(getInterdependency(i).getType() == type) {
                counter ++;
            }
        }
        return counter;
    }
    */
    
    /*
    public List<Dependency> getDependencyList(DependencyType type) {
        List<Dependency> list = new ArrayList<>();
        
        for(int i = 0; i < getDependencyCount(); i++) {
            if(getInterdependency(i).getType() == type) {
                list.add(getInterdependency(i));
            }
        }
        return list;
    }
    */
    
    /*
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
    */
    
    /**
     * Query only certain types of dependencies.
     * For example, to get all instances of <code>FixedDependancy</code>:
     * FixedDependency[] fds = dependencies.getTypedDependencies(FixedDependency.class, null);
     * 
     * @param <T> type of dependencies to query
     * @param cls class that represents the type T
     * @param criterium optional criterium for filtering dependencies
     * @return Returns an array dependencies of type T
     */
    @SuppressWarnings("unchecked")
    public <T extends Dependency> T[] getTypedDependencies(Class<T> cls, Integer criterium) {
        ArrayList<T> list = new ArrayList<>(this.interdependenciesContainer.size());
        for (Dependency d: this.interdependenciesContainer) {
            if (cls.isInstance(d) && (criterium == null || d.type == criterium)) {
                list.add((T) d);
            }
        }
        
        return list.toArray((T[]) Array.newInstance(cls, list.size()));
    }
    
    public int getTypedDependancyCount(Class<? extends Dependency> cls, Integer criterium) {
        int count = 0;
        
        for (Dependency d: this.interdependenciesContainer) {
            if (cls.isInstance(d) && (criterium == null || d.type == criterium)) {
                count++;
            }
        }
        
        return count;
    }
    
}
