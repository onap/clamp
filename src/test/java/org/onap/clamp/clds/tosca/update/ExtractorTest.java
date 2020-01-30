package test.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;

import junit.framework.TestCase;
import main.java.Extractor;

public class ExtractorTest extends TestCase {
	
	public void testGetComponents() throws FileNotFoundException, IOException{
		
		String file = "base.yaml";
		//TemplateManagment templateManagment = new TemplateManagment(file);
		Extractor extractor = new Extractor(file);
		assertEquals(7,extractor.getAllItems().size());
		
	}
	
	public void testGetMaps() throws FileNotFoundException, IOException{
		
		String file = "base.yaml";
		Extractor extractor = new Extractor(file);
		assertEquals(7, extractor.getAllAsMaps().size());
		
	}

	public void testGetAllParsedComponent() throws FileNotFoundException, IOException{
	
		String file = "base.yaml";
		//TemplateManagment templateManagment = new TemplateManagment(file);
		Extractor extractor = new Extractor(file);
		LinkedHashMap<String, Object> toParse = extractor.getAllAsMaps();
		extractor.parseInComponent(toParse);
		assertEquals(7,extractor.getAllItems().size());
	
	}
	
}
