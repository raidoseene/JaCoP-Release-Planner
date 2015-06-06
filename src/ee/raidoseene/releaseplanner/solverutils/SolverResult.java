/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.solverutils;

/**
 *
 * @author Raido Seene
 */
public class SolverResult {
    private String result;
    private long time;
    
    public SolverResult(String result, long time) {
        this.result = result;
        this.time = time;
    }
    
    public String getResult() {
        return this.result;
    }
    
    public long getTime() {
        return this.time;
    }
}
