/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 *
 * @author Raido Seene
 */
public class SettingsManager {

    private final static String SETTINGS_FILE = "settings";
    private final static Settings currentSettings = SettingsManager.initializeSettings();

    private static Settings initializeSettings() {
        Settings settings;

        try {
            File file = new File(ResourceManager.getDirectory(), SettingsManager.SETTINGS_FILE);

            try (InputStream in = new FileInputStream(file)) {
                try (ObjectInputStream oin = new ObjectInputStream(in)) {
                    settings = (Settings) oin.readObject();

                    try {
                        settings.setMiniZincPath(settings.getMiniZincPath());
                    } catch (Throwable t) {
                        settings.setMiniZincPath(null);
                    }
                    
                    try {
                        settings.setJaCoPPath(settings.getJaCoPPath());
                    } catch (Throwable t) {
                        settings.setJaCoPPath(null);
                    }
                }
            }
        } catch (Throwable t) {
            settings = new Settings();
        }

        return settings;
    }
    
    public static void saveCurrentSettings() throws Exception {
        File file = new File(ResourceManager.getDirectory(), SettingsManager.SETTINGS_FILE);
        
        try (OutputStream out = new FileOutputStream(file)) {
            try (ObjectOutputStream oout = new ObjectOutputStream(out)) {
                oout.writeObject(SettingsManager.currentSettings);
            }
        }
    }

    public static Settings getCurrentSettings() {
        return SettingsManager.currentSettings;
    }

}
