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
public class Resources extends ProjectElement implements Serializable {

    private static final long serialVersionUID = 1;
    private final List<Resource> resourceContainer;

    Resources(Project project) {
        super(project);

        this.resourceContainer = new ArrayList<>();
    }

    public Resource addResource() {
        Resource r = new Resource();
        this.resourceContainer.add(r);
        return r;
    }

    public void removeResource(Resource r) {
        if (this.resourceContainer.remove(r) && super.parent != null) {
            super.parent.resourceRemoved(r);
        }
    }

    public Resource getResource(int index) {
        return this.resourceContainer.get(index);
    }

    public int getResourceCount() {
        return this.resourceContainer.size();
    }
}
