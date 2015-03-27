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
public class Groups extends ProjectElement implements Serializable {
    
    private static final long serialVersionUID = 1;
    private final List<Group> groupContainer;
    
    Groups(Project project) {
        super(project);

        this.groupContainer = new ArrayList<>();
    }
    
    public Group addGroup() {
        Group g = new Group();
        this.groupContainer.add(g);
        return g;
    }

    public void removeGroup(Group g) {
        if (this.groupContainer.remove(g) && super.parent != null) {
            super.parent.groupRemoved(g);
        }
    }

    public Group getGroup(int index) {
        return this.groupContainer.get(index);
    }

    public int getGroupCount() {
        return this.groupContainer.size();
    }
    
    public int getGroupIndex(Group g) {
        return this.groupContainer.indexOf(g);
    }
    
    public void addFeature(Group g, Feature f) {
        Group group = getGroupByFeature(f);
        if(group != null) {
            group.removeFeature(f);
        }
        g.addFeature(f);
    }
    
    public List<Feature> getFeaturesInGroup(Group g) {
        ArrayList<Feature> list = new ArrayList<>();
        Group group = getGroup(getGroupIndex(g));
        for (int i = 0; i < group.getFeatureCount(); i++) {
            list.add(group.getFeature(i));
        }
        return list;
    }
    
    public Group getGroupByFeature(Feature f) {
        for(Group g : groupContainer) {
            if(g.contains(f)) {
                return g;
            }
        }
        return null;
    }
}
