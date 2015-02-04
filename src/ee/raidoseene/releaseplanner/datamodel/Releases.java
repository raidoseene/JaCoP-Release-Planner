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
public class Releases {

    private List<Release> releaseContainer;
    private Resources resources;

    Releases(List<String> defaultReleases, Resources resources) {
        releaseContainer = new ArrayList<Release>();
        this.resources = resources;
        for (String r : defaultReleases) {
            addRelease(r, resources);
        }
    }

    public void addRelease(Resources resources) {
        releaseContainer.add(new Release(resources));
    }
    
    public void addRelease(String name, Resources resources) {
        releaseContainer.add(new Release(name, resources));
    }

    public void removeRelease(int id) {
        try {
            releaseContainer.remove(this.getRelease(id));
        } catch (Exception ex) {
            Messenger.showWarning(ex, "Impossible to remove release");
        }
    }

    public void setReleaseName(int id, String name) {
        this.getRelease(id).setName(name);
    }
    
    public void setReleaseImportance(int id, int importance) {
        this.getRelease(id).setImportance(importance);
    }
    
    public void setReleaseCapacity(int id, Resource res, int capacity) {
        this.getRelease(id).changeCapacity(res, capacity);
    }

    public List<Release> getReleaseList() {
        List<Release> list = new ArrayList<Release>();
        for (Release r : releaseContainer) {
            list.add(r);
        }
        return list;
    }

    private Release getRelease(int id) {
        return releaseContainer.get(id);
    }
}
