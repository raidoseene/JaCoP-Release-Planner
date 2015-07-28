/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.backend;

/**
 *
 * @author Raido Seene
 */
public class UnsavedException extends RuntimeException {

    public UnsavedException() {
    }

    public UnsavedException(String message) {
        super(message);
    }

    public UnsavedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsavedException(Throwable cause) {
        super(cause);
    }
    
}
