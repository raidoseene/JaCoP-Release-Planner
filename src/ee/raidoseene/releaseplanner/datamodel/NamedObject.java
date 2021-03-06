/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;

/**
 *
 * @author Raido Seene
 */
public abstract class NamedObject extends ModifiableObject implements Serializable {

    private static final long serialVersionUID = 1;
    private String name;

    protected NamedObject() {
        this.name = new String();
    }

    protected NamedObject(String name) {
        this.setName(name);
    }

    public final void setName(String name) {
        if (name != null) {
            this.name = name;
        } else {
            this.name = new String();
        }
        this.modify();
    }

    public final String getName() {
        return this.name;
    }
}
