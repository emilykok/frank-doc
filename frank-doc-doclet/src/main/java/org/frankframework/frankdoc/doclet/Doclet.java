/* 
Copyright 2021, 2022 WeAreFrank! 

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

package org.frankframework.frankdoc.doclet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.json.JsonObject;

import org.apache.logging.log4j.Logger;

import com.sun.javadoc.ClassDoc;

import org.frankframework.frankdoc.AttributeTypeStrategy;
import org.frankframework.frankdoc.DocWriterNew;
import org.frankframework.frankdoc.FrankDocElementSummaryFactory;
import org.frankframework.frankdoc.FrankDocJsonFactory;
import org.frankframework.frankdoc.XsdVersion;
import org.frankframework.frankdoc.wrapper.FrankClassRepository;
import org.frankframework.frankdoc.wrapper.FrankDocException;
import org.frankframework.frankdoc.model.FrankDocModel;
import org.frankframework.frankdoc.model.FrankElementFilters;
import org.frankframework.frankdoc.util.LogUtil;
import org.frankframework.frankdoc.Utils;

class Doclet {
	private static final Logger log = LogUtil.getLogger(Doclet.class);

	private final FrankDocModel model;
	private final File xsdStrictFile;
	private final File xsdCompatibilityFile;
	private final File jsonFile;
	private final File elementSummaryFile;
	private final String frankFrameworkVersion;

	Doclet(ClassDoc[] classes, FrankDocletOptions options) throws FrankDocException {
		log.info("Output base directory is: [{}]", options.getOutputBaseDir());
		try {
			FrankClassRepository repository = FrankClassRepository.getDocletInstance(
					classes, FrankElementFilters.getIncludeFilter(), FrankElementFilters.getExcludeFilter(), FrankElementFilters.getExcludeFiltersForSuperclass());
			model = FrankDocModel.populate(options.getDigesterRulesUrl(), options.getRootClass(), repository);
			File outputBaseDir = new File(options.getOutputBaseDir());
			outputBaseDir.mkdirs();
			xsdStrictFile = new File(outputBaseDir, options.getXsdStrictPath());
			xsdStrictFile.getParentFile().mkdirs();
			xsdCompatibilityFile = new File(outputBaseDir, options.getXsdCompatibilityPath());
			xsdCompatibilityFile.getParentFile().mkdirs();
			jsonFile = new File(outputBaseDir, options.getJsonOutputPath());
			jsonFile.getParentFile().mkdirs();
			elementSummaryFile = new File(outputBaseDir, options.getElementSummaryPath());
			elementSummaryFile.getParentFile().mkdirs();
			frankFrameworkVersion = options.getFrankFrameworkVersion();
		} catch(SecurityException e) {
			throw new FrankDocException("SecurityException occurred initializing the output directory", e);
		}
	}

	void run() throws FrankDocException {
		if(frankFrameworkVersion == null) {
			log.error("No Frank!Framework version set; please configure it in your pom.xml as argument -frankFrameworkVersion");
		}
		writeStrictXsd();
		writeCompatibilityXsd();
		writeJson();
		writeElementSummary();
	}

	void writeStrictXsd() throws FrankDocException {
		log.info("Calculating XSD without deprecated items that allows property references");
		DocWriterNew docWriter = new DocWriterNew(model, AttributeTypeStrategy.ALLOW_PROPERTY_REF, frankFrameworkVersion);
		docWriter.init(XsdVersion.STRICT);
		String schemaText = docWriter.getSchema();
		log.info("Done calculating XSD without deprecated items that allows property references, writing it to file {}", xsdStrictFile.getAbsolutePath());
		writeStringToFile(schemaText, xsdStrictFile);
		log.info("Writing output file done");
	}

	void writeStringToFile(String text, File file) throws FrankDocException {
		Writer w = null;
		try {
			w = new BufferedWriter(new FileWriter(file));
			w.append(text);
			w.flush();
		} catch(IOException e) {
			throw new FrankDocException(String.format("Could not write file [%s]", file.getPath()), e);
		} finally {
			try {
				if(w != null) {
					w.close();
				}
			} catch(IOException e) {
				log.error("Failed to close file {}", file, e);
			}
		}
	}

	void writeCompatibilityXsd() throws FrankDocException {
		log.info("Calculating XSD with deprecated items that does not allow property references");
		DocWriterNew docWriter = new DocWriterNew(model, AttributeTypeStrategy.ALLOW_PROPERTY_REF_ENUM_VALUES_IGNORE_CASE, frankFrameworkVersion);
		docWriter.init(XsdVersion.COMPATIBILITY);
		String schemaText = docWriter.getSchema();
		log.info("Done calculating XSD with deprecated items that does not allow property references, writing it to file {}", xsdCompatibilityFile.getAbsolutePath());
		writeStringToFile(schemaText, xsdCompatibilityFile);
		log.info("Writing output file done");
	}

	void writeJson() throws FrankDocException {
		log.info("Calculating JSON file with documentation of the F!F");
		FrankDocJsonFactory jsonFactory = new FrankDocJsonFactory(model, frankFrameworkVersion);
		JsonObject jsonObject = jsonFactory.getJson();
		String jsonText = Utils.jsonPretty(jsonObject.toString());
		log.info("Done calculating JSON file with documentation of the F!F, writing the text to file {}", jsonFile.getAbsolutePath());
		writeStringToFile(jsonText, jsonFile);
		log.info("Writing output file done");
	}

	void writeElementSummary() throws FrankDocException {
		log.info("Calculating element summary");
		FrankDocElementSummaryFactory elementSummaryFactory = new FrankDocElementSummaryFactory(model);
		String text = elementSummaryFactory.getText();
		log.info("Done calculating element summary");
		writeStringToFile(text, elementSummaryFile);
		log.info("Writing output file done");
	}
}
