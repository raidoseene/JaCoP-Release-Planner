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
public final class ModifyingParameterDependency extends Dependency implements Serializable {

    private static final long serialVersionUID = 1;
    protected final Feature primary, secondary;
    private final Object change;

    public ModifyingParameterDependency(Feature f1, Feature f2, Feature feature) {
        super(CC);
        this.primary = f1;
        this.secondary = f2;
        this.change = feature;
    }

    public ModifyingParameterDependency(Feature f1, Feature f2, Value value) {
        super(CV);
        this.primary = f1;
        this.secondary = f2;
        this.change = value;
    }

    public ModifyingParameterDependency(Feature f1, Feature f2, Urgency urgency) {
        super(CU);
        this.primary = f1;
        this.secondary = f2;
        this.change = urgency;
    }

    public int getType() {
        return this.type;
    }

    public Feature getPrimary() {
        return this.primary;
    }

    public Feature getSecondary() {
        return this.secondary;
    }

    public <T> T getChange(Class<T> cls) {
        if (cls.isInstance(change)) {
            return (T) change;
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public boolean isModified() {
        if (super.isModified()) {
            return true;
        } else {
            if (change != null && change instanceof ModifiableObject) {
                return ((ModifiableObject) change).isModified();
            }
            return false;
        }
    }

    @Override
    public void resetModification() {
        super.resetModification();
        if (change != null && change instanceof ModifiableObject) {
            ((ModifiableObject) change).resetModification();
        }
    }
}
