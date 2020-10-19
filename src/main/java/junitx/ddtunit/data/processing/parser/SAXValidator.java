//$Id: SAXValidator.java 351 2008-08-14 20:20:56Z jg_hamburg $
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import junitx.ddtunit.DDTException;

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
 * @author jg
 */
public final class SAXValidator extends ErrorHandler {
    private static final String XML_XERCES_NONAMESPACE_SCHEMA_LOCATION_URI = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";

    private static final String XML_XERCES_SCHEMA_FULL_CHECK_URI = "http://apache.org/xml/features/validation/schema-full-checking";

    private static final String XML_XERCES_VALIDATE_URI = "http://apache.org/xml/features/validation/schema";

    /**
     * resource path of xml schema used for validation
     */
    public final static String XSD_RESOURCE_PATH = "/junitx/ddtunit/data/processing/parser/ddtunit.xsd";

    private Logger log = LoggerFactory.getLogger(SAXValidator.class);

    private XMLReader producer;

    private DefaultHandler consumer;

    private static final String XML_VALIDATE_URI = "http://xml.org/sax/features/validation";

    private static final String XML_NAMESPACE_URI = "http://xml.org/sax/features/namespaces";

    private static final String XML_NAMESPACE_PREFIX_URI = "http://xml.org/sax/features/namespace-prefixes";

    private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

    private static final String LF = System.getProperty("line.separator");

    public SAXValidator() {
        log.debug("DDTParser - constructor START");
        try {
            // the SAX way
            // this.producer = XMLReaderFactory.createXMLReader();
            // the jaxp1.1 way
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);
            this.producer = factory.newSAXParser().getXMLReader();
            // this.producer = new SAXParser();
            this.producer.setFeature(XML_NAMESPACE_PREFIX_URI, false);
            this.producer.setFeature(XML_NAMESPACE_URI, true);
            
            this.producer.setFeature(XML_VALIDATE_URI, true);
            this.producer.setFeature(XML_XERCES_VALIDATE_URI, true);
            this.producer.setFeature(XML_XERCES_SCHEMA_FULL_CHECK_URI, true);
            this.producer.setProperty(
                XML_XERCES_NONAMESPACE_SCHEMA_LOCATION_URI, ParserImpl.XSD_URL);
            this.producer.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
            this.producer.setProperty(JAXP_SCHEMA_SOURCE, ParserImpl.XSD_URL);
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

    public void validate(String resourceName) {
        this.consumer = new DefaultHandler();
        Locator locator = new LocatorImpl();
        this.consumer.setDocumentLocator(locator);
        this.producer.setContentHandler(consumer);
        this.producer.setEntityResolver(new EntityResolver());
        try {
            // process reource
            // check if resource is file or resource
            InputSource iSource = null;
            InputStream in = this.getClass().getResourceAsStream(resourceName);

            if (in == null) {
                File inFile = new File(resourceName);

                if (inFile.exists()) {
                    iSource = new InputSource(resourceName);
                    iSource.setSystemId(inFile.toURL().toExternalForm());
                    this.producer.parse(iSource);
                } else {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Could not find provided testdata resource: ")
                        .append(resourceName);
                    throw DDTException.create(sb, null);
                }
            } else {
                iSource = new InputSource(in);
                iSource.setSystemId(this.getClass().getResource(resourceName)
                    .toExternalForm());
                this.producer.parse(iSource);
            }
        } catch (IOException e) {
            log.error("Error on behalf of xml test resource.", e);
            throw DDTException.create(new StringBuffer(
                    "Error on behalf of xml test resource."), e);
        } catch (SAXException e) {
            StringBuffer sb = new StringBuffer(
                    "Error during parsing of xml testresource");
            if (SAXParseException.class.isInstance(e)) {
                sb.append(LF).append("Resource \'").append(resourceName)
                    .append("\' line/column ").append(
                        ((SAXParseException) e).getLineNumber()).append("/")
                    .append(((SAXParseException) e).getColumnNumber());
            }
            log.error(sb.toString(), e);
            throw DDTException.create(sb, e);
        } finally {
            log.debug("parse(" + resourceName + ")-END");
        }
    }
}
