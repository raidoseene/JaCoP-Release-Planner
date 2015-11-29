/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataimport;

import ee.raidoseene.releaseplanner.datamodel.Criteria;
import ee.raidoseene.releaseplanner.datamodel.Criterium;
import ee.raidoseene.releaseplanner.datamodel.Dependencies;
import ee.raidoseene.releaseplanner.datamodel.Dependency;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.Features;
import ee.raidoseene.releaseplanner.datamodel.Group;
import ee.raidoseene.releaseplanner.datamodel.Groups;
import ee.raidoseene.releaseplanner.datamodel.Project;
import ee.raidoseene.releaseplanner.datamodel.Release;
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

        Element rootElement = doc.getDocumentElement();
        if (rootElement.getTagName().equals("project")) {
            project.setName(rootElement.getAttribute("name"));

            if (!importResources(project, rootElement.getElementsByTagName("resources"))
                    || !importReleases(project, rootElement.getElementsByTagName("releases"))
                    || !importFeatures(project, rootElement.getElementsByTagName("features"))
                    || !importStakeholders(project, rootElement.getElementsByTagName("stakeholders"))
                    || !importCriteria(project, rootElement.getElementsByTagName("criteria"))
                    || !importPriorities(project, rootElement.getElementsByTagName("priorities"))
                    || !importGroups(project, rootElement.getElementsByTagName("groups"))
                    || !importDependencies(project, rootElement.getElementsByTagName("dependencies"))) {
                throw new Exception("Import halted due to missing elements in imported file!");
            }
        } else {
            throw new Exception("Imported file do not have 'project' element!");
        }

        /*
         NodeList nodes = doc.getElementsByTagName("project");
         if (nodes.getLength() > 0) {
         Node root = nodes.item(0);
         project.setName(root.getAttributes().getNamedItem("name").getNodeValue());

         if (root.getNodeType() == Node.ELEMENT_NODE) {
         importResources(project, (Element) root);
         importReleases(project, (Element) root);
         importFeatures(project, (Element) root);
         importStakeholders(project, (Element) root);
         importPriorities(project, (Element) root);
         importGroups(project, (Element) root);
         importDependencies(project, (Element) root);
         }
         } else {
         throw new Exception("Imported file do not have 'project' element!");
         }
         */
    }

    public static void importFeatures(Project project, File file) throws Exception {
        // ToDo
    }

    private static boolean importResources(Project project, NodeList resourcesNodes) {
        Resources resources = project.getResources();
        Resource resource;

        if (resourcesNodes.getLength() > 0) {
            //NodeList resourceNodes = resourcesNodes.item(0).getChildNodes();
            NodeList resourceNodes = ((Element)resourcesNodes.item(0)).getElementsByTagName("resource");

            int resCount = resourceNodes.getLength();
            for (int r = 0; r < resCount; r++) {
                resource = resources.addResource();
                resource.setName(((Element)resourceNodes.item(r)).getAttribute("name"));
            }
            return true;
        } else {
            return false;
        }

        /*
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
         return false;
         */
    }

    private static boolean importReleases(Project project, NodeList releasesNodes) {
        Resources resources = project.getResources();
        Releases releases = project.getReleases();
        Resource resource;
        Release release;

        Element releaseElement;
        Element capacityElement;
        NodeList capacityNodes;
        int capCount;

        if (releasesNodes.getLength() > 0) {
            //NodeList releaseNodes = releasesNodes.item(0).getChildNodes();
            NodeList releaseNodes = ((Element)releasesNodes.item(0)).getElementsByTagName("release");

            int relCount = releaseNodes.getLength();
            for (int r = 0; r < relCount; r++) {
                releaseElement = (Element) releaseNodes.item(r);

                if (((Element) releaseNodes.item(r)).getAttribute("type").equals("postponed")) {
                    release = releases.getRelease(releases.getReleaseCount() - 1);
                    release.setName(releaseElement.getAttribute("name"));
                    int importance = Integer.parseInt(releaseElement.getAttribute("importance"));
                    if(importance > 0) {
                        release.setImportance(importance);
                    }
                } else {
                    release = releases.addRelease();
                    release.setName(releaseElement.getAttribute("name"));
                    release.setImportance(Integer.parseInt(releaseElement.getAttribute("importance")));

                    //capacityNodes = releaseElement.getChildNodes();
                    capacityNodes = ((Element)releaseElement).getElementsByTagName("capacity");
                    capCount = capacityNodes.getLength();
                    for (int c = 0; c < capCount; c++) {
                        capacityElement = (Element) capacityNodes.item(c);
                        resource = resources.getResource(Integer.parseInt(capacityElement.getAttribute("resource")) - 1);
                        release.setCapacity(resource, Integer.parseInt(capacityElement.getTextContent()));
                    }
                }
            }
            return true;
        } else {
            return false;
        }

        /*
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
         */
    }

    private static boolean importFeatures(Project project, NodeList featuresNodes) {
        Resources resources = project.getResources();
        Features features = project.getFeatures();
        Resource resource;
        Feature feature;

        Element featureElement;
        Element consumptionElement;
        NodeList consumptionNodes;
        int conCount;

        if (featuresNodes.getLength() > 0) {
            //NodeList featureNodes = featuresNodes.item(0).getChildNodes();
            NodeList featureNodes = ((Element)featuresNodes.item(0)).getElementsByTagName("feature");

            int featCount = featureNodes.getLength();
            for (int f = 0; f < featCount; f++) {
                featureElement = (Element) featureNodes.item(f);

                feature = features.addFeature();
                feature.setName(featureElement.getAttribute("name"));

                //consumptionNodes = featureElement.getChildNodes();
                consumptionNodes = ((Element)featureElement).getElementsByTagName("consumption");
                conCount = consumptionNodes.getLength();
                for (int c = 0; c < conCount; c++) {
                    consumptionElement = (Element) consumptionNodes.item(c);
                    resource = resources.getResource(Integer.parseInt(consumptionElement.getAttribute("resource")) - 1);
                    feature.setConsumption(resource, Integer.parseInt(consumptionElement.getTextContent()));
                }
            }
            return true;
        } else {
            return false;
        }

        /*
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
         */
    }

    private static boolean importStakeholders(Project project, NodeList stakeholdersNodes) {
        Stakeholders stakeholders = project.getStakeholders();

        if (stakeholdersNodes.getLength() > 0) {
            //NodeList stakeholderNodes = stakeholdersNodes.item(0).getChildNodes();
            NodeList stakeholderNodes = ((Element)stakeholdersNodes.item(0)).getElementsByTagName("stakeholder");

            int stkCount = stakeholderNodes.getLength();
            for (int s = 0; s < stkCount; s++) {
                Stakeholder stakeholder = stakeholders.addStakeholder();
                stakeholder.setName(((Element) stakeholderNodes.item(s)).getAttribute("name"));
                stakeholder.setImportance(Integer.parseInt(((Element) stakeholderNodes.item(s)).getAttribute("importance")));
            }
            return true;
        } else {
            return false;
        }

        /*
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
         */
    }

    private static boolean importCriteria(Project project, NodeList criteriaNodes) {
        Criteria criteria = project.getCriteria();
        Criterium criterium;

        if (criteriaNodes.getLength() > 0) {
            //NodeList criteriumNodes = criteriaNodes.item(0).getChildNodes();
            NodeList criteriumNodes = ((Element)criteriaNodes.item(0)).getElementsByTagName("criterium");

            int criCount = criteriumNodes.getLength();
            for (int c = 0; c < criCount; c++) {
                Element criteriumElement = (Element) criteriumNodes.item(c);
                switch (criteriumElement.getAttribute("name")) {
                    case "Value":
                        criteria.getCriterium(0).setWeight(Integer.parseInt(criteriumElement.getAttribute("importance")));
                        break;
                    case "Urgency":
                        criteria.getCriterium(1).setWeight(Integer.parseInt(criteriumElement.getAttribute("importance")));
                        break;
                    default:
                        criterium = criteria.addCriterium();
                        System.out.println(criterium.getName() + " " + criterium.getWeight());
                        criterium.setName(criteriumElement.getAttribute("name"));
                        System.out.println(criterium.getName() + " " + criterium.getWeight());
                        criterium.setWeight(Integer.parseInt(criteriumElement.getAttribute("importance")));
                        System.out.println(criterium.getName() + " " + criterium.getWeight());
                        break;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private static boolean importPriorities(Project project, NodeList prioritiesNodes) {
        ValueAndUrgency valueAndUrgency = project.getValueAndUrgency();
        Features features = project.getFeatures();
        Releases releases = project.getReleases();
        Criteria criteria = project.getCriteria();
        Criterium criterium;
        Feature feature;
        Release release;

        Stakeholders stakeholders = project.getStakeholders();
        Stakeholder stakeholder;

        Element criteriumElement;
        int deadlineCurve;
        int featCount;
        int critCount;
        int stkCount;
        int index;
        int val;

        if (prioritiesNodes.getLength() > 0) {
            //NodeList stakeholderNodes = prioritiesNodes.item(0).getChildNodes();
            NodeList stakeholderNodes = ((Element)prioritiesNodes.item(0)).getElementsByTagName("stakeholder");

            stkCount = stakeholderNodes.getLength();
            for (int s = 0; s < stkCount; s++) {
                index = (Integer.parseInt(((Element) (stakeholderNodes.item(s))).getAttribute("id"))) - 1;
                stakeholder = stakeholders.getStakeholder(index);
                //NodeList featureNodes = stakeholderNodes.item(s).getChildNodes();
                NodeList featureNodes = ((Element)stakeholderNodes.item(s)).getElementsByTagName("feature");

                featCount = featureNodes.getLength();
                for (int f = 0; f < featCount; f++) {
                    index = (Integer.parseInt(((Element) (featureNodes.item(f))).getAttribute("id"))) - 1;
                    feature = features.getFeature(index);
                    //NodeList criteriumNodes = featureNodes.item(f).getChildNodes();
                    NodeList criteriumNodes = ((Element)featureNodes.item(f)).getElementsByTagName("*");

                    critCount = criteriumNodes.getLength();
                    for (int c = 0; c < critCount; c++) {
                        criteriumElement = (Element) criteriumNodes.item(c);

                        val = Integer.parseInt(criteriumElement.getTextContent());
                        switch (criteriumElement.getTagName()) {
                            case "value":
                                valueAndUrgency.setValue(stakeholder, feature, val);
                                break;
                            case "urgency":
                                release = releases.getRelease(Integer.parseInt(criteriumElement.getAttribute("release")) - 1);
                                deadlineCurve = deadlineCurveCalculator(criteriumElement.getAttribute("deadline"), criteriumElement.getAttribute("curve"));
                                valueAndUrgency.setRelease(stakeholder, feature, release);
                                valueAndUrgency.setDeadlineCurve(stakeholder, feature, deadlineCurve);
                                valueAndUrgency.setUrgency(stakeholder, feature, val);
                                break;
                            default:
                                criterium = criteria.getCriterium(criteriumElement.getTagName());
                                criterium.setWeight(Integer.parseInt(criteriumElement.getTextContent()));
                                break;
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }

        /*
         NodeList priorities = root.getElementsByTagName("priorities");
         if (priorities.getLength() > 0) {
         priorities = ((Element) priorities.item(0)).getElementsByTagName("stakeholder");
         ValueAndUrgency valAndUrg = project.getValueAndUrgency();
         Criteria criteria = project.getCriteria();
         Stakeholders stakeholders = project.getStakeholders();
         Features features = project.getFeatures();
         Releases releases = project.getReleases();

         int critCount = priorities.getLength();

         for (int i = 0; i < critCount; i++) {
         Element sElement = (Element) priorities.item(i);
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
         */
    }

    private static boolean importGroups(Project project, NodeList groupsNodes) {
        Features features = project.getFeatures();
        Groups groups = project.getGroups();
        Feature feature;
        Group group;

        Element groupElement;
        Element featureElement;
        NodeList featureNodes;
        int featCount;

        if (groupsNodes.getLength() > 0) {
            //NodeList groupNodes = groupsNodes.item(0).getChildNodes();
            NodeList groupNodes = ((Element)groupsNodes.item(0)).getElementsByTagName("group");

            int groCount = groupNodes.getLength();
            for (int g = 0; g < groCount; g++) {
                groupElement = (Element) groupNodes.item(g);

                group = groups.addGroup();
                group.setName(groupElement.getAttribute("name"));

                //featureNodes = groupElement.getChildNodes();
                featureNodes = ((Element)groupElement).getElementsByTagName("feature");
                featCount = featureNodes.getLength();
                for (int f = 0; f < featCount; f++) {
                    featureElement = (Element) featureNodes.item(f);
                    feature = features.getFeature(Integer.parseInt(featureElement.getAttribute("id")) - 1);
                    groups.addFeature(group, feature);
                }
            }
            return true;
        } else {
            return false;
        }
    }

    private static boolean importDependencies(Project project, NodeList dependenciesNodes) {
        ValueAndUrgency valueAndUrgency = project.getValueAndUrgency();
        Dependencies dependencies = project.getDependencies();
        Stakeholders stakeholders = project.getStakeholders();
        Resources resources = project.getResources();
        Releases releases = project.getReleases();
        Features features = project.getFeatures();
        Stakeholder stakeholder;
        Resource resource;
        Release release;
        Feature feature1, feature2;

        Element dependencyElement;
        Element typeElement;
        Element ccElement;
        NodeList dependencyNodes;
        NodeList ccNodes;
        int depCount;
        int newValue;
        int cCount;

        if (dependenciesNodes.getLength() > 0) {
            //NodeList typeNodes = dependenciesNodes.item(0).getChildNodes();
            NodeList typeNodes = ((Element)dependenciesNodes.item(0)).getElementsByTagName("*");

            int typeCount = typeNodes.getLength();
            for (int t = 0; t < typeCount; t++) {
                typeElement = (Element) typeNodes.item(t);
                //dependencyNodes = typeElement.getChildNodes();
                dependencyNodes = ((Element)typeElement).getElementsByTagName("*");
                depCount = dependencyNodes.getLength();

                switch (typeElement.getTagName()) {
                    case "releasedependencies":
                        for (int d = 0; d < depCount; d++) {
                            dependencyElement = (Element) dependencyNodes.item(d);
                            switch (dependencyElement.getTagName()) {
                                case "fixed":
                                    feature1 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature")) - 1);
                                    release = releases.getRelease(Integer.parseInt(dependencyElement.getAttribute("release")) - 1);
                                    dependencies.addReleaseDependency(feature1, release, Dependency.FIXED, true, true);
                                    break;
                                case "excluded":
                                    feature1 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature")) - 1);
                                    release = releases.getRelease(Integer.parseInt(dependencyElement.getAttribute("release")) - 1);
                                    dependencies.addReleaseDependency(feature1, release, Dependency.EXCLUDED, true, true);
                                    break;
                                case "earlier":
                                    feature1 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature")) - 1);
                                    release = releases.getRelease(Integer.parseInt(dependencyElement.getAttribute("release")) - 1);
                                    dependencies.addReleaseDependency(feature1, release, Dependency.EARLIER, true, true);
                                    break;
                                case "later":
                                    feature1 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature")) - 1);
                                    release = releases.getRelease(Integer.parseInt(dependencyElement.getAttribute("release")) - 1);
                                    dependencies.addReleaseDependency(feature1, release, Dependency.LATER, true, true);
                                    break;
                            }
                        }
                        break;
                    case "orderdependencies":
                        for (int d = 0; d < depCount; d++) {
                            dependencyElement = (Element) dependencyNodes.item(d);
                            switch (dependencyElement.getTagName()) {
                                case "softprecedence":
                                    feature1 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature1")) - 1);
                                    feature2 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature2")) - 1);
                                    dependencies.addOrderDependency(feature1, feature2, Dependency.SOFTPRECEDENCE);
                                    break;
                                case "hardprecedence":
                                    feature1 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature1")) - 1);
                                    feature2 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature2")) - 1);
                                    dependencies.addOrderDependency(feature1, feature2, Dependency.HARDPRECEDENCE);
                                    break;
                                case "coupling":
                                    feature1 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature1")) - 1);
                                    feature2 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature2")) - 1);
                                    dependencies.addOrderDependency(feature1, feature2, Dependency.COUPLING);
                                    break;
                                case "separation":
                                    feature1 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature1")) - 1);
                                    feature2 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature2")) - 1);
                                    dependencies.addOrderDependency(feature1, feature2, Dependency.SEPARATION);
                                    break;
                            }
                        }
                        break;
                    case "existancedependencies":
                        for (int d = 0; d < depCount; d++) {
                            dependencyElement = (Element) dependencyNodes.item(d);
                            switch (dependencyElement.getTagName()) {
                                case "and":
                                    feature1 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature1")) - 1);
                                    feature2 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature2")) - 1);
                                    dependencies.addExistanceDependency(feature1, feature2, Dependency.AND);
                                    break;
                                case "xor":
                                    feature1 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature1")) - 1);
                                    feature2 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature2")) - 1);
                                    dependencies.addExistanceDependency(feature1, feature2, Dependency.XOR);
                                    break;
                            }
                        }
                        break;
                    case "groupdependencies":
                        // TO DO
                        break;
                    case "modparamdependencies":
                        for (int d = 0; d < depCount; d++) {
                            dependencyElement = (Element) dependencyNodes.item(d);
                            switch (dependencyElement.getTagName()) {
                                case "cc":
                                    feature1 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature1")) - 1);
                                    feature2 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature2")) - 1);
                                    //ccNodes = dependencyElement.getChildNodes();
                                    ccNodes = ((Element)dependencyElement).getElementsByTagName("consumption");
                                    cCount = ccNodes.getLength();
                                    Feature feat = Features.createStandaloneFeature();
                                    for (int c = 0; c < cCount; c++) {
                                        ccElement = (Element) ccNodes.item(c);
                                        resource = resources.getResource(Integer.parseInt(ccElement.getAttribute("resource")) - 1);
                                        newValue = Integer.parseInt(ccElement.getTextContent());
                                        feat.setConsumption(resource, newValue);
                                    }
                                    dependencies.addModifyingParameterDependency(feature1, feature2, feat);
                                    break;
                                case "cv":
                                    feature1 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature1")) - 1);
                                    feature2 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature2")) - 1);
                                    Element valueElement = (Element)(((Element)dependencyElement).getElementsByTagName("value")).item(0);
                                    stakeholder = stakeholders.getStakeholder(Integer.parseInt(valueElement.getAttribute("stakeholder")) - 1);
                                    Value value = new Value(feature2, stakeholder);
                                    value.setValue(Integer.parseInt(valueElement.getTextContent()));
                                    dependencies.addModifyingParameterDependency(feature1, feature2, value);
                                    break;
                                case "cu":
                                    feature1 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature1")) - 1);
                                    feature2 = features.getFeature(Integer.parseInt(dependencyElement.getAttribute("feature2")) - 1);
                                    Element urgencyElement = (Element)(((Element)dependencyElement).getElementsByTagName("urgency")).item(0);
                                    stakeholder = stakeholders.getStakeholder(Integer.parseInt(urgencyElement.getAttribute("stakeholder")) - 1);
                                    release = releases.getRelease(Integer.parseInt(urgencyElement.getAttribute("release")) - 1);
                                    int deadlineCurve = deadlineCurveCalculator(urgencyElement.getAttribute("deadline"), urgencyElement.getAttribute("curve"));
                                    Urgency urgency = valueAndUrgency.createStandaloneUrgency();
                                    urgency.setStakeholder(stakeholder);
                                    urgency.setRelease(release);
                                    urgency.setDeadlineCurve(deadlineCurve);
                                    urgency.setUrgency(Integer.parseInt(urgencyElement.getTextContent()));
                                    dependencies.addModifyingParameterDependency(feature1, feature2, urgency);
                                    break;
                            }
                        }
                        break;
                }
            }
            return true;
        } else {
            return false;
        }

        /*
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
         */
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
        /*
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
         */
    }

    private static void addReleaseDependency(Dependencies dependencies, Features features, Releases releases, Node n, int type) {
        Feature f = features.getFeature(Integer.parseInt(n.getAttributes().getNamedItem("feature").getNodeValue()) - 1);
        Release r = releases.getRelease(Integer.parseInt(n.getAttributes().getNamedItem("release").getNodeValue()) - 1);
        dependencies.addReleaseDependency(f, r, type, false, true);
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

    private static int deadlineCurveCalculator(String sDeadline, String sCurve) {
        int deadline = 0;
        int curve = 0;
        switch (sDeadline) {
            case "earliest":
                deadline = Urgency.EARLIEST;
                break;
            case "exact":
                deadline = Urgency.EXACT;
                break;
            case "latest":
                deadline = Urgency.LATEST;
                break;
        }
        switch (sCurve) {
            case "hard":
                curve = Urgency.HARD;
                break;
            case "soft":
                curve = Urgency.SOFT;
                break;
        }
        return deadline | curve;
    }
}
