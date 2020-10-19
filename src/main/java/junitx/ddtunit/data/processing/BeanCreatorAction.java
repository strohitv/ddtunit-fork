//$Id: BeanCreatorAction.java 358 2009-08-05 13:58:44Z jg_hamburg $
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

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import junitx.ddtunit.DDTException;
import junitx.ddtunit.data.DDTTestDataException;
import junitx.ddtunit.data.TypedObject;
import junitx.ddtunit.util.ClassAnalyser;

/**
 * This class contains object state and other information to create object from
 * SAX event stream.
 * 
 * @author jg
 */
public class BeanCreatorAction extends ActionBase {
	/**
	 * 
	 * Constructor used as standard constructor to instanciate actions of this
	 * type
	 * 
	 * @param attrMap
	 */
	public BeanCreatorAction(Map attrMap) {
		super(attrMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junitx.ddtunit.parser.ActionBase#process()
	 */
	public IAction process() {
		log.debug("process BeanCreator - START");
		IAction rootAction = this.getPrevious();
		if (!this.successorProcessed) {
			processNoSuccessor();
		}
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
		IAction action = new AttributeListCreatorAction(new HashMap());
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
			List fields = (List) successor.getObject().getValue();
			TypedObject field = null;
			StringBuffer setter = new StringBuffer();
			try {
				// returned type is never used but method may specify this type
				// internally in injected object
				this.getType();
				if (this.getValue() == null) {
					this.createObject();
				}
				for (int count = 0; count < fields.size(); count++) {
					field = (TypedObject) fields.get(count);
					// check for existing setter
					setter.replace(0, setter.length(), "set");
					setter.append(field.getId().substring(0, 1).toUpperCase())
							.append(field.getId().substring(1));
					Class fieldClazz = null;
					if (field.getValue() != null) {
						fieldClazz = field.getValue().getClass();
						Class[] args = new Class[1];
						args[0] = fieldClazz;
						Method setMethod = (Method) ClassAnalyser
								.findMethodByParams(this.getType(), setter
										.toString(), args);
						setMethod.setAccessible(true);
						Object[] objs = new Object[1];
						objs[0] = field.getValue();
						setMethod.invoke(this.getValue(), objs);
					}
				}
			} catch (Exception ex) {
				StringBuffer sb = new StringBuffer("Error executing setter ")
						.append(this.getType())
						.append(".")
						.append(setter)
						.append("(...). ")
						.append(
								"Check if hint and setter are correct (Do not provide set-prefix!).");
				throw new DDTException(sb.toString(), ex);
			}
		} else if (HintTypes.COLLECTION.equals(successor.getHint())) {
			// provide a Collection as setter argument
			Collection param = (Collection) successor.getObject().getValue();
			TypedObject field = successor.getObject();
			StringBuffer setter = new StringBuffer();
			try {
				// returned type is never used but method may specify this type
				// internally in injected object
				this.getType();
				if (this.getValue() == null) {
					this.createObject();
				}
				// check for existing setter
				setter.replace(0, setter.length(), "set");
				setter.append(field.getId().substring(0, 1).toUpperCase())
						.append(field.getId().substring(1));
				Class fieldClazz = field.getValue().getClass();
				Class[] args = new Class[1];
				args[0] = fieldClazz;
				Method setMethod = (Method) ClassAnalyser.findMethodByParams(
						this.getType(), setter.toString(), args);
				setMethod.setAccessible(true);
				Object[] objs = new Object[1];
				objs[0] = field.getValue();
				setMethod.invoke(this.getValue(), objs);
			} catch (Exception ex) {
				StringBuffer sb = new StringBuffer("Error executing setter ")
						.append(this.getType())
						.append(".")
						.append(setter)
						.append("(...). ")
						.append(
								"Check if hint and setter are correct (Do not provide set-prefix!).");
				throw new DDTException(sb.toString(), ex);
			}
		} else if (HintTypes.CONTENT.equals(successor.getHint())) {
			if (!ContentCreatorAction.CONTENT_NULL.equals(successor.getValue()
					.toString())) {
				throw new DDTTestDataException(
						"Unsupported content for BeanCreator:"
								+ successor.getValue().toString());
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

}