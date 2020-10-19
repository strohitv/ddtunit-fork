package junitx.ddtunit.data.processing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import junit.framework.TestCase;
import junitx.ddtunit.data.TypedObject;
import junitx.ddtunit.util.DDTConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateCreatorActionTest extends TestCase {
	private static final String DDTCONFIG_RESOURCE = "/ddtunitConfig.properties";

	private IAction contentAction;

	private TypedObject dateString;

	@Override
	protected void setUp() throws Exception {
		// prepare logging
//		BasicConfigurator.configure();
		// prepare DDTUnit configuration details
		DDTConfiguration.getInstance().load(DDTCONFIG_RESOURCE);
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("hint", HintTypes.CONTENT.toString());
		contentAction = new ContentCreatorAction(attrMap);
		dateString = new TypedObject("content", "java.lang.StringBuffer");
		contentAction.setObject(dateString);
	}

	public void testShortFormat() {
		String testFormat = "dd.MM.yyyy";
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("type", "java.util.Date");
		attrMap.put("hint", "date");
		// basically identify format and content to parse
		attrMap.put("dateformat", "generic");
		String testDate = "10.01.2007";
		dateString.setValue(testDate);
		// prepare supported structures
		IAction dateAction = new DateCreatorAction(attrMap);
		dateAction.setNext(contentAction);
		contentAction.setPrevious(dateAction);
		dateAction.setObject(new TypedObject("myObj", "java.util.Date"));

		dateAction.processSuccessor(contentAction);
		TypedObject obj = dateAction.getObject();
		assertNotNull(obj);
		DateFormat formater = new SimpleDateFormat(testFormat);
		assertEquals("Wrong date created", testDate, formater.format(obj
				.getValue()));
	}

	public void testspecialFormat() {
		String testFormat = "EEE MMM dd HH:mm:ss zzz yyyy";
		Locale.setDefault(new Locale("en", "US"));
		SimpleDateFormat formater = new SimpleDateFormat(testFormat);
		String testDate = "Wed Jan 10 00:00:00 CET 2007";
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("type", "java.util.Date");
		attrMap.put("hint", "date");
		// basically identify format and content to parse
		attrMap.put("dateformat", testFormat);
		dateString.setValue(testDate);
		// prepare supported structures
		IAction dateAction = new DateCreatorAction(attrMap);
		dateAction.setNext(contentAction);
		contentAction.setPrevious(dateAction);
		dateAction.setObject(new TypedObject("myObj", "java.util.Date"));
		dateAction.processSuccessor(contentAction);
		TypedObject obj = dateAction.getObject();
		assertNotNull(obj);
		String dateString = formater.format(obj.getValue());
		System.err.println(testDate + " - " + dateString);
		assertEquals("Wrong date generated", testDate, dateString);
	}

}
