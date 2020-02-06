/*-
 * ============LICENSE_START=======================================================
 * ONAP CLAMP
 * ================================================================================
 * Copyright (C) 2020 AT&T Intellectual Property. All rights
 *                             reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END============================================
 * ===================================================================
 *
 */


package org.onap.clamp.clds.tosca.update;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;

import junit.framework.TestCase;
import org.onap.clamp.clds.tosca.update.Extractor;

public class ExtractorTest extends TestCase {
	
	public void testGetComponents() throws FileNotFoundException, IOException{
		
		String file = "base.yaml";
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
		Extractor extractor = new Extractor(file);
		LinkedHashMap<String, Object> toParse = extractor.getAllAsMaps();
		extractor.parseInComponent(toParse);
		assertEquals(7,extractor.getAllItems().size());
	
	}
	
}
