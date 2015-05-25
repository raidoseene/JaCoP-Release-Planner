/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataoutput;

import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Release;
import ee.raidoseene.releaseplanner.datamodel.Urgency;
import ee.raidoseene.releaseplanner.datamodel.ValueAndUrgency;

/**
 *
 * @author Raido Seene
 */
public class UrgencyManager {

    public static int[][] calculateUrgencies(Project project, Project ModDep) {
        int featureCount = project.getStakeholders().getStakeholderCount() * (project.getFeatures().getFeatureCount()
                + ModDep.getFeatures().getFeatureCount());
        int urgencies[][] = new int[featureCount][project.getReleases().getReleaseCount()];

        ValueAndUrgency valueAndUrgency = project.getValueAndUrgency();
        for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
            for (int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
                int urgency = valueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                        project.getFeatures().getFeature(f));
                if (urgency == 0) {
                    for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                        urgencies[(s * featureCount) + f][r] = 0;
                    }
                } else {
                    Release release = valueAndUrgency.getUrgencyRelease(project.getStakeholders().getStakeholder(s),
                            project.getFeatures().getFeature(f));
                    int releaseNr = project.getReleases().getReleaseIndex(release);
                    int deadlineCurve = valueAndUrgency.getDeadlineCurve(project.getStakeholders().getStakeholder(s),
                            project.getFeatures().getFeature(f));
                    if (deadlineCurve == (Urgency.DEADLYNE_MASK & Urgency.EARLIEST)) {
                        if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
                            for (int r = 0; r < releaseNr - 2; r++) {
                                urgencies[(s * featureCount) + f][r] = 0;
                            }
                            for (int r = releaseNr - 1; r < project.getReleases().getReleaseCount(); r++) {
                                urgencies[(s * featureCount) + f][r] = urgency;
                            }
                        } else {
                            int[] tempUrgencies = new int[project.getReleases().getReleaseCount() + 1];
                            int tempUrgency = urgency;
                            if ((project.getReleases().getReleaseCount() + 1) - releaseNr >= ((urgency > 3)
                                    ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1))) {
                                for (int i = (releaseNr - 1) + (int) Math.round(urgency / 2.5f); i >= releaseNr - 1; i--) {
                                    tempUrgencies[i] = tempUrgency;
                                    tempUrgency = tempUrgency / 2;
                                }
                                for (int i = 0; i < releaseNr - 1; i++) {
                                    tempUrgencies[i] = 0;
                                }
                                if (releaseNr + ((urgency > 3)
                                        ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)) < project.getReleases().getReleaseCount() + 1) {
                                    for (int i = (releaseNr) + ((urgency > 3)
                                            ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)); i <= project.getReleases().getReleaseCount(); i++) {
                                        tempUrgencies[i] = urgency;
                                    }
                                }
                                for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
                                    urgencies[(s * featureCount) + f][r] = tempUrgencies[r];
                                }
                            } else {
                                for (int i = project.getReleases().getReleaseCount(); i >= releaseNr - 1; i--) {
                                    tempUrgencies[i] = tempUrgency;
                                    tempUrgency = tempUrgency / 2;
                                }
                                for (int i = 0; i < releaseNr - 1; i++) {
                                    tempUrgencies[i] = 0;
                                }
                            }
                        }
                    } else if (deadlineCurve == (Urgency.DEADLYNE_MASK & Urgency.LATEST)) {
                        if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
                            for (int r = 0; r < releaseNr - 1; r++) {
                                urgencies[(s * featureCount) + f][r] = urgency;
                            }
                            for (int r = releaseNr; r < project.getReleases().getReleaseCount(); r++) {
                                urgencies[(s * featureCount) + f][r] = 0;
                            }
                        } else {
                            int[] tempUrgencies = new int[project.getReleases().getReleaseCount() + 1];
                            int tempUrgency = urgency;
                            if (releaseNr - ((urgency > 3)
                                    ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)) > 0) {
                                for (int i = (releaseNr - 1) - ((urgency > 3)
                                        ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)); i < releaseNr; i++) {
                                    tempUrgencies[i] = tempUrgency;
                                    tempUrgency = tempUrgency / 2;
                                }
                                for (int i = releaseNr; i < project.getReleases().getReleaseCount(); i++) {
                                    tempUrgencies[i] = 0;
                                }
                                if (releaseNr - (int) ((urgency > 3)
                                        ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)) > 0) {
                                    for (int i = 0; i < (releaseNr - 1) - ((urgency > 3)
                                            ? (int) (Math.round(urgency / 2.5f)) : ((urgency == 3) ? 2 : 1)); i++) {
                                        tempUrgencies[i] = urgency;
                                    }
                                }
                                for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
                                    urgencies[(s * featureCount) + f][r] = tempUrgencies[r];
                                }
                            } else {
                                for (int i = project.getReleases().getReleaseCount(); i >= releaseNr - 1; i--) {
                                    tempUrgencies[i] = tempUrgency;
                                    tempUrgency = tempUrgency / 2;
                                }
                                for (int i = 0; i < releaseNr - 1; i++) {
                                    tempUrgencies[i] = 0;
                                }
                            }
                        }
                    } else {
                        if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
                            for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                                if (r == urgency - 1) {
                                    urgencies[(s * featureCount) + f][r] = urgency;
                                } else {
                                    urgencies[(s * featureCount) + f][r] = 0;
                                }
                            }
                        } else {
                            int[] tempUrgencies = new int[project.getReleases().getReleaseCount() + 1];
                            tempUrgencies[releaseNr - 1] = urgency;
                            int tempUrgency = urgency;
                            for (int r = releaseNr; r <= project.getReleases().getReleaseCount(); r++) {
                                tempUrgency = tempUrgency / 2;
                                tempUrgencies[r] = tempUrgency;
                            }
                            tempUrgency = urgency;
                            for (int r = 0; r < releaseNr - 1; r++) {
                                tempUrgency = tempUrgency / 2;
                                tempUrgencies[r] = tempUrgency;
                            }
                            for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
                                urgencies[(s * featureCount) + f][r] = tempUrgencies[r];
                            }
                        }
                    }
                }
                /*printWriter.print(valueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                        project.getFeatures().getFeature(f)) + ", ");*/
            }

            if (ModDep.getValueAndUrgency().getValueAndUrgencyCount() > 0) {
                ValueAndUrgency newValueAndUrgency = ModDep.getValueAndUrgency();
                for (int f = 0; f < ModDep.getFeatures().getFeatureCount(); f++) {
                    int newUrgency = newValueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                            ModDep.getFeatures().getFeature(f));
                    if (newUrgency == 0) {
                        for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                            urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = 0;
                        }
                    } else {
                        Release release = newValueAndUrgency.getUrgencyRelease(project.getStakeholders().getStakeholder(s),
                                ModDep.getFeatures().getFeature(f));
                        int releaseNr = project.getReleases().getReleaseIndex(release);
                        int deadlineCurve = newValueAndUrgency.getDeadlineCurve(project.getStakeholders().getStakeholder(s),
                                ModDep.getFeatures().getFeature(f));
                        if (deadlineCurve == (Urgency.DEADLYNE_MASK & Urgency.EARLIEST)) {
                            if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
                                for (int r = 0; r < releaseNr - 2; r++) {
                                    urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = 0;
                                }
                                for (int r = releaseNr - 1; r < project.getReleases().getReleaseCount(); r++) {
                                    urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = newUrgency;
                                }
                            } else {
                                int[] tempUrgencies = new int[project.getReleases().getReleaseCount() + 1];
                                int tempUrgency = newUrgency;
                                if ((project.getReleases().getReleaseCount() + 1) - releaseNr >= ((newUrgency > 3)
                                        ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1))) {
                                    for (int i = (releaseNr - 1) + (int) Math.round(newUrgency / 2.5f); i >= releaseNr - 1; i--) {
                                        tempUrgencies[i] = tempUrgency;
                                        tempUrgency = tempUrgency / 2;
                                    }
                                    for (int i = 0; i < releaseNr - 1; i++) {
                                        tempUrgencies[i] = 0;
                                    }
                                    if (releaseNr + ((newUrgency > 3)
                                            ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)) < project.getReleases().getReleaseCount() + 1) {
                                        for (int i = (releaseNr) + ((newUrgency > 3)
                                                ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)); i <= project.getReleases().getReleaseCount(); i++) {
                                            tempUrgencies[i] = newUrgency;
                                        }
                                    }
                                    for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
                                        urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = tempUrgencies[r];
                                    }
                                } else {
                                    for (int i = project.getReleases().getReleaseCount(); i >= releaseNr - 1; i--) {
                                        tempUrgencies[i] = tempUrgency;
                                        tempUrgency = tempUrgency / 2;
                                    }
                                    for (int i = 0; i < releaseNr - 1; i++) {
                                        tempUrgencies[i] = 0;
                                    }
                                }
                            }
                        } else if (deadlineCurve == (Urgency.DEADLYNE_MASK & Urgency.LATEST)) {
                            if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
                                for (int r = 0; r < releaseNr - 1; r++) {
                                    urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = newUrgency;
                                }
                                for (int r = releaseNr; r < project.getReleases().getReleaseCount(); r++) {
                                    urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = 0;
                                }
                            } else {
                                int[] tempUrgencies = new int[project.getReleases().getReleaseCount() + 1];
                                int tempUrgency = newUrgency;
                                if (releaseNr - ((newUrgency > 3)
                                        ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)) > 0) {
                                    for (int i = (releaseNr - 1) - ((newUrgency > 3)
                                            ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)); i < releaseNr; i++) {
                                        tempUrgencies[i] = tempUrgency;
                                        tempUrgency = tempUrgency / 2;
                                    }
                                    for (int i = releaseNr; i < project.getReleases().getReleaseCount(); i++) {
                                        tempUrgencies[i] = 0;
                                    }
                                    if (releaseNr - (int) ((newUrgency > 3)
                                            ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)) > 0) {
                                        for (int i = 0; i < (releaseNr - 1) - ((newUrgency > 3)
                                                ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)); i++) {
                                            tempUrgencies[i] = newUrgency;
                                        }
                                    }
                                    for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
                                        urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = tempUrgencies[r];
                                    }
                                } else {
                                    for (int i = project.getReleases().getReleaseCount(); i >= releaseNr - 1; i--) {
                                        tempUrgencies[i] = tempUrgency;
                                        tempUrgency = tempUrgency / 2;
                                    }
                                    for (int i = 0; i < releaseNr - 1; i++) {
                                        tempUrgencies[i] = 0;
                                    }
                                }
                            }
                        } else {
                            if (deadlineCurve == (Urgency.CURVE_MASK & Urgency.HARD)) {
                                for (int r = 0; r < project.getReleases().getReleaseCount(); r++) {
                                    if (r == newUrgency - 1) {
                                        urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = newUrgency;
                                    } else {
                                        urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = 0;
                                    }
                                }
                            } else {
                                int[] tempUrgencies = new int[project.getReleases().getReleaseCount() + 1];
                                tempUrgencies[releaseNr - 1] = newUrgency;
                                int tempUrgency = newUrgency;
                                for (int r = releaseNr; r <= project.getReleases().getReleaseCount(); r++) {
                                    tempUrgency = tempUrgency / 2;
                                    tempUrgencies[r] = tempUrgency;
                                }
                                tempUrgency = newUrgency;
                                for (int r = 0; r < releaseNr - 1; r++) {
                                    tempUrgency = tempUrgency / 2;
                                    tempUrgencies[r] = tempUrgency;
                                }
                                for (int r = 0; r <= project.getReleases().getReleaseCount(); r++) {
                                    urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = tempUrgencies[r];
                                }
                            }
                        }
                    }
                    /*printWriter.print(valueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                            project.getFeatures().getFeature(f)) + ", ");*/
                }
            }
        }
        return urgencies;
    }
}
