package com.powsybl.cse;

import com.powsybl.cse.model.Bay;
import com.powsybl.cse.model.ConductingEquipment;
import com.powsybl.cse.model.ConnectivityNode;
import com.powsybl.cse.model.Substation;
import com.powsybl.cse.model.Terminal;
import com.powsybl.cse.model.VoltageLevel;

import org.xml.sax.Attributes;
// import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SCDParser extends DefaultHandler {

    private static final String SUBSTATION = "Substation";

    private StringBuilder currentValue = new StringBuilder();
    private boolean inSubstation = false;
    private Substation currentSubstation;
    private VoltageLevel currentVoltageLevel;
    private Bay currentBay;
    private ConductingEquipment currentConductingEquipment;

    Substation getSubstation() {
        return this.currentSubstation;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {

        // reset the tag value
        currentValue.setLength(0);
        if (qName.equals(SUBSTATION)) {
            inSubstation = true;
            currentSubstation = new Substation(attributes.getValue("name"), attributes.getValue("desc"));
        }

        if (!inSubstation) {
            return;
        }

        if (qName.equals("VoltageLevel")) {
            currentVoltageLevel = new VoltageLevel(attributes.getValue("name"), attributes.getValue("desc"), 20,
                    gSyx(attributes, "x"), gSyx(attributes, "y"));
            currentSubstation.addVoltageLevel(currentVoltageLevel);
        }

        if (qName.equals("Bay")) {
            currentBay = new Bay(attributes.getValue("name"), attributes.getValue("desc"), gSyx(attributes, "x"),
                    gSyx(attributes, "y"));
            currentVoltageLevel.addBay(currentBay);
        }

        if (qName.equals("ConnectivityNode")) {
            currentBay.addConnectivityNode(
                    new ConnectivityNode(attributes.getValue("pathName"), attributes.getValue("name")));
        }

        if (qName.equals("ConductingEquipment")) {
            currentConductingEquipment = new ConductingEquipment(attributes.getValue("type"),
                    attributes.getValue("name"), gSyx(attributes, "x"), gSyx(attributes, "y"));
            currentBay.addConductingEquipments(currentConductingEquipment);
        }

        if (qName.equals("Terminal")) {
            currentConductingEquipment.addTerminal(new Terminal(currentVoltageLevel, attributes.getValue("name"),
                    attributes.getValue("connectivityNode"), attributes.getValue("cNodeName")));
        }
    }

    private int gSyx(Attributes attributes, String xy) {
        // TODO : to develop
        return 0;
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals(SUBSTATION)) {
            inSubstation = false;
        }
    }

    @Override
    public void endDocument() {
        currentVoltageLevel.collectConnectivityNodes();
    }

    // http://www.saxproject.org/apidoc/org/xml/sax/ContentHandler.html#characters%28char%5B%5D,%20int,%20int%29
    // SAX parsers may return all contiguous character data in a single chunk,
    // or they may split it into several chunks
    @Override
    public void characters(char[] ch, int start, int length) {

        // The characters() method can be called multiple times for a single text node.
        // Some values may missing if assign to a new string

        // avoid doing this
        // value = new String(ch, start, length);

        // better append it, works for single or multiple calls
        currentValue.append(ch, start, length);

    }

}
