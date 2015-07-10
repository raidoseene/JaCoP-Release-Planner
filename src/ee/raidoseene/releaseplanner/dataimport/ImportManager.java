/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataimport;

import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.Features;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Release;
import ee.raidoseene.releaseplanner.datamodel.Releases;
import ee.raidoseene.releaseplanner.datamodel.Resources;
import ee.raidoseene.releaseplanner.datamodel.Stakeholder;
import ee.raidoseene.releaseplanner.datamodel.Stakeholders;
import ee.raidoseene.releaseplanner.datamodel.Urgency;
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

    private static void importDependencies(Project project, Element root) {
    }
}
