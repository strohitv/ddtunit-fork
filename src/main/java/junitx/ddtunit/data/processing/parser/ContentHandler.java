// $Id: ContentHandler.java 351 2008-08-14 20:20:56Z jg_hamburg $
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
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTOSRS
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

import junitx.ddtunit.DDTException;
import junitx.ddtunit.data.IDataSet;
import junitx.ddtunit.data.processing.Engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX content handler of ddtunit xml extension
 * 
 * @author jg
 */
class ContentHandler extends DefaultHandler implements LexicalHandler {
    private static final String LF = System.getProperty("line.separator");

    Logger log = LoggerFactory.getLogger(ContentHandler.class);

    private Locator locator;

    private String clusterId;

    private String resourceName;

    private int levelCount;

    private Engine eventConsumer;

    /**
     * Instanciate sax content handler
     * 
     * @param aParser
     */
    public ContentHandler(String resourceName, IDataSet clusterDataSet) {
        super();
        this.resourceName = resourceName;
        this.eventConsumer = new Engine(clusterDataSet);
        this.levelCount = 0;
    }

    /**
     * Set Locator
     * 
     * @param aLocator
     */
    public void setDocumentLocator(Locator aLocator) {
        this.locator = aLocator;
    }

    /**
     * SAX event startDocument ...
     */
    public void startDocument() {
        log.debug("Start Document...");
    }

    /**
     * SAX event endDocument ...
     */
    public void endDocument() {
        log.debug("End Document...");
    }

    /**
     * SAX startElement()
     * 
     * @param namespaceURI namespace not null if provided in xml element
     * @param localName of xml element
     * @param qName qualified name of xml element
     * @param attributes of xml element
     */
    public void startElement(String namespaceURI, String localName,
            String qName, Attributes attributes) {
        try {
            this.levelCount++;
            this.eventConsumer.processStartElement(qName, attributes,
                this.levelCount);
        } catch (Exception ex) {
            StringBuffer sb = new StringBuffer(ex.getClass().getName());
            sb.append(ex.getMessage());
            sb.append(LF).append("Resource \'").append(this.resourceName)
                .append("\' line/column ").append(locator.getLineNumber())
                .append("/").append(locator.getColumnNumber());
            DDTException ddtEx = new DDTException(sb.toString(), ex);
            ddtEx.setStackTrace(ex.getStackTrace());
            log.warn(sb.toString(), ddtEx);
            throw ddtEx;
        }
    }

    /**
     * SAX endElement()
     * 
     * @param namespace of xml element
     * @param localName of xml element
     * @param qName qualified name of xml element
     * 
     * @throws SAXException if any parsing exception occures
     */
    public void endElement(String namespace, String localName, String qName)
            throws SAXException {
        try {
            this.eventConsumer.processEndElement(qName, this.levelCount);
        } catch (Exception ex) {
            StringBuffer sb = new StringBuffer(ex.getClass().getName());
            sb.append(ex.getMessage());
            sb.append(LF).append("Resource \'").append(this.resourceName)
                .append("\' line/column ").append(locator.getLineNumber())
                .append("/").append(locator.getColumnNumber());
            DDTException ddtEx = new DDTException(sb.toString(), ex);
            ddtEx.setStackTrace(ex.getStackTrace());
            log.warn(sb.toString(), ddtEx);
            throw ddtEx;
        } finally {
            this.levelCount--;
        }
    }

    /**
     * Process simple content of xml tag values. <br/ > Ignore content outside
     * of <code>&lt;obj&gt;</code> tags. <br/ > No splitted text content is
     * processed correctly.
     * 
     * @param buffer of character that contains value of xml element
     * @param offset of xml value start
     * @param length of xml value content
     * 
     * @throws SAXException if any processing error occures
     */
    public void characters(char[] buffer, int offset, int length)
            throws SAXException {
        this.eventConsumer.processCharacters(buffer, offset, length);
    }

    public void ignorableWhitespace(char[] buffer, int offset, int length) {
        System.err.println(new String(buffer, offset, length));
    }

    /**
     * Implementation of ErrorHandler interface
     * 
     * @param e
     * @throws SAXException DOCUMENT ME!
     */
    public void error(SAXParseException e) throws SAXException {
        log.error("Parse error detected", e);
        throw e;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
        this.eventConsumer.setClusterId(clusterId);
    }

    public String getClusterId() {
        return clusterId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ext.LexicalHandler#endCDATA()
     */
    public void endCDATA() throws SAXException {
        this.eventConsumer.endCDATA();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ext.LexicalHandler#endDTD()
     */
    public void endDTD() throws SAXException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ext.LexicalHandler#startCDATA()
     */
    public void startCDATA() throws SAXException {
        this.eventConsumer.startCDATA();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
     */
    public void comment(char[] ch, int start, int length) throws SAXException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
     */
    public void endEntity(String name) throws SAXException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
     */
    public void startEntity(String name) throws SAXException {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String,
     *      java.lang.String, java.lang.String)
     */
    public void startDTD(String name, String publicId, String systemId)
            throws SAXException {
    }
}
