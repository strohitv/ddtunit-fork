//$Id: ArrayCreatorAction.java 351 2008-08-14 20:20:56Z jg_hamburg $
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

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
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
public class ArrayCreatorAction extends ActionBase {
    protected Logger log = LoggerFactory.getLogger(ArrayCreatorAction.class);

    /**
     * 
     * Constructor used as standard constructor to instanciate actions of this
     * type
     * 
     * @param attrMap
     */
    public ArrayCreatorAction(Map<String, String> attrMap) {
        super(attrMap);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.parser.ActionBase#process()
     */
    public IAction process() {
        log.debug("process - process new signature for call...");
        if (!this.successorProcessed) {
            processNoSuccessor();
        }
        IAction rootAction = getPrevious();
        if (rootAction != null) {
            String hintValue = rootAction.getHint();

            if (HintTypes.INTERNAL_MAPENTRY.equals(hintValue)
                    || HintTypes.FIELDS.equals(hintValue)
                    || HintTypes.COLLECTION.equals(hintValue)
                    || HintTypes.ATTRLIST.equals(hintValue)) {
                rootAction.processSuccessor(this);
            } else {
                throw new UnsupportedOperationException(
                        "CollectionCreatorAction does not support hint '"
                                + hintValue + "'");
            }
        } else {
            rootAction = this;
        }
        // if rootAction is null simply return action -> base object is a
        // collection
        pop();
        // do not forget to process the actual root element
        return rootAction;
    }

    public void processSuccessor(IAction successor) {
        log.debug("processSuccessor(" + successor + " - START");
        if (successor != null) {
            if (HintTypes.FIELDS.equals(successor.getHint())
                    || HintTypes.COLLECTION.equals(successor.getHint())) {
                Map attribMap = new HashMap();
                IAction attribListAction = ActionFactory.getAction(
                    ActionState.ATTRLIST_CREATION, attribMap);
                this.insert(attribListAction);
                try {
                    attribListAction.createObject();
                    // initialize with first list element
                    ((List) attribListAction.getValue()).add(successor
                        .getObject());
                } catch (Exception ex) {
                    throw new DDTException("Error on action processing", ex);
                }
            } else if (HintTypes.CONTENT.equals(successor.getHint())) {
                String value = successor.getValue().toString();
                if (!ContentCreatorAction.CONTENT_NULL.equals(value)) {
                    try {
                        int size = Integer.parseInt(value);
                        instanciateArray(size);
                    } catch (NumberFormatException ex) {
                        throw new DDTTestDataException(
                                "Must provide an integer as size of array.");
                    } catch (Exception ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                }
            } else {
                List fields = (List) successor.getObject().getValue();
                TypedObject field = null;
                try {
                    instanciateArray(fields.size());
                    for (int count = 0; count < fields.size(); count++) {
                        field = (TypedObject) fields.get(count);
                        // provided
                        ((Object[]) this.getValue())[count] = field.getValue();
                    }
                } catch (Exception ex) {
                    StringBuffer sb = new StringBuffer(
                            "Error on setting elements of array.");
                    throw new DDTException(sb.toString(), ex);
                }
            }
            if (!getType().startsWith("[L")) {
                this.setType(getType());
            }
            this.successorProcessed = true;
        }
    }

    /**
     * @param fields
     * @throws ClassNotFoundException
     * @throws Exception
     */
    private void instanciateArray(int size) throws ClassNotFoundException {
        if (this.getValue() == null) {
            String localType = null;
            if (getType().startsWith("[L")) {
                // cut off "[L" ... ";" from array type
                localType = getType().substring(2, getType().length() - 1);
            } else {
                localType = getType();
            }
            Class clazz = Class.forName(localType);
            Object arrayObj = Array.newInstance(clazz, size);
            this.setValue(arrayObj);
        }
    }

    public void processNoSuccessor() {
        try {
            instanciateArray(1);
            if (getType() != null && !getType().startsWith("[L")) {
                setType(getType());
            }
        } catch (ClassNotFoundException ex) {
            StringBuffer sb = new StringBuffer(
                    "Error on setting elements of array.");
            throw new DDTException(sb.toString(), ex);
        }
    }

    public void setType(String type) {
        if (this.injectedObject == null) {
            inject();
        }
        if (type != null && !type.startsWith("[L")) {
            this.injectedObject.setType("[L" + type + ";");
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.parser.ActionBase#inject()
     */

    public IAction inject() {
        String type = (String) this.attrMap.get(ParserConstants.XML_ATTR_TYPE);
        String id = (String) this.attrMap.get(ParserConstants.XML_ATTR_ID);
        // check if type is based on java.util.Collection
        try {
            if (type == null && ParserConstants.XML_ELEM_ITEM.equals(id)) {
                type = TypedObject.UNKNOWN_TYPE;
            }
        } catch (Exception ex) {
            throw new DDTException("Container class '" + type
                    + "' does not implement java.util.Collection.", ex);
        }
        this.injectedObject = new TypedObject(id, type);
        return this;
    }

}
