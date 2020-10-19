// $Id: ParserImpl.java 351 2008-08-14 20:20:56Z jg_hamburg $
/********************************************************************************
 * DDTUnit, a Datadriven Approach to Unit- and Moduletesting
 * Copyright (c) 2004, Joerg and Kai Gellien
 * All rights reserved.
 *
 * The Software is provided under the terms of the Common Public License 1.0
 * as provided with the distribution of DDTUnit in the file cpl-v10.html.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *     + Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     + Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 *     + Neither the name of the authors or DDTUnit, nor the
 *       names of its contributors may be used to endorse or promote
 *       products derived from this software without specific prior
 *       written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ********************************************************************************/
package junitx.ddtunit.data.processing.parser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junitx.ddtunit.DDTException;
import junitx.ddtunit.data.DDTTestDataException;
import junitx.ddtunit.data.TestClusterDataSet;
import junitx.ddtunit.data.TypedObject;
import junitx.ddtunit.data.processing.IParser;
import junitx.ddtunit.util.DDTConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.LocatorImpl;

/**
 * XML SAX parser converting xml testdatea resourse into object structure used
 * by DDTestCase class execution. <br/>Using JAXP 1.1 to specify and configure
 * SAX conformant parser. <br/>As project default Apache Crimson provided in the
 * distribution of JDK 1.4.2 is used.
 * 
 * @author jg
 */
public final class ParserImpl implements IParser {
	private static final String XML_XERCES_NONAMESPACE_SCHEMA_LOCATION_URI = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";

	private static final String XML_XERCES_SCHEMA_FULL_CHECK_URI = "http://apache.org/xml/features/validation/schema-full-checking";

	private static final String XML_VALIDATE_URI = "http://xml.org/sax/features/validation";

	private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	private static final String XML_LEXICAL_HANDLER_URI = "http://xml.org/sax/properties/lexical-handler";

	private static final String XML_XERCES_VALIDATE_URI = "http://apache.org/xml/features/validation/schema";

	/**
	 * URL of xml schema for validation
	 */
	public final static String XSD_URL = "http://ddtunit.sourceforge.net/ddtunit.xsd";

	/**
	 * resource path of xml schema used for validation
	 */
	public final static String XSD_RESOURCE_PATH = "/junitx/ddtunit/data/processing/parser/ddtunit.xsd";

	private final Logger log = LoggerFactory.getLogger(ParserImpl.class);

	private XMLReader producer;

	private ContentHandler consumer;

	private final static String XML_NAMESPACE_URI = "http://xml.org/sax/features/namespaces";

	private final static String XML_NAMESPACE_PREFIX_URI = "http://xml.org/sax/features/namespace-prefixes";

	private static final String LF = System.getProperty("line.separator");

	private static final String XML_HEADER = "<?xml version=\"1.0\" ?>";

	/**
	 * 
	 */
	public ParserImpl() {
		log.debug("DDTParser - constructor START");
		try {
			// the SAX way
			// this.producer = XMLReaderFactory.createXMLReader();
			// the jaxp1.1 way
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			// factory.setValidating(DDTConfiguration.getInstance()
			// .isActiveXmlValidation());
			SAXParser saxParser = factory.newSAXParser();
			// Assert.assertTrue("SAX parser should be validating", saxParser
			// .isValidating());
			this.producer = saxParser.getXMLReader();
			this.producer.setFeature(XML_NAMESPACE_PREFIX_URI, false);
			this.producer.setFeature(XML_NAMESPACE_URI, true);
		} catch (SAXNotRecognizedException e) {
			throw DDTException
					.create(
							new StringBuffer(
									"XML ParserImpl does not support schema validation as of JAXP 1.2"),
							e);
		} catch (ParserConfigurationException e) {
			throw DDTException.create(new StringBuffer(
					"Error configuring parser."), e);
		} catch (SAXException e) {
			throw DDTException.create(new StringBuffer(
					"Error configuring parser."), e);
		}
		this.producer.setErrorHandler(new ErrorHandler());
		this.producer.setEntityResolver(new EntityResolver());
		log.debug("DDTParser - constructor END");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junitx.ddtunit.data.processing.parser.IParser#parse(java.lang.String,
	 *      java.lang.String)
	 */
	public TestClusterDataSet parse(String resource, boolean byName,
			String clusterId, TestClusterDataSet baseDataSet) {
		log.debug("parse(" + resource + ", " + clusterId + ")-START");
		this.consumer = new ContentHandler(resource, baseDataSet);
		Locator locator = new LocatorImpl();
		this.consumer.setDocumentLocator(locator);

		try {
			// add classId to consumer as a processing filter criterion
			this.consumer.setClusterId(clusterId);
			// process reource
			InputSource iSource = createInputSource(resource, true);
			if (validateSource(iSource)) {
				iSource = createInputSource(resource, byName);
			}
			this.producer.setContentHandler(consumer);
			this.producer.parse(iSource);
		} catch (IOException e) {
			log.error("Error on behalf of xml test resource.", e);
			throw DDTTestDataException.create(new StringBuffer(
					"Error on behalf of xml test resource."), e);
		} catch (SAXException e) {
			StringBuffer sb = new StringBuffer(
					"Error during parsing of xml testresource");
			if (SAXParseException.class.isInstance(e)) {
				sb.append(LF).append("Resource \'").append(resource).append(
						"\' line/column ").append(
						((SAXParseException) e).getLineNumber()).append("/")
						.append(((SAXParseException) e).getColumnNumber());
			}
			log.error(sb.toString(), e);
			throw DDTTestDataException.create(sb, e);
		} finally {
			log.debug("parse(" + resource + ", " + clusterId + ")-END");
		}
		if (baseDataSet.size() == 0) {
			StringBuffer sb = new StringBuffer(
					"No testdata provided for class id \'")
					.append(baseDataSet.getId())
					.append("\' in testresource \'")
					.append(resource)
					.append("\'\n")
					.append(
							"Check if referred class id in xml resources matches definition")
					.append(
							" of \n initTestData(resource, classId) inside of your testclass.");
			throw new DDTTestDataException(sb.toString());
		}

		return baseDataSet;
	}

	/**
	 * Parse object definition and use provided object as base class. If set to
	 * null a new instance of required type will be created during processing.
	 * 
	 * @param xmlObjectDef
	 *            string defining object to instanciate
	 * @param addXMLHeader -
	 *            set true if xml header should be added
	 * @return object to instanciate as defined
	 */
	public TypedObject parseElement(String xmlObjectDef, boolean addXMLHeader) {
		TypedObject myObj = null;
		String defaultCluster = "singleton";
		log
				.debug("parse(\"" + xmlObjectDef + "\", " + addXMLHeader
						+ ")-START");
		TestClusterDataSet dataSet = new TestClusterDataSet(defaultCluster,
				null);
		this.consumer = new ContentHandler(null, dataSet);
		Locator locator = new LocatorImpl();
		this.consumer.setDocumentLocator(locator);
		this.producer.setContentHandler(consumer);
		// dataSet = (TestClusterDataSet) parse(xmlObjectDef, false,
		// defaultCluster, dataSet);
		try {
			InputSource iSource = createInputSource(xmlObjectDef, false);
			validateSource(iSource);
			iSource = createInputSource(xmlObjectDef, false);
			this.producer.parse(iSource);
		} catch (IOException e) {
			log.error("Error on behalf of xml test resource.", e);
			throw DDTTestDataException.create(new StringBuffer(
					"Error on behalf of xml test resource."), e);
		} catch (SAXException e) {
			StringBuffer sb = new StringBuffer(
					"Error during parsing of xml testresource from reader.");
			throw DDTTestDataException.create(sb, e);
		} finally {
			log.debug("parse(reader)-END");
		}
		myObj = dataSet.getObject("singleton");
		return myObj;
	}

	/**
	 * @param resource
	 *            to process
	 * @param byName
	 *            set to true if resource is a name of resource to process,
	 *            false if it is the resource itself.
	 * @return
	 * @throws MalformedURLException
	 */
	private InputSource createInputSource(String resource, boolean byName)
			throws MalformedURLException {
		// check if resource is file or resource
		InputSource iSource = null;
		if (byName) {
			InputStream in = this.getClass().getResourceAsStream(resource);
			if (in == null) {
				File inFile = new File(resource);

				iSource = new InputSource(resource);
				iSource.setSystemId(inFile.toURL().toExternalForm());
			} else {
				iSource = new InputSource(in);
				iSource.setSystemId(this.getClass().getResource(resource)
						.toExternalForm());
			}
		} else {
			String xmlDef = null;
			xmlDef = XML_HEADER + resource;
			Reader reader = new StringReader(xmlDef);
			iSource = new InputSource(reader);
		}
		return iSource;
	}

	/**
	 * @param iSource
	 *            input source to validate
	 * @return true if validation was activated and processed
	 */
	private boolean validateSource(InputSource iSource) {
		boolean validated = false;
		try {
			if (this.producer.getFeature(XML_XERCES_VALIDATE_URI)) {
				DDTConfiguration.getInstance().setActiveParserValidation(true);
			}
		} catch (SAXException ex) {
			// no Apache Xerces found, so just ignore validation
		}
		if (DDTConfiguration.getInstance().isActiveXmlValidation()
				&& DDTConfiguration.getInstance().isActiveParserValidation()) {
			try {
				validated = true;
				this.producer.setContentHandler(new DefaultHandler());
				// validation properties and features must be activated
				this.producer.setFeature(XML_VALIDATE_URI, true);
				this.producer.setFeature(XML_XERCES_VALIDATE_URI, true);
				this.producer
						.setFeature(XML_XERCES_SCHEMA_FULL_CHECK_URI, true);
				this.producer.setProperty(
						XML_XERCES_NONAMESPACE_SCHEMA_LOCATION_URI,
						ParserImpl.XSD_URL);
				this.producer.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
				this.producer.setProperty(JAXP_SCHEMA_SOURCE,
						ParserImpl.XSD_URL);

				this.producer.parse(iSource);
			} catch (DDTException ex) {
				if (ex.getMessage() != null
						&& ex.getMessage().startsWith("Error during parsing")) {
					throw ex;
				}
				log.warn("Error on validation", ex);
			} catch (IOException e) {
				log.error("Error on behalf of xml test resource.", e);
				throw DDTException.create(new StringBuffer(
						"Error on behalf of xml test resource."), e);
			} catch (SAXException e) {
				StringBuffer sb = new StringBuffer(
						"Error during parsing of xml testresource");
				if (SAXParseException.class.isInstance(e)) {
					sb.append(LF).append("Resource \'").append(
							iSource.getSystemId()).append("\' line/column ")
							.append(((SAXParseException) e).getLineNumber())
							.append("/").append(
									((SAXParseException) e).getColumnNumber());
				}
				log.error(sb.toString(), e);
				throw DDTException.create(sb, e);
			}
		}
		return validated;
	}

	public TypedObject parseElement(String xmlObjectDef) {
		return parseElement(xmlObjectDef, true);
	}

}
