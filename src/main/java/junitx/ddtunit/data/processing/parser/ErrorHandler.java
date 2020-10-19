// $Id: ErrorHandler.java 351 2008-08-14 20:20:56Z jg_hamburg $
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Basic class idea taken from : <br/>SAX2 - David Brownell, O'Reilly, Köln,
 * Paris 2002, p.51ff
 * 
 * @author jg
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
class ErrorHandler implements org.xml.sax.ErrorHandler {
    private final static int ERR_PRINT = 1;

    private final static int ERR_IGNORE = 2;

    private final static int WARN_PRINT = 4;

    private final static int WARN_IGNORE = 8;

    private final static int FATAL_PRINT = 16;

    private final static int FATAL_IGNORE = 32;

    private static final String LF = System.getProperty("line.separator");

    private Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    private int flags;

    /**
     * 
     */
    public ErrorHandler() {
        flags = ERR_PRINT + WARN_PRINT + FATAL_PRINT;
    }

    /**
     * @param exception propagated from sax parser
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException exception) throws SAXException {
        log.error(printParseException("Error", exception), exception);

        if ((flags & ERR_IGNORE) == 0) {
            throw exception;
        }
    }

    /**
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException exception) throws SAXException {
        log.error(printParseException("Error", exception), exception);

        if ((flags & FATAL_IGNORE) == 0) {
            throw new SAXException(printParseException("Error", exception));
        }
    }

    /**
     * @param exception propagated from sax parser
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException exception) throws SAXException {
        log.warn(printParseException("Warning", exception), exception);

        if ((flags & WARN_IGNORE) == 0) {
            throw exception;
        }
    }

    private String printParseException(String message, SAXParseException e) {
        StringBuffer sb = new StringBuffer();
        int temp;

        sb.append("** ").append(message).append(":  ").append(e.getMessage())
            .append(LF);

        // most such exceptions include the (absolute) URI forthe text
        if (e.getSystemId() != null) {
            sb.append("  URI: ").append(e.getSystemId()).append(" ");
        }

        // many include approximate line and column numbers
        if ((temp = e.getLineNumber()) != -1) {
            sb.append("  - line: ").append(temp).append(" ");
        }

        if ((temp = e.getColumnNumber()) != -1) {
            sb.append(",  col : ").append(temp);
        }

        // public id might be available, but is seldom useful
        return sb.toString();
    }
}
