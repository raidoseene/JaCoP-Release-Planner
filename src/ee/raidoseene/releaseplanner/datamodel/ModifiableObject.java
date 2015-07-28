/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

/**
 *
 * @author Raido Seene
 */
public abstract class ModifiableObject {
    private transient boolean modified;
    
    ModifiableObject() {
        this.modified = false;
    }
    
    public void modify() {
        this.modified = true;
    }
    
    public boolean isModified() {
        return this.modified;
    }
    
    public void resetModification() {
        this.modified = false;
    }
}
