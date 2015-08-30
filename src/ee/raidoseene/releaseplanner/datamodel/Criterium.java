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
public class Criterium extends NamedObject implements Serializable {
    
    private static final long serialVersionUID = 1;
    private boolean permanent;
    private int weight;
    
    public Criterium(boolean permanent) {
        this.permanent = permanent;
    }
    
    public void setWeight(int weight) {
        this.weight = weight;
        this.modify();
    }
    
    public int getWeight() {
        return this.weight;
    }
    
    public boolean isPermanent() {
        return this.permanent;
    }
}
