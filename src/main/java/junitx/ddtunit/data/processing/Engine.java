//$Id: Engine.java 351 2008-08-14 20:20:56Z jg_hamburg $
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
import java.util.HashMap;
import java.util.Map;

import junitx.ddtunit.data.DDTTestDataException;
import junitx.ddtunit.data.DDTDataRepository;
import junitx.ddtunit.data.ExceptionAsserter;
import junitx.ddtunit.data.IDataSet;
import junitx.ddtunit.data.ObjectAsserter;
import junitx.ddtunit.data.TestDataSet;
import junitx.ddtunit.data.TestGroupDataSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;

/**
 * Class used for creation of a {@link junitx.ddtunit.data.TestClusterDataSet}
 * by using SAX parsing event information.
 * 
 * @author jg
 */
public class Engine {
    final static int LEVEL_CLUSTER_GLOBAL_OBJ = 4;

    final static int LEVEL_RESOURCE_OBJ = 3;

    final static int LEVEL_SINGLE_OBJ = 1;

    final static int LEVEL_GROUP = 3;

    final static int LEVEL_GROUP_GLOBAL = 3;

    final static int LEVEL_TEST = 4;

    final static int LEVEL_TEST_OBJS = 5;

    final static int LEVEL_TEST_OBJ = 6;

    private Logger log = LoggerFactory.getLogger(Engine.class);

    private IDataSet clusterDataSet;

    /**
     * processCluster is set to true if parsing the selected class substructure
     * of xml resource.
     */
    private boolean processCluster = false;

    private boolean processResource = false;

    private String clusterId;

    private String groupId;

    private IDataSet actualDataSet;

    private ActionStack actionStack;

    private boolean cdataProcessing;

    private ReferenceProcessor refProcessor;

    /**
     * Instanciate processing engin to fill provided clusterDataSet
     * 
     * @param clusterDataSet to be filled during engine processing
     */
    public Engine(IDataSet clusterDataSet) {
        this.cdataProcessing = false;
        this.clusterDataSet = clusterDataSet;
    }

    /**
     * Process start of a SAX startElement event by extracting information an
     * storing it in an {@link ActionStack}
     * 
     * @param qname
     * @param attribs
     * @param level
     */
    public void processStartElement(String qName, Attributes attribs, int level) {
        Map<String, String> attrMap = getAttrMap(attribs);
        if (ParserConstants.XML_ELEM_RESOURCES.equals(qName)
                && level == (LEVEL_RESOURCE_OBJ - 1)) {
            this.refProcessor = new ReferenceProcessor(this.clusterDataSet);
        } else if (ParserConstants.XML_ELEM_OBJ.equals(qName)
                && level == LEVEL_SINGLE_OBJ) {
            this.processCluster = true;
            this.actionStack = new ActionStack();
            pushAction(qName, attrMap);
        } else if (ParserConstants.XML_ELEM_OBJ.equals(qName)
                && level == LEVEL_RESOURCE_OBJ) {
            this.processResource = true;
            this.actionStack = new ActionStack();
            pushAction(qName, attrMap);
        } else if (ParserConstants.XML_ELEM_CLUSTER.equals(qName)
                && this.clusterId.equals(attrMap
                    .get(ParserConstants.XML_ATTR_ID))) {
            log.debug("Found clusterid=" + this.clusterId);
            processCluster = true;
            this.actualDataSet = this.clusterDataSet;
        } else if (processCluster) {
            log.debug("Process Start Element <" + qName + "> Level " + level
                    + " Attrs " + attrMap.size());
            // process method tag
            if (ParserConstants.XML_ELEM_OBJS.equals(qName)
                    && level == (LEVEL_CLUSTER_GLOBAL_OBJ - 1)) {
                if (this.refProcessor == null) {
                    this.refProcessor = new ReferenceProcessor(
                            this.clusterDataSet);
                }
            } else if (ParserConstants.XML_ELEM_OBJ.equals(qName)
                    && (level == LEVEL_CLUSTER_GLOBAL_OBJ || level == LEVEL_TEST_OBJ)) {
                this.actionStack = new ActionStack();
                pushAction(qName, attrMap);
            } else if (ParserConstants.XML_ELEM_EXCEPTION.equals(qName)
                    && level == LEVEL_TEST_OBJ) {
                this.actionStack = new ActionStack();
                IAction action = pushAction(qName, attrMap);
                String type = (String) attrMap
                    .get(ParserConstants.XML_ATTR_TYPE);
                String id = (String) attrMap.get(ParserConstants.XML_ATTR_ID);
                String actionAttr = (String) attrMap
                    .get(ParserConstants.XML_ATTR_ACTION);

                action.setObject(new ExceptionAsserter(id, type, actionAttr));
            } else if (ParserConstants.XML_ELEM_ASSERT.equals(qName)
                    && level == LEVEL_TEST_OBJ) {
                this.actionStack = new ActionStack();
                IAction action = pushAction(qName, attrMap);
                String type = (String) attrMap
                    .get(ParserConstants.XML_ATTR_TYPE);
                String id = (String) attrMap.get(ParserConstants.XML_ATTR_ID);
                String actionAttr = (String) attrMap
                    .get(ParserConstants.XML_ATTR_ACTION);
                action.setObject(new ObjectAsserter(id, type, actionAttr));
            } else if (ParserConstants.XML_ELEM_TEST.equals(qName)
                    && level == LEVEL_TEST) {
                // initialize temporary methoddataset only once
                TestGroupDataSet methodDataSet = (TestGroupDataSet) this.clusterDataSet
                    .get(groupId);
                String testName = (String) attrMap
                    .get(ParserConstants.XML_ATTR_ID);

                this.actualDataSet = new TestDataSet(testName, methodDataSet);
                methodDataSet.put(testName, (TestDataSet) actualDataSet);
                methodDataSet.addSubKey(testName);
                this.refProcessor.setTestId(testName);
            } else if (ParserConstants.XML_ELEM_GROUP.equals(qName)
                    && level == LEVEL_GROUP) {
                this.groupId = (String) attrMap
                    .get(ParserConstants.XML_ATTR_ID);
                if (this.refProcessor == null) {
                    this.refProcessor = new ReferenceProcessor(
                            this.clusterDataSet);
                }
                this.refProcessor.setGroupId(this.groupId);
                this.actualDataSet = new TestGroupDataSet(this.groupId,
                        this.clusterDataSet);
                this.clusterDataSet.put(this.groupId,
                    (TestGroupDataSet) this.actualDataSet);

                // process test tag
            } else if (!contains(ParserConstants.XML_IGNORE_ELEM, qName)) {
                // process generic tag
                // check for valid id attribute. If not exist set tag name
                pushAction(qName, attrMap);
            }
        } else {
            log.debug("Ignore Start Element <" + qName + "> Level " + level
                    + " Attrs " + attrMap.size());
        }

    }

    /**
     * @param qName
     * @param attrMap
     */
    private IAction pushAction(String qName, Map<String, String> attrMap) {
        IAction action;
        String id = (String) attrMap.get(ParserConstants.XML_ATTR_ID);
        if (id == null) {
            attrMap.put("id", qName);
        }
        String hint = (String) attrMap.get(ParserConstants.XML_ATTR_HINT);
        if (HintTypes.COLLECTION.equals(hint)) {
            action = push(attrMap, ActionState.COLLECTION_CREATION);
        } else if (HintTypes.ARRAY.equals(hint)) {
            action = push(attrMap, ActionState.ARRAY_CREATION);
        } else if (HintTypes.MAP.equals(hint)) {
            action = push(attrMap, ActionState.MAP_CREATION);
        } else if (HintTypes.BEAN.equals(hint)) {
            action = push(attrMap, ActionState.BEAN_CREATION);
        } else if (HintTypes.CONSTRUCTOR.equals(hint)
                || HintTypes.CALL.equals(hint)) {
            action = push(attrMap, ActionState.CALL_CREATION);
        } else if (HintTypes.CONTENT.equals(hint)) {
            action = push(attrMap, ActionState.CONTENT_CREATION);
        } else if (HintTypes.CONSTANT.equals(hint)) {
            action = push(attrMap, ActionState.CONSTANT_CREATION);
        } else if (HintTypes.DATE.equals(hint)) {
            action = push(attrMap, ActionState.DATE_CREATION);
        } else {
            // no special processing for constructor elements, use
            // generic
            action = push(attrMap, ActionState.SUBELEMENT_CREATION);
        }
        return action;
    }

    /**
     * @param or
     */
    private IAction push(Map attrMap, ActionState processState) {
        IAction action = ActionFactory.getAction(processState, attrMap);
        this.actionStack.push(action);
        action.registerReferenceListener(this.refProcessor);
        log.debug("push(" + action.toString() + ") - END");
        return action;
    }

    /**
     * @param attribs
     * @return
     */
    private Map<String, String> getAttrMap(Attributes attribs) {
        Map<String, String> attrMap = new HashMap<String, String>();
        for (int count = 0; count < attribs.getLength(); count++) {
            String key = attribs.getQName(count);
            String value = attribs.getValue(count);
            if (ParserConstants.XML_ATTR_TYPE.equals(key)
                    || ParserConstants.XML_ATTR_VALUETYPE.equals(key)
                    || ParserConstants.XML_ATTR_KEYTYPE.equals(key)) {
                try {
                    value = TypeAbbreviator.getInstance().resolve(value);
                } catch (IOException ex) {
                    throw new DDTTestDataException(
                            "Problem using TypeAbbreviator.", ex);
                }
            }
            attrMap.put(key, value);
        }
        if (!attrMap.containsKey(ParserConstants.XML_ATTR_HINT)) {
            attrMap.put(ParserConstants.XML_ATTR_HINT, HintTypes.FIELDS
                .toString());
        }
        return attrMap;
    }

    /**
     * Check if searchText is contained in String array
     * 
     * @param array to search
     * @param searchText to look for
     * @return true if searchText is found in array
     */
    private boolean contains(String[] array, String searchText) {
        boolean found = false;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(searchText)) {
                found = true;
                break;
            }
        }
        return found;
    }

    /**
     * Process SAX events of elemet end status
     * 
     * @param qName
     * @param level
     */
    public void processEndElement(String qName, long level) {
        if (processCluster || processResource) {
            log.debug("process End Element <" + qName + "> Level " + level
                    + " - START");
            if ((ParserConstants.XML_ELEM_RESOURCES.equals(qName)
                    && level == (LEVEL_RESOURCE_OBJ - 1) || (ParserConstants.XML_ELEM_OBJS
                .equals(qName) && level == (LEVEL_CLUSTER_GLOBAL_OBJ - 1)))) {
                this.refProcessor.resolve();
            } else if (ParserConstants.XML_ELEM_OBJ.equals(qName)
                    && level == LEVEL_SINGLE_OBJ) {
                IAction action = null;
                while (!this.actionStack.isEmpty()) {
                    action = this.actionStack.process();
                }
                this.clusterDataSet.putObject("singleton", action.getObject());
                this.processResource = false;
            } else if (ParserConstants.XML_ELEM_OBJ.equals(qName)
                    && level == LEVEL_RESOURCE_OBJ) {
                IAction action = null;
                while (!this.actionStack.isEmpty()) {
                    action = this.actionStack.process();
                }
                // select method-test based dataset and store ObjectRecord
                DDTDataRepository.getInstance().putObject(action.getId(),
                    action.getObject());
                this.processResource = false;
            } else if (ParserConstants.XML_ELEM_OBJ.equals(qName)
                    && level == LEVEL_CLUSTER_GLOBAL_OBJ) {
                IAction action = null;
                while (!this.actionStack.isEmpty()) {
                    action = this.actionStack.process();
                }
                // select method-test based dataset and store ObjectRecord
                this.clusterDataSet.putObject(action.getId(), action
                    .getObject());
            } else if (ParserConstants.XML_ELEM_OBJ.equals(qName)
                    && level == LEVEL_TEST_OBJ) {
                IAction action = null;
                while (!this.actionStack.isEmpty()) {
                    action = this.actionStack.process();
                }
                // select method-test based dataset and store ObjectRecord
                this.actualDataSet
                    .putObject(action.getId(), action.getObject());
            } else if ((ParserConstants.XML_ELEM_ASSERT.equals(qName) || ParserConstants.XML_ELEM_EXCEPTION
                .equals(qName))
                    && level == LEVEL_TEST_OBJ) {
                IAction action = null;
                while (!this.actionStack.isEmpty()) {
                    action = this.actionStack.process();
                }
                // select method-test based dataset and store ObjectRecords
                ((TestDataSet) this.actualDataSet).getAssertMap().put(
                    action.getId(), action.getObject());
            } else if (ParserConstants.XML_ELEM_TEST.equals(qName)
                    && level == LEVEL_TEST && this.processCluster) {
                this.refProcessor.resolve();
                this.refProcessor.setTestId(null);
            } else if (ParserConstants.XML_ELEM_GROUP.equals(qName)
                    && level == LEVEL_GROUP && this.processCluster) {
                this.groupId = null;
                this.refProcessor.resolve();
            } else if (ParserConstants.XML_ELEM_CLUSTER.equals(qName)
                    && this.processCluster) {
                this.refProcessor.resolve();
                processCluster = false;
            } else if (!ParserConstants.XML_ELEM_TEST.equals(qName)
                    && !contains(ParserConstants.XML_IGNORE_ELEM, qName)) {
                // process generic tag
                log.debug("Process generic tag <" + qName + ">");
                this.actionStack.process();
            }
        } else {
            log.debug("Ignore End Element </" + qName + "> Level " + level);
        }
        StringBuffer sb = new StringBuffer("process End Element <").append(
            qName).append("> Level ").append(level).append(" - END");
        if (this.actionStack != null) {
            sb.append(", ").append(this.actionStack.infoOf());
        }
        log.debug(sb.toString());
    }

    /**
     * Process sax content only if specified cluster tag is processed and an
     * action stack is allready instanciated. If the trimmed content equals an
     * empty string or Else ignore event.
     * 
     * @param buffer
     * @param offset
     * @param length
     */
    public void processCharacters(char[] buffer, int offset, int length) {
        if ((processCluster || processResource) && this.actionStack != null) {
            StringBuffer sb = new StringBuffer();
            sb.append(buffer, offset, length);
            if (!"".equals(sb.toString().trim()) || this.cdataProcessing) {
                log.debug("Process content <\"" + sb.toString() + "\">");
                Map<String, String> attrMap = new HashMap<String, String>();
                attrMap.put(ParserConstants.XML_ATTR_CONTENT, sb.toString());
                attrMap.put(ParserConstants.XML_ATTR_ID,
                    ParserConstants.XML_ATTR_CONTENT);
                attrMap.put(ParserConstants.XML_ATTR_HINT,
                    ParserConstants.XML_ATTR_CONTENT);
                attrMap.put(ParserConstants.XML_ATTR_TYPE,
                    "java.lang.StringBuffer");
                attrMap.put(ParserConstants.XML_ATTR_PICDATA, Boolean
                    .toString(this.cdataProcessing));
                pushAction("content", attrMap);
            }
        }
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String classId) {
        this.clusterId = classId;
    }

    /**
     * 
     */
    public void endCDATA() {
        this.cdataProcessing = false;
    }

    /**
     * 
     */
    public void startCDATA() {
        this.cdataProcessing = true;
    }

}
