/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel;

import ee.raidoseene.releaseplanner.gui.Messenger;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raido Seene
 */
public class Resources {

    private List<Resource> resourceContainer;

    Resources(List<String> defaultResources) {
        resourceContainer = new ArrayList<Resource>();
        for (String r : defaultResources) {
            resourceContainer.add(new Resource(r));
        }
    }

    public void addResource() {
        resourceContainer.add(new Resource());
        /*if (this.getResource(name) == null) {
            resourceContainer.add(new Resource(name));
            return true;
        } else {
            Messenger.showWarning(null, "Resource: " + name + " already exists");
            return false;
        }*/
    }

    public void removeResource(int id) {
        try {
            resourceContainer.remove(this.getResource(id));
        } catch (Exception ex) {
            Messenger.showWarning(ex, "Impossible to remove resource");
        }
    }

    public void changeResourceName(int id ,String name) {
        this.getResource(id).setName(name);
        //this.getResource(oldName).setName(newName);
    }

    public List<Resource> getResourceList() {
        List<Resource> list = new ArrayList<Resource>();
        for (Resource r : resourceContainer) {
            list.add(r/*.getName()*/);
        }
        return list;
    }

    private Resource getResource(int id/*String name*/) {
        return resourceContainer.get(id);
        /*for (Resource r : resourceContainer) {
            if (r.getName().equals(name)) {
                return r;
            }
        }
        return null;*/
    }
}
