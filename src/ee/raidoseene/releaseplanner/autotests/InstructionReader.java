/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.autotests;

import ee.raidoseene.releaseplanner.backend.Settings;
import ee.raidoseene.releaseplanner.backend.SettingsManager;
import ee.raidoseene.releaseplanner.gui.Messenger;

/**
 *
 * @author Raido Seene
 */
public class InstructionReader {

    static String[] instructions = new String[2];
    static String[] parameters = new String[12];
    static String[] constraints;
    static String ID = null;
    static String temp = null;

    public static String readSettings(AutotestSettings settings, String line) {
        Settings projectSettings = SettingsManager.getCurrentSettings();

        try {
            instructions = line.split("\\|");
            parameters = instructions[0].split(";");
            constraints = instructions[1].split(";");

            /*
            if (parameters[0].startsWith("ID")) {
                ID = parameters[0].replaceAll("ID\\[", "").replaceAll("\\]", "");
                //System.out.println("ID is: " + ID);
            } else {
                Messenger.showError(null, "'ID' was not found!");
            }
            */
            if (parameters[0].startsWith("TL")) {
                int time = Integer.parseInt(parameters[0].replaceAll("TL\\[", "").replaceAll("\\]", ""));
                //System.out.println("Time is: " + time);
                if (time > 0) {
                    projectSettings.setLimitSolverTime(true);
                    projectSettings.setSolverTimeLimit(time);
                } else {
                    projectSettings.setLimitSolverTime(false);
                    projectSettings.setSolverTimeLimit(null);
                }
            } else {
                Messenger.showError(null, "'TL' was not found!");
            }
            if (parameters[1].startsWith("CO")) {
                //System.out.println("Shifting is: " + (Integer.parseInt(parameters[1].replaceAll("CO\\[", "").replaceAll("\\]", "")) != 0));
                projectSettings.setResourceShifting(Integer.parseInt(parameters[1].replaceAll("CO\\[", "").replaceAll("\\]", "")) != 0);
            } else {
                Messenger.showError(null, "'CO' was not found!");
            }
            if (parameters[2].startsWith("NORM[")) {
                //System.out.println("Normalization is: " + (Integer.parseInt(parameters[2].replaceAll("NORM\\[", "").replaceAll("\\]", "")) != 0));
                projectSettings.setNormalizedImportances(Integer.parseInt(parameters[2].replaceAll("NORM\\[", "").replaceAll("\\]", "")) != 0);
            } else {
                Messenger.showError(null, "'NORM' was not found!");
            }
            if (parameters[3].startsWith("SET")) {
                int sets = Integer.parseInt(parameters[3].replaceAll("SET\\[", "").replaceAll("\\]", ""));
                //System.out.println("No. of sets: " + sets);
                if (sets > 0) {
                    settings.setProjectNo(sets);
                } else {
                    settings.setProjectNo(1);
                }
            } else {
                Messenger.showError(null, "'SET' was not found!");
            }
            if (parameters[4].startsWith("REP")) {
                int reps = Integer.parseInt(parameters[4].replaceAll("REP\\[", "").replaceAll("\\]", ""));
                //System.out.println("No. of repetitions while reading: " + reps);
                if (reps > 0) {
                    settings.setRepetitionNo(reps);
                } else {
                    settings.setRepetitionNo(1);
                }
            } else {
                Messenger.showError(null, "'REP' was not found!");
            }
            if (parameters[5].startsWith("CRIT")) {
                int crit = Integer.parseInt(parameters[5].replaceAll("CRIT\\[", "").replaceAll("\\]", ""));
                System.out.println("No. of criteria: " + crit);
                settings.setCriteriaNo(crit);
            } else {
                Messenger.showError(null, "'CRIT' was not found!");
            }
            if (parameters[6].startsWith("F")) {
                String feat = parameters[6].replaceAll("F\\[", "").replaceAll("\\]", "");
                String features[] = feat.split("-");
                if (features.length > 1) {
                    System.out.println("F: " + features[0] + " - " + features[1]);
                    ID = "F" + features[0] + "-" + features[1] + "_";
                    settings.setParameter(AutotestSettings.Parameter.FEATURES, Integer.parseInt(features[0]), Integer.parseInt(features[1]));
                } else {
                    System.out.println("F: " + features[0]);
                    ID = "F" + features[0] + "_";
                    settings.setParameter(AutotestSettings.Parameter.FEATURES, Integer.parseInt(features[0]), null);
                }
            } else {
                Messenger.showError(null, "'F' was not found!");
            }
            if (parameters[7].startsWith("RC")) {
                String rc = parameters[7].replaceAll("RC\\[", "").replaceAll("\\]", "");
                String rcs[] = rc.split("-");
                if (rcs.length > 1) {
                    settings.setParameter(AutotestSettings.Parameter.RESOURCE_CONS, Integer.parseInt(rcs[0]), Integer.parseInt(rcs[1]));
                } else {
                    settings.setParameter(AutotestSettings.Parameter.RESOURCE_CONS, Integer.parseInt(rcs[0]), null);
                }
            } else {
                Messenger.showError(null, "'RC' was not found!");
            }
            if (parameters[8].startsWith("RES")) {
                String res = parameters[8].replaceAll("RES\\[", "").replaceAll("\\]", "");
                String ress[] = res.split("-");
                if (ress.length > 1) {
                    temp = "Res" + ress[0] + "-" + ress[1];
                    settings.setParameter(AutotestSettings.Parameter.RESOURCES, Integer.parseInt(ress[0]), Integer.parseInt(ress[1]));
                } else {
                    temp = "Res" + ress[0];
                    settings.setParameter(AutotestSettings.Parameter.RESOURCES, Integer.parseInt(ress[0]), null);
                }
            } else {
                Messenger.showError(null, "'RES' was not found!");
            }
            if (parameters[9].startsWith("REL")) {
                String rel = parameters[9].replaceAll("REL\\[", "").replaceAll("\\]", "");
                String rels[] = rel.split("-");
                if (rels.length > 1) {
                    ID = ID.concat("Rel" + rels[0] + "-" + rels[1] + "_");
                    ID = ID.concat(temp);
                    settings.setParameter(AutotestSettings.Parameter.RELEASES, Integer.parseInt(rels[0]), Integer.parseInt(rels[1]));
                } else {
                    ID = ID.concat("Rel" + rels[0] + "_");
                    ID = ID.concat(temp);
                    settings.setParameter(AutotestSettings.Parameter.RELEASES, Integer.parseInt(rels[0]), null);
                }
            } else {
                Messenger.showError(null, "'REL' was not found!");
            }
            if (parameters[10].startsWith("STK")) {
                String stk = parameters[10].replaceAll("STK\\[", "").replaceAll("\\]", "");
                String stks[] = stk.split("-");
                if (stks.length > 1) {
                    settings.setParameter(AutotestSettings.Parameter.STAKEHOLDERS, Integer.parseInt(stks[0]), Integer.parseInt(stks[1]));
                } else {
                    settings.setParameter(AutotestSettings.Parameter.STAKEHOLDERS, Integer.parseInt(stks[0]), null);
                }
            } else {
                Messenger.showError(null, "'STK' was not found!");
            }
            if (parameters[11].startsWith("TGH")) {
                String dgh = parameters[11].replaceAll("TGH\\[", "").replaceAll("\\]", "");
                String dghs[] = dgh.split("-");
                if (dghs.length > 1) {
                    settings.setParameter(AutotestSettings.Parameter.TIGHTNESS, Float.valueOf(dghs[0]), Float.valueOf(dghs[1]));
                } else {
                    settings.setParameter(AutotestSettings.Parameter.TIGHTNESS, Float.valueOf(dghs[0]), null);
                }
            } else {
                Messenger.showError(null, "'TGH' was not found!");
            }

            int conLen = constraints.length;
            String con;
            for (int i = 0; i < conLen; i++) {
                if (constraints[i].startsWith("FIX")) {
                    con = constraints[i].replaceAll("FIX\\[", "").replaceAll("\\]", "");
                    String cons[] = con.split("-");
                    if (cons.length > 1) {
                        ID = ID.concat("_FIX(" + cons[0] + "-" + cons[1] + ")");
                        settings.setParameter(AutotestSettings.Parameter.FIXED_DEP, Integer.parseInt(cons[0]), Integer.parseInt(cons[1]));
                    } else {
                        ID = ID.concat("_FIX(" + cons[0] + ")");
                        settings.setParameter(AutotestSettings.Parameter.FIXED_DEP, Integer.parseInt(cons[0]), null);
                    }
                } else if (constraints[i].startsWith("EXC")) {
                    con = constraints[i].replaceAll("EXC\\[", "").replaceAll("\\]", "");
                    String cons[] = con.split("-");
                    if (cons.length > 1) {
                        ID = ID.concat("_EXC(" + cons[0] + "-" + cons[1] + ")");
                        settings.setParameter(AutotestSettings.Parameter.EXCLUDED_DEP, Integer.parseInt(cons[0]), Integer.parseInt(cons[1]));
                    } else {
                        ID = ID.concat("_EXC(" + cons[0] + ")");
                        settings.setParameter(AutotestSettings.Parameter.EXCLUDED_DEP, Integer.parseInt(cons[0]), null);
                    }
                }else if (constraints[i].startsWith("EAR")) {
                    con = constraints[i].replaceAll("EAR\\[", "").replaceAll("\\]", "");
                    String cons[] = con.split("-");
                    if (cons.length > 1) {
                        ID = ID.concat("_EAR(" + cons[0] + "-" + cons[1] + ")");
                        settings.setParameter(AutotestSettings.Parameter.EARLIER_DEP, Integer.parseInt(cons[0]), Integer.parseInt(cons[1]));
                    } else {
                        ID = ID.concat("_EAR(" + cons[0] + ")");
                        settings.setParameter(AutotestSettings.Parameter.EARLIER_DEP, Integer.parseInt(cons[0]), null);
                    }
                } else if(constraints[i].startsWith("LAT")) {
                    con = constraints[i].replaceAll("LAT\\[", "").replaceAll("\\]", "");
                    String cons[] = con.split("-");
                    if (cons.length > 1) {
                        ID = ID.concat("_LAT(" + cons[0] + "-" + cons[1] + ")");
                        settings.setParameter(AutotestSettings.Parameter.LATER_DEP, Integer.parseInt(cons[0]), Integer.parseInt(cons[1]));
                    } else {
                        ID = ID.concat("_LAT(" + cons[0] + ")");
                        settings.setParameter(AutotestSettings.Parameter.LATER_DEP, Integer.parseInt(cons[0]), null);
                    }
                } else if(constraints[i].startsWith("SOF")) {
                    con = constraints[i].replaceAll("SOF\\[", "").replaceAll("\\]", "");
                    String cons[] = con.split("-");
                    if (cons.length > 1) {
                        ID = ID.concat("_SOF(" + cons[0] + "-" + cons[1] + ")");
                        settings.setParameter(AutotestSettings.Parameter.SOFT_PRECEDENCE_DEP, Integer.parseInt(cons[0]), Integer.parseInt(cons[1]));
                    } else {
                        ID = ID.concat("_SOF(" + cons[0] + ")");
                        settings.setParameter(AutotestSettings.Parameter.SOFT_PRECEDENCE_DEP, Integer.parseInt(cons[0]), null);
                    }
                } else if(constraints[i].startsWith("HAR")) {
                    con = constraints[i].replaceAll("HAR\\[", "").replaceAll("\\]", "");
                    String cons[] = con.split("-");
                    if (cons.length > 1) {
                        ID = ID.concat("_HAR(" + cons[0] + "-" + cons[1] + ")");
                        settings.setParameter(AutotestSettings.Parameter.HARD_PRECEDENCE_DEP, Integer.parseInt(cons[0]), Integer.parseInt(cons[1]));
                    } else {
                        ID = ID.concat("_HAR(" + cons[0] + ")");
                        settings.setParameter(AutotestSettings.Parameter.HARD_PRECEDENCE_DEP, Integer.parseInt(cons[0]), null);
                    }
                } else if(constraints[i].startsWith("COU")) {
                    con = constraints[i].replaceAll("COU\\[", "").replaceAll("\\]", "");
                    String cons[] = con.split("-");
                    if (cons.length > 1) {
                        ID = ID.concat("_COU(" + cons[0] + "-" + cons[1] + ")");
                        settings.setParameter(AutotestSettings.Parameter.COUPLING_DEP, Integer.parseInt(cons[0]), Integer.parseInt(cons[1]));
                    } else {
                        ID = ID.concat("_COU(" + cons[0] + ")");
                        settings.setParameter(AutotestSettings.Parameter.COUPLING_DEP, Integer.parseInt(cons[0]), null);
                    }
                } else if(constraints[i].startsWith("SEP")) {
                    con = constraints[i].replaceAll("SEP\\[", "").replaceAll("\\]", "");
                    String cons[] = con.split("-");
                    if (cons.length > 1) {
                        ID = ID.concat("_SEP(" + cons[0] + "-" + cons[1] + ")");
                        settings.setParameter(AutotestSettings.Parameter.SEPARATION_DEP, Integer.parseInt(cons[0]), Integer.parseInt(cons[1]));
                    } else {
                        ID = ID.concat("_SEP(" + cons[0] + ")");
                        settings.setParameter(AutotestSettings.Parameter.SEPARATION_DEP, Integer.parseInt(cons[0]), null);
                    }
                } else if(constraints[i].startsWith("XOR")) {
                    con = constraints[i].replaceAll("XOR\\[", "").replaceAll("\\]", "");
                    String cons[] = con.split("-");
                    if (cons.length > 1) {
                        ID = ID.concat("_XOR(" + cons[0] + "-" + cons[1] + ")");
                        settings.setParameter(AutotestSettings.Parameter.XOR_DEP, Integer.parseInt(cons[0]), Integer.parseInt(cons[1]));
                    } else {
                        ID = ID.concat("_XOR(" + cons[0] + ")");
                        settings.setParameter(AutotestSettings.Parameter.XOR_DEP, Integer.parseInt(cons[0]), null);
                    }
                }
                 else if(constraints[i].startsWith("AND")) {
                    con = constraints[i].replaceAll("AND\\[", "").replaceAll("\\]", "");
                    String cons[] = con.split("-");
                    if (cons.length > 1) {
                        ID = ID.concat("_AND(" + cons[0] + "-" + cons[1] + ")");
                        settings.setParameter(AutotestSettings.Parameter.AND_DEP, Integer.parseInt(cons[0]), Integer.parseInt(cons[1]));
                    } else {
                        ID = ID.concat("_AND(" + cons[0] + ")");
                        settings.setParameter(AutotestSettings.Parameter.AND_DEP, Integer.parseInt(cons[0]), null);
                    }
                }
            }
        } catch (Exception ex) {
            Messenger.showError(ex, "Incorrect instructions!");
            return null;
        }

        return ID;
    }
}
