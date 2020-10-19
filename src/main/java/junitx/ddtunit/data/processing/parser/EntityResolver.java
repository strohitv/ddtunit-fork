// $Id: EntityResolver.java 351 2008-08-14 20:20:56Z jg_hamburg $
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;

/**
 * Example taken from: David Brownell, SAX2, O'Reilly 2002 Paris, p.88ff.
 * 
 * @author jg
 */
class EntityResolver implements org.xml.sax.EntityResolver {
    private Logger log = LoggerFactory.getLogger(EntityResolver.class);

    /**
     * Default contructor no extras
     */
    public EntityResolver() {
        // just instanciate object
    }

    /**
     * @param publicId of xml entity to retrieve
     * @param systemId of xml entity to retrieve
     * 
     * @return InputSource of specified xml entity
     * 
     * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String,
     *      java.lang.String)
     */
    public InputSource resolveEntity(String publicId, String systemId) {
        StringBuffer logMsg = new StringBuffer("resolveEntity(publicId=")
            .append(publicId).append(", systemId=").append(systemId);

        log.debug(logMsg.toString() + ") - START)");

        InputSource source = null;

        try {
            if (ParserImpl.XSD_URL.equals(systemId)) {
                URL url = this.getClass().getResource(ParserImpl.XSD_RESOURCE_PATH);
                Reader xsdReader = new InputStreamReader(this.getClass()
                    .getResourceAsStream(ParserImpl.XSD_RESOURCE_PATH));

                source = new InputSource(url.toExternalForm());
                source.setCharacterStream(xsdReader);
            } else if (systemId != null && systemId.startsWith("file:/")) {
                // try local resource for XML external entity
                String resourceName = systemId.substring(5, systemId.length());

                URL url = this.getClass().getResource(resourceName);
                InputStream resourceStream = this.getClass()
                    .getResourceAsStream(resourceName);
                if (resourceStream != null) {
                    source = new InputSource(url.toExternalForm());
                    source.setByteStream(resourceStream);
                }
            }
        } finally {
            log.debug(logMsg + ") - END");
        }

        return source;
    }
}
