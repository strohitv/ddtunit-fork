//$Id: SubelementCreatorAction.java 359 2009-08-05 23:54:59Z jg_hamburg $
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junitx.ddtunit.DDTException;
import junitx.ddtunit.data.DDTTestDataException;
import junitx.ddtunit.data.IDataSet;
import junitx.ddtunit.data.TestDataSet;
import junitx.ddtunit.data.TypedObject;
import junitx.ddtunit.data.processing.CollectionCreatorAction.CollectionReferenceInfo;
import junitx.ddtunit.util.ClassAnalyser;
import junitx.ddtunit.util.PrivilegedAccessor;

/**
 * This class contains object state and other information to create object from
 * SAX event stream.
 * 
 * @author jg
 */
public class SubelementCreatorAction extends ActionBase {
    /**
     * 
     * Constructor used as standard constructor to instanciate actions of this
     * type
     * 
     * @param attrMap
     */
    public SubelementCreatorAction(Map attrMap) {
        super(attrMap);
    }

    /*
     * (non-Javadoc)
     * 
     * @see junitx.ddtunit.parser.ActionBase#process()
     */
    public IAction process() {
        log.debug("process SubelementCreator - START");
        IAction rootAction = this.getPrevious();
        if (!this.successorProcessed) {
            processNoSuccessor();
        }
        if (rootAction != null) {
            String hintValue = rootAction.getHint();

            if (HintTypes.COLLECTION.equals(hintValue)
                    || HintTypes.MAP.equals(hintValue)
                    || HintTypes.ATTRLIST.equals(hintValue)
                    || HintTypes.FIELDS.equals(hintValue)
                    || HintTypes.CONSTRUCTOR.equals(hintValue)
                    || HintTypes.CALL.equals(hintValue)
                    || HintTypes.BEAN.equals(hintValue)
                    || HintTypes.ARRAY.equals(hintValue)
                    || HintTypes.INTERNAL_MAPENTRY.equals(hintValue)) {
                if (hasReferenceInfo()) {
                    rootAction.processSuccessor(this);
                } else {
                    rootAction.processSuccessor(this);
                }
            } else {
                throw new DDTException("Unknown hint (" + hintValue
                        + ")- stop processing.");
            }
        } else {
            // process obj element - no rootAction exiats
            if (hasReferenceInfo()) {
                TypedObject destObject;
                if (this.attrMap.get(ParserConstants.XML_ATTR_TYPE) == null) {
                    destObject = new TypedObject(getAttribute("refid"));
                } else {
                    destObject = new TypedObject(getAttribute("refid"),
                            getType());
                }
                IReferenceInfo refInfo = new ObjectReferenceInfo(this
                    .getObject(), destObject);
                add(refInfo);
            }
        }
        this.pop();
        return this;
    }

    /**
     * If no content on tag is provided just instanciate object by default
     * constructor.
     */
    public void processNoSuccessor() {
        // process direct object call providing no parameters (empty tag)
        if (hasReferenceInfo()) {
            TypedObject destObject;
            if (this.attrMap.get(ParserConstants.XML_ATTR_TYPE) == null) {
                destObject = new TypedObject(getAttribute("refid"));
            } else {
                destObject = new TypedObject(getAttribute("refid"), getType());
            }
            IReferenceInfo refInfo;
            if (this.getPrevious() == null) {
                refInfo = new ObjectReferenceInfo(this.getObject(), destObject);
            } else if (HintTypes.ATTRLIST.equals(this.getPrevious().getHint())) {
                refInfo = new SubelementReferenceInfo(this.getPrevious()
                    .getPrevious().getObject(), this.getId(), destObject);
                add(refInfo);
            } else if (HintTypes.COLLECTION.equals(this.getPrevious().getHint())){
            	// if a collection type is processed the subelement must be specified as ARGLIST
            	// the reference is processed inside of rootAction
            	this.attrMap.put(ParserConstants.XML_ATTR_HINT, HintTypes.ATTRLIST.toString());
            } else {
                refInfo = new SubelementReferenceInfo(this.getPrevious()
                    .getObject(), this.getId(), destObject);
                add(refInfo);
            }
        } else {
            Map attribMap = new HashMap();
            IAction action = ActionFactory.getAction(
                ActionState.ATTRLIST_CREATION, attribMap);
            action.inject();
            action.setValue(new Vector());
            this.insert(action);
            action.process();
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
        this.injectedObject = new TypedObject(id, type);
        if (attrMap.containsKey(ParserConstants.XML_ATTR_BASEID)) {
            String baseId = (String) attrMap
                .get(ParserConstants.XML_ATTR_BASEID);
            TypedObject baseObj = InstanceFactory.getInstance().getObject(
                baseId, type);
            this.injectedObject.setValue(baseObj.getValue());
        }
        return this;
    }

    public void processSuccessor(IAction successor) {
        log.debug("processSuccessor(" + successor + ") - START");
        // create attribute list action and insert after rootAction
        if (HintTypes.ATTRLIST.equals(successor.getHint())) {
            List fields = (List) successor.getObject().getValue();
            TypedObject field = null;
            try {
                this.getType();
                // if TypedObject is not instanciated and it is no call
                // operation
                // create object by default constructor
                if (this.getValue() == null){
//                        && !HintTypes.CALL.equals(this.getHint())) {
                    this.createObject();
                }
                for (int count = 0; count < fields.size(); count++) {
                    field = (TypedObject) fields.get(count);
                    // provided
                    if (HintTypes.INTERNAL_MAPENTRY.equals(this.getHint())) {
                        PrivilegedAccessor.setFieldValue(this.getValue(), field
                            .getId(), field);
                    } else {
                        PrivilegedAccessor.setFieldValue(this.getValue(), field
                            .getId(), field.getValue());
                    }
                }
            } catch (Exception ex) {
                StringBuffer sb = new StringBuffer("Error on setting field ")
                    .append(field).append(" on ").append(this.getType())
                    .append(". Check if hint is correct.");
                throw new DDTException(sb.toString(), ex);
            }
        } else if (HintTypes.INTERNAL_MAPENTRY.equals(this.getHint())) {
            try {
                PrivilegedAccessor.setFieldValue(this.getValue(), successor
                    .getId(), successor.getObject());
            } catch (Exception ex) {
                throw new DDTTestDataException("Error filling map entry "
                        + this.getId(), ex);
            }
        } else if (HintTypes.CONTENT.equals(successor.getHint())) {
            String content = successor.getValue().toString();
            if (ContentCreatorAction.CONTENT_EMPTY.equals(content)) {
                content = "";
                this.setValue(content);
            } else if (ContentCreatorAction.CONTENT_BLANK.equals(content)) {
                content = " ";
                this.setValue(content);
            } else if (!ContentCreatorAction.CONTENT_NULL.equals(content)) {
                // separate specific constructors for base types
                try {
                    Constructor constr = null;
                    Object obj = null;
                    String myType = this.getType();
                    if ("java.lang.Character".equals(myType)) {
                        obj = new Character(content.charAt(0));
                    } else {
                        constr = (Constructor) ClassAnalyser
                            .findMethodByParams(myType,
                                ClassAnalyser.CLASS_CONSTRUCTOR,
                                new Class[] { java.lang.String.class });
                        obj = constr.newInstance(new Object[] { content });
                    }
                    this.setValue(obj);
                } catch (DDTTestDataException ex) {
                    throw ex;
                } catch (Exception ex) {
                    throw new DDTTestDataException(
                            "Error on creation using constructor(String) of "
                                    + this.getType() + "(\""
                                    + successor.getValue().toString() + "\")",
                            ex);
                }
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
                ((List) attribListAction.getValue()).add(successor.getObject());
            } catch (Exception ex) {
                throw new DDTException("Error on action processing", ex);
            }
        }
        this.successorProcessed = true;
    }

    class SubelementReferenceInfo extends ReferenceInfoBase {
        private String field;

        /**
         * @param source
         * @param destination
         */
        public SubelementReferenceInfo(TypedObject source, String field,
                TypedObject destination) {
            super(source, destination);
            this.field = field;
        }

        public void resolve(IDataSet dataSet, String groupId, String testId) {
            if (ParserConstants.UNKNOWN.equals(groupId)
                    && ParserConstants.UNKNOWN.equals(testId)) {
                doResolution(dataSet);
            } else if (!ParserConstants.UNKNOWN.equals(testId)) {
                IDataSet groupSet = dataSet.get(groupId);
                IDataSet testDataSet = null;
                if (groupSet != null) {
                    testDataSet = groupSet.get(testId);
                }
                doResolution(testDataSet);
            } else {
                throw new DDTTestDataException(
                        "Do not process group data without testId");
            }
        }

        /**
         * @param dataSet to resolve reference to
         */
        private void doResolution(IDataSet dataSet) {
            TypedObject dest = dataSet.findObject(this.getDestId(), this
                .getDestType());
            TypedObject source = dataSet.getObject(this.getSourceId(), this
                .getSourceType());
            if (source == null && dataSet instanceof TestDataSet) {
                source = ((TestDataSet) dataSet).getAssert(this.getSourceId(),
                    this.getSourceType());
            }
            if (dest != null && source != null) {
                try {
                    PrivilegedAccessor.setFieldValue(source.getValue(),
                        this.field, dest.getValue());
                } catch (IllegalAccessException ex) {
                    throw new DDTTestDataException(
                            "Error resolving reference of " + dest
                                    + " to field " + this.field + " on "
                                    + source, ex);
                } catch (NoSuchFieldException ex) {
                    throw new DDTTestDataException(
                            "Error resolving reference of " + dest
                                    + " to field " + this.field + " on "
                                    + source, ex);
                }
            } else {
                throw new DDTTestDataException(
                        "Error on processing references on testdata.");
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