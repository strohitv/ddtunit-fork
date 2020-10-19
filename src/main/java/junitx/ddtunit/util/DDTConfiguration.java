//$Id: DDTConfiguration.java 358 2009-08-05 13:58:44Z jg_hamburg $
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
package junitx.ddtunit.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import junitx.ddtunit.DDTSetUpException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load configuration file and provide access to specified content in an
 * instance of this class
 * 
 * @author jg
 */
public class DDTConfiguration {
	public static final String DDTUNIT_CONFIG_PROPERTIES = "/junitx/ddtunit/ddtunitConfig.properties";

	private static DDTConfiguration singleton;

	private static long propsModifyTime;

	private Properties props;

	private String log4jConfigResource;

	private boolean activeRunMonitor;

	private boolean activeXmlValidation;

	private boolean activeParserValidation;

	private boolean activeAsserts;

	private boolean specificationAssert;

	private Locale activeLocale;

	private Map<String, DDTDateFormat> dateMap;

	/**
	 * Number of cluster data that could not be deleted by jvm garbage
	 * collection via SoftReference implementation.
	 */
	private int hardCacheSize = 25;

	private final static Logger log = LoggerFactory.getLogger(DDTConfiguration.class);

	private static final String PROP_ACTIVE_RUN_MONITOR = "activeRunMonitor";

	private static final String PROP_LOG4J_CONFIG_RESOURCE = "log4jConfigResource";

	private static final String PROP_ACTIVE_XML_VALIDATION = "activeXmlValidation";

	private static final String PROP_ACTIVE_PARSER_VALIDATION = "activeParserValidation";

	private static final String PROP_ACTIVE_LOCALE = "activeLocale";

	private static final String PROP_ACTIVE_ASSERTS = "activeAsserts";

	private static final String PROP_DATE_PREFIX = "date.";

	public static final String PROP_RESOURCE = DDTUNIT_CONFIG_PROPERTIES;

	/**
	 * Default constructor
	 */
	private DDTConfiguration() {
		initProps();
	}

	private void initProps() {
		this.log4jConfigResource = "/junitx.ddtunit.log4j.properties";
		this.activeRunMonitor = true;
		this.activeXmlValidation = true;
		this.activeParserValidation = false;
		this.activeAsserts = true;
		this.specificationAssert = false;
		this.activeLocale = Locale.getDefault();
		this.dateMap = new HashMap<String, DDTDateFormat>();
	}

	/**
	 * @return only one instance of config information
	 */
	public static DDTConfiguration getInstance() {
		if (singleton == null) {
			singleton = new DDTConfiguration();
		}
		return singleton;
	}

	public void load() {
		load(DDTUNIT_CONFIG_PROPERTIES);
	}

	public void load(String resourceName) {
		log.debug("load(" + resourceName + ") - START");
		File propsFile = new File(this.getClass().getResource(resourceName)
				.getPath());
		if (propsFile.lastModified() > propsModifyTime) {

			propsModifyTime = propsFile.lastModified();
			initProps();
			this.props = new Properties();
			try {
				this.props.load(this.getClass().getResourceAsStream(
						resourceName));
			} catch (IOException ex) {
				throw new DDTSetUpException(
						"Error on initialization of properties", ex);
			}
			// check for LOCALE defails before processing date formats
			if (this.props.containsKey(PROP_ACTIVE_LOCALE)) {
				String locale = null;
				try {
					locale = (String) this.props.get(PROP_ACTIVE_LOCALE);
					String[] details = locale.split("_");
					if (details != null && details.length == 2) {
						this
								.setActiveLocale(new Locale(details[0],
										details[1]));
					} else {
						this.setActiveLocale(Locale.getDefault());
					}
					Locale.setDefault(this.getActiveLocale());
				} catch (Exception ex) {
					log.error("Error on initialization of LOCALE(" + locale
							+ "). Reset to default.");
				}
			} else {
				this.setActiveLocale(Locale.getDefault());
			}
			for (Entry entry : this.props.entrySet()) {
				if (PROP_ACTIVE_RUN_MONITOR.equals(entry.getKey())) {
					this.setActiveRunMonitor(Boolean
							.parseBoolean((String) entry.getValue()));
				} else if (PROP_LOG4J_CONFIG_RESOURCE.equals(entry.getKey())) {
					this.setLog4jConfigResource((String) entry.getValue());
				} else if (PROP_ACTIVE_XML_VALIDATION.equals(entry.getKey())) {
					this.setActiveXmlValidation(Boolean
							.parseBoolean((String) entry.getValue()));
				} else if (PROP_ACTIVE_PARSER_VALIDATION.equals(entry.getKey())) {
					this.setActiveParserValidation(Boolean
							.parseBoolean((String) entry.getValue()));
				} else if (PROP_ACTIVE_ASSERTS.equals(entry.getKey())) {
					this.setActiveAsserts(Boolean.parseBoolean((String) entry
							.getValue()));
				} else if (((String) entry.getKey())
						.startsWith(PROP_DATE_PREFIX)) {
					String key = ((String) entry.getKey())
							.substring(PROP_DATE_PREFIX.length());
					this.dateMap.put(key, new DDTDateFormat((String) entry
							.getValue(), this.getActiveLocale()));
				}
			}
		}
	}

	public boolean isActiveRunMonitor() {

		return activeRunMonitor;
	}

	public String getLog4jConfigResource() {
		return log4jConfigResource;
	}

	public boolean isActiveXmlValidation() {
		return activeXmlValidation;
	}

	public int getHardCacheSize() {
		return this.hardCacheSize;
	}

	public boolean isActiveParserValidation() {
		return this.activeParserValidation;
	}

	public void setActiveParserValidation(boolean parserValidationSupport) {
		this.activeParserValidation = parserValidationSupport;
	}

	public void setActiveXmlValidation(boolean activeXmlValidation) {
		this.activeXmlValidation = activeXmlValidation;
	}

	public boolean isActiveAsserts() {
		return this.activeAsserts;
	}

	public void setActiveAsserts(boolean assertSupport) {
		this.activeAsserts = assertSupport;
	}

	public boolean isSpecificationAssert() {
		return this.specificationAssert;
	}

	public void setSpecificationAssert(boolean specificationAssert) {
		this.specificationAssert = specificationAssert;
	}

	public Locale getActiveLocale() {
		return activeLocale;
	}

	public void setActiveRunMonitor(boolean activeRunMonitor) {
		this.activeRunMonitor = activeRunMonitor;
	}

	public void setLog4jConfigResource(String log4jConfigResource) {
		this.log4jConfigResource = log4jConfigResource;
	}

	public void setActiveLocale(Locale activeLocale) {
		this.activeLocale = activeLocale;
	}

	public Map<String, DDTDateFormat> getDateMap() {
		return dateMap;
	}
}
