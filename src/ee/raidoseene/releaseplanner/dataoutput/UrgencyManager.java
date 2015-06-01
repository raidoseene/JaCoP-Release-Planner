/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataoutput;

import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Release;
import ee.raidoseene.releaseplanner.datamodel.Releases;
import ee.raidoseene.releaseplanner.datamodel.Stakeholder;
import ee.raidoseene.releaseplanner.datamodel.Urgency;
import ee.raidoseene.releaseplanner.datamodel.ValueAndUrgency;

/**
 *
 * @author Raido Seene
 */
public class UrgencyManager {

    public static int[][] getUrgencies(Project project, Project ModDep) {
        int featureCount = project.getFeatures().getFeatureCount() + ModDep.getFeatures().getFeatureCount();
        int urgencies[][] = new int[project.getStakeholders().getStakeholderCount() * featureCount][project.getReleases().getReleaseCount() + 1];

        //ValueAndUrgency valueAndUrgency = project.getValueAndUrgency();
        //ValueAndUrgency newValueAndUrgency = ModDep.getValueAndUrgency();

        for (int s = 0; s < project.getStakeholders().getStakeholderCount(); s++) {
            calculateUrgencies(urgencies, project.getReleases(), project.getStakeholders().getStakeholder(s), s, featureCount, project, 0);
            calculateUrgencies(urgencies, project.getReleases(), project.getStakeholders().getStakeholder(s), s, featureCount, ModDep, project.getFeatures().getFeatureCount());
            
            /*
            for (int f = 0; f < project.getFeatures().getFeatureCount(); f++) {
                int urgency = valueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                        project.getFeatures().getFeature(f));
                if (urgency == 0) {
                    for (int r = 0; r < project.getReleases().getReleaseCount() + 1; r++) {
                        urgencies[(s * featureCount) + f][r] = 0;
                    }
                } else {
                    Release release = valueAndUrgency.getUrgencyRelease(project.getStakeholders().getStakeholder(s),
                            project.getFeatures().getFeature(f));
                    int releaseNr = project.getReleases().getReleaseIndex(release);
                    int deadlineCurve = valueAndUrgency.getDeadlineCurve(project.getStakeholders().getStakeholder(s),
                            project.getFeatures().getFeature(f));
                    if ((deadlineCurve & Urgency.DEADLYNE_MASK) == Urgency.EARLIEST) {
                        if ((deadlineCurve & Urgency.CURVE_MASK) == Urgency.HARD) {
                            for (int r = 0; r < releaseNr; r++) {
                                urgencies[(s * featureCount) + f][r] = 0;
                            }
                            for (int r = releaseNr; r < project.getReleases().getReleaseCount() + 1; r++) {
                                urgencies[(s * featureCount) + f][r] = urgency;
                            }
                        } else {
                            int[] tempUrgencies = new int[project.getReleases().getReleaseCount() + 1];
                            int tempUrgency = urgency;
                            if ((project.getReleases().getReleaseCount() + 1) - releaseNr > ((urgency > 4)
                                    ? (int) (Math.round(urgency / 3f)) : ((urgency == 4 || urgency == 3) ? 2 : 1))) {
                                for (int i = (releaseNr) + (urgency == 4 ? 2 : ((int) (Math.round(urgency / 3f)))); i >= releaseNr; i--) {
                                    tempUrgencies[i] = tempUrgency;
                                    tempUrgency = tempUrgency / 2;
                                }
                                for (int i = 0; i < releaseNr; i++) {
                                    tempUrgencies[i] = 0;
                                }
                                if (releaseNr + ((urgency > 4)
                                        ? (int) (Math.round(urgency / 3f)) : ((urgency == 4 || urgency == 3) ? 2 : 1)) < project.getReleases().getReleaseCount() + 1) {
                                    for (int i = (releaseNr) + ((urgency > 4)
                                            ? (int) (Math.round(urgency / 3f)) : ((urgency == 4 || urgency == 3) ? 2 : 1)); i < project.getReleases().getReleaseCount() + 1; i++) {
                                        tempUrgencies[i] = urgency;
                                    }
                                }
                            } else {
                                for (int i = project.getReleases().getReleaseCount(); i >= releaseNr; i--) {
                                    tempUrgencies[i] = tempUrgency;
                                    tempUrgency = tempUrgency / 2;
                                }
                                for (int i = 0; i < releaseNr; i++) {
                                    tempUrgencies[i] = 0;
                                }
                            }
                            System.arraycopy(tempUrgencies, 0, urgencies[(s * featureCount) + f], 0, project.getReleases().getReleaseCount() + 1);
                        }
                    } else if ((deadlineCurve & Urgency.DEADLYNE_MASK) == Urgency.LATEST) {
                        if ((deadlineCurve & Urgency.CURVE_MASK) == Urgency.HARD) {
                            for (int r = 0; r <= releaseNr; r++) {
                                urgencies[(s * featureCount) + f][r] = urgency;
                            }
                            for (int r = releaseNr + 1; r < project.getReleases().getReleaseCount() + 1; r++) {
                                urgencies[(s * featureCount) + f][r] = 0;
                            }
                        } else {
                            int[] tempUrgencies = new int[project.getReleases().getReleaseCount() + 1];
                            int tempUrgency = urgency;
                            if (releaseNr - ((urgency > 4)
                                    ? (int) (Math.round(urgency / 3f)) : (urgency == 4 ? 2 : (urgency == 1 ? 0 : 1))) >= 0) {
                                for (int i = (releaseNr) - ((urgency > 4)
                                        ? (int) (Math.round(urgency / 3f)) : (urgency == 4 ? 2 : (urgency == 1 ? 0 : 1))); i <= releaseNr; i++) {
                                    tempUrgencies[i] = tempUrgency;
                                    tempUrgency = tempUrgency / 2;
                                }
                                for (int i = releaseNr + 1; i < project.getReleases().getReleaseCount() + 1; i++) {
                                    tempUrgencies[i] = 0;
                                }
                                if (releaseNr - ((urgency > 4)
                                        ? (int) (Math.round(urgency / 3f)) : (urgency == 4 ? 2 : (urgency == 1 ? 0 : 1))) >= 0) {
                                    for (int i = 0; i < releaseNr - ((urgency > 4)
                                            ? (int) (Math.round(urgency / 3f)) : (urgency == 4 ? 2 : (urgency == 1 ? 0 : 1))); i++) {
                                        tempUrgencies[i] = urgency;
                                    }
                                }
                            } else {
                                for (int i = 0; i <= releaseNr; i++) {
                                    tempUrgencies[i] = tempUrgency;
                                    tempUrgency = tempUrgency / 2;
                                }
                                for (int i = releaseNr + 1; i < project.getReleases().getReleaseCount() + 1; i++) {
                                    tempUrgencies[i] = 0;
                                }
                            }
                            System.arraycopy(tempUrgencies, 0, urgencies[(s * featureCount) + f], 0, project.getReleases().getReleaseCount() + 1);
                        }
                    } else {
                        if ((deadlineCurve & Urgency.CURVE_MASK) == Urgency.HARD) {
                            for (int r = 0; r < project.getReleases().getReleaseCount() + 1; r++) {
                                if (r == releaseNr) {
                                    urgencies[(s * featureCount) + f][r] = urgency;
                                } else {
                                    urgencies[(s * featureCount) + f][r] = 0;
                                }
                            }
                        } else {
                            int[] tempUrgencies = new int[project.getReleases().getReleaseCount() + 1];
                            tempUrgencies[releaseNr] = urgency;
                            int tempUrgency = urgency;
                            for (int r = releaseNr + 1; r < project.getReleases().getReleaseCount() + 1; r++) {
                                tempUrgency = tempUrgency / 2;
                                tempUrgencies[r] = tempUrgency;
                            }
                            tempUrgency = urgency;
                            for (int r = releaseNr - 1; r >= 0; r--) {
                                tempUrgency = tempUrgency / 2;
                                tempUrgencies[r] = tempUrgency;
                            }
                            System.arraycopy(tempUrgencies, 0, urgencies[(s * featureCount) + f], 0, project.getReleases().getReleaseCount() + 1);
                        }
                    }
                }
            }

            if (ModDep.getValueAndUrgency().getValueAndUrgencyCount() > 0) {
                for (int f = 0; f < ModDep.getFeatures().getFeatureCount(); f++) {
                    int newUrgency = newValueAndUrgency.getUrgency(project.getStakeholders().getStakeholder(s),
                            ModDep.getFeatures().getFeature(f));
                    if (newUrgency == 0) {
                        for (int r = 0; r < project.getReleases().getReleaseCount() + 1; r++) {
                            urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = 0;
                        }
                    } else {
                        Release release = newValueAndUrgency.getUrgencyRelease(project.getStakeholders().getStakeholder(s),
                                ModDep.getFeatures().getFeature(f));
                        int releaseNr = project.getReleases().getReleaseIndex(release);
                        int deadlineCurve = newValueAndUrgency.getDeadlineCurve(project.getStakeholders().getStakeholder(s),
                                ModDep.getFeatures().getFeature(f));
                        if ((deadlineCurve & Urgency.DEADLYNE_MASK) == Urgency.EARLIEST) {
                            if ((deadlineCurve & Urgency.CURVE_MASK) == Urgency.HARD) {
                                for (int r = 0; r < releaseNr; r++) {
                                    urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = 0;
                                }
                                for (int r = releaseNr; r < project.getReleases().getReleaseCount() + 1; r++) {
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
                                    for (int i = 0; i < releaseNr; i++) {
                                        tempUrgencies[i] = 0;
                                    }
                                    if (releaseNr + ((newUrgency > 3)
                                            ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)) < project.getReleases().getReleaseCount() + 1) {
                                        for (int i = (releaseNr) + ((newUrgency > 3)
                                                ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)); i < project.getReleases().getReleaseCount() + 1; i++) {
                                            tempUrgencies[i] = newUrgency;
                                        }
                                    }
                                    for (int r = 0; r < project.getReleases().getReleaseCount() + 1; r++) {
                                        urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = tempUrgencies[r];
                                    }
                                } else {
                                    for (int i = project.getReleases().getReleaseCount(); i >= releaseNr; i--) {
                                        tempUrgencies[i] = tempUrgency;
                                        tempUrgency = tempUrgency / 2;
                                    }
                                    for (int i = 0; i < releaseNr; i++) {
                                        tempUrgencies[i] = 0;
                                    }
                                }
                            }
                        } else if ((deadlineCurve & Urgency.DEADLYNE_MASK) == Urgency.LATEST) {
                            if ((deadlineCurve & Urgency.CURVE_MASK) == Urgency.HARD) {
                                for (int r = 0; r < releaseNr; r++) {
                                    urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = newUrgency;
                                }
                                for (int r = releaseNr; r < project.getReleases().getReleaseCount() + 1; r++) {
                                    urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = 0;
                                }
                            } else {
                                int[] tempUrgencies = new int[project.getReleases().getReleaseCount() + 1];
                                int tempUrgency = newUrgency;
                                if (releaseNr - ((newUrgency > 3)
                                        ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)) > 0) {
                                    for (int i = (releaseNr) - ((newUrgency > 3)
                                            ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)); i < releaseNr; i++) {
                                        tempUrgencies[i] = tempUrgency;
                                        tempUrgency = tempUrgency / 2;
                                    }
                                    for (int i = releaseNr; i < project.getReleases().getReleaseCount() + 1; i++) {
                                        tempUrgencies[i] = 0;
                                    }
                                    if (releaseNr - (int) ((newUrgency > 3)
                                            ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)) > 0) {
                                        for (int i = 0; i < releaseNr - ((newUrgency > 3)
                                                ? (int) (Math.round(newUrgency / 2.5f)) : ((newUrgency == 3) ? 2 : 1)); i++) {
                                            tempUrgencies[i] = newUrgency;
                                        }
                                    }
                                    for (int r = 0; r < project.getReleases().getReleaseCount() + 1; r++) {
                                        urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = tempUrgencies[r];
                                    }
                                } else {
                                    for (int i = project.getReleases().getReleaseCount(); i >= releaseNr; i--) {
                                        tempUrgencies[i] = tempUrgency;
                                        tempUrgency = tempUrgency / 2;
                                    }
                                    for (int i = 0; i < releaseNr; i++) {
                                        tempUrgencies[i] = 0;
                                    }
                                }
                            }
                        } else {
                            if ((deadlineCurve & Urgency.CURVE_MASK) == Urgency.HARD) {
                                for (int r = 0; r < project.getReleases().getReleaseCount() + 1; r++) {
                                    if (r == newUrgency - 1) {
                                        urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = newUrgency;
                                    } else {
                                        urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = 0;
                                    }
                                }
                            } else {
                                int[] tempUrgencies = new int[project.getReleases().getReleaseCount() + 1];
                                tempUrgencies[releaseNr] = newUrgency;
                                int tempUrgency = newUrgency;
                                for (int r = releaseNr; r < project.getReleases().getReleaseCount() + 1; r++) {
                                    tempUrgency = tempUrgency / 2;
                                    tempUrgencies[r] = tempUrgency;
                                }
                                tempUrgency = newUrgency;
                                for (int r = 0; r < releaseNr; r++) {
                                    tempUrgency = tempUrgency / 2;
                                    tempUrgencies[r] = tempUrgency;
                                }
                                for (int r = 0; r < project.getReleases().getReleaseCount() + 1; r++) {
                                    urgencies[(s * featureCount) + project.getFeatures().getFeatureCount() + f][r] = tempUrgencies[r];
                                }
                            }
                        }
                    }
                }
            }
            */
        }
        return urgencies;
    }

    private static void calculateUrgencies(int[][] urgencies, Releases releases, Stakeholder stakeholder, int s, int featureCount, Project proj, int featureStep) {
        int releaseCount = releases.getReleaseCount();
        ValueAndUrgency valueAndUrgency = proj.getValueAndUrgency();

        for (int f = 0; f < proj.getFeatures().getFeatureCount(); f++) {
            int urgency = valueAndUrgency.getUrgency(stakeholder,
                    proj.getFeatures().getFeature(f));
            if (urgency == 0) {
                for (int r = 0; r < releaseCount + 1; r++) {
                    urgencies[(s * featureCount) + featureStep + f][r] = 0;
                }
            } else {
                Release release = valueAndUrgency.getUrgencyRelease(stakeholder,
                        proj.getFeatures().getFeature(f));
                int releaseNr = releases.getReleaseIndex(release);
                int deadlineCurve = valueAndUrgency.getDeadlineCurve(stakeholder,
                        proj.getFeatures().getFeature(f));
                if ((deadlineCurve & Urgency.DEADLINE_MASK) == Urgency.EARLIEST) {
                    if ((deadlineCurve & Urgency.CURVE_MASK) == Urgency.HARD) {
                        for (int r = 0; r < releaseNr; r++) {
                            urgencies[(s * featureCount) + featureStep + f][r] = 0;
                        }
                        for (int r = releaseNr; r < releaseCount + 1; r++) {
                            urgencies[(s * featureCount) + featureStep + f][r] = urgency;
                        }
                    } else {
                        int[] tempUrgencies = new int[releaseCount + 1];
                        int tempUrgency = urgency;
                        if ((releaseCount + 1) - releaseNr > ((urgency > 4)
                                ? (int) (Math.round(urgency / 3f)) : ((urgency == 4 || urgency == 3) ? 2 : 1))) {
                            for (int i = (releaseNr) + (urgency == 4 ? 2 : ((int) (Math.round(urgency / 3f/* 2.5f */)))); i >= releaseNr; i--) {
                                tempUrgencies[i] = tempUrgency;
                                tempUrgency = tempUrgency / 2;
                            }
                            for (int i = 0; i < releaseNr; i++) {
                                tempUrgencies[i] = 0;
                            }
                            if (releaseNr + ((urgency > 4)
                                    ? (int) (Math.round(urgency / 3f)) : ((urgency == 4 || urgency == 3) ? 2 : 1)) < releaseCount + 1) {
                                for (int i = (releaseNr) + ((urgency > 4)
                                        ? (int) (Math.round(urgency / 3f)) : ((urgency == 4 || urgency == 3) ? 2 : 1)); i < releaseCount + 1; i++) {
                                    tempUrgencies[i] = urgency;
                                }
                            }
                        } else {
                            for (int i = releaseCount; i >= releaseNr; i--) {
                                tempUrgencies[i] = tempUrgency;
                                tempUrgency = tempUrgency / 2;
                            }
                            for (int i = 0; i < releaseNr; i++) {
                                tempUrgencies[i] = 0;
                            }
                        }
                        System.arraycopy(tempUrgencies, 0, urgencies[(s * featureCount) + featureStep + f], 0, releaseCount + 1);
                    }
                } else if ((deadlineCurve & Urgency.DEADLINE_MASK) == Urgency.LATEST) {
                    if ((deadlineCurve & Urgency.CURVE_MASK) == Urgency.HARD) {
                        for (int r = 0; r <= releaseNr; r++) {
                            urgencies[(s * featureCount) + featureStep + f][r] = urgency;
                        }
                        for (int r = releaseNr + 1; r < releaseCount + 1; r++) {
                            urgencies[(s * featureCount) + featureStep + f][r] = 0;
                        }
                    } else {
                        int[] tempUrgencies = new int[releaseCount + 1];
                        int tempUrgency = urgency;
                        if (releaseNr - ((urgency > 4)
                                ? (int) (Math.round(urgency / 3f)) : (urgency == 4 ? 2 : (urgency == 1 ? 0 : 1))) >= 0) {
                            for (int i = (releaseNr) - ((urgency > 4)
                                    ? (int) (Math.round(urgency / 3f)) : (urgency == 4 ? 2 : (urgency == 1 ? 0 : 1))); i <= releaseNr; i++) {
                                tempUrgencies[i] = tempUrgency;
                                tempUrgency = tempUrgency / 2;
                            }
                            for (int i = releaseNr + 1; i < releaseCount + 1; i++) {
                                tempUrgencies[i] = 0;
                            }
                            if (releaseNr - ((urgency > 4)
                                    ? (int) (Math.round(urgency / 3f)) : (urgency == 4 ? 2 : (urgency == 1 ? 0 : 1))) >= 0) {
                                for (int i = 0; i < releaseNr - ((urgency > 4)
                                        ? (int) (Math.round(urgency / 3f)) : (urgency == 4 ? 2 : (urgency == 1 ? 0 : 1))); i++) {
                                    tempUrgencies[i] = urgency;
                                }
                            }
                        } else {
                            for (int i = 0; i <= releaseNr; i++) {
                                tempUrgencies[i] = tempUrgency;
                                tempUrgency = tempUrgency / 2;
                            }
                            for (int i = releaseNr + 1; i < releaseCount + 1; i++) {
                                tempUrgencies[i] = 0;
                            }
                        }
                        System.arraycopy(tempUrgencies, 0, urgencies[(s * featureCount) + featureStep + f], 0, releaseCount + 1);
                    }
                } else {
                    if ((deadlineCurve & Urgency.CURVE_MASK) == Urgency.HARD) {
                        for (int r = 0; r < releaseCount + 1; r++) {
                            if (r == releaseNr) {
                                urgencies[(s * featureCount) + featureStep + f][r] = urgency;
                            } else {
                                urgencies[(s * featureCount) + featureStep + f][r] = 0;
                            }
                        }
                    } else {
                        int[] tempUrgencies = new int[releaseCount + 1];
                        tempUrgencies[releaseNr] = urgency;
                        int tempUrgency = urgency;
                        for (int r = releaseNr + 1; r < releaseCount + 1; r++) {
                            tempUrgency = tempUrgency / 2;
                            tempUrgencies[r] = tempUrgency;
                        }
                        tempUrgency = urgency;
                        for (int r = releaseNr - 1; r >= 0; r--) {
                            tempUrgency = tempUrgency / 2;
                            tempUrgencies[r] = tempUrgency;
                        }
                        System.arraycopy(tempUrgencies, 0, urgencies[(s * featureCount) + featureStep + f], 0, releaseCount + 1);
                    }
                }
            }
        }
    }
}
