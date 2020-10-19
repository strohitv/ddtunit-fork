//$Id: AttributeListCreatorAction.java 351 2008-08-14 20:20:56Z jg_hamburg $
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
package junitx.ddtunit.data.processing;

import java.util.List;
import java.util.Map;

import junitx.ddtunit.DDTException;
import junitx.ddtunit.data.TypedObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeListCreatorAction extends ActionBase {
    protected Logger log = LoggerFactory.getLogger(AttributeListCreatorAction.class);

    public AttributeListCreatorAction(Map<String, String> attrMap) {
        super(attrMap);
        this.attrMap.put(ParserConstants.XML_ATTR_ID, "attrlist");
        this.attrMap.put(ParserConstants.XML_ATTR_TYPE, "java.util.Vector");
        this.attrMap.put(ParserConstants.XML_ATTR_HINT, "attrlist");
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.parser.ActionBase#process()
     */
    public IAction process() {
        log.debug("process AttributeListCreator - START");
        IAction rootAction = getPrevious();
        if (rootAction == null) {
            throw new DDTException(
                    "AttributeListCreatorAction must follow another action in action stack.");
        }
        // process different types of actions according to rootAction type
        String hintValue = rootAction.getHint();

        if (HintTypes.INTERNAL_MAPENTRY.equals(hintValue)) {
            rootAction.processSuccessor(this);
        } else if (HintTypes.CONSTRUCTOR.equals(hintValue)
                || HintTypes.CALL.equals(hintValue)) {
            rootAction.processSuccessor(this);
        } else if (HintTypes.FIELDS.equals(hintValue)) {
            rootAction.processSuccessor(this);
        } else if (HintTypes.BEAN.equals(hintValue)) {
            rootAction.processSuccessor(this);
        } else if (HintTypes.ARRAY.equals(hintValue)) {
            rootAction.processSuccessor(this);
        } else {
            throw new UnsupportedOperationException(
                    "AttributeListCreatorAction does not support hint '"
                            + hintValue + "'");
        }
        pop();
        // do not forget to process the actual root element
        if (rootAction.getPrevious() != null) {
            rootAction = rootAction.process();
        }
        return rootAction;
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
            } else if (HintTypes.ARRAY.equals(this.getHint())) {
                // do nothing
            } else {
                Class clazz = Class.forName(type);
                Class superclazz = clazz;
                boolean found = false;
                while (!found && superclazz != null) {
                    if ("java.util.Collection".equals(superclazz.getName())
                            || "java.util.AbstractCollection".equals(superclazz
                                .getName())) {
                        found = true;
                    }
                    superclazz = superclazz.getSuperclass();
                }
                if (!found) {
                    throw new DDTException("Container class '" + type
                            + "' does not implement java.util.Collection.");
                }
            }
        } catch (Exception ex) {
            throw new DDTException("Container class '" + type
                    + "' does not implement java.util.Collection.", ex);
        }
        this.injectedObject = new TypedObject(id, type);
        return this;
    }

    public void processSuccessor(IAction successor) {
        log.debug("processSuccessor(" + successor + ") - START");
        ((List<TypedObject>) this.getValue()).add(successor.getObject());
    }

}
