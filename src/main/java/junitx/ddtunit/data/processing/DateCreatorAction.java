//$Id: DateCreatorAction.java 351 2008-08-14 20:20:56Z jg_hamburg $
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import junitx.ddtunit.DDTException;
import junitx.ddtunit.data.DDTTestDataException;
import junitx.ddtunit.data.TypedObject;
import junitx.ddtunit.util.ClassAnalyser;
import junitx.ddtunit.util.DDTConfiguration;
import junitx.ddtunit.util.DDTDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class contains object state and other information to create object from
 * SAX event stream.
 * 
 * @author jg
 */
public class DateCreatorAction extends ActionBase {
	private final static Logger log = LoggerFactory.getLogger(DateCreatorAction.class);

	private final String XML_ATTR_DATEFORMAT = "dateformat";

	private final String XML_ATTR_DATELOCALE = "locale";

	/**
	 * special key for actual date only
	 */
	public final static String SYSDATE = "!SYSDATE!";

	/**
	 * special key for actual date/time
	 */
	public final static String SYSTIME = "!SYSTIME!";

	public static final String DATE_GENERIC = "generic";

	public static final String DATE_LOCALE = "locale";

	/**
	 * 
	 * Constructor used as standard constructor to instanciate actions of this
	 * type
	 * 
	 * @param attrMap
	 */
	public DateCreatorAction(Map<String, String> attrMap) {
		super(attrMap);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junitx.ddtunit.parser.ActionBase#process()
	 */
	public IAction process() {
		log.debug("process DateCreator - START");
		if (!this.successorProcessed) {
			processNoSuccessor();
		}
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
		}
		this.pop();
		return this;
	}

	public void processSuccessor(IAction successor) {
		log.debug("processSuccessor(" + successor + ") - START");
		// create attribute list action and insert after rootAction
		if (HintTypes.CONTENT.equals(successor.getHint())) {
			String content = successor.getValue().toString();
			if (!ContentCreatorAction.CONTENT_NULL.equals(content)) {
				String format = (this.getAttribute(XML_ATTR_DATEFORMAT) == null) ? DATE_GENERIC
						: this.getAttribute(XML_ATTR_DATEFORMAT);
				String locale = ((this.getAttribute(XML_ATTR_DATELOCALE) == null) ? DATE_LOCALE
						: this.getAttribute(XML_ATTR_DATELOCALE));
				Date date = DateFactory.create(this.getType(), content, format,
						locale);
				this.setValue(date);
			}
		}
		this.successorProcessed = true;
	}

	public void processNoSuccessor() {
		throw new DDTException(
				"Specify !NULL! or date. No empty date tag allowed.");
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

	static class DateFactory {
		private static Calendar CAL = Calendar.getInstance();

		private final static DateFormat PROP_SYSDATE = new SimpleDateFormat(
				"dd.mm.yyyy");

		private final static DateFormat PROP_SYSTIME = new SimpleDateFormat(
				"dd.mm.yyyy HH:mm:ss.SSSS");

		/**
		 * Create Date object
		 * 
		 * @param clazz
		 *            of Date to create
		 * @param content
		 *            representing the date details
		 * @param format
		 *            of date provided as String
		 * @param locale
		 *            to use for date parsing
		 * @return requested date object or exception on creation error
		 */
		public static Date create(String clazz, String content, String format,
				String locale) {
			Date date = null;
			checkOnDateAncestor(clazz);
			if (SYSDATE.equals(content)) {
				date = CAL.getTime();
				try {
					PROP_SYSDATE.parse(PROP_SYSDATE.format(date));
				} catch (Exception ex) {
					throw new DDTTestDataException(
							"Error on creation of actual Date object '"
									+ content + "'", ex);
				}
			} else if (SYSTIME.equals(content)) {
				date = CAL.getTime();
			} else if (DATE_GENERIC.equals(format)) {
				Map<String, DDTDateFormat> dateFormatMap = DDTConfiguration
						.getInstance().getDateMap();
				boolean errorOnParsing = false;
				boolean formatFound = false;
				for (Map.Entry entry : dateFormatMap.entrySet()) {
					String name = (String) entry.getKey();
					DDTDateFormat formater = (DDTDateFormat) entry.getValue();
					try {
						if (formater.toString().length() == content.length()) {
							date = formater.parse(content);
							errorOnParsing = false;
							formatFound = true;
							break;
						}
					} catch (Exception ex) {
						log.warn("Error on creation of Date object '" + content
								+ "' by format '" + formater + "'");
						errorOnParsing = true;
					}
				}
				if (errorOnParsing) {
					throw new DDTTestDataException(
							"Error on creation of Date object (format="
									+ format + ", content=" + content + ")");
				}
				if (!formatFound) {
					throw new DDTTestDataException(
							"Error on creation of Date object, format not found. (format="
									+ format + ", content=" + content + ")");
				}
			} else {
				DDTDateFormat formater = selectDateFormater(format, locale);
				try {
					if (formater.toString().length() == content.length()) {
						date = formater.parse(content);
					} else {
						throw new DDTTestDataException("Date Formater '"
								+ formater + "' does not match '" + content
								+ "'.");
					}
				} catch (ParseException ex) {
					throw new DDTTestDataException(
							"Error on creation of Date object '" + content
									+ "' by format '" + format
									+ "' and LOCALE '" + locale + "'", ex);
				}
			}
			return date;
		}

		/**
		 * @param format
		 * @param locale
		 * @return
		 */
		private static DDTDateFormat selectDateFormater(String format,
				String locale) {
			DDTDateFormat formater;
			// check for predefined format first
			Map<String, DDTDateFormat> dateFormatMap = DDTConfiguration
					.getInstance().getDateMap();
			if (dateFormatMap.containsKey(format)) {
				formater = dateFormatMap.get(format);
			} else {
				Locale myLocale;
				if (DATE_LOCALE.equals(locale)) {
					myLocale = DDTConfiguration.getInstance().getActiveLocale();
				} else {
					String[] details = locale.split("_");
					if (details.length != 2) {
						throw new DDTTestDataException("Provided wrong locale "
								+ locale);
					}
					myLocale = new Locale(details[0], details[1]);
				}

				formater = new DDTDateFormat(format, myLocale);
			}
			return formater;
		}

		private static void checkOnDateAncestor(String clazz) {
			boolean check = false;
			try {
				Class checkClazz = Class.forName(clazz);
				Set superElements = ClassAnalyser.getSuperElements(checkClazz);
				check = checkClazz.equals(java.util.Date.class)
						|| superElements.contains(java.util.Date.class);
			} catch (Exception ex) {
				throw new DDTTestDataException("Class " + clazz
						+ " is no ancestor of java.util.Date.", ex);
			}
			if (!check) {
				throw new DDTTestDataException("Class " + clazz
						+ " is no ancestor of java.util.Date.");
			}
		}

	}
}
