/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataimport;

import ee.raidoseene.releaseplanner.datamodel.Dependencies;
import ee.raidoseene.releaseplanner.datamodel.Dependency;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.Features;
import ee.raidoseene.releaseplanner.datamodel.OrderDependency;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Release;
import ee.raidoseene.releaseplanner.datamodel.ReleaseDependency;
import ee.raidoseene.releaseplanner.datamodel.Releases;
import ee.raidoseene.releaseplanner.datamodel.Resource;
import ee.raidoseene.releaseplanner.datamodel.Resources;
import ee.raidoseene.releaseplanner.datamodel.Stakeholder;
import ee.raidoseene.releaseplanner.datamodel.Stakeholders;
import ee.raidoseene.releaseplanner.datamodel.Urgency;
import ee.raidoseene.releaseplanner.datamodel.Value;
import ee.raidoseene.releaseplanner.datamodel.ValueAndUrgency;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Raido Seene
 */
public class ImportManager {

    public static void importProject(Project project, File file) throws Exception {
        Document doc;

        try (InputStream in = new FileInputStream(file)) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(in);
        }

        NodeList nodes = doc.getElementsByTagName("project");
        if (nodes.getLength() > 0) {
            Node root = nodes.item(0);
            project.setName(root.getAttributes().getNamedItem("name").getNodeValue());

            if (root.getNodeType() == Node.ELEMENT_NODE) {
                importResources(project, (Element) root);
                importReleases(project, (Element) root);
                importFeatures(project, (Element) root);
                importStakeholders(project, (Element) root);
                importValueAndUrgency(project, (Element) root);
                importGroups(project, (Element) root);
                importDependencies(project, (Element) root);
            }
        } else {
            throw new Exception("Imported file do not have 'project' element!");
        }
    }

    public static void importFeatures(Project project, File file) throws Exception {
        // ToDo
    }

    private static void importResources(Project project, Element root) {
        NodeList nResources = root.getElementsByTagName("resources");
        if (nResources.getLength() > 0) {
            nResources = ((Element) nResources.item(0)).getElementsByTagName("resource");
            Resources resources = project.getResources();
            int rCount = nResources.getLength();

            for (int i = 0; i < rCount; i++) {
                String name = nResources.item(i).getAttributes().getNamedItem("name").getNodeValue();
                resources.addResource().setName(name);
            }
        }
    }

    private static void importReleases(Project project, Element root) {
        NodeList nReleases = root.getElementsByTagName("releases");
        if (nReleases.getLength() > 0) {
            nReleases = ((Element) nReleases.item(0)).getElementsByTagName("release");
            Resources resources = project.getResources();
            Releases releases = project.getReleases();
            int rCount = nReleases.getLength();

            for (int i = 0; i < rCount; i++) {
                Element relElement = (Element) nReleases.item(i);
                String name = relElement.getAttributes().getNamedItem("name").getNodeValue();
                int importance = Integer.parseInt(relElement.getAttributes().getNamedItem("importance").getNodeValue());
                Release.Type type = Release.Type.valueOf(relElement.getAttributes().getNamedItem("type").getNodeValue());
                if (type != Release.Type.POSTPONED) {
                    Release r = releases.addRelease();

                    r.setName(name);
                    r.setImportance(importance);

                    NodeList capacities = relElement.getElementsByTagName("capacity");
                    int resLen = capacities.getLength();
                    for (int j = 0; j < resLen; j++) {
                        Node n = capacities.item(j);
                        int resource = Integer.parseInt(n.getAttributes().getNamedItem("resource").getNodeValue()) - 1;
                        int cap = Integer.parseInt(n.getTextContent());
                        r.setCapacity(resources.getResource(resource), cap);
                    }
                } else {
                    Release r = releases.getRelease(releases.getReleaseCount() - 1);
                    r.setImportance(importance);
                }
            }
        }
    }

    private static void importFeatures(Project project, Element root) {
        NodeList nFeatures = root.getElementsByTagName("features");
        if (nFeatures.getLength() > 0) {
            nFeatures = ((Element) nFeatures.item(0)).getElementsByTagName("feature");
            Resources resources = project.getResources();
            Features features = project.getFeatures();
            int fCount = nFeatures.getLength();

            for (int i = 0; i < fCount; i++) {
                Element fElement = (Element) nFeatures.item(i);
                String name = fElement.getAttributes().getNamedItem("name").getNodeValue();
                Feature f = features.addFeature();

                f.setName(name);

                NodeList consumption = fElement.getElementsByTagName("consumption");
                int resLen = consumption.getLength();
                for (int j = 0; j < resLen; j++) {
                    Node n = consumption.item(j);
                    int resource = Integer.parseInt(n.getAttributes().getNamedItem("resource").getNodeValue()) - 1;
                    int cons = Integer.parseInt(n.getTextContent());
                    f.setConsumption(resources.getResource(resource), cons);
                }
            }
        }
    }

    private static void importStakeholders(Project project, Element root) {
        NodeList nStakeholders = root.getElementsByTagName("stakeholders");
        if (nStakeholders.getLength() > 0) {
            nStakeholders = ((Element) nStakeholders.item(0)).getElementsByTagName("stakeholder");
            Stakeholders stakeholders = project.getStakeholders();
            int sCount = nStakeholders.getLength();

            for (int i = 0; i < sCount; i++) {
                Node n = nStakeholders.item(i);
                String name = n.getAttributes().getNamedItem("name").getNodeValue();
                int importance = Integer.parseInt(n.getAttributes().getNamedItem("importance").getNodeValue());
                Stakeholder s = stakeholders.addStakeholder();
                s.setImportance(importance);
                s.setName(name);
            }
        }
    }

    private static void importValueAndUrgency(Project project, Element root) {
        NodeList valueAndUrgency = root.getElementsByTagName("valueandurgency");
        if (valueAndUrgency.getLength() > 0) {
            valueAndUrgency = ((Element) valueAndUrgency.item(0)).getElementsByTagName("stakeholder");
            ValueAndUrgency valAndUrg = project.getValueAndUrgency();
            Stakeholders stakeholders = project.getStakeholders();
            Features features = project.getFeatures();
            Releases releases = project.getReleases();

            int vuCount = valueAndUrgency.getLength();

            for (int i = 0; i < vuCount; i++) {
                Element sElement = (Element) valueAndUrgency.item(i);
                int stkId = Integer.parseInt(sElement.getAttributes().getNamedItem("id").getNodeValue()) - 1;
                Stakeholder stk = stakeholders.getStakeholder(stkId);

                NodeList feature = sElement.getElementsByTagName("feature");
                int fLen = feature.getLength();

                for (int j = 0; j < fLen; j++) {
                    Element fElement = (Element) feature.item(j);
                    NodeList val = fElement.getElementsByTagName("value");
                    NodeList urg = fElement.getElementsByTagName("urgency");
                    int featId = Integer.parseInt(fElement.getAttributes().getNamedItem("id").getNodeValue()) - 1;
                    int value = Integer.parseInt(val.item(0).getTextContent());
                    int relId = Integer.parseInt(urg.item(0).getAttributes().getNamedItem("release").getNodeValue()) - 1;
                    String deadline = urg.item(0).getAttributes().getNamedItem("deadline").getNodeValue();
                    String curve = urg.item(0).getAttributes().getNamedItem("curve").getNodeValue();
                    int urgency = Integer.parseInt(urg.item(0).getTextContent());
                    Feature feat = features.getFeature(featId);

                    valAndUrg.setValue(stk, feat, value);
                    valAndUrg.setUrgency(stk, feat, urgency);
                    valAndUrg.setRelease(stk, feat, releases.getRelease(relId));

                    int deadlineCurve = 0;
                    switch (deadline) {
                        case "earliest":
                            deadlineCurve = Urgency.EARLIEST;
                            break;
                        case "latest":
                            deadlineCurve = Urgency.LATEST;
                            break;
                        case "exact":
                            deadlineCurve = Urgency.EXACT;
                            break;
                    }
                    switch (curve) {
                        case "hard":
                            deadlineCurve = deadlineCurve | Urgency.HARD;
                            break;
                        case "soft":
                            deadlineCurve = deadlineCurve | Urgency.SOFT;
                            break;
                    }
                    valAndUrg.setDeadlineCurve(stk, feat, deadlineCurve);
                }
            }
        }
    }

    private static void importGroups(Project project, Element root) {
    }

    private static void importDependencies(Project project, Element root) {
        NodeList nDependencies = root.getElementsByTagName("dependencies");
        if (nDependencies.getLength() > 0) {
            nDependencies = ((Element) nDependencies.item(0)).getChildNodes();
            ValueAndUrgency valAndUrg = project.getValueAndUrgency();
            Dependencies dependencies = project.getDependencies();
            Stakeholders stakeholders = project.getStakeholders();
            Resources resources = project.getResources();
            Features features = project.getFeatures();
            Releases releases = project.getReleases();
            int dCount = nDependencies.getLength();

            for (int i = 0; i < dCount; i++) {
                Node n = nDependencies.item(i);

                if (n.getNodeName().equals("fixed")) {
                    addReleaseDependency(dependencies, features, releases, n, Dependency.FIXED);
                } else if (n.getNodeName().equals("excluded")) {
                    addReleaseDependency(dependencies, features, releases, n, Dependency.EXCLUDED);
                } else if (n.getNodeName().equals("earlier")) {
                    addReleaseDependency(dependencies, features, releases, n, Dependency.EARLIER);
                } else if (n.getNodeName().equals("later")) {
                    addReleaseDependency(dependencies, features, releases, n, Dependency.LATER);
                } else if (n.getNodeName().equals("softprecedence")) {
                    addOrderDependency(dependencies, features, n, Dependency.SOFTPRECEDENCE);
                } else if (n.getNodeName().equals("hardprecedence")) {
                    addOrderDependency(dependencies, features, n, Dependency.HARDPRECEDENCE);
                } else if (n.getNodeName().equals("coupling")) {
                    addOrderDependency(dependencies, features, n, Dependency.COUPLING);
                } else if (n.getNodeName().equals("separation")) {
                    addOrderDependency(dependencies, features, n, Dependency.SEPARATION);
                } else if (n.getNodeName().equals("and")) {
                    addExistanceDependency(dependencies, features, n, Dependency.AND);
                } else if (n.getNodeName().equals("xor")) {
                    addExistanceDependency(dependencies, features, n, Dependency.XOR);
                } else if (n.getNodeName().equals("cc")) {
                    Feature f1 = features.getFeature(Integer.parseInt(n.getAttributes().getNamedItem("feature1").getNodeValue()) - 1);
                    Feature f2 = features.getFeature(Integer.parseInt(n.getAttributes().getNamedItem("feature2").getNodeValue()) - 1);
                    NodeList nConsumptions = ((Element) n).getElementsByTagName("consumption");
                    int consLen = nConsumptions.getLength();

                    Feature f = Features.createStandaloneFeature();
                    dependencies.addModifyingParameterDependency(f1, f2, f);

                    for (int c = 0; c < consLen; c++) {
                        Resource r = resources.getResource(Integer.parseInt(nConsumptions.item(c).getAttributes().getNamedItem("resource").getNodeValue()) - 1);
                        int value = Integer.parseInt(nConsumptions.item(c).getTextContent());
                        f.setConsumption(r, value);
                    }
                    /*
                     int stkCount = stakeholders.getStakeholderCount();
                     for(int s = 0; s < stkCount; s++) {
                     Stakeholder stk = stakeholders.getStakeholder(s);
                     ValueAndUrgency.ValUrg valUrg = valAndUrg.getValUrgObject(stk, f);
                     if(valUrg != null) {
                     valAndUrg.setValUrgObject(stk, f, valUrg);
                     }
                     }
                     */
                } else if (n.getNodeName().equals("cv")) {
                    NodeList nStakeholders = ((Element) n).getElementsByTagName("stakeholder");
                    int stkLen = nStakeholders.getLength();

                    for (int s = 0; s < stkLen; s++) {
                        Element stkElement = (Element) nStakeholders.item(s);
                        Stakeholder stk = stakeholders.getStakeholder(Integer.parseInt(stkElement.getAttributes().getNamedItem("id").getNodeValue()) - 1);
                        NodeList nValues = stkElement.getElementsByTagName("value");
                        int valLen = nValues.getLength();

                        for (int v = 0; v < valLen; v++) {
                            Feature f1 = features.getFeature(Integer.parseInt(nValues.item(v).getAttributes().getNamedItem("feature1").getNodeValue()) - 1);
                            Feature f2 = features.getFeature(Integer.parseInt(nValues.item(v).getAttributes().getNamedItem("feature2").getNodeValue()) - 1);
                            int value = Integer.parseInt(nValues.item(v).getTextContent());

                            //Feature f = Features.createStandaloneFeature();

                            //valAndUrg.setValue(stk, f, value);
                            //valAndUrg.setUrgency(stk, f2, valAndUrg.getUrgency(stk, f2));
                            //valAndUrg.setRelease(stk, f2, valAndUrg.getUrgencyRelease(stk, f2));
                            //valAndUrg.setDeadlineCurve(stk, f2, valAndUrg.getDeadlineCurve(stk, f));
                            Value newValue = new Value(f2, stk);
                            newValue.setValue(value);

                            dependencies.addModifyingParameterDependency(f1, f2, newValue);
                        }
                    }
                } else if (n.getNodeName().equals("cu")) {
                    NodeList nStakeholders = ((Element) n).getElementsByTagName("stakeholder");
                    int stkLen = nStakeholders.getLength();

                    for (int s = 0; s < stkLen; s++) {
                        Element stkElement = (Element) nStakeholders.item(s);
                        Stakeholder stk = stakeholders.getStakeholder(Integer.parseInt(stkElement.getAttributes().getNamedItem("id").getNodeValue()) - 1);
                        NodeList nValues = stkElement.getElementsByTagName("urgency");
                        int valLen = nValues.getLength();

                        for (int v = 0; v < valLen; v++) {
                            Feature f1 = features.getFeature(Integer.parseInt(nValues.item(v).getAttributes().getNamedItem("feature1").getNodeValue()) - 1);
                            Feature f2 = features.getFeature(Integer.parseInt(nValues.item(v).getAttributes().getNamedItem("feature2").getNodeValue()) - 1);
                            Release r = releases.getRelease(Integer.parseInt(nValues.item(v).getAttributes().getNamedItem("release").getNodeValue()) - 1);
                            String deadline = nValues.item(v).getAttributes().getNamedItem("deadline").getNodeValue();
                            String curve = nValues.item(v).getAttributes().getNamedItem("curve").getNodeValue();
                            int urgency = Integer.parseInt(nValues.item(v).getTextContent());

                            Urgency newUrgency = valAndUrg.createStandaloneUrgency();
                            newUrgency.setUrgency(urgency);
                            newUrgency.setRelease(r);

                            int deadlineCurve = 0;
                            switch (deadline) {
                                case "earliest":
                                    deadlineCurve = Urgency.EARLIEST;
                                    break;
                                case "latest":
                                    deadlineCurve = Urgency.LATEST;
                                    break;
                                case "exact":
                                    deadlineCurve = Urgency.EXACT;
                                    break;
                            }
                            switch (curve) {
                                case "hard":
                                    deadlineCurve = deadlineCurve | Urgency.HARD;
                                    break;
                                case "soft":
                                    deadlineCurve = deadlineCurve | Urgency.SOFT;
                                    break;
                            }
                            newUrgency.setDeadlineCurve(deadlineCurve);

                            dependencies.addModifyingParameterDependency(f1, f2, newUrgency);
                        }
                    }
                }
            }
        }
    }

    private static void addReleaseDependency(Dependencies dependencies, Features features, Releases releases, Node n, int type) {
        Feature f = features.getFeature(Integer.parseInt(n.getAttributes().getNamedItem("feature").getNodeValue()) - 1);
        Release r = releases.getRelease(Integer.parseInt(n.getAttributes().getNamedItem("release").getNodeValue()) - 1);
        dependencies.addReleaseDependency(f, r, type);
    }

    private static void addOrderDependency(Dependencies dependencies, Features features, Node n, int type) {
        Feature f1 = features.getFeature(Integer.parseInt(n.getAttributes().getNamedItem("feature1").getNodeValue()) - 1);
        Feature f2 = features.getFeature(Integer.parseInt(n.getAttributes().getNamedItem("feature2").getNodeValue()) - 1);
        dependencies.addOrderDependency(f1, f2, type);
    }

    private static void addExistanceDependency(Dependencies dependencies, Features features, Node n, int type) {
        Feature f1 = features.getFeature(Integer.parseInt(n.getAttributes().getNamedItem("feature1").getNodeValue()) - 1);
        Feature f2 = features.getFeature(Integer.parseInt(n.getAttributes().getNamedItem("feature2").getNodeValue()) - 1);
        dependencies.addExistanceDependency(f1, f2, type);
    }
}
