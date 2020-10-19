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
package junitx.ddtunit;

/**
 * Base exception of ddtunit package.
 * 
 * @author jg
 */
public class DDTException extends RuntimeException {
    private Throwable cause;

    private final static String LF = System.getProperty("line.separator");

    /**
     * Standard exception class for any ddtunit specific problems
     *  
     */
    public DDTException() {
        super();
    }

    /**
     * Standard exception class for any ddtunit specific problems
     * 
     * @param message to display in exception
     */
    public DDTException(String message) {
        super(message);
    }

    /**
     * Standard exception class for any ddtunit specific problems
     * 
     * @param aCause of actuall exception
     */
    public DDTException(Throwable aCause) {
        super(aCause);
        this.cause = aCause;
    }

    /**
     * Standard exception class for any ddtunit specific problems
     * 
     * @param message to display in exception
     * @param aCause of actuall exception
     */
    public DDTException(String message, Throwable aCause) {
        super(message, aCause);
        this.cause = aCause;
    }

    /**
     * get cause exception
     * 
     * @see java.lang.Throwable#getCause()
     * @return cause of exception or null if not exists
     */
    public Throwable getCause() {
        return this.cause;
    }

    /**
     * Create exception with compact trace hirachie information summariesed in
     * exception message.
     * 
     * @param message provided as prefix of exception
     * @param exception to start information exception
     * @return DDTException with created message
     */
    public static DDTException create(StringBuffer message, Throwable exception) {
        StringBuffer msg = new StringBuffer();
        DDTException ddtException = null;
        msg.append(message);
        if (exception != null) {
            msg.append(LF).append("Caused by:").append(
                exception.getClass().getName()).append(" - ").append(
                exception.getMessage());
        }
        msg.append(getCauseMessage(exception));
        ddtException = new DDTException(msg.toString(), exception);
        return ddtException;
    }

    private static String getCauseMessage(Throwable exception) {
        StringBuffer msg = new StringBuffer();
        if (exception != null) {
            Throwable cause = exception.getCause();
            if (cause != null && exception != cause) {
                msg.append(LF).append("Caused by (").append(
                    cause.getClass().getName()).append(") ");
                String causeMessage = cause.getMessage();
                if (causeMessage == null || causeMessage.compareTo("") == 0) {
                    msg.append("<No cause message>");
                } else {
                    msg.append(cause.getMessage());
                }
                msg.append(getCauseMessage(cause));
            }
        }
        return msg.toString();
    }
}