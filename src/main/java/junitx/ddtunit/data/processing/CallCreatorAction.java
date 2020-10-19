//$Id: CallCreatorAction.java 250 2006-08-25 21:30:26Z jg_hamburg $
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junitx.ddtunit.DDTException;
import junitx.ddtunit.data.DDTTestDataException;
import junitx.ddtunit.data.TypedObject;
import junitx.ddtunit.util.ClassAnalyser;
import junitx.ddtunit.util.PrivilegedAccessor;

/**
 * This class contains object state and other information to create object from
 * SAX event stream.
 * 
 * @author jg
 */
public class CallCreatorAction extends ActionBase {
    /**
     * 
     * Constructor used as standard constructor to instanciate actions of this
     * type
     * 
     * @param attrMap
     */
    public CallCreatorAction(Map<String, String> attrMap) {
        super(attrMap);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.parser.ActionBase#process()
     */
    public IAction process() {
        log.debug("process CallCreator - START");
        if (!this.successorProcessed) {
            processNoSuccessor();
        }
        IAction rootAction = this.getPrevious();
        if (rootAction != null) {
            String hintValue = rootAction.getHint();

            if (HintTypes.COLLECTION.equals(hintValue)) {
                rootAction.processSuccessor(this);
            } else if (HintTypes.MAP.equals(hintValue)) {
                rootAction.processSuccessor(this);
            } else if (HintTypes.ATTRLIST.equals(hintValue)) {
                rootAction.processSuccessor(this);
            } else if (HintTypes.FIELDS.equals(hintValue)) {
                rootAction.processSuccessor(this);
            } else if (HintTypes.CONSTRUCTOR.equals(hintValue)
                    || HintTypes.CALL.equals(hintValue)) {
                rootAction.processSuccessor(this);
            } else if (HintTypes.BEAN.equals(hintValue)) {
                rootAction.processSuccessor(this);
            } else if (HintTypes.ARRAY.equals(hintValue)) {
                rootAction.processSuccessor(this);
            } else if (HintTypes.INTERNAL_MAPENTRY.equals(hintValue)) {
                rootAction.processSuccessor(this);
            } else {
                throw new DDTException("Unknown hint (" + hintValue
                        + ")- stop processing.");
            }
        } else {
            rootAction = this;
        }
        this.pop();
        return this;
    }

    public void processNoSuccessor() {
        // process direct object call providing no parameters (empty tag)
        IAction action = new AttributeListCreatorAction(
                new HashMap<String, String>());
        action.inject();
        action.setValue(new Vector());
        this.insert(action);
        action.process();
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
        return this;
    }

    public void processSuccessor(IAction successor) {
        log.debug("processSuccessor(" + successor + ") - START");
        // create attribute list action and insert after rootAction
        if (HintTypes.ATTRLIST.equals(successor.getHint())) {
            String rootType = this.getType();
            if (TypedObject.UNKNOWN_TYPE.equals(rootType)) {
                rootType = successor.getTypeFromRoot();
            }
            // do create object by calling the constructor
            // extract Class array from signature list to select
            // constructor
            Class[] sigClasses = getClassesFrom((List) successor.getValue());
            Object[] sigObjects = getObjectsFrom((List) successor.getValue());
            String methodName = this.getAttribute("method");
            Object obj = null;
            if (methodName == null || "".equals(methodName)
                    || "constructor".equals(methodName)) {
                Constructor constructor = (Constructor) ClassAnalyser
                    .findMethodByParams(rootType,
                        ClassAnalyser.CLASS_CONSTRUCTOR, sigClasses);
                checkForValidMethod(constructor, sigClasses, "constructor");
                try {
                    obj = constructor.newInstance(sigObjects);
                } catch (Exception ex) {
                    throw new DDTException(
                            "Error on object creation by constructor", ex);
                }
            } else {
                String callType = this.getAttribute("calltype");
                if (callType == null || "".equals(callType)) {
                    callType = rootType;
                }
                Method method = (Method) ClassAnalyser.findMethodByParams(
                    callType, methodName, sigClasses);
                checkForValidMethod(method, sigClasses, methodName);
                try {
                    if (Modifier.isStatic(method.getModifiers())) {
                        obj = method.invoke(null, sigObjects);
                    } else {
                        Class callClazz = Class.forName(callType);
                        Object callObj = callClazz.newInstance();
                        obj = method.invoke(callObj, sigObjects);
                    }
                } catch (Exception ex) {
                    throw new DDTException(
                            "Error on object creation by method call", ex);
                }
            }
            this.setValue(obj);
            this.successorProcessed = true;
        } else if (HintTypes.INTERNAL_MAPENTRY.equals(this.getHint())) {
            try {
                PrivilegedAccessor.setFieldValue(this.getValue(), successor
                    .getId(), successor.getObject());
            } catch (Exception ex) {
                throw new DDTTestDataException("Error filling map entry "
                        + this.getId(), ex);
            }
        } else {
            // ARRAY successor
            Map attribMap = new HashMap();
            IAction attribListAction = ActionFactory.getAction(
                ActionState.ATTRLIST_CREATION, attribMap);
            this.insert(attribListAction);
            try {
                // create container
                attribListAction.createObject();
                // initialize with first list element
                ((List<TypedObject>) attribListAction.getValue()).add(successor
                    .getObject());
            } catch (Exception ex) {
                throw new DDTException("Error on action processing", ex);
            }
        }
    }

    /**
     * @param sigClasses
     * @param constructor
     */
    private void checkForValidMethod(Object caller, Class[] sigClasses,
            String type) {
        if (caller == null) {
            StringBuffer sb = new StringBuffer("No method found! Expected ")
                .append(this.getType()).append(".").append(type).append("(");
            for (int count = 0; count < sigClasses.length; count++) {
                sb.append(sigClasses[count].getName());
                if (count < sigClasses.length) {
                    sb.append(", ");
                }
            }
            sb.append(")");
            throw new DDTException(sb.toString());
        }
    }

    /**
     * Process a list of RecordBase objects and return the array of Class
     * elements of provided records
     * 
     * @param recordList to process
     * @return array of element classes
     */
    private Class[] getClassesFrom(List recordList) {
        int max = recordList.size();
        Class[] sigClasses;
        sigClasses = new Class[max];
        for (int pos = 0; pos < max; pos++) {
            TypedObject tObject = (TypedObject) recordList.get(pos);
            String objectType = tObject.getType();
            try {

                sigClasses[pos] = Class.forName(objectType);
            } catch (ClassNotFoundException ex) {
                throw new DDTException("Could not instanciate class for "
                        + objectType, ex);
            }
        }
        return sigClasses;
    }

    /**
     * Process a list of RecordBase objects and return the array of instanciated
     * Objects of provided records
     * 
     * @param objectList to process
     * @return array of element classes
     */
    private Object[] getObjectsFrom(List objectList) {
        int max = objectList.size();
        Object[] sigObjects;
        sigObjects = new Object[max];
        for (int pos = 0; pos < max; pos++) {
            TypedObject tObject = (TypedObject) objectList.get(pos);
            sigObjects[pos] = tObject.getValue();
        }
        return sigObjects;
    }

}