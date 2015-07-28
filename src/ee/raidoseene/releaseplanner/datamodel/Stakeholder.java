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
public class Stakeholder extends NamedObject implements Serializable {

    private static final long serialVersionUID = 1;
    private int importance;

    Stakeholder() {
        this.importance = 1;
    }

    public void setImportance(int importance) {
        if (importance > 0 && importance < 10) {
            this.importance = importance;
            this.modify();
        } else {
            throw new ArrayIndexOutOfBoundsException(importance);
        }
    }

    public int getImportance() {
        return this.importance;
    }

}
