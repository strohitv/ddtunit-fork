//$Id: ContentCreatorAction.java 351 2008-08-14 20:20:56Z jg_hamburg $
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
 *     + Neither the id of the authors or DDTUnit, nor the
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
package junitx.ddtunit.data.processing;

import java.util.Map;

import junitx.ddtunit.DDTException;
import junitx.ddtunit.data.DDTTestDataException;
import junitx.ddtunit.data.TypedObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains object state and other information to create object from
 * SAX event stream.
 * 
 * @author jg
 */
public class ContentCreatorAction extends ActionBase {
    private Logger log = LoggerFactory.getLogger(ContentCreatorAction.class);

    /**
     * Constant representing null assignment in xml resource
     */
    public final static String CONTENT_NULL = "!NULL!";

    /**
     * Constant representation of empty string value in xml resource
     */
    public final static String CONTENT_EMPTY = "!EMPTY!";

    /**
     * Content representing string value of one space
     */
    public final static String CONTENT_BLANK = "!BLANK!";

    /**
     * 
     * Constructor used as standard constructor to instanciate actions of this
     * type
     * 
     * @param attrMap
     */
    public ContentCreatorAction(Map<String, String> attrMap) {
        super(attrMap);
    }

    /**
     * Contract constraints on processing of Content action: <br/>
     * <ul>
     * <li>Content action must be the last on action stack</li>
     * <li>There must be a valid root action distinct from content action.
     * </li>
     * <li>&nbsp;</li>
     * </ul>
     * 
     * @see junitx.ddtunit.data.processing.ActionBase#process()
     */
    public IAction process() {
        log.debug("Process ContentCreator - START");
        // check if action is last on stack - if not throw error
        if (this.getNext() != null) {
            throw new DDTException(
                    "Contract error - Content action must be last on stack");
        }
        IAction rootAction = this.getPrevious();
        if (this == rootAction) {
            throw new DDTTestDataException(
                    "Contract error - there must be at least one valid root action");
        }
        // check if rootAction is ContentCreatorAction as well - then concat
        // content
        IAction actionBase = null;
        String hintValue = rootAction.getHint();
        if (this.getValue() != null) {
            boolean cdataProcessing = "true".equals((String) this.attrMap
                .get(ParserConstants.XML_ATTR_PICDATA));
            if (HintTypes.CONTENT.equals(hintValue)) {
                rootAction.processSuccessor(this);
            } else if (HintTypes.CONSTANT.equals(hintValue)
                    || HintTypes.DATE.equals(hintValue)
                    || HintTypes.ARRAY.equals(hintValue)
                    || HintTypes.FIELDS.equals(hintValue)
                    || HintTypes.COLLECTION.equals(hintValue)
                    || HintTypes.MAP.equals(hintValue)
                    || HintTypes.BEAN.equals(hintValue)) {
                if (!cdataProcessing) {
                    this.setValue(new StringBuffer(this.getValue().toString()
                        .trim()));
                }
                rootAction.processSuccessor(this);
            } else {
                throw new UnsupportedOperationException(
                        "ContentCreatorAction does not support hint '"
                                + hintValue + "'");
            }

        }
        this.pop();
        actionBase = rootAction.process();
        return actionBase;
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.parser.ActionBase#inject()
     */
    public IAction inject() {
        String type = (String) this.attrMap.get(ParserConstants.XML_ATTR_TYPE);
        String id = (String) this.attrMap.get(ParserConstants.XML_ATTR_ID);
        this.injectedObject = new TypedObject(id, type);
        this.injectedObject.setValue(this.attrMap
            .get(ParserConstants.XML_ATTR_CONTENT));
        this.attrMap.remove(ParserConstants.XML_ATTR_CONTENT);
        return this;
    }

    public void processSuccessor(IAction successor) {
        log.debug("processSuccessor(" + successor + ") - START");
        // create attribute list action and insert after rootAction
        if (HintTypes.CONTENT.equals(successor.getHint())) {
            StringBuffer value = new StringBuffer().append(this.getValue());
            value.append(successor.getValue());
            this.setValue(value);
        } else {
            throw new DDTTestDataException("Unsupported successor action");
        }
    }
}
