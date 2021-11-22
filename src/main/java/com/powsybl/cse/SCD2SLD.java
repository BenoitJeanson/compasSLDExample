package com.powsybl.cse;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

public final class SCD2SLD {

    private SCD2SLD() {

    }

    private static final String FILENAME = "src/main/resources/TrainingIEC61850.scd";

    public static void main(String[] args) {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        SCDParser scdParser;
        try {

            SAXParser saxParser = factory.newSAXParser();

            scdParser = new SCDParser();
            saxParser.parse(FILENAME, scdParser);

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            return;
        }
        SLDGenerator sldGenerator = new SLDGenerator(scdParser.getSubstation());
        sldGenerator.buildSldGraph();
        sldGenerator.computeAndWriteResults("test");
    }

}
