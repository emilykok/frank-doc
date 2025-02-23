/* 
Copyright 2021 WeAreFrank! 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

    http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/
package org.frankframework.frankdoc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.logging.log4j.Logger;
import org.frankframework.frankdoc.model.FrankDocModel;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.TestUtil;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DocWriterNewIntegrationTest {
	private static Logger log = LogUtil.getLogger(DocWriterNewIntegrationTest.class);
	private static final String EXOTIC_PACKAGE = "org.frankframework.frankdoc.testtarget.exotic.";

	private FrankClassRepository classRepository;

	@ClassRule
	public static TemporaryFolder testFolder = new TemporaryFolder();

	@Before
	public void setUp() {
		classRepository = TestUtil.getFrankClassRepositoryDoclet(EXOTIC_PACKAGE);
	}

	private void validate(Schema schema, String testConfigurationFileName) throws Exception {
		Validator validator = schema.newValidator();
		String resourcePath = "/doc/testConfigs/" + testConfigurationFileName;
		URL resourceUrl = TestUtil.resourceAsURL(resourcePath);
		validator.validate(new SAXSource(Utils.asInputSource(resourceUrl)));
	}

	private Schema getSchemaFromSimpleFile(String schemaFileName) throws Exception {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		return schemaFactory.newSchema(new File(schemaFileName));
	}

	@Test
	public void testExotic() throws Exception {
		String outputFileName = generateXsd(
				XsdVersion.STRICT, "/doc/exotic-digester-rules.xml", EXOTIC_PACKAGE + "Master", "exotic.xsd", AttributeTypeStrategy.ALLOW_PROPERTY_REF);
		validate(getSchemaFromSimpleFile(outputFileName), "testExotic.xml");
		log.info("Validation of XML document against schema [{}] succeeded", outputFileName);
	}

	private String generateXsd(
			XsdVersion version, final String digesterRulesFileName, final String rootClassName, String outputSchemaFileName, AttributeTypeStrategy attributeTypeStrategy) throws IOException {
		FrankDocModel model = FrankDocModel.populate(TestUtil.resourceAsURL(digesterRulesFileName), rootClassName, classRepository);
		DocWriterNew docWriter = new DocWriterNew(model, attributeTypeStrategy, "1.2.3-SNAPSHOT");
		docWriter.init(rootClassName, version);
		String xsdString = docWriter.getSchema();

		File output = new File(testFolder.getRoot(), outputSchemaFileName);
		log.info("Output file of test xsd: " + output.getAbsolutePath());
		Writer writer = new BufferedWriter(new FileWriter(output));
		try {
			writer.append(xsdString);
		}
		finally {
			writer.close();
		}
		return output.getAbsolutePath();
	}
}
