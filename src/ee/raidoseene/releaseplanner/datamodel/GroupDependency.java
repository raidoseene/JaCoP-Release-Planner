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
public class GroupDependency extends Dependency implements Serializable {

    private static final long serialVersionUID = 1;
    
    private Group group;
    private int fCount;

    GroupDependency(Group group, int fCount, int type) {
        super(type);
        this.group = group;
        this.fCount = fCount;
    }

    public Group getGroup() {
        return this.group;
    }

    public int getFeatureCount() {
        return this.fCount;
    }

    public int getType() {
        return this.type;
    }

    public void setFeatureCount(int fCount) {
        this.fCount = fCount;
        this.modify();
    }

    public void setGroup(Group group) {
        this.group = group;
        this.modify();
    }
}