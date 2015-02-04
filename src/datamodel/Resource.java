/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package datamodel;

/**
 *
 * @author Raido Seene
 */
public class Resource {

    private String name;
    
    Resource() {}
    
    Resource(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
