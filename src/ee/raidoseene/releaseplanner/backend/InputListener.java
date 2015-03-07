/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.backend;

/**
 *
 * @author risto
 */
public interface InputListener {
    
    public void lineRead(String line);
    public void errorThrown(Throwable error);
    public void finishedReading();

}
