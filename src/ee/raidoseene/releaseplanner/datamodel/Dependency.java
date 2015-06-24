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
public abstract class Dependency implements Serializable {

    private static final long serialVersionUID = 1;
    
    // Dependency masks
    public static final int TYPE_MASK = 0xf0;
    public static final int SUB_MASK = 0x0f;
    
    // Release related dependencies
    public static final int RELEASE = 0x10;
    public static final int FIXED = RELEASE | 0x01;
    public static final int EXCLUDED = RELEASE | 0x02;
    public static final int EARLIER = RELEASE | 0x03;
    public static final int LATER = RELEASE | 0x04;
    
    // Order related dependencies
    public static final int ORDER = 0x20;
    public static final int SOFTPRECEDENCE = ORDER | 0x01;
    public static final int HARDPRECEDENCE = ORDER | 0x02;
    public static final int COUPLING = ORDER | 0x03;
    public static final int SEPARATION = ORDER | 0x04;
    
    // Existance related dependencies
    public static final int EXISTANCE = 0x30;
    public static final int AND = EXISTANCE | 0x01;
    public static final int XOR = EXISTANCE | 0x02;
    
    // Modifying parameter dependencies
    public static final int MODIF = 0x40;
    public static final int CC = MODIF | 0x01;
    public static final int CV = MODIF | 0x02;
    public static final int CU = MODIF | 0x03;
    
    // Group related dependencies
    public static final int GROUP = 0x50;
    public static final int ATLEAST = GROUP | 0x01;
    public static final int EXACTLY = GROUP | 0x02;
    public static final int ATMOST = GROUP | 0x03;
    
    public final int type;

    protected Dependency(int type) {
        this.type = type;
    }
}
