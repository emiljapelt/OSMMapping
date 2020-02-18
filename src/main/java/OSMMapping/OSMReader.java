package OSMMapping;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;

public class OSMReader {

    OSMHandler handler;

    public OSMReader(String mapFileLocation){
        try {
            XMLReader reader = SAXParserFactory
                    .newInstance()
                    .newSAXParser()
                    .getXMLReader();
            reader.setContentHandler(new OSMHandler());
            reader.parse(mapFileLocation);
            handler = ((OSMHandler) reader.getContentHandler());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    public OSMHandler getHandler(){return handler;}
}
