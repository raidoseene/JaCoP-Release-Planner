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
    public static final int TYPE_MASK = 0xf0;
    public static final int SUB_MASK = 0x0f;
    
    public static final int FIXED = 0x10;
    
    public static final int INTER = 0x20;
    public static final int AND = INTER | 0x01;
    public static final int REQ = INTER | 0x02;
    public static final int PRE = INTER | 0x03;
    public static final int XOR = INTER | 0x04;
    
    
    public static final int MODIF = 0x30;
    public static final int CC = MODIF | 0x01;
    public static final int CV = MODIF | 0x02;
    public static final int CU = MODIF | 0x03;
    
    public final int type;
    
    protected Dependency(int type) {
        this.type = type;
    }
    
}
