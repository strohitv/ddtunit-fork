//$Id: ActionBase.java 359 2009-08-05 23:54:59Z jg_hamburg $
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

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junitx.ddtunit.DDTException;
import junitx.ddtunit.data.DDTTestDataException;
import junitx.ddtunit.data.IDataSet;
import junitx.ddtunit.data.TestDataSet;
import junitx.ddtunit.data.TypedObject;
import junitx.ddtunit.util.ClassAnalyser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for representing objects and assert information created from xml
 * 
 * @author jg
 */
abstract class ActionBase implements IAction {
    protected Logger log = LoggerFactory.getLogger(ActionBase.class);

    private List<ILinkChangeListener> changeListeners;

    private List<IReferenceListener> referenceListeners;

    protected Map<String, String> attrMap;

    protected TypedObject injectedObject;

    protected IAction previous;

    protected IAction next;

    protected static final String LF = System.getProperty("line.separator");

    /**
     * specifies if a successing tag is allready processed.<br/> can be used to
     * detect empty tags
     */
    boolean successorProcessed = false;

    /**
     * 
     * Constructor used as standard constructor to instanciate objects of this
     * this type
     * 
     * @param attrMap provided during scan with all attributes of actual parsing
     *        action
     */
    public ActionBase(Map<String, String> attrMap) {
        if (this.attrMap == null) {
            this.attrMap = new HashMap<String, String>();
        }
        this.attrMap.putAll(attrMap);
        this.changeListeners = new Vector<ILinkChangeListener>();
        this.referenceListeners = new Vector<IReferenceListener>();
    }

    public void processNoSuccessor() {
        // do nothing in default implementation
    }

    public void registerLinkChangeListener(ILinkChangeListener listener) {
        this.changeListeners.add(listener);
    }

    public void removeLinkChangeListener(ILinkChangeListener listener) {
        this.changeListeners.remove(listener);
    }

    public void pop() {
        for (int count = 0; count < this.changeListeners.size(); count++) {
            ((ILinkChangeListener) this.changeListeners.get(count)).pop();
        }
        this.removeFromLinkChangeListener(this);
    }

    private void removeFromLinkChangeListener(IAction action) {
        for (int count = 0; count < this.changeListeners.size(); count++) {
            action
                .removeLinkChangeListener(((ILinkChangeListener) this.changeListeners
                    .get(count)));
        }
    }

    public void promoteLinkChangeListener(IAction action) {
        for (int count = 0; count < this.changeListeners.size(); count++) {
            ActionStack actionStack = (ActionStack) this.changeListeners
                .get(count);
            action.registerLinkChangeListener(actionStack);
            if (actionStack.last == this) {
                actionStack.last = action;
            }
        }
    }

    public void registerReferenceListener(IReferenceListener listener) {
        this.referenceListeners.add(listener);
    }

    public void add(IReferenceInfo info) {
        for (int count = 0; count < this.referenceListeners.size(); count++) {
            ((IReferenceListener) this.referenceListeners.get(count)).add(info);
        }
    }

    public void removeReferenceListener(IReferenceListener listener) {
        this.referenceListeners.remove(listener);
    }

    protected void desintegrate() {
        this.attrMap.clear();
        this.injectedObject = null;
    }

    public TypedObject getInjectedObject() {
        return injectedObject;
    }

    public String getHint() {
        String hint = (String) this.attrMap.get(ParserConstants.XML_ATTR_HINT);
        if (hint == null) {
            hint = HintTypes.FIELDS.toString();
        }
        return hint;
    }

    public String getId() {
        String id = null;
        if (this.injectedObject != null) {
            id = this.injectedObject.getId();
        }
        return id;
    }

    public String getType() {
        String type = null;
        if (this.injectedObject != null) {
            type = this.injectedObject.getType();
            if (TypedObject.UNKNOWN_TYPE.equals(type)) {
                type = getTypeFromRoot();
                if (type == null) {
                    throw new DDTTestDataException(
                            "Could not specify type of object");
                }
                this.injectedObject.setType(type);
            }
        } else {
            type = getTypeFromRoot();
        }
        return type;
    }

    public void setType(String type) {
        if (this.injectedObject != null) {
            this.injectedObject.setType(type);
        }
    }

    public Object getValue() {
        Object obj = null;
        if (this.injectedObject != null) {
            obj = this.injectedObject.getValue();
        }
        return obj;
    }

    public TypedObject getObject() {
        return this.injectedObject;
    }

    public void setObject(TypedObject newObject) {
        this.injectedObject = newObject;
    }

    public void setValue(Object obj) {
        if (this.injectedObject != null) {
            this.injectedObject.setValue(obj);
        }
    }

    /**
     * Extract object type of associated root action on action stack. <br/>If no
     * root action exists return null
     * 
     * @return full qualified class name of root object type or null if no root
     *         exists.
     */
    public String getTypeFromRoot() {
        String mapEntryType = "junitx.ddtunit.data.processing.MapEntry";
        log.debug("getTypeFromRoot() - START " + this);

        String objectType = null;
        IAction rootAction = this.getPrevious();
        // field information
        if (rootAction == null) {
            throw new DDTException("Corrupt internal Action structure");
        }
        // objectType = rootAction.getType();
        String hintValue = rootAction.getHint();
        String rootTypeValue = rootAction.getType();
        if (HintTypes.ATTRLIST.equals(hintValue)) {
            rootAction = rootAction.getPrevious();
            hintValue = rootAction.getHint();
            rootTypeValue = rootAction.getType();
        }
        if (HintTypes.INTERNAL_MAPENTRY.equals(hintValue)
                || mapEntryType.equals(rootTypeValue)) {
            rootAction.createObject();
            if ("key".equals(this.injectedObject.getId())) {
                objectType = rootAction
                    .getAttribute(ParserConstants.XML_ATTR_KEYTYPE);
            } else if ("value".equals(this.injectedObject.getId())) {
                objectType = rootAction
                    .getAttribute(ParserConstants.XML_ATTR_VALUETYPE);
            }
            // objectType = mapEntryType;
            // this.setAttribute(ParserConstants.XML_ATTR_HINT,
            // HintTypes.INTERNAL_MAPENTRY.toString());
            // this.setType(objectType);
        } else if (HintTypes.MAP.equals(hintValue)) {
            String keyType = rootAction
                .getAttribute(ParserConstants.XML_ATTR_KEYTYPE);
            String valueType = rootAction
                .getAttribute(ParserConstants.XML_ATTR_VALUETYPE);
            if (keyType != null && !keyType.equals("")) {
                this.setAttribute(ParserConstants.XML_ATTR_KEYTYPE, keyType);
            }
            if (valueType != null && !valueType.equals("")) {
                this
                    .setAttribute(ParserConstants.XML_ATTR_VALUETYPE, valueType);
            }
            objectType = mapEntryType;
            this.setAttribute(ParserConstants.XML_ATTR_HINT,
                HintTypes.INTERNAL_MAPENTRY.toString());
            this.setType(objectType);
        } else if (HintTypes.COLLECTION.equals(hintValue)) {
            objectType = rootAction
                .getAttribute(ParserConstants.XML_ATTR_VALUETYPE);
        } else if (HintTypes.ARRAY.equals(hintValue)) {
            objectType = rootAction.getType();
            if (objectType.startsWith("[L")) {
                objectType = objectType.substring(2, objectType.length() - 1);
            }
        } else if (HintTypes.BEAN.equals(hintValue)){
        	// specify type from setter Definition of field name
        	StringBuffer setterName = new StringBuffer("set").append(getId().substring(0,1).toUpperCase())
        	.append(getId().substring(1));
        	Method setter = ClassAnalyser.findMethodByName(rootTypeValue, setterName.toString());
        	objectType = setter.getParameterTypes()[0].getName();
        	try {
				objectType = TypeAbbreviator.getInstance().resolve(objectType);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        } else {
            objectType = rootAction.extractFieldType(getId());
        }
        log.debug("getTypeFromRoot() - END (" + objectType + ")");
        return objectType;
    }

    private void setAttribute(String key, String value) {
        this.attrMap.put(key, value);
    }

    /**
     * Extract field type information from object sitting on the stack. <br/>If
     * rootAction is a generated container action used for construction of
     * fields or constructor call of the underlying object then skip the
     * container action and retrieve requested type from underlying object.
     * 
     * @param fieldName to extract type from
     * @return full type of field
     */
    public String extractFieldType(String fieldName) {
        log.debug("extractFieldType(" + fieldName + ") - START");
        String fieldType = null;
        String rootType = null;
        IAction rootAction = getPrevious();
        if ("java.util.Vector".equals(getType()) && "attrlist".equals(getId())) {
            rootType = rootAction.getType();
        } else {
            rootType = getType();
        }

        if (rootAction != null && TypedObject.UNKNOWN_TYPE.equals(rootType)
                && HintTypes.INTERNAL_MAPENTRY.equals(rootAction.getHint())) {
            fieldType = rootAction.getAttribute(fieldName + "type");
        } else {
            Field field = ClassAnalyser.getSelectedField(rootType, fieldName);
            if (field != null) {
                fieldType = field.getType().getName();
                // check for primitive type
                try {
                    fieldType = TypeAbbreviator.getInstance()
                        .resolve(fieldType);
                } catch (IOException ex) {
                    throw new DDTTestDataException(
                            "could not identify field of type " + fieldType);
                }
            }
        }
        log
            .debug("extractFieldType(" + fieldName + ")=" + fieldType
                    + " - END");
        return fieldType;
    }

    /**
     * Create injected object to according type specification on this action.
     * Just do nothing if object allready instanciated.
     * 
     * @return RecordBase
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public TypedObject createObject() {
        log.debug("Instanciate TypedObject ...");
        if (getInjectedObject() == null) {
            inject();
        }
        if (this.getValue() == null) {
            String recordType = getType();
            if (TypedObject.UNKNOWN_TYPE.equals(recordType)) {
                recordType = getTypeFromRoot();
            }
            Class clazz;
            Object obj = null;
            try {
                clazz = Class.forName(recordType);
                if (HintTypes.ARRAY.equals(this.getHint())) {
                    obj = Array.newInstance(clazz, 1);
                } else {
                    obj = clazz.newInstance();
                }
            } catch (Exception ex) {
                throw new DDTException("Error on object creation of class "
                        + recordType, ex);
            }
            setValue(obj);
            setType(recordType);
        }
        return this.getInjectedObject();
    }

    public IAction getNext() {
        return next;
    }

    public void setNext(IAction next) {
        this.next = next;
    }

    public IAction getPrevious() {
        return previous;
    }

    public void setPrevious(IAction previous) {
        this.previous = previous;
    }

    /**
     * Insert a new Record entry after this.
     * 
     * @param action to insert
     */
    public void insert(IAction action) {
        IAction nextAction = this.getNext();
        this.setNext(action);
        if (nextAction != null) {
            action.setNext(nextAction);
            nextAction.setPrevious(action);
        }
        action.setPrevious(this);
        promoteLinkChangeListener(action);
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[").append(this.getClass().getName());
        if (this.injectedObject != null) {
            buffer.append(" id: ");
            buffer.append(this.injectedObject.getId());
            buffer.append(" type: ");
            buffer.append(this.injectedObject.getType());
        }
        buffer.append("]");
        return buffer.toString();
    }

    public String getAttribute(String id) {
        String attrValue = (String) this.attrMap.get(id);
        return attrValue;
    }

    public boolean hasReferenceInfo() {
        boolean check = false;
        String refid = this.getAttribute(ParserConstants.XML_ATTR_REFID);
        if (refid != null) {
            check = true;
        }
        return check;
    }

    class ObjectReferenceInfo extends ReferenceInfoBase {

        /**
         * @param source
         * @param destination
         */
        public ObjectReferenceInfo(TypedObject source, TypedObject destination) {
            super(source, destination);
        }

        public void resolve(IDataSet dataSet, String groupId, String testId) {
            if (ParserConstants.UNKNOWN.equals(groupId)) {
                if (ParserConstants.UNKNOWN.equals(testId)) {
                    TypedObject dest = dataSet.findObject(this.getDestId(),
                        this.getDestType());
                    TypedObject source = dataSet.getObject(this.getSourceId(),
                        this.getSourceType());
                    if (source == null && dataSet instanceof TestDataSet) {
                        source = ((TestDataSet) dataSet).getAssert(this
                            .getSourceId(), this.getSourceType());
                    }
                    if (dest != null && source != null) {
                        source.setValue(dest.getValue());
                        source.setType(dest.getType());
                    } else {
                        throw new DDTTestDataException(
                                "Error on processing references on testdata.");
                    }
                } else {
                    throw new DDTTestDataException(
                            "No testId expected because groupId is unspecified");
                }
            } else {
                if (!ParserConstants.UNKNOWN.equals(testId)) {
                    IDataSet groupSet = dataSet.get(groupId);
                    IDataSet testDataSet = null;
                    if (groupSet != null) {
                        testDataSet = groupSet.get(testId);
                    }
                    TypedObject dest = testDataSet.findObject(this.getDestId(),
                        this.getDestType());
                    TypedObject source = testDataSet.getObject(this
                        .getSourceId(), this.getSourceType());
                    if (source == null && testDataSet instanceof TestDataSet) {
                        source = ((TestDataSet) testDataSet).getAssert(this
                            .getSourceId(), this.getSourceType());
                    }
                    if (dest != null && source != null) {
                        source.setValue(dest.getValue());
                        source.setType(dest.getType());
                    } else {
                        throw new DDTTestDataException(
                                "Error on processing references on testdata.");
                    }
                } else {
                    throw new DDTTestDataException(
                            "Do not process group data without testId");
                }
            }
        }

        /**
         * If this object is referencing to the provided info object then raise
         * rank of referenced object info.<br/> A reference from an
         * ObjectAction to another Action is detected if destination info is
         * equal to the source info of provided info.
         * 
         * @param info to check reference on and raise rank respectivly
         */
        public void raiseRankOf(IReferenceInfo info) {
            if (this.getDestId().equals(info.getSourceId())
                    && (this.getDestType().equals(info.getSourceType()) || TypedObject.UNKNOWN_TYPE
                        .equals(info.getDestType()))) {
                if (this.getRank() >= info.getRank()) {
                    info.setRank(this.getRank() + 1);
                }
            }
        }
    }

}
