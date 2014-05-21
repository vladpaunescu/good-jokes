import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;


public class XMLInfo {
	
	private static String initialTagNames[] = {"rating", "votes", "title", "url", "content", "comments_count", "id", "categories"};
	
	private org.w3c.dom.Document document;
	private org.w3c.dom.Document outputDocument;
	
	private String filename;
	private String outputFilename;
	
	private String content;
	
	private Joke joke;
	
	public int countRacist = 0;
	public int countSexism = 0;
	public int countInsulting = 0;
	
	public XMLInfo(String filename, String insultingWordsFilename, String racistWordsFilename) {
		
		this.filename = filename;
		
		getOutputFilename();
				
		getDocument();	
		getOutputDocument();
		
		getContent();
		this.joke = new Joke(this.content, insultingWordsFilename, racistWordsFilename);
		this.joke.extractFeatures();
		
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

	private void getOutputDocument() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			this.outputDocument = db.newDocument();	
		}
		catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		}
	}
	
	private void getContent() {
		Element docElem = document.getDocumentElement();
		
		NodeList nl = docElem.getElementsByTagName("content");
		if(nl != null && nl.getLength() > 0) {
			this.content = nl.item(0).getTextContent();
		}
	}

	private void getOutputFilename() {
	/*	int index;
		index = this.filename.lastIndexOf(".");
		this.outputFilename = this.filename.substring(0, index);
		this.outputFilename += "_out.xml";*/
	
		int index;
		index = this.filename.lastIndexOf("\\");
		this.outputFilename = this.filename.substring(0, index) + "\\Out" + this.filename.substring(index);		
	}
	
	private Element constrElement(String name, String text) {
		Element element = this.outputDocument.createElement(name);
		element.setAttribute("type", "str");
		Text eText = this.outputDocument.createTextNode(text);
		element.appendChild(eText);
		return element;
	}	
	
	private void createDOMTree() {
		Element rootElement = this.outputDocument.createElement("root");
		this.outputDocument.appendChild(rootElement);
		
		Element docElem = this.document.getDocumentElement();
		
		// copy elements from previous document
		for(int i=0; i<this.initialTagNames.length; i++) {
			NodeList nl = docElem.getElementsByTagName(this.initialTagNames[i]);
			if (nl!=null && nl.getLength()>0) {
				Element newElement = (Element)this.outputDocument.importNode(nl.item(0), true);
				rootElement.appendChild(newElement);
			}
		}
		
		// add new elements
		
		// length
		rootElement.appendChild(constrElement("length", this.joke.getLength()+""));
		
		// POS histo
		rootElement.appendChild(constrElement("POS_histogram", this.joke.getHistoPOS()));
		
		// isQA
		rootElement.appendChild(constrElement("QA", this.joke.getIsQA()));
		
		// nouns with sexual connotations
		rootElement.appendChild(constrElement("nouns_with_sexual_connotations", this.joke.getPOSWithSexualConnotations(Constants.nounCategoryID)));
		// nouns with multiple meanings
		rootElement.appendChild(constrElement("nouns_with_multiple_meanings", this.joke.getPOSWithMultipleMeanings(Constants.nounCategoryID)));
		
		// verbs with sexual connotations 
		rootElement.appendChild(constrElement("verbs_with_sexual_connotations", this.joke.getPOSWithSexualConnotations(Constants.verbCategoryID)));
		// verbs with multiple meanings
		rootElement.appendChild(constrElement("verbs_with_multiple_meanings", this.joke.getPOSWithMultipleMeanings(Constants.verbCategoryID)));
		
		// adjectives with sexual connotations
		rootElement.appendChild(constrElement("adjectives_with_sexual_connotations", this.joke.getPOSWithSexualConnotations(Constants.adverbCategoryID)));
		// adjectives with multiple meanings
		rootElement.appendChild(constrElement("adjectives_with_multiple_meanings", this.joke.getPOSWithMultipleMeanings(Constants.adverbCategoryID)));
		
		// adverb with sexual connotations
		rootElement.appendChild(constrElement("adverbs_with_sexual_connotations", this.joke.getPOSWithSexualConnotations(Constants.adjectiveCategoryID)));
		// adverbs with multiple meanings
		rootElement.appendChild(constrElement("adverbs_with_multiple_meanings", this.joke.getPOSWithMultipleMeanings(Constants.adjectiveCategoryID)));
		
		// histogram for professional communities
		rootElement.appendChild(constrElement("professional_communities_histogram", this.joke.getHistoProfessionalCommunities()));
		
		// percent of racist slur words
		rootElement.appendChild(constrElement("racist_slur", this.joke.getRacistSlur()));
		
		// percent of sexist words
		rootElement.appendChild(constrElement("sexism", this.joke.getSexism()));
		
		// simplified POS histo
		rootElement.appendChild(constrElement("simple_POS_histo", this.joke.getSimplifiedHistoPOS()));
		
		// percent of insulting words
		rootElement.appendChild(constrElement("insulting", this.joke.getInsulting())); 
		
		// percent of quotes
		rootElement.appendChild(constrElement("quote", this.joke.getQuotePercent()));
		
		if (!this.joke.getInsulting().equals("0.0"))
			this.countInsulting++;
		if (!this.joke.getSexism().equals("0.0"))
			this.countSexism++;
		if (!this.joke.getRacistSlur().equals("0.0"))
			this.countRacist++;
	}
	
	

	public void printOutputXMLFile() {
		createDOMTree();
		try {
			OutputFormat format = new OutputFormat(this.outputDocument);
			format.setIndenting(true);
			
			XMLSerializer serializer = new XMLSerializer(new FileOutputStream(new File(this.outputFilename)), format);
			
			serializer.serialize(this.outputDocument);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
