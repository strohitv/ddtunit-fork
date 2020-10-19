//$Id: MapCreatorAction.java 351 2008-08-14 20:20:56Z jg_hamburg $
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junitx.ddtunit.DDTException;
import junitx.ddtunit.data.TypedObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains object state and other information to create object from
 * SAX event stream.
 * 
 * @author jg
 */
public class MapCreatorAction extends ActionBase {
	private Logger log = LoggerFactory.getLogger(MapCreatorAction.class);

	/**
	 * 
	 * Constructor used as standard constructor to instanciate actions of this
	 * type
	 * 
	 * @param attrMap
	 */
	public MapCreatorAction(Map<String, String> attrMap) {
		super(attrMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junitx.ddtunit.parser.ActionBase#process()
	 */
	public IAction process() {
		log.debug("process MapCreator Action - START");
		if (!this.successorProcessed) {
			processNoSuccessor();
		}
		IAction rootAction = getPrevious();
		if (rootAction != null) {
			String hintValue = rootAction.getHint();
			if (HintTypes.CONSTRUCTOR.equals(hintValue)
					|| HintTypes.CALL.equals(hintValue)) {
				log.debug("createRecord - process addition of field '"
						+ this.getId() + "'...");
				// create attribute list action and insert after rootAction
				Map attribMap = new HashMap();
				IAction attribListAction = ActionFactory.getAction(
						ActionState.ATTRLIST_CREATION, attribMap);
				rootAction.insert(attribListAction);
				try {
					// create container
					attribListAction.createObject();
					// initialize with first list element
					((List) attribListAction.getValue()).add(this.getObject());
					rootAction = attribListAction;
				} catch (Exception ex) {
					throw new DDTException("Error on action processing", ex);
				}

			} else if (HintTypes.FIELDS.equals(hintValue)) {
				rootAction.processSuccessor(this);
			} else if (HintTypes.COLLECTION.equals(hintValue)) {
				rootAction.processSuccessor(this);
			} else if (HintTypes.MAP.equals(hintValue)) {
				rootAction.processSuccessor(this);
			} else {
				throw new UnsupportedOperationException(
						"MapCreatorAction does not support hint '" + hintValue
								+ "'");
			}
		} else {
			rootAction = this;
		}
		pop();
		return rootAction;
	}

	public void processSuccessor(IAction successor) {
		log.debug("processSuccessor(" + successor + ") - START");
		if (HintTypes.CONTENT.equals(successor.getHint())) {
			String content = successor.getValue().toString();
			if (!ContentCreatorAction.CONTENT_NULL.equals(content)) {
				throw new DDTException("Only \"!NULL!\" supported in Map type");
			}
		} else {
			try {
				this.createObject();
				MapEntry entry = (MapEntry) successor.getValue();
				if (entry == null || entry.getKey() == null
						|| entry.getValue() == null) {
					throw new DDTException(
							"<key> or <value> tag of map element missing");
				}
				((Map<Object, Object>) this.getValue()).put(entry.getKey()
						.getValue(), entry.getValue().getValue());
			} catch (Exception e) {
				throw new DDTException("Error on map processing", e);
			}
		}
		this.successorProcessed = true;
	}

	public void processNoSuccessor() {
		this.createObject();
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
			Class clazz = Class.forName(type);
			Class superclazz = clazz;

			boolean found = false;
			while (!found && superclazz != null) {
				Class[] interfaces = superclazz.getInterfaces();
				for (int i = 0; i < interfaces.length; i++) {
					Class iface = interfaces[i];
					if ("java.util.Map".equals(iface.getName())) {
						found = true;
						break;
					}
				}
				if (found) {
					break;
				}
				superclazz = superclazz.getSuperclass();
			}
			if (!found) {
				throw new DDTException("Container class '" + type
						+ "' does not implement java.util.Map.");
			}
		} catch (Exception ex) {
			throw new DDTException("Container class '" + type
					+ "' does not implement java.util.Map.", ex);
		}
		this.injectedObject = new TypedObject(id, type);
		return this;
	}

}
