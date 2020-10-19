package junitx.ddtunit.util;

import java.text.DateFormat;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DDTConfigurationTest extends TestCase {

	private DDTConfiguration config;

	@Override
	protected void setUp() throws Exception {
		this.config = DDTConfiguration.getInstance();
//		BasicConfigurator.configure();
	}

	public void testLoadString() throws Exception {
		config.load("/ddtunitConfig.properties");
		assertFalse("Expected deactivated Monitor", config.isActiveRunMonitor());
		assertFalse("Expected deactivated xml validation", config
				.isActiveXmlValidation());
		assertFalse("Expected deactivated assert support", config
				.isActiveAsserts());
		assertTrue("Expected activated parser validation", config
				.isActiveParserValidation());
		assertEquals("Wrong log4j resource path",
				"/junitx.ddtunit.log4j.properties", config.getLog4jConfigResource());
		assertEquals("Wrong active Locale",
				"English (United States)", config
						.getActiveLocale().getDisplayName());
		assertEquals("Wrong number of date definitions", 4, config.getDateMap()
				.size());
		DateFormat formater = config.getDateMap().get("example");
		assertEquals("Wrong date format", "Mon Jun 25 00:00:00 CEST 2007",
				formater.parse("Mon Jun 25 00:00:00 CEST 2007").toString());
	}
}
