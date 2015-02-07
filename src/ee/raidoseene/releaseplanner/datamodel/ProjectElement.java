/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

/**
 *
 * @author risto
 */
abstract class ProjectElement {
    
    protected Project parent;
    
    protected ProjectElement(Project project) {
        this.parent = project;
    }
    
    void setProject(Project project) {
        this.parent = project;
    }
    
    Project getProject() {
        return this.parent;
    }
    
}
