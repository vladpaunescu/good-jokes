package classification;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLInfo {

	// XML filename
	private String filename;
	
	private org.w3c.dom.Document document;
	
	public XMLInfo(String filename) {
		this.filename = filename;
		getDocument();
	}
	
	private void getDocument() {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			this.document = db.parse(this.filename);
		}
		catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
		catch (SAXException se) {
			se.printStackTrace();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public String getContentForTagName(String tagName) {
		Element docElem = document.getDocumentElement();
		
		NodeList nl = docElem.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			return nl.item(0).getTextContent();
		}
		
		return null;
	}
}
