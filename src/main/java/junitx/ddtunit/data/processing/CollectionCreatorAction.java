//$Id: CollectionCreatorAction.java 351 2008-08-14 20:20:56Z jg_hamburg $
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import junitx.ddtunit.DDTException;
import junitx.ddtunit.data.DDTTestDataException;
import junitx.ddtunit.data.IDataSet;
import junitx.ddtunit.data.TestDataSet;
import junitx.ddtunit.data.TypedObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains object state and other information to create object from
 * SAX event stream.
 * 
 * @author jg
 */
public class CollectionCreatorAction extends ActionBase {
	protected Logger log = LoggerFactory.getLogger(CollectionCreatorAction.class);
	protected int elementCount;

	/**
	 * 
	 * Constructor used as standard constructor to instanciate actions of this
	 * type
	 * 
	 * @param attrMap
	 */
	public CollectionCreatorAction(Map<String, String> attrMap) {
		super(attrMap);
		this.elementCount = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junitx.ddtunit.parser.ActionBase#process()
	 */
	public IAction process() {
		log.debug("process CollectionCreator - START");
		IAction rootAction = getPrevious();
		if (!this.successorProcessed) {
			processNoSuccessor();
		}
		if (rootAction != null) {
			String hintValue = rootAction.getHint();

			if (HintTypes.COLLECTION.equals(hintValue)) {
				rootAction.processSuccessor(this);
			} else if (HintTypes.ARRAY.equals(hintValue)) {
				rootAction.processSuccessor(this);
			} else if (HintTypes.ATTRLIST.equals(hintValue)) {
				rootAction.processSuccessor(this);
			} else if (HintTypes.BEAN.equals(hintValue)) {
				rootAction.processSuccessor(this);
			} else if (HintTypes.FIELDS.equals(hintValue)) {
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
		IAction previousRootAction = rootAction.getPrevious();
		if (previousRootAction != null) {
			if (!((previousRootAction instanceof CollectionCreatorAction) || (previousRootAction instanceof SubelementCreatorAction))) {
				rootAction = rootAction.process();
			}
		}
		return rootAction;
	}

	public void processNoSuccessor() {
		this.createObject();
	}

	public void processSuccessor(IAction successor) {
		log.debug("processSuccessor(" + successor + ") - START");
		if (HintTypes.CONTENT.equals(successor.getHint())) {
			String content = successor.getValue().toString();
			if (!ContentCreatorAction.CONTENT_NULL.equals(content)) {
				throw new DDTException(
						"Only \"!NULL!\" supported in Collection type");
			}
		} else {
			try {
				this.createObject();
				this.elementCount += 1;
				// if no reference is set or if collection is sorted simply add
				// object (maybe an empty one)

				// check if successor contains referenceInfo
				if (HintTypes.ATTRLIST.equals(successor.getHint())
						&& ((SubelementCreatorAction) successor)
								.hasReferenceInfo()) {
					TypedObject destObject;
					String linkType = successor.getType();
					if (linkType == null) {
						destObject = new TypedObject(successor
								.getAttribute("refid"));
					} else {
						destObject = new TypedObject(successor
								.getAttribute("refid"), linkType);
					}
					IReferenceInfo refInfo;
					if (isSorted()) {
						refInfo = new CollectionReferenceInfo(this.getObject(),
								destObject, this.elementCount);
						// reserve position for referenced object
						((List) this.getValue()).add(null);
					} else {
						refInfo = new CollectionReferenceInfo(this.getObject(),
								destObject, CollectionReferenceInfo.noPosition);
					}
					add(refInfo);
				} else {
					((Collection) this.getValue()).add(successor.getValue());
				}
			} catch (Exception ex) {
				throw new DDTException(
						"Error on Collection element processing", ex);
			}
		}
		this.successorProcessed = true;
	}

	private boolean isSorted() {
		boolean found = verifyInterface(this.getType(), List.class);
		return found;
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
				boolean found = verifyInterface(type, Collection.class);
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

	private boolean verifyInterface(String clazzType, Class interFace) {
		Class clazz;
		try {
			clazz = Class.forName(clazzType);
		} catch (ClassNotFoundException ex) {
			throw new DDTTestDataException("Wrong data type " + clazzType, ex);
		}
		Class superclazz = clazz;
		Class[] interfaces = null;
		boolean found = false;
		while (!found && superclazz != null) {
			found = isImplementing(superclazz, interFace);
			if (!found) {
				superclazz = superclazz.getSuperclass();
			}
		}
		return found;
	}

	private boolean isImplementing(Class clazz, Class interFace) {
		boolean found = false;
		Class[] interfaces = clazz.getInterfaces();
		for (Class inter : interfaces) {
			if (interFace.equals(inter)) {
				found = true;
				break;
			} else {
				found = isImplementing(inter, interFace);
			}
		}
		return found;
	}

	static class CollectionReferenceInfo extends ReferenceInfoBase {
		protected int position;
		static int noPosition = -1;

		public CollectionReferenceInfo(TypedObject source,
				TypedObject destination, int position) {
			super(source, destination);
			this.position = position;
		}

		public void raiseRankOf(IReferenceInfo info) {
			if (this.getDestId().equals(info.getSourceId())
					&& (this.getDestType().equals(info.getSourceType()) || TypedObject.UNKNOWN_TYPE
							.equals(info.getDestType()))) {
				if (this.getRank() >= info.getRank()) {
					info.setRank(this.getRank() + 1);
				}
			}
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
		 * @param dataSet
		 *            to resolve reference to
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
				// if unsorted collection then just add to end
				if (this.position == this.noPosition) {
					((Collection) source.getValue()).add(dest.getValue());
				} else {
					((List) source.getValue()).set(this.position - 1, dest
							.getValue());
				}
			} else {
				throw new DDTTestDataException(
						"Error on processing references on testdata.");
			}
		}

	}
}
