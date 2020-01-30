package test.java;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import junit.framework.TestCase;
import main.java.Extractor;
import main.java.Template;
import main.java.TemplateManagment;

public class TemplateManagmentTest extends TestCase {

	public void testIsEmpty() throws FileNotFoundException, IOException {
		String file = "dontExist.yaml";
		TemplateManagment templateManagment = new TemplateManagment(file);
		assertNull(templateManagment.getComponents());
		file = "base.xml";
		templateManagment = new TemplateManagment(file);
		assertNull(templateManagment.getComponents());	
	}
	
	public void testGetAllAttributes() throws FileNotFoundException, IOException{
		String file = "base.yaml";
		TemplateManagment templateManagment = new TemplateManagment(file);
		assertEquals(7,templateManagment.getComponents().size());
		Extractor extractor = new Extractor(file);
		assertEquals(extractor.getAllItems().size(),templateManagment.getExtractor().getAllItems().size());
		assertTrue(templateManagment.getTemplates().size()  > 0 );
	}
	
	@SuppressWarnings("restriction")
	public void testCRUDTemplate() throws FileNotFoundException, IOException {
		String file = "base.yaml";
		TemplateManagment templateManagment = new TemplateManagment(file);
		LinkedHashMap<String, Template> initialTemplates = templateManagment.getTemplates();
		//RemoveTemplate
		templateManagment.removeTemplate("String");
		assertEquals(templateManagment.getTemplates().size(), 5);
		//AddTemplate
		ArrayList<String> templateFields = new ArrayList<>(Arrays.asList("type","description","required","metadata","constraints"));
		templateManagment.addTemplate("String", templateFields);
		assertEquals(initialTemplates.size(), templateManagment.getTemplates().size());
		//UpdateTemplate 
		templateManagment.updateTemplate("Integer", "type" , false);
		assertEquals(2, templateManagment.getTemplates().get("Integer").getFields().size());
		templateManagment.updateTemplate("Integer", "type" , true);
		assertEquals(3, templateManagment.getTemplates().get("Integer").getFields().size());
		//ContentTemplate
		boolean has = true;	
		ArrayList<String> templateFieldsString = new ArrayList<>(Arrays.asList("type","description","required","metadata","constraints"));
		Template templateTest = new Template("String", templateFieldsString);
		has = templateManagment.hasTemplate(templateTest);
		assertEquals(true, has);
		templateManagment.getTemplates().get("String").removeField("description");
		has = templateManagment.hasTemplate(templateTest);
		assertEquals(false, has);

	}
	
	public void testLaunchProcess() throws FileNotFoundException, IOException {
		String file = "base.yaml";
		TemplateManagment templateManagment = new TemplateManagment(file);
		assertNull(templateManagment.getParseToJSON());
		String componentName = "onap.policies.controlloop.operational.common.Apex";
		templateManagment.launchTranslation(componentName);
		assertNotNull(templateManagment.getParseToJSON());
	}
	
}
