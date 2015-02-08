/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.datamodel;

import java.io.Serializable;

/**
 *
 * @author risto
 */
public abstract class NamedObject implements Serializable {

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
    }

    public final String getName() {
        return this.name;
    }
}
