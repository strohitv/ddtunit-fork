//$Id: ConstantCreatorAction.java 282 2007-07-19 22:46:27Z jg_hamburg $
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

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

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
public class ConstantCreatorAction extends ActionBase {
	/**
	 * 
	 * Constructor used as standard constructor to instanciate actions of this
	 * type
	 * 
	 * @param attrMap
	 */
	public ConstantCreatorAction(Map<String, String> attrMap) {
		super(attrMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junitx.ddtunit.parser.ActionBase#process()
	 */
	public IAction process() {
		log.debug("process ConstantCreator - START");
		IAction rootAction = this.getPrevious();
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
				rootAction.processSuccessor(this);
			} else {
				throw new DDTException("Unknown hint (" + hintValue
						+ ")- stop processing.");
			}
		} else {
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
			rootAction = this;
		}
		this.pop();
		return this;
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
		if (HintTypes.CONTENT.equals(successor.getHint())) {
			try {
				Field field = ClassAnalyser.getSelectedField(this.getType(),
						successor.getValue().toString());
				if (((Modifier.STATIC & field.getModifiers()) == Modifier.STATIC)
						// or java 5 enumeration is found
						// Modifier.ENUM=16384
						|| ((16384 & field.getModifiers()) == 16384)) {
					field.setAccessible(true);
					Object obj = field.get(null);
					this.setValue(obj);
					this.setType(field.getType().getName());
				} else {
					throw new DDTTestDataException(
							"Constant field must be defined static: "
									+ this.getType() + "."
									+ successor.getValue().toString());
				}
			} catch (Exception ex) {
				throw new DDTTestDataException(
						"Error on creation of constant field: "
								+ this.getType() + "."
								+ successor.getValue().toString(), ex);
			}

		} else {
			throw new DDTTestDataException("Unsupported successor action");
		}
	}

}