/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.raidoseene.releaseplanner.dataimport;

import ee.raidoseene.releaseplanner.datamodel.Criteria;
import ee.raidoseene.releaseplanner.datamodel.Criterium;
import ee.raidoseene.releaseplanner.datamodel.Dependencies;
import ee.raidoseene.releaseplanner.datamodel.Dependency;
import ee.raidoseene.releaseplanner.datamodel.ExistanceDependency;
import ee.raidoseene.releaseplanner.datamodel.Feature;
import ee.raidoseene.releaseplanner.datamodel.Features;
import ee.raidoseene.releaseplanner.datamodel.Group;
import ee.raidoseene.releaseplanner.datamodel.GroupDependency;
import ee.raidoseene.releaseplanner.datamodel.Groups;
import ee.raidoseene.releaseplanner.datamodel.ModifyingParameterDependency;
import ee.raidoseene.releaseplanner.datamodel.OrderDependency;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
import ee.raidoseene.releaseplanner.dataoutput.DependencyManager;
import javax.xml.transform.TransformerFactory;
import org.w3c.dom.Node;

/**
 *
 * @author Raido Seene
 */
public class ExportManager {

    public static void dependencies(Project project, String dir) throws Exception {
        ValueAndUrgency valueAndUrgency = project.getValueAndUrgency();
        Stakeholders stakeholders = project.getStakeholders();
        Dependencies dependencies = project.getDependencies();
        Resources resources = project.getResources();
        Releases releases = project.getReleases();
        Features features = project.getFeatures();
        Criteria criteria = project.getCriteria();
        Groups groups = project.getGroups();
        
        DependencyManager dm = new DependencyManager(project);
        
        int groupCount;
        int featCount;
        int critCount;
        int resCount;
        int relCount;
        int stkCount;
        
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            
            // root element
            Element rootElement = doc.createElement("project");
            rootElement.setAttribute("name", project.getName());
            doc.appendChild(rootElement);

            // resources element
            Element resourcesElement = doc.createElement("resources");
            rootElement.appendChild(resourcesElement);
            
            resCount = resources.getResourceCount();
            
            for(int r = 0; r < resCount; r++) {
                Element resourceElement = doc.createElement("resource");
                resourceElement.setAttribute("name", resources.getResource(r).getName());
                resourcesElement.appendChild(resourceElement);
            }
            
            // releases element
            Element releasesElement = doc.createElement("releases");
            rootElement.appendChild(releasesElement);
            
            relCount = releases.getReleaseCount();
            
            for(int r = 0; r < relCount; r++) {
                Release rel = releases.getRelease(r);
                
                Element releaseElement = doc.createElement("release");
                releaseElement.setAttribute("name", rel.getName());
                releaseElement.setAttribute("importance", String.valueOf(rel.getImportance()));
                if(rel.getType() == Release.Type.RELEASE) {
                    releaseElement.setAttribute("type", "release");
                    
                    for(int res = 0; res < resCount; res++) {
                        if(rel.hasCapacity(resources.getResource(res))) {
                            Element capacityElement = doc.createElement("capacity");
                            capacityElement.setAttribute("resource", String.valueOf(res + 1));
                            capacityElement.setTextContent(String.valueOf(rel.getCapacity(resources.getResource(res))));
                            releaseElement.appendChild(capacityElement);
                        }
                    }
                } else {
                    releaseElement.setAttribute("type", "postponed");
                }
                releasesElement.appendChild(releaseElement);
            }
            
            // features element
            Element featuresElement = doc.createElement("features");
            rootElement.appendChild(featuresElement);
            
            featCount = features.getFeatureCount();
            
            for(int f = 0; f < featCount; f++) {
                Feature feat = features.getFeature(f);
                
                Element featureElement = doc.createElement("feature");
                featureElement.setAttribute("name", feat.getName());
                for(int res = 0; res < resCount; res++) {
                    if(feat.hasConsumption(resources.getResource(res))) {
                        Element consumptionElement = doc.createElement("consumption");
                        consumptionElement.setAttribute("resource", String.valueOf(res + 1));
                        consumptionElement.setTextContent(String.valueOf(feat.getConsumption(resources.getResource(res))));
                        featureElement.appendChild(consumptionElement);
                    }
                }
                featuresElement.appendChild(featureElement);
            }
            
            // stakeholders element
            Element stakeholdersElement = doc.createElement("stakeholders");
            rootElement.appendChild(stakeholdersElement);
            
            stkCount = stakeholders.getStakeholderCount();
            
            for(int s = 0; s < stkCount; s++) {
                Stakeholder stk = stakeholders.getStakeholder(s);
                
                Element stakeholderElement = doc.createElement("stakeholder");
                stakeholderElement.setAttribute("name", stk.getName());
                stakeholderElement.setAttribute("importance", String.valueOf(stk.getImportance()));
                stakeholdersElement.appendChild(stakeholderElement);
            }
            
            // criteria element
            Element criteriaElement = doc.createElement("criteria");
            rootElement.appendChild(criteriaElement);
            
            critCount = criteria.getCriteriumCount();
            
            for(int c = 0; c < critCount; c++) {
                Criterium crit = criteria.getCriterium(c);
                
                Element criteriumElement = doc.createElement("criterium");
                criteriumElement.setAttribute("name", crit.getName());
                criteriumElement.setAttribute("importance", String.valueOf(crit.getWeight()));
                criteriaElement.appendChild(criteriumElement);
            }
            
            // priorities element
            Element prioritiesElement = doc.createElement("priorities");
            rootElement.appendChild(prioritiesElement);
            
            for(int s = 0; s < stkCount; s++) {
                Element stakeholderElement = doc.createElement("stakeholder");
                stakeholderElement.setAttribute("id", String.valueOf(s + 1));
                prioritiesElement.appendChild(stakeholderElement);
                
                for(int f = 0; f < featCount; f++) {
                    Element featureElement = doc.createElement("feature");
                    featureElement.setAttribute("id", String.valueOf(f + 1));
                    stakeholderElement.appendChild(featureElement);
                    
                    for(int c = 0; c < critCount; c++) {
                        String name = String.valueOf(criteria.getCriterium(c).getName());
                        Element criteriumElement = doc.createElement(name.toLowerCase());
                        switch (name) {
                            case "Value":
                                {
                                    int value = valueAndUrgency.getValue(stakeholders.getStakeholder(s), features.getFeature(f));
                                    if(value > 0) {
                                        criteriumElement.setTextContent(String.valueOf(value));
                                        featureElement.appendChild(criteriumElement);
                                    }
                                    break;
                                }
                            case "Urgency":
                                Urgency urgencyObject = valueAndUrgency.getUrgencyObject(stakeholders.getStakeholder(s), features.getFeature(f));
                                if (urgencyObject != null) {
                                    criteriumElement.setAttribute("release", String.valueOf(releases.getReleaseIndex(urgencyObject.getRelease()) + 1));
                                    //criteriumElement.setAttribute("deadline", String.valueOf(urgencyObject.getDeadlineCurve()));
                                    criteriumElement.setAttribute("deadline", deadlineCalculator(urgencyObject.getDeadlineCurve()));
                                    //criteriumElement.setAttribute("curve", String.valueOf(urgencyObject.getDeadlineCurve()));
                                    criteriumElement.setAttribute("curve", curveCalculator(urgencyObject.getDeadlineCurve()));
                                    criteriumElement.setTextContent(String.valueOf(urgencyObject.getUrgency()));
                                    featureElement.appendChild(criteriumElement);
                                }
                                break;
                            default:
                                {
                                    int value = criteria.getCriteriumValue(criteria.getCriterium(c), stakeholders.getStakeholder(s), features.getFeature(f));
                                    if(value > 0) {
                                        criteriumElement.setTextContent(String.valueOf(value));
                                        featureElement.appendChild(criteriumElement);
                                    }
                                    break;
                                }
                        }
                    }
                }
            }
            
            // groups element
            Element groupsElement = doc.createElement("groups");
            rootElement.appendChild(groupsElement);
            
            groupCount = groups.getGroupCount();
            
            for(int g = 0; g < groupCount; g++) {
                Group group = groups.getGroup(g);
                
                Element groupElement = doc.createElement("group");
                groupElement.setAttribute("name", group.getName());
                groupsElement.appendChild(groupElement);
                
                featCount = group.getFeatureCount();
                for(int f = 0; f < featCount; f++){
                    Element featureElement = doc.createElement("feature");
                    featureElement.setAttribute("id", String.valueOf(features.getFeatureIndex(group.getFeature(f)) + 1));
                    groupElement.appendChild(featureElement);
                }
            }
            
            // dependencies element
            Element dependenciesElement = doc.createElement("dependencies");
            rootElement.appendChild(dependenciesElement);
            
            Element releaseDepsElement = doc.createElement("releasedependencies");
            dependenciesElement.appendChild(releaseDepsElement);
            releaseDepElements(project, doc, dm.getReleaseDS(Dependency.FIXED), Dependency.FIXED);
            releaseDepElements(project, doc, dm.getReleaseDS(Dependency.EXCLUDED), Dependency.EXCLUDED);
            releaseDepElements(project, doc, dm.getReleaseDS(Dependency.EARLIER), Dependency.EARLIER);
            releaseDepElements(project, doc, dm.getReleaseDS(Dependency.LATER), Dependency.LATER);
            
            Element orderDepsElement = doc.createElement("orderdependencies");
            dependenciesElement.appendChild(orderDepsElement);
            orderDepElements(project, doc, dm.getOrderDS(Dependency.SOFTPRECEDENCE), Dependency.SOFTPRECEDENCE);
            orderDepElements(project, doc, dm.getOrderDS(Dependency.HARDPRECEDENCE), Dependency.HARDPRECEDENCE);
            orderDepElements(project, doc, dm.getOrderDS(Dependency.COUPLING), Dependency.COUPLING);
            orderDepElements(project, doc, dm.getOrderDS(Dependency.SEPARATION), Dependency.SEPARATION);
            
            Element existanceDepsElement = doc.createElement("existancedependencies");
            dependenciesElement.appendChild(existanceDepsElement);
            existanceDepElements(project, doc, dm.getExistanceDS(Dependency.AND), Dependency.AND);
            existanceDepElements(project, doc, dm.getExistanceDS(Dependency.XOR), Dependency.XOR);
            
            Element groupDepsElement = doc.createElement("groupdependencies");
            dependenciesElement.appendChild(groupDepsElement);
            groupDepElements(project, doc, dm.getGroupDS(Dependency.ATLEAST), Dependency.ATLEAST);
            groupDepElements(project, doc, dm.getGroupDS(Dependency.EXACTLY), Dependency.EXACTLY);
            groupDepElements(project, doc, dm.getGroupDS(Dependency.ATMOST), Dependency.ATMOST);
            
            // change dependencies
            Element modParamDepsElement = doc.createElement("modparamdependencies");
            dependenciesElement.appendChild(modParamDepsElement);
            
            modParamDepElements(project, doc, dependencies.getTypedDependencies(ModifyingParameterDependency.class, Dependency.CC), Dependency.CC);
            modParamDepElements(project, doc, dependencies.getTypedDependencies(ModifyingParameterDependency.class, Dependency.CV), Dependency.CV);
            modParamDepElements(project, doc, dependencies.getTypedDependencies(ModifyingParameterDependency.class, Dependency.CU), Dependency.CU);
            
            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("C:\\Users\\Raido\\Desktop\\Presentation - export\\file.xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);

            System.out.println("File saved!");

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }
    }
    
    private static String deadlineCalculator(int deadlineCurve) {
        if((deadlineCurve & Urgency.DEADLINE_MASK) == Urgency.EXACT) {
            return "exact";
        } else if((deadlineCurve & Urgency.DEADLINE_MASK) == Urgency.EARLIEST) {
            return "earliest";
        } else if((deadlineCurve & Urgency.DEADLINE_MASK) == Urgency.LATEST) {
            return "latest";
        }
        return "error";
    }
    
    private static String curveCalculator(int deadlineCurve) {
        if((deadlineCurve & Urgency.CURVE_MASK) == Urgency.HARD) {
            return "hard";
        } else if((deadlineCurve & Urgency.CURVE_MASK) == Urgency.SOFT) {
            return "soft";
        }
        return "error";
    }
    
    private static void releaseDepElements(Project project, Document doc, ReleaseDependency[] dep, int type) {
        Features features = project.getFeatures();
        Releases releases = project.getReleases();
        Node element = doc.getDocumentElement().getLastChild().getLastChild();
        String typeName;
        if(type == Dependency.FIXED) {
            typeName = "fixed";
        } else if(type == Dependency.EXCLUDED) {
            typeName = "excluded";
        } else if(type == Dependency.EARLIER) {
            typeName = "earlier";
        } else if(type == Dependency.LATER) {
            typeName = "later";
        } else {
            typeName = "error";
        }
        
        int len = dep.length;
        for(int i = 0; i < len; i++) {
            Element depElement = doc.createElement(typeName);
            depElement.setAttribute("feature", String.valueOf(features.getFeatureIndex(dep[i].getFeature()) + 1));
            depElement.setAttribute("release", String.valueOf(releases.getReleaseIndex(dep[i].getRelease()) + 1));
            element.appendChild(depElement);
        }
    }
    
    private static void orderDepElements(Project project, Document doc, OrderDependency[] dep, int type) {
        Features features = project.getFeatures();
        Node element = doc.getDocumentElement().getLastChild().getLastChild();
        String typeName;
        if(type == Dependency.SOFTPRECEDENCE) {
            typeName = "softprecedence";
        } else if(type == Dependency.HARDPRECEDENCE) {
            typeName = "hardprecedence";
        } else if(type == Dependency.COUPLING) {
            typeName = "coupling";
        } else if(type == Dependency.SEPARATION) {
            typeName = "separation";
        } else {
            typeName = "error";
        }
        
        int len = dep.length;
        for(int i = 0; i < len; i++) {
            Element depElement = doc.createElement(typeName);
            depElement.setAttribute("feature1", String.valueOf(features.getFeatureIndex(dep[i].getPrimary()) + 1));
            depElement.setAttribute("feature2", String.valueOf(features.getFeatureIndex(dep[i].getSecondary()) + 1));
            element.appendChild(depElement);
        }
    }
    
    private static void existanceDepElements(Project project, Document doc, ExistanceDependency[] dep, int type) {
        Features features = project.getFeatures();
        Node element = doc.getDocumentElement().getLastChild().getLastChild();
        String typeName;
        if(type == Dependency.AND) {
            typeName = "and";
        } else if(type == Dependency.XOR) {
            typeName = "xor";
        } else {
            typeName = "error";
        }
        
        int len = dep.length;
        for(int i = 0; i < len; i++) {
            Element depElement = doc.createElement(typeName);
            depElement.setAttribute("feature1", String.valueOf(features.getFeatureIndex(dep[i].getPrimary()) + 1));
            depElement.setAttribute("feature2", String.valueOf(features.getFeatureIndex(dep[i].getSecondary()) + 1));
            element.appendChild(depElement);
        }
    }
    
    private static void groupDepElements(Project project, Document doc, GroupDependency[] dep, int type) {
        
    }
    
    private static void modParamDepElements(Project project, Document doc, ModifyingParameterDependency[] dep, int type) {
        Features features = project.getFeatures();
        Node element = doc.getDocumentElement().getLastChild().getLastChild();
        String typeName;
        if(type == Dependency.CC) {
            typeName = "cc";
        } else if(type == Dependency.CV) {
            typeName = "cv";
        } else if(type == Dependency.CU) {
            typeName = "cu";
        } else {
            typeName = "error";
        }
        
        int len = dep.length;
        for(int i = 0; i < len; i++) {
            Element depElement = doc.createElement(typeName);
            depElement.setAttribute("feature1", String.valueOf(features.getFeatureIndex(dep[i].getPrimary()) + 1));
            depElement.setAttribute("feature2", String.valueOf(features.getFeatureIndex(dep[i].getSecondary()) + 1));
            element.appendChild(depElement);
            
            if(type == Dependency.CC) {
                Feature f = dep[i].getChange(Feature.class);
                Resources resources = project.getResources();
                int resLen = resources.getResourceCount();
                
                for(int r = 0; r < resLen; r++) {
                    Resource res = resources.getResource(r);
                    int cons = f.getConsumption(res);
                    if(cons > 0) {
                        Element consElement = doc.createElement("consumption");
                        consElement.setAttribute("resource", String.valueOf(resources.getResourceIndex(res) + 1));
                        consElement.setTextContent(String.valueOf(cons));
                        depElement.appendChild(consElement);
                    }
                }
            } else if(type == Dependency.CV) {
                Value v = dep[i].getChange(Value.class);
                Stakeholders stakeholders = project.getStakeholders();
                
                Element valueElement = doc.createElement("value");
                valueElement.setAttribute("stakeholder", String.valueOf(stakeholders.getStakeholderIndex(v.getStakeholder()) + 1));
                valueElement.setAttribute("feature", String.valueOf(features.getFeatureIndex(v.getFeature()) + 1));
                valueElement.setTextContent(String.valueOf(v.getValue()));
                depElement.appendChild(valueElement);
            } else if(type == Dependency.CU) {
                Urgency u = dep[i].getChange(Urgency.class);
                Releases releases = project.getReleases();
                Stakeholders stakeholders = project.getStakeholders();
                
                Element urgencyElement = doc.createElement("urgency");
                urgencyElement.setAttribute("stakeholder", String.valueOf(stakeholders.getStakeholderIndex(u.getStakeholder()) + 1));
                urgencyElement.setAttribute("release", String.valueOf(releases.getReleaseIndex(u.getRelease()) + 1));
                urgencyElement.setAttribute("deadline", deadlineCalculator(u.getDeadlineCurve()));
                urgencyElement.setAttribute("curve", curveCalculator(u.getDeadlineCurve()));
                urgencyElement.setTextContent(String.valueOf(u.getUrgency()));
                depElement.appendChild(urgencyElement);
            } else {}
        }
    }
}