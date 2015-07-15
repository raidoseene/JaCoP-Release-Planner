/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.gui.utils;

import ee.raidoseene.releaseplanner.datamodel.Feature;

/**
 *
 * @author Raido Seene
 */
public class FeatureNode {
    private Feature feature;
    private int up = 0;
    private int down = 0;
    
    FeatureNode(Feature feature) {
        this.feature = feature;
    }
    
    public Feature getFeature() {
        return this.feature;
    }
    
    public void addUpDistance(int dist) {
        this.up += dist;
    }
    
    public void addDownDistance(int dist) {
        this.down += dist;
    }
    
    public int getUpDistance() {
        return this.up;
    }
    
    public int getDownDistance() {
        return this.down;
    }
    
    public void resetDistances() {
        this.up = 0;
        this.down = 0;
    }
}
