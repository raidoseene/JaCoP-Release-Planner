/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import ee.raidoseene.releaseplanner.gui.Messenger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Resources {

    private List<Resource> resourceContainer;
    private Releases releases;

    Resources(List<String> defaultResources, Releases releases) {
        resourceContainer = new ArrayList<>();
        this.releases = releases;
        for (String r : defaultResources) {
            Resource res = new Resource();
            resourceContainer.add(res);
            res.setName(r);
        }
    }

    public Resource addResource() {
        Resource r = new Resource();
        resourceContainer.add(r);
        releases.addResource(r);
        return r;
    }

    public void removeResource(Resource r) {
            resourceContainer.remove(r);
            releases.removeResource(r);
    }

    public List<Resource> getResourceList() {
        return resourceContainer;
    }
}
