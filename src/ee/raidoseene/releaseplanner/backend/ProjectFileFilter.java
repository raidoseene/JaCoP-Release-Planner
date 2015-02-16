/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.backend;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author risto
 */
public class ProjectFileFilter implements FilenameFilter {
    
    public static final String FILE_EXTENSION = ".proj";

    @Override
    public boolean accept(File file, String string) {
        if (string != null) {
            return string.endsWith(FILE_EXTENSION);
        }
        return false;
    }
    
}
