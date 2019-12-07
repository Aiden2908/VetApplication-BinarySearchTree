package assignment2;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AnimalProcessor {

    protected MinHeap<AnimalPatient> waitList;
    private static final String XSD_PATH = "./src/assignment2/animals.xsd";
    protected static String DEFAULT_XML_PATH = "./src/assignment2/AnimalsInVet.xml";
    private Document XML;

    public AnimalProcessor() {
        waitList = new MinHeap<>();
    }

    public void addAnimal(AnimalPatient animal) {
        waitList.add(animal);
    }

    public void seeLater() {
        AnimalPatient temp = getNextAnimal();
        releaseAnimal();
        temp.setPriority(temp.getPriority() + 2);
        addAnimal(temp);
    }

    public AnimalPatient getNextAnimal() {
        if (animalsLeftToProcess() > 0) {
            return waitList.getMin();
        }
        return null;
    }

    public AnimalPatient releaseAnimal() {
        return waitList.removeMin();
    }

    public int animalsLeftToProcess() {
        return waitList.size();
    }

    public Document getDocument(String xmlPath) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            try {
                doc = builder.parse(xmlPath);
            } catch (SAXException | IOException ex) {
                System.out.println("File not found- path is incorrect!");
                Logger.getLogger(VetGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(VetGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.XML = doc;
        this.DEFAULT_XML_PATH = xmlPath;
        return doc;
    }

    public BufferedImage getImage(String imageName) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(imageName));
        } catch (Exception e) {
            System.out.println("Failed to load " + imageName + "[.png/.jpg]" + " (Error " + e + ").");
        }
        return img;
    }

    public void loadAnimalsFromXML(Document doc) {
        waitList = new MinHeap<>();
        AnimalPatient animalPatient;
        NodeList patients = doc.getElementsByTagName("animal");
        for (int i = 0; i < patients.getLength(); i++) {
            Element currentPatient = (Element) patients.item(i);
            animalPatient = new AnimalPatient(currentPatient.getAttribute("species"), currentPatient.getAttribute("name"));
            animalPatient.setPriority(Integer.parseInt(currentPatient.getAttribute("priority")));

            Element elemSymptoms = (Element) currentPatient.getElementsByTagName("symptoms").item(0);
            if (elemSymptoms != null) {
                animalPatient.setSymptoms(elemSymptoms.getTextContent());
            }

            Element elemTreatment = (Element) currentPatient.getElementsByTagName("treatment").item(0);
            if (elemTreatment != null) {
                animalPatient.setTreatment(elemTreatment.getTextContent());

            }
            Element elemPic = (Element) currentPatient.getElementsByTagName("picURL").item(0);
            if (elemPic != null) {
                if (elemPic.getTextContent() != null) {
                    try {
                        animalPatient.setImage(new ImageIcon(getImage(elemPic.getTextContent())));
                    } catch (Exception e) {
                        System.out.println("" + e);
                    }
                }
            }
            Element elemDateSeen = (Element) currentPatient.getElementsByTagName("dateSeen").item(0);
            if (elemDateSeen != null) {
                // animalPatient.updateDate(new Date(elemDateSeen.getTextContent()));
            }
            waitList.add(animalPatient);
        }

    }

    public void saveXML(String symptoms, String treatment, String picPath, String dateSeen, String priority) {
        Element currentPatient = getCurrentPatientElement();
        Element elemDateSeen = null;
        if (currentPatient != null) {
            System.out.println("Patient " + currentPatient.getAttribute("name") + " to be updated.");
            currentPatient.setAttribute("priority", priority);
            if (priority != null) {
                getNextAnimal().setPriority(Integer.parseInt(priority));
            }
            

            elemDateSeen = (Element) currentPatient.getElementsByTagName("dateSeen").item(0);
            if (elemDateSeen == null) {
                elemDateSeen = XML.createElement("dateSeen");
                currentPatient.appendChild(elemDateSeen);
            }
            elemDateSeen.setTextContent(dateSeen);
            getNextAnimal().updateDate(new Date());

            replaceXML();
            if (symptoms != null) {
                Element elemSymptoms = (Element) currentPatient.getElementsByTagName("symptoms").item(0);
                if (elemSymptoms == null) {
                    elemSymptoms = XML.createElement("symptoms");
                    currentPatient.insertBefore(elemSymptoms, elemDateSeen);
                }
                elemSymptoms.setTextContent(symptoms);
                getNextAnimal().setSymptoms(symptoms);
            }

            if (treatment != null) {
                Element elemTreatment = (Element) currentPatient.getElementsByTagName("treatment").item(0);
                if (elemTreatment == null) {
                    elemTreatment = XML.createElement("treatment");
                    currentPatient.insertBefore(elemTreatment, elemDateSeen);
                }
                elemTreatment.setTextContent(treatment);
                getNextAnimal().setTreatment(treatment);
            }

            if (picPath != null) {
                Element elemPicPath = (Element) currentPatient.getElementsByTagName("picURL").item(0);
                if (elemPicPath == null) {
                    elemPicPath = XML.createElement("picURL");
                    currentPatient.insertBefore(elemPicPath, elemDateSeen);
                }
                elemPicPath.setTextContent(picPath);
                getNextAnimal().setImage(new ImageIcon(getImage(picPath)));
            }

        }
    }

    public void deletePatient() {
        Element currentPatient = getCurrentPatientElement();
        if (currentPatient != null) {
            System.out.println("Patient " + currentPatient.getAttribute("name") + " to be removed.");
            currentPatient.getParentNode().removeChild(currentPatient);
            replaceXML();
        }
    }

    public void addPatient(String name, String species, String priority, String symptoms, String picPath) {
        Element animals = (Element) XML.getElementsByTagName("animals").item(0);
        Element animal = XML.createElement("animal");
        animal.setAttribute("name", name);
        animal.setAttribute("priority", priority);
        animal.setAttribute("species", species);

        Element elemSymptoms = XML.createElement("symptoms");
        Element elemTreatments = XML.createElement("treatment");
        Element elemPicParh = XML.createElement("picURL");
        Element elemDateSeen = XML.createElement("dateSeen");

        elemPicParh.setTextContent(picPath);
        elemSymptoms.setTextContent(symptoms);
        elemTreatments.setTextContent("unknown");
        elemDateSeen.setTextContent(new Date().toString());

        animal.appendChild(elemPicParh);
        animal.appendChild(elemSymptoms);
        animal.appendChild(elemTreatments);
        animal.appendChild(elemDateSeen);

        animals.appendChild(animal);
        replaceXML();
    }

    private Element getCurrentPatientElement() {
        Element currentPatient = null;

        NodeList animals = XML.getElementsByTagName("animal");
        //String priority = ;// Cast it once.
        for (int i = 0; i < animals.getLength(); i++) {
            Element elem = (Element) animals.item(i);
            if (elem.getAttribute("name").equals(getNextAnimal().getName()) && elem.getAttribute("species").equals(getNextAnimal().getSpecies())) {
                System.out.println("Found " + elem.getAttribute("name"));
                currentPatient = elem;
                break;
            }
        }
        return currentPatient;
    }

    private void replaceXML() {
        XML.normalize();

        Transformer tf = null;
        try {
            tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.setOutputProperty(OutputKeys.METHOD, "xml");
            tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(AnimalProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }

        DOMSource domSource = new DOMSource(XML);
        StreamResult sr = new StreamResult(new File(DEFAULT_XML_PATH));
        try {
            tf.transform(domSource, sr);
        } catch (TransformerException ex) {
            Logger.getLogger(AnimalProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean XMLValidator(String xmlPath) {
        File schemaF = new File(XSD_PATH);
        Source xmlFile = new StreamSource(new File(DEFAULT_XML_PATH));
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(schemaF);
            Validator validator = schema.newValidator();
            validator.validate(xmlFile);
            System.out.println(xmlFile.getSystemId() + " is valid");
            return true;
        } catch (SAXException e) {
            System.out.println(xmlFile.getSystemId());
            System.out.println("is NOT valid reason:" + e);
        } catch (IOException e) {
        }
        return false;
    }

    public static void main(String[] args) {
        AnimalProcessor animalProcessor = new AnimalProcessor();
        AnimalPatient jackal = new AnimalPatient("Dog", "Jackal", StringToDate("06-mar-2018"));
        jackal.setPriority(7);
        AnimalPatient Catman = new AnimalPatient("Cat", "Catman", StringToDate("09-mar-2017"));
        Catman.setPriority(3);
        AnimalPatient Ratman = new AnimalPatient("Rat", "Ratman", StringToDate("29-aug-2015"));
        Ratman.setPriority(3);
        AnimalPatient Foxxie = new AnimalPatient("Fox", "Foxxie", StringToDate("09-mar-2018"));
        Foxxie.setPriority(10);

        animalProcessor.waitList.add(jackal);
        animalProcessor.waitList.add(Catman);
        animalProcessor.waitList.add(Ratman);
        animalProcessor.waitList.add(Foxxie);
        animalProcessor.waitList.add(jackal);

        System.out.println("Elements sorted using heap sort");
        while (!animalProcessor.waitList.isEmpty()) {
            System.out.print(animalProcessor);
            System.out.println(" (smallest = " + animalProcessor.waitList.removeMin() + ")");
        }

        if (XMLValidator(DEFAULT_XML_PATH)) {
            System.out.println(" TRUE");
        } else {
            System.out.println("FALSE");
        }
    }

    public static Date StringToDate(String dateInString) {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            date = formatter.parse(dateInString);
        } catch (ParseException e) {
        }
        return date;
    }
}
