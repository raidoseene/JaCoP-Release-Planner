/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;

/**
 *
 * @author Raido Seene
 */
public class FixedDependency implements Dependency, Serializable {
    
    private Feature feature;
    private Release release;
    
    FixedDependency(Feature feature, Release release) {
        this.feature = feature;
        this.release = release;
    }
}
