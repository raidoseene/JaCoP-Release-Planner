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
    private final List<Dependency> dependenciesContainer;

    Dependencies(Project project) {
        super(project);

        this.dependenciesContainer = new ArrayList<>();
    }

    public ReleaseDependency addReleaseDependency(Feature feature, Release release, int type, boolean automatic, boolean hasToPass) {
        if (capacityCheck(feature, release, type) || hasToPass) {
            //System.out.println("!!! SUCCESS !!!");
            ReleaseDependency dep = new ReleaseDependency(feature, release, type);
            this.dependenciesContainer.add(dep);
            return dep;
        } else {
            //System.out.println("!!! FEIL !!!");
            if(!automatic) {
                throw new RuntimeException("Adding another FIXED dependency to release " + release.getName()
                        + " will violate release capacity!");
            } else {
                return null;
            }
        }
    }

    public OrderDependency addOrderDependency(Feature feature1, Feature feature2, int type) {
        OrderDependency dependency = new OrderDependency(feature1, feature2, type);
        this.dependenciesContainer.add(dependency);
        return dependency;
    }

    public ExistanceDependency addExistanceDependency(Feature feature1, Feature feature2, int type) {
        ExistanceDependency dependency = new ExistanceDependency(feature1, feature2, type);
        this.dependenciesContainer.add(dependency);
        return dependency;
    }

    public ModifyingParameterDependency addModifyingParameterDependency(Feature feature1, Feature feature2, Feature feature) {
        ModifyingParameterDependency dependency = new ModifyingParameterDependency(feature1, feature2, feature);
        this.dependenciesContainer.add(dependency);
        return dependency;
    }

    public ModifyingParameterDependency addModifyingParameterDependency(Feature feature1, Feature feature2, Value value) {
        ModifyingParameterDependency dependency = new ModifyingParameterDependency(feature1, feature2, value);
        this.dependenciesContainer.add(dependency);
        return dependency;
    }

    public ModifyingParameterDependency addModifyingParameterDependency(Feature feature1, Feature feature2, Urgency urgency) {
        ModifyingParameterDependency dependency = new ModifyingParameterDependency(feature1, feature2, urgency);
        this.dependenciesContainer.add(dependency);
        return dependency;
    }

    public GroupDependency addAtLeastGroupDependency(Group group, int featureCount) {
        GroupDependency dependency = new GroupDependency(group, featureCount, Dependency.ATLEAST);
        this.dependenciesContainer.add(dependency);
        return dependency;
    }

    public GroupDependency addExactlyGroupDependency(Group group, int featureCount) {
        GroupDependency dependency = new GroupDependency(group, featureCount, Dependency.EXACTLY);
        this.dependenciesContainer.add(dependency);
        return dependency;
    }

    public GroupDependency addAtMostGroupDependency(Group group, int featureCount) {
        GroupDependency dependency = new GroupDependency(group, featureCount, Dependency.ATMOST);
        this.dependenciesContainer.add(dependency);
        return dependency;
    }

    public void removeInterdependency(Dependency dependency) {
        if (this.dependenciesContainer.remove(dependency) && super.parent != null) {
            super.parent.dependencyRemoved(dependency);
        }
    }

    public Dependency getInterdependency(int index) {
        return this.dependenciesContainer.get(index);
    }

    public int getDependencyCount() {
        return this.dependenciesContainer.size();
    }

    /**
     * Query only certain types of dependencies. For example, to get all
     * instances of
     * <code>FixedDependancy</code>: ReleaseDependency[] fds =
     * dependencies.getTypedDependencies(ReleaseDependency.class, null);
     *
     * @param <T> type of dependencies to query
     * @param cls class that represents the type T
     * @param criterium optional criterium for filtering dependencies
     * @return Returns an array dependencies of type T
     */
    @SuppressWarnings("unchecked")
    public <T extends Dependency> T[] getTypedDependencies(Class<T> cls, Integer criterium) {
        ArrayList<T> list = new ArrayList<>(this.dependenciesContainer.size());
        for (Dependency d : this.dependenciesContainer) {
            if (cls.isInstance(d) && (criterium == null || d.type == criterium)) {
                list.add((T) d);
            }
        }

        return list.toArray((T[]) Array.newInstance(cls, list.size()));
    }

    public int getTypedDependancyCount(Class<? extends Dependency> cls, Integer criterium) {
        int count = 0;

        for (Dependency d : this.dependenciesContainer) {
            if (cls.isInstance(d) && (criterium == null || d.type == criterium)) {
                count++;
            }
        }

        return count;
    }
    
    public Feature[] getDependantFeatures(Feature f) {
        ArrayList<Feature> list = new ArrayList();
        int depCount;
        
        depCount = getTypedDependancyCount(OrderDependency.class, null);
        if(depCount > 0) {
            OrderDependency deps[] = getTypedDependencies(OrderDependency.class, null);
            for(int i = 0; i < depCount; i++) {
                if(deps[i].getPrimary() == f) {
                    list.add(deps[i].getSecondary());
                } else if(deps[i].getSecondary() == f) {
                    list.add(deps[i].getPrimary());
                }
            }
        }
        
        depCount = getTypedDependancyCount(ExistanceDependency.class, null);
        if(depCount > 0) {
            ExistanceDependency deps[] = getTypedDependencies(ExistanceDependency.class, null);
            for(int i = 0; i < depCount; i++) {
                if(deps[i].getPrimary() == f) {
                    list.add(deps[i].getSecondary());
                } else if(deps[i].getSecondary() == f) {
                    list.add(deps[i].getPrimary());
                }
            }
        }
        
        return list.toArray((Feature[]) Array.newInstance(Feature.class, list.size()));
    }

    private boolean capacityCheck(Feature feature, Release release, int type) {
        if (type == Dependency.FIXED) {
            Resources resources = super.getProject().getResources();
            ReleaseDependency[] fixed = getTypedDependencies(ReleaseDependency.class, Dependency.FIXED);
            int sum;
            boolean check = true;

            if (release.getType() != Release.Type.POSTPONED) {
                for (int res = 0; res < resources.getResourceCount(); res++) {
                    sum = feature.getConsumption(resources.getResource(res));
                    for (ReleaseDependency f : fixed) {
                        sum += (f.getRelease() == release ? f.getFeature().getConsumption(resources.getResource(res)) : 0);
                    }
                    if (sum <= release.getCapacity(resources.getResource(res))) {
                        //System.out.println("Sum = " + sum + ", capacity = " + release.getCapacity(resources.getResource(res)));
                        check = check && true;
                    } else {
                        //System.out.println("Sum = " + sum + ", capacity = " + release.getCapacity(resources.getResource(res)));
                        check = false;
                    }
                }
            }

            return check;
        } else if (type == Dependency.EARLIER) {
            Resources resources = super.getProject().getResources();
            Releases releases = super.getProject().getReleases();
            ReleaseDependency[] fixed = getTypedDependencies(ReleaseDependency.class, Dependency.EARLIER);
            int sum;
            boolean check = true;

            //if (release.getType() != Release.Type.POSTPONED) {
                for (int res = 0; res < resources.getResourceCount(); res++) {
                    sum = feature.getConsumption(resources.getResource(res));
                    
                    int relIndex = releases.getReleaseIndex(release);
                    for (ReleaseDependency f : fixed) {
                        sum += (releases.getReleaseIndex(f.getRelease()) < relIndex ? f.getFeature().getConsumption(resources.getResource(res)) : 0);
                    }
                    int capSum = 0;
                    for(int r = 0; r < relIndex; r++) {
                        capSum = releases.getRelease(r).getCapacity(resources.getResource(res));
                    }
                    if (sum <= capSum) {
                        //System.out.println("Sum = " + sum + ", capacity = " + release.getCapacity(resources.getResource(res)));
                        check = check && true;
                    } else {
                        //System.out.println("Sum = " + sum + ", capacity = " + release.getCapacity(resources.getResource(res)));
                        check = false;
                    }
                }
            //}

            return check;
        } else {
            return true;
        }
    }
}
